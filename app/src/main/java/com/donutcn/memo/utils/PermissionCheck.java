package com.donutcn.memo.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionCheck {

    public static final int PERMISSION_STORAGE = 20;
    public static final int PERMISSION_RECORD = 21;
    private Context context;

    public PermissionCheck(Context context) {
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkStoragePermission() {
        int permissionCheckRead = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED || permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_STORAGE);
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_STORAGE);
            }
            return false;
        } else
            return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkRecordPermission() {
        int permissionCheckRead = ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO);
        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_RECORD);
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_RECORD);
            }
            return false;
        } else
            return true;
    }

    public void showPermissionDialog() {
        ToastUtil.show(context, "permission deny");
    }
}
