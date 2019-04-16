package com.telyo.cvlib.imggeter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

import com.telyo.cvlib.ImgExtractor;
import com.telyo.cvlib.R;

public class CameraActivity extends FragmentActivity {
    private String TAG = CameraActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_camera);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.container, ((ImageGetterImpl) ImgExtractor.getInstance().getImageGetter()).getFragment(), TAG)
                .commitNow();

    }
}
