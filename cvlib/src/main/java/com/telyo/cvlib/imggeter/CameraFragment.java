package com.telyo.cvlib.imggeter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.telyo.cvlib.ImgExtractor;
import com.telyo.cvlib.PointMat;
import com.telyo.cvlib.R;
import com.telyo.cvlib.utils.LibUtils;
import com.telyo.cvlib.widget.MatImageView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;

public class CameraFragment extends Fragment implements View.OnClickListener {

    private JavaCameraView cameraView;
    private MatImageView ivMat;
    private ImageView ivReTack;
    private ImageView ivOk;
    private ImageView btnOpenCv;
    private PointMat matChanged = new PointMat();
    private Mat mGray;
    private PointMat matCamera = new PointMat();
    private PointMat matResult = new PointMat();
    private RelativeLayout llCamera;
    private ImageView back;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        cameraView.enableView();
    }

    @Override
    public void onStop() {
        super.onStop();
        cameraView.disableView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        cameraView = view.findViewById(R.id.camera);
        ivMat = view.findViewById(R.id.ivMat);
        ivReTack = view.findViewById(R.id.ivReTack);
        llCamera = view.findViewById(R.id.llCamera);
        ivOk = view.findViewById(R.id.ivOk);
        back = view.findViewById(R.id.back);
        btnOpenCv = view.findViewById(R.id.btnOpenCv);
        back.setOnClickListener(this);
        cameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mGray = inputFrame.gray();
                LibUtils.rotate90(mGray, false);
                Mat mRgb = inputFrame.rgba();
                LibUtils.rotate90(mRgb, false);
                mRgb.copyTo(matChanged);
                mRgb.copyTo(matResult);
                matCamera = doExtract(matChanged, true);
                matResult.setCorners(matCamera.getCorners());
                return matCamera;
            }
        });
        ivOk.setOnClickListener(this);
        ivReTack.setOnClickListener(this);
        btnOpenCv.setOnClickListener(this);
    }

    private PointMat doExtract(PointMat pointMat, boolean isDrawLine) {
        pointMat.setNeedPreView(isDrawLine);
        return ImgExtractor.getInstance().doExtract(pointMat);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ivReTack) {
            cameraView.enableView();
            llCamera.setVisibility(View.VISIBLE);

        }
        if (i == R.id.ivOk) {
            ImgExtractor.getInstance().onMat(doExtract(matResult, false));
            getActivity().finish();
        }
        if (i == R.id.back) {
            ImgExtractor.getInstance().onCancel();
            getActivity().finish();
        }
        if (i == R.id.btnOpenCv) {
            ivMat.setMat(matResult);
            cameraView.disableView();
            llCamera.setVisibility(View.INVISIBLE);
        }
    }


}
