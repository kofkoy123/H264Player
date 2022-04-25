package com.example.h264player2.code;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * H264解码器
 */
public class H264Player implements Runnable {


    private Context mContext;
    private String mVideoPath;
    private Surface mSurface;
    private MediaCodec mVideoCode;


    public H264Player(Context mContext, String mVideoPath, Surface mSurface) {
        this.mContext = mContext;
        this.mVideoPath = mVideoPath;
        this.mSurface = mSurface;
        initCode();
    }

    private void initCode() {

        try {
            //h264解码器
            mVideoCode = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            //createVideoFormat 视频解码  createAudioFormat 音频  createSubtitleFormat （动态）字幕
            //解码这里的宽高可以随便传
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 720, 1280);
            //帧率
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            //MediaCrypto 要不要加密 一般不传  第四个标志位解码传0
            mVideoCode.configure(mediaFormat, mSurface, null, 0);

        } catch (IOException e) {

        }
    }


    public void start() {
        mVideoCode.start();
        new Thread(this).start();
    }


    @Override
    public void run() {
        try {
            decodeH264();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 解码
     */
    private void decodeH264() {
        byte[] bytes = null;
        try {
            //数据在这里。
            bytes = getFileByte();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int startIndex = 0;
        //入参出参对象
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        while (true) {
            //加2是防止传入是0，返回也是0
            int nextFrameIndex = findFrame(bytes, startIndex+2, bytes.length);
            //拿到所有队列。因为队列是所有APP公用  方法弃用了，可以用另外方式，直接拿可以使用的。
//        ByteBuffer[] byteBuffers = mVideoCode.getInputBuffers();
            //拿到其中可以使用的  参数是等待时间，10000就表示10毫秒。
            int index = mVideoCode.dequeueInputBuffer(10000);
            if (index >= 0) {
                //这个就是队列中可以使用的容器
//            ByteBuffer byteBuffer = byteBuffers[index];
                //这里就是直接拿到可以使用的容器
                ByteBuffer byteBuffer = mVideoCode.getInputBuffer(index);
                //一个容器放一帧的内容去解码
                //通过H264分隔符 00 00 00 01 和 00 00 01来获取每一帧
                int length = nextFrameIndex - startIndex;
                //起始位置和总长度
                byteBuffer.put(bytes, startIndex, length);
                //告诉DSP芯片使用了第几个容器 数据从CPU流动到DSP  presentationTimeUs pts 按照编码的时间就传0
                //如果编码不能传0
                mVideoCode.queueInputBuffer(index, 0, length, 0, 0);
                startIndex= nextFrameIndex;
            }
            //输入 输出不是同步的，解码比较耗时所以有延时
            //上面循环是从CPU拿到数据放到DSP解码，然后这一步是从dsp把解码数据返回给CPU芯片
            int outIndex = mVideoCode.dequeueOutputBuffer(info, 10000);
            //大于0表示有数据
            if (outIndex >=0){
                //释放对应容器，如果是有surface渲染就一定要传true
                mVideoCode.releaseOutputBuffer(outIndex,true);
            }
        }
    }


    /**
     * 查找下一帧数据
     *
     * @param bytes
     * @param startIndex
     * @param totalSize
     * @return
     */
    private int findFrame(byte[] bytes, int startIndex, int totalSize) {
        //-4防止越界 根据分隔符获取每一帧数据
        for (int i = startIndex; i < totalSize - 4; i++) {
            if ((bytes[i] == 0x00 && bytes[i + 1] == 0x00 && bytes[i + 2] == 0x00 && bytes[i + 3] == 0x01) ||
                    (bytes[i] == 0x00 && bytes[i + 1] == 0x00 && bytes[i + 2] == 0x01)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 读文件数据
     *
     * @return
     */
    private byte[] getFileByte() throws Exception {
        InputStream inputStream = new DataInputStream(new FileInputStream(mVideoPath));
        int len;
        int size = 1024;
        byte[] buf;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = inputStream.read(buf, 0, size)) != -1) {
            byteArrayOutputStream.write(buf, 0, len);
            buf = byteArrayOutputStream.toByteArray();
        }
        return buf;
    }
}
