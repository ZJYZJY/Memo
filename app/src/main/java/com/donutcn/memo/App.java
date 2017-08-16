package com.donutcn.memo;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.donutcn.memo.DaoMaster;
import com.donutcn.memo.DaoSession;
import com.donutcn.memo.utils.HttpUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zzhoujay.richtext.RichText;

import org.greenrobot.greendao.database.Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * com.donutcn.memo
 * Created by 73958 on 2017/7/9.
 */

public class App extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Config.isJumptoAppStore = true;
        // enable umeng debug.
        Config.DEBUG = true;
        UMShareAPI.get(this);
        // initialize Bugly
        initBugly();
        // initialize greenDAO
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "memo-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        HttpUtils.create(getApplicationContext());
        //开启信鸽的日志输出
        XGPushConfig.enableDebug(this, true);
//        XGPushConfig.setAccessId(getApplicationContext(), 2100265112);
//        XGPushConfig.setAccessKey(getApplicationContext(), "A95K4WQ6E2QU");

        RichText.initCacheDir(this);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59647377");
        QbSdk.initX5Environment(getApplicationContext(), null);
        // set the default header and footer.
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    {
        PlatformConfig.setWeixin("wx528a40584bf7de1c", "670e9ab67ebd4458af43a6f261c27ac6");
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

    public DaoSession getDaoSession() {
        return daoSession;
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
        // unregister push service.
        XGPushManager.unregisterPush(this);
        RichText.recycle();
    }
}
