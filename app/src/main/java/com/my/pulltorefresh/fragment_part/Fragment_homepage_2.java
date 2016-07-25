package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.my.configs.ConstantsME;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.Y;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Fragment_homepage_2 extends Fragment implements OnClickListener {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    Fragment Center_mContent;
    Fragment fragment_1;
    Fragment fragment_2;
    Fragment fragment_3;
    Fragment fragment_4;
    Fragment fragment_5;
    Animation animation = null;
    ImageView cursor;
    RelativeLayout relative_1, relative_2, relative_3, relative_4, relative_5;
    int num = 5;
    ///////////////////////////////
    TextView tv_title;
    TextView tv_1, tv_2, tv_3, tv_4, tv_5;
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
        moveCursor(PreferencesHelper.getInt(ConstantsME.goodDream_question_position),
                PreferencesHelper.getInt(ConstantsME.goodDream_question_position));
        showCursor();
    }

    private void showCursor() {
        if (0 == PreferencesHelper.getInt(ConstantsME.goodDream_question_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_4.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_5.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
//            tv_title.setText(getResources().getText(R.string.question_1));
        } else if (1 == PreferencesHelper.getInt(ConstantsME.goodDream_question_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_4.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_5.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
//            tv_title.setText(getResources().getText(R.string.question_2));
        } else if (2 == PreferencesHelper.getInt(ConstantsME.goodDream_question_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_4.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_5.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
//            tv_title.setText(getResources().getText(R.string.question_2));
        } else if (3 == PreferencesHelper.getInt(ConstantsME.goodDream_question_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_4.setTextColor(getResources().getColor(R.color.gooddream_blue));
            tv_5.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
//            tv_title.setText(getResources().getText(R.string.question_2));
        } else if (4 == PreferencesHelper.getInt(ConstantsME.goodDream_question_position)) {
            tv_1.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_2.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_3.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_4.setTextColor(getResources().getColor(R.color.gooddream_gray_light));
            tv_5.setTextColor(getResources().getColor(R.color.gooddream_blue));
//            tv_title.setText(getResources().getText(R.string.question_2));
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
            view = inflater.inflate(R.layout.fragment_homepage2, null);
            initView(view);
            initWidth();
            initEvent();
            PreferencesHelper.putInt(ConstantsME.goodDream_question_position, 0);
            if (null == fragment_1) {
                fragment_1 = new Fragment_2_1();
            }
            if (null == fragment_2) {
                fragment_2 = new Fragment_2_2_gridview();
//                fragment_2 = new Fragment_2_2();
            }
            if (null == fragment_3) {
                fragment_3 = new Fragment_2_3_gridview();
//                fragment_3 = new Fragment_2_3(PreferencesHelper.getString(ConstantsME.constant3));
            }
            if (null == fragment_4) {
//                fragment_4 = new Fragment_2_4_gridview();
                fragment_4=new Fragment_2_4_new();
//                fragment_4 = new Fragment_2_3(PreferencesHelper.getString(ConstantsME.constant4));
            }
            if (null == fragment_5) {
                fragment_5 = new Fragment_2_3(PreferencesHelper.getString(ConstantsME.constant5));
            }
            oldposition = 0;
            ArrayList<Fragment> listFragments = new ArrayList<Fragment>();
            listFragments.add(fragment_1);
            listFragments.add(fragment_2);
            listFragments.add(fragment_3);
            listFragments.add(fragment_4);
            listFragments.add(fragment_5);
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
                PreferencesHelper.putInt(ConstantsME.goodDream_question_position, position);
                showCursor();
                oldposition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    int[] arrPosition;
    int position_zero, position_one, position_two, position_three, position_four;

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
        relative_3 = (RelativeLayout) v.findViewById(R.id.relative_3);
        relative_4 = (RelativeLayout) v.findViewById(R.id.relative_4);
        relative_5 = (RelativeLayout) v.findViewById(R.id.relative_5);
        relative_1.setOnClickListener(this);
        relative_2.setOnClickListener(this);
        relative_3.setOnClickListener(this);
        relative_4.setOnClickListener(this);
        relative_5.setOnClickListener(this);

        tv_1 = (TextView) v.findViewById(R.id.tv_1);
        tv_2 = (TextView) v.findViewById(R.id.tv_2);
        tv_3 = (TextView) v.findViewById(R.id.tv_3);
        tv_4 = (TextView) v.findViewById(R.id.tv_4);
        tv_5 = (TextView) v.findViewById(R.id.tv_5);
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        viewPager = (ViewPager) v.findViewById(R.id.viewPager);
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
        position_zero = cursorLenth * 0;
        position_one = cursorLenth * 1;
        position_two = cursorLenth * 2;
        position_three = cursorLenth * 3;
        position_four = cursorLenth * 4;
        arrPosition = new int[]{position_zero, position_one, position_two, position_three, position_four
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
                PreferencesHelper.putInt(ConstantsME.goodDream_question_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                Y.y("currentPostion:" + currentPostion);
                Y.y("null == fragment_1:" + (null == fragment_1));
                // 约会
                if (null == fragment_1) {
                    fragment_1 = new Fragment_2_1();
                }
                showCursor();
                viewPager.setCurrentItem(0);
//                changeFragment(fragment_1, currentPostion);
                break;
            case R.id.relative_2:
                currentPostion = 1;
                PreferencesHelper.putInt(ConstantsME.goodDream_question_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                Y.y("currentPostion:" + currentPostion);
                Y.y("null == fragment_2:" + (null == fragment_2));
                if (null == fragment_2) {
                    fragment_2 = new Fragment_2_2_gridview();
//                    fragment_2 = new Fragment_2_2();
                }
                showCursor();
                viewPager.setCurrentItem(1);
//                changeFragment(fragment_2, currentPostion);
                break;
            case R.id.relative_3:
                currentPostion = 2;
                PreferencesHelper.putInt(ConstantsME.goodDream_question_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                Y.y("currentPostion:" + currentPostion);
                Y.y("null == fragment_2:" + (null == fragment_2));
                if (null == fragment_3) {
                    fragment_3 = new Fragment_2_3_gridview();
//                    fragment_3 = new Fragment_2_3("http://www.baidu.com");
                }
                showCursor();
                viewPager.setCurrentItem(2);
//                changeFragment(fragment_2, currentPostion);
                break;
            case R.id.relative_4:
                currentPostion = 3;
                PreferencesHelper.putInt(ConstantsME.goodDream_question_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                Y.y("currentPostion:" + currentPostion);
                Y.y("null == fragment_2:" + (null == fragment_2));
                if (null == fragment_4) {
//                    fragment_4 = new Fragment_2_3("http://translate.google.cn/");
                    fragment_4=new Fragment_2_4_new();
                }
                showCursor();
                viewPager.setCurrentItem(3);
//                changeFragment(fragment_2, currentPostion);
                break;
            case R.id.relative_5:
                currentPostion = 4;
                PreferencesHelper.putInt(ConstantsME.goodDream_question_position, currentPostion);
                moveCursor(currentPostion, oldposition);
                Y.y("currentPostion:" + currentPostion);
                Y.y("null == fragment_2:" + (null == fragment_5));
                if (null == fragment_5) {
                    fragment_5 = new Fragment_2_3("http://dict.youdao.com/search?q=descendants&keyfrom=hao360");
                }
                showCursor();
                viewPager.setCurrentItem(4);
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
