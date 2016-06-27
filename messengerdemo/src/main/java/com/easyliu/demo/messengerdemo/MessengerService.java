package com.easyliu.demo.messengerdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;


public class MessengerService extends Service {

    static final String MSG_KEY_FROM_SERVICE = "message";
    static final int MSG_FROM_CLIENT = 1;
    static final int MSG_FROM_SERVICE = 2;
    /**
     * 处理从客户端发来的消息
     */
    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    Messenger client=msg.replyTo;//得到客户端的Messenger
                    Message message = Message.obtain(null,
                            MessengerService.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString(MessengerService.MSG_KEY_FROM_SERVICE, "I have received message!");
                    message.setData(bundle);
                    try {
                        client.send(message);//服务端回传给客户端
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
