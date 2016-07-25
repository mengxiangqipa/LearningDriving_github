package com.gooddream.learningdriving.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.customviews.CustomFlowLayout;
import com.my.customviews.OverScrollView;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.PicToastUtil;
import com.my.utils.PreferencesHelper;
import com.my.utils.ReLoginUtils;
import com.my.utils.Utils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ComplaintActivity extends BaseActivity implements ReLoginUtils.CallBack_ReLogin, CustomFlowLayout.OnCheckedChangeListener_ {
    private EditText et_content;
    private CheckBox checkbox;
    OverScrollView overScrollView;

    CustomFlowLayout flowLayout;
    boolean[] labelCheckedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complaint);
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
        stringRequestLabel();
    }

    private boolean firstSuccess = false;

    private void initEvent() {
        overScrollView.setOverScrollTinyListener(new OverScrollView.OverScrollTinyListener() {
            @Override
            public void scrollDistance(int tinyDistance, int totalDistance) {
                Utils.isCloseSoftInputMethod(ComplaintActivity.this, et_content, true);
            }

            @Override
            public void scrollLoosen() {
                if (!firstSuccess) {
                    stringRequestLabel();
                }
            }
        });
    }

    private void initView() {
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tv_title)).setText("一键投诉");

        overScrollView = (OverScrollView) findViewById(R.id.overScrollView);

        et_content = (EditText) findViewById(R.id.et_content);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        flowLayout = (CustomFlowLayout) findViewById(R.id.flowLayout);
    }

    @Override
    public void loginSuccess(int requestCode) {
        stringRequestComplaint();
    }

    @Override
    public void loginFail(int requestCode) {

    }

    boolean hadAdded2 = false;
    String[] labelIdList;

    private void stringRequestLabel() {
        // TODO Auto-generated method stub
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

//            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));
//            jsonObjectData.put("teacherId", "");

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestApplyState:" + jsonObject);
        JSONObject jsonObject1 = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.complaintLabel + "?jsonRequest=" + jsonObject1.toString(),
                null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestLabel:" + response);
                VolleyUtilsTemp.parseJson(response);
                CustomProgressDialogUtils.dismissProcessDialog();
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    firstSuccess = true;
                    JSONArray jsonArray = VolleyUtilsTemp.optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "labels");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        int len = jsonArray.length();
                        String[] labelList = new String[len];
                        labelIdList = new String[len];
                        for (int i = 0; i < len; i++) {
                            JSONObject jsonObject2 = jsonArray.optJSONObject(i);
                            if (null != jsonObject2) {
                                labelList[i] = jsonObject2.optString("labelName", "");
                                labelIdList[i] = jsonObject2.optString("labelId", "");
                            }
                        }
                        if (flowLayout.getChildCount() <= 0) {
                            labelCheckedList = new boolean[len];
                            View[] views = new View[len];
                            int _8dp = Utils.dip2px(ComplaintActivity.this, 8);
                            int _5dp = Utils.dip2px(ComplaintActivity.this, 5);
                            if (len > 0 && !hadAdded2) {
                                for (int i = 0; i < len; i++) {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.rightMargin = _8dp;
                                    params.topMargin = _5dp;
                                    params.bottomMargin = _5dp;
                                    params.leftMargin = _8dp;
                                    CheckBox checkBox = (CheckBox) LayoutInflater.from(ComplaintActivity.this).inflate(R.layout.inflater_flowlabel_checkbox2, null);
                                    checkBox.setText(labelList[i]);
                                    checkbox.setTag(labelIdList[i]);
                                    checkBox.setLayoutParams(params);
                                    checkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                    views[i] = checkBox;
                                }
                                flowLayout.addData(views, true);
                                hadAdded2 = true;
                            }
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
                Configs.complaintLabel);
    }

    private void stringRequestComplaint() {
        // TODO Auto-generated method stub
        CustomProgressDialogUtils.showProcessDialog(ComplaintActivity.this, "正在投诉教练...");
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));
            jsonObjectData.put("teacherId", getIntent().getStringExtra("id"));
            jsonObjectData.put("cmtIdentity", checkbox.isChecked() ? "1" : "0");
            int len = labelIdList.length;
            JSONArray array = new JSONArray();
            for (int i = 0; i < len; i++) {
                if (labelCheckedList[i]) {
                    array.put(labelIdList[i]);
                }
            }
            jsonObjectData.put("reason", array);
            jsonObjectData.put("content", et_content.getText().toString());

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Y.y("stringRequestComplaint:" + jsonObject);
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL + Configs.complaint,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.i("yy", "stringRequestComplaint:" + response);
                CustomProgressDialogUtils.dismissProcessDialog();
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    PicToastUtil.showPicToast(ComplaintActivity.this, "投诉成功，我们将尽快处理你的投诉");
                    setResult(RESULT_OK, getIntent());
                    finish();
                    ShareApplication.share.removeActivity(ComplaintActivity.this);
                } else if (Configs.reLogin.equals(VolleyUtilsTemp.flag)) {
                    ReLoginUtils.newInstance().reLogin(ComplaintActivity.this);
                } else {
                    PicToastUtil.showPicToast(ComplaintActivity.this, TextUtils.isEmpty(VolleyUtilsTemp.msg) ? "投诉异常" : VolleyUtilsTemp.msg);
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
                Configs.complaint);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_sure:
                break;
            case R.id.relative_back:
                ShareApplication.share.removeActivity(this);
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;
            case R.id.tv_ensure:
                if (labelCheckedList == null) {
                    Utils.showToast("未获取标签信息，正在重新获取...");
                    stringRequestLabel();
                } else {
                    boolean hasChoosed = false;
                    for (boolean checked : labelCheckedList) {
                        if (checked) {
                            hasChoosed = true;
                        }
                    }
                    if (!hasChoosed) {
                        Utils.showToast("请选择投诉原因！");
                    } else if (TextUtils.isEmpty(et_content.getText())) {
                        Utils.showToast("请输入投诉内容!");
                    } else {
                        stringRequestComplaint();
                    }
//                showPop(this);
                }
                break;
            default:
                break;
        }
    }

    private PopupWindow popwindow = null;

    private void showPop(Context context) {
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
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
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
                Utils.showToast("确定");
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popwindow.dismiss();
                Utils.showToast("取消");
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
    public void onCheckedChanged_(int position, boolean b, CompoundButton compoundButton) {
        labelCheckedList[position] = b;
    }
}
