package com.gooddream.learningdriving.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.ConstantsME;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.pulltorefresh.fragment_part.Fragment_login;
import com.my.pulltorefresh.fragment_part.Fragment_register;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.Y;

import java.util.ArrayList;

public class LoginDialogActivity extends BaseActivity implements View.OnClickListener {
    Fragment fragment_1;
    Fragment fragment_2;
    Animation animation = null;
    ImageView cursor;
    RelativeLayout relative_1, relative_2;
    int num = 2;
    ///////////////////////////////
    TextView tvLogin, tvRegister;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inflater_login_dialog);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.trans));
        initView();
        initWidth();
        initEvent();
        PreferencesHelper.putInt(ConstantsME.goodDream_login_position, 0);
        if (null == fragment_1) {
            fragment_1 = new Fragment_login();
        }
        if (null == fragment_2) {
            fragment_2 = new Fragment_register();
        }
        oldposition = 0;
        ArrayList<Fragment> listFragments = new ArrayList<Fragment>();
        Fragment_login fragment_1 = new Fragment_login();
        Fragment_register fragment_2 = new Fragment_register();
        listFragments.add(fragment_1);
        listFragments.add(fragment_2);
        // TODO Auto-generated method stub
        GuideFragmentPagerAdapter adapter = new GuideFragmentPagerAdapter(
                getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(adapter);
        ShareApplication.share.addActivity(this);
    }

    private void initView() {
        // TODO Auto-generated method stub
        cursor = (ImageView) findViewById(R.id.cursor);
        relative_1 = (RelativeLayout) findViewById(R.id.relative_1);
        relative_2 = (RelativeLayout) findViewById(R.id.relative_2);
        relative_1.setOnClickListener(this);
        relative_2.setOnClickListener(this);

        tvLogin = (TextView) findViewById(R.id.tv_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    int oldposition = -1;
    int currentPostion = 0;

    private void initEvent() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                moveCursor(position, oldposition);
                PreferencesHelper.putInt(ConstantsME.goodDream_login_position, position);
                if (0 == PreferencesHelper.getInt(ConstantsME.goodDream_login_position)) {
                    tvLogin.setTextColor(getResources().getColor(R.color.gooddream_blue));
                    tvRegister.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
                } else {
                    tvLogin.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
                    tvRegister.setTextColor(getResources().getColor(R.color.gooddream_blue));
                }
                oldposition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    int[] arrPosition;
    int position_zero, position_one;

    public void moveCursor(int position, int oldposition) {
        animation = new TranslateAnimation(arrPosition[oldposition],
                arrPosition[position], 0, 0);
        if (null != animation) {
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }
    }

    private void initWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int screenW = Utils.getScreenWidth(this) - Utils.dip2px(this, 100);
        int cursorLenth = screenW / num;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                cursorLenth, Utils.dip2px(this, 2));
        Y.y("cursor==null:" + (cursor == null));
        Y.y("params==null:" + (params == null));
        cursor.setLayoutParams(params);
        position_zero = 0;
        position_one = cursorLenth;
        arrPosition = new int[]{position_zero, position_one};
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, screenW / num);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.relative_1:
                currentPostion = 0;
                PreferencesHelper.putInt(ConstantsME.goodDream_login_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                Y.y("currentPostion:" + currentPostion);
                Y.y("null == fragment_1:" + (null == fragment_1));
                // 约会
                if (null == fragment_1) {
                    fragment_1 = new Fragment_login();
                }
                tvLogin.setTextColor(getResources().getColor(R.color.gooddream_blue));
                tvRegister.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
                viewPager.setCurrentItem(0);
//                changeFragment(fragment_1, currentPostion);
                break;
            case R.id.relative_2:
                currentPostion = 1;
                PreferencesHelper.putInt(ConstantsME.goodDream_login_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                Y.y("currentPostion:" + currentPostion);
                Y.y("null == fragment_2:" + (null == fragment_2));
                if (null == fragment_2) {
                    fragment_2 = new Fragment_register();
                }
                tvLogin.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
                tvRegister.setTextColor(getResources().getColor(R.color.gooddream_blue));
                viewPager.setCurrentItem(1);
//                changeFragment(fragment_2, currentPostion);
                break;
            case R.id.linear_root:
                finish();
                ShareApplication.share.removeActivity(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        moveCursor(PreferencesHelper.getInt(ConstantsME.goodDream_login_position),
                PreferencesHelper.getInt(ConstantsME.goodDream_login_position));
        if (0 == PreferencesHelper.getInt(ConstantsME.goodDream_login_position)) {
            tvLogin.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tvRegister.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
        } else {
            tvLogin.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tvRegister.setTextColor(getResources().getColor(R.color.gooddream_blue));
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ShareApplication.share.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            ShareApplication.share.removeActivity(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
