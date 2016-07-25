package com.gooddream.learningdriving.other;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.utils.PreferencesHelper;
import com.my.utils.RSAmethod;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetConstantUtil {
    static GetConstantUtil newInstance;

    public static GetConstantUtil newInstance() {
        if (null == newInstance) {
            synchronized (GetConstantUtil.class) {
                if (null == newInstance) {
                    newInstance = new GetConstantUtil();
                }
            }
        }
        return newInstance;
    }

    // 登录
    public void getContant(Context context, Fragment fragment) {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(context));
            JSONObject jsonObjectData = new JSONObject();

//            jsonObjectData.put("username", RSAmethod.rsaDecrypt(context,
//                    PreferencesHelper.getString(ConstantsME.PHONE)));
//            jsonObjectData.put("password", RSAmethod.rsaDecrypt(context,
//                    PreferencesHelper.getString(ConstantsME.USER_KEY)));

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        baseGet(context, fragment, jsonObject);

    }

    public void getContant(Context context) {
        getContant(context, null);
    }

    private void baseGet(final Context context, final Fragment fragment,
                         final JSONObject jsonObject) {
        Y.y("getContant:" + jsonObject.toString());
//        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        JSONObject params = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Method.GET, Configs.BASE_SERVER_URL + Configs.getConstant + "?jsonRequest=" + params.toString(),
                null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "getContant:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONObject complainData = VolleyUtilsTemp
                            .optJSONObject(VolleyUtilsTemp.dataBodyJSONObject
                                    .toString(), "complainData");
                    JSONObject studyData = VolleyUtilsTemp
                            .optJSONObject(VolleyUtilsTemp.dataBodyJSONObject
                                    .toString(), "studyData");
                    JSONObject agreement = VolleyUtilsTemp
                            .optJSONObject(VolleyUtilsTemp.dataBodyJSONObject
                                    .toString(), "agreement");
                    if (null != studyData) {
                        JSONArray jsonArray1 = VolleyUtilsTemp.optJSONArray(studyData, "k1");
                        if (jsonArray1 != null && jsonArray1.length() > 0) {
                            int length = jsonArray1.length();
                            for (int i = 0; i < length; i++) {
                                String s = jsonArray1.optString(i, "");
                                PreferencesHelper.putString("constant2_1_" + (i + 1), s);
                            }
                        }
                        JSONArray jsonArray4 = VolleyUtilsTemp.optJSONArray(studyData, "k4");
                        if (jsonArray1 != null && jsonArray1.length() > 0) {
                            int length = jsonArray1.length();
                            for (int i = 0; i < length; i++) {
                                String s = jsonArray1.optString(i, "");
                                PreferencesHelper.putString("constant2_4_" + (i + 1), s);
                            }
                        }
                        PreferencesHelper.putString(ConstantsME.constantUrlList, VolleyUtilsTemp.optJSONArray(studyData, "k2").toString());
                        PreferencesHelper.putString(ConstantsME.constantUrlListk3, VolleyUtilsTemp.optJSONArray(studyData, "k3").toString());
                        PreferencesHelper.putString(ConstantsME.constantUrlListk4, VolleyUtilsTemp.optJSONArray(studyData, "k4").toString());
                    }

                    if (complainData != null) {
                        PreferencesHelper.putString(ConstantsME.servicePhone, VolleyUtilsTemp.optString(complainData, "tel"));
                        PreferencesHelper.putString(ConstantsME.constantConsult, VolleyUtilsTemp.optString(complainData, "onLineServeUrl"));
                    }
                    if (agreement != null) {
                        PreferencesHelper.putString(ConstantsME.registerProtocol, VolleyUtilsTemp.optString(agreement, "studentRegisterUrl"));
                    }
                    Y.y("relogin回调：loginSuccess");
                    if (null != fragment) {
                        if (fragment instanceof CallBack_GetConstant) {
                            Y.y("relogin回调：loginSuccess1111");
                            ((CallBack_GetConstant) fragment)
                                    .getSuccess();
                            Y.y("relogin回调：loginSuccess2222");
                        }
                    } else if (context instanceof CallBack_GetConstant) {
                        Y.y("relogin回调：loginSuccess1111");
                        ((CallBack_GetConstant) context).getSuccess();
                        Y.y("relogin回调：loginSuccess2222");
                    }
                } else {
                    if (null != fragment) {
                        if (fragment instanceof CallBack_GetConstant) {
                            ((CallBack_GetConstant) fragment)
                                    .getFail();
                        }
                    } else if (context instanceof CallBack_GetConstant) {
                        ((CallBack_GetConstant) context).getFail();
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
                Configs.getConstant);
    }

    private void baseGet(final Context context, final JSONObject jsonObject) {
        this.baseGet(context, null, jsonObject);
    }

    public interface CallBack_GetConstant {
        void getSuccess();

        void getFail();
    }
}
