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

import com.demo.messenger.server.MsgData;

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
            Bundle bundle = new Bundle();
            bundle.putParcelable("client", new MsgData("一条来自客户端的消息"));
            msg.setData(bundle);
            msg.replyTo = clientMessenger;
            try {
                Log.d("gxd", "客户端发送了消息-->");
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
            Bundle bundle = msg.getData();
            // 这段话用于获取客户端发送过来的Book信息，难点在于bundles为什么要设置当前环境的类的加载器，前面说过，Messenger远程通信，归根到底是Parcel在起作用，因此传递的对象必须是实现了Parcelable接口的对象，而ClassLoader类并没有实现，因此在传递过程中会丢失，所以需要重新加载当前环境的类的加载器去寻找User类别进行实例化。如果不设置，会报ClassNotFoundException错误。如果我们把上面的Log语句的注释去掉，系统会报空指针异常，很好的证明了ClassLoader类的消息并不能跨进程传输。
            bundle.setClassLoader(Thread.currentThread().getContextClassLoader());
            MsgData msgData = bundle.getParcelable("server");
            Log.d("gxd", "客户端收到了消息-->" + msgData);
        }
    }
}
