package com.donutcn.memo;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zzhoujay.richtext.RichText;

/**
 * com.donutcn.memo
 * Created by 73958 on 2017/7/9.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Config.isJumptoAppStore = true;
        Config.DEBUG = true;
        UMShareAPI.get(this);
        RichText.initCacheDir(this);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=59647377");
    }

    {
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RichText.recycle();
    }
}
