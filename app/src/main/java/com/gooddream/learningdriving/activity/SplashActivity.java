package com.gooddream.learningdriving.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.utils.ImageLoaderUtils_nostra13;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends BaseFullscreenMobclickAgentActivity {

    com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    private ImageView iv_splash;

    @Override
    public String getPageName() {
        // TODO Auto-generated method stub
        return "闪屏页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        PreferencesHelper.putBoolean("shouldResume", true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageLoader = ImageLoaderUtils_nostra13.initImageLoader(this);
        setContentView(R.layout.splash);
        initView();
//        String imgUrl = PreferencesHelper.getString(ConstantsME.imgUrl);
//        if (!"".equals(imgUrl)) {
//            try {
//                imageLoader.displayImage(imgUrl, iv_splash,
//                        ImageLoaderUtils_nostra13
//                                .getFadeOptions(
//                                        R.drawable.splash,
//                                        R.drawable.splash,
//                                        R.drawable.splash));
//            } catch (Exception e) {
//
//            }
//        }
        initData();
        ShareApplication.share.addActivity(this);
    }

    private void initData() {
        // TODO Auto-generated method stub
        imageLoader = ImageLoaderUtils_nostra13.initImageLoader(this);
        initMobclickAgent();
        initDeviceToken();
        if (PreferencesHelper.getBoolean(ConstantsME.LOGINED)
                && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
//            stringRequestSystemMsg();
        }
        stringRequestCity();
        stringRequestVehicleType();
        GetConstantUtil.newInstance().getContant(this);
//        stringRequestVersion();
//        stringRequestSplash();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(SplashActivity.this,
                            HomepageActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                    ShareApplication.share.removeActivity(SplashActivity.this);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        // TODO Auto-generated method stub
        iv_splash = (ImageView) findViewById(R.id.iv_splash);
    }

    /**
     * 初始化友盟统计
     */
    private void initMobclickAgent() {
        // TODO Auto-generated method stub
//        MobclickAgent.setDebugMode(true);
//        // 在仅有Activity的程序中，SDK 自动帮助开发者调用了 2. 中的方法，并把Activity
//        // 类名作为页面名称统计。但是在包含fragment的程序中我们希望统计更详细的页面，所以需要自己调用方法做更详细的统计。首先，需要在程序入口处，调用
//        // MobclickAgent.openActivityDurationTrack(false)
//        // 禁止默认的页面统计方式，这样将不会再自动统计Activity。
//        MobclickAgent.openActivityDurationTrack(false);
//        // 发送策略定义了用户由统计分析SDK产生的数据发送回友盟服务器的频率。
//        MobclickAgent.updateOnlineConfig(this);
//        /** 设置是否对日志信息进行加密, 默认false(不加密). */
//        AnalyticsConfig.enableEncrypt(true);
    }

    private void initDeviceToken() {
        // TODO Auto-generated method stub
        PreferencesHelper.putString("deviceToken", "");
        Y.y("initDeviceToken_结束存入device_token:");
    }

    private void stringRequestCity() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject params = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("stringRequestCity:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.cityList + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("stringRequestCity:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray jsonArray = VolleyUtilsTemp
                            .optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "list");
                    if (null != jsonArray && jsonArray.length() > 1) {
                        PreferencesHelper.putString(ConstantsME.cityList, jsonArray.toString());
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Y.y("stringRequestCity:" + error);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.cityList);
    }

    private void stringRequestVehicleType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject params = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("stringRequestVehicleType:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.vehicleTypeList + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("stringRequestVehicleType:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray jsonArray = VolleyUtilsTemp
                            .optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "list");
                    if (null != jsonArray && jsonArray.length() > 1) {
                        PreferencesHelper.putString(ConstantsME.vehicleTypeList, jsonArray.toString());
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Y.y("stringRequestCity:" + error);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.vehicleTypeList);
    }
//    private void stringRequestSplash() {
//        // TODO Auto-generated method stub
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("userType", "50");
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        HashMap<String, String> params = VolleyUtils.returnParams(jsonObject,
//                Configs.startup, true);
//        StringRequestSession stringRequest = new StringRequestSession(Method.POST, Configs.BASE_SERVER_URL,
//                params, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Y.y("stringRequestSplash:" + response);
//                CustomProgressDialogUtils.dismissProcessDialog();
//                VolleyUtils.parseJson(response);
//                if (Configs.SUCCESS.equals(VolleyUtils.flag)) {
//                    Y.y("闪屏页：" + "--0");
//                    JSONArray array = VolleyUtils.optJSONArray(VolleyUtils.dataBodyString, "vipList");
//                    if (array != null && array.length() > 0) {
//                        Y.y("闪屏页：" + "--1");
//                        int le = array.length();
//                        for (int i = 0; i < le; i++) {
//                            JSONObject jsonObject1 = array.optJSONObject(i);
//                            int vipType = jsonObject1.optInt("vipType", 0);
//                            int discount = jsonObject1.optInt("discount", 0);
//                            int vipPrice = jsonObject1.optInt("vipPrice", 0);
//                            int pricePre2 = vipPrice * discount / 100;
//                            if (53 == vipType) {
//                                PreferencesHelper.putString("vip6", pricePre2 + "");
//                                PreferencesHelper.putString("vip6_2", vipPrice + "");
//                            } else if (52 == vipType) {
//                                PreferencesHelper.putString("vip5", pricePre2 + "");
//                                PreferencesHelper.putString("vip5_2", vipPrice + "");
//                            } else if (50 == vipType) {
//                                PreferencesHelper.putString("vip4", pricePre2 + "");
//                                PreferencesHelper.putString("vip42", vipPrice + "");
//                            } else if (55 == vipType) {
//                                PreferencesHelper.putString("vip3", pricePre2 + "");
//                                PreferencesHelper.putString("vip3_2", vipPrice + "");
//                            } else if (54 == vipType) {
//                                PreferencesHelper.putString("vip2", pricePre2 + "");
//                                PreferencesHelper.putString("vip2_2", vipPrice + "");
//                            } else if (51 == vipType) {
//                                PreferencesHelper.putString("vip1", pricePre2 + "");
//                                PreferencesHelper.putString("vip1_2", vipPrice + "");
//                            }
//                        }
//                    }
//                    Y.y("闪屏页：" + "0");
//                    String imgUrl = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "bootPage");
//                    String qqGroup = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "qqGroup");
//                    String tutorial = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "tutorial");
//                    String tutorial1 = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "tutorialNormal");
//                    String tutorial2 = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "tutorialRedpkt");
//                    String tutorial3 = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "tutorialHide");
//                    PreferencesHelper.putString("tutorial1",
//                            tutorial1);
//                    PreferencesHelper.putString("tutorial2",
//                            tutorial2);
//                    PreferencesHelper.putString("tutorial3",
//                            tutorial3);
//                    String shareYY = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "sharePage");
//                    PreferencesHelper.putString("qqGroup",
//                            qqGroup);
//                    Y.y("闪屏页：" + "1");
//                    String protocol = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "agreement");
//                    Y.y("闪屏页：" + "2");
//                    String about = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "about");
//                    Y.y("闪屏页：" + "3");
//                    String usual = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "faq");
//                    String introduction = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "charges");
//                    String hotline = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "hotline");
//                    Y.y("闪屏页：" + "5");
//                    PreferencesHelper.putString(ConstantsME.imgUrl,
//                            imgUrl);
//                    Y.y("闪屏页：" + "6");
//                    PreferencesHelper.putString("shareYY",
//                            shareYY);
//                    Y.y("闪屏页：" + "7");
//                    PreferencesHelper.putString("tutorial",
//                            tutorial);
//                    Y.y("闪屏页：" + "8");
//                    PreferencesHelper.putString("protocol",
//                            protocol);
//                    PreferencesHelper.putString("about",
//                            about);
//                    PreferencesHelper.putString("introduction",
//                            introduction);
//                    PreferencesHelper.putString("usual",
//                            usual);
//                    PreferencesHelper.putString(ConstantsME.servicePhone, hotline);
////                    Intent home = new Intent(SplashActivity.this,
////                            HomePageActivity.class);
////                    startActivity(home);
////                    overridePendingTransition(R.anim.slide_right_in,
////                            R.anim.slide_left_out);
////                    finish();
////                    ShareApplication.share.removeActivity(SplashActivity.this);
//                } else {
//                    String imgUrl = PreferencesHelper
//                            .getString(ConstantsME.imgUrl);
//                    if (!"".equals(imgUrl)) {
//                        try {
//                            imageLoader.displayImage(imgUrl, iv_splash,
//                                    ImageLoaderUtils_nostra13
//                                            .getFadeOptions(
//                                                    R.drawable.splash,
//                                                    R.drawable.splash,
//                                                    R.drawable.splash));
//                        } catch (Exception e) {
//
//                        }
//                    }
//                    if (VolleyUtils.flag.equals("2")) {
//                        ReLoginUtils.newInstance().reLogin(
//                                SplashActivity.this);
//                    }
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                String imgUrl = PreferencesHelper
//                        .getString(ConstantsME.imgUrl);
//                if (!"".equals(imgUrl)) {
//                    try {
//                        imageLoader.displayImage(imgUrl, iv_splash,
//                                ImageLoaderUtils_nostra13
//                                        .getFadeOptions(
//                                                R.drawable.splash,
//                                                R.drawable.splash,
//                                                R.drawable.splash));
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//        });
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000, 0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//        ShareApplication.volleyUtils.addToRequestQueue(this, stringRequest,
//                Configs.startup);
//    }

//    private void stringRequestVersion() {
//        // TODO Auto-generated method stub
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("type", "50");
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        HashMap<String, String> params = VolleyUtils.returnParams(jsonObject,
//                Configs.version, true);
//        StringRequestSession stringRequest = new StringRequestSession(Method.POST, Configs.BASE_SERVER_URL,
//                params, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Y.i("yy", "stringRequestVersion:" + response);
//                CustomProgressDialogUtils.dismissProcessDialog();
//                VolleyUtils.parseJson(response);
//                if (Configs.SUCCESS.equals(VolleyUtils.flag)) {
//                    String appVersion = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "androidVersion");
//                    String versionData = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "updateMsg");
//                    String androidCo = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "androidCode");
//                    String isAndroidHide = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "isAndroidHide");
//                    String apploadUrl = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "downloadUrl");
//                    int androidCode;
//                    try {
//                        androidCode = Integer.parseInt(androidCo);
//                    } catch (NumberFormatException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        androidCode = 1;
//                    }
//                    PreferencesHelper
//                            .putInt("androidCode", androidCode);
//                    PreferencesHelper.putString("versionData",
//                            versionData);
//                    PreferencesHelper.putString(ConstantsME.appVersion,
//                            appVersion);
//                    PreferencesHelper.putString(
//                            ConstantsME.isAndroidHide, isAndroidHide);
//                    PreferencesHelper.putString(
//                            ConstantsME.downloadUrl, apploadUrl);
//                    Y.y("appVersion:" + appVersion);
//                    Y.y("appVersion:"
//                            + PreferencesHelper
//                            .getString(ConstantsME.appVersion));
//                } else {
//
//                    if (VolleyUtils.flag.equals("2")) {
//                        ReLoginUtils.newInstance().reLogin(
//                                SplashActivity.this);
//                    }
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        });
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000, 0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//        ShareApplication.volleyUtils.addToRequestQueue(this, stringRequest,
//                Configs.version);
//    }

//    private void stringRequestSystemMsg() {
//        // TODO Auto-generated method stub
//        JSONObject jsonObject = new JSONObject();
//        // try {
//        // jsonObject.put("phone", et_phone.getText().toString());
//        // jsonObject.put("password", et_password.getText().toString());
//        // } catch (JSONException e) {
//        // // TODO Auto-generated catch block
//        // e.printStackTrace();
//        // }
//        HashMap<String, String> params = VolleyUtils.returnParams(jsonObject,
//                Configs.systemMsg);
//        StringRequestSession stringRequest = new StringRequestSession(Method.POST, Configs.BASE_SERVER_URL,
//                params, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Y.i("yy", "stringRequestSystemMsg:" + response);
//                CustomProgressDialogUtils.dismissProcessDialog();
//                VolleyUtils.parseJson(response);
//                if (Configs.SUCCESS.equals(VolleyUtils.flag)) {
//                    String systemMsg = VolleyUtils.optString(
//                            VolleyUtils.dataBodyString.toString(),
//                            "systemMsgs");
//                    if (null != systemMsg
//                            && !systemMsg.equals(PreferencesHelper
//                            .getString(ConstantsME.systemMsg))) {
//                        PreferencesHelper.putBoolean(
//                                ConstantsME.hasChange, true);
//                    }
//                    PreferencesHelper.putString(ConstantsME.systemMsg,
//                            systemMsg);
//                } else {
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        });
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000, 0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//        ShareApplication.volleyUtils.addToRequestQueue(this, stringRequest,
//                Configs.systemMsg);
//    }
}
