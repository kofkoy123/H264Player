package com.example.h264player2.socket;

import android.media.projection.MediaProjection;

import com.example.h264player2.code.TouPinPushEncoder;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * 内网连接   server ----- client
 * 服务端-客户端
 * 推流端
 */
public class PushSocketLive {

    /**
     * 另外一个设备的socket  ----》发送数据
     */
    private WebSocket mWebSocket;


    //服务，端口号8888
    private WebSocketServer webSocketServer = new WebSocketServer(new InetSocketAddress(8888)) {
        //连接了这里回调
        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            mWebSocket = conn;//收到socket
        }

        //关闭
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        }

        //接收到信息
        @Override
        public void onMessage(WebSocket conn, String message) {

        }

        @Override
        public void onError(WebSocket conn, Exception ex) {

        }

        @Override
        public void onStart() {

        }
    };

    public void startLive(MediaProjection mediaProjection) {
        webSocketServer.start();
        TouPinPushEncoder h264Encoder = new TouPinPushEncoder(mediaProjection,this);
        h264Encoder.start();
    }


    /**
     * 发送数据
     *
     * @param bytes
     */
    public void sendData(byte[] bytes) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.send(bytes);
        }
    }

    public void close() {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.close();
        }
    }
}
