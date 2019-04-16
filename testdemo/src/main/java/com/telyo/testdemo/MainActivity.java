package com.telyo.testdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.telyo.cvlib.ImgExtractor;
import com.telyo.cvlib.imggeter.ImageGetterImpl;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class MainActivity extends AppCompatActivity implements LoaderCallbackInterface,ImageGetterImpl.OnMatStreamListener{

    private ImgExtractor imgExtractor;
    //动态请求的权限数组
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,};
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);
        imgExtractor = ImgExtractor.getInstance();
        imgExtractor.addOnMatStreamListener(this);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgExtractor.openCamera(MainActivity.this);
            }
        });
    }
    private void checkPermission() {
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 100);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, this);
        }
    }

    @Override
    public void onManagerConnected(int status) {

    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onMat(Mat img) {
        Bitmap bitmap =  Bitmap.createBitmap(img.width(),img.height(),ARGB_8888);
        Utils.matToBitmap(img,bitmap);
        iv.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        imgExtractor.removeOnMatStreamListener(this);
        super.onDestroy();
    }
}
