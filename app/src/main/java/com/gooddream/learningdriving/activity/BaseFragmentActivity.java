package com.gooddream.learningdriving.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.utils.Utils;

public abstract class BaseFragmentActivity extends AppCompatActivity implements
        OnClickListener {
    String right = "";
    boolean showLeft = true;
    int oldposition = -1;
    int currentPostion = 0;
    Fragment findFragment = null;
    Fragment Center_mContent;
    Fragment fragment_1;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initIntent();
        setContentView(R.layout.common_layout);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        String title = initTitle();
        ((TextView) findViewById(R.id.tv_title)).setText(title);
        if(!showLeft){
            findViewById(R.id.iv_back).setVisibility(View.GONE);
        }
        if(!"".equals(right)){
            findViewById(R.id.tv_edit).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_edit)).setText(title);
        }
        fragment_1 = initFragment();
        changeFragment(fragment_1, 0);
    }

    /**
     * 初始化intent
     */
    public void initIntent() {
    }

    /**
     * <pre>
     *
     * @return fragment_1
     * <p/>
     * <pre/>
     */
    public abstract Fragment initFragment();

    /**
     * <pre>
     *
     * @return fragment_1
     * <p/>
     * <pre/>
     */
    public abstract String initTitle();

    public void showRightText(String text) {
        this.right = text;
    }

    public void showLeft(boolean show) {
        this.showLeft = show;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_back:
                ShareApplication.share.removeActivity(this);
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;
            default:
                break;
        }
    }

    private void changeFragment(Fragment f, int position) {
        if (oldposition != position) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            findFragment = fm.findFragmentByTag("viewid" + position);
            if (Center_mContent != null) {
                ft.detach(Center_mContent);
            }
            oldposition = position;
            if (findFragment != null) {
                ft.attach(findFragment);
                Center_mContent = findFragment;
            } else {
                Center_mContent = f;
                ft.add(R.id.relative_center, Center_mContent, "viewid"
                        + position);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(R.anim.slide_left_in,
                    R.anim.slide_right_out);
            ShareApplication.share.removeActivity(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    protected void onResume() {
//        // TODO Auto-generated method stub
//        super.onResume();
//        MobclickAgent.onResume(this);// 友盟统计时长
//    }
//
//    @Override
//    protected void onPause() {
//        // TODO Auto-generated method stub
//        super.onPause();
//        MobclickAgent.onPause(this);// 友盟统计时长
//    }
}
