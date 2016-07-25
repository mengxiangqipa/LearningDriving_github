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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.my.configs.ConstantsME;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.pulltorefresh.fragment_part.Fragment_applystate1;
import com.my.pulltorefresh.fragment_part.Fragment_applystate2;
import com.my.pulltorefresh.fragment_part.Fragment_applystate3;
import com.my.pulltorefresh.fragment_part.Fragment_login;
import com.my.pulltorefresh.fragment_part.Fragment_register;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.Y;

import java.util.ArrayList;

public class ApplyStateActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    Animation animation = null;
    ImageView cursor;
    RelativeLayout relative_1, relative_2, relative_3;
    TextView tv_1, tv_2, tv_3;
    int num = 3;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_state);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        PreferencesHelper.putInt(ConstantsME.goodDream_applyState_position, 0);
        initView();
        initWidth();
        initData();
        initEvent();
        ShareApplication.share.addActivity(this);
    }

    ListItem item;

    private void initData() {
        // TODO Auto-generated method stub

        ArrayList<Fragment> listFragments = new ArrayList<Fragment>();
        Fragment_applystate1 fragment_1 = new Fragment_applystate1();
        Fragment fragment_2 = new Fragment_applystate2();
        Fragment fragment_3 = new Fragment_applystate3();
        Bundle bundle = new Bundle();
        item = (ListItem) getIntent().getSerializableExtra("item");
        bundle.putSerializable("item", getIntent().getSerializableExtra("item"));
        fragment_1.setArguments(bundle);
        listFragments.add(fragment_1);
        listFragments.add(fragment_2);
        listFragments.add(fragment_3);
        // TODO Auto-generated method stub
        GuideFragmentPagerAdapter adapter = new GuideFragmentPagerAdapter(
                getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(adapter);
    }

    private void initEvent() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    viewPager.setCurrentItem(oldposition);
                    return;
                }
                moveCursor(position, oldposition);
                PreferencesHelper.putInt(ConstantsME.goodDream_applyState_position, position);
                showTxtColor();
                oldposition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tv_title)).setText("报名状态");

        relative_1 = (RelativeLayout) findViewById(R.id.relative_1);
        relative_2 = (RelativeLayout) findViewById(R.id.relative_2);
        relative_3 = (RelativeLayout) findViewById(R.id.relative_3);

        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);

        cursor = (ImageView) findViewById(R.id.cursor);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

    }

    private void initWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int screenW = Utils.getScreenWidth(this);
        int cursorLenth = screenW / num;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                cursorLenth, Utils.dip2px(this, 2));
        Y.y("cursor==null:" + (cursor == null));
        Y.y("params==null:" + (params == null));
        cursor.setLayoutParams(params);
        position_zero = cursorLenth * 0;
        position_one = cursorLenth * 1;
        position_two = cursorLenth * 2;
        arrPosition = new int[]{position_zero, position_one, position_two};
    }

    int[] arrPosition;
    int position_zero, position_one, position_two;
    int oldposition = 0;
    int currentPostion = 0;

    public void moveCursor(int position, int oldposition) {
        animation = new TranslateAnimation(arrPosition[oldposition],
                arrPosition[position], 0, 0);
        if (null != animation) {
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }
    }

    private void showTxtColor() {
        if (0 == PreferencesHelper.getInt(ConstantsME.goodDream_applyState_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
        } else if (1 == PreferencesHelper.getInt(ConstantsME.goodDream_applyState_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
        } else if (2 == PreferencesHelper.getInt(ConstantsME.goodDream_applyState_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        moveCursor(PreferencesHelper.getInt(ConstantsME.goodDream_applyState_position),
                PreferencesHelper.getInt(ConstantsME.goodDream_applyState_position));
        showTxtColor();
    }

    private int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private int PERMISSION_LOCATION_REQUEST_CODE = 2;
    private int PERMISSION_RECORD_REQUEST_CODE = 3;
    private int PERMISSION_CALL = 4;

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.relative_back:
                PreferencesHelper.putString(ConstantsME.teacherName, item.group1Nick);
                PreferencesHelper.putString(ConstantsME.teacherType, item.group1C1);
                PreferencesHelper.putString(ConstantsME.teacherProductName, item.group1ProductName1);
                PreferencesHelper.putString(ConstantsME.teacherFee, item.group1ProductPrice1);
                PreferencesHelper.putString(ConstantsME.teacherUrl, item.group1IconUrl);
                PreferencesHelper.putBoolean(ConstantsME.showBottomBar, true);
                PreferencesHelper.putString(ConstantsME.teacherId, getIntent().getStringExtra("teacherId"));
                setResult(RESULT_OK, getIntent());
                finish();
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
                    int permissionCheck1 = ContextCompat.checkSelfPermission(ApplyStateActivity.this, Manifest.permission.CALL_PHONE);
                    if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ApplyStateActivity.this,
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
            case R.id.tv_rewrite_type:
                PreferencesHelper.putString(ConstantsME.teacherName, item.group1Nick);
                PreferencesHelper.putString(ConstantsME.teacherType, item.group1C1);
                PreferencesHelper.putString(ConstantsME.teacherProductName, item.group1ProductName1);
                PreferencesHelper.putString(ConstantsME.teacherFee, item.group1ProductPrice1);
                PreferencesHelper.putString(ConstantsME.teacherUrl, item.group1IconUrl);
                PreferencesHelper.putBoolean(ConstantsME.showBottomBar, true);
                PreferencesHelper.putString(ConstantsME.teacherId, getIntent().getStringExtra("teacherId"));
                setResult(RESULT_OK, getIntent());
                finish();
                break;
            case R.id.tv_call_vehicle2:
                PreferencesHelper.putInt(ConstantsME.goodDream_applyState_position, 1);
                oldposition = currentPostion;
                currentPostion = 1;
                moveCursor(currentPostion, oldposition);
                showTxtColor();
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_call_vehicle:
                //呼叫报名车
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
                    int permissionCheck1 = ContextCompat.checkSelfPermission(ApplyStateActivity.this, Manifest.permission.CALL_PHONE);
                    if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(ApplyStateActivity.this,
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
            case R.id.tv_ensure:
                showPop(this);
                break;
            case R.id.relative_1:
                currentPostion = 0;
                PreferencesHelper.putInt(ConstantsME.goodDream_applyState_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                viewPager.setCurrentItem(0);
                showTxtColor();
                break;
            case R.id.relative_2:
                currentPostion = 1;
                PreferencesHelper.putInt(ConstantsME.goodDream_applyState_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                viewPager.setCurrentItem(1);
                showTxtColor();
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PreferencesHelper.putString(ConstantsME.teacherName, item.group1Nick);
        PreferencesHelper.putString(ConstantsME.teacherType, item.group1C1);
        PreferencesHelper.putString(ConstantsME.teacherProductName, item.group1ProductName1);
        PreferencesHelper.putString(ConstantsME.teacherFee, item.group1ProductPrice1);
        PreferencesHelper.putString(ConstantsME.teacherUrl, item.group1IconUrl);
        PreferencesHelper.putBoolean(ConstantsME.showBottomBar, true);
        PreferencesHelper.putString(ConstantsME.teacherId, getIntent().getStringExtra("teacherId"));
        setResult(RESULT_OK, getIntent());
        finish();
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
        PreferencesHelper.putBoolean(ConstantsME.showBottomBar, true);
        ShareApplication.share.removeActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
