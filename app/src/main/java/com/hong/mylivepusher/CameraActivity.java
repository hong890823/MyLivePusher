package com.hong.mylivepusher;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hong.mylivepusher.camera.HCameraView;

public class CameraActivity extends AppCompatActivity{

    private HCameraView hCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        hCameraView = findViewById(R.id.camera_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hCameraView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hCameraView.previewAngle(this);
    }

}
