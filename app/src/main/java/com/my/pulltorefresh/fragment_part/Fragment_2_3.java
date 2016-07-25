package com.my.pulltorefresh.fragment_part;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.ForgetPasswordActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.broadcast.FinishBroadCastReceiver;
import com.my.configs.Configs;
import com.my.configs.SMStest;
import com.my.customviews.OverScrollView;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.PicToastUtil;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.Y;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Fragment_2_3 extends Fragment {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////
    WebView webview_video;
    RelativeLayout relative_hide;
    TextView tv_progress;
    String url = "";

    public Fragment_2_3(String url) {
        this.url = url;
    }

    public Fragment_2_3() {

    }

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
            view = inflater.inflate(R.layout.fragment_2_3, null);
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
        initData();
        return myContainer;
    }

    private void initData() {
        // TODO Auto-generated method stub
        // 启用支持javascript
        WebSettings webSettings = webview_video.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // webview_video.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        // webSettings.setBuiltInZoomControls(true);
        // webSettings.setSupportZoom(true);
        // webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDomStorageEnabled(true);
        if (!"".equals(url)) {
            webview_video.loadUrl(url);
            webview_video.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });
            webview_video.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    // TODO Auto-generated method stub
                    if (newProgress == 100) {
                        // 网页加载完成
                        tv_progress.setText("正在加载网页 :" + newProgress + "%");
                        relative_hide.setVisibility(View.GONE);
                    } else {
                        // 加载中
                        tv_progress.setText("正在加载网页 :" + newProgress + "%");
                    }

                }
            });
        }
    }

    private void initView() {
        webview_video = (WebView) view.findViewById(R.id.webview);
        relative_hide = (RelativeLayout) view.findViewById(R.id.relative_hide);
        tv_progress = (TextView) view.findViewById(R.id.tv_progress);
    }

}
