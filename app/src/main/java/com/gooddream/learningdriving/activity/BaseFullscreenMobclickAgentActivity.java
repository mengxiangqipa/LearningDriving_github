package com.gooddream.learningdriving.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.utils.Y;

/**
 * @author Administrator 统计抽象类
 */
public abstract class BaseFullscreenMobclickAgentActivity extends Activity {
	private String tag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initSuperIntent();
		tag = getPageName();
	}
	public void initSuperIntent() {
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public abstract String getPageName();

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Y.y("onTouchEvent:");
		// ViewHelper.setRotationX(Utils.getRootView(this), 5f);
		return super.onTouchEvent(event);
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
}
