package com.gooddream.learningdriving.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.interfaces.CallBack_SignUp;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.customviews.MyRadioGroupForAll;
import com.my.pulltorefresh.fragment_part.Fragment_choose_coach_pull2refresh_expanable;
import com.my.pulltorefresh.fragment_part.Fragment_homepage_1;
import com.my.pulltorefresh.fragment_part.Fragment_homepage_2;
import com.my.pulltorefresh.fragment_part.Fragment_homepage_3;
import com.my.pulltorefresh.fragment_part.Fragment_homepage_4;
import com.my.pulltorefresh.fragment_part.Fragment_homepage_4_personal;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.DownLoadManagerUtils;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HomepageActivity extends AppCompatActivity implements CallBack_SignUp, ActivityCompat.OnRequestPermissionsResultCallback {
    //////////////////////
    MyRadioGroupForAll rg;
    long exitTime;
    int oldposition = -1;
    Fragment findresult = null;
    RadioButton rb_1, rb_2, rb_3, rb_4;
    // /////
    private Fragment Center_mContent;
    Fragment fragment_1;
    private Fragment fragment_2, fragment_3;
    Fragment fragment_4;
    private String verName, newVer, content, downloadUrl;
    LoginsuccessBroadCastReceiver receiver;
    public static HomepageActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.gooddream_black));
        setContentView(R.layout.homepage);
        instance = this;
        initBroadcast();
        initView();
        initData();
        initEvent();
        if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL))) {
            shouldRefresh = true;
        }
        initFragment1(false);

        ShareApplication.share.addActivity(this);


        PreferencesHelper.putInt("androidCode", 34);
        PreferencesHelper.putString(ConstantsME.appVersion, "2.3.4");
        PreferencesHelper.putString("versionData", "更新一些东西");
        stringRequestUpdate();
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomepageActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION);
        }
    }

    private void initFragment1(boolean mustDo) {
        if (fragment_1 == null || mustDo) {
            JSONObject jsonObjectData = new JSONObject();
            JSONObject jsonObjectPage = new JSONObject();
            try {
                jsonObjectData.put("latitude",
                        PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL));
                jsonObjectData.put("longitude",
                        PreferencesHelper.getString(ConstantsME.LONGTITUDE_ORIGINAL));
                JSONObject jsonObjectAreaName = new JSONObject();
                jsonObjectAreaName.put("areaName", "");
                jsonObjectAreaName.put("productName", "");
                jsonObjectAreaName.put("projectType", "C1");
                JSONObject jsonObjectOrder = new JSONObject();
                jsonObjectOrder.put("starLevel", "desc");
//                jsonObjectOrder.put("passRate", "desc");
                jsonObjectData.put("searchFilter", jsonObjectAreaName);
                jsonObjectData.put("order", jsonObjectOrder);

                jsonObjectPage.put("pageIndex", 0);
                jsonObjectPage.put("pageSize", 20);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment_1 = Fragment_choose_coach_pull2refresh_expanable.newInstance(Request.Method.POST, Configs.coachList, jsonObjectData, jsonObjectPage);
            switchFragment(fragment_1, 0, mustDo);
        } else {
            checkLocationPermission();
        }
    }

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    /**
     * 设置定位参数
     *
     * @param strInterval 定位间隔
     */
    private void initOption(long strInterval) {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption.setOnceLocationLatest(false);
        /**
         *  设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
         *  只有持续定位设置定位间隔才有效，单次定位无效
         */
        locationOption.setInterval(strInterval);
    }

    private void initLocation_gaode() {
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(new MyAMapLocationListener());
        initOption(30000);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    private boolean firstLocationSuccess = false;
    private boolean shouldRefresh = false;

    class MyAMapLocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Y.y("高德定位homepage_onLocationChanged:" + aMapLocation);
            if (null != aMapLocation) {
                double longitude = aMapLocation.getLongitude();
                double latitude = aMapLocation.getLatitude();
                String address = aMapLocation.getAddress();
                Y.y("高德定位homepage_onLocationChanged:" + address + " " + longitude + " " + latitude);
                if (longitude > 1 && latitude > 1) {
                    PreferencesHelper.putString(ConstantsME.LATITUDE_ORIGINAL, String.valueOf(latitude));
                    PreferencesHelper.putString(ConstantsME.LONGTITUDE_ORIGINAL, String.valueOf(longitude));
                    if (!TextUtils.isEmpty(address)) {
                        PreferencesHelper.putString(ConstantsME.ADDRESS, address);
                    }
                    if (!firstLocationSuccess && shouldRefresh) {
                        initFragment1(true);
                        firstLocationSuccess = true;
                    }
                }
            }
        }

    }

    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsME.goodDream_broadcast_loginsuccess);
        receiver = new LoginsuccessBroadCastReceiver();
        registerReceiver(receiver, filter);
    }

    public void onClick(View view) {

    }

    private void stringRequestUpdate() {
        // TODO Auto-generated method stub
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(this));
            JSONObject jsonObjectData = new JSONObject();

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject jsonObject1 = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.update + "?jsonRequest=" + jsonObject1.toString(),
                null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("stringRequestUpdate:" + response);
                VolleyUtilsTemp.parseJson(response);
                CustomProgressDialogUtils.dismissProcessDialog();
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray updateItem = VolleyUtilsTemp.optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "updateItem");
                    String downLoadUrl = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "downLoadUrl");
                    String versionDate = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "versionDate");
                    String version = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "version");
                    int versionCode = VolleyUtilsTemp.optInt(VolleyUtilsTemp.dataBodyJSONObject, "versionCode");
                    String[] content = null;
                    if (null != updateItem && updateItem.length() > 0) {
                        int len = updateItem.length();
                        content = new String[len];
                        for (int i = 0; i < len; i++) {
                            content[i] = updateItem.optString(i);
                        }
                    }
                    update(3, version, downLoadUrl, content);
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
                Configs.applyState);
    }

    private void update(final int serverCode, final String newVer, String downloadUrl, final String[] content) {
        try {
            Y.y("版本更新:1");
            final int localCode = Utils.getVersionCode(this);
            Y.y("localCode:" + localCode);
            Y.y("版本更新:4");
            if ((serverCode > localCode && localCode != 0)) {
                verName = Utils.getVersion(this);
                Y.y("verName:" + verName);
                Y.y("content:" + content);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if ((localCode < serverCode)) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("●当前版本:");
                            sb.append(verName);
                            sb.append("\n\n●发现新版本:");
                            sb.append(newVer);
                            sb.append("\n\n●描述：");
                            if (content != null && content.length > 0) {
                                int len = content.length;
                                for (int i = 0; i < len; i++) {
                                    if (!TextUtils.isEmpty(content[i])) {
                                        if (i == 0) {
                                            sb.append((i + 1) + ":" + content[i] + "\n");
                                        } else {
                                            sb.append("\n" + "                  " + (i + 1) + ":" + content[i] + "\n");
                                        }
                                    }
                                }
                            }
                            sb.append("\n\n●是否更新?" + "\n");
                            showUpdateDialog(HomepageActivity.this, sb.toString(),
                                    true);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Y.y("版本更新:" + e.getMessage());
        }
    }

    public void showUpdateDialog(final Context context, String descContent,
                                 final boolean isUpdate) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.update_layout, null);
        final AlertDialog mDialog = new AlertDialog.Builder(context).create();
        TextView desc = (TextView) view.findViewById(R.id.desc);
        TextView tvSure = (TextView) view.findViewById(R.id.tv_ensure);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        desc.setText(descContent);
        tvSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isUpdate) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        PreferencesHelper.putString(
                                "fileName",
                                getResources().getString(R.string.app_name)
                                        + PreferencesHelper.getString(ConstantsME.appVersion) + ".apk");
                        DownLoadManagerUtils.newInstance(context).requestDownLoad(
                                downloadUrl,
                                getResources().getString(R.string.app_name)
                                        + PreferencesHelper.getString(ConstantsME.appVersion) + ".apk",
                                getResources().getString(R.string.app_name) + "新版本"
                                        + PreferencesHelper.getString(ConstantsME.appVersion));
                        mDialog.dismiss();
                    } else {
                        int permissionCheck1 = ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        int permissionCheck2 = ContextCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                            //申请WRITE_EXTERNAL_STORAGE权限
                            mDialog.dismiss();
                            ActivityCompat.requestPermissions(HomepageActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                        } else {
                            PreferencesHelper.putString(
                                    "fileName",
                                    getResources().getString(R.string.app_name)
                                            + PreferencesHelper.getString(ConstantsME.appVersion) + ".apk");
                            DownLoadManagerUtils.newInstance(context).requestDownLoad(
                                    downloadUrl,
                                    getResources().getString(R.string.app_name)
                                            + PreferencesHelper.getString(ConstantsME.appVersion) + ".apk",
                                    getResources().getString(R.string.app_name) + "新版本"
                                            + PreferencesHelper.getString(ConstantsME.appVersion));
                            mDialog.dismiss();
                        }
                    }
                    // finish();
                } else {
                    mDialog.dismiss();
                }
            }
        });
        if (!isUpdate)
            tvCancel.setVisibility(View.GONE);
        else
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    // finish();
                }
            });
//        view.setPadding(32, 0, 32, 0);
        mDialog.show();

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = Utils.getScreenWidth(this);
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.BOTTOM);

        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
    }

    private void initEvent() {
        rg.setOnCheckedChangeListener(new MyRadioGroupForAll.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroupForAll group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_1:
                        if (fragment_1 == null) {
                            JSONObject jsonObjectData = new JSONObject();
                            JSONObject jsonObjectPage = new JSONObject();
                            try {
                                jsonObjectData.put("latitude",
                                        PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL));
                                jsonObjectData.put("longitude",
                                        PreferencesHelper.getString(ConstantsME.LONGTITUDE_ORIGINAL));
                                JSONObject jsonObjectAreaName = new JSONObject();
                                jsonObjectAreaName.put("areaName", "");
                                jsonObjectAreaName.put("productName", "");
                                jsonObjectAreaName.put("projectType", "C1");
                                JSONObject jsonObjectOrder = new JSONObject();
                                jsonObjectOrder.put("starLevel", "desc");
//                jsonObjectOrder.put("passRate", "desc");
                                jsonObjectData.put("searchFilter", jsonObjectAreaName);
                                jsonObjectData.put("order", jsonObjectOrder);

                                jsonObjectPage.put("pageIndex", 0);
                                jsonObjectPage.put("pageSize", 20);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            fragment_1 = Fragment_choose_coach_pull2refresh_expanable.newInstance(Request.Method.POST, Configs.coachList, jsonObjectData, jsonObjectPage);
                        }
                        switchFragment(fragment_1, 0);
                        break;
                    case R.id.rb_2:
                        if (fragment_2 == null) {
                            fragment_2 = new Fragment_homepage_2();
                        }
                        switchFragment(fragment_2, 1);
                        break;
                    case R.id.rb_3:
                        if (fragment_3 == null) {
                            JSONObject jsonObjectData = new JSONObject();
                            JSONObject jsonObjectPage = new JSONObject();
                            try {
                                jsonObjectPage.put("pageIndex", 0);
                                jsonObjectPage.put("pageSize", 20);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            fragment_3 = Fragment_homepage_3.newInstance(Request.Method.GET, Configs.infoList, jsonObjectData, jsonObjectPage);
                        }
                        switchFragment(fragment_3, 2);
                        break;
                    case R.id.rb_4:
                        if (fragment_4 == null) {
                            if (PreferencesHelper.getBoolean(ConstantsME.LOGINED) && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
                                fragment_4 = new Fragment_homepage_4_personal();
                                ((Fragment_homepage_4_personal) fragment_4).setCallBack(HomepageActivity.this);
                            } else {
                                fragment_4 = new Fragment_homepage_4();
                            }
                        }
                        switchFragment(fragment_4, 3);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initView() {
        rg = (MyRadioGroupForAll) findViewById(R.id.rg);
        rb_1 = (RadioButton) findViewById(R.id.rb_1);
        rb_2 = (RadioButton) findViewById(R.id.rb_2);
        rb_3 = (RadioButton) findViewById(R.id.rb_3);
        rb_4 = (RadioButton) findViewById(R.id.rb_4);
    }

    private void initData() {
        Drawable[] compoundDrawables = rb_1.getCompoundDrawables();
        compoundDrawables[1].setBounds(0, 0, Utils.dip2px(this, 25), Utils.dip2px(this, 25));
        rb_1.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        compoundDrawables = rb_2.getCompoundDrawables();
        compoundDrawables[1].setBounds(0, 0, Utils.dip2px(this, 25), Utils.dip2px(this, 25));
        rb_2.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        compoundDrawables = rb_3.getCompoundDrawables();
        compoundDrawables[1].setBounds(0, 0, Utils.dip2px(this, 25), Utils.dip2px(this, 25));
        rb_3.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        compoundDrawables = rb_4.getCompoundDrawables();
        compoundDrawables[1].setBounds(0, 0, Utils.dip2px(this, 25), Utils.dip2px(this, 25));
        rb_4.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - this.exitTime > 2000L) {
                Toast.makeText(this.getApplicationContext(), this.getResources().getString(R.string.exist) + this.getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                this.exitTime = System.currentTimeMillis();
            } else {
                // 返回主界面
                //
                /**
                 * 退出登录,清空数据
                 */
                Intent intent_home = new Intent(Intent.ACTION_MAIN);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_home.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent_home);
                finish();
                System.exit(0);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void switchFragment(final Fragment fragment, final int position) {
        switchFragment(fragment, position, false);
    }

    public void switchFragment(final Fragment fragment, final int position, boolean mustReplace) {
        if (oldposition != position || mustReplace) {
            FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            findresult = getSupportFragmentManager().findFragmentByTag(
                    "viewid" + position);

            if (Center_mContent != null) {
                Y.y("detach");
                if (!mustReplace) {
                    ft.detach(Center_mContent);
                }
            }
            oldposition = position;
            if (findresult != null) {
                Y.y("attach");
                if (mustReplace || (position == 3 && fromDialogLogin)) {
                    fromDialogLogin = false;
                    Center_mContent = fragment;
                    ft.replace(R.id.relative_center_homepage, Center_mContent, "viewid"
                            + position);
                } else {
                    ft.attach(findresult);
                    Center_mContent = findresult;
                }
            } else {
                Y.y("findresult== null");
                Center_mContent = fragment;
                ft.add(R.id.relative_center_homepage, Center_mContent, "viewid"
                        + position);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void clickSignUp() {
        rg.check(R.id.rb_1);
        switchFragment(fragment_1, 0);
    }

    private boolean fromDialogLogin = false;

    class LoginsuccessBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Y.y("获取登录广播：" + intent.getBooleanExtra(ConstantsME.fromDialog, false));
            if (intent.getBooleanExtra(ConstantsME.fromDialog, false)) {
                fromDialogLogin = true;
                fragment_4 = new Fragment_homepage_4_personal();
                ((Fragment_homepage_4_personal) fragment_4).setCallBack(HomepageActivity.this);
            } else {
                fragment_4 = new Fragment_homepage_4_personal();
                ((Fragment_homepage_4_personal) fragment_4).setCallBack(HomepageActivity.this);
                switchFragment(fragment_4, 3, true);
            }
        }
    }

    private int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private int PERMISSION_LOCATION = 8;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Y.y("onRequestPermissionsResult");
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if ((grantResults.length > 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                if (0 ==
                        DownLoadManagerUtils.newInstance(HomepageActivity.this).requestDownLoad(
                                PreferencesHelper.getString("downLoadUrl"),
                                getResources().getString(R.string.app_name)
                                        + Utils.getVersion(HomepageActivity.this) + ".apk",
                                getResources().getString(R.string.app_name) + "版本2")) {
                    Utils.showToast("正在下载，请稍后");
                }
            }
        } else if (requestCode == PERMISSION_LOCATION) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initLocation_gaode();
            } else {
                Utils.showToast("未能获取定位权限，将会导致无法定位!");
            }
        }
    }

}
