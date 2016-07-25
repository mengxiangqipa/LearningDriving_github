package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.my.configs.ConstantsME;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.Y;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Fragment_homepage_4 extends Fragment implements OnClickListener {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    Fragment Center_mContent;
    Fragment fragment_1;
    Fragment fragment_2;
    Animation animation = null;
    ImageView cursor;
    RelativeLayout relative_1, relative_2;
    int num = 2;
    ///////////////////////////////
    TextView tv_title;
    TextView tvLogin, tvRegister;
    ViewPager viewPager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
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
            tv_title.setText(getResources().getText(R.string.login));
        } else {
            tvLogin.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tvRegister.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_title.setText(getResources().getText(R.string.register));
        }
    }

    // @Override
    // public void onAttach(Activity activity) {
    // // TODO Auto-generated method stub
    // super.onAttach(activity);
    // if (null == fragment_1) {
    // fragment_1 = FragmentVideo_pull2refresh.newInstance(
    // FunID.VIDEO,
    // PerferenceHelper.getStringData(ConstantsME.USERID),
    // "0", true);
    //
    // }
    // changeFragment(fragment_1, 0);
    // }
    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            // throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.fragment_login2register, null);
            initView(view);
            initWidth();
            initEvent();
            PreferencesHelper.putInt(ConstantsME.goodDream_login_position, 0);
            if (null == fragment_1) {
                fragment_1 = new Fragment_login();
            }
            if (null == fragment_2) {
                fragment_2 = new Fragment_register();
            }
            oldposition=0;
            ArrayList<Fragment> listFragments = new ArrayList<Fragment>();
            Fragment_login fragment_1 = new Fragment_login();
            Fragment_register fragment_2 = new Fragment_register();
            listFragments.add(fragment_1);
            listFragments.add(fragment_2);
            // TODO Auto-generated method stub
            GuideFragmentPagerAdapter adapter = new GuideFragmentPagerAdapter(
                    getChildFragmentManager(), listFragments);
            viewPager.setAdapter(adapter);
//            changeFragment(fragment_1, 0);
            myContainer.addView(view);
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
        return myContainer;
    }

    private void initEvent() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                moveCursor(position, oldposition);
                PreferencesHelper.putInt(ConstantsME.goodDream_login_position,position);
                if (0 == PreferencesHelper.getInt(ConstantsME.goodDream_login_position)) {
                    tvLogin.setTextColor(getResources().getColor(R.color.gooddream_blue));
                    tvRegister.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
                    tv_title.setText(getResources().getText(R.string.login));
                } else {
                    tvLogin.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
                    tvRegister.setTextColor(getResources().getColor(R.color.gooddream_blue));
                    tv_title.setText(getResources().getText(R.string.register));
                }
                oldposition=position;
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

    private void initView(View v) {
        // TODO Auto-generated method stub
        cursor = (ImageView) v.findViewById(R.id.cursor);
        relative_1 = (RelativeLayout) v.findViewById(R.id.relative_1);
        relative_2 = (RelativeLayout) v.findViewById(R.id.relative_2);
        relative_1.setOnClickListener(this);
        relative_2.setOnClickListener(this);

        tvLogin = (TextView) v.findViewById(R.id.tv_login);
        tvRegister = (TextView) v.findViewById(R.id.tv_register);
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        viewPager= (ViewPager) v.findViewById(R.id.viewPager);
    }

    private void initWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int screenW = Utils.getScreenWidth(mContext);
        int cursorLenth = screenW / num;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                cursorLenth, Utils.dip2px(mContext, 2));
        Y.y("cursor==null:" + (cursor == null));
        Y.y("params==null:" + (params == null));
        cursor.setLayoutParams(params);
        position_zero = 0;
        position_one = cursorLenth;
        arrPosition = new int[]{position_zero, position_one
        };
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
                tv_title.setText(getResources().getText(R.string.login));
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
                tv_title.setText(getResources().getText(R.string.register));
                viewPager.setCurrentItem(1);
//                changeFragment(fragment_2, currentPostion);
                break;
            default:
                break;
        }
    }

    int oldposition = -1;
    int currentPostion = 0;
//    Fragment findFragment = null;

//    private void changeFragment(Fragment f, int position) {
//        if (oldposition != position) {
//            FragmentManager fm = getChildFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            findFragment = fm.findFragmentByTag("viewid" + position);
//            if (Center_mContent != null) {
//                ft.detach(Center_mContent);
//            }
//            oldposition = position;
//            if (findFragment != null) {
//                ft.attach(findFragment);
//                Center_mContent = findFragment;
//            } else {
//                Center_mContent = f;
//                ft.add(R.id.relative_center, Center_mContent, "viewid"
//                        + position);
//            }
//            ft.commitAllowingStateLoss();
//        }
//    }
}
