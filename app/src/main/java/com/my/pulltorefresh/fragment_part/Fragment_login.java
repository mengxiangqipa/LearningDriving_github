package com.my.pulltorefresh.fragment_part;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.gooddream.learningdriving.activity.ForgetPasswordActivity;
import com.gooddream.learningdriving.activity.HomepageActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.broadcast.FinishBroadCastReceiver;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.configs.SMStest;
import com.my.customviews.OverScrollView;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.PicToastUtil;
import com.my.utils.PreferencesHelper;
import com.my.utils.RSAmethod;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Fragment_login extends Fragment implements View.OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback{
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////
    OverScrollView overScrollView;
    EditText et_phone;
    EditText et_password;
    TextView tv_sure, tv_forget_password;
    private FinishBroadCastReceiver receiver;

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
            view = inflater.inflate(R.layout.fragment_login, null);
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
        initData();
        try {
            SMStest.test(getActivity());
        } catch (Exception e) {
            Y.y("SMStest_e:"+e.getMessage());
            e.printStackTrace();
        }
        receiver = new FinishBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.gooddream.learningdriving.Login");
        getActivity().registerReceiver(receiver, filter);
        return myContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
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
        et_password = (EditText) view.findViewById(R.id.et_password);
        overScrollView = (OverScrollView) view.findViewById(R.id.overScrollView);
        tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        tv_forget_password = (TextView) view.findViewById(R.id.tv_forget_password);

        tv_sure.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
    }

    /**
     * login
     */
    private void stringRequestLogin() {
        // TODO Auto-generated method stub
        CustomProgressDialogUtils.showProcessDialog(getContext(),
                "正在登录...");
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
                            .equals(VolleyUtilsTemp.msg)||"success".equals(VolleyUtilsTemp.msg) ? "登录成功！"
                            : VolleyUtilsTemp.msg);
                    String token = VolleyUtilsTemp.optString(
                            VolleyUtilsTemp.dataBodyJSONObject,
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
                        intent.putExtra(ConstantsME.fromDialog,true);
                        intent.setAction(ConstantsME.goodDream_broadcast_loginsuccess);
                        mContext.sendBroadcast(intent);
                        getActivity().setResult(getActivity().RESULT_OK, getActivity().getIntent());
                        getActivity().finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(ConstantsME.fromDialog,false);
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

        if (null == et_phone.getText() || null == et_phone.getText().toString()
                || "".equals(et_phone.getText().toString())) {
            PicToastUtil.showPicToast(getContext(), "请输入你的手机号码!");
            et_phone.requestFocus();
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
        } else if (!Utils.isMobileNO(et_phone.getText().toString())) {
            // Utils.showToast("请输入正确的手机号码!");
            PicToastUtil.showPicToast(getContext(), "请输入正确的手机号码!");
            et_phone.requestFocus();
            Utils.isCloseSoftInputMethod(getContext(), et_phone, false);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure:
                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
                if (isLegal()) {
                    stringRequestLogin();
                }
                break;
            case R.id.tv_forget_password:
                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
                Intent intent = new Intent(getContext(),
                        ForgetPasswordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}
