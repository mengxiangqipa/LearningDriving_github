package com.gooddream.learningdriving.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.utils.Utils;

public class EvaluateActivity extends BaseActivity {
    private TextView tv_phone1, tv_phone2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluate);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        initView();
        initData();
        ShareApplication.share.addActivity(this);
    }

    private void initData() {
        // TODO Auto-generated method stub
    }

    private void initView() {
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tv_title)).setText("评价");

        tv_phone1 = (TextView) findViewById(R.id.tv_phone1);
        tv_phone2 = (TextView) findViewById(R.id.tv_phone2);
    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_get_captcha:
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
                Utils.showToast("在线1");
                break;
            case R.id.linear_phone2:
                Utils.showToast("在线2");
                break;
            case R.id.tv_ensure:
                showPop(this);
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
}
