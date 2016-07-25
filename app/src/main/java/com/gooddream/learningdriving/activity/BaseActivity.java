package com.gooddream.learningdriving.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;

/**
 * Created by YorberonJomi 2016/6/30.
 */
public class BaseActivity extends AppCompatActivity {
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
