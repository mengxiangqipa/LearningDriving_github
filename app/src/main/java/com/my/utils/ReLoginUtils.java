package com.my.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReLoginUtils {
    static ReLoginUtils newInstance;

    public static ReLoginUtils newInstance() {
        if (null == newInstance) {
            synchronized (ReLoginUtils.class) {
                if (null == newInstance) {
                    newInstance = new ReLoginUtils();
                }
            }
        }
        return newInstance;
    }

    // 登录
    public void reLogin(Context context, Fragment fragment,int requestCode) {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(context));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("username", RSAmethod.rsaDecrypt(context,
                    PreferencesHelper.getString(ConstantsME.PHONE)));
            jsonObjectData.put("password", RSAmethod.rsaDecrypt(context,
                    PreferencesHelper.getString(ConstantsME.USER_KEY)));

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        basePost(context, fragment, jsonObject,requestCode);

    }

    public void reLogin(Context context,int requestCode) {
        reLogin(context, null,requestCode);
    }
    public void reLogin(Context context,Fragment fragment) {
        reLogin(context, fragment,0);
    }
    public void reLogin(Context context) {
        reLogin(context, null,0);
    }
    private void basePost(final Context context, final Fragment fragment,
                          final JSONObject jsonObject) {
        basePost(context, fragment, jsonObject, 0);
    }

    private void basePost(final Context context, final Fragment fragment,
                          final JSONObject jsonObject, final int requestCode) {
        Y.y("重登开始:" + jsonObject.toString());
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.login,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "登录结果时间:" + System.currentTimeMillis());
                Y.i("yy", "登录结果:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONObject optJSONObject = VolleyUtilsTemp
                            .optJSONObject(VolleyUtilsTemp.dataBodyJSONObject
                                    .toString(), "userInfo");
                    String userId = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "uuid");
                    PreferencesHelper.putBoolean(ConstantsME.LOGINED,
                            true);
                    PreferencesHelper.putString(ConstantsME.USERID,
                            userId);
                    Y.y("relogin回调：loginSuccess");
                    if (null != fragment) {
                        if (fragment instanceof CallBack_ReLogin) {
                            Y.y("relogin回调：loginSuccess1111");
                            ((CallBack_ReLogin) fragment)
                                    .loginSuccess(requestCode);
                            Y.y("relogin回调：loginSuccess2222");
                        }
                    } else if (context instanceof CallBack_ReLogin) {
                        Y.y("relogin回调：loginSuccess1111");
                        ((CallBack_ReLogin) context).loginSuccess(requestCode);
                        Y.y("relogin回调：loginSuccess2222");
                    }
                } else {
                    PreferencesHelper.putBoolean(ConstantsME.LOGINED,
                            false);
                    PreferencesHelper.putString(ConstantsME.USERID,
                            "");
                    if (null != fragment) {
                        if (fragment instanceof CallBack_ReLogin) {
                            ((CallBack_ReLogin) fragment)
                                    .loginFail(requestCode);
                        }
                    } else if (context instanceof CallBack_ReLogin) {
                        ((CallBack_ReLogin) context).loginFail(requestCode);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.login);
        Y.y("重登end");
    }

    private void basePost(final Context context, final JSONObject jsonObject) {
        this.basePost(context, null, jsonObject);
    }

    public interface CallBack_ReLogin {
        public void loginSuccess(int requestCode);

        public void loginFail(int requestCode);
    }
}
