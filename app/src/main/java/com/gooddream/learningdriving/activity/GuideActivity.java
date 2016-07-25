package com.gooddream.learningdriving.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.ConstantsME;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.pulltorefresh.fragment_part.Fragment_guide_1;
import com.my.pulltorefresh.fragment_part.Fragment_guide_2;
import com.my.pulltorefresh.fragment_part.Fragment_guide_3;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.Y;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;


/**
 * @author Administrator 引导页
 */
public class GuideActivity extends FragmentActivity {
    String TAG = "GuideActivity";
    ViewPager viewPager;
    TextView tv_dot_dynamic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (PreferencesHelper.getBoolean(ConstantsME.hasGuide)) {
                startActivity(new Intent(this, SplashActivity.class));
                finish();
                ShareApplication.share.removeActivity(this);
            }
            setContentView(R.layout.guide);
            initView();
            initData();
        } catch (Exception e) {
        }
    }

    int count = 0;
    int currentPosition = 0;
    int oldPosition = 0;

    private void initData() {
        Y.y("guide:1");
        ArrayList<Fragment> listFragments = new ArrayList<Fragment>();
        Y.y("guide:2");
        Fragment_guide_1 fragment_guide_1 = new Fragment_guide_1();
        Y.y("guide:3");
        Fragment_guide_2 fragment_guide_2 = new Fragment_guide_2();
        Y.y("guide:4");
        Fragment_guide_3 fragment_guide_3 = new Fragment_guide_3();
        Y.y("guide:5");
        listFragments.add(fragment_guide_1);
        Y.y("guide:6");
        listFragments.add(fragment_guide_2);
        Y.y("guide:7");
        listFragments.add(fragment_guide_3);
        Y.y("guide:8");
        // TODO Auto-generated method stub
        GuideFragmentPagerAdapter adapter = new GuideFragmentPagerAdapter(
                getSupportFragmentManager(), listFragments);
//		viewPager.setPageTransformer(true, new MyPageTransformer());
        Y.y("guide:9");
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        final int screenWidth = Utils.getScreenWidth(this);
        final int _21dp = Utils.dip2px(this, 21);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Y.y("onPageScrolled:" + position + "   " + positionOffsetPixels + "    " + positionOffset + "          " + ViewHelper.getTranslationX(tv_dot_dynamic));
                if (currentPosition == position && oldPosition == position) {
                    ViewHelper.setTranslationX(tv_dot_dynamic, currentPosition * _21dp + positionOffsetPixels * _21dp / screenWidth);
                }
                oldPosition = currentPosition;//最后一次位置
                currentPosition = position;
                if (currentPosition == 2 && positionOffsetPixels == 0) {
                    count++;
                } else {
                    count = 0;
                }
                if (count >= 3) {
                    PreferencesHelper.putBoolean(ConstantsME.hasGuide, true);
                    startActivity(new Intent(GuideActivity.this, SplashActivity.class));
                    finish();
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        Y.y("guide:10");
    }


    private void initView() {
        // TODO Auto-generated method stub
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tv_dot_dynamic = (TextView) findViewById(R.id.tv_dot_dynamic);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//		MobclickAgent.onPageStart("GuideActivity");
//		MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//		MobclickAgent.onPageEnd("GuideActivity");
//		MobclickAgent.onPause(this);
    }
}
