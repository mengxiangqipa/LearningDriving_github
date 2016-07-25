package com.my.pulltorefresh.fragment_part;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
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
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.interfaces.CallBack_Banner;
import com.gooddream.learningdriving.webview.WebViewActivity;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.customviews.CustomRoundImageView_new;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.pulltorefresh.fragment.Pull2RefreshListView2VolleyFragment_hover;
import com.my.pulltorefresh.pullableview.PullableListView2;
import com.my.utils.ImageLoaderUtils_nostra13;
import com.my.utils.PreferencesHelper;
import com.my.utils.ThreadPoolUtil;
import com.my.utils.Utils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_homepage_3 extends Pull2RefreshListView2VolleyFragment_hover<ListItem> implements CallBack_Banner {
    private int bannerSize;
    private ViewPager viewPager;
    private TextView tv_title_left, tv_title_right;
    private ImageView iv_icon_left, iv_icon_right;
    private LinearLayout linear_left;
    private LinearLayout linear_right;
    ////////////////////

    com.nostra13.universalimageloader.core.ImageLoader imageLoader2;
    private Context mContext;

    public Fragment_homepage_3(int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        super(post2get, inter, jsonObjectData, jsonObjectPage);
    }

    static JSONObject jsonObjectData;
    static JSONObject jsonObjectPage;

    static {
        jsonObjectData = new JSONObject();
        jsonObjectPage = new JSONObject();
        try {
            jsonObjectPage.put("pageIndex", 0);
            jsonObjectPage.put("pageSize", 20);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Fragment_homepage_3() {
        super(Request.Method.GET, Configs.infoList, jsonObjectData, jsonObjectPage);
    }

    public static synchronized Fragment_homepage_3 newInstance(
            int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        Fragment_homepage_3 newInstance = new Fragment_homepage_3(
                post2get, inter, jsonObjectData, jsonObjectPage);
        final Bundle bundle = new Bundle();
        bundle.putString("inter", inter);
        newInstance.setArguments(bundle);
        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments() != null ? getArguments() : null;
        if (null != bundle) {

        }
        mContext = getActivity();
        imageLoader2 = ImageLoaderUtils_nostra13.initImageLoader(getActivity());
    }

    android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 50:
                    final int type = msg.arg1;
                    viewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(type);
                        }
                    });
                    Y.y("第三个mHandler:" + type);
                    break;
                default:
                    break;
            }
        }
    };
    View viewHeader1;

    @Override
    public void addHoverTitle(RelativeLayout relative_hover_title) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.hover_title3, null);
        v.setMinimumWidth(Utils.getScreenWidth(mContext));
        relative_hover_title.addView(v);
    }

    @Override
    public void onScroll_(AbsListView paramAbsListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        if (firstVisibleItem >= 1) {
//            hover.setVisibility(View.VISIBLE);
//        } else {
//            hover.setVisibility(View.INVISIBLE);
//        }
        Y.y("onScroll_1:" + firstVisibleItem);
        Y.y("onScroll_2:" + visibleItemCount);
        Y.y("onScroll_3:" + totalItemCount);
    }

    @Override
    public void initData() {
        viewHeader1 = LayoutInflater.from(mContext).inflate(R.layout.lv_hearderview_viewpager, null);
        lv_center.addHeaderView(viewHeader1);
        tv_title_left = (TextView) viewHeader1.findViewById(R.id.tv_title_left);
        tv_title_right = (TextView) viewHeader1.findViewById(R.id.tv_title_right);

        iv_icon_left = (ImageView) viewHeader1.findViewById(R.id.iv_icon_left);
        iv_icon_right = (ImageView) viewHeader1.findViewById(R.id.iv_icon_right);


        pullToRefreshLayout.setPullDownContentText("下拉刷新数据");
        //pullToRefreshLayout.setPullUpContentText("宁静以致远");
        pullToRefreshLayout.setPullDownStateText("刷新成功");
        int color = getResources().getColor(R.color.default_text_blue);
        pullToRefreshLayout.setHeadViewBackgroundColor(color);
        // pull.setHeadViewBackgroundResource(R.drawable.allview_refreshing);
        pullToRefreshLayout.setPullDownContentTextSize(14);
        pullToRefreshLayout.setPullDownStateTextSize(12);

        initViewM();
        initBanner();
        initNavigation();
    }

    private void initViewM() {
        viewPager = (ViewPager) viewHeader1.findViewById(R.id.viewPager);
        linear_left = (LinearLayout) viewHeader1.findViewById(R.id.linear_left);
        linear_right = (LinearLayout) viewHeader1.findViewById(R.id.linear_right);

        linear_left.setOnClickListener(this);
        linear_right.setOnClickListener(this);
    }

    private void initBanner() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject params = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("initbanner:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.banner_enquiries + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("initbanner:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray jsonArray = VolleyUtilsTemp
                            .optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "list");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        final ArrayList<ListItem> list = new ArrayList<ListItem>();
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            ListItem item = new ListItem();
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            if (null != jsonObject1) {
                                item.date = jsonObject1.optString("startMills", "");
                                item.banner = jsonObject1.optString("imgUrl", "");
                                item.string3 = jsonObject1.optString("title", "");
                                item.bannerUrl = jsonObject1.optString("url", "");
                            }
                            list.add(item);
                        }
                        ArrayList<Fragment> listFragments = new ArrayList<Fragment>();
                        bannerSize = list.size();
                        for (int i = 0; i < bannerSize; i++) {
                            ListItem item = list.get(i);
                            Fragment_Banner fragment_banner = Fragment_Banner.newInstance(item, i, bannerSize);
                            listFragments.add(fragment_banner);
                            fragment_banner.setCallBack(Fragment_homepage_3.this);
                        }
                        // TODO Auto-generated method stub
                        GuideFragmentPagerAdapter adapter = new GuideFragmentPagerAdapter(
                                getChildFragmentManager(), listFragments);
                        viewPager.setAdapter(adapter);
                        if (!hasStart) {
                            initGesture();
                        }
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Y.y("initbanner_onErrorResponse:" + error);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.banner_enquiries);
    }

    private void initNavigation() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject params = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("initNavigation:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.navigation + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("initNavigation:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray jsonArray = VolleyUtilsTemp
                            .optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "list");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            final ListItem item = new ListItem();
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            if (null != jsonObject1) {
                                item.banner = jsonObject1.optString("imgUrl", "");
                                item.string3 = jsonObject1.optString("title", "");
                                item.bannerUrl = jsonObject1.optString("url", "");
                                if (i == 0) {
                                    tv_title_left.setText(item.string3);
                                    try {
                                        imageLoader2.displayImage(item.banner.split(";")[0],
                                                iv_icon_left, ImageLoaderUtils_nostra13.getFadeOptions(
                                                        R.drawable.bg_default_listitem,
                                                        R.drawable.bg_default_listitem,
                                                        R.drawable.bg_default_listitem));
                                    } catch (Exception e) {

                                    }
                                    linear_left.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intentWebView1 = new Intent(mContext,
                                                    WebViewActivity.class);
                                            intentWebView1.putExtra("url", item.bannerUrl);
                                            intentWebView1.putExtra("title", item.string3);
                                            startActivity(intentWebView1);
                                            getActivity().overridePendingTransition(R.anim.slide_right_in,
                                                    R.anim.slide_left_out);
                                        }
                                    });
                                } else if (i == 1) {
                                    tv_title_right.setText(item.string3);
                                    try {
                                        imageLoader2.displayImage(item.banner.split(";")[0],
                                                iv_icon_right, ImageLoaderUtils_nostra13.getFadeOptions(
                                                        R.drawable.bg_default_listitem,
                                                        R.drawable.bg_default_listitem,
                                                        R.drawable.bg_default_listitem));
                                    } catch (Exception e) {

                                    }
                                    linear_right.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intentWebView1 = new Intent(mContext,
                                                    WebViewActivity.class);
                                            intentWebView1.putExtra("url", item.bannerUrl);
                                            intentWebView1.putExtra("title", item.string3);
                                            startActivity(intentWebView1);
                                            getActivity().overridePendingTransition(R.anim.slide_right_in,
                                                    R.anim.slide_left_out);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.navigation);
    }

    boolean hasStart = false;
    int timeTick = 0;
    int timeTickMaxBanner = 5;

    private void initGesture() {
        ThreadPoolUtil.getInstanceSingleTaskExecutor2().submit(new Runnable() {
            @Override
            public void run() {
                while (PreferencesHelper.getBoolean("homepage")) {
                    try {
                        hasStart = true;
                        Thread.sleep(1000);
                        timeTick++;
                        if (0 == (timeTick % timeTickMaxBanner)) {
                            Message message = mHandler.obtainMessage();
                            message.what = 50;
                            message.arg1 = (viewPager.getCurrentItem() + 1) % bannerSize;
                            mHandler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        Y.y("initGesture_exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initView() {

    }

    @Override
    public void onCancelClicked() {

    }

    int oldposition = -1;
    int currentPostion = 0;
    Fragment findFragment = null;
    Fragment Center_mContent;

    public void changeFragment(final Fragment fragment, final int position, final String TAG) {
        changeFragment(fragment, position, TAG, false);
    }

    public void changeFragment(final Fragment fragment, final int position, final String TAG, boolean mustReplace) {
        if (oldposition != position || mustReplace) {
            FragmentTransaction ft = getChildFragmentManager()
                    .beginTransaction();
            findFragment = getChildFragmentManager().findFragmentByTag(
                    "viewid" + position + TAG);

            if (Center_mContent != null) {
                Y.y("detach");
                if (!mustReplace) {
                    ft.detach(Center_mContent);
                }
            }
            oldposition = position;
            if (findFragment != null) {
                Y.y("attach");
                if (mustReplace) {
                    Center_mContent = fragment;
                    ft.replace(R.id.relative_center_homepage, Center_mContent, "viewid"
                            + position + TAG);
                } else {
                    ft.attach(findFragment);
                    Center_mContent = findFragment;
                }
            } else {
                Y.y("findresult== null");
                Center_mContent = fragment;
                ft.add(R.id.relative_center_homepage, Center_mContent, "viewid"
                        + position);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_left:
                break;
            case R.id.linear_right:
                break;
        }
    }

    @Override
    public JSONArray parseDataFields(JSONObject json) {
        // TODO Auto-generated method stub
        return json.optJSONArray("list");
    }

    @Override
    public Object parseJson(JSONObject obj) {
        // TODO Auto-generated method stub
        ListItem nl = new ListItem();
        nl.title = obj.optString("title", "");
        nl.name = obj.optString("digest", "");
        nl.url = obj.optString("url", "");
        nl.string1 = obj.optString("cover", "");
        return nl;
    }

    @Override
    public void initAdapter(Context context, int itemLayoutId) {
        // TODO Auto-generated method stub
        context = getActivity();
        itemLayoutId = R.layout.listitem_enquiries;
        super.initAdapter(context, itemLayoutId);
    }


    @Override
    public View getListItemview(com.my.pulltorefresh.fragment.ViewHolder viewHolder, View view, final ListItem item, int position, ViewGroup parent) {
        if (position == 0 && item.isEmpty) {
            View emptyView = LayoutInflater.from(mContext).inflate(R.layout.inflater_empty, null);
            RelativeLayout relative_empty = (RelativeLayout) emptyView.findViewById(R.id.relative_empty);
            emptyView.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            emptyView.setMinimumHeight(Utils.getScreenHeight(mContext) - Utils.getStatusBarHeightPx(getActivity()) - Utils.dip2px(mContext, 50 + 50) - viewHeader1.getHeight());
            relative_empty.setGravity(Gravity.CENTER);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!hasStart) {
                        initBanner();
                        initNavigation();
                    }
                    stringRequestMethod();

                }
            });
            return emptyView;
        }
        viewHolder.setText(R.id.tv_title, TextUtils.isEmpty(item.title) ? "一大波标题正在赶来!" : item.title);
        viewHolder.setText(R.id.tv_content, TextUtils.isEmpty(item.name) ? "一大波精彩内容正在赶来!" : item.name);
        try {
            imageLoader2.displayImage(item.string1.split(";")[0],
                    (ImageView) viewHolder.getView(R.id.iv), ImageLoaderUtils_nostra13.getFadeOptions(
                            R.drawable.bg_default_listitem,
                            R.drawable.bg_default_listitem,
                            R.drawable.bg_default_listitem));
        } catch (Exception e) {

        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentWebView1 = new Intent(mContext,
                        WebViewActivity.class);
                intentWebView1.putExtra("url", item.url);
                intentWebView1.putExtra("title", item.title);
                startActivity(intentWebView1);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
            }
        });

        return null;
    }

    @Override
    public void onLoad(PullableListView2 pullableListView2) {
        // TODO Auto-generated method stub
//        lv_center.finishLoading();
//        adapter.notifyDataSetChanged();
//        if (lv_center.getCount() >= 30) {
//            lv_center.setHasMoreData(false);
//        }
    }
}
