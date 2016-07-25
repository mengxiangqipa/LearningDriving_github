package com.my.pulltorefresh.fragment_part;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.HomepageActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.gooddream.learningdriving.webview.WebViewActivity;
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
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.PublicKey;
import java.util.HashMap;

public class Fragment_register extends Fragment implements View.OnClickListener, GetConstantUtil.CallBack_GetConstant {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////
    OverScrollView overScrollView;
    private EditText et_phone, et_captcha, et_password;
    private TextView tv_get_captcha, tv_sure;
    private TextView tv_protocol;
    CheckBox checkbox;
    ///////////////////////////////
    private MyCountDownTimer myCountDownTimer;
    SMSReceiver receiver;
    IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.fragment_register, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            myContainer.addView(view);
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
        initView();
        initEvent();
        myCountDownTimer = new MyCountDownTimer(60000, 1000);
        initData();
        return myContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new SMSReceiver(et_captcha, "", "好梦学车", 4);
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mContext.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        // TODO Auto-generated method stub

        String phoneTemp = RSAmethod.rsaDecrypt(getContext(),
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
            String phone = RSAmethod.rsaDecrypt(getContext(),
                    PreferencesHelper.getString(ConstantsME.PHONE));
            if (!"".equals(phone)) {
                et_phone.setText(phone);
                et_phone.setSelection(phone.length());
                et_password.requestFocus();
                Utils.isCloseSoftInputMethod(getContext(),
                        et_phone, true);
            }
        }
    }

    private void initEvent() {
        overScrollView.setOverScrollTinyListener(new OverScrollView.OverScrollTinyListener() {
            @Override
            public void scrollDistance(int tinyDistance, int totalDistance) {
                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
            }

            @Override
            public void scrollLoosen() {

            }
        });
    }

    private void initView() {
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        et_captcha = (EditText) view.findViewById(R.id.et_captcha);
        et_password = (EditText) view.findViewById(R.id.et_password);
        tv_get_captcha = (TextView) view.findViewById(R.id.tv_captcha);
        tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        overScrollView = (OverScrollView) view.findViewById(R.id.overScrollView);
        tv_protocol = (TextView) view.findViewById(R.id.tv_protocol);
        checkbox = (CheckBox) view.findViewById(R.id.checkbox);

        tv_get_captcha.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
        SpannableString spannableString = new SpannableString(getString(R.string.protocol));
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gooddream_blue)), 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_protocol.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_protocol:
                if ("".equals(PreferencesHelper.getString(ConstantsME.registerProtocol))) {
                    GetConstantUtil.newInstance().getContant(mContext, this);
                } else {
                    Intent intentWebview = new Intent(getContext(),
                            WebViewActivity.class);
                    intentWebview.putExtra("title", "好梦学车用户协议");
                    intentWebview.putExtra("url", PreferencesHelper.getString(ConstantsME.registerProtocol));
                    startActivity(intentWebview);
                    getActivity().overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                }
                break;
            case R.id.tv_captcha:
                Utils.isCloseSoftInputMethod(getContext(), et_phone,
                        true);
                if (null == et_phone.getText()
                        || null == et_phone.getText().toString()
                        || "".equals(et_phone.getText().toString())
                        || !Utils.isMobileNO(et_phone.getText().toString())) {
                    PicToastUtil.showPicToast(getContext(),
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
                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
//                Utils.showToast(SMSread());
                if (isLegal()) {
                    stringRequestRegister();
                }
                break;
            default:
                break;
        }
    }

    /**
     * captcha
     *
     * @param string
     */
    private void getCaptcha(String string) {
        // TODO Auto-generated method stub
        CustomProgressDialogUtils.showProcessDialog(
                getContext(), "正在获取验证码。。");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();
            jsonObjectData.put("mobile", et_phone.getText().toString());
            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.captcha_register,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "getCaptcha_response:" + response);
                CustomProgressDialogUtils.dismissProcessDialog();
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    PicToastUtil.showPicToast(
                            getContext(),
                            "".equals(VolleyUtilsTemp.msg)||"success".equals(VolleyUtilsTemp.msg) ? "获取验证码成功！"
                                    : VolleyUtilsTemp.msg);
                } else {
                    PicToastUtil.showPicToast(
                            getContext(),
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
                            .getColor(R.color.white));
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
                        R.color.white));
                tv_get_captcha.setClickable(true);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.captcha_register);
    }

    /**
     * register
     */
    private void stringRequestRegister() {
        // TODO Auto-generated method stub
        CustomProgressDialogUtils.showProcessDialog(getContext(),
                "正在注册。。");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
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

        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.register,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgressDialogUtils.dismissProcessDialog();
                Y.i("yy", "stringRequestRegister:" + response);
                VolleyUtilsTemp.parseJson(response);
                Y.i("yy", "stringRequestRegister:" + VolleyUtilsTemp.dataBodyJSONObject);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    PreferencesHelper.putLong(
                            ConstantsME.captcha_last_clicked, 0l);
                    PicToastUtil.showPicToast(getContext(), ""
                            .equals(VolleyUtilsTemp.msg)||"success".equals(VolleyUtilsTemp.msg)  ? "注册成功！"
                            : VolleyUtilsTemp.msg);
                    rsaEncrypt(et_phone.getText().toString(),
                            et_password.getText().toString());
                    stringRequestLogin();
                } else {
                    PicToastUtil.showPicToast(getContext(), ""
                            .equals(VolleyUtilsTemp.msg) ? "注册失败！"
                            : VolleyUtilsTemp.msg);
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
                Configs.register);
    }

    /**
     * login
     */
    private void stringRequestLogin() {
        // TODO Auto-generated method stub
        CustomProgressDialogUtils.showProcessDialog(getContext(),
                "正在自动登录。。");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("username", et_phone.getText().toString());
            jsonObjectData.put("password", et_password.getText().toString());

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.login,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestLogin:" + response);
                VolleyUtilsTemp.parseJson(response);
                CustomProgressDialogUtils.dismissProcessDialog();

                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    PreferencesHelper.putBoolean(ConstantsME.LOGINED, true);
                    PreferencesHelper.putString(ConstantsME.PHONE,
                            RSAmethod.rsaEncrypt(getContext(),
                                    et_phone.getText().toString()));
                    PreferencesHelper.putString(ConstantsME.USER_KEY,
                            RSAmethod.rsaEncrypt(getContext(),
                                    et_password.getText().toString()));
                    PicToastUtil.showPicToast(getContext(), ""
                            .equals(VolleyUtilsTemp.msg) ? "登录成功！"
                            : VolleyUtilsTemp.msg);
                    String token = VolleyUtilsTemp.optString(
                            VolleyUtilsTemp.dataBodyJSONObject.toString(),
                            "token");
                    JSONObject optJSONObject = VolleyUtilsTemp
                            .optJSONObject(VolleyUtilsTemp.dataBodyJSONObject
                                    .toString(), "userInfo");
                    String userId = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "uuid");
                    if (!TextUtils.isEmpty(userId)) {
                        PreferencesHelper.putBoolean(ConstantsME.LOGINED,
                                true);
                        PreferencesHelper.putString(ConstantsME.USERID,
                                userId);
                    }
                    if (null != optJSONObject) {
                        String totalPurchase = optJSONObject.optString(
                                "totalPurchase", "");
                        String totalEarnings = optJSONObject.optString(
                                "totalEarnings", "");
                        String yesterdayEarnings = optJSONObject.optString(
                                "yesterdayEarnings", "");
                        PreferencesHelper.putString(ConstantsME.token,
                                token);
                    }
                    if (getActivity().getIntent().getBooleanExtra(ConstantsME.fromDialog, false)) {
                        Intent intent = new Intent();
                        intent.putExtra(ConstantsME.fromDialog, true);
                        intent.setAction(ConstantsME.goodDream_broadcast_loginsuccess);
                        mContext.sendBroadcast(intent);
                        getActivity().setResult(getActivity().RESULT_OK, getActivity().getIntent());
                        getActivity().finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(ConstantsME.fromDialog, false);
                        intent.setAction(ConstantsME.goodDream_broadcast_loginsuccess);
                        mContext.sendBroadcast(intent);
                    }
//                    if (ShareApplication.share
//                            .hasActivity(HomepageActivity.class)) {
//                        ShareApplication.share
//                                .removeActivity(HomepageActivity.class);
//                        Intent intentBroadcast2 = new Intent();
//                        intentBroadcast
//                                .setAction("com.tong.financial_management.HomepageActivity");
//                        getContext().sendBroadcast(intentBroadcast2);
//                    }

//                    Intent home = new Intent(getContext(),
//                            HomepageActivity.class);
//                    startActivity(home);
//                    getActivity().overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);
//                    getActivity().finish();
                } else {
                    PicToastUtil.showPicToast(getContext(), ""
                            .equals(VolleyUtilsTemp.msg) ? "登录失败！"
                            : VolleyUtilsTemp.msg);
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
                Configs.login);
    }

    private boolean isLegal() {
        if (!checkbox.isChecked()) {
            PicToastUtil.showPicToast(getContext(), "您还未同意注册协议!");
            return false;
        }
        if (null == et_phone.getText() || null == et_phone.getText().toString()
                || "".equals(et_phone.getText().toString())) {
            PicToastUtil.showPicToast(getContext(), "请输入你的手机号码!");
            et_phone.requestFocus();
            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
            return false;
        } else if (null == et_captcha.getText()
                || null == et_captcha.getText().toString()
                || "".equals(et_captcha.getText().toString())) {
            // Utils.showToast("请输入验证码!");
            PicToastUtil.showPicToast(getContext(), "请输入验证码!");
            et_captcha.requestFocus();
            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
            return false;
        } else if (null == et_password.getText()
                || null == et_password.getText().toString()
                || "".equals(et_password.getText().toString())) {
            // Utils.showToast("请输入密码!");
            PicToastUtil.showPicToast(getContext(), "请输入密码!");
            et_password.requestFocus();
            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
            return false;
        } else if (et_password.getText().toString().length() < 6) {
            PicToastUtil.showPicToast(getContext(), "密码至少6位!");
            et_password.requestFocus();
            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
            return false;
        } else if (!Utils.isMobileNO(et_phone.getText().toString())) {
            // Utils.showToast("请输入正确的手机号码!");
            PicToastUtil.showPicToast(getContext(), "请输入正确的手机号码!");
            et_phone.requestFocus();
            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
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
    public void getSuccess() {
        if ("".equals(PreferencesHelper.getString(ConstantsME.registerProtocol))) {
            PicToastUtil.showPicToast(mContext, "未能获取用户协议");
        } else {
            Intent intentWebview = new Intent(getContext(),
                    WebViewActivity.class);
            intentWebview.putExtra("title", "好梦学车用户协议");
            intentWebview.putExtra("url", PreferencesHelper.getString(ConstantsME.registerProtocol));
            startActivity(intentWebview);
            getActivity().overridePendingTransition(R.anim.slide_right_in,
                    R.anim.slide_left_out);
        }
    }

    @Override
    public void getFail() {
        PicToastUtil.showPicToast(mContext, "未能获取用户协议");
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
            tv_get_captcha.setTextColor(getResources().getColor(R.color.white));
            tv_get_captcha.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            try {
                tv_get_captcha.setClickable(false);
//			tv_get_captcha.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.selector_bg_captcha));
                tv_get_captcha.setTextColor(getResources().getColor(
                        R.color.white));
                tv_get_captcha.setText(millisUntilFinished / 1000 + "秒");
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mContext.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
