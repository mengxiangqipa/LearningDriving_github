package com.my.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.my.configs.ConstantsME;
import com.my.security.Base64Util;
import com.my.security.MD5;
import com.my.security.Md5Util;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

public class VolleyUtilsTemp {
    public static String msg = "";
    public static String flag = "";
    public static String id = "";
    public static int pi = 0;
    public static String token = "";
    public static JSONObject dataBodyJSONObject = null;

    public static HashMap<String, String> returnParams(
            final JSONObject jsonObject, final String inter) {
        return returnParams(jsonObject, false);
    }

    public static HashMap<String, String> returnParams(
            final JSONObject jsonObject, boolean userIdVoid) {
        dataBodyJSONObject = null;
        msg = "";
        flag = "";
        pi = 0;
        id = "";
        try {
            JSONObject jsonObjectNew = new JSONObject();
            jsonObjectNew.put("id", System.currentTimeMillis() + String.valueOf(new Random().nextDouble()).substring(3, 3 + 5));
            JSONObject jsonObjectClient = new JSONObject();
            jsonObjectClient.put("caller", "app");
            jsonObjectClient.put("os", "android");
            jsonObjectClient.put("ver", jsonObject.optString(ConstantsME.version, "1.0.0"));
            jsonObjectClient.put("sid", jsonObject.optString(ConstantsME.deviceId, "androidDeviceId"));
            jsonObjectClient.put("ex", "");
            jsonObjectNew.put("client", jsonObjectClient);
            jsonObjectNew.put("data", jsonObject.optJSONObject("data"));
            jsonObjectNew.put("page", jsonObject.optJSONObject("page"));
            final HashMap<String, String> params = new HashMap<String, String>();
//            String userId = PreferencesHelper.getString(ConstantsME.USERID);
//            String token = PreferencesHelper.getString(ConstantsME.token);
//            String currentTimeMillis = System.currentTimeMillis() + "";
            // //////////////
//            if (userIdVoid) {
//                token = "";
//                userId = "";
//            }
//            String sign = Md5Util.MD5Normal(inter + "-" + jsonObject.toString()
//                    + "-" + currentTimeMillis + "-" + userId + "-" + token);
//            params.put("inter", inter);
//            params.put("encrypt", "AES");
//            params.put("sign", sign);
//            params.put("submitTime", currentTimeMillis);
//            params.put("userId", userId);
//            params.put("dataBody",
//                    Base64Util.encode(jsonObject.toString()));
            params.put("jsonRequest", jsonObjectNew.toString());
//            Y.y("jsonRequest：：：：" + jsonObjectNew.toString());
            return params;
        } catch (Exception e) {
            Y.y("VolleyUtilsTemp:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject returnParamsJSONobject(
            final JSONObject jsonObject, boolean userIdVoid) {
        dataBodyJSONObject = null;
        msg = "";
        flag = "";
        pi = 0;
        id = "";
        try {
            JSONObject jsonObjectNew = new JSONObject();
            jsonObjectNew.put("id", System.currentTimeMillis() + String.valueOf(new Random().nextDouble()).substring(3, 3 + 5));
            JSONObject jsonObjectClient = new JSONObject();
            jsonObjectClient.put("caller", "app");
            jsonObjectClient.put("os", "android");
            jsonObjectClient.put("ver", jsonObject.optString(ConstantsME.version, "1.0.0"));
            jsonObjectClient.put("sid", jsonObject.optString(ConstantsME.deviceId, "androidDeviceId"));
            jsonObjectClient.put("ex", "");
            jsonObjectNew.put("client", jsonObjectClient);
            jsonObjectNew.put("data", jsonObject.optJSONObject("data"));
            jsonObjectNew.put("page", jsonObject.optJSONObject("page"));
            return jsonObjectNew;
        } catch (Exception e) {
            Y.y("VolleyUtilsTemp:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 默认返回 dataBody为String
     *
     * @param response
     */
    public static void parseJson(String response) {
        dataBodyJSONObject = null;
        msg = "";
        flag = "";
        pi = 0;
        id = "";
        try {
            JSONObject object = new JSONObject(response);
            id = object.optString("id", "");
            JSONObject jsonObjectState = object.optJSONObject("state");
            if (jsonObjectState != null) {
                msg = jsonObjectState.optString("msg", "");
                flag = jsonObjectState.optString("code", "");
            }
            dataBodyJSONObject = object.optJSONObject("data");
            pi = dataBodyJSONObject.optInt("nextPageIndex", 0);
//            try {
//                dataBodyJSONObject = Base64Util.decode(dataBodyJSONObject);
//                token = VolleyUtilsTemp.optString(dataBodyJSONObject, "token");
//                Y.y("VolleyUtilsTemp.parse_解密后:" + dataBodyJSONObject);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                Y.y("解密出错：" + e.getMessage());
//            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }

    public static String optString(String response, String key) {
        JSONObject object;
        try {
            object = new JSONObject(response);
            String optString = object.optString(key, "");
            return optString;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public static String optString(JSONObject jsonObject, String key) {
        return optString(jsonObject, key, "");
    }

    public static String optString(JSONObject jsonObject, String key, String defaultValue) {
        JSONObject object;
        try {
            String optString = jsonObject.optString(key, defaultValue);
            if (TextUtils.isEmpty(optString)) {
                return defaultValue;
            }
            return optString;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean optBoolean(String response, String key) {
        JSONObject object;
        try {
            object = new JSONObject(response);
            boolean bb = object.optBoolean(key, false);
            return bb;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static boolean optBoolean(JSONObject jsonObject, String key) {
        JSONObject object;
        try {
            boolean bb = jsonObject.optBoolean(key, false);
            return bb;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static int optInt(String response, String key) {
        JSONObject object;
        try {
            object = new JSONObject(response);
            int optInt = object.optInt(key, 0);
            return optInt;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public static int optInt(JSONObject jsonObject, String key) {
        JSONObject object;
        try {
            int optInt = jsonObject.optInt(key, 0);
            return optInt;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public static JSONObject optJSONObject(String response, String key) {
        JSONObject object;
        try {
            object = new JSONObject(response);
            JSONObject optString = object.optJSONObject(key);
            return optString;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject optJSONObject(JSONObject jsonObject, String key) {
        JSONObject object;
        try {
            JSONObject optString = jsonObject.optJSONObject(key);
            return optString;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray optJSONArray(String response, String key) {
        JSONObject object;
        try {
            object = new JSONObject(response);
            JSONArray optString = object.optJSONArray(key);
            return optString;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray optJSONArray(JSONObject jsonObject, String key) {
        JSONObject object;
        try {
            JSONArray optString = jsonObject.optJSONArray(key);
            return optString;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

// 以下为volley请求配置
    /**
     * Volley
     */
    // http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
    // http client instance
    private DefaultHttpClient mHttpClient;
    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;
    private RequestQueue mRequestQueueCookies;
    public static VolleyUtilsTemp instance;
    String TAG = "Volley";

    public static VolleyUtilsTemp getInstance() {
        if (instance == null) {
            synchronized (VolleyUtilsTemp.class) {
                instance = new VolleyUtilsTemp();
            }
        }
        return instance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue(Context mContext) {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext,
                    "fast");
        }
        return mRequestQueue;
    }

    /**
     * param isUsingCookies
     *
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue(Context mContext, boolean isUsingCookies) {
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
                        mContext, new HttpClientStack(
                                mHttpClient), "fast");
            }

            return mRequestQueueCookies;
        } else {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mContext,
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
    public <T> void addToRequestQueue(Context mContext, Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue(mContext).add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Context mContext, Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue(mContext).add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     * @param timeout
     */
    public <T> void addToRequestQueue(Context mContext, Request<T> req, int timeout) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        getRequestQueue(mContext).add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Context mContext, Request<T> req, String tag, int timeout) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue(mContext).add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueueWithCookies(Context mContext, Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue(mContext, true).add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueueWithCookies(Context mContext, Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue(mContext, true).add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     * @param timeout
     */
    public <T> void addToRequestQueueWithCookies(Context mContext, Request<T> req, int timeout) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        getRequestQueue(mContext, true).add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueueWithCookies(Context mContext, Request<T> req, String tag,
                                                 int timeout) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 0, 1.0f));
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue(mContext, true).add(req);
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
        org.apache.http.client.CookieStore cs = mHttpClient.getCookieStore();
        // create a cookie
        cs.addCookie(new BasicClientCookie2("cookie", "spooky"));
    }
    // 以上为volley请求配置
}
