package com.donutcn.memo;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.donutcn.memo.utils.HttpUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zzhoujay.richtext.RichText;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * com.donutcn.memo
 * Created by 73958 on 2017/7/9.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Config.isJumptoAppStore = true;
        // enable umeng debug.
        Config.DEBUG = true;
        UMShareAPI.get(this);
        // init Bugly
        initBugly();
        HttpUtils.create(getApplicationContext());
        RichText.initCacheDir(this);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59647377");
    }

    {
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setSinaWeibo("784424263", "199cc871ee45736419a67864febc9674", "http://sns.whalecloud.com");
        PlatformConfig.setQQZone("1106313160", "j53KME8pmA8fhOcB");
    }

    private void initBugly() {
        Context context = getApplicationContext();
        String packageName = context.getPackageName();
        String processName = getProcessName(android.os.Process.myPid());
        // setting for the reporting process.
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));

        Bugly.init(context, "d2e8294caf", true, strategy);
    }

    /**
     * Gets the process name corresponding to the process number.
     *
     * @param pid process id
     * @return process name
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RichText.recycle();
    }
}
