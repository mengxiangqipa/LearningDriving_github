package com.gooddream.learningdriving.webview;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.utils.Utils;


public class WebViewActivity extends Activity implements OnClickListener {
    WebView webview_video;
    RelativeLayout relative_hide;
    TextView tv_progress;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview_common);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        ShareApplication.getInstance().addActivity(this);
        initView();
        initData();
        initIntent();
    }

    private void initIntent() {
        if (null != getIntent()) {
            ((TextView) findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("title"));
        }
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
        if (getIntent() != null && null != getIntent().getStringExtra("url")) {
            url = getIntent().getStringExtra("url");
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
        // TODO Auto-generated method stub
        webview_video = (WebView) findViewById(R.id.webview);
        relative_hide = (RelativeLayout) findViewById(R.id.relative_hide);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_back:
                super.onBackPressed();
                if (null != webview_video) {
                    // webview_video.pauseTimers();
                    // webview_video.stopLoading();
                    webview_video.destroy();
                    webview_video = null;
                }
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;
            case R.id.relative_back:
                if (null != webview_video) {
                    // webview_video.pauseTimers();
                    // webview_video.stopLoading();
                    webview_video.destroy();
                    webview_video = null;
                }
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;
            default:
                break;
        }
    }

    // @Override
    // protected void onResume() {
    // // TODO Auto-generated method stub
    // super.onResume();
    // if (null != webview_video){
    // webview_video.onResume();
    // }
    // }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // if (null != webview_video)
            // try {
            // webview_video.getClass().getMethod("onPause")
            // .invoke(webview_video, (Object[]) null);
            // } catch (IllegalAccessException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // } catch (IllegalArgumentException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // } catch (InvocationTargetException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // } catch (NoSuchMethodException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // webview_video.onPause(); // 暂停网页中正在播放的视频
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (null != webview_video) {
            // webview_video.pauseTimers();
            // webview_video.stopLoading();
            webview_video.destroy();
            webview_video = null;
        }
        super.onDestroy();
    }
}
