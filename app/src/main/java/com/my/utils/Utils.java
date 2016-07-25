package com.my.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.ConstantsME;
import com.my.security.Base64Coder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing some static utility methods.
 *
 * @author Administrator
 * @package_name com.tongfu.me.utils
 * @project_name ME
 * @file_name Utils.java
 * @user Administrator
 * @date,time 2014-10-16下午12:09:03 ALL RIGHT REVERSE
 */
@SuppressLint("NewApi")
public class Utils {
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int DATE_DIALOG = 0;

    ;
    /**
     * 地球半径m
     */
    private static final double EARTH_RADIUS = 6378137.0;
    public static Handler h;
    public static ProgressDialog mypDialog;

    // "[\\[\\]]" 匹配[]
    private Utils() {
    }

    /**
     * 获得屏幕宽度px
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度px
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @return px值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @return dp值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return px值
     */
    public static int getStatusBarHeightPx(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context
     *            The context to use
     * @return The external cache dir
     */

	/*
     * public static File getExternalCacheDir(Context context) { // if
	 * (hasExternalCacheDir()) { // return context.getExternalCacheDir(); // }
	 * // Before Froyo we need to construct the external cache dir ourselves
	 * final String cacheDir = FileUtils.sdPath + "/pic_cache/"; return new
	 * File(cacheDir); }
	 */

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return db值
     */
    public static int getStatusBarHeightDp(Activity activity) {
        // Rect rect = new Rect();
        // activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        final float scale = activity.getResources().getDisplayMetrics().density;
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return (int) (statusHeight / scale + 0.5f);
    }

    /**
     * Workaround for bug pre-Froyo, see here for more info:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (hasHttpConnectionBug()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Get the size in bytes of a bitmap.
     *
     * @param bitmap
     * @return size in bytes
     */

    public static int getBitmapSize(Bitmap bitmap) {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        // return bitmap.getByteCount();
        // }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */

    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        // return Environment.isExternalStorageRemovable();
        // }
        return true;
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */

    public static long getUsableSpace(File path) {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        // return path.getUsableSpace();
        // }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Get the memory class of this device (approx. per-app memory limit)
     *
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /**
     * Check if OS version has a http URLConnection bug. See here for more
     * information:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     *
     * @return
     */
    public static boolean hasHttpConnectionBug() {
        return Build.VERSION.SDK_INT < 8;
    }

    /**
     * Check if OS version has built-in external cache dir method.
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= 8;
    }

    /**
     * Check if ActionBar is available.
     *
     * @return
     */
    public static boolean hasActionBar() {
        return Build.VERSION.SDK_INT >= 11;
    }

    public static void showToast(final String info) {
        h.post(new Runnable() {

            @Override
            public void run() {
                try {
                    // if (mypDialog != null) {
                    // mypDialog.dismiss();
                    // }
                    Toast.makeText(ShareApplication.share, info, 2000).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void showToast(final int id) {
        h.post(new Runnable() {
            @Override
            public void run() {
                // if (mypDialog != null) {
                // mypDialog.dismiss();
                // }
                try {
                    Toast.makeText(
                            ShareApplication.share,
                            ShareApplication.share.getResources().getString(id),
                            2000).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void showToastLong(final String info) {
        h.post(new Runnable() {

            @Override
            public void run() {
                try {
                    // if (mypDialog != null) {
                    // mypDialog.dismiss();
                    // }
                    Toast.makeText(ShareApplication.share, info,
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void showToastLong(final int id) {
        h.post(new Runnable() {
            @Override
            public void run() {
                // if (mypDialog != null) {
                // mypDialog.dismiss();
                // }
                try {
                    Toast.makeText(
                            ShareApplication.share,
                            ShareApplication.share.getResources().getString(id),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void showDialog(Context ac, String info, String title) {
        try {
            new AlertDialog.Builder(ac)
                    .setPositiveButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setMessage(info).setTitle(title).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void showTimeDialog(final Activity ac, int type,
                                      final TextView tv) {
        Calendar calendar = Calendar.getInstance();
        if (type == DATE_DIALOG) {
            DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year,
                                      int month, int dayOfMonth) {
                    tv.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                }
            };
            DatePickerDialog dlg = new DatePickerDialog(ac, dateListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dlg.show();
        } else {
            TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay,
                                      int minute) {
                    tv.setText(hourOfDay + ":" + minute + ":00");
                }
            };
            new TimePickerDialog(ac, timeListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), true).show();
        }

    }

    public static void dismissProcessDialog() {
        // h.post(new Runnable() {
        // @Override
        // public void run() {
        // TODO Auto-generated method stub
        if (mypDialog != null) {
            mypDialog.dismiss();
            mypDialog = null;
        }
        // }
        // });

    }

    public static ProgressDialog showProcessDialog(Context ac, String message) {
        if (mypDialog != null) {
            try {
                mypDialog.setMessage(message);
                mypDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mypDialog;
        }
        mypDialog = new ProgressDialog(ac);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage(message);
        mypDialog.setCancelable(false);
        mypDialog.setIndeterminate(false);
        mypDialog.setCancelable(false);
        try {
            // h.post(new Runnable() {
            //
            // @Override
            // public void run() {
            if (null != mypDialog) {
                mypDialog.show();
            }
            // }
            // });

        } catch (Exception e) {
            e.printStackTrace();
        }
        mypDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // h.post(new Runnable() {
                // @Override
                // public void run() {
                if (null != mypDialog) {
                    mypDialog.dismiss();
                }
                mypDialog = null;

                // }
                // });
                return false;
            }
        });
        return mypDialog;

    }

    public static ProgressDialog showProcessDialog(Context ac, String message,
                                                   Drawable indeterminateDrawable) {
        if (mypDialog != null) {
            try {
                mypDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mypDialog.setMessage(message);
            return mypDialog;
        }
        mypDialog = new ProgressDialog(ac);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage(message);
        mypDialog.setCancelable(false);
        mypDialog.setIndeterminate(false);
        if (null != indeterminateDrawable) {
            mypDialog.setIndeterminateDrawable(indeterminateDrawable);
            mypDialog.setIndeterminate(false);
            mypDialog.setInverseBackgroundForced(false);
            indeterminateDrawable.setBounds(20, 20, 20, 20);
        }
        mypDialog.setCancelable(false);
        try {
            // h.post(new Runnable() {
            //
            // @Override
            // public void run() {
            if (null != mypDialog) {
                mypDialog.show();
            }
            // }
            // });

        } catch (Exception e) {
            e.printStackTrace();
        }
        mypDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // h.post(new Runnable() {
                //
                // @Override
                // public void run() {
                if (null != mypDialog) {
                    mypDialog.dismiss();
                }
                mypDialog = null;

                // }
                // });

                return false;
            }
        });

        return mypDialog;
    }

    public static ProgressDialog showProcessDialog(Context ac, int id) {
        if (mypDialog != null) {
            try {
                mypDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mypDialog.setMessage(ac.getResources().getString(id));
            return mypDialog;
        }
        mypDialog = new ProgressDialog(ac);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage(ac.getResources().getString(id));
        mypDialog.setCancelable(false);
        mypDialog.setIndeterminate(false);
        mypDialog.setCancelable(false);
        try {
            if (null != mypDialog) {
                mypDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mypDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (null != mypDialog) {
                    mypDialog.dismiss();
                }
                mypDialog = null;
                return false;
            }
        });

        return mypDialog;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 计算距离
     * <p>
     * 网 返回单位米；
     */
    public synchronized static double distanceByLngLat(double lng1,
                                                       double lat1, double lng2, double lat2) {
        double radLat1 = lat1 * Math.PI / 180;
        double radLat2 = lat2 * Math.PI / 180;
        double a = radLat1 - radLat2;
        double b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    // 两个方法差不多；

    /**
     * 计算距离 直接传入自己的经纬度和另外一个点的经纬度
     *
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return 返回单位米；
     */
    public synchronized static double getDistance(double longitude1,
                                                  double latitude1, double longitude2, double latitude2) {
        try {
            if (null == String.valueOf(longitude1)
                    || "".equals(String.valueOf(longitude1)))
                return 0.00;
        } catch (Exception e) {
            return 0.00;
        }
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static void getFormatDistance(TextView tv_distance,
                                         double longitude, double latitude) {
        Double distance = null;
        String distance_str = null;
        if ("".equals(PreferencesHelper.getString(ConstantsME.LONGITUDE))
                || null == PreferencesHelper.getString(ConstantsME.LONGITUDE)) {
            tv_distance.setText("定位失败");
        } else {
            try {
                double longitude_local = Double.parseDouble(PreferencesHelper
                        .getString(ConstantsME.LONGITUDE));
                double latitude_local = Double.parseDouble(PreferencesHelper
                        .getString(ConstantsME.LATITUDE));
                distance = Utils.getDistance(longitude_local, latitude_local,
                        longitude, latitude);
                // if(0.00==distance){
                // tv_distance.setText("距离未知");
                // return;
                // }
                distance_str = (distance != null) ? distance.toString() : null;
                if (distance_str != null) {
                    int first_index = distance_str.indexOf(".");
                    distance_str = first_index > 0 ? distance_str.substring(0,
                            first_index) : distance_str;
                }
                if (Integer.valueOf(distance_str) > 1000) {
                    DecimalFormat df = (DecimalFormat) NumberFormat
                            .getInstance();
                    // 获得格式化类对象
                    df.applyPattern("0.00");// 设置小数点位数(两位) 余下的会四舍五入
                    Double size = (Double.valueOf(distance_str) / 1000);
                    if (size > 10000) {
                        tv_distance.setText("未知");
                    } else {
                        tv_distance.setText(df.format(size) + "km");
                    }
                }
                // 没有返回来；
                else {
                    tv_distance.setText(distance_str + "m");
                }
            } catch (NumberFormatException e) {
                tv_distance.setText("距离未知");
            }
        }
    }

    /**
     * @param longitude
     * @param latitude
     * @param unit      单位（米：m）
     * @return
     */
    public static String getFormatDistance(double longitude, double latitude,
                                           String unit) {
        Double distance = null;
        String distance_str = null;
        if ("".equals(PreferencesHelper.getString(ConstantsME.LONGITUDE))
                || null == PreferencesHelper.getString(ConstantsME.LONGITUDE)) {
            return "定位失败";
        } else {
            try {
                double longitude_local = Double.parseDouble(PreferencesHelper
                        .getString(ConstantsME.LONGITUDE));
                double latitude_local = Double.parseDouble(PreferencesHelper
                        .getString(ConstantsME.LATITUDE));
                distance = Utils.getDistance(longitude_local, latitude_local,
                        longitude, latitude);
                // if(0.00==distance){
                // tv_distance.setText("距离未知");
                // return;
                // }
                distance_str = (distance != null) ? distance.toString() : null;
                if (distance_str != null) {
                    int first_index = distance_str.indexOf(".");
                    distance_str = first_index > 0 ? distance_str.substring(0,
                            first_index) : distance_str;
                }
                if (Integer.valueOf(distance_str) > 1000) {
                    DecimalFormat df = (DecimalFormat) NumberFormat
                            .getInstance();
                    // 获得格式化类对象
                    df.applyPattern("0.00");// 设置小数点位数(两位) 余下的会四舍五入
                    Double size = (Double.valueOf(distance_str) / 1000);
                    if (size > 10000) {
                        return "未知";
                    } else {
                        if (unit.equals("米")) {

                            return df.format(size) + "千米";
                        }
                        return df.format(size) + "km";
                    }
                }
                // 没有返回来；
                else {
                    if (unit.equals("米")) {

                        return distance_str + "米";
                    }
                    return distance_str + "m";
                }
            } catch (NumberFormatException e) {
                return ("距离未知");
            }
        }
    }

    // DecimalFormat dt = (DecimalFormat) DecimalFormat.getInstance(); //
    // // 获得格式化类对象
    // dt.applyPattern("0.00");// 设置小数点位数(两位) 余下的会四舍五入
    // Double size = (Double.valueOf(distance_str) / 1000);
    // if (size > 10000) {
    // listitem_distence.setText("未知");
    // } else {
    // listitem_distence.setText(dt.format(size) + "km");
    // }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳 传入日期格式"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getDateDistance(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timemillis = 0;
        try {
            Date date = sdf.parse(timeStr);
            timemillis = date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "时间未知";
        }
        StringBuffer sb = new StringBuffer();
        // long timemillis = Long.parseLong(timeStr);
        if (0 == timemillis) {
            return "";
        } else {
            long time = System.currentTimeMillis() - (timemillis);
            long mill = (long) Math.floor(time / 1000);// 秒前
            long minute = (long) Math.floor(time / 60 / 1000.0f);// 分钟前
            long hour = (long) Math.floor(time / 60 / 60 / 1000.0f);// 小时
            long day = (long) Math.floor(time / 24 / 60 / 60 / 1000.0f);// 天前
            if (day > 0) {
                sb.append(day + "天");
            } else if (hour > 0) {
                if (hour >= 24) {
                    sb.append("1天");
                } else {
                    sb.append(hour + "小时");
                }
            } else if (minute > 0) {
                if (minute == 60) {
                    sb.append("1小时");
                } else {
                    sb.append(minute + "分钟");
                }
            } else if (mill > 0) {
                if (mill == 60) {
                    sb.append("1分钟");
                } else {
                    sb.append(mill + "秒");
                }
            } else {
                sb.append("刚刚");
            }
            if (!"刚刚".equals(sb.toString())) {
                sb.append("前");
            }
            return sb.toString();
        }
    }

    /**
     * @param timeMillis 毫秒数
     * @return 30:32
     */
    public static String getTimeFilmFormat(float timeMillis) {
        if (timeMillis <= 0) {
            return "0:00";
        }
        int seconds = (int) ((timeMillis / 1000) / 60);
        int minute = (int) ((timeMillis / 1000) % 60);
        String secondsT = (seconds >= 10) ? String.valueOf(minute) : "0"
                + String.valueOf(seconds);
        String minuteT = (minute >= 10) ? String.valueOf(minute) : "0"
                + String.valueOf(minute);
        return secondsT + ":" + minuteT;

    }

    /**
     * @param timeMillis 毫秒数
     * @return 30:32
     */
    public static String getTimeFilmFormat(String timeMillis) {
        try {
            Y.y("String_timeMillis:" + timeMillis);
            float timeMillis2 = Float.parseFloat(timeMillis);
            Y.y("timeMillis2:" + timeMillis2);
            if (timeMillis2 <= 0) {
                return "0:00";
            }
            int seconds = (int) ((timeMillis2 / 1000) / 60);
            int minute = (int) ((timeMillis2 / 1000) % 60);
            String secondsT = (seconds >= 10) ? String.valueOf(minute) : "0"
                    + String.valueOf(seconds);
            String minuteT = (minute >= 10) ? String.valueOf(minute) : "0"
                    + String.valueOf(minute);
            return secondsT + ":" + minuteT;
        } catch (Exception e) {
            return "0:00";
        }

    }

    /**
     * 获取手机自身内存路径
     */
    public static String getPhoneCardPath() {
        return Environment.getDataDirectory().getPath();

    }

    /**
     * 判断SD卡是否存在
     */
    public static boolean isSDcardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * android获取sd卡路径方法
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        }
        return null;
    }

    /**
     * 查看SD卡的剩余空间
     */
    public static long getSDcardFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 查看SD卡总容量
     */
    public static long getSDcardTotalSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        // return allBlocks * blockSize; //单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 验证号码的有效性
     *
     * @param mobiles
     * @return boolean
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,1,2,5-9])|(17[0-9]))\\d{8}$");
        // 下面这个是坑爹的,用上面那个
        // Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(mobiles);
        // MLog.d("BaseUtils-->isMobileNO-->", m.matches() + "");
        return m.matches();
    }

    /**
     * 匹配邮箱
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 获取所选图片的路径/主要解决4.4选择图片的问题
     *
     * @param context
     * @param uri     图片返回的uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/"
                                + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection,
                            selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            } else if (isKitKat
                    && DocumentsContract.isDocumentUri(context, uri)) {

            }
            try {
                return uri.toString();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            return uri.toString();
        }
        return null;
    }

    /**
     * @param context
     * @param data
     * @return
     */
    public static String selectImage(Context context, Intent data) {
        Uri selectedImage = data.getData();
        // Log.e(TAG, selectedImage.toString());
        if (selectedImage != null) {
            String uriStr = selectedImage.toString();
            String path = uriStr.substring(10, uriStr.length());
            if (path.startsWith("com.sec.android.gallery3d")) {
                Y.y("It's auto backup pic path:" + selectedImage.toString());
                return null;
            }
        }
        String[] filePathColumn = {MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    /**
     * 根据图片地址获取uri 次方法可能不行
     *
     * @param context
     * @param path
     * @return
     */
    public static Uri getUriFromPicpath(Context context, String path) {
        // if (uri.getScheme().equals("file") && (path.contains("image/"))) {
        // String path = uri.getEncodedPath();
        Uri uri = null;
        if (path != null) {
            path = Uri.decode(path);
            ContentResolver cr = context.getContentResolver();
            StringBuffer buff = new StringBuffer();
            buff.append("(").append(MediaColumns.DATA).append("=")
                    .append("'" + path + "'").append(")");
            Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{BaseColumns._ID}, buff.toString(), null,
                    null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                index = cur.getColumnIndex(BaseColumns._ID);
                // set _id value
                index = cur.getInt(index);
            }
            if (index == 0) {
                // do nothing
            } else {
                Uri uri_temp = Uri
                        .parse("content://media/external/images/media/" + index);
                if (uri_temp != null) {
                    uri = uri_temp;
                }
            }
        }
        // }
        return uri;
    }

    /**
     * 从view 得到图片
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }

    public static Bitmap getBitmapScale(Bitmap bitmapSrc, float xScale, float yScale) {
        Bitmap bitmap;
        int dstWidth = bitmapSrc.getWidth();
        int dstHeight = bitmapSrc.getHeight();
        bitmap = Bitmap.createScaledBitmap(bitmapSrc, (int) (dstWidth * xScale), (int) (dstHeight * yScale), false);
        return bitmap;
    }

    public static Bitmap getBitmapFromSDcard(String path) {
        Y.y("11");
        Bitmap bitmap = null;
        Y.y("12");
        if (null == path || "".equals(path)) {
            Y.y("13");
            return bitmap;
        }
        Y.y("14");
        try {
            Y.y("15");
            bitmap = BitmapFactory.decodeFile(path);
            Y.y("16");
        } catch (Exception e) {
            Y.y("17");
            return bitmap;
        }
        Y.y("18");
        return bitmap;
    }

    /**
     * 将file转成bitmap
     *
     * @param file   src文件
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromFile(File file, int width, int height) {
        Bitmap bitmap = null;
        // Y.y("getBitmapFromFile/1");
        // Y.y("flie==null:" + (file == null));
        // Y.y("flie=getAbsolutePath:" + (file.getAbsolutePath()));
        // Y.y("flie=getPath:" + (file.getPath()));
        // Y.y("file.exists():" + (file.exists()));
        if (null != file && file.exists()) {
            // Y.y("getBitmapFromFile/2");
            BitmapFactory.Options opts = null;
            // Y.y("getBitmapFromFile/3");
            if (width > 0 && height > 0) {
                // Y.y("getBitmapFromFile/4");
                opts = new BitmapFactory.Options();
                // Y.y("getBitmapFromFile/5");
                opts.inJustDecodeBounds = true;
                // Y.y("getBitmapFromFile/6");
                BitmapFactory.decodeFile(file.getPath(), opts);
                // Y.y("getBitmapFromFile/7");
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                // Y.y("minSideLength：" + minSideLength);
                // Y.y("width:" + width);
                // Y.y("height:" + height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);
                // Y.y("computeSampleSize:" + opts.inSampleSize);
                // Y.y("getBitmapFromFile/9");
                opts.inJustDecodeBounds = false;
                // Y.y("getBitmapFromFile/10");
                opts.inInputShareable = true;
                // Y.y("getBitmapFromFile/11");
                opts.inPurgeable = true;
                // Y.y("getBitmapFromFile/12");
            }
            try {
                // Y.y("getBitmapFromFile/start");
                bitmap = BitmapFactory.decodeFile(file.getPath(), opts);
                // Y.y("bitmap==null:" + (bitmap == null));
            } catch (OutOfMemoryError e) {
                // Y.y("getBitmapFromFile/OutOfMemoryError:" +
                // e.getMessage());
                e.printStackTrace();
            }
        }
        // Y.y("getBitmapFromFile//null");
        // Y.y("bitmap==null" + (bitmap == null));
        return bitmap;
    }

    /**
     * 图片压缩
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        Y.y("computeSampleSize/1");
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        Y.y("initialSize:" + initialSize);
        Y.y("computeSampleSize/2");
        int roundedSize;
        if (initialSize <= 8) {
            Y.y("computeSampleSize/3");
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
            Y.y("computeSampleSize/4");
        } else {
            Y.y("computeSampleSize/5");
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        Y.y("computeSampleSize/6");
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        Y.y("computeInitialSampleSize/1");
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        Y.y("computeInitialSampleSize/2");
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        Y.y("minSideLength:" + minSideLength);
        Y.y("maxNumOfPixels:" + maxNumOfPixels);
        Y.y("lowerBound:" + lowerBound);
        Y.y("upperBound:" + upperBound);
        Y.y("computeInitialSampleSize/3");
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        Y.y("computeInitialSampleSize/4");
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            Y.y("computeInitialSampleSize/5");
            return 1;
        } else if (minSideLength == -1) {
            Y.y("computeInitialSampleSize/6");
            return lowerBound;
        } else {
            Y.y("computeInitialSampleSize/7");
            return upperBound;
        }
    }

    /**
     * 将uri转成bitmap
     *
     * @param uri
     * @return
     */
    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            return null;
        }
        return bitmap;
    }

    /**
     * 从Android手机屏幕截图
     *
     * @param activity
     * @return 返回一个bitmap
     */
    private static Bitmap getScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        Bitmap bitmapScreenShot = Bitmap.createBitmap(bitmap, 0,
                statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bitmapScreenShot;
    }

    /**
     * 从Android手机屏幕截图后保存
     *
     * @return 返回一个bitmap
     */
    private static void savePicAfterShotScreen(Bitmap bitmap, String filePath,
                                               String fileName) {
        FileOutputStream fos = null;
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(file, fileName + ".jpg");
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * 从Android手机屏幕截图成功后并保存
     *
     * @param activity
     * @return 返回一个bitmap
     */
    public static void getScreenShotThenSave(Activity activity,
                                             String filePath, String fileName) {
        if (filePath == null) {
            return;
        }
        // if (!filePath.getParentFile().exists()) {
        // filePath.getParentFile().mkdirs();
        // }
        savePicAfterShotScreen(getScreenShot(activity), filePath, fileName);
    }

    /**
     * <pre>
     * 上传文件至Server的方法
     *
     * @param newName   传到服务器的文件文字
     * @param filePath  上传文件的sdcard路径
     * @param actionUrl 服务器的url
     *                  <p>
     *                  <pre/>
     */
    public static void upLoadFiles(String newName, String filePath,
                                   String actionUrl) {
        // String newName = "image.jpg";
        // String uploadFile = "/sdcard/image.JPG";
        // String actionUrl = "http://192.168.0.71:8086/HelloWord/myForm";
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置传送的method=POST */
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            /* 设置DataOutputStream */
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; "
                    + "name=\"file1\";filename=\"" + newName + "\"" + end);
            dos.writeBytes(end);
            /* 取得文件的FileInputStream */
            FileInputStream fis = new FileInputStream(filePath);
            /* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            /* 从文件读取数据至缓冲区 */
            while ((length = fis.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                dos.write(buffer, 0, length);
            }
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            fis.close();
            dos.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            /* 将Response显示于Dialog */
            showDialog(null, "上传成功" + b.toString().trim());
            /* 关闭DataOutputStream */
            dos.close();
        } catch (Exception e) {
            showDialog(null, "上传失败" + e);
        }
    }

    private static void showDialog(Context context, String mess) {
        new AlertDialog.Builder(context).setTitle("Message").setMessage(mess)
                .setNegativeButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    /**
     * <pre>
     * 传入ture表示收起软键盘，false改变软键盘当前显示状态
     */
    public static void isCloseSoftInputMethod(Context context, EditText et,
                                              boolean bool) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (bool) {
            if (null != et) {
                // 设置软键盘隐藏
                imm.hideSoftInputFromWindow(et.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                // imm.hideSoftInputFromWindow(getCurrentFocus()
                // .getApplicationWindowToken(),
                // InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                // 关闭activity的SoftInputMethod
                try {
                    imm.hideSoftInputFromWindow(((Activity) context)
                                    .getCurrentFocus().getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            // 改变软键盘当前显示状态
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 从一个bitmap获取它转成的base64code
     *
     * @param bitmap
     * @return
     */
    public static String getPicBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] by = baos.toByteArray(); // 将图片流以字符串形式存储下来
        // String pic =Base64.encodeToString(by, Base64.DEFAULT);//android自带
        String pic = Base64Coder.encodeLines(by);// 自定义base64code
        if (null != pic && !"".equals(pic)) {

            return pic;
        } else {
            return "";
        }
    }

    /**
     * 获取栈顶activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    /**
     * 获取运行的service
     *
     * @param context
     * @return
     */
    public static List<ActivityManager.RunningServiceInfo> getRunningServices(Context context, int maxNum) {

        ActivityManager activityManager;
        try {
            activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            //获取服务
            List<ActivityManager.RunningServiceInfo> serviceTasks =
                    activityManager.getRunningServices(maxNum);
            return serviceTasks;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取运行的service
     *
     * @param context
     * @return
     */
    public static List<ActivityManager.RunningServiceInfo> getRunningServices(Context context) {

        ActivityManager activityManager;
        try {
            activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            //获取服务
            List<ActivityManager.RunningServiceInfo> serviceTasks =
                    activityManager.getRunningServices(30);
            return serviceTasks;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取运行的service
     *
     * @param context
     * @param processName 进程名称
     * @return
     */
    public static boolean isServiceRunning(Context context, String processName) {

        ActivityManager activityManager;
        try {
            activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            //获取服务
            List<ActivityManager.RunningServiceInfo> serviceTasks =
                    activityManager.getRunningServices(30);
            if (null == processName || "".equals(processName)) {
                return false;
            }
            for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceTasks) {
//                Y.y("获取服务是否存在：" + runningServiceInfo.process + "   " + runningServiceInfo.pid + "   " + runningServiceInfo.clientPackage);
                if (processName.equals(runningServiceInfo.process)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 获取运行的service的ComponentName
     *
     * @param context
     * @param processName 进程名称
     * @return
     */
    public static ComponentName getRunningServiceComponentName(Context context, String processName) {

        ActivityManager activityManager;
        try {
            activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            //获取服务
            List<ActivityManager.RunningServiceInfo> serviceTasks =
                    activityManager.getRunningServices(30);
            if (null == processName || "".equals(processName)) {
                return null;
            }
            for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceTasks) {
//                Y.y("获取服务是否存在：" + runningServiceInfo.process + "   " + runningServiceInfo.pid + "   " + runningServiceInfo.clientPackage);
                if (processName.equals(runningServiceInfo.process)) {
//                    runningServiceInfo.
                    return runningServiceInfo.service;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * <pre>
     * 获取rootview根
     *
     * @param activity
     * @return </pre>
     */
    public static View getRootView(Activity activity) {
        View rootView = ((ViewGroup) (activity.getWindow().getDecorView()
                .findViewById(android.R.id.content))).getChildAt(0);
        // return ((ViewGroup) activity.findViewById(android.R.id.content))
        // .getChildAt(0);
        return rootView;
    }

    /**
     * Android 读取Assets中图片 String path = "file:///android_asset/文件名";
     *
     * @param filePath 为文件名 ，不加"file:///android_asset/"
     * @return
     */
    public static Bitmap getBitmapFromAssetsFile(Context context,
                                                 String filePath) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(filePath);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * <pre>
     * 插入表情
     * 用指定的resourceId图片insert编辑框
     * resourceId资源id 对应drawable
     * drawable的优先级高于resourceId
     * et_message编辑的EditText
     * outputX设置图片x,drawable==null无效
     * outputY设置图片y，drawable==null无效
     * </pre>
     */
    public static void insertMood(final Context context,
                                  final String filePath_assets, final Drawable drawable,
                                  EditText et_message, final int outputX, final int outputY) {
        final int outputX_new = Utils.dip2px(context, outputX);
        final int outputY_new = Utils.dip2px(context, outputY);
        ImageGetter imageGetter = new ImageGetter() {
            @Override
            public Drawable getDrawable(String arg0) {
                // TODO Auto-generated method stub
                if (drawable == null) {
                    Drawable d = new BitmapDrawable(
                            Utils.getBitmapFromAssetsFile(context,
                                    filePath_assets));
                    d.setBounds(0, 0, outputX_new, outputY_new);
                    // d.setBounds(0, 0, d.getIntrinsicWidth(),
                    // d.getIntrinsicHeight());
                    return d;
                } else {
                    drawable.setBounds(0, 0, outputX_new, outputY_new);
                    // drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    // drawable.getIntrinsicHeight());
                    return drawable;
                }
            }
        };
        CharSequence charsequence = Html.fromHtml("<img src='"
                + filePath_assets + "'/>", imageGetter, null);
        int currentPositionStart = et_message.getSelectionStart();// 得到当前光标位置
        int currentPositionEnd = et_message.getSelectionEnd();// 得到当前光标位置
        et_message.getText().insert(currentPositionStart, charsequence);
        // String message = FilterHtml(Html.toHtml(et_message.getText()));
    }

    public static String getFilterText(EditText et_message) {
        String message = FilterHtml(Html.toHtml(et_message.getText()));
        // Y.y( "et_message.getText():" + et_message.getText());
        // Y.y( "message:" + message);
        return message;

    }

    /**
     * 方法过滤一下
     */
    private static String FilterHtml(String str) {
        str = str.replaceAll("<(?!br|img)[^>]+>", "").trim();
        return UnicodeToGBK2(str);
    }

    /**
     * 有些可能发送后就会出现#&62137这样编码的内容，需要再转码一下，转码的方法
     */
    private static String UnicodeToGBK2(String s) {
        String[] k = s.split(";");
        String rs = "";
        for (int i = 0; i < k.length; i++) {
            int strIndex = k[i].indexOf("&#");
            String newstr = k[i];
            if (strIndex > -1) {
                String kstr = "";
                if (strIndex > 0) {
                    kstr = newstr.substring(0, strIndex);
                    rs += kstr;
                    newstr = newstr.substring(strIndex);
                }
                int m = Integer.parseInt(newstr.replace("&#", ""));
                char c = (char) m;
                rs += c;
            } else {
                rs += k[0];
            }
        }
        return rs;
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromNetwork(String url) {
        return getBitmapFromNetwork(url, 0, 0);
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromNetwork(String url, int bitmapWidth,
                                              int bitmapHeight) {
        Y.y("getHttpBitmap1");
        URL myFileURL = null;
        Bitmap bitmap = null;
        Y.y("getHttpBitmap2");
        if (null == url || "".equals(url)) {
            return null;
        }
        try {
            myFileURL = new URL(url);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 获得连接
        Y.y("getHttpBitmap3");
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) myFileURL.openConnection();
            // 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            Y.y("getHttpBitmap4");
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            Y.y("getHttpBitmap5");
            // 连接设置获得数据流
            // conn.setDoInput(true);
            // conn.setDoOutput(true);
            Y.y("getHttpBitmap6");
            // 不使用缓存
            conn.setUseCaches(false);
            conn.connect();
            Y.y("getHttpBitmap7");
            // 这句可有可无，没有影响
            // conn.connect();
            // 得到数据流
            InputStream is = conn.getInputStream();
            // byte[] buffer=new byte[1024];
            // while(is.read(buffer)!=-1){
            Y.y("getHttpBitmap8");
            // 解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            // }
            Y.y("getHttpBitmap9");
            if (bitmapWidth != 0 && bitmapHeight != 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth,
                        bitmapHeight, true);
            }
            // 关闭数据流
            is.close();
            Y.y("getHttpBitmap10");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Y.y("getHttpBitmap11");
            Y.y("e:" + e.getMessage() + e.getLocalizedMessage());
            // return null;
        }
        Y.y("bitmap==null:" + (null == bitmap));
        return bitmap;
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static File getBitmapFromNetwork(String url, String path,
                                            String fileName) {
        return getBitmapFromNetwork(url, path, fileName, 0, 0);
    }

    /**
     * 获取网落图片资源
     *
     * @param url          图片url
     * @param path         保存file在path中
     * @param fileName     文件名
     * @param bitmapWidth  设置保存图片的width
     * @param bitmapHeight 设置保存图片的height
     * @return
     */
    public static File getBitmapFromNetwork(String url, String path,
                                            String fileName, int bitmapWidth, int bitmapHeight) {
        Y.y("getHttpBitmap1");
        URL myFileURL = null;
        Bitmap bitmap = null;
        Y.y("getHttpBitmap2");
        if (null == url || "".equals(url) || null == path || "".equals(path)
                || null == fileName || "".equals(fileName)) {
            return null;
        }
        try {
            myFileURL = new URL(url);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 获得连接
        Y.y("getHttpBitmap3");
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) myFileURL.openConnection();
            // 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            Y.y("getHttpBitmap4");
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            Y.y("getHttpBitmap5");
            // 连接设置获得数据流
            // conn.setDoInput(true);
            // conn.setDoOutput(true);
            Y.y("getHttpBitmap6");
            // 不使用缓存
            conn.setUseCaches(false);
            conn.connect();
            Y.y("getHttpBitmap7");
            // 这句可有可无，没有影响
            // conn.connect();
            // 得到数据流
            InputStream is = conn.getInputStream();
            // byte[] buffer=new byte[1024];
            // while(is.read(buffer)!=-1){
            Y.y("getHttpBitmap8");
            // 解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            // }
            Y.y("getHttpBitmap9");
            if (bitmapWidth != 0 && bitmapHeight != 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth,
                        bitmapHeight, true);
            }
            // 关闭数据流
            is.close();
            Y.y("getHttpBitmap10");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Y.y("getHttpBitmap11");
            Y.y("e:" + e.getMessage() + e.getLocalizedMessage());
            // return null;
        }
        Y.y("bitmap==null:" + (null == bitmap));
        if (null == bitmap) {
            return null;
        } else {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            f = new File(path, fileName);
            BufferedOutputStream bos;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(f));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return f;
        }
    }

    /**
     * 添加文字到图片，类似水印文字。
     */
    public static Bitmap getBitmapWatermark(Context mContext, int gResId,
                                            String text, String text2) {
        Resources resources = mContext.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
        return getBitmapWatermark(mContext, bitmap, text, text2);
    }

    /**
     * 图片加水印
     *
     * @param mContext
     * @param bitmap
     * @param text     字体尺寸12sp
     * @return
     */
    public static Bitmap getBitmapWatermark(Context mContext, Bitmap bitmap,
                                            String text, String text2) {
        return getBitmapWatermark(mContext, bitmap, text, text2, 18);

    }

    /**
     * 图片加水印
     *
     * @param mContext
     * @param bitmap
     * @param text
     * @param textSize 字体尺寸12sp
     * @return
     */
    public static Bitmap getBitmapWatermark(Context mContext, Bitmap bitmap,
                                            String text, String text2, int textSize) {
        if (null == bitmap) {
            return null;
        }
        if (null == text || "".equals(text)) {
            text = "";
        }
        if (null == text2 || "".equals(text2)) {
            text2 = "";
        }
        Resources resources = mContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(255, 255, 255));
        // text size in pixels
        paint.setTextSize((int) (textSize));
        // paint.setTextSize((int) (14 * scale * 5));
        // text shadow
        // paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        // int x = (bitmap.getWidth() - bounds.width()) / 2;
        // int y = (bitmap.getHeight() + bounds.height()) / 2;
        // draw text to the bottom
        int x = (bitmap.getWidth() - bounds.width()) / 10 * 5;
        int y = (bitmap.getHeight() + bounds.height()) / 20 * 15;
        if (!"".equals(text)) {
            canvas.drawText(text, x, y, paint);
        } else if (!"".equals(text2)) {
            canvas.drawText(text2, x, y, paint);
        }
        if (!"".equals(text) && !"".equals(text2)) {
            int x1 = (bitmap.getWidth() - bounds.width()) / 10 * 5;
            int y1 = (bitmap.getHeight() + bounds.height()) / 20 * 17;
            canvas.drawText(text2, x1, y1, paint);
        }

        return bitmap;
    }

    /**
     * MD5加密,不可逆
     *
     * @param str
     * @return
     */
    public static String getMD5digest(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toUpperCase();
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static String getVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "1.0.0";
        }
    }

    public static int getVersionCode(Context context) {
        try {
            int code = 0;
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            code = info.versionCode;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            CharSequence applicationLabel = packageManager
                    .getApplicationLabel(packageManager.getApplicationInfo(
                            context.getPackageName(), 0));
            return applicationLabel.toString();
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * <pre>
     *
     * @param context
     * @return 最后不要忘记在AndroidManifest.xml中增加权限：
     * <uses-permission android:name ="android.permission.GET_TASKS"/>
     * </pre>
     */
    public static String getTopActivity(Activity context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).toString();
        else
            return "";
    }

    /**
     * 设置activity状态栏悬浮
     *
     * @param activity
     * @param setTranslucentStatus
     * @return
     */
    public static boolean setTranslucentStatus(Activity activity,
                                               boolean setTranslucentStatus) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (setTranslucentStatus) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
            return true;
        }
        return false;
    }

    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarTintColor(Activity activity) {
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(activity.getResources().getColor(
                R.color.transparent));// 通知栏所需颜色
    }

    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarTintColor(Activity activity,
                                             int colorResource) {
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        // 设置一个颜色给系统栏
        tintManager.setTintColor(colorResource);// 通知栏所需颜色
    }

    /**
     * 以下为直接从assets读取 代码
     *
     * @param fileName
     * @return
     */
    public String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context
                    .getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
