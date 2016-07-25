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
import com.my.utils.Utils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPoptipsUtil {
    static GetPoptipsUtil newInstance;

    public static GetPoptipsUtil newInstance() {
        if (null == newInstance) {
            synchronized (GetPoptipsUtil.class) {
                if (null == newInstance) {
                    newInstance = new GetPoptipsUtil();
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
        StringRequest stringRequest = new StringRequest(Method.GET, Configs.BASE_SERVER_URL + Configs.tips + "?jsonRequest=" + params.toString(),
                null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "getContant:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONObject readTips = VolleyUtilsTemp
                            .optJSONObject(VolleyUtilsTemp.dataBodyJSONObject
                                    .toString(), "readTips");
                    JSONObject operateTips = VolleyUtilsTemp
                            .optJSONObject(VolleyUtilsTemp.dataBodyJSONObject
                                    .toString(), "operateTips");
                    if (null != readTips) {
                        PreferencesHelper.putString(ConstantsME.constantTipsRead, readTips.toString());
                    }

                    if (operateTips != null) {
                        PreferencesHelper.putString(ConstantsME.constantTipsOperate,operateTips.toString());
                    }
                    if (null != fragment) {
                        if (fragment instanceof CallBack_Tips) {
                            ((CallBack_Tips) fragment)
                                    .getSuccess();
                        }
                    } else if (context instanceof CallBack_Tips) {
                        ((CallBack_Tips) context).getSuccess();
                    }
                } else {
                    if (null != fragment) {
                        if (fragment instanceof CallBack_Tips) {
                            ((CallBack_Tips) fragment)
                                    .getFail();
                        }
                    } else if (context instanceof CallBack_Tips) {
                        ((CallBack_Tips) context).getFail();
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
                Configs.tips);
    }

    private void baseGet(final Context context, final JSONObject jsonObject) {
        this.baseGet(context, null, jsonObject);
    }

    public interface CallBack_Tips {
        void getSuccess();

        void getFail();
    }
}
