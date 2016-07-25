package com.my.configs;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * 这个是为了提前获取短信权限
 * Created by Administrator on 2016/1/20 0020.
 */
public class SMStest implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static int PERMISSION_READ_SMS = 4;

    public static void test(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS},
                    PERMISSION_READ_SMS);
        } else {
            Uri uri = Uri.parse("content://sms/inbox");
            String[] projection = new String[]{"address", "person", "body"};
            String selection = " address='" + 10086 + "' ";
            String[] selectionArgs = new String[]{};
            String sortOrder = "_id desc";         //我改的
            //        String sortOrder = "date desc";   //以前的
            activity.managedQuery(uri, projection, selection, selectionArgs,
                    sortOrder);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_READ_SMS) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            }
        }
    }
}
