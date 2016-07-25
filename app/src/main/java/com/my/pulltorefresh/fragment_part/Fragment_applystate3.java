package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.webview.WebViewActivity;

public class Fragment_applystate3 extends Fragment implements View.OnClickListener {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////

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
            view = inflater.inflate(R.layout.fragment_applystate3, null);
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

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_1:
                Intent intentWebView1 = new Intent(mContext,
                        WebViewActivity.class);
                intentWebView1.putExtra("url", "http://www.baidu.com");
                intentWebView1.putExtra("title", getResources().getText(R.string.learning_1_1));
                startActivity(intentWebView1);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
                break;
            case R.id.linear_2:
                Intent intentWebView2 = new Intent(mContext,
                        WebViewActivity.class);
                intentWebView2.putExtra("url", "http://www.baidu.com");
                intentWebView2.putExtra("title", getResources().getText(R.string.learning_1_2));
                startActivity(intentWebView2);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
                break;
            default:
                break;
        }
    }

}
