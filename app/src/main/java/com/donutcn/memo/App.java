package com.donutcn.memo;

import android.app.Application;

import com.zzhoujay.richtext.RichText;

/**
 * com.donutcn.memo
 * Created by 73958 on 2017/7/9.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RichText.initCacheDir(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RichText.recycle();
    }
}
