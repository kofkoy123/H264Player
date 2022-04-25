package com.example.h264player2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.h264player2.code.H264Player;
import com.example.h264player2.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class H264PlayerActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private H264Player mH264Player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h264_player);
        initPlayer();
    }



    private void initPlayer() {
        mSurfaceView = findViewById(R.id.surface_view);
        String path = getFromAssets("test.h264");
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                Surface surface = surfaceHolder.getSurface();
                mH264Player = new H264Player(H264PlayerActivity.this, path, surface);
                mH264Player.start();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });
    }


    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}