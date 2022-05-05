package com.example.h264player2.code;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import com.example.h264player2.socket.PushSocketLive;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TouPinPushEncoder extends Thread {

    private MediaProjection mediaProjection;
    private MediaCodec mMediaCodec;
    private int width = 720;
    private int height = 1280;
    private byte[] sps_pps_buf;
    private static final int NAL_I = 5;
    private static final int NAL_SPS = 7;
    private PushSocketLive mSocketLive;

    public TouPinPushEncoder(MediaProjection mediaProjection, PushSocketLive socketLive) {
        this.mediaProjection = mediaProjection;
        this.mSocketLive = socketLive;
        initCodec();
    }

    private void initCodec() {

        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 30);
            format.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface surface = mMediaCodec.createInputSurface();
            mediaProjection.createVirtualDisplay("lzr_touping", width, height, 2,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void run() {
        super.run();
        mMediaCodec.start();

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true) {
            //从CPU传数据 由于MediaProjection和MediaCodec关系，帮他做了
            int outIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
            if (outIndex >= 0) {
                //解码后数据
                ByteBuffer buffer = mMediaCodec.getOutputBuffer(outIndex);
//                //bufferInfo大小等价于 buffer大小
//                byte[] bytes = new byte[bufferInfo.size];
//                //buffer 内部数据转移到 bytes中。
//                buffer.get(bytes);
//                FileUtils.writeBytes(bytes);
//                FileUtils.writeContent(bytes);

                dealFrame(buffer, bufferInfo);
                //释放数据，第二个没有实际渲染就false
                mMediaCodec.releaseOutputBuffer(outIndex, false);
            }
        }
    }


    /**
     * 处理每一帧
     *
     * @param buffer
     * @param bufferInfo
     */
    private void dealFrame(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
        int offset = 4;
        //分隔符有两种类型 00 00 00 01 或者 00 00 01
        if (buffer.get(2) == 0x01) {
            offset = 3;
        }
        //与1f求出帧类型
        int type = buffer.get(offset) & 0x1F;
        //7就是sps pps 只会输出一份，所以保存复制到每个I帧前面。
        if (type == NAL_SPS) {
            //因为是一帧帧输出，所以这里整帧都是 sps pps
            sps_pps_buf = new byte[bufferInfo.size];
            buffer.get(sps_pps_buf);
            //等于5就是I帧了
        } else if (type == NAL_I) {
            //创建数组填充编码数据
            byte[] sourceBuf = new byte[bufferInfo.size];
            //要拿出数据
            buffer.get(sourceBuf);
            //创建新数组接收数据
            byte [] newBuf = new byte[sps_pps_buf.length + sourceBuf.length];
            //复制数据，1.复制到哪个数组，从第几位开始复制，复制哪个数组，复制的起点，复制的终点
            System.arraycopy(newBuf,0,sps_pps_buf,0,sps_pps_buf.length);
            //复制I帧数据
            System.arraycopy(newBuf,sps_pps_buf.length,sourceBuf,0,sourceBuf.length);
            //发送数据
            mSocketLive.sendData(newBuf);
            //其他帧就不做处理原封输出
        }else {
            byte[] otherBuf = new byte[bufferInfo.size];
            buffer.get(otherBuf);
            //发送数据
            mSocketLive.sendData(otherBuf);
        }

    }
}
