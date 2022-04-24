package com.example.h264player2;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class H264Encoder extends Thread{

    private MediaProjection mediaProjection;
    private MediaCodec mMediaCodec;
    private int width = 720;
    private int height = 1280;

    public H264Encoder(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
        initMediaCode();
    }

    private void initMediaCode() {
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,width,height);

        try {
            mMediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            //每秒20帧
            format.setInteger(MediaFormat.KEY_FRAME_RATE,20);
            //每隔30帧就创建一个I帧
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,30);
            //码率，码率越高越清晰
            format.setInteger(MediaFormat.KEY_BIT_RATE,width*height);
            //数据来源
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            //surface 不用，加密不用，编码传encode
            mMediaCodec.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            //创建虚拟surface 接收数据
            Surface surface = mMediaCodec.createInputSurface();
            //告诉MediaProjection 数据放到surface去
            mediaProjection.createVirtualDisplay("lzr_record",width,height,2,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,surface,null,null);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void run() {
        super.run();
        mMediaCodec.start();
        //接收数据
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true){
            //从CPU传数据 由于MediaProjection和MediaCodec关系，帮他做了
            int outIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10000);

            if (outIndex >=0){
                //解码后数据
                ByteBuffer buffer = mMediaCodec.getOutputBuffer(outIndex);
                //bufferInfo大小等价于 buffer大小
                byte[] bytes = new byte[bufferInfo.size];
                //buffer 内部数据转移到 bytes中。
                buffer.get(bytes);
                FileUtils.writeBytes(bytes);
                FileUtils.writeContent(bytes);
                //释放数据，第二个没有实际渲染就false
                mMediaCodec.releaseOutputBuffer(outIndex,false);
            }

        }

    }
}
