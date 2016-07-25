package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.SplashActivity;
import com.my.configs.ConstantsME;
import com.my.utils.PreferencesHelper;
import com.my.utils.Y;


public class Fragment_guide_3 extends Fragment implements GestureDetector.OnGestureListener {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        gestureDetectorCompat = new GestureDetectorCompat(mContext, this);
    }

    public View onCreateView(LayoutInflater inflater,
                             // TODO Auto-generated method stub
                             ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.fragment_guide_3, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            myContainer.addView(view);
            initView();
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return gestureDetectorCompat.onTouchEvent(motionEvent);//返回手势识别触发的事件
//            }
//        });
        return myContainer;
    }

    GestureDetectorCompat gestureDetectorCompat;

    private void initView() {
        // TODO Auto-generated method stub
        TextView tv_go = (TextView) view.findViewById(R.id.tv_go);
        tv_go.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PreferencesHelper.putBoolean(ConstantsME.hasGuide, true);
                startActivity(new Intent(mContext, SplashActivity.class));
                ((Activity) mContext).finish();
            }
        });
        tv_go.setVisibility(View.GONE);
    }

    @Override//此方法必须重写且返回真，否则onFling不起效
    public boolean onDown(MotionEvent e) {
//        PreferencesHelper.putBoolean(ConstantsME.hasGuide, true);
//        startActivity(new Intent(mContext, SplashActivity.class));
//        ((Activity) mContext).finish();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if ((motionEvent.getX() - motionEvent1.getX() > 5)) {
            PreferencesHelper.putBoolean(ConstantsME.hasGuide, true);
            startActivity(new Intent(mContext, SplashActivity.class));
            ((Activity) mContext).finish();
            return true;
        } else if ((motionEvent.getX() - motionEvent1.getX() > 120) && Math.abs(v1) > 200) {
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
