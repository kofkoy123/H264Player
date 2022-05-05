package com.example.h264player2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.h264player2.R;
import com.example.h264player2.code.CameraView;

/**
 * 摄像头
 */
public class CameraActivity extends AppCompatActivity {

    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraView = findViewById(R.id.camera_view);
    }
}