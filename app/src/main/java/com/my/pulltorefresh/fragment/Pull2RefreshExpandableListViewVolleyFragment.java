package com.my.pulltorefresh.fragment;

import android.content.Context;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.pulltorefresh.PullToRefreshLayout;
import com.my.pulltorefresh.pullableview.PullableExpandableListView;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.VolleyUtilsTemp;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 * 上下拉刷新的抽象类ExpandableListView
 * @author Administrator_yang
 * @param <T>
 *
 * <pre/>
 */
public abstract class Pull2RefreshExpandableListViewVolleyFragment<T> extends
        Fragment implements OnClickListener,
        PullToRefreshLayout.OnRefreshListener {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    private int pi = 0;
    private int oldCount = 0;
    private int totalCount = 0;
    private int newCount = 0;
    public PullableExpandableListView lv_center_expanable;// 加载中间内容的listview
    public PullToRefreshLayout pullToRefreshLayout;
    private static final int HANDLER_REQUEST = 100;
    private static final int HANDLER_DOING = 101;
    private static final int HANDLER_SUCCESS = 102;
    private static final int HANDLER_FAILURE = 103;
    private static final int HANDLER_LOGINFAILURE = 105;
    private String inter = "";
    int post2get = 1;
    public List<ListItem<T>> mList;
    public CommonExpandableAdapter adapter;
    private Context context;
    private int mItemLayoutIdGroup;
    private int mItemLayoutIdChild;
    private int tryCount = 0;
    RelativeLayout relative_hover, relative_hover_title, relative_hover_bottom;
    JSONObject jsonObjectData;
    JSONObject jsonObjectPage;

    /**
     * <pre>
     * (0)/(1)/(2)xx
     * @return
     * </pre>
     */
    public Pull2RefreshExpandableListViewVolleyFragment(int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        // TODO Auto-generated constructor stub
        this.post2get = post2get;
        this.inter = inter;
        this.jsonObjectData = jsonObjectData;
        this.jsonObjectPage = jsonObjectPage;
    }

    public Pull2RefreshExpandableListViewVolleyFragment() {
        // TODO Auto-generated constructor stub
    }

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
            view = inflater.inflate(
                    R.layout.pull2refresh_all_expandablelistview, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            initView();
            addHoverTitle(relative_hover_title);
            addHover(relative_hover);
            addHoverBottom(relative_hover_bottom);
            initData();
            initEvent();
            firstRequest(inter);
            myContainer.addView(view);
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
        return myContainer;
    }

    public void firstRequest(String funid) {
        Message msg = handler.obtainMessage();
        msg.what = HANDLER_REQUEST;
        msg.obj = funid;
        handler.sendMessage(msg);
    }

    public void firstRequestHideHover() {

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_REQUEST:
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
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_SUCCESS:
                    if (adapter != null) {
                        if (adapter.getGroupCount() == 1 && ((ListItem) adapter.getGroup(0)).isEmpty) {
                            adapter = null;
                        }
                    }
                    tryCount = 0;
                    reLoginTimes = 0;
                    // Utils.dismissProcessDialog();
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
                    if (tryCount > 1) {
                        Utils.showToast("未能获取登录信息,请重新登录");
                    }
                    reLoginTimes++;
                    if (reLoginTimes < 3) {
//                        reLogin();
                    } else {
                        Message ms = handler.obtainMessage();
                        ms.what = HANDLER_FAILURE;
                        handler.sendMessage(ms);
                    }
                    try {
                        if (lv_center_expanable.getFooterViewsCount() >= 1) {
                            lv_center_expanable.removeFooterView(mLoadmoreView);
                        }
                    } catch (Exception e) {
                    }
                    break;
                case HANDLER_FAILURE:
                    String mess_failure = (String) msg.obj;
                    Utils.showToast((mess_failure == null || ""
                            .equals(mess_failure)) ? "数据加载失败" : mess_failure);
                    try {
                        if (lv_center_expanable.getFooterViewsCount() >= 1) {
                            lv_center_expanable.removeFooterView(mLoadmoreView);
                        }
                    } catch (Exception e) {
                    }
                    CustomProgressDialogUtils.dismissProcessDialog();
                    Y.y("null == adapter:" + (null == adapter));

                    initAdapter(context, mItemLayoutIdGroup, mItemLayoutIdChild);
//                    newCount = mList.size();
//                    totalCount = 0;
//                    oldCount = newCount;
                    if (pi == 0) {
//                        pi = -1;
                        List<ListItem<T>> mListTemp = new ArrayList<>();
                        ListItem item = new ListItem();
                        item.isEmpty = true;
                        mListTemp.add(item);
                        adapter = new CommonExpandableAdapter(context, mListTemp,
                                mItemLayoutIdGroup, mItemLayoutIdChild);
                        lv_center_expanable.setAdapter(adapter);
                    }

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


    private void initView() {
        // TODO Auto-generated method stub
        lv_center_expanable = (PullableExpandableListView) view
                .findViewById(R.id.lv_center);
        pullToRefreshLayout = (PullToRefreshLayout) view
                .findViewById(R.id.refresh_view);

        relative_hover = (RelativeLayout) view.findViewById(R.id.relative_hover);
        relative_hover_title = (RelativeLayout) view.findViewById(R.id.relative_hover_title);
        relative_hover_bottom = (RelativeLayout) view.findViewById(R.id.relative_hover_bottom);

        pullToRefreshLayout.setOnRefreshListener(this);
    }

    View mLoadmoreView;
    private String loadingMoreText = "加载更多...";
    public int scrollState1;

    protected void initEvent() {
        lv_center_expanable.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                scrollState1 = scrollState;
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (lv_center_expanable.getLastVisiblePosition() == lv_center_expanable.getCount() - 1) {
                        if (mLoadmoreView != null) {
                            if (lv_center_expanable.getFooterViewsCount() >= 1) {
                                return;
                            }
                        }
                        mLoadmoreView = LayoutInflater.from(mContext).inflate(
                                R.layout.allview_auto_data, null);
                        ImageView mLoadingView = (ImageView) mLoadmoreView.findViewById(R.id.iv_loading);
                        mLoadingView.setImageResource(R.drawable.arrow_black);
                        mLoadingView.setVisibility(View.VISIBLE);
                        TextView tvMore = (TextView) mLoadmoreView
                                .findViewById(R.id.tv_loadstate);
                        tvMore.setText(loadingMoreText);
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.rotating);
                        mLoadingView.startAnimation(anim);
                        //加载更多功能的代码
                        if (pi != -1) {
                            lv_center_expanable.addFooterView(mLoadmoreView, null, false);
                            firstRequest(inter);
                        } else {
                            tvMore.setText("没有更多数据了");
                            mLoadingView.setVisibility(View.GONE);
                            try {
                                mLoadingView.clearAnimation();
                            } catch (Exception e) {
                            }
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

    public void addHoverBottom(RelativeLayout relative_hover_bottom) {

    }

    public void initData() {

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.relative_refresh:
//                Y.y("LONGTITUDE:"
//                        + PreferencesHelper.getString(ConstantsME.LONGITUDE));
//                Y.y("LATITUDE:"
//                        + PreferencesHelper.getString(ConstantsME.LATITUDE));
//                tryCount++;
//                relative_refresh.setVisibility(View.GONE);
//                Message msg = handler.obtainMessage();
//                msg.what = HANDLER_REQUEST;
//                handler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    public List<ListItem<T>> parseJson(String response) {
        List<ListItem<T>> list = new ArrayList<ListItem<T>>();
        try {
            JSONObject json = new JSONObject(response);
            Y.y("解析json：" + (json == null));
            if (json == null)
                return null;
            JSONArray array = parseFirstJSONArray(json, "areas");
            if (array == null)
                return null;
            Y.y("解析json:" + array.length());
            // 解析列表
            if (null != array && array.length() != 0) {
                int len = array.length();
                for (int j = 0; j < len; j++) {
                    JSONObject obj = array.optJSONObject(j);
                    ListItem<T> listItem = parseJson1(obj);
                    if (!parseJson1Intercept()) {
                        JSONArray array2 = parseSecondJSONArray(obj, "teachers");
                        if (null == array2 || array2.length() <= 0) {
                        } else {
                            int len2 = array2.length();
                            for (int k = 0; k < len2; k++) {
                                JSONObject obj2 = array2.optJSONObject(k);
                                Object ob = parseJson2(obj2, listItem);
                                listItem.mdatas.add(ob);
                                ob = null;
                            }
                        }
                    }
                    list.add(listItem);
                }
            } else {
                list = null;
            }
        } catch (Exception e) {
            Y.y("Exception_root:" + e.getMessage());
        }
        return list;
    }

    public boolean parseJson1Intercept() {
        return false;
    }

    /**
     * 解析1级array
     *
     * @param obj
     * @return
     */
    public ListItem<T> parseJson1(JSONObject obj) {
        ListItem<T> listItem = new ListItem<T>();
        listItem.address = obj.optString("address", "");
        return listItem;
    }

    /**
     * 解析2级array
     *
     * @return
     */
    public Object parseJson2(JSONObject object, ListItem<T> listItem) {

        return null;
    }

    /**
     * 解析1级array
     *
     * @return
     */
    public JSONArray parseFirstJSONArray(JSONObject object, String key) {
        return object.optJSONArray(key);
    }

    /**
     * 解析2级array
     *
     * @return
     */
    public JSONArray parseSecondJSONArray(JSONObject object, String key) {
        return object.optJSONArray(key);
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
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLER_SUCCESS;
                            msg.obj = VolleyUtilsTemp.dataBodyJSONObject
                                    .toString();
                            pi = VolleyUtilsTemp.pi;
                            handler.sendMessage(msg);
                        } else if (mess.contains("数据校验") || mess.contains("userId")) {
                            onRefreshing = false;
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLER_LOGINFAILURE;
                            handler.sendMessage(msg);
                        } else {
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
        String url = post2get == Request.Method.POST ? Configs.BASE_SERVER_URL + inter :
                Configs.BASE_SERVER_URL + inter + "?jsonRequest=" + jj.toString();
        StringRequest stringRequest = new StringRequest(post2get == Request.Method.POST ? Request.Method.POST : Request.Method.GET, url,
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
                            Y.y("HANDLER_FAILURE:" + "oncomplete");
                            onRefreshing = false;
                            refreshSuccess = false;
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
                refreshSuccess = false;
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

    int reLoginTimes = 0;

    /**
     * 上一次group展开的位置
     */
    private int oldGroupPosition = -1;// 控制列表的展开
    /**
     * 现在group展开的位置
     */
    public int newGroupPosition = -1;// 控制列表的展开
    private int tempGroupPosition = -1;

    public void dealData(String response) {
        Y.y("dealData_all:" + response);
        try {
            try {
                if (lv_center_expanable.getFooterViewsCount() >= 1) {
                    lv_center_expanable.removeFooterView(mLoadmoreView);
                }
            } catch (Exception e) {
            }
            if (mList != null) {
                mList.addAll(parseJson(response));
            } else {
                mList = parseJson(response);
            }
            Y.y("dealData_all:" + mList.size());
            if (mList == null) {
                pi = -1;
            } else {
                pi = VolleyUtilsTemp.pi;
            }
            Y.y("dealData_all_pi" + pi);
            if (adapter == null) {
                context = getActivity();
                mItemLayoutIdGroup = R.layout.listitem_pop;
                mItemLayoutIdChild = R.layout.listitem_pop;
                initAdapter(context, mItemLayoutIdGroup, mItemLayoutIdChild);
                newCount = mList.size();
                totalCount = 0;
                oldCount = newCount;
                adapter = new CommonExpandableAdapter(context, mList,
                        mItemLayoutIdGroup, mItemLayoutIdChild);
                lv_center_expanable.setAdapter(adapter);
            } else if (mList != null && !onRefreshing) {
                newCount = mList.size();
                totalCount += oldCount;
                oldCount = newCount;
                Y.y("newCount:" + newCount);
                Y.y("totalCount:" + totalCount);
                Y.y("oldCount:" + oldCount);
                adapter.addDatas(mList);
                adapter.notifyDataSetChanged();
                Utils.h.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
//                            setListViewPosition(lv_center_expanable, totalCount);
                            // setListViewPosition(lv_center,
                            // oldCount*(Integer.parseInt(oldPi)-1));
                        } catch (Exception e) {

                        }
                    }
                }, 600);
            } else if (onRefreshing) {
                onRefreshing = false;
                context = getActivity();
                mItemLayoutIdGroup = R.layout.listitem_pop;
                mItemLayoutIdChild = R.layout.listitem_pop;
                initAdapter(context, mItemLayoutIdGroup, mItemLayoutIdChild);
                newCount = mList.size();
                totalCount = 0;
                oldCount = newCount;
                adapter = new CommonExpandableAdapter(context, mList,
                        mItemLayoutIdGroup, mItemLayoutIdChild);
                lv_center_expanable.setAdapter(adapter);
            }
            // mList=null;
            Y.y("dealData111");
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件刷新完毕了哦！
                    Y.y("dealData222");
                    pullToRefreshLayout
                            .refreshFinish(PullToRefreshLayout.SUCCEED);
                    Y.y("dealData333");
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
            // MLog.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    /**
     * 设置Listview的滚动
     *
     * @author Administrator
     * @tags @param lv
     * @tags @param position
     */
    private void setListViewPosition(ListView lv, int position) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            lv.smoothScrollToPositionFromTop(position, 0, 1000);
            // lv.smoothScrollBy(Utils.getScreenHeight(mContext)-Utils.dip2px(mContext,
            // 96), 1000);
        } else {
            lv.setSelection(position);
        }
    }

    public void initAdapter(Context context, int mItemLayoutIdGroup,
                            int mItemLayoutIdChild) {
        this.context = context;
        this.mItemLayoutIdGroup = mItemLayoutIdGroup;
        this.mItemLayoutIdChild = mItemLayoutIdChild;
    }

    private boolean onRefreshing = false;

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
        mList = null;
        onRefreshing = true;
        refreshSuccess = true;
        oldGroupPosition = -1;
        newGroupPosition = -1;
        tempGroupPosition = -1;
        if (null == adapter) {
            pi = 0;
            Message msg = handler.obtainMessage();
            msg.what = HANDLER_REQUEST;
            handler.sendMessage(msg);
            Y.y("onRefresh1");
        } else {
            pi = 0;
//            if (null != mList) {
//                mList.clear();
//            }
//            adapter.notifyDataSetChanged();
//            adapter = null;
            Message msg = handler.obtainMessage();
            msg.what = HANDLER_REQUEST;
            handler.sendMessage(msg);
            Y.y("onRefresh1");
        }
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
        if (-1 == pi) {
            Utils.showToast("暂时没有数据了");
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

    /**
     * <pre>
     * 返回列表项,作事件处理
     * @param view
     * @param item
     * @param childPosition
     * @param groupPosition
     * @return
     *
     * <pre/>
     */
    public abstract View getChildListItemview(ViewHolder viewHolder, View view,
                                              T item, int childPosition, int groupPosition);

    public abstract View getGroupListItemview(ViewHolder viewHolder, View view,
                                              ListItem<T> item, int position);

    public class CommonExpandableAdapter extends BaseExpandableListAdapter {
        protected LayoutInflater mInflater;
        protected Context mContext;
        public List<ListItem<T>> mlist;
        protected final int mItemLayoutIdGroup;
        protected final int mItemLayoutIdChild;

        public CommonExpandableAdapter(Context context,
                                       List<ListItem<T>> mlist, int mItemLayoutIdGroup,
                                       int mItemLayoutIdChild) {
            // TODO Auto-generated constructor stub
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
            this.mlist = mlist;
            this.mItemLayoutIdGroup = mItemLayoutIdGroup;
            this.mItemLayoutIdChild = mItemLayoutIdChild;

        }

        private ViewHolder getChildViewHolder(int position, View convertView,
                                              ViewGroup parent) {
            return ViewHolder.get(mContext, convertView, parent,
                    mItemLayoutIdChild, position);
        }

        private ViewHolder getGroupViewHolder(int position, View convertView,
                                              ViewGroup parent) {
            return ViewHolder.get(mContext, convertView, parent,
                    mItemLayoutIdGroup, position);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            // SparseArray<List> a=new SparseArray<List>();
            return mlist.get(groupPosition).mdatas.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @SuppressWarnings("unchecked")
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder viewHolder = getChildViewHolder(groupPosition,
                    convertView, parent);
            View view = getChildListItemview(viewHolder, viewHolder.getConvertView(),
                    (T) getChild(groupPosition, childPosition), childPosition, groupPosition);
            if (view == null) {
                return viewHolder.getConvertView();
            } else {
                return view;
            }
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return (null == mlist.get(groupPosition)) ? 0 : mlist
                    .get(groupPosition).mdatas.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return mlist.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return mlist.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @SuppressWarnings("unchecked")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder viewHolder = getGroupViewHolder(groupPosition,
                    convertView, parent);
            View view = getGroupListItemview(viewHolder, viewHolder.getConvertView(),
                    (ListItem<T>) getGroup(groupPosition), groupPosition);
//            Animation animation_n = AnimationUtils.loadAnimation(mContext,
//                    R.anim.second_mark_rotate_n);
//            animation_n.setFillEnabled(true);
//            animation_n.setFillAfter(true);
//            Animation animation_temp = AnimationUtils.loadAnimation(mContext,
//                    R.anim.second_mark_temp);
//            animation_temp.setFillEnabled(true);
//            animation_temp.setFillAfter(true);
//            Animation animation_h = AnimationUtils.loadAnimation(mContext,
//                    R.anim.second_mark_rotate_h);
//            animation_h.setFillEnabled(true);
//            animation_h.setFillAfter(true);
//            ImageView iv_expanded = (ImageView)viewHolder.getView(R.id.iv_expanded);
//            iv_expanded.setImageResource(R.drawable.bg_arrow_down);
//            if (isExpanded && (groupPosition == oldGroupPosition)) {
//                iv_expanded.startAnimation(animation_n);
//            } else if ((!isExpanded && groupPosition == newGroupPosition)
//                // ||!isExpanded&&(groupPosition == tempGroupPosition)
//                    ) {
//                iv_expanded.startAnimation(animation_h);
//            } else if (!isExpanded && groupPosition == tempGroupPosition) {
//                iv_expanded.startAnimation(animation_temp);
//            }
            if (view == null) {
                return viewHolder.getConvertView();
            } else {
                return view;
            }
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

        public void addDatas(List list) {// 数据追加
            if (mlist != null)
                mlist.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    public void notifyData() {
        adapter.notifyDataSetChanged();
    }
}
