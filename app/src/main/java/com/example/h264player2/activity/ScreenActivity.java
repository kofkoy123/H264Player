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

import com.example.h264player2.code.H264Encoder;
import com.example.h264player2.R;

public class ScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mStartView;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        initViews();

    }

    private void applyRecordPermission() {
         mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //是否同意录屏
        Intent intent= mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent,1);

    }

    private void initViews() {
        mStartView=findViewById(R.id.start_record);
        mStartView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_record:
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
                H264Encoder h264Encoder = new H264Encoder(mediaProjection);



            }
        }
    }
}