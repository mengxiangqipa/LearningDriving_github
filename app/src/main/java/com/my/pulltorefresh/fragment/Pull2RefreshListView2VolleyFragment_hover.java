package com.my.pulltorefresh.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.LoginActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.progress.LoadingView;
import com.my.pulltorefresh.PullToRefreshLayout;
import com.my.pulltorefresh.pullableview.PullableListView2;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.PreferencesHelper;
import com.my.utils.ReLoginUtils;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 * 上下拉刷新的抽象类
 *
 * @param <T> <pre/>
 * @author Administrator_yang
 */
public abstract class Pull2RefreshListView2VolleyFragment_hover<T> extends Fragment
        implements OnClickListener,
        PullToRefreshLayout.OnRefreshListener,
        ReLoginUtils.CallBack_ReLogin, PullableListView2.OnLoadListener {
    private static final int HANDLER_REQUEST = 100;
    private static final int HANDLER_DOING = 101;
    private static final int HANDLER_SUCCESS = 102;
    private static final int HANDLER_FAILURE = 103;
    private static final int HANDLER_LOGINFAILURE = 105;
    public PullableListView2 lv_center;// 加载中间内容的listview
    public PullToRefreshLayout pullToRefreshLayout;
    public LoadingView loadingView;
    RelativeLayout relative_hover, relative_hover_title;
    public List<T> mList;
    public ListItem<T> itemTemp;
    public CommonAdapter adapter;
    public View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    int in = 0;
    int reLoginTimes = 0;
    private Context mContext;
    private int pi = 0;
    private String inter = "";
    private int type = 0;
    private JSONObject jsonObject;
    private int itemLayoutId;
    private int tryCount = 0;

    public void firstRequestHideHover() {

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_REQUEST:
                    Y.y("HANDLER_REQUEST");
                    try {
                        if (0 == pi && !onRefreshing) {
                            CustomProgressDialogUtils.showProcessDialog(mContext,
                                    "拼命加载中...");
                        }
                        if (0 == pi) {
                            firstRequestHideHover();
                        }
                        stringRequestMethod();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Y.y("HANDLER_REQUEST e:" + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_SUCCESS:
                    Y.y("HANDLER_SUCCESS");
                    if (adapter != null) {
                        if (adapter.getCount()== 1 && ((ListItem) adapter.getItem(0)).isEmpty) {
                            adapter = null;
                        }
                    }
                    tryCount = 0;
                    reLoginTimes = 0;
                    CustomProgressDialogUtils.dismissProcessDialog();
                    try {
                        String response = (String) msg.obj;
                        dealData(response);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case HANDLER_LOGINFAILURE:
                    Y.y("HANDLER_LOGINFAILURE");
                    CustomProgressDialogUtils.dismissProcessDialog();
                    Y.y("HANDLER_LOGINFAILURE");
                    try {
                        if (lv_center.getFooterViewsCount() >= 1) {
                            lv_center.removeFooterView(mLoadmoreView);
                        }
                    } catch (Exception e) {
                    }
//                    if (tryCount > 1) {
//                        Utils.showToast("未能获取登录信息,请重新登录");
//                    }
                    reLoginTimes++;
                    Y.y("relogintimes:" + reLoginTimes);
                    if (reLoginTimes <= 1) {
                        Y.y("真的重登：1+reLoginTimes：" + reLoginTimes);
                        ReLoginUtils.newInstance().reLogin(mContext,
                                Pull2RefreshListView2VolleyFragment_hover.this);
                        Y.y("真的重登：2+reLoginTimes:" + reLoginTimes);
                    } else {
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                // 千万别忘了告诉控件刷新完毕了哦！
                                pullToRefreshLayout
                                        .refreshFinish(PullToRefreshLayout.FAIL);
                            }
                        }.sendEmptyMessage(0);
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                // 千万别忘了告诉控件加载完毕了哦！
                                pullToRefreshLayout
                                        .loadmoreFinish(PullToRefreshLayout.FAIL);
                            }
                        }.sendEmptyMessage(0);
                        CustomProgressDialogUtils.dismissProcessDialog();
                        initAdapter(mContext, itemLayoutId);
                        ArrayList mListTemp = new ArrayList<>();
                        ListItem item = new ListItem();
                        item.isEmpty = true;
                        mListTemp.add(item);
                        adapter = new CommonAdapter(mContext, mListTemp, itemLayoutId);
                        lv_center.setAdapter(adapter);
//                        Intent intentLogin = new Intent(mContext,
//                                LoginActivity.class);
//                        startActivity(intentLogin);
//                        ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
//                                R.anim.slide_left_out);
                    }
                    break;
                case HANDLER_FAILURE:
                    Y.y("HANDLER_FAILURE");
                    String mess_failure = (String) msg.obj;
                    Utils.showToast((mess_failure == null || ""
                            .equals(mess_failure)) ? "访问服务器出错啦！" : mess_failure);
                    // Utils.dismissProcessDialog();
                    CustomProgressDialogUtils.dismissProcessDialog();
                    Y.y("null == adapter:" + (null == adapter));
                    try {
                        if (lv_center.getFooterViewsCount() >= 1) {
                            lv_center.removeFooterView(mLoadmoreView);
                        }
                    } catch (Exception e) {
                    }
                    initAdapter(mContext, itemLayoutId);
                    ArrayList mListTemp = new ArrayList<>();
                    ListItem item = new ListItem();
                    item.isEmpty = true;
                        mListTemp.add(item);
                    adapter = new CommonAdapter(mContext, mListTemp, itemLayoutId);
                    lv_center.setAdapter(adapter);
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            // 千万别忘了告诉控件刷新完毕了哦！
                            pullToRefreshLayout
                                    .refreshFinish(PullToRefreshLayout.FAIL);
                        }
                    }.sendEmptyMessage(0);
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            // 千万别忘了告诉控件加载完毕了哦！
                            pullToRefreshLayout
                                    .loadmoreFinish(PullToRefreshLayout.FAIL);
                        }
                    }.sendEmptyMessage(0);
                    break;
                default:
                    break;
            }
        }
    };
    private int countTry = 0;

    /**
     * <pre>
     *
     * @return </pre>
     */
    public Pull2RefreshListView2VolleyFragment_hover(int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        // TODO Auto-generated constructor stub
        this.post2get = post2get;
        this.inter = inter;
        this.jsonObjectData = jsonObjectData;
        this.jsonObjectPage = jsonObjectPage;
    }

    public Pull2RefreshListView2VolleyFragment_hover(ListItem<T> itemTemp) {
        // TODO Auto-generated constructor stub
        this.itemTemp = itemTemp;
    }

    public Pull2RefreshListView2VolleyFragment_hover() {
        // TODO Auto-generated constructor stub
    }

    /**
     * <pre>
     * 返回列表项,作事件处理
     *
     * @param view
     * @param item
     * @param position
     * @return 返回的view不为null时为adapter填充数据
     *
     * <pre/>
     */
    public abstract View getListItemview(ViewHolder viewHolder, View view,
                                         T item, int position, ViewGroup parent);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.pull2refresh_all_listview_hover, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            if (type != 1) {
                adapter = null;
                initView();
                addHoverTitle(relative_hover_title);
                addHover(relative_hover);
                Y.y("我是测试：1");
                initData();
                Y.y("我是测试：2");
                initEvent();
                Y.y("我是测试：3");
                if (null != itemTemp) {
                    dealData(itemTemp);
                } else {
                    firstRequest(inter);
                }
            }
            myContainer.addView(view);
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
        if (type == 1) {
            adapter = null;
            initView();
            initData();
            if (null != itemTemp) {
                dealData(itemTemp);
            } else {
                firstRequest(inter);
            }
        }
        return myContainer;
    }

    View mLoadmoreView;
    private String loadingMoreText = "加载更多...";
    public int scrollState1;

    protected void initEvent() {
        lv_center.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                scrollState1 = scrollState;
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (lv_center.getLastVisiblePosition() == lv_center.getCount() - 1) {
                        //加载更多功能的代码
                        if (pi != -1) {
                            if (mLoadmoreView != null) {
                                if (lv_center.getFooterViewsCount() >= 1) {
                                    return;
                                }
                            }
                            mLoadmoreView = LayoutInflater.from(mContext).inflate(
                                    R.layout.allview_auto_data, null);
                            ImageView mLoadingView = (ImageView) mLoadmoreView.findViewById(R.id.iv_loading);
                            mLoadingView.setImageResource(R.drawable.allview_loading);
                            mLoadingView.setVisibility(View.VISIBLE);
                            TextView tvMore = (TextView) mLoadmoreView
                                    .findViewById(R.id.tv_loadstate);
                            tvMore.setText(loadingMoreText);
                            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.rotating);
                            mLoadingView.startAnimation(anim);
                            lv_center.addFooterView(mLoadmoreView, null, false);
//                            firstRequest(inter);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (scrollState1 != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    onScroll_(absListView, i, i1, i2);
                }
            }
        });
    }

    public void onScroll_(AbsListView paramAbsListView, int firstVisibleItem,
                          int visibleItemCount, int totalItemCount) {

    }

    public void addHover(RelativeLayout relative_hover) {

    }

    public void addHoverTitle(RelativeLayout relative_hover_title) {

    }

    public void initData() {

    }

    ;

    /**
     * 通过反射改变listview的快速滑块的图标
     */
    private void fieldThumb() {
        try {
            Field f = AbsListView.class.getDeclaredField("mFastScroller");
            f.setAccessible(true);
            Object o = f.get(lv_center);
            f = f.getType().getDeclaredField("mThumbDrawable");
            f.setAccessible(true);
            Drawable drawable = (Drawable) f.get(o);
            drawable = getResources().getDrawable(R.mipmap.ic_launcher);
            f.set(o, drawable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void firstRequest(String inter) {
        Message msg = handler.obtainMessage();
        msg.what = HANDLER_REQUEST;
        msg.obj = inter;
        handler.sendMessage(msg);
    }

    int post2get = 1;
    JSONObject jsonObjectData;
    JSONObject jsonObjectPage;

    public void stringRequestMethod() {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));

            jsonObjectPage.put("pageIndex", pi);

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", jsonObjectPage);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        JSONObject jj = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("请求内部 jsonObjectData==NULL：" + (jsonObjectData == null));
        Y.y("请求内部 jsonObjectPage==NULL：" + (jsonObjectPage == null));
        Y.y("请求内部：" + params.toString());
        StringRequest stringRequest = new StringRequest(post2get == Request.Method.POST ? Request.Method.POST : Request.Method.GET, post2get == Request.Method.POST ? Configs.BASE_SERVER_URL + inter :
                Configs.BASE_SERVER_URL + inter + "?jsonRequest=" + jj.toString(),
                post2get == Request.Method.POST ? params : null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgressDialogUtils.dismissProcessDialog();
                Y.y("all_onResponse:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (response != null) {
                    String mess = VolleyUtilsTemp.msg;
                    String flag = VolleyUtilsTemp.flag;
                    try {
                        if (Configs.SUCCESS.equals(flag)) {
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLER_SUCCESS;
                            msg.obj = VolleyUtilsTemp.dataBodyJSONObject.toString();
                            pi = VolleyUtilsTemp.pi;
                            handler.sendMessage(msg);
                        } else if (mess.contains("数据校验") || mess.contains("userId")) {
                            onRefreshing = false;
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLER_LOGINFAILURE;
                            handler.sendMessage(msg);
                        } else {
                            onRefreshing = false;
                            Y.y("HANDLER_FAILURE:" + "oncomplete");
                            Message msg1 = handler.obtainMessage();
                            msg1.what = HANDLER_FAILURE;
                            msg1.obj = mess;
                            handler.sendMessage(msg1);
                        }
                        //
                    } catch (Exception e) {
                        Y.y("HANDLER_FAILURE:" + "oncomplete"
                                + e.getMessage());
                        Message msg = handler.obtainMessage();
                        msg.what = HANDLER_FAILURE;
                        handler.sendMessage(msg);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onRefreshing = false;
                CustomProgressDialogUtils.dismissProcessDialog();
                Message msg = handler.obtainMessage();
                msg.what = HANDLER_FAILURE;
                handler.sendMessage(msg);
                Y.y("Pull2RefreshListView2VolleyFragment_error:"
                        + error.getMessage());
            }
        });
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                inter + "");
    }

    public void refreshData(JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        // TODO Auto-generated method stub
        adapter = null;
        mList = null;
        this.jsonObjectData = jsonObjectData;
        this.jsonObjectPage = jsonObjectPage;
        pi = 0;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));

            jsonObjectPage.put("pageIndex", pi);

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", jsonObjectPage);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HashMap<String, String> params = VolleyUtilsTemp.returnParams(jsonObject, true);
        JSONObject jj = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("请求内部 jsonObjectData==NULL：" + (jsonObjectData == null));
        Y.y("请求内部 jsonObjectPage==NULL：" + (jsonObjectPage == null));
        Y.y("请求内部：" + params.toString());
        StringRequest stringRequest = new StringRequest(post2get == Request.Method.POST ? Request.Method.POST : Request.Method.GET, post2get == Request.Method.POST ? Configs.BASE_SERVER_URL + inter :
                Configs.BASE_SERVER_URL + inter + "?jsonRequest=" + jj.toString(),
                post2get == Request.Method.POST ? params : null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgressDialogUtils.dismissProcessDialog();
                Y.y("all_onResponse:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (response != null) {
                    String mess = VolleyUtilsTemp.msg;
                    String flag = VolleyUtilsTemp.flag;
                    try {
                        if (Configs.SUCCESS.equals(flag)) {
                            refreshSuccess = true;
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLER_SUCCESS;
                            msg.obj = VolleyUtilsTemp.dataBodyJSONObject
                                    .toString();
                            pi = VolleyUtilsTemp.pi;
                            handler.sendMessage(msg);
                        } else if (mess.contains("数据校验") || mess.contains("userId")) {
                            onRefreshing = false;
                            refreshSuccess = false;
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLER_LOGINFAILURE;
                            handler.sendMessage(msg);
                        } else {
                            refreshSuccess = false;
                            Y.y("HANDLER_FAILURE:" + "oncomplete");
                            onRefreshing = false;
                            Message msg1 = handler.obtainMessage();
                            msg1.what = HANDLER_FAILURE;
                            msg1.obj = mess;
                            handler.sendMessage(msg1);
                        }
                        //
                    } catch (Exception e) {
                        Y.y("HANDLER_FAILURE:" + "oncomplete"
                                + e.getMessage());
                        Message msg = handler.obtainMessage();
                        msg.what = HANDLER_FAILURE;
                        handler.sendMessage(msg);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onRefreshing = false;
                refreshSuccess = false;
                CustomProgressDialogUtils.dismissProcessDialog();
                Message msg = handler.obtainMessage();
                msg.what = HANDLER_FAILURE;
                handler.sendMessage(msg);
                Y.y("Pull2RefreshListView2VolleyFragment_error:"
                        + error.getMessage());
            }
        });
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                inter + "");
    }

    private boolean refreshSuccess = false;

    public boolean refreshSuccess() {
        return refreshSuccess;
    }

    public JSONObject getJSONObject() {
        // TODO Auto-generated method stub
        return jsonObject;
    }

    private void initView() {
        // TODO Auto-generated method stub
        lv_center = (PullableListView2) view.findViewById(R.id.lv_center);
        lv_center.setOnLoadListener(this);
        pullToRefreshLayout = (PullToRefreshLayout) view
                .findViewById(R.id.refresh_view);
        pullToRefreshLayout.setOnRefreshListener(this);
        loadingView = (LoadingView) view.findViewById(R.id.loadView);
        relative_hover = (RelativeLayout) view.findViewById(R.id.relative_hover);
        relative_hover_title = (RelativeLayout) view.findViewById(R.id.relative_hover_title);
//        loadingView.setLoadingText();
        ////////////////////////////
        lv_center.setLoadMoreBackgroundColor(getResources().getColor(
                R.color.default_text_blue));
        lv_center.setLoadMoreTextNoMore("没有数据了!");
        lv_center.setLoadMoreText("点我查看更多");
        lv_center.setLoadMoreTextColor(getResources().getColor(R.color.white));
        lv_center.setLoadMoreTextSize(14);
        lv_center.setCanPullUp(false);
        lv_center.setCanPullDown(true);
        lv_center.setAutoLoad(true);
        lv_center.setHasMoreData(true);
        lv_center.setLoadmoreVisible(false);
        pullToRefreshLayout.setPullDownContentText("下拉刷新数据");
        // pullToRefreshLayout.setPullUpContentText("宁静以致远");
        pullToRefreshLayout.setPullDownStateText("刷新成功");
        int color = getResources().getColor(R.color.default_text_blue);
        pullToRefreshLayout.setHeadViewBackgroundColor(color);
        // pull.setHeadViewBackgroundResource(R.drawable.allview_refreshing);
        pullToRefreshLayout.setPullDownContentTextSize(14);
        pullToRefreshLayout.setPullDownStateTextSize(12);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            default:
                break;
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    /**
     * @param json
     * @return 解析data字段//根据服务器返回解析
     */
    public JSONArray parseDataFields(JSONObject json) {
        return json.optJSONArray("data");
    }

    public ListItem<T> parseJson(String response) {
        return parseJson(response, 0);
    }

    public ListItem<T> parseJson(String response, int type) {
        ListItem<T> listItem = new ListItem<T>();
        try {
            JSONObject json = new JSONObject(response);
            Y.y("父类：response:" + response);
            JSONArray array = parseDataFields(json);
            // 解析列表
            if (null != array && array.length() != 0) {
                Y.y("父类：array:" + array.length());
                listItem.isDataNULL = false;
                int len = array.length();
                for (int j = 0; j < len; j++) {
                    JSONObject obj = array.optJSONObject(j);
                    Object ob = parseJson(obj);
                    // mList.add(ml);
                    listItem.mdatas.add(ob);
                    ob = null;
                }
            } else if (json != null && 1 == type) {
                listItem.mdatas.add(parseJson(json));
            } else {
                listItem.isDataNULL = true;
            }
        } catch (Exception e) {
            Y.y("Exception_root:" + e.getMessage());
        }
        return listItem;
    }

    public Object parseJson(JSONObject obj) {
        ListItem nl = new ListItem();
        nl.title = obj.optString("title", "");
        return nl;
    }

    public void dealData(String response) {
        Y.y("dealData_all");
        try {
            if (lv_center.getFooterViewsCount() >= 1) {
                lv_center.removeFooterView(mLoadmoreView);
            }
        } catch (Exception e) {
        }
        lv_center.setLoadmoreVisible(false);
//        if (mList != null) {
//            mList.addAll(parseJson(response));
//        } else {
//            mList = parseJson(response);
//        }
        try {
            ListItem<T> listItem = parseJson(response);
            Y.y("mlist==null:"+(mList==null));
            if (mList == null) {
                mList = listItem.mdatas;
            } else {
                mList.addAll(listItem.mdatas);
            }
            if (mList == null) {
                pi = -1;
            } else {
                pi = VolleyUtilsTemp.pi;
            }
            if (pi == -1) {
                lv_center.setHasMoreData(false);
            } else {
                lv_center.setHasMoreData(true);
            }
            try {
                if (pi > 0) {
                    lv_center.setLoadmoreVisible(true);
                } else {
                    lv_center.setLoadmoreVisible(false);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                lv_center.setLoadmoreVisible(false);
            }
            lv_center.setVisibility(View.VISIBLE);
            Y.y("adapter==null:"+(adapter==null)+"  listItem.isDataNULL:"+(listItem.isDataNULL));
            if (adapter == null) {
                mContext = getActivity();
                itemLayoutId = R.layout.common_layout;
                initAdapter(mContext, itemLayoutId);
                adapter = new CommonAdapter(mContext, mList, itemLayoutId);
                lv_center.setAdapter(adapter);
            } else if (!listItem.isDataNULL) {
                adapter.addDatas(mList);
                adapter.notifyDataSetChanged();
            }
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件刷新完毕了哦！
                    pullToRefreshLayout
                            .refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessage(0);
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件加载完毕了哦！
                    pullToRefreshLayout
                            .loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessage(0);

        } catch (Exception e) {
            Y.y("dealData:"+ e.getLocalizedMessage());
        }
    }

    /**
     * 结果从其他页面传递
     */
    public void dealData(ListItem<T> listItem) {
        try {
            mList = listItem.mdatas;
//            Y.y("我在刷新layout里面_1" + mList.size());
            if (mList == null) {
                pi = -1;
            } else {
                pi = VolleyUtilsTemp.pi;
            }
            if (adapter == null) {
                mContext = getActivity();
                itemLayoutId = R.layout.common_layout;
                initAdapter(mContext, itemLayoutId);
                adapter = new CommonAdapter(mContext, mList, itemLayoutId);
                lv_center.setAdapter(adapter);
            } else if (!listItem.isDataNULL) {
//                    Y.y("newCount:" + newCount);
//                    Y.y("totalCount:" + totalCount);
//                    Y.y("oldCount:" + oldCount);
                adapter.addDatas(mList);
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
        }
    }

    public void initAdapter(Context context, int itemLayoutId) {
        this.mContext = context;
        this.itemLayoutId = itemLayoutId;
    }

    private boolean onRefreshing = false;

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
        onRefreshing = true;
        refreshSuccess = true;
        lv_center.setLoadmoreVisible(false);
        if (null == adapter) {
            pi = 0;
            Message msg = handler.obtainMessage();
            msg.what = HANDLER_REQUEST;
            handler.sendMessage(msg);
//            Y.y("onRefresh1");
        } else {
            if (null != mList) {
                mList.clear();
            }
            pi = 0;
            adapter.notifyDataSetChanged();
            adapter = null;
            Message msg = handler.obtainMessage();
            msg.what = HANDLER_REQUEST;
            handler.sendMessage(msg);
//            Y.y("onRefresh1");
        }
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
        if ("-1".equals(pi)) {
            Utils.showToast("♬♬么么哒，没有数据啦♬♬");
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件加载完毕了哦！
                    pullToRefreshLayout
                            .loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessage(0);
        } else {
            Message msg = handler.obtainMessage();
            msg.what = HANDLER_REQUEST;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onLoad(PullableListView2 pullableListView2) {
        try {
            if (pi == -1) {
                Utils.showToast("没有数据了");
                lv_center.setHasMoreData(false);
                lv_center.setLoadmoreVisible(false);
            } else {
                Message msg = handler.obtainMessage();
                msg.what = HANDLER_REQUEST;
                msg.arg1 = 2;
                handler.sendMessage(msg);
            }
//            new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    lv_center.finishLoading();
//                    adapter.notifyDataSetChanged();
//                }
//            }.sendEmptyMessage(0);
        } catch (Exception e) {
        }
    }

    public void notifyData() {
        adapter.notifyDataSetChanged();
    }

    public void loginSuccess() {
        // TODO Auto-generated method stub
//        Y.y("回调：loginSuccess");
        firstRequest(inter);
    }

    public void loginFail() {
        // TODO Auto-generated method stub
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 千万别忘了告诉控件刷新完毕了哦！
                pullToRefreshLayout
                        .refreshFinish(PullToRefreshLayout.FAIL);
            }
        }.sendEmptyMessage(0);
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 千万别忘了告诉控件加载完毕了哦！
                pullToRefreshLayout
                        .loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        }.sendEmptyMessage(0);
        CustomProgressDialogUtils.dismissProcessDialog();
        Intent intentLogin = new Intent(mContext,
                LoginActivity.class);
        startActivity(intentLogin);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                R.anim.slide_left_out);
    }

    @Override
    public void loginSuccess(int requestCode) {
    }

    @Override
    public void loginFail(int requestCode) {

    }
    public class CommonAdapter extends BaseAdapter {
        protected final int mItemLayoutId;
        public List<T> mDatas;
        protected LayoutInflater mInflater;
        protected Context mContext;

        public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
            this.mDatas = mDatas;
            this.mItemLayoutId = itemLayoutId;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public T getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder = getViewHolder(position, convertView,
                    parent);
            View view = getListItemview(viewHolder,
                    viewHolder.getConvertView(), getItem(position), position,
                    parent);
            if (view == null) {
                return viewHolder.getConvertView();
            } else {
                return view;
            }
        }

        public ViewHolder getViewHolder(int position, View convertView,
                                        ViewGroup parent) {
            return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                    position);
        }

        public void addDatas(List list) {// 数据追加
            if (mDatas != null)
                mDatas.addAll(list);
            this.notifyDataSetChanged();
        }

        public void add(int position, T t) {
            mDatas.add(position, t);
        }

        public void clearData() {
            try {
                mDatas.clear();
                adapter.notifyDataSetChanged();
                lv_center.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
            }
        }
    }
}
