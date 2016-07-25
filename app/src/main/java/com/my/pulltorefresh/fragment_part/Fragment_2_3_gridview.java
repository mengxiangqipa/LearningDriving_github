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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.other.GetConstantUtil;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.my.configs.ConstantsME;
import com.my.item.ListItemChild;
import com.my.item.ViewHolder;
import com.my.other.CommonAdapter;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.utils.ImageLoaderUtils_nostra13;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.Y;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_2_3_gridview extends Fragment implements View.OnClickListener, GetConstantUtil.CallBack_GetConstant {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////
    GridView gridView;
    ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        imageLoader = ImageLoader.getInstance();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.fragment_2_2_gridview, null);
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
        initData();
        return myContainer;
    }

    List<ListItem> list;
    int tryCount = 0;

    private void initData() {
        if (TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.constantUrlListk3)) && tryCount < 2) {
            GetConstantUtil.newInstance().getContant(mContext, this);
            tryCount++;
        } else {
            getList();
        }
    }

    private void getList() {
        String string = PreferencesHelper.getString(ConstantsME.constantUrlListk3);
        try {
            JSONArray array = new JSONArray(string);
            if (array != null && array.length() > 0) {
                Y.y("yaya:" + array.length());
                list = new ArrayList<>();
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject1 = array.optJSONObject(i);
                    String title = jsonObject1.optString("title", "");
                    String iconUrl = jsonObject1.optString("cover", "");
                    String url = jsonObject1.optString("url", "");
                    ListItem itemChild = new ListItem();
                    itemChild.url = iconUrl;
                    itemChild.title = title;
                    itemChild.url = url;
                    list.add(itemChild);
                }
            }
            if (list != null) {
                Y.y("adapter:" + (list != null));
                Y.y("adapter:" + (list.size()));
                final int _10dp = Utils.dip2px(mContext, 10);
                final int _20dp = Utils.dip2px(mContext, 20);
                CommonAdapter adapter = new CommonAdapter(mContext, list, R.layout.listitem_gridview) {
                    @Override
                    public View getListItemview(ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                        viewHolder.setText(R.id.tv_title, item.title);
                        ImageView iv = viewHolder.getView(R.id.iv_yaya);
                        int screenWidth = Utils.getScreenWidth(mContext);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 3);
//                        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) iv.getLayoutParams();
//                        params.bottomMargin = _10dp;
//                        params.topMargin = _20dp;
                        iv.setLayoutParams(params);
                        iv.setPadding(_20dp,_20dp,_20dp,_10dp);
                        try {
                            imageLoader.displayImage(item.url.split(";")[0],
                                    iv, ImageLoaderUtils_nostra13.getFadeOptions(
                                            R.drawable.bg_default_listitem,
                                            R.drawable.bg_default_listitem,
                                            R.drawable.bg_default_listitem));
                        } catch (Exception e) {

                        }
                        return null;
                    }
                };
                Y.y("adapter:22");
                gridView.setAdapter(adapter);
                Y.y("adapter:33");
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intentWebView1 = new Intent(mContext,
                                WebViewActivity.class);
                        intentWebView1.putExtra("url", list.get(i).url);
                        intentWebView1.putExtra("title", list.get(i).title);
                        startActivity(intentWebView1);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                                R.anim.slide_left_out);
                    }
                });
                Y.y("adapter:44");
            }
        } catch (Exception e) {
            Y.y("异常：" + e.getMessage());
            e.printStackTrace();
        }

    }


    private void initView() {
        gridView = (GridView) view.findViewById(R.id.gridView);
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
            default:
                break;
        }
    }

    @Override
    public void getSuccess() {
        initData();
    }

    @Override
    public void getFail() {

    }
}
