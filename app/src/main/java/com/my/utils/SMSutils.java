package com.my.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信工具类
 */
public class SMSutils {
    // 注册短信变化监听
//        this.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mContentObserver);
    //        getContentResolver().unregisterContentObserver(mContentObserver);// 监听完成后记得注销监听
    //调用前在调用activity注册以下方法
//    android.os.Handler mHandler = new android.os.Handler() {
//        @SuppressWarnings("deprecation")
//        public void handleMessage(android.os.Message msg) {
//            if (msg.what == 100) {
//                String codestr = null;
//                try {
//                    codestr = Cus_UnitTools.getsmsyzm(Reg_ForgetPassword.this);
//                    code.setText(codestr);
//                } catch (Exception e) {
//                }
//            }
//        }
//    };
//    ContentObserver mContentObserver = new ContentObserver(mHandler) {
//        @Override
//        public void onChange(boolean selfChange) {
//            // TODO Auto-generated method stub
//            super.onChange(selfChange);
//            mHandler.sendEmptyMessage(100);
//        }
//    };
    public static String getLatestCaptcha(Activity mActivity, String phoneNum, String contains, int lenth) {
        Y.y("短信解析phoneNum："+phoneNum);
        Y.y("短信解析contains："+contains);
        Y.y("lenth："+lenth);
        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"address", "person", "body"};
        String selection = null;
        if (!TextUtils.isEmpty(phoneNum)) {
            selection = " address='" + phoneNum + "' ";
        }
        String[] selectionArgs = new String[]{};
        String sortOrder = "_id desc";         //我改的
//        String sortOrder = "date desc";   //以前的
        @SuppressWarnings("deprecation")
        Cursor cur = mActivity.managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
        Y.y("cur.getCount() ："+cur.getCount() );
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            String body = cur.getString(cur.getColumnIndex("body")).replaceAll(
                    "\n", " ");
            Y.y("短信1：" + body);
            if (!body.contains(contains)) {
                while (cur.moveToNext()) {
                    String body2 = cur.getString(cur.getColumnIndex("body")).replaceAll(
                            "\n", " ");
                    Y.y("短信2：" + body2);
                    if (body.contains(contains)) {
                        cur.close();
                        return filterFromSMS(body, lenth);
                    }
                }
            }else {
                Y.y("短信3：" + filterFromSMS(body, lenth));
                return filterFromSMS(body, lenth);
            }
            cur.close();
            return filterFromSMS(body, lenth);
        }
        cur.close();
        return null;
    }

    /**
     * 获取最新一条消息的验证码 ,默认为4位
     */
    public static String getLatestCaptcha(Activity mActivity, String phoneNum, String contains) {
        return getLatestCaptcha(mActivity, phoneNum, contains, 4);
    }

    /**
     * 获取最新一条SMS消息
     */
    public static String getLatestSMS(Activity mActivity, int phoneNum) {
        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"address", "person", "body"};
        String selection = " address='" + phoneNum + "' ";
        String[] selectionArgs = new String[]{};
        String sortOrder = "_id desc";         //我改的
//        String sortOrder = "date desc";   //以前的
        @SuppressWarnings("deprecation")
        Cursor cur = mActivity.managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            String body = cur.getString(cur.getColumnIndex("body")).replaceAll(
                    "\n", " ");
            cur.close();
            return body;
        }
        cur.close();
        return null;
    }

    /**
     * 从短信字符窜提取验证码
     *
     * @param body  短信内容
     * @param lenth 验证码的长度 一般6位或者4位
     * @return 接取出来的验证码
     */
    public static String filterFromSMS(String body, int lenth) {
        // 首先([a-zA-Z0-9]{YZMLENGTH})是得到一个连续的六位数字字母组合
        // (?<![a-zA-Z0-9])负向断言([0-9]{YZMLENGTH})前面不能有数字
        // (?![a-zA-Z0-9])断言([0-9]{YZMLENGTH})后面不能有数字出现
        Pattern p = Pattern
                .compile("(?<![a-zA-Z0-9])([a-zA-Z0-9]{" + lenth + "})(?![a-zA-Z0-9])");
        Matcher m = p.matcher(body);
        if (m.find()) {
            System.out.println(m.group());
            return m.group(0);
        }
        return null;
    }
}
