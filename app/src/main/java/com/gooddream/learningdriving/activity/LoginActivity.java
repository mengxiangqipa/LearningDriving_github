package com.gooddream.learningdriving.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.broadcast.FinishBroadCastReceiver;
import com.my.configs.ConstantsME;
import com.my.customviews.OverScrollView;
import com.my.utils.PreferencesHelper;
import com.my.utils.RSAmethod;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.Y;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText et_phone, et_password;
    private TextView tv_register, tv_forget_password;
    private FinishBroadCastReceiver receiver;
    private int tryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
//        initView();
//        initData();
//        ShareApplication.share.addActivity(this);
//        receiver = new FinishBroadCastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.tongfu.delivery_driver.activity.LoginActivity");
//        registerReceiver(receiver, filter);
    }

//    private void initData() {
//        // TODO Auto-generated method stub
//        ((OverScrollView) findViewById(R.id.overScrollView))
//                .setOverScrollTinyListener(new OverScrollView.OverScrollTinyListener() {
//
//                    @Override
//                    public void scrollLoosen() {
//                        // TODO Auto-generated method stub
//
//                    }
//
//                    @Override
//                    public void scrollDistance(int tinyDistance,
//                                               int totalDistance) {
//                        // TODO Auto-generated method stub
//                        Utils.isCloseSoftInputMethod(getContext(),
//                                et_phone, true);
//                    }
//                });
//        String phone = RSAmethod.rsaDecrypt(this,
//                PreferencesHelper.getString(ConstantsME.PHONE));
//        if (!"".equals(phone)) {
//            et_phone.setText(phone);
//            et_phone.setSelection(phone.length());
//            et_password.requestFocus();
//            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
//        }
//    }
//
//    private void initView() {
//        // TODO Auto-generated method stub
//        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
//        tv_forget_password.getPaint().setAntiAlias(true);
//        tv_forget_password.getPaint().setUnderlineText(true);
//        et_phone = (EditText) findViewById(R.id.et_phone);
//        et_password = (EditText) findViewById(R.id.et_password);
//        ((TextView) findViewById(R.id.tv_title)).setText("用户登录");
//        tv_register = ((TextView) findViewById(R.id.tv_register));
//        findViewById(R.id.relative_total_bar).setOnTouchListener(
//                new OnTouchListener() {
//
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        // TODO Auto-generated method stub
//                        Utils.isCloseSoftInputMethod(getContext(),
//                                et_phone, true);
//                        return false;
//                    }
//                });
//    }
//
//    private void initDeviceToken() {
//        // TODO Auto-generated method stub
////        String device_token = UmengRegistrar.getRegistrationId(this);
////        Y.y("initDeviceToken_device_token:" + device_token);
////        Y.y("initDeviceToken_开始存入device_token:");
////        PreferencesHelper.putString("deviceToken", device_token);
////        Y.y("initDeviceToken_结束存入device_token:");
//    }
//
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        switch (v.getId()) {
//            case R.id.tv_register:
//                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
//                Intent intentRegister = new Intent(getContext(),
//                        RegisterActivity.class);
//                intentRegister.putExtra("phone",
//                        (null == et_phone.getText() || null == et_phone.getText()
//                                .toString()) ? "" : et_phone.getText().toString());
//                startActivity(intentRegister);
//                overridePendingTransition(R.anim.slide_right_in,
//                        R.anim.slide_left_out);
//                break;
//            case R.id.tv_forget_password:
//                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
//                Intent intentForget = new Intent(getContext(),
//                        ForgetPasswordActivity.class);
//                startActivity(intentForget);
//                overridePendingTransition(R.anim.slide_right_in,
//                        R.anim.slide_left_out);
//                break;
//            case R.id.tv_sure:
//                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
//                if (isLegal()) {
//                    stringRequestLogin();
//                }
//                break;
//            case R.id.iv_back:
//                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
//                finish();
//                overridePendingTransition(R.anim.slide_left_in,
//                        R.anim.slide_right_out);
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * login
//     */
//    private void stringRequestLogin() {
//        // TODO Auto-generated method stub
//        CustomProgressDialogUtils.showProcessDialog(getContext(),
//                "正在登录。。");
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("phone", et_phone.getText().toString());
//            jsonObject.put("password", et_password.getText().toString());
//            jsonObject.put("deviceToken",
//                    PreferencesHelper.getString("deviceToken"));
//            jsonObject.put("userType", "51");
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        HashMap<String, String> params = VolleyUtils.returnParams(jsonObject,
//                Configs.login, true);
//        StringRequestSession stringRequest = new StringRequestSession(Method.POST, Configs.BASE_SERVER_URL,
//                params, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Y.i("yy", "stringRequestLogin:" + response);
//                CustomProgressDialogUtils.dismissProcessDialog();
//                VolleyUtils.parseJson(response);
//                if (Configs.SUCCESS.equals(VolleyUtils.flag)) {
//                    String token = VolleyUtils.optString(
//                            VolleyUtils.dataBodyJSONObject.toString(),
//                            "token");
//                    JSONObject optJSONObject = VolleyUtils
//                            .optJSONObject(VolleyUtils.dataBodyJSONObject
//                                    .toString(), "userInfo");
//                    if (null != optJSONObject) {
//                        String userId = optJSONObject.optString("id", "");
//                        String uid = optJSONObject.optString("uid", "");
//                        String totalPurchase = optJSONObject.optString(
//                                "totalPurchase", "");
//                        String totalEarnings = optJSONObject.optString(
//                                "totalEarnings", "");
//                        String yesterdayEarnings = optJSONObject.optString(
//                                "yesterdayEarnings", "");
//                        PicToastUtil.showPicToast(getContext(), ""
//                                .equals(VolleyUtils.msg) ? "登录成功！"
//                                : VolleyUtils.msg);
//                        PreferencesHelper.putBoolean(ConstantsME.LOGINED,
//                                true);
//                        PreferencesHelper.putString(ConstantsME.PHONE,
//                                RSAmethod.rsaEncrypt(getContext(),
//                                        et_phone.getText().toString()));
//                        PreferencesHelper.putString(ConstantsME.USER_KEY,
//                                RSAmethod.rsaEncrypt(getContext(),
//                                        et_password.getText().toString()));
//                        PreferencesHelper.putString(ConstantsME.USERID,
//                                userId);
//                        PreferencesHelper.putString(ConstantsME.uid,
//                                uid);
//                        PreferencesHelper.putString(ConstantsME.token,
//                                token);
//                        if (ShareApplication.share
//                                .hasActivity(HomePageActivity.class)) {
////                            ShareApplication.share
////                                    .removeActivity(HomePageActivity.class);
//                            Intent intentBroadcast = new Intent();
//                            intentBroadcast
//                                    .setAction("com.tong.delivery_driver.HomepageActivity");
//                            sendBroadcast(intentBroadcast);
//                        }
////                        Intent home = new Intent(getContext(),
////                                HomePageActivity.class);
////                        startActivity(home);
//                        overridePendingTransition(R.anim.slide_right_in,
//                                R.anim.slide_left_out);
//                        finish();
//                    }
//                } else if (!"".equals(VolleyUtils.msg)
//                        && VolleyUtils.msg.contains("device")
//                        && tryCount <= 2) {
//                    tryCount++;
//                    initDeviceToken();
//                    stringRequestLogin();
//                } else {
//                    PicToastUtil.showPicToast(getContext(), ""
//                            .equals(VolleyUtils.msg) ? "登录失败！"
//                            : VolleyUtils.msg);
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Utils.showToast("访问服务出错啦,呜呜!");
//                CustomProgressDialogUtils.dismissProcessDialog();
//            }
//        });
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//        ShareApplication.getInstance().addToRequestQueue(stringRequest,
//                Configs.login);
//    }
//
//    private boolean isLegal() {
//
//        if (null == et_phone.getText() || null == et_phone.getText().toString()
//                || "".equals(et_phone.getText().toString())) {
//            PicToastUtil.showPicToast(getContext(), "请输入你的手机号码!");
//            et_phone.requestFocus();
//            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
//            return false;
//        } else if (null == et_password.getText()
//                || null == et_password.getText().toString()
//                || "".equals(et_password.getText().toString())) {
//            // Utils.showToast("请输入密码!");
//            PicToastUtil.showPicToast(getContext(), "请输入密码!");
//            et_password.requestFocus();
//            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
//            return false;
//        } else if (!Utils.isMobileNO(et_phone.getText().toString())) {
//            // Utils.showToast("请输入正确的手机号码!");
//            PicToastUtil.showPicToast(getContext(), "请输入正确的手机号码!");
//            et_phone.requestFocus();
//            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//        try {
//            unregisterReceiver(receiver);
//            ShareApplication.share.removeActivity(this);
//            Y.y("被广播杀死了");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Y.y("被广播杀死了:" + e.getMessage());
//        }
//    }

//    @Override
//    public String getPageName() {
//        // TODO Auto-generated method stub
//        return "LoginActivity";
//    }
}
