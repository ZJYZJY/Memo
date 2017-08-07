package com.donutcn.memo.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/6.
 */

public class FileCacheUtil {

    public static final String docCache = "docs_cache.txt";//缓存文件

    public static final int CACHE_SHORT_TIMEOUT = 1000 * 60 * 5; // 5 分钟

    public static final int CACHE_LONG_TIMEOUT = 1000 * 60 * 60; // 60 分钟

    public static void setCache(Context context, String content, String cacheFileName) {
        setCache(context, content, cacheFileName, MODE_PRIVATE);
    }

    public static void setCache(Context context, String content) {
        setCache(context, content, docCache, MODE_PRIVATE);
    }

    /**
     * set cache.
     * content是要存储的内容，可以是任意格式的，不一定是字符串。
     */
    public static void setCache(Context context, String content, String cacheFileName, int mode) {
        FileOutputStream fos = null;
   try {
            Log.e("write_cache", content);
            //打开文件输出流，接收参数是文件名和模式
            fos = context.openFileOutput(cacheFileName, mode);
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * load the cache
     * @param context context
     * @param cacheFileName cache file name
     * @return
     */
    public static String getCache(Context context, String cacheFileName, int timeOut) {
        FileInputStream fis = null;
        StringBuffer sBuf = new StringBuffer();
        try {
            // if cache file is not out of data, read the cache.
            if (!isCacheOutOfDate(context, cacheFileName, timeOut)) {
                fis = context.openFileInput(cacheFileName);
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = fis.read(buf)) != -1) {
                    sBuf.append(new String(buf, 0, len));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("read_cache", sBuf.toString());
        return sBuf.toString();
    }

    public static String getCachePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    //判断缓存是否过期,比较文件最后修改时间
    private static boolean isCacheOutOfDate(Context context, String cacheFileName, int timeOut) {
        File file = new File(FileCacheUtil.getCachePath(context) + "/"
                + cacheFileName);
        //get the last modified time of cache file.
        long date = file.lastModified();
        long time_out = (System.currentTimeMillis() - date);
        if(timeOut == CACHE_SHORT_TIMEOUT)
            return time_out > FileCacheUtil.CACHE_SHORT_TIMEOUT;
        else
            return time_out > FileCacheUtil.CACHE_LONG_TIMEOUT;
    }
}
