package com.my.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gooddream.learningdriving.R;


/**
 * @Description:自定义对话框
 */
public class CustomProgressDialog extends ProgressDialog {

    private AnimationDrawable mAnimation;
    // private Context mContext;
    private ImageView mImageView;
    private String mLoadingTip;
    private TextView mLoadingTv;
    private int mResid;

    public CustomProgressDialog(Context context, String content, int id) {
        super(context);
        // this.mContext = context;
        this.mLoadingTip = content;
        this.mResid = id;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {

//		mImageView.setBackgroundResource(mResid);
//		mImageView.setBackgroundDrawable(null);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
//		mAnimation = (AnimationDrawable) mImageView.getBackground();

        mImageView.setBackgroundDrawable(null);
        mImageView.setImageDrawable(null);
        mImageView.setImageResource(mResid);
        mAnimation = (AnimationDrawable) mImageView.getDrawable();
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();

            }
        });
        mLoadingTv.setText(mLoadingTip);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));

    }

    public void setContent(String str) {
        mLoadingTv.setText(str);
    }

    private void initView() {
        setContentView(R.layout.progress_dialog);
        mLoadingTv = (TextView) findViewById(R.id.loadingTv);
        mImageView = (ImageView) findViewById(R.id.loadingIv);
    }

	/*
     * @Override public void onWindowFocusChanged(boolean hasFocus) { // TODO
	 * Auto-generated method stub mAnimation.start();
	 * super.onWindowFocusChanged(hasFocus); }
	 */
}
