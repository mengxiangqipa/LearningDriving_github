package com.gooddream.learningdriving.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.map.MapActivity;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.customviews.CustomFlowLayout;
import com.my.customviews.CustomListView;
import com.my.customviews.CustomRoundImageView_new;
import com.my.customviews.OverScrollView;
import com.my.item.ViewHolder;
import com.my.other.CommonAdapter;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.pulltorefresh.fragment.Pull2RefreshListView2VolleyFragment;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.ImageLoaderUtils_nostra13;
import com.my.utils.PicToastUtil;
import com.my.utils.PreferencesHelper;
import com.my.utils.ReLoginUtils;
import com.my.utils.Utils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoachDetailActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ReLoginUtils.CallBack_ReLogin {
    private TextView tv_nick, tv_safeguard, tv_ensure;
    private TextView tv_count_1, tv_count_2, tv_count_3, tv_count_4;
    private TextView tv_location;
    private OverScrollView overScrollView;
    private RatingBar ratingbar;
    private CustomRoundImageView_new iv_icon;
    private CustomFlowLayout flowLayout;
    private LinearLayout linear_add_attr;
    private LinearLayout linear_c1c2;
    private LinearLayout linear_c1;
    private LinearLayout linear_c2;
    private LinearLayout linear_complain;
    private RadioGroup rg_c1, rg_c2;
    TextView tv_navigation;

    TextView tv_more_comments;
    CustomListView lv_comments;
    ListItem item1 = new ListItem();
    ListItem item2 = new ListItem();
    int chooseType = 0;
    int position1 = 0;
    int position2 = 0;
    private int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private int PERMISSION_LOCATION_REQUEST_CODE = 2;
    private int PERMISSION_RECORD_REQUEST_CODE = 3;
    private int PERMISSION_CALL = 4;
    boolean canComplain = false;
    String complainMsg = "";
    boolean canSignup = false;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader2;
    private String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_detail);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        initView();
        initData();
        initEvent();
        ShareApplication.share.addActivity(this);
    }


    private void initData() {
        // TODO Auto-generated method stub
        imageLoader2 = ImageLoaderUtils_nostra13.initImageLoader(this);
        if (getIntent().getBooleanExtra(ConstantsME.fromBottomBar, false)) {
            tv_ensure.setText(getResources().getText(R.string.apply_rewrite));
        }
    }


    private void initEvent() {
        overScrollView.setOverScrollTinyListener(new OverScrollView.OverScrollTinyListener() {
            @Override
            public void scrollDistance(int tinyDistance, int totalDistance) {

            }

            @Override
            public void scrollLoosen() {
                if (!firstSuccess) {
                    if (getIntent().getBooleanExtra(ConstantsME.fromMyCoach, false)) {
                        stringRequestMyCoach();
                    } else {
                        stringRequestCoachDetail();
                    }
                }
            }
        });
        rg_c1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i >= 10000) {
                    chooseType = 0;
                    rg_c2.clearCheck();
                    rg_c1.check(i);
                    position1 = i % 10000;
                    try {
                        if (item1 != null) {
                            tv_location.setText(((ListItem) item1.mdatas.get(position1)).group1Address);
                        }
                        initProductAttrs(((ListItem) item1.mdatas.get(position1)).group1Attrs);
                    } catch (Exception e) {
                    }
                }
            }
        });
        rg_c2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i >= 20000) {
                    chooseType = 1;
                    rg_c1.clearCheck();
                    rg_c2.check(i);
                    position2 = i % 20000;
                    try {
                        if (item2 != null) {
                            tv_location.setText(((ListItem) item2.mdatas.get(position2)).group1Address);
                        }
                        initProductAttrs(((ListItem) item2.mdatas.get(position2)).group1Attrs);
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private void preData() {
        if (getIntent().getBooleanExtra(ConstantsME.fromMyCoach, false)) {
            ((TextView) findViewById(R.id.tv_title)).setText("我的教练");
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText("教练详情");
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        lv_comments = (CustomListView) findViewById(R.id.lv_comments);
        tv_more_comments = (TextView) findViewById(R.id.tv_more_comments);

        linear_complain = (LinearLayout) findViewById(R.id.linear_complain);
        tv_ensure = (TextView) findViewById(R.id.tv_ensure);
        overScrollView = (OverScrollView) findViewById(R.id.overScrollView);
        tv_nick = (TextView) findViewById(R.id.tv_nick);
        tv_safeguard = (TextView) findViewById(R.id.tv_safeguard);
        ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        tv_count_1 = (TextView) findViewById(R.id.tv_count_1);
        tv_count_2 = (TextView) findViewById(R.id.tv_count_2);
        tv_count_3 = (TextView) findViewById(R.id.tv_count_3);
        tv_count_4 = (TextView) findViewById(R.id.tv_count_4);

        tv_location = (TextView) findViewById(R.id.tv_location);

        iv_icon = (CustomRoundImageView_new) findViewById(R.id.iv_icon);

        flowLayout = (CustomFlowLayout) findViewById(R.id.flowLayout);

        rg_c1 = (RadioGroup) findViewById(R.id.rg_c1);
        rg_c2 = (RadioGroup) findViewById(R.id.rg_c2);

        linear_add_attr = (LinearLayout) findViewById(R.id.linear_add_attr);
        linear_c1c2 = (LinearLayout) findViewById(R.id.linear_c1c2);
        linear_c1 = (LinearLayout) findViewById(R.id.linear_c1);
        linear_c2 = (LinearLayout) findViewById(R.id.linear_c2);
        tv_navigation = (TextView) findViewById(R.id.tv_navigation);

        linear_complain.setEnabled(false);
        preData();
    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_more_comments:
                Intent intentComment = new Intent(this,
                        CommentActivity.class);
                intentComment.putExtra("teacherId",teacherId);
                intentComment.putExtra("label",label);
                startActivity(intentComment);
                overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
                break;
            case R.id.tv_sure:
                break;
            case R.id.relative_back:
                ShareApplication.share.removeActivity(this);
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;
            case R.id.linear_phone1:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PreferencesHelper.getString(ConstantsME.servicePhone)));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                                PERMISSION_CALL);
                        return;
                    }
                    startActivity(intentCall);
                    overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    int permissionCheck1 = ContextCompat.checkSelfPermission(CoachDetailActivity.this, Manifest.permission.CALL_PHONE);
                    if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CoachDetailActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                PERMISSION_CALL);
                    } else {
                        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PreferencesHelper.getString(ConstantsME.servicePhone)));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                                    PERMISSION_CALL);
                            return;
                        }
                        startActivity(intentCall);
                        overridePendingTransition(R.anim.slide_right_in,
                                R.anim.slide_left_out);
                    }
                }
                break;
            case R.id.linear_phone2:
                if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantConsult))) {
                    Intent intentWebView1 = new Intent(this,
                            WebViewActivity.class);
                    intentWebView1.putExtra("url", PreferencesHelper.getString(ConstantsME.constantConsult));
                    intentWebView1.putExtra("title", "在线咨询");
                    startActivity(intentWebView1);
                    overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    GetConstantUtil.newInstance().getContant(this);
                }
                break;
            case R.id.linear_complain:
                if (canComplain) {
                    try {
                        Intent intentWebView1 = new Intent(this,
                                ComplaintActivity.class);
                        intentWebView1.putExtra("id", teacherId);
                        startActivityForResult(intentWebView1, 10087);
                        overridePendingTransition(R.anim.slide_right_in,
                                R.anim.slide_left_out);

//                    Intent intentWebView1 = new Intent(this,
//                            WebViewActivity.class);
//                    intentWebView1.putExtra("url", PreferencesHelper.getString(ConstantsME.constantConsult));
//                    intentWebView1.putExtra("title", "在线咨询");
//                    startActivity(intentWebView1);
//                    overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PicToastUtil.showPicToast(CoachDetailActivity.this, TextUtils.isEmpty(complainMsg) ? "不满足投诉条件" : complainMsg);
                }
                break;
            case R.id.tv_navigation:
                Intent intent = new Intent(CoachDetailActivity.this,
                        MapActivity.class);
                Y.y("item1:" + (item1 == null));
                Y.y("item1:" + (item1.mdatas == null));
                Y.y("item1:" + (item1.mdatas.size()));
                if (chooseType == 1) {
                    intent.putExtra(ConstantsME.LATITUDE, ((ListItem) item2.mdatas.get(position2)).group1Latitude);
                    intent.putExtra(ConstantsME.LONGITUDE, ((ListItem) item2.mdatas.get(position2)).group1Longitude);
                    intent.putExtra("title", ((ListItem) item2.mdatas.get(position2)).group1Address);
                } else {
                    intent.putExtra(ConstantsME.LATITUDE, ((ListItem) item1.mdatas.get(position1)).group1Latitude);
                    intent.putExtra(ConstantsME.LONGITUDE, ((ListItem) item1.mdatas.get(position1)).group1Longitude);
                    intent.putExtra("title", ((ListItem) item1.mdatas.get(position2)).group1Address);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
                break;
            case R.id.tv_ensure:
                try {
                    if (PreferencesHelper.getBoolean(ConstantsME.LOGINED) && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
                        ListItem item;
                        if (chooseType == 1) {
                            item = (ListItem) item2.mdatas.get(position2);
                            item.group1C1 = "C2";
                        } else {
                            item = (ListItem) item1.mdatas.get(position1);
                            item.group1C1 = "C1";
                        }
                        if (!TextUtils.isEmpty(tv_ensure.getText().toString()) && !getText(R.string.apply_rewrite).equals(tv_ensure.getText().toString())) {
                            showPop(this, item);
                        } else {
//                            if (chooseType == 1 && !"".equals(PreferencesHelper.getString(ConstantsME.teacherProductId)) &&
//                                    ((ListItem) item2.mdatas.get(position2)).group1Id.
//                                            equals(PreferencesHelper.getString(ConstantsME.teacherProductId))) {
//                                PicToastUtil.showPicToast(this, "产品相同，无需更改");
//                            } else if (chooseType == 0 && !"".equals(PreferencesHelper.getString(ConstantsME.teacherProductId)) &&
//                                    ((ListItem) item1.mdatas.get(position1)).group1Id.
//                                            equals(PreferencesHelper.getString(ConstantsME.teacherProductId))) {
//                                PicToastUtil.showPicToast(this, "产品相同，无需更改");
//                            } else {
                            stringRequestSignUp(item);
//                            }
                        }
                    } else {
                        Intent intentLogin = new Intent(this,
                                LoginDialogActivity.class);
                        intentLogin.putExtra(ConstantsME.fromDialog, true);
                        startActivityForResult(intentLogin, 10086);
                    }
                } catch (Exception e) {
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra(ConstantsME.fromBottomBar, false)) {
            tv_ensure.setText(getResources().getText(R.string.apply_rewrite));
        }
        preData();
        if (getIntent().getBooleanExtra(ConstantsME.fromMyCoach, false)) {
            stringRequestMyCoach();
        } else {
            stringRequestCoachDetail();
        }
    }

    private boolean firstSuccess = false;
    private boolean hadAdded1 = false;
    private boolean hadAdded2 = false;

    private void stringRequestCoachDetail() {
        stringRequestCoachDetail(0);
    }

    private void stringRequestCoachDetail(final int requestCode) {
        // TODO Auto-generated method stub
        if (!firstSuccess) {
            CustomProgressDialogUtils.showProcessDialog(this, "读取教练信息...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("teacherId", getIntent().getStringExtra("id"));

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestCoachDetail:" + jsonObject);
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.coachDetail,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestCoachDetail:" + response);
                VolleyUtilsTemp.parseJson(response);
                CustomProgressDialogUtils.dismissProcessDialog();
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    dealResponse();
                    if (requestCode == 10086) {
                        stringRequestApplyState(10086);
                    } else {
                        if ("".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
                            tv_ensure.setVisibility(View.VISIBLE);
                            linear_complain.setEnabled(true);
                            complainMsg = "您还未登录，不能投诉教练!";
                            canComplain = false;
                        } else {
                            stringRequestApplyState(0);
                        }
                    }
                } else {

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
                Configs.coachDetail);
    }

    private void stringRequestMyCoach() {
        // TODO Auto-generated method stub
        if (!firstSuccess) {
            CustomProgressDialogUtils.showProcessDialog(this, "读取我的教练...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestMyCoach:" + jsonObject);
        HashMap<String, String> stringStringHashMap = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.myCoach,
                stringStringHashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestMyCoach:" + response);
                VolleyUtilsTemp.parseJson(response);
                CustomProgressDialogUtils.dismissProcessDialog();
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONObject jsonObject1 = VolleyUtilsTemp.optJSONObject(VolleyUtilsTemp.dataBodyJSONObject, "myData");
                    if (jsonObject1 != null) {
                        JSONObject complainTagJB = jsonObject1.optJSONObject("complainOperate");
                        JSONObject signupTagJB = jsonObject1.optJSONObject("signupOperate");
                        JSONObject rewardTagJB = jsonObject1.optJSONObject("rewardOperate");
                        String complainStatus = "", tipComplaint = "";
                        String modifyProductStatus = "", tipSignUp = "";
                        String rewardStatus = "", tipReward = "";
                        if (null != complainTagJB) {
                            complainStatus = complainTagJB.optString("status", "");
                            tipComplaint = complainTagJB.optString("msg", "");
                        }
                        if (null != signupTagJB) {
                            modifyProductStatus = signupTagJB.optString("status", "");
                            tipSignUp = signupTagJB.optString("msg", "");
                        }
                        if (null != rewardTagJB) {
                            rewardStatus = rewardTagJB.optString("status", "");
                            tipReward = rewardTagJB.optString("msg", "");
                        }
                        if ("permit".equals(complainStatus)) {
                            canComplain = true;
                            linear_complain.setEnabled(true);
                        } else {
                            canComplain = false;
                            linear_complain.setEnabled(true);
                        }
                        if (!TextUtils.isEmpty(tipComplaint)) {
                            complainMsg = tipComplaint;
                        }
                        if ("permit".equals(modifyProductStatus)) {
                            tv_ensure.setVisibility(View.VISIBLE);
                            tv_ensure.setText(getResources().getText(R.string.apply_rewrite));
                        } else {
                            tv_ensure.setVisibility(View.GONE);
                        }
                        if ("permit".equals(rewardStatus)) {

                        }
                    }
//                    } else if ("2".equals(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "state"))) {
//                        tv_ensure.setVisibility(View.GONE);
//                    }
                    dealResponse();
                } else {

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
                Configs.myCoach);
    }

    private void stringRequestApplyState(final int flag) {
        // TODO Auto-generated method stub
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));
            jsonObjectData.put("teacherId", teacherId);

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestApplyState:" + jsonObject);
        JSONObject jsonObject1 = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.applyState + "?jsonRequest=" + jsonObject1.toString(),
                null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestApplyState:" + response);
                VolleyUtilsTemp.parseJson(response);
                CustomProgressDialogUtils.dismissProcessDialog();
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONObject jsonObject1 = VolleyUtilsTemp.optJSONObject(VolleyUtilsTemp.dataBodyJSONObject, "operates");
                    if (jsonObject1 != null) {
                        JSONObject complainTagJB = jsonObject1.optJSONObject("complainOperate");
                        JSONObject signupTagJB = jsonObject1.optJSONObject("signupOperate");
                        JSONObject rewardTagJB = jsonObject1.optJSONObject("rewardOperate");
                        String complainStatus = "", tipComplaint = "";
                        String modifyProductStatus = "", tipSignUp = "";
                        String rewardStatus = "", tipReward = "";
                        if (null != complainTagJB) {
                            complainStatus = complainTagJB.optString("status", "");
                            tipComplaint = complainTagJB.optString("msg", "");
                        }
                        if (null != signupTagJB) {
                            modifyProductStatus = signupTagJB.optString("status", "");
                            tipSignUp = signupTagJB.optString("msg", "");
                        }
                        if (null != rewardTagJB) {
                            rewardStatus = rewardTagJB.optString("status", "");
                            tipReward = rewardTagJB.optString("msg", "");
                        }
                        if ("permit".equals(complainStatus)) {
                            canComplain = true;
                            linear_complain.setEnabled(true);
                        } else {
                            canComplain = false;
                            linear_complain.setEnabled(true);
                        }
                        if (!TextUtils.isEmpty(tipComplaint)) {
                            complainMsg = tipComplaint;
                        }
                        if ("permit".equals(modifyProductStatus)) {
                            tv_ensure.setVisibility(View.VISIBLE);
                            if (getIntent().getBooleanExtra(ConstantsME.fromBottomBar, false)) {
                                tv_ensure.setText(getResources().getText(R.string.apply_rewrite));
                            }

                            if (flag == 10086) {
                                ListItem item;
                                if (chooseType == 1) {
                                    item = (ListItem) item2.mdatas.get(position2);
                                    item.group1C1 = "C2";
                                } else {
                                    item = (ListItem) item1.mdatas.get(position1);
                                    item.group1C1 = "C1";
                                }
                                showPop(CoachDetailActivity.this, item);
                            }
                        } else {
                            if (flag == 10086) {
                                PicToastUtil.showPicToast(CoachDetailActivity.this, TextUtils.isEmpty(tipSignUp) ? "您已经报过名了" : tipSignUp);
                            }
                            tv_ensure.setVisibility(View.GONE);
                        }
                        if ("permit".equals(rewardStatus)) {

                        }
                    }
                } else if (Configs.reLogin.equals(VolleyUtilsTemp.flag)) {
                    if (flag == 10086) {
                        ReLoginUtils.newInstance().reLogin(CoachDetailActivity.this, 10086);
                    } else if (flag == 0) {
                        ReLoginUtils.newInstance().reLogin(CoachDetailActivity.this, 1);
                    }
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
                Configs.applyState);
    }
    String label;
    int labelLenth=2;
    private void dealResponse() {
        firstSuccess = true;
        teacherId = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teacherId");
        try {
            imageLoader2.displayImage(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "photoPath", "").split(";")[0],
                    iv_icon, ImageLoaderUtils_nostra13.getFadeOptions(
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon));
        } catch (Exception e) {

        }
        tv_nick.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teacherName", "教练"));
        ratingbar.setRating(Float.parseFloat(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "starLevel", "0")));
        tv_safeguard.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "guaranteeMoney", "？") + "保障金");
        tv_count_1.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teachTime", "0"));
        tv_count_2.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "passRate", "0") + "%");
        tv_count_3.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "countStuding", "0"));
        tv_count_4.setText(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "allStudy", "0"));
        label = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "labelList");
        if (!TextUtils.isEmpty(label) && flowLayout.getChildCount() <= 0) {
            String[] labelList = label.split(",");
            int length = labelList.length;
            View[] views = new View[length];
            int _8dp = Utils.dip2px(CoachDetailActivity.this, 8);
            int _5dp = Utils.dip2px(CoachDetailActivity.this, 5);
            if (length > 0 && !hadAdded2) {
                for (int j = 0; j < length; j++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.rightMargin = _8dp;
                    params.topMargin = _5dp;
                    params.bottomMargin = _5dp;
                    TextView textView = (TextView) LayoutInflater.from(CoachDetailActivity.this).inflate(R.layout.inflater_flowlabel, null);
                    textView.setText(labelList[j]);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    textView.setLayoutParams(params);
//                    flowLayout.addView(textView);
                    views[j] = textView;
                }
                flowLayout.addData(views);
                hadAdded2 = true;
            }
        }

        JSONArray arrayProduct = VolleyUtilsTemp.optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "products");
        if (arrayProduct != null && arrayProduct.length() > 0) {
            int length = arrayProduct.length();
            int k = 0;
            int j = 0;
            for (int i = 0; i < length; i++) {
                linear_c1c2.setVisibility(View.VISIBLE);
                JSONObject jsonObject1 = arrayProduct.optJSONObject(i);
                String projectType = jsonObject1.optString("projectType", "");
                int _20dp = Utils.dip2px(CoachDetailActivity.this, 20);
                if ("C1".equals(projectType)) {
                    linear_c1.setVisibility(View.VISIBLE);
                    if (rg_c1.getChildCount() <= 1 && !hadAdded1) {
                        RadioButton radioButton = (RadioButton) LayoutInflater.from(CoachDetailActivity.this).inflate(R.layout.inflater_radiobutton, null);
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        params.rightMargin = _20dp;
                        radioButton.setId(10000 + k);
                        k++;
                        radioButton.setText(jsonObject1.optString("productName", "") + " ¥" + jsonObject1.optString("productPrice", ""));
                        //根据首页底部选中状态
                        if (PreferencesHelper.getString(ConstantsME.teacherProductId).equals(jsonObject1.optString("productId", ""))) {
                            tv_ensure.setText(getResources().getText(R.string.apply_rewrite));
                            radioButton.setChecked(true);
                            position1 = i;
                            tv_location.setText(jsonObject1.optString("areaName", "场地位置"));
                            tv_navigation.setVisibility(View.VISIBLE);
                            JSONObject product = arrayProduct.optJSONObject(i);
                            if (product != null) {
                                initProductAttrs(product.toString());
                            }
                        }
                        //保存的选中状态
                        if (i == position1) {
                            radioButton.setChecked(true);
                            tv_location.setText(jsonObject1.optString("areaName", "场地位置"));
                            tv_navigation.setVisibility(View.VISIBLE);
                            JSONObject product = arrayProduct.optJSONObject(i);
                            if (product != null) {
                                initProductAttrs(product.toString());
                            }
                        }
                        radioButton.setLayoutParams(params);
                        rg_c1.addView(radioButton);
                        ListItem item = new ListItem();
                        item.group1Id = jsonObject1.optString("productId", "");
                        item.group1ProductName1 = jsonObject1.optString("productName", "");
                        item.group1ProductPrice1 = jsonObject1.optString("productPrice", "");
                        item.group1Address = jsonObject1.optString("areaName", "场地位置");
                        item.group1Longitude = Double.parseDouble(jsonObject1.optString("longitude", "0"));
                        item.group1Latitude = Double.parseDouble(jsonObject1.optString("latitude", "0"));
                        item.group1Nick = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teacherName", "教练");
                        item.group1Star = Float.parseFloat(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "starLevel", "0"));
                        item.group1IconUrl = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "photoPath", "").split(";")[0];
                        JSONObject product = arrayProduct.optJSONObject(i);
                        if (product != null) {
                            item.group1Attrs = product.toString();
                        }
                        item1.mdatas.add(item);
                    }
                }
                if ("C2".equals(projectType)) {
                    linear_c2.setVisibility(View.VISIBLE);
                    if (rg_c2.getChildCount() <= 1 && !hadAdded1) {
                        RadioButton radioButton = (RadioButton) LayoutInflater.from(CoachDetailActivity.this).inflate(R.layout.inflater_radiobutton, null);
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        params.rightMargin = _20dp;
                        radioButton.setId(20000 + j);
                        j++;
                        radioButton.setText(jsonObject1.optString("productName", "") + " ¥" + jsonObject1.optString("productPrice", ""));

                        //根据首页底部选中状态
                        if (PreferencesHelper.getString(ConstantsME.teacherProductId).equals(jsonObject1.optString("productId", ""))) {
                            tv_ensure.setText(getResources().getText(R.string.apply_rewrite));
                            radioButton.setChecked(true);
                            position2 = i;
                            tv_location.setText(jsonObject1.optString("areaName", "场地位置"));
                            tv_navigation.setVisibility(View.VISIBLE);
                            JSONObject product = arrayProduct.optJSONObject(i);
                            if (product != null) {
                                initProductAttrs(product.toString());
                            }
                        }
                        //保存的选中状态
                        if (chooseType == 1 && position2 == i) {
                            tv_ensure.setText(getResources().getText(R.string.apply_rewrite));
                            radioButton.setChecked(true);
                            tv_location.setText(jsonObject1.optString("areaName", "场地位置"));
                            tv_navigation.setVisibility(View.VISIBLE);
                            JSONObject product = arrayProduct.optJSONObject(i);
                            if (product != null) {
                                initProductAttrs(product.toString());
                            }
                        }
                        radioButton.setLayoutParams(params);
                        rg_c2.addView(radioButton);
                        ListItem item = new ListItem();
                        item.group1Id = jsonObject1.optString("productId", "");
                        item.group1ProductName1 = jsonObject1.optString("productName", "");
                        item.group1ProductPrice1 = jsonObject1.optString("productPrice", "");
                        item.group1Address = jsonObject1.optString("areaName", "");
                        item.group1Longitude = Double.parseDouble(jsonObject1.optString("longitude", "0"));
                        item.group1Latitude = Double.parseDouble(jsonObject1.optString("latitude", "0"));
                        item.group1Nick = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teacherName", "教练");
                        item.group1Star = Float.parseFloat(VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "starLevel", "0"));
                        item.group1IconUrl = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "photoPath", "").split(";")[0];
                        JSONObject product = arrayProduct.optJSONObject(i);
                        if (product != null) {
                            item.group1Attrs = product.toString();
                        }
                        item2.mdatas.add(item);
                    }
                }
            }
            hadAdded1 = true;
        }
        JSONObject jsonObjectComment = VolleyUtilsTemp.optJSONObject(VolleyUtilsTemp.dataBodyJSONObject, "commentNumAndAvgStar");
        JSONArray arrayComments = VolleyUtilsTemp.optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "comments");
        if (null != arrayComments && arrayComments.length() > 0) {
            int length = arrayComments.length();
            final List<ListItem> list = new ArrayList<>();
            for (int i = 0; i < (length > labelLenth ? labelLenth : length); i++) {
                JSONObject jsonObject = arrayComments.optJSONObject(i);
                ListItem item = new ListItem();
                item.group1IconUrl = jsonObject.optString("photo_path", "");
                item.group1Nick = jsonObject.optString("student_name", "");
                item.group1Star = Float.parseFloat(jsonObject.optString("star", "0"));
                item.group1ProductName1 = jsonObject.optString("content", "");
                item.title = jsonObject.optString("cmt_time", "");
                list.add(item);
            }
            if (null != list && list.size() > 0) {
                CommonAdapter adapter = new CommonAdapter(CoachDetailActivity.this, list, R.layout.listitem_comment) {
                    @Override
                    public View getListItemview(ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                        if (position != list.size() - 1) {
                            viewHolder.getView(R.id.view_hide).setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.getView(R.id.view_hide).setVisibility(View.INVISIBLE);
                        }
                        RatingBar ratingbar = viewHolder.getView(R.id.ratingbar);
                        ratingbar.setRating(item.group1Star);
                        try {
                            imageLoader2.displayImage(item.group1IconUrl.split(";")[0],
                                    (CustomRoundImageView_new) viewHolder.getView(R.id.iv_icon), ImageLoaderUtils_nostra13.getFadeOptions(
                                            R.drawable.bg_default_icon,
                                            R.drawable.bg_default_icon,
                                            R.drawable.bg_default_icon));
                        } catch (Exception e) {

                        }
                        viewHolder.setText(R.id.tv_title,
                                "".equals(item.group1Nick) ? "****" : item.group1Nick);
                        viewHolder.setText(R.id.tv_date,
                                "".equals(item.title) ? "评论日期未知" : item.title);
                        viewHolder.setText(R.id.tv_content,
                                "".equals(item.group1ProductName1) ? "****" : item.group1ProductName1);

                        return null;
                    }
                };
                lv_comments.setAdapter(adapter);
            }
        }
        if (jsonObjectComment != null) {
            int commentNum = jsonObjectComment.optInt("commentNum", 0);
            if (commentNum >labelLenth) {
                tv_more_comments.setVisibility(View.VISIBLE);
            } else {
                tv_more_comments.setVisibility(View.GONE);
            }
        } else {
            tv_more_comments.setVisibility(View.GONE);
        }
    }

    private void stringRequestSignUp(final ListItem item) {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(teacherId)) {
            PicToastUtil.showPicToast(this, "未获取教练信息");
            return;
        }
        if (getIntent().getBooleanExtra(ConstantsME.fromMyCoach, false) || getIntent().getBooleanExtra(ConstantsME.fromBottomBar, false) || getResources().getText(R.string.apply_rewrite).equals(tv_ensure.getText().toString())) {
            CustomProgressDialogUtils.showProcessDialog(CoachDetailActivity.this, "正在修改报名信息...");
        } else {
            CustomProgressDialogUtils.showProcessDialog(CoachDetailActivity.this, "正在报名...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));
            jsonObjectData.put("teacherId", teacherId);
            jsonObjectData.put("productId", item.group1Id);
            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestSignUp:" + jsonObject);
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.signUp,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestSignUp:" + response);
                CustomProgressDialogUtils.dismissProcessDialog();
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    Intent intentWebview = new Intent(CoachDetailActivity.this,
                            ApplyStateActivity.class);
                    PreferencesHelper.putString(ConstantsME.teacherName, item.group1Nick);
                    PreferencesHelper.putString(ConstantsME.teacherType, item.group1C1);
                    PreferencesHelper.putString(ConstantsME.teacherProductName, item.group1ProductName1);
                    PreferencesHelper.putString(ConstantsME.teacherFee, item.group1ProductPrice1);
                    PreferencesHelper.putString(ConstantsME.teacherUrl, item.group1IconUrl);
                    PreferencesHelper.putString(ConstantsME.teacherProductId, item.group1Id);
                    PreferencesHelper.putBoolean(ConstantsME.showBottomBar, true);
                    PreferencesHelper.putString(ConstantsME.teacherId, teacherId);

                    intentWebview.putExtra("teacherId", !TextUtils.isEmpty(getIntent().getStringExtra("id")) ? getIntent().getStringExtra("id") : teacherId);
                    intentWebview.putExtra("item", item);
                    startActivityForResult(intentWebview, 10085);
                    overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else if (Configs.reLogin.equals(VolleyUtilsTemp.flag)) {
                    ReLoginUtils.newInstance().reLogin(CoachDetailActivity.this, 0);
                } else if (!TextUtils.isEmpty(VolleyUtilsTemp.msg)) {
                    PicToastUtil.showPicToast(CoachDetailActivity.this, VolleyUtilsTemp.msg);
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
                Configs.signUp);
    }

    private void initProductAttrs(String str) {
        try {
            JSONObject product = new JSONObject(str);
            if (null != product) {
                linear_add_attr.setVisibility(View.GONE);
                linear_add_attr.removeAllViews();
                JSONArray jsonArrayAttrs = product.optJSONArray("attrs");
                if (null != jsonArrayAttrs && jsonArrayAttrs.length() > 0) {
                    int lenth = jsonArrayAttrs.length();
                    for (int q = 0; q < lenth; q++) {
                        JSONObject jsonObjectAttrs = jsonArrayAttrs.optJSONObject(q);
                        String attrName = jsonObjectAttrs.optString("attrName");
                        JSONArray attrValue = jsonObjectAttrs.optJSONArray("attrValue");
                        if (attrValue != null && attrValue.length() > 0) {
                            int length1 = attrValue.length();
                            if (!TextUtils.isEmpty(attrName)) {
                                View inflate1 = LayoutInflater.from(CoachDetailActivity.this).inflate(R.layout.inflater_textview1, null);
                                TextView textView1 = (TextView) inflate1.findViewById(R.id.tv_textview1);
                                textView1.setText(attrName);
                                linear_add_attr.addView(inflate1);
                                for (int w = 0; w < length1; w++) {
                                    View inflate2 = LayoutInflater.from(CoachDetailActivity.this).inflate(R.layout.inflater_textview2, null);
                                    TextView textView2 = (TextView) inflate2.findViewById(R.id.tv_textview2);
                                    String s = attrValue.optString(w);
                                    textView2.setText(s);
                                    linear_add_attr.addView(inflate2);
                                }
                            }
                        }
                    }
                }
                linear_add_attr.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PopupWindow popwindow = null;

    private void showPop(Context context, final ListItem item) {
        // TODO Auto-generated method stub
        popwindow = null;
        View viewPop = LayoutInflater.from(this).inflate(
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
        final Animation animation_n = AnimationUtils.loadAnimation(this,
                R.anim.second_mark_rotate_n);
        // 以下两个属性设置位移动画的停止
        animation_n.setFillEnabled(true);
        animation_n.setFillAfter(true);
        // 加载动画
        // 以下两个属性设置位移动画的停止
//         animation_h.setFillEnabled(true);
//         animation_h.setFillAfter(true);
//         TextView tv_1=view_pop_1.findViewById(R.id.);
//         Animation animation = AnimationUtils.loadAnimation(context,
//         R.anim.myset_show_pop);
//         animation.setFillAfter(false);
//         lv.setAnimationCacheEnabled(false);
//         lv.setAnimation(animation);
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub

            }
        });
        TextView tvEnsure = (TextView) viewPop.findViewById(R.id.tv_ensure);
        TextView tvCancel = (TextView) viewPop.findViewById(R.id.tv_cancel);
        tvEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindow.dismiss();
                stringRequestSignUp(item);
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ShareApplication.share.removeActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Y.y("教练详情onActivityResult：" + requestCode + "   " + resultCode + "     " + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10086) {
                //relogin
                stringRequestCoachDetail(10086);
            } else if (requestCode == 10085) {
                //applySuccess
                finish();
                ShareApplication.share.removeActivity(this);
            } else if (requestCode == 10087) {
                //complaint
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PreferencesHelper.getString(ConstantsME.servicePhone)));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_CALL);
                    return;
                }
                startActivity(intentCall);
                overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
            }
        }
    }

    @Override
    public void loginSuccess(int requestCode) {
        if (requestCode == 10086) {
            stringRequestApplyState(10086);
        } else if (requestCode == 1) {
            stringRequestApplyState(0);
        } else {
            ListItem item;
            if (chooseType == 1) {
                item = (ListItem) item2.mdatas.get(position2);
                item.group1C1 = "C2";
            } else {
                item = (ListItem) item1.mdatas.get(position1);
                item.group1C1 = "C1";
            }
            if (chooseType == 1 && !"".equals(PreferencesHelper.getString(ConstantsME.teacherProductId)) &&
                    ((ListItem) item2.mdatas.get(position2)).group1Id.
                            equals(PreferencesHelper.getString(ConstantsME.teacherProductId))) {
                PicToastUtil.showPicToast(this, "产品相同，无需更改");
            } else if (chooseType == 0 && !"".equals(PreferencesHelper.getString(ConstantsME.teacherProductId)) &&
                    ((ListItem) item1.mdatas.get(position1)).group1Id.
                            equals(PreferencesHelper.getString(ConstantsME.teacherProductId))) {
                PicToastUtil.showPicToast(this, "产品相同，无需更改");
            } else {
                stringRequestSignUp(item);
            }
        }
    }

    @Override
    public void loginFail(int reqestCode) {

    }
}
