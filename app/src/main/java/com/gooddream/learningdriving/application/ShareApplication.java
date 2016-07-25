package com.gooddream.learningdriving.application;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.security.MD5;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.Y;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShareApplication extends Application{

    public static final String EXTRA_IMAGE = "extra_image";
    // //////////////////
    public static ShareApplication share;
    public static VolleyUtils volleyUtils;
    static String TAG = "ShareApplication";
    /**
     * 管理全局activity
     */
    ArrayList<Activity> list = new ArrayList<Activity>();

    /**
     * 获取ShareApplication的单例
     *
     * @return ApplicationController singleton instance
     */
    public static synchronized ShareApplication getInstance() {
        return share;
    }

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Y.y("ShareApplication1");
        if (Configs.allowLog) {
//            LeakCanary.install(this);
        }
        Utils.h = new Handler();
        Y.y("ShareApplication2");
        CustomProgressDialogUtils.h = new Handler();
        Y.y("ShareApplication3");
        share = this;
        Y.y("ShareApplication4");
        volleyUtils = VolleyUtils.getInstance();
        Y.y("ShareApplication5");
        PreferencesHelper.putString(ConstantsME.deviceId, MD5.md5(getIMEI(getApplicationContext())) + MD5.md5(DevIDShort));
        Y.y("ShareApplication6");
//        initDefaultUncaughtExceptionHandler();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION  ) == PackageManager.PERMISSION_GRANTED) {
            initLocation_gaode();
        }
        Y.y("ShareApplication7");
    }

    private void initLocation_gaode() {
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(new MyAMapLocationListener());
        initOption(30000);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 设置定位参数
     *
     * @param strInterval 定位间隔
     */
    private void initOption(long strInterval) {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption.setOnceLocationLatest(false);
        /**
         *  设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
         *  只有持续定位设置定位间隔才有效，单次定位无效
         */
        locationOption.setInterval(strInterval);
    }

    class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Y.y("高德定位onLocationChanged:" + aMapLocation);
            if (null != aMapLocation) {
                double longitude = aMapLocation.getLongitude();
                double latitude = aMapLocation.getLatitude();
                String address = aMapLocation.getAddress();
                Y.y("高德定位onLocationChanged:" + address + " " + longitude + " " + latitude);
                if (longitude > 1 && latitude > 1) {
                    PreferencesHelper.putString(ConstantsME.LATITUDE_ORIGINAL, String.valueOf(latitude));
                    PreferencesHelper.putString(ConstantsME.LONGTITUDE_ORIGINAL, String.valueOf(longitude));
                    if (!TextUtils.isEmpty(address)) {
                        PreferencesHelper.putString(ConstantsME.ADDRESS, address);
                    }
                }
            }
        }

    }

    String DevIDShort =
            "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; //13 digits

    /**
     * @return
     */
    private String getIMEI(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (null == mTelephonyManager.getDeviceId() || "".equals(mTelephonyManager.getDeviceId()) ? "null" : mTelephonyManager.getDeviceId());
    }

    /**
     * 初始化全局未被捕获的异常
     */
    private void initDefaultUncaughtExceptionHandler() {
        // 设置该MyUncaughtExceptionHandler为程序的默认处理器
        MyUncaughtExceptionHandler myUncaughtExceptionHandler = new MyUncaughtExceptionHandler(
                this);
        Thread.setDefaultUncaughtExceptionHandler(myUncaughtExceptionHandler);
    }

    // 以下为管理全局activity配置

    /**
     * 从Activity列表中返回Activity对象
     */
    public Activity getActivity(Class<? extends Activity> cla) {
        for (Activity a : list) {
            if (a.getClass() == cla) {
                return a;
            }
        }
        return null;
    }

    /**
     * 从Activity列表中返回Activity对象
     */
    public Activity getActivity(Class<? extends Activity> cla, String TAG) {
        for (Activity a : list) {
            if (a.getClass() == cla) {
                Intent intent = a.getIntent();
                String response = intent.getStringExtra("msg");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String orderId = jsonObject.optString("orderId", "");
                    if (TAG.equals(orderId)) {
                        return a;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public boolean hasActivity(Class<? extends Activity> cla) {
        for (Activity a : list) {
            if (a.getClass() == cla) {
                return true;
            }
        }
        return false;
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Class<? extends Activity> cla) {
        try {
            list.remove(cla);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivityAllSameTag(Class<? extends Activity> cla) {
        try {
            for (Activity a : list) {
                if (a.getClass() == cla) {
                    list.remove(cla);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivityAllSameTag(Activity activity) {
        try {
            for (Activity a : list) {
                if (a == activity) {
                    list.remove(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity activity, String TAG) {
        try {
            for (Activity a : list) {
                if (a == activity) {
                    Intent intent = a.getIntent();
                    String response = intent.getStringExtra("msg");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String orderId = jsonObject.optString("orderId", "");
                        if (TAG.equals(orderId)) {
                            list.remove(activity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Class<? extends Activity> cla, String TAG) {
        try {
            for (Activity a : list) {
                if (a.getClass() == cla) {
                    Intent intent = a.getIntent();
                    String response = intent.getStringExtra("msg");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String orderId = jsonObject.optString("orderId", "");
                        if (TAG.equals(orderId)) {
                            list.remove(cla);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity activity) {
        try {
            list.remove(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity activity) {
        list.add(activity);
    }

    public void showActivity() {
        for (Activity a : list) {
            Y.y("share：" + a.getLocalClassName());
        }
    }

    /**
     * 关闭Activity列表中的所有Activity 退出app
     */
    public void exitApp() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    // 以上为管理全局activity配置

    /**
     * Volley
     */
    // http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
    // http client instance
    private DefaultHttpClient mHttpClient;
    private RequestQueue mRequestQueue;
    private RequestQueue mRequestQueueCookies;
    /**
     * Global request queue for Volley
     */
    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(),
                    "fast");
        }
        return mRequestQueue;
    }

    /**
     * param isUsingCookies
     *
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue(boolean isUsingCookies) {
        if (isUsingCookies) {
            // Create an instance of the Http client.
            // We need this in order to access the cookie store
            mHttpClient = new DefaultHttpClient();
            // add the cookie before adding the request to the queue
            setCookie();
            // lazy initialize the request queue, the queue instance will be
            // created when it is accessed for the first time
            if (mRequestQueueCookies == null) {
                mRequestQueueCookies = Volley.newRequestQueue(
                        getApplicationContext(), new HttpClientStack(
                                mHttpClient), "fast");
            }

            return mRequestQueueCookies;
        } else {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(getApplicationContext(),
                        "fast");
            }
            return mRequestQueue;
        }
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     * @param timeout
     */
    public <T> void addToRequestQueue(Request<T> req, int timeout) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag, int timeout) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueueWithCookies(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue(true).add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueueWithCookies(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue(true).add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     * @param timeout
     */
    public <T> void addToRequestQueueWithCookies(Request<T> req, int timeout) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        getRequestQueue(true).add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueueWithCookies(Request<T> req, String tag,
                                                 int timeout) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue(true).add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     */
    public void cancelPendingRequests(RequestQueue.RequestFilter filter) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(filter);
        }
    }

    /**
     * Method to set a cookie
     */
    public void setCookie() {
        CookieStore cs = mHttpClient.getCookieStore();
        // create a cookie
        cs.addCookie(new BasicClientCookie2("cookie", "spooky"));
    }

    // 以上为volley请求配置


//    private int result;
//    private Intent intent;
//    private MediaProjectionManager mMediaProjectionManager;

//    public int getResult() {
//        return result;
//    }
//
//    public Intent getIntent() {
//        return intent;
//    }
//
//    public MediaProjectionManager getMediaProjectionManager() {
//        return mMediaProjectionManager;
//    }
//
//    public void setResult(int result1) {
//        this.result = result1;
//    }
//
//    public void setIntent(Intent intent1) {
//        this.intent = intent1;
//    }
//
//    public void setMediaProjectionManager(MediaProjectionManager mMediaProjectionManager) {
//        this.mMediaProjectionManager = mMediaProjectionManager;
//    }
}
