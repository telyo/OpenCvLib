package com.telyo.cvlib.imggeter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;

import org.opencv.core.Mat;


public class ImageGetterImpl implements IImageGetter {
    private final static String TAG = ImageGetterImpl.class.getSimpleName();

    @VisibleForTesting
    private Lazy<CameraFragment> mCameraFragment;

    public ImageGetterImpl() {
        mCameraFragment = getSingleCameraFragment();
    }

    public Fragment getFragment() {
        return mCameraFragment.get();
    }

    @Override
    public void openCamera(Context context) {
        context.startActivity(new Intent(context, CameraActivity.class));
    }

    @NonNull
    private Lazy<CameraFragment> getSingleCameraFragment() {
        return new Lazy<CameraFragment>() {
            private CameraFragment cameraFragment;

            @Override
            public CameraFragment get() {
                if (cameraFragment == null) {
                    cameraFragment = new CameraFragment();
                }
                return cameraFragment;
            }
        };
    }


    public interface OnMatStreamListener {
        void onCancel();

        void onMat(Mat img);
    }

    @FunctionalInterface
    public interface Lazy<V> {
        V get();
    }
}
