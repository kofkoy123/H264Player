package com.example.h264player2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.h264player2.R;
import com.example.h264player2.code.PullH264Player;
import com.example.h264player2.socket.PullSocketLive;

/**
 * 投屏拉流端
 */
public class TouPinPullActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tou_pin_pull);
        initViews();
    }

    private void initViews() {
        mSurfaceView=findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                Surface surface = surfaceHolder.getSurface();
                PullH264Player player = new PullH264Player(surface);
                //这样因为player实现了callback接口，数据不停回调给player处理。
                PullSocketLive live = new PullSocketLive(player,8888);
                //链接
                live.connect();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });
    }
}