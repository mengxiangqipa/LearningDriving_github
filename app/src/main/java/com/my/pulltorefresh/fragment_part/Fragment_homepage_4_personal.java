package com.my.pulltorefresh.fragment_part;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.ClipCircleIconActivity;
import com.gooddream.learningdriving.activity.CoachDetailActivity;
import com.gooddream.learningdriving.activity.HomepageActivity;
import com.gooddream.learningdriving.activity.TestActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.interfaces.CallBack_SignUp;
import com.gooddream.learningdriving.other.DialogAdapter;
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.gooddream.learningdriving.other.GetPoptipsUtil;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.my.broadcast.FinishBroadCastReceiver;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.customviews.CustomRoundImageView_new;
import com.my.customviews.OverScrollView;
import com.my.security.Base64Coder;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.ImageLoaderUtils_nostra13;
import com.my.utils.PicToastUtil;
import com.my.utils.PreferencesHelper;
import com.my.utils.ReLoginUtils;
import com.my.utils.ThreadPoolUtil;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

public class Fragment_homepage_4_personal extends Fragment implements View.OnClickListener, ReLoginUtils.CallBack_ReLogin, ActivityCompat.OnRequestPermissionsResultCallback {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////
    private FinishBroadCastReceiver receiver;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader2;

    private OverScrollView overScrollView;
    private CustomRoundImageView_new iv_icon;
    private TextView tv_nick, tv_phone, tv_day1, tv_day2, tv_sign_go, tv_sign, tv_total_money, tv_left_money, tv_left_time;
    private ImageView iv_setting, iv_progress1, iv_progress2, iv_progress3, iv_progress4, iv_progress5;
    private LinearLayout linear_sign, linaear_my_coach, linaear_my_coupon, linaear_online_consult;
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
    private TextView tv_progress_1, tv_progress_2, tv_progress_3, tv_progress_4, tv_progress_5, tv_ensure;

    private String teacherId = "";
    private int state = 0;
    /**
     * 从相册获取图片
     */
    private final int REQUESTCODE_FROMSDCARD = 1000;
    /**
     * 从相机获取图片并裁剪
     */
    private final int REQUESTCODE_CAPTURE = 1001;
    /**
     * 从相册获取图片并裁剪
     */
    private final int REQUESTCODE_FROMSDCARD_RESULT = 2000;
    /**
     * 从相机获取图片并裁剪
     */
    private final int REQUESTCODE_CAPTURE_RESULT = 2001;

    private static final int HANDLDER_REQUEST = 100;
    private static final int HANDLDER_DOING = 101;
    private static final int HANDLDER_SUCCESS = 102;
    private static final int HANDLDER_FAILURE = 103;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        imageLoader2 = ImageLoaderUtils_nostra13.initImageLoader(mContext);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.fragment_homepage4_personal, null);
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
        return myContainer;
    }

    private void initData() {
        GetPoptipsUtil.newInstance().getContant(mContext);
    }

    private void initEvent() {
        overScrollView.setOverScrollTinyListener(new OverScrollView.OverScrollTinyListener() {
            @Override
            public void scrollDistance(int tinyDistance, int totalDistance) {

            }

            @Override
            public void scrollLoosen() {
                if (!firstSuccess) {
                    stringRequestPerson();
                }
            }
        });
    }


    private void initView() {
        overScrollView = (OverScrollView) view.findViewById(R.id.overScrollView);
        iv_icon = (CustomRoundImageView_new) view.findViewById(R.id.iv_icon);
        tv_nick = (TextView) view.findViewById(R.id.tv_nick);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_day1 = (TextView) view.findViewById(R.id.tv_day1);
        tv_day2 = (TextView) view.findViewById(R.id.tv_day2);
        tv_sign_go = (TextView) view.findViewById(R.id.tv_sign_go);
        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        tv_total_money = (TextView) view.findViewById(R.id.tv_total_money);
        tv_left_money = (TextView) view.findViewById(R.id.tv_left_money);
        tv_left_time = (TextView) view.findViewById(R.id.tv_left_time);

        tv_progress_1 = (TextView) view.findViewById(R.id.tv_progress_1);
        tv_progress_2 = (TextView) view.findViewById(R.id.tv_progress_2);
        tv_progress_3 = (TextView) view.findViewById(R.id.tv_progress_3);
        tv_progress_4 = (TextView) view.findViewById(R.id.tv_progress_4);
        tv_progress_5 = (TextView) view.findViewById(R.id.tv_progress_5);

        iv_setting = (ImageView) view.findViewById(R.id.iv_setting);
        iv_progress1 = (ImageView) view.findViewById(R.id.iv_progress1);
        iv_progress2 = (ImageView) view.findViewById(R.id.iv_progress2);
        iv_progress3 = (ImageView) view.findViewById(R.id.iv_progress3);
        iv_progress4 = (ImageView) view.findViewById(R.id.iv_progress4);
        iv_progress5 = (ImageView) view.findViewById(R.id.iv_progress5);

        linear_sign = (LinearLayout) view.findViewById(R.id.linear_sign);
        linaear_my_coach = (LinearLayout) view.findViewById(R.id.linaear_my_coach);
        linaear_my_coupon = (LinearLayout) view.findViewById(R.id.linaear_my_coupon);
        linaear_online_consult = (LinearLayout) view.findViewById(R.id.linaear_online_consult);

        progressBar1 = (ProgressBar) view.findViewById(R.id.progress1);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        progressBar3 = (ProgressBar) view.findViewById(R.id.progress3);
        progressBar4 = (ProgressBar) view.findViewById(R.id.progress4);

        tv_ensure = (TextView) view.findViewById(R.id.tv_ensure);

        iv_setting.setOnClickListener(this);
        linear_sign.setOnClickListener(this);
        tv_sign_go.setOnClickListener(this);
        linaear_my_coach.setOnClickListener(this);
        linaear_my_coupon.setOnClickListener(this);
        linaear_online_consult.setOnClickListener(this);
        iv_progress1.setOnClickListener(this);
        iv_progress2.setOnClickListener(this);
        iv_progress3.setOnClickListener(this);
        iv_progress4.setOnClickListener(this);
        iv_progress5.setOnClickListener(this);
        tv_ensure.setOnClickListener(this);
        iv_icon.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.tv_sure:
//                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
//                if (isLegal()) {
//                    stringRequestLogin();
//                }
//                break;
//            case R.id.tv_forget_password:
//                Utils.isCloseSoftInputMethod(getContext(), et_phone, true);
//                Intent intent = new Intent(getContext(),
//                        ForgetPasswordActivity.class);
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_right_in,
//                        R.anim.slide_left_out);
//                break;
            case R.id.iv_setting:
                Intent intentTest = new Intent(getContext(),
                        TestActivity.class);
                startActivity(intentTest);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
                break;
            case R.id.linear_sign:
                Utils.showToast("敬请期待!");
                break;
            case R.id.tv_sign_go:
                if (mCallBack != null) {
                    mCallBack.clickSignUp();
                }
                break;
            case R.id.linaear_my_coach:
                if (TextUtils.isEmpty(teacherId)) {
                    PicToastUtil.showPicToast(mContext, "还没有报名");
                    return;
                }
                Intent intent = new Intent(getContext(),
                        CoachDetailActivity.class);
                intent.putExtra(ConstantsME.fromMyCoach, true);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
                break;
            case R.id.linaear_my_coupon:
                Utils.showToast("敬请期待!");
                break;
            case R.id.linaear_online_consult:
                if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantConsult))) {
                    Intent intentWebView1 = new Intent(mContext,
                            WebViewActivity.class);
                    intentWebView1.putExtra("url", PreferencesHelper.getString(ConstantsME.constantConsult));
                    intentWebView1.putExtra("title", "在线咨询");
                    startActivity(intentWebView1);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    GetConstantUtil.newInstance().getContant(mContext);
                }
                break;
            case R.id.iv_progress1:
                if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantTipsRead))) {
                    GetPoptipsUtil.newInstance().getContant(mContext);
                } else {
                    showPop2(mContext, 1);
                }
                break;
            case R.id.iv_progress2:
                if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantTipsRead))) {
                    GetPoptipsUtil.newInstance().getContant(mContext);
                } else {
                    showPop2(mContext, 2);
                }
                break;
            case R.id.iv_progress3:
                if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantTipsRead))) {
                    GetPoptipsUtil.newInstance().getContant(mContext);
                } else {
                    showPop2(mContext, 3);
                }
                break;
            case R.id.iv_progress4:
                if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantTipsRead))) {
                    GetPoptipsUtil.newInstance().getContant(mContext);
                } else {
                    showPop2(mContext, 4);
                }
                break;
            case R.id.iv_progress5:
                if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantTipsRead))) {
                    GetPoptipsUtil.newInstance().getContant(mContext);
                } else {
                    showPop2(mContext, 5);
                }
                break;
            case R.id.tv_ensure:
                if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantTipsOperate))) {
                    GetPoptipsUtil.newInstance().getContant(mContext);
                } else {
                    showPop1(mContext, state);
                }
                break;
            case R.id.iv_icon:
                showChoosePicDialog();
                break;
            default:
                break;
        }
    }

    private boolean firstSuccess = false;
    private boolean fromCapture = false;
    private boolean fromProgress = true;

    private void stringRequestPerson() {
        // TODO Auto-generated method stub
        if (!firstSuccess) {
            CustomProgressDialogUtils.showProcessDialog(mContext, "正在获取用户信息...");
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject params = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("stringRequestPerson:" + params);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.personalInfo + "?jsonRequest=" + params.toString(),
                null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgressDialogUtils.dismissProcessDialog();
                Y.i("yy", "stringRequestPerson:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    firstSuccess = true;

                    tv_nick.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "name", "nick"));
                    tv_phone.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "mobile", "mobile"));
                    tv_day1.setText("已报名" + VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "singupDays", "0") + "天");
                    tv_day2.setText("已练车" + VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "studyCarDays", "0") + "天");
                    teacherId = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teacherId");
                    JSONObject objcetProduct = VolleyUtilsTemp.optJSONObject(VolleyUtilsTemp.dataBodyJSONObject, "product");
                    tv_total_money.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "signupFee", "0.0"));
                    tv_left_money.setText(VolleyUtilsTemp.optString(objcetProduct, "signupBalance", "0.0"));
                    tv_sign_go.setText(VolleyUtilsTemp.optString(objcetProduct, "productName", "别等啦 马上报名"));
                    if (!TextUtils.isEmpty(VolleyUtilsTemp.optString(objcetProduct, "productName"))) {
                        tv_sign_go.setEnabled(false);
                    }
                    try {
                        imageLoader2.displayImage(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "photoPath").split(";")[0],
                                iv_icon, ImageLoaderUtils_nostra13.getFadeOptions(
                                        R.drawable.bg_default_icon,
                                        R.drawable.bg_default_icon,
                                        R.drawable.bg_default_icon));
                    } catch (Exception e) {

                    }
                    String a = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "studyStatus");
                    if (!TextUtils.isEmpty(a)) {
                        switch (a) {
                            case "RJ":
                                state = 1;
                                break;
                            case "K1":
                                state = 2;
                                break;
                            case "K2":
                                state = 3;
                                break;
                            case "K3":
                                state = 4;
                                break;
                            case "K4":
                                state = 5;
                                break;
                            case "OVER":
                                state = 6;
                                break;
                            default:
                                state = 0;
                                break;
                        }
                    }
                    switch (state) {
                        case 0:
                            tv_ensure.setText("确定入籍");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            tv_ensure.setText("确定通过科一");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            tv_ensure.setText("确定通过科二");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            tv_ensure.setText("确定通过科三");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            tv_ensure.setText("确定通过科四");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 5:
                        case 6:
                            tv_ensure.setVisibility(View.INVISIBLE);
                            break;
                        default:
                            break;

                    }

                    Y.y("进度：" + progressBar1.getProgress());
                    if (fromProgress && 0 != state && 1 != state) {
                        Message message = mHandler.obtainMessage();
                        message.what = Handler_progress_go;
                        message.arg1 = state - 1;
                        mHandler.sendMessage(message);
                    } else {
                        if (0 == state) {
                        } else if (1 == state) {
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                        } else if (2 == state) {
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(100);
                            iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                            tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                        } else if (3 == state) {
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(100);
                            iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                            tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar2.setProgress(100);
                            iv_progress3.setImageResource(R.drawable.icon_personal_3_2);
                            tv_progress_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
                        } else if (4 == state) {
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(100);
                            iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                            tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar2.setProgress(100);
                            iv_progress3.setImageResource(R.drawable.icon_personal_3_2);
                            tv_progress_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar3.setProgress(100);
                            iv_progress4.setImageResource(R.drawable.icon_personal_3_2);
                            tv_progress_4.setTextColor(getResources().getColor(R.color.gooddream_blue));
                        } else if (5 == state) {
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(100);
                            iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                            tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar2.setProgress(100);
                            iv_progress3.setImageResource(R.drawable.icon_personal_3_2);
                            tv_progress_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar3.setProgress(100);
                            iv_progress4.setImageResource(R.drawable.icon_personal_3_2);
                            tv_progress_4.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar4.setProgress(100);
                            iv_progress5.setImageResource(R.drawable.icon_personal_2_2);
                            tv_progress_5.setTextColor(getResources().getColor(R.color.gooddream_blue));
                        }
                    }
//                    firstSuccess = false;
                    fromProgress = false;
                } else if (Configs.reLogin.equals(VolleyUtilsTemp.flag)) {
                    ReLoginUtils.newInstance().reLogin(mContext);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressDialogUtils.dismissProcessDialog();
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.personalInfo);
    }

    private void stringRequestApply(int type) {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
//        JSONObject jsonObject1 = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("stringRequestApply:" + params);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.apply,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestApply:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    String signupBalance = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "signupBalance");
                    tv_left_money.setText(TextUtils.isEmpty(signupBalance) ? "0.0" : signupBalance);
                    state += 1;
                    Message message = mHandler.obtainMessage();
                    message.what = Handler_progress_go;
                    message.arg1 = state - 1;
                    mHandler.sendMessage(message);
                    switch (state) {
                        case 0:
                            tv_ensure.setText("确定入籍");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            tv_ensure.setText("确定通过科一");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            tv_ensure.setText("确定通过科二");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            tv_ensure.setText("确定通过科三");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            tv_ensure.setText("确定通过科四");
                            tv_ensure.setVisibility(View.VISIBLE);
                            break;
                        default:
                            tv_ensure.setVisibility(View.INVISIBLE);
                            break;

                    }
                } else if (Configs.reLogin.equals(VolleyUtilsTemp.flag)) {
                    ReLoginUtils.newInstance().reLogin(mContext);
                } else {
                    PicToastUtil.showPicToast(mContext, "".equals(VolleyUtilsTemp.msg) ? "操作异常" : VolleyUtilsTemp.msg);
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
                Configs.apply);
    }

    private void stringRequestUploadIcon() {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));
            jsonObjectData.put("icon", picStr);

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HashMap<String, String> stringStringHashMap = VolleyUtilsTemp.returnParams(jsonObject, true);
//        JSONObject jsonObject1 = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("stringRequestUploadIcon:" + stringStringHashMap);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.uploadImage,
                stringStringHashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                fromCapture = false;
                Y.i("yy", "stringRequestUploadIcon:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    stringRequestPerson();
                } else if (Configs.reLogin.equals(VolleyUtilsTemp.flag)) {
                    ReLoginUtils.newInstance().reLogin(mContext);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fromCapture = false;
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.uploadImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!fromCapture) {
            stringRequestPerson();
        }
    }

    @Override
    public void loginSuccess(int requestCode) {
        if (!firstSuccess) {
            stringRequestPerson();
        }
    }

    @Override
    public void loginFail(int requestCode) {
        if (HomepageActivity.instance != null) {
            Fragment f = new Fragment_homepage_4();
            HomepageActivity.instance.switchFragment(f, 3, true);
        }
    }

    CallBack_SignUp mCallBack;

    public void setCallBack(CallBack_SignUp mCallBack) {
        this.mCallBack = mCallBack;
    }

    private PopupWindow popwindow = null;

    private void showPop1(Context context, final int type) {
        // TODO Auto-generated method stub
        popwindow = null;
        View viewPop = LayoutInflater.from(context).inflate(
                R.layout.inflater_pop_apply, null);
        LinearLayout relative = (LinearLayout) viewPop
                .findViewById(R.id.relative_pop_item);
        relative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popwindow.isShowing()) {
                    popwindow.dismiss();
                }
            }
        });
        // int width_screen = ScreenUtils.getScreenWidth(this);
        // width_screen=ScreenUtils.dip2px(this, width_screen);
        popwindow = new PopupWindow(viewPop,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置整个popupwindow的弹出，收起方式
        popwindow.setAnimationStyle(R.style.popWindowAnim);
        // 需要设置一下此参数，点击外边可消失
        popwindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popwindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popwindow.setFocusable(true);
        // 加载动画
        final Animation animation_n = AnimationUtils.loadAnimation(context,
                R.anim.second_mark_rotate_n);
        // 以下两个属性设置位移动画的停止
        animation_n.setFillEnabled(true);
        animation_n.setFillAfter(true);
        // 加载动画
        // 以下两个属性设置位移动画的停止
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub

            }
        });
        String[] tips = null;
        try {
            JSONObject jsonObject = new JSONObject(PreferencesHelper.getString(ConstantsME.constantTipsOperate));
            String rj = jsonObject.optString("rj");
            String k1 = jsonObject.optString("k1");
            String k2 = jsonObject.optString("k2");
            String k3 = jsonObject.optString("k3");
            String k4 = jsonObject.optString("k4");
            tips = new String[]{rj, k1, k2, k3, k4};
        } catch (Exception e) {
        }
        TextView tvEnsure = (TextView) viewPop.findViewById(R.id.tv_ensure);
        TextView tvCancel = (TextView) viewPop.findViewById(R.id.tv_cancel);
        final TextView tv_content = (TextView) viewPop.findViewById(R.id.tv_content);
        switch (type) {
            case 0:
                tv_content.setText(null == tips ? "           确定入籍？" : "           " + tips[type]);
                break;
            case 1:
                tv_content.setText(null == tips ? "           确定通过科目一？" : "           " + tips[type]);
                break;
            case 2:
                tv_content.setText(null == tips ? "           确定通过科目二？" : "           " + tips[type]);
                break;
            case 3:
                tv_content.setText(null == tips ? "           确定通过科目三？" : "           " + tips[type]);
                break;
            case 4:
                tv_content.setText(null == tips ? "           确定通过科目四？" : "           " + tips[type]);
                break;
            default:
                break;
        }
        tvEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindow.dismiss();
                stringRequestApply(type);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindow.dismiss();
            }
        });
        popwindow.showAtLocation(((Activity) context).getWindow().getDecorView(),
                Gravity.CENTER, 0, 50);
    }

    private void showPop2(Context context, int type) {
        // TODO Auto-generated method stub
        popwindow = null;
        View viewPop = LayoutInflater.from(context).inflate(
                R.layout.inflater_pop_apply_naturalization, null);
        LinearLayout relative = (LinearLayout) viewPop
                .findViewById(R.id.relative_pop_item);
        relative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popwindow.isShowing()) {
                    popwindow.dismiss();
                }
            }
        });
        popwindow = new PopupWindow(viewPop,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置整个popupwindow的弹出，收起方式
        popwindow.setAnimationStyle(R.style.popWindowAnim);
        // 需要设置一下此参数，点击外边可消失
        popwindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popwindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popwindow.setFocusable(true);
        // 加载动画
        final Animation animation_n = AnimationUtils.loadAnimation(context,
                R.anim.second_mark_rotate_n);
        // 以下两个属性设置位移动画的停止
        animation_n.setFillEnabled(true);
        animation_n.setFillAfter(true);
        // 加载动画
        // 以下两个属性设置位移动画的停止
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub

            }
        });
        String[] tips = null;
        try {
            JSONObject jsonObject = new JSONObject(PreferencesHelper.getString(ConstantsME.constantTipsRead));
            String rj = jsonObject.optString("rj");
            String k1 = jsonObject.optString("k1");
            String k2 = jsonObject.optString("k2");
            String k3 = jsonObject.optString("k3");
            String k4 = jsonObject.optString("k4");
            tips = new String[]{rj, k1, k2, k3, k4};
        } catch (Exception e) {
        }
        TextView tv_content = (TextView) viewPop.findViewById(R.id.tv_content);
        if (type == 1) {
            tv_content.setText(tips == null ? "           入籍" : "           " + tips[type - 1]);
        } else if (type == 2) {
            tv_content.setText(tips == null ? "           科一" : "           " + tips[type - 1]);
        } else if (type == 3) {
            tv_content.setText(tips == null ? "           科二" : "           " + tips[type - 1]);
        } else if (type == 4) {
            tv_content.setText(tips == null ? "           科三" : "           " + tips[type - 1]);
        } else if (type == 5) {
            tv_content.setText(tips == null ? "           科四" : "           " + tips[type - 1]);
        }
        TextView tvEnsure = (TextView) viewPop.findViewById(R.id.tv_ensure);
        TextView tvCancel = (TextView) viewPop.findViewById(R.id.tv_cancel);
        tvEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindow.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindow.dismiss();
            }
        });
        popwindow.showAtLocation(((Activity) context).getWindow().getDecorView(),
                Gravity.CENTER, 0, 50);
    }

    private void showChoosePicDialog() {
        // TODO Auto-generated method stub
        final String[] items = new String[]{"从手机相册中选取", "拍照"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setAdapter(new DialogAdapter(mContext, true, items),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface diaLog, int position) {
                        // TODO Auto-generated method stub
                        switch (position) {
                            case 0:
                                int permissionCheck1 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                                int permissionCheck2 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                                    //申请WRITE_EXTERNAL_STORAGE权限
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                                            PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//                                    ActivityCompat.requestPermissions(getActivity(),
//                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                                    Manifest.permission.READ_EXTERNAL_STORAGE},
//                                            PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                                } else {
                                    choosePicFromSDcard(100, 100);
                                }
                                break;
                            case 1:
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    Y.y("请求相机权限1");
                                    requestPermissions(
                                            new String[]{Manifest.permission.CAMERA},
                                            PERMISSION_CAMERA);
                                } else {
                                    Y.y("请求相机权限2_拍照");
                                    choosePicFromCapture(100, 100);
                                }
                                break;

                            default:
                                break;
                        }
                    }
                });
        Dialog diaLog = builder.create();
        Window diaLogwindow = diaLog.getWindow();
        WindowManager.LayoutParams params = diaLogwindow.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.windowAnimations = R.anim.slide_left_in;
        params.width = 200;
        params.y = 40;
        params.x = 0;
        /*
         * 将对话框的大小按屏幕大小的百分比设置
		 */
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        diaLogwindow.setAttributes(params);
        diaLog.show();
    }

    /**
     * <pre>
     * 从sdcard选择图片并裁剪
     * outputX 输出图片x值
     * outputY 输出图片y值
     * </pre>
     */
    private void choosePicFromSDcard(int outputX, int outputY) {
        // try {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        String sdpath = Utils.getSDPath();// /storage/sdcard0
        tempFileFromSDcard = new File(sdpath + "/" + "gooddreamCache");
        if (!tempFileFromSDcard.exists()) {
            tempFileFromSDcard.mkdirs();
        }
        tempFileFromSDcard = new File(tempFileFromSDcard, +Calendar
                .getInstance().getTimeInMillis() + ".jpg"); // 以时间秒为文件名
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFileFromSDcard));
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUESTCODE_FROMSDCARD);

    }

    File tempFileFromSDcard;
    File tempFileFromCapture;

    /**
     * <pre>
     * 从相机拍摄图片并裁剪
     * outputX 输出图片x值
     * outputY 输出图片y值
     * </pre>
     */
    private void choosePicFromCapture(int outputX, int outputY) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String sdpath = Utils.getSDPath();// /storage/sdcard0
        tempFileFromCapture = new File(sdpath + "/" + "gooddreamCache");
        if (!tempFileFromCapture.exists()) {
            tempFileFromCapture.mkdirs();
        }
        tempFileFromCapture = new File(tempFileFromCapture, +Calendar
                .getInstance().getTimeInMillis() + ".jpg"); // 以时间秒为文件名
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFileFromCapture));
        startActivityForResult(intent, REQUESTCODE_CAPTURE);
    }

    private String picStr;

    /**
     * 保存裁剪之后的图片数据
     */
    private void uploadPic(Intent data, Bitmap bitmap) {
        Bitmap photo = null;
        if (null != bitmap) {
            photo = bitmap;
        } else if (null != data && null != data.getExtras()) {
            Bundle extras = data.getExtras();
            photo = extras.getParcelable("data");
        }
        if (null != photo) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] bytes = stream.toByteArray();
            picStr = new String(Base64Coder.encodeLines(bytes));
            Y.y("picStrUpload:" + picStr);
            Y.y("picStrUpload:" + picStr.length());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_FROMSDCARD:
                    Bitmap bmap_sdcard = null;
                    if (null != data) {
                        try {
                            Y.y("null==:"
                                    + (null == Utils.getRealFilePath(
                                    mContext, data.getData())));
                            Y.y(Utils.getRealFilePath(mContext,
                                    data.getData()));
                            File file = new File(Utils.getRealFilePath( mContext, data.getData()));
                            Y.y("file.exists:" + file.exists());
                            bmap_sdcard = Utils.getBitmapFromFile(file,
                                    Utils.getScreenWidth(mContext),
                                    Utils.getScreenWidth(mContext));
                            Y.y("bmap_sdcard==null 1:" + (bmap_sdcard == null));
                            Y.y("bmap_sdcard getByteCount1:"
                                    + bmap_sdcard.getByteCount());
                            if (null != bmap_sdcard) {
                                Y.y("跳转:" + "1");
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                Y.y("跳转:" + "2");
                                bmap_sdcard.compress(Bitmap.CompressFormat.JPEG,
                                        100, baos);
                                Y.y("跳转:" + "3");
                                byte[] datas = baos.toByteArray();
                                Y.y("跳转:" + "4");
                                Intent intent = new Intent(mContext,
                                        ClipCircleIconActivity.class);
                                Y.y("跳转:" + "5");
//                                intent.putExtra("bitmap", datas);
                                intent.putExtra("filePath", file.getAbsolutePath());
                                Y.y("跳转:" + "6");
                                startActivityForResult(intent, 6000);
                                Y.y("跳转:" + "7");
                            }
                        } catch (Exception e) {
                            Y.y("e.:" + e.getMessage());
                            Utils.showToast("图片解析出错");
                        }
                    }
                    break;
                case REQUESTCODE_CAPTURE:
                    Bitmap bmap_capture = null;
                    if (null != tempFileFromCapture) {
                        bmap_capture = Utils.getBitmapFromFile(tempFileFromCapture,
                                Utils.getScreenWidth(mContext),
                                Utils.getScreenWidth(mContext));
                    }
                    if (null != bmap_capture) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmap_capture
                                .compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] datas = baos.toByteArray();
                        Intent intent = new Intent(mContext, ClipCircleIconActivity.class);
//                        intent.putExtra("bitmap", datas);
                        intent.putExtra("filePath", tempFileFromCapture.getAbsolutePath());
                        startActivityForResult(intent, 6000);
                    }
                    break;

                case 6000:
                    if (data != null) {
                        Y.y("data.getByteArrayExtra:"
                                + data.getByteArrayExtra("bitmap"));
                        byte[] b = data.getByteArrayExtra("bitmap");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0,
                                b.length);
                        uploadPic(null, bitmap);
                        if (null != bitmap) {

                            iv_icon.setScaleType(ImageView.ScaleType.FIT_XY);
                            iv_icon.setImageDrawable(new BitmapDrawable(
                                    bitmap));
                        }
                        fromCapture = true;
                        stringRequestUploadIcon();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private final int Handler_progress_go = 100;
    private final int Handler_progress_going = 101;
    private final int Handler_progress_end = 102;
    private final int[] totalTimemillis = {1000, 2000, 3000, 4000};
    private final int interValTimemillis = 50;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Handler_progress_go:
                    Y.y("我是 Handler_progress_go");
                    final int state = msg.arg1;
                    Y.y("我是 Handler_progress_go" + state);
                    if (state >= 1 && state <= 4) {
                        ThreadPoolUtil.getInstanceSingleTaskExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                int a = 0;
                                while (a < totalTimemillis[state - 1]) {
                                    Y.y("a:" + a);
                                    Message message = mHandler.obtainMessage();
                                    message.what = Handler_progress_going;
                                    message.arg1 = state;
                                    mHandler.sendMessageDelayed(message, interValTimemillis);
                                    a += interValTimemillis;
                                    try {
                                        Thread.sleep(interValTimemillis);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        });
                    }
                    break;
                case Handler_progress_going:
                    switch (msg.arg1) {
                        case 1:
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(progressBar1.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 1));
                            if (progressBar1.getProgress() >= 100) {
                                iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                                tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            }
                            break;
                        case 2:
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(progressBar1.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 2));
                            if (progressBar1.getProgress() >= 100) {
                                iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                                tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                progressBar2.setProgress(progressBar2.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 2));
                                if (progressBar2.getProgress() >= 100) {
                                    iv_progress3.setImageResource(R.drawable.icon_personal_3_2);
                                    tv_progress_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                }
                            }
                            break;
                        case 3:
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(progressBar1.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 3));
                            if (progressBar1.getProgress() >= 100) {
                                iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                                tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                progressBar2.setProgress(progressBar2.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 3));
                                if (progressBar2.getProgress() >= 100) {
                                    iv_progress3.setImageResource(R.drawable.icon_personal_3_2);
                                    tv_progress_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                    progressBar3.setProgress(progressBar3.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 3));
                                    if (progressBar3.getProgress() >= 100) {
                                        iv_progress4.setImageResource(R.drawable.icon_personal_3_2);
                                        tv_progress_4.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                    }
                                }
                            }
                            break;
                        case 4:
                            Y.y("刷新进度：" + progressBar1.getProgress());
                            iv_progress1.setImageResource(R.drawable.icon_personal_1_2);
                            tv_progress_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
                            progressBar1.setProgress(progressBar1.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 4));
                            if (progressBar1.getProgress() >= 100) {
                                iv_progress2.setImageResource(R.drawable.icon_personal_2_2);
                                tv_progress_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                progressBar2.setProgress(progressBar2.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 4));
                                if (progressBar2.getProgress() >= 100) {
                                    iv_progress3.setImageResource(R.drawable.icon_personal_3_2);
                                    tv_progress_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                    progressBar3.setProgress(progressBar3.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 4));
                                    if (progressBar3.getProgress() >= 100) {
                                        iv_progress4.setImageResource(R.drawable.icon_personal_3_2);
                                        tv_progress_4.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                        progressBar4.setProgress(progressBar4.getProgress() + 100 / (totalTimemillis[msg.arg1 - 1] / interValTimemillis / 4));
                                        if (progressBar4.getProgress() >= 100) {
                                            iv_progress5.setImageResource(R.drawable.icon_personal_2_2);
                                            tv_progress_5.setTextColor(getResources().getColor(R.color.gooddream_blue));
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case Handler_progress_end:

                    break;
                default:
                    break;
            }
        }
    };
    private int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 10;
    private int PERMISSION_CAMERA = 11;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Y.y("个人中心onRequestPermissionsResult");
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if ((grantResults.length > 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                choosePicFromSDcard(100, 100);
            } else {
                Utils.showToast("未能获取存储权限，请设置允许读取存储卡");
            }
        } else if (requestCode == PERMISSION_CAMERA) {
            if ((grantResults.length >= 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                choosePicFromCapture(100, 100);
            } else {
                Utils.showToast("相机权限被禁用 ，请设置允许调用相机");
            }
        }
    }

}
