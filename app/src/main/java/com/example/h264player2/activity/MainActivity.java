package com.example.h264player2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.example.h264player2.code.H264Player;
import com.example.h264player2.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView mSurfaceView;
    private H264Player mH264Player;
    private Button mPlayH264;
    private Button mScreenRecord;
    private Button mCameraRecord;
    private Button mProjectionScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initViews();
    }

    private void initViews() {
        mPlayH264 = findViewById(R.id.h264_player);
        mScreenRecord = findViewById(R.id.screen_record);
        mCameraRecord = findViewById(R.id.camera_record);
        mProjectionScreen = findViewById(R.id.screen_projection);
        mPlayH264.setOnClickListener(this);
        mScreenRecord.setOnClickListener(this);
        mCameraRecord.setOnClickListener(this);
        mProjectionScreen.setOnClickListener(this);
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, 1);
        }
    }


    private void startIntent(Class className) {
        Intent intent = new Intent(this, className);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.h264_player:
                startIntent(H264PlayerActivity.class);
                break;
            case R.id.screen_record:
                startIntent(ScreenActivity.class);
                break;
            case R.id.camera_record:
                startIntent(CameraActivity.class);
                break;
            case R.id.screen_projection:

                break;
        }
    }
}