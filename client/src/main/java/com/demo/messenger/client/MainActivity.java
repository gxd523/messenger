package com.demo.messenger.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    private Messenger serverMessenger;
    private Messenger clientMessenger;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverMessenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serverMessenger = null;
        }
    };

    private void bindService() {
        Intent intent = new Intent();
        intent.setClassName("com.demo.messenger.server", "com.demo.messenger.server.MessengerService");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onBtnClick(View view) {
        if (serverMessenger != null) {
            Message msg = Message.obtain();
            msg.arg1 = 9527;
            msg.replyTo = clientMessenger;
            try {
                serverMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
        clientMessenger = new Messenger(new ClientMessengerHandler());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverMessenger != null) {
            unbindService(serviceConnection);
        }
    }

    private static class ClientMessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d("gxd", "ClientMessengerHandler.handleMessage-->" + msg.arg1);
        }
    }
}
