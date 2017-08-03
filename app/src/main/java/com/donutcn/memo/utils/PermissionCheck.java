package com.donutcn.memo.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import me.iwf.photopicker.utils.PermissionsConstant;

import static android.Manifest.permission.CAMERA;

public class PermissionCheck {

    public static final int PERMISSION_STORAGE = 20;
    public static final int PERMISSION_RECORD_AUDIO = 21;
    public static final int PERMISSION_READ_CONTACTS = 22;
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
                        getPermissionStr(PERMISSION_STORAGE),
                        PERMISSION_STORAGE);
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        getPermissionStr(PERMISSION_STORAGE),
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
                        getPermissionStr(PERMISSION_RECORD_AUDIO),
                        PERMISSION_RECORD_AUDIO);
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        getPermissionStr(PERMISSION_RECORD_AUDIO),
                        PERMISSION_RECORD_AUDIO);
            }
            return false;
        } else
            return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkContactsPermission() {
        int permissionCheckRead = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS);
        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions((Activity) context,
                        getPermissionStr(PERMISSION_READ_CONTACTS),
                        PERMISSION_READ_CONTACTS);
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        getPermissionStr(PERMISSION_READ_CONTACTS),
                        PERMISSION_READ_CONTACTS);
            }
            return false;
        } else
            return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkContactsPermission(Fragment fragment) {
        int contactPermissionState = ContextCompat.checkSelfPermission(fragment.getContext(),
                Manifest.permission.READ_CONTACTS);

        boolean cameraPermissionGranted = contactPermissionState == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionGranted) {
            fragment.requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    PERMISSION_READ_CONTACTS);
        }
        return cameraPermissionGranted;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String[] getPermissionStr(int code){
        switch (code){
            case PERMISSION_STORAGE:
                return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
            case PERMISSION_RECORD_AUDIO:
                return new String[]{Manifest.permission.RECORD_AUDIO};
            case PERMISSION_READ_CONTACTS:
                return new String[]{android.Manifest.permission.READ_CONTACTS};
        }
        return new String[]{};
    }

    public void showPermissionDialog() {
        ToastUtil.show(context, "permission deny");
    }
}
