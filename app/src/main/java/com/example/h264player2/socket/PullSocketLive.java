package com.example.h264player2.socket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;

public class PullSocketLive {



    private SocketCallBack mSocketCallBack;
    private int port =8888;
    private MySocketClient mMySocketClient;

    public PullSocketLive(SocketCallBack mSocketCallBack,int port) {
        this.mSocketCallBack = mSocketCallBack;
        this.port= port;
    }



    private class MySocketClient extends WebSocketClient{

        public MySocketClient(URI serverUri) {
            super(serverUri);
        }

        public MySocketClient(URI serverUri, Draft protocolDraft) {
            super(serverUri, protocolDraft);
        }

        public MySocketClient(URI serverUri, Map<String, String> httpHeaders) {
            super(serverUri, httpHeaders);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {

        }

        @Override
        public void onMessage(String message) {

        }


        @Override
        public void onMessage(ByteBuffer bytes) {
            byte[] datas = new byte[bytes.remaining()];
            bytes.get(datas);
            mSocketCallBack.receiveData(datas);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onError(Exception ex) {

        }
    }


    /**
     * 链接
     */
    public void connect(){
        //对方手机IP地址
        try {
            URI uri = new URI("ws://192.168.31.94:"+port);
            mMySocketClient = new MySocketClient(uri);
            mMySocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }



    public interface SocketCallBack{
        void receiveData(byte[] bytes);
    }

}
