package com.example.h264player2.code;

import static android.content.ContentValues.TAG;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.example.h264player2.socket.PullSocketLive;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PullH264Player implements PullSocketLive.SocketCallBack {

    private static final String TAG = "PullH264Player";
    private MediaCodec mMediaCodec;
    private int width = 720;
    private int height = 1280;
    private Surface mSurface;


    public PullH264Player(Surface mSurface) {
        this.mSurface = mSurface;
        initMediaCodec();
    }

    private void initMediaCodec() {
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        try {
            mMediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            format.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            mMediaCodec.configure(format, mSurface, null, 0);
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void receiveData(byte[] bytes) {
        try {
            decodeH264(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 传过来就是整帧数据
     */
    private void decodeH264(byte[] bytes) {
        Log.e(TAG, "decodeH264: 解码前数据长度："+bytes.length);
        //入参出参对象
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        //拿到其中可以使用的  参数是等待时间，10000就表示10毫秒。
        int index = mMediaCodec.dequeueInputBuffer(10000);
        if (index >= 0) {
            //这个就是队列中可以使用的容器
//            ByteBuffer byteBuffer = byteBuffers[index];
            //这里就是直接拿到可以使用的容器
            ByteBuffer byteBuffer = mMediaCodec.getInputBuffer(index);
            byteBuffer.clear();
            //起始位置和总长度
            byteBuffer.put(bytes,0,bytes.length);
            //告诉DSP芯片使用了第几个容器 数据从CPU流动到DSP  presentationTimeUs pts 按照编码的时间就传0
            //如果编码不能传0
            mMediaCodec.queueInputBuffer(index, 0, bytes.length, System.currentTimeMillis(), 0);
        }
        //输入 输出不是同步的，解码比较耗时所以有延时
        //上面循环是从CPU拿到数据放到DSP解码，然后这一步是从dsp把解码数据返回给CPU芯片
        int outIndex = mMediaCodec.dequeueOutputBuffer(info, 10000);
        //使用if不严谨
//        if (outIndex >=0){
//            mMediaCodec.releaseOutputBuffer(outIndex, true);
//        }

        Log.e(TAG, "decodeH264: 解码后数据长度："+info.size);
        //大于0表示有数据  真实项目需要要循环去获取下标处理。因为处理解码有延迟的
        //输入一个数据，可能输出多个
        while (outIndex >= 0) {
            //释放对应容器，如果是有surface渲染就一定要传true
            mMediaCodec.releaseOutputBuffer(outIndex, true);
            outIndex = mMediaCodec.dequeueOutputBuffer(info,0);
        }
    }


}
