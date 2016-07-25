package com.gooddream.learningdriving.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.broadcast.SMSReceiver;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.customviews.OverScrollView;
import com.my.security.Base64Coder;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.PicToastUtil;
import com.my.utils.PreferencesHelper;
import com.my.utils.RSAmethod;
import com.my.utils.RSAutils;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.PublicKey;
import java.util.HashMap;

public class ForgetPasswordActivity extends BaseActivity {
    private EditText et_phone, et_password, et_captcha;
    private TextView tv_get_captcha;
    private MyCountDownTimer myCountDownTimer;
    SMSReceiver receiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        initView();
        initData();
        myCountDownTimer = new MyCountDownTimer(60000, 1000);
        ShareApplication.share.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new SMSReceiver(et_captcha, "", "好梦学车", 4);
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        // TODO Auto-generated method stub

        String phoneTemp = RSAmethod.rsaDecrypt(this,
                PreferencesHelper.getString(ConstantsME.PHONE_TEMP));

        long last = PreferencesHelper.getLong(ConstantsME.captcha_last_clicked);
        long currentTimeMillis = System.currentTimeMillis();
        long difference = currentTimeMillis - last;
        Y.y("difference:" + difference);
        Y.y("phoneTemp:" + phoneTemp);
        int diff = (int) ((60 * 1000 - difference) / 1000);
        if (difference < 60 * 1000 && diff >= 1) {
            if (phoneTemp != null && !"".equals(phoneTemp)) {
                et_phone.setText(phoneTemp);
                et_phone.setSelection(phoneTemp.length());
            }
            new MyCountDownTimer(diff * 1000, 1000).start();

        } else {
            String phone = RSAmethod.rsaDecrypt(this,
                    PreferencesHelper.getString(ConstantsME.PHONE));
            if (!"".equals(phone)) {
                et_phone.setText(phone);
                et_phone.setSelection(phone.length());
                et_password.requestFocus();
                Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this,
                        et_phone, false);
            }
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tv_title)).setText("忘记密码");
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_captcha = (EditText) findViewById(R.id.et_captcha);
        tv_get_captcha = (TextView) findViewById(R.id.tv_get_captcha);
        findViewById(R.id.relative_total_bar).setOnTouchListener(ontouch);
        findViewById(R.id.linear_root).setOnTouchListener(ontouch);
        ((OverScrollView) findViewById(R.id.overScrollView))
                .setOverScrollTinyListener(new OverScrollView.OverScrollTinyListener() {

                    @Override
                    public void scrollLoosen() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void scrollDistance(int tinyDistance,
                                               int totalDistance) {
                        // TODO Auto-generated method stub
                        Utils.isCloseSoftInputMethod(
                                ForgetPasswordActivity.this, et_phone, true);
                    }
                });
    }

    OnTouchListener ontouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                    true);
            return false;
        }
    };

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_get_captcha:
                Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                        true);
                if (null == et_phone.getText()
                        || null == et_phone.getText().toString()
                        || "".equals(et_phone.getText().toString())
                        || !Utils.isMobileNO(et_phone.getText().toString())) {
                    PicToastUtil.showPicToast(ForgetPasswordActivity.this,
                            "请输入正确的手机号码!");
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PreferencesHelper.putLong(
                                    ConstantsME.captcha_last_clicked,
                                    System.currentTimeMillis());
                            PreferencesHelper.putString(ConstantsME.PHONE_TEMP,
                                    rsaEncrypt(et_phone.getText().toString()));
                            getCaptcha(et_phone.getText().toString());
                            myCountDownTimer.start();
                        }
                    }).start();
                }
                break;
            case R.id.tv_sure:
                Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                        true);
                if (isLegal()) {
                    stringRequestForgetPWD();
                }
                break;
            case R.id.iv_back:
                Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                        true);
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;
            case R.id.relative_back:
                Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                        true);
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;

            default:
                break;
        }
    }

    /**
     * register
     */
    private void stringRequestForgetPWD() {
        // TODO Auto-generated method stub
        CustomProgressDialogUtils.showProcessDialog(
                ForgetPasswordActivity.this, "正在重置密码。。");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("username", et_phone.getText().toString());
            jsonObjectData.put("password", et_password.getText().toString());
            jsonObjectData.put("smsCode", et_captcha.getText().toString());

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestForgetPWD:" + jsonObject);
        HashMap<String, String> stringStringHashMap = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Method.POST, Configs.BASE_SERVER_URL + Configs.resetPWD,
                stringStringHashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgressDialogUtils.dismissProcessDialog();
                Y.i("yy", "stringRequestForgetPWD:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    PreferencesHelper.putLong(
                            ConstantsME.captcha_last_clicked, 0l);
                    PicToastUtil.showPicToast(
                            ForgetPasswordActivity.this,
                            "".equals(VolleyUtilsTemp.msg) ? "密码重置成功！"
                                    : VolleyUtilsTemp.msg);
                    rsaEncrypt(et_phone.getText().toString(),
                            et_password.getText().toString());
                    finish();
                } else {
                    PicToastUtil.showPicToast(
                            ForgetPasswordActivity.this,
                            "".equals(VolleyUtils.msg) ? "密码重置失败！"
                                    : VolleyUtils.msg);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast("访问服务出错啦,呜呜!");
                CustomProgressDialogUtils.dismissProcessDialog();
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.resetPWD);
    }

    /**
     * captcha
     *
     * @param string
     */
    private void getCaptcha(String string) {
        // TODO Auto-generated method stub
        CustomProgressDialogUtils.showProcessDialog(
                ForgetPasswordActivity.this, "正在获取验证码。。");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));
            jsonObjectData.put("mobile", string);

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestApplyState:" + jsonObject);
        HashMap<String, String> stringStringHashMap = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Method.POST, Configs.BASE_SERVER_URL + Configs.getCaptchaForgetpwd,
                stringStringHashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "getCaptcha_response:" + response);
                CustomProgressDialogUtils.dismissProcessDialog();
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    PicToastUtil.showPicToast(
                            ForgetPasswordActivity.this,
                            "".equals(VolleyUtilsTemp.msg) ? "获取验证码成功！"
                                    : VolleyUtilsTemp.msg);
                } else {
                    PicToastUtil.showPicToast(
                            ForgetPasswordActivity.this,
                            "".equals(VolleyUtilsTemp.msg) ? "未能获取验证码！"
                                    : VolleyUtilsTemp.msg);
                    PreferencesHelper.putLong(
                            ConstantsME.captcha_last_clicked, 0l);
                    if (null != myCountDownTimer) {
                        myCountDownTimer.cancel();
                    }
                    tv_get_captcha.setText("获取验证码");
//							tv_get_captcha
//									.setBackgroundDrawable(getResources()
//											.getDrawable(
//													R.drawable.selector_bg_captcha_clickable));
                    tv_get_captcha.setTextColor(getResources()
                            .getColor(R.color.gooddream_blue));
                    tv_get_captcha.setClickable(true);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast("访问服务出错啦,呜呜!");
                CustomProgressDialogUtils.dismissProcessDialog();
                PreferencesHelper.putLong(
                        ConstantsME.captcha_last_clicked, 0l);
                if (null != myCountDownTimer) {
                    myCountDownTimer.cancel();
                }
                tv_get_captcha.setText("获取验证码");
//						tv_get_captcha
//								.setBackgroundDrawable(getResources()
//										.getDrawable(
//												R.drawable.selector_bg_captcha_clickable));
                tv_get_captcha.setTextColor(getResources().getColor(
                        R.color.gooddream_blue));
                tv_get_captcha.setClickable(true);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.getCaptchaForgetpwd);
    }

    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            tv_get_captcha.setText("重新获取");
//			tv_get_captcha.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.selector_bg_captcha_clickable));
            tv_get_captcha.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_get_captcha.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            tv_get_captcha.setClickable(false);
//			tv_get_captcha.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.selector_bg_captcha));
            tv_get_captcha.setTextColor(getResources().getColor(
                    R.color.gooddream_blue));
            tv_get_captcha.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    private boolean isLegal() {

        if (null == et_phone.getText() || null == et_phone.getText().toString()
                || "".equals(et_phone.getText().toString())) {
            PicToastUtil
                    .showPicToast(ForgetPasswordActivity.this, "请输入你的手机号码!");
            et_phone.requestFocus();
            Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                    false);
            return false;
        } else if (null == et_password.getText()
                || null == et_password.getText().toString()
                || "".equals(et_password.getText().toString())) {
            // Utils.showToast("请输入密码!");
            PicToastUtil.showPicToast(ForgetPasswordActivity.this, "请输入密码!");
            et_password.requestFocus();
            Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                    false);
            return false;
        } else if (et_password.getText().toString().length() < 6) {
            PicToastUtil.showPicToast(ForgetPasswordActivity.this, "密码至少6位!");
            et_password.requestFocus();
            Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                    false);
            return false;
        } else if (!Utils.isMobileNO(et_phone.getText().toString())) {
            // Utils.showToast("请输入正确的手机号码!");
            PicToastUtil.showPicToast(ForgetPasswordActivity.this,
                    "请输入正确的手机号码!");
            et_phone.requestFocus();
            Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                    false);
            return false;
        } else if (null == et_captcha.getText()
                || null == et_captcha.getText().toString()
                || "".equals(et_captcha.getText().toString())) {
            PicToastUtil.showPicToast(ForgetPasswordActivity.this, "请输入验证码!");
            et_captcha.requestFocus();
            Utils.isCloseSoftInputMethod(ForgetPasswordActivity.this, et_phone,
                    false);
            return false;
        }
        return true;
    }

    /**
     * RSA数据加密
     *
     * @param phone
     */
    private String rsaEncrypt(String phone) {
        try {
            InputStream inPublic = getResources().getAssets().open(
                    "rsa_public_key.pem");
            PublicKey publicKey = RSAutils.loadPublicKey(inPublic);
            // 加密
            byte[] encryptByte = RSAutils.encryptData(phone.getBytes(),
                    publicKey);
            return new String(Base64Coder.encode(encryptByte));
        } catch (Exception e) {
            e.printStackTrace();
            Y.y("e.::" + e.getMessage());
            return "";
        }
    }

    /**
     * RSA数据加密
     *
     * @param phone
     * @param password
     */
    private void rsaEncrypt(String phone, String password) {
        try {
            InputStream inPublic = getResources().getAssets().open(
                    "rsa_public_key.pem");
            PublicKey publicKey = RSAutils.loadPublicKey(inPublic);
            // 加密
            byte[] encryptByte = RSAutils.encryptData(phone.getBytes(),
                    publicKey);
            byte[] encryptByte2 = RSAutils.encryptData(password.getBytes(),
                    publicKey);
            Y.y("phone:" + new String(Base64Coder.encode(encryptByte)));
            Y.y("password:" + new String(Base64Coder.encode(encryptByte2)));
            PreferencesHelper.putString(ConstantsME.PHONE, new String(
                    Base64Coder.encode(encryptByte)));
            PreferencesHelper.putString(ConstantsME.PASSWORD, new String(
                    Base64Coder.encode(encryptByte2)));
        } catch (Exception e) {
            e.printStackTrace();
            Y.y("e.::" + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ShareApplication.share.removeActivity(this);
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }
}
