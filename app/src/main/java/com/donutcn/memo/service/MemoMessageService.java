package com.donutcn.memo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.donutcn.memo.utils.LogUtil;

public class MemoMessageService extends Service {

    private MessageBinder mBinder = new MessageBinder();

    public MemoMessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if(mBinder != null)
            return mBinder;
        else
            throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy");
    }

    public class MessageBinder extends Binder{

    }
}
