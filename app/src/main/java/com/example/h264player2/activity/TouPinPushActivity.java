package com.example.h264player2.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.h264player2.R;
import com.example.h264player2.socket.PushSocketLive;

/**
 * 投屏推流端
 */
public class TouPinPushActivity extends AppCompatActivity implements View.OnClickListener {


    private MediaProjectionManager mMediaProjectionManager;
    private Button mStartScreen;
    private PushSocketLive mSocketLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projection_screen);

        mStartScreen = findViewById(R.id.start_screen);
        mStartScreen.setOnClickListener(this);
    }


    private void applyRecordPermission() {
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //是否同意录屏
        Intent intent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, 1);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_screen:
                applyRecordPermission();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 1){
                MediaProjection mediaProjection =  mMediaProjectionManager.getMediaProjection(resultCode,data);
                //用户同意录屏
                mSocketLive = new PushSocketLive();
                mSocketLive.startLive(mediaProjection);
            }
        }
    }

}