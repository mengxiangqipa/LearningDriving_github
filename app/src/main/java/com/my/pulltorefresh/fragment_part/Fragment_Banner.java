package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.interfaces.CallBack_Banner;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.my.configs.Configs;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.utils.ImageLoaderUtils_nostra13;

public class Fragment_Banner extends Fragment {
    static Fragment_Banner newInstance;
    final int fail = 501;
    final int success = 502;
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ListItem item;
    int position;
    int size;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader2;
    CallBack_Banner mCallBack;

    public static Fragment_Banner newInstance(ListItem item,
                                              int position, int size) {
        Fragment_Banner newInstance = new Fragment_Banner();
        final Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        bundle.putInt("position", position);
        bundle.putInt("size", size);
        newInstance.setArguments(bundle);
        return newInstance;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (this.isVisible()) {
            // we check that the fragment is becoming visible
        }
        super.setUserVisibleHint(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        imageLoader2 = ImageLoaderUtils_nostra13.initImageLoader(mContext);
        Bundle arguments = getArguments();
        item = (ListItem) arguments.get("item");
        position = arguments.getInt("position");
        size = arguments.getInt("size");
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.listitem_banner, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            initData();
            myContainer.addView(view);
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
        return myContainer;
    }

    private void initData() {
        ImageView iv_banner = (ImageView) view.findViewById(R.id.iv_banner);
        ImageView iv_cancel = (ImageView) view.findViewById(R.id.iv_cancel);
        try {
            imageLoader2.displayImage(item.banner.split(";")[0],
                    iv_banner, ImageLoaderUtils_nostra13.getFadeOptions(
                            R.drawable.bg_default_listitem,
                            R.drawable.bg_default_listitem,
                            R.drawable.bg_default_listitem));
        } catch (Exception e) {

        }
        iv_banner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(item.bannerUrl)) {
                    Intent intentWebView = new Intent(mContext,
                            WebViewActivity.class);
                    intentWebView.putExtra("url", item.bannerUrl);
                    intentWebView.putExtra("title", "好梦学车");
                    startActivity(intentWebView);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                }
            }
        });
        // TODO Auto-generated method stub
        iv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null != mCallBack) {
                    mCallBack.onCancelClicked();
                }
            }
        });
    }

    public void setCallBack(CallBack_Banner mCallBack) {
        this.mCallBack = mCallBack;
    }
}
