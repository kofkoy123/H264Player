package com.example.h264player2.code;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Debug;

import com.example.h264player2.FileUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CameraCoder {

    private int mWidth;
    private int mHeight;
    private MediaCodec mMediaCodec;
    private int mFrameCount = 20;
    private int mFrameIndex = 1;


    public CameraCoder(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }


    public void startLive() {
        mFrameIndex = 1;
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameCount);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 30);
            format.setInteger(MediaFormat.KEY_BIT_RATE, mWidth * mHeight);
            //数据是从摄像头
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int encodeFrame(byte[] data) {
        //解决方向不对，要处理这里的数据


        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int inputIndex = mMediaCodec.dequeueInputBuffer(10000);
        //输入
        if (inputIndex >= 0) {
            ByteBuffer buffer = mMediaCodec.getInputBuffer(inputIndex);
            buffer.clear();
            buffer.put(data);
            //PTS编码需要传
            mMediaCodec.queueInputBuffer(inputIndex, 0, data.length, computPts(), 0);
            mFrameIndex++;
        }
        //输出
        int outIndex = mMediaCodec.dequeueOutputBuffer(info, 10000);
        if (outIndex >= 0) {
            ByteBuffer buffer = mMediaCodec.getOutputBuffer(outIndex);
            //长度
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileUtils.writeBytes(bytes);
            FileUtils.writeContent(bytes);
            mMediaCodec.releaseOutputBuffer(outIndex, false);
        }
        return -1;
    }

    //视频剪辑都是微秒  就是 1秒= 100000;
    private int computPts() {
        return 100000 / mFrameCount * mFrameIndex;
    }

}
