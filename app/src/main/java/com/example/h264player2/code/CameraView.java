package com.example.h264player2.code;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Printer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraView extends SurfaceView implements Camera.PreviewCallback, SurfaceHolder.Callback {


    private Camera mCamera;
    private Camera.Size mSize;
    private byte[] bytes;
    private CameraCoder mCameraCoder;


    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }


    private void initCamera(){
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Camera.Parameters parame = mCamera.getParameters();
        mSize = parame.getPreviewSize();
        try {
            mCamera.setPreviewDisplay(getHolder());
            //因为安卓相机放的位置导致要旋转 显示式旋转了 但预览数据没有
            mCamera.setDisplayOrientation(90);
            bytes = new byte[mSize.width*mSize.height*3/2];
            mCamera.addCallbackBuffer(bytes);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //这里拍出来没有彩色，所以需要把安卓捕捉的NV21转出大家都会识别的NV12。
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        //byte 返回yuv数据
        if (mCameraCoder == null){
            mCameraCoder = new CameraCoder(mSize.width,mSize.height);
            mCameraCoder.startLive();
        }
        mCameraCoder.encodeFrame(bytes);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        initCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
