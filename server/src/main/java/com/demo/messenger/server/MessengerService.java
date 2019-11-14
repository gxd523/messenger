package com.demo.messenger.server;

import android.app.Service;
import android.content.Intent;
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
            Log.d("gxd", "ServerMessengerHandler.handleMessage-->" + msg.arg1);
            Messenger clientMessenger = msg.replyTo;
            Message msgToClient = Message.obtain();
            msgToClient.arg1 = msg.arg1 * 2;
            try {
                clientMessenger.send(msgToClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
