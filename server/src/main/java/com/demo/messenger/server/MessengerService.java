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
            // 这段话用于获取客户端发送过来的Book信息，难点在于bundles为什么要设置当前环境的类的加载器，前面说过，Messenger远程通信，归根到底是Parcel在起作用，因此传递的对象必须是实现了Parcelable接口的对象，而ClassLoader类并没有实现，因此在传递过程中会丢失，所以需要重新加载当前环境的类的加载器去寻找User类别进行实例化。如果不设置，会报ClassNotFoundException错误。如果我们把上面的Log语句的注释去掉，系统会报空指针异常，很好的证明了ClassLoader类的消息并不能跨进程传输。
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
