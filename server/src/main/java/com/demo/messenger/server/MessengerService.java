package com.demo.messenger.server;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {
    private Messenger serverMessenger = new Messenger(new ServerMessengerHandler());

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serverMessenger.getBinder();
    }

    private static class ServerMessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            bundle.setClassLoader(Thread.currentThread().getContextClassLoader());
            MsgData msgData = bundle.getParcelable("client");
            Log.d("gxd", "服务端收到了消息-->" + msgData);

            Messenger clientMessenger = msg.replyTo;
            replyMsg(clientMessenger);
        }

        private void replyMsg(Messenger messenger) {
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putParcelable("server", new MsgData("一条来自服务端的消息"));
            msg.setData(bundle);
            try {
                messenger.send(msg);
                Log.d("gxd", "服务端回复了消息-->");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
