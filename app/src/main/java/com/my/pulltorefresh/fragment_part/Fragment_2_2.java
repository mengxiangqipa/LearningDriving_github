package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.my.configs.ConstantsME;
import com.my.utils.PreferencesHelper;

public class Fragment_2_2 extends Fragment implements View.OnClickListener {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////
    LinearLayout linear_1, linear_2, linear_3, linear_4, linear_5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.fragment_2_2, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            myContainer.addView(view);
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
        initView();
//        initEvent();
        return myContainer;
    }


    private void initView() {
        linear_1 = (LinearLayout) view.findViewById(R.id.linear_1);
        linear_2 = (LinearLayout) view.findViewById(R.id.linear_2);
        linear_3 = (LinearLayout) view.findViewById(R.id.linear_3);
        linear_4 = (LinearLayout) view.findViewById(R.id.linear_4);
        linear_5 = (LinearLayout) view.findViewById(R.id.linear_5);

        linear_1.setOnClickListener(this);
        linear_2.setOnClickListener(this);
        linear_3.setOnClickListener(this);
        linear_4.setOnClickListener(this);
        linear_5.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_1:
                if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constant2_2_1))) {
                    Intent intentWebView1 = new Intent(mContext,
                            WebViewActivity.class);
                    intentWebView1.putExtra("url", PreferencesHelper.getString(ConstantsME.constant2_2_1));
                    intentWebView1.putExtra("title", getResources().getText(R.string.learning_2_1));
                    startActivity(intentWebView1);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);

                } else {
                    GetConstantUtil.newInstance().getContant(mContext);
                }
                break;
            case R.id.linear_2:
                if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constant2_2_2))) {
                    Intent intentWebView2 = new Intent(mContext,
                            WebViewActivity.class);
                    intentWebView2.putExtra("url", PreferencesHelper.getString(ConstantsME.constant2_2_2));
                    intentWebView2.putExtra("title", getResources().getText(R.string.learning_2_2));
                    startActivity(intentWebView2);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    GetConstantUtil.newInstance().getContant(mContext);
                }
                break;
            case R.id.linear_3:
                if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constant2_2_3))) {
                    Intent intentWebView3 = new Intent(mContext,
                            WebViewActivity.class);
                    intentWebView3.putExtra("url", PreferencesHelper.getString(ConstantsME.constant2_2_3));
                    intentWebView3.putExtra("title", getResources().getText(R.string.learning_2_3));
                    startActivity(intentWebView3);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    GetConstantUtil.newInstance().getContant(mContext);
                }
                break;
            case R.id.linear_4:
                if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constant2_2_4))) {
                    Intent intentWebView4 = new Intent(mContext,
                            WebViewActivity.class);
                    intentWebView4.putExtra("url", PreferencesHelper.getString(ConstantsME.constant2_2_4));
                    intentWebView4.putExtra("title", getResources().getText(R.string.learning_2_4));
                    startActivity(intentWebView4);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    GetConstantUtil.newInstance().getContant(mContext);
                }
                break;
            case R.id.linear_5:
                if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constant2_2_5))) {
                    Intent intentWebView5 = new Intent(mContext,
                            WebViewActivity.class);
                    intentWebView5.putExtra("url", PreferencesHelper.getString(ConstantsME.constant2_2_5));
                    intentWebView5.putExtra("title", getResources().getText(R.string.learning_2_5));
                    startActivity(intentWebView5);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    GetConstantUtil.newInstance().getContant(mContext);
                }
                break;
            default:
                break;
        }
    }

}
