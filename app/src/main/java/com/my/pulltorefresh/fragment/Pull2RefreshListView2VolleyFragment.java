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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.LoginActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.Configs;
import com.my.pulltorefresh.PullToRefreshLayout;
import com.my.pulltorefresh.pullableview.PullableListView2;
import com.my.utils.CustomProgressDialogUtils;
import com.my.utils.ReLoginUtils;
import com.my.utils.Utils;
import com.my.utils.VolleyUtils;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 * 上下拉刷新的抽象类
 *
 * @param <T> <pre/>
 * @author Administrator_yang
 */
public abstract class Pull2RefreshListView2VolleyFragment<T> extends Fragment
        implements OnClickListener,
        PullToRefreshLayout.OnRefreshListener,
        ReLoginUtils.CallBack_ReLogin, PullableListView2.OnLoadListener {
    private static final int HANDLDER_REQUEST = 100;
    private static final int HANDLDER_DOING = 101;
    private static final int HANDLDER_SUCCESS = 102;
    private static final int HANDLDER_FAILURE = 103;
    private static final int HANDLDER_LOGINFAILURE = 105;
    public PullableListView2 lv_center;// 加载中间内容的listview
    public PullToRefreshLayout pullToRefreshLayout;
    public TextView tv_refresh;
    public List<T> mList;
    public ListItem<T> itemTemp;
    public CommonAdapter adapter;
    public View view;// infalte的布局
    public RelativeLayout relative_refresh;
    LinearLayout myContainer;// 新建容器
    int in = 0;
    int reLoginTimes = 0;
    private Context mContext;
    private String pi = "1";
    private int oldCount = 0;
    private int totalCount = 0;
    private int newCount = 0;
    private String inter = "";
    private int type = 0;
    private JSONObject jsonObject;
    private int itemLayoutId;
    private int tryCount = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLDER_REQUEST:
                    try {
                        if (msg.arg1 != 2) {
                            CustomProgressDialogUtils.showProcessDialog(mContext,
                                    "拼命加载中...");
                        }
                        stringRequestMethod();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HANDLDER_SUCCESS:
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
                case HANDLDER_LOGINFAILURE:
                    CustomProgressDialogUtils.dismissProcessDialog();
                    Y.y("HANDLDER_LOGINFAILURE");
//                    if (tryCount > 1) {
//                        Utils.showToast("未能获取登录信息,请重新登录");
//                    }
                    reLoginTimes++;
                    Y.y("relogintimes:" + reLoginTimes);
                    if (reLoginTimes <= 1) {
                        Y.y("真的重登：1+reLoginTimes：" + reLoginTimes);
                        ReLoginUtils.newInstance().reLogin(mContext,
                                Pull2RefreshListView2VolleyFragment.this);
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
                        if (null == adapter || adapter.isEmpty()) {
                            relative_refresh.setVisibility(View.VISIBLE);
                            relative_refresh.setClickable(true);
                            relative_refresh.setEnabled(true);
                        } else {
                            relative_refresh.setVisibility(View.GONE);
                            relative_refresh.setClickable(false);
                            relative_refresh.setEnabled(false);
                        }
//                        Intent intentLogin = new Intent(mContext,
//                                LoginActivity.class);
//                        startActivity(intentLogin);
//                        ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
//                                R.anim.slide_left_out);
                    }
                    break;
                case HANDLDER_FAILURE:
                    String mess_failure = (String) msg.obj;
                    Utils.showToast((mess_failure == null || ""
                            .equals(mess_failure)) ? "访问服务器出错啦！" : mess_failure);
                    // Utils.dismissProcessDialog();
                    CustomProgressDialogUtils.dismissProcessDialog();
                    Y.y("null == adapter:" + (null == adapter));
                    if (null == adapter || adapter.isEmpty()) {
                        relative_refresh.setVisibility(View.VISIBLE);
                        relative_refresh.setClickable(true);
                        relative_refresh.setEnabled(true);
                    } else {
                        relative_refresh.setVisibility(View.GONE);
                        relative_refresh.setClickable(false);
                        relative_refresh.setEnabled(false);
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
                case 1:
                    // 登录失败
                    // Utils.dismissProcessDialog();
                    CustomProgressDialogUtils.dismissProcessDialog();
                    String mess = "";
                    if (null != msg.obj && !"".equals(msg.obj)) {
                        mess = (String) msg.obj;
                    }
                    if (null == adapter || adapter.isEmpty()) {
                        relative_refresh.setVisibility(View.VISIBLE);
                    }
                    break;
                case 3:
                    // error
                    // Utils.showToast("连接超时");
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private int countTry = 0;

    /**
     * <pre>
     *
     * @return </pre>
     */
    public Pull2RefreshListView2VolleyFragment(String inter, JSONObject jsonObject) {
        // TODO Auto-generated constructor stub
        this.inter = inter;
        this.jsonObject = jsonObject;
    }

    /**
     * <pre>
     *
     * @return </pre>
     */
    public Pull2RefreshListView2VolleyFragment(String inter, JSONObject jsonObject, int type) {
        // TODO Auto-generated constructor stub
        this.inter = inter;
        this.jsonObject = jsonObject;
        this.type = type;
    }

    public Pull2RefreshListView2VolleyFragment(ListItem<T> itemTemp) {
        // TODO Auto-generated constructor stub
        this.itemTemp = itemTemp;
    }

    public Pull2RefreshListView2VolleyFragment() {
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
     * <p/>
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
            view = inflater.inflate(R.layout.pull2refresh_all_listview, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            if (type != 1) {
                adapter = null;
                initView();
                initData();
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
        msg.what = HANDLDER_REQUEST;
        msg.obj = inter;
        handler.sendMessage(msg);
    }

    private void stringRequestMethod() {
        // TODO Auto-generated method stub
        JSONObject jsonObject = getJSONObject();
        try {
            jsonObject.put("pageNum", pi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> params = VolleyUtils.returnParams(jsonObject,
                inter);
        StringRequest stringRequest = new StringRequest(Method.POST, Configs.BASE_SERVER_URL,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomProgressDialogUtils.dismissProcessDialog();
//                Y.y("all_onResponse:" + response);
                VolleyUtils.parseJson(response);
                if (response != null) {
                    String mess = VolleyUtils.msg;
                    String flag = VolleyUtils.flag;
                    try {
                        if (Configs.SUCCESS.equals(flag)) {
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLDER_SUCCESS;
                            msg.obj = VolleyUtils.dataBodyJSONObject
                                    .toString();
                            handler.sendMessage(msg);
                        } else if (mess.contains("数据校验") || mess.contains("userId")) {
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLDER_LOGINFAILURE;
                            handler.sendMessage(msg);
//                            Utils.showToast(mess);
                        } else {
                            Y.y("HANDLDER_FAILURE:" + "oncomplete");
                            Message msg1 = handler.obtainMessage();
                            msg1.what = HANDLDER_FAILURE;
                            msg1.obj = mess;
                            handler.sendMessage(msg1);
                        }
                        //
                    } catch (Exception e) {
                        Y.y("HANDLDER_FAILURE:" + "oncomplete"
                                + e.getMessage());
                        Message msg = handler.obtainMessage();
                        msg.what = HANDLDER_FAILURE;
                        handler.sendMessage(msg);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressDialogUtils.dismissProcessDialog();
                Message msg = handler.obtainMessage();
                msg.what = HANDLDER_FAILURE;
                handler.sendMessage(msg);
                Y.y("Pull2RefreshListView2VolleyFragment_error:"
                        + error.getMessage());
            }
        });
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                inter + "");
    }

    public JSONObject getJSONObject() {
        // TODO Auto-generated method stub
        return jsonObject;
    }

    private void initView() {
        // TODO Auto-generated method stub
        lv_center = (PullableListView2) view.findViewById(R.id.lv_center);
        lv_center.setOnLoadListener(this);
        relative_refresh = (RelativeLayout) view
                .findViewById(R.id.relative_refresh);
        relative_refresh.setOnClickListener(this);
        pullToRefreshLayout = (PullToRefreshLayout) view
                .findViewById(R.id.refresh_view);
        pullToRefreshLayout.setOnRefreshListener(this);
        tv_refresh = (TextView) view.findViewById(R.id.loadView);
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
            case R.id.relative_refresh:
                tryCount++;
                relative_refresh.setVisibility(View.GONE);
                Message msg = handler.obtainMessage();
                msg.what = HANDLDER_REQUEST;
                handler.sendMessage(msg);
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
//            Y.y("父类：response:" + response);
            try {
                listItem.flag = json.optString("flag", "");
                listItem.msg = json.optString("msg", "");
                listItem.pi = json.optString("pageNum", "");
                Y.y("父类：" + listItem.pi);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
//                Y.y("json!=null");
                listItem.mdatas.add(parseJson(json));
//                Y.y("listItem==null:" + (listItem == null));
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
//        Y.y("dealData_all");
        lv_center.setLoadmoreVisible(false);
        try {
            ListItem<T> listItem = parseJson(response);
            mList = listItem.mdatas;
            pi = listItem.pi;
            if (listItem.isDataNULL) {
                pi = "-1";
            }
            if ("-1".equals(pi)) {
                lv_center.setHasMoreData(false);
            } else {
                lv_center.setHasMoreData(true);
            }
            try {
                if (Integer.parseInt(pi) > 1) {
                    lv_center.setLoadmoreVisible(true);
                } else {
                    lv_center.setLoadmoreVisible(false);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                lv_center.setLoadmoreVisible(false);
            }
            if (mList.size() == 0 && adapter == null) {
                relative_refresh.setVisibility(View.VISIBLE);
            } else {
                relative_refresh.setVisibility(View.GONE);
                relative_refresh.setClickable(false);
                relative_refresh.setEnabled(false);
                lv_center.setVisibility(View.VISIBLE);
                if (adapter == null) {
                    mContext = getActivity();
                    itemLayoutId = R.layout.common_layout;
                    initAdapter(mContext, itemLayoutId);
                    newCount = mList.size();
                    totalCount = 0;
                    oldCount = newCount;
                    adapter = new CommonAdapter(mContext, mList, itemLayoutId);
                    lv_center.setAdapter(adapter);
                } else if (!listItem.isDataNULL) {
                    newCount = mList.size();
                    totalCount += oldCount;
                    oldCount = newCount;
//                    Y.y("newCount:" + newCount);
//                    Y.y("totalCount:" + totalCount);
//                    Y.y("oldCount:" + oldCount);
                    adapter.addDatas(mList);
                    adapter.notifyDataSetChanged();
                }
                // mList=null;
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
            // MLog.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    /**
     * 结果从其他页面传递
     */
    public void dealData(ListItem<T> listItem) {
        relative_refresh.setClickable(false);
        relative_refresh.setEnabled(true);
        try {
            mList = listItem.mdatas;
//            Y.y("我在刷新layout里面_1" + mList.size());
            pi = listItem.pi;
            if (listItem.isDataNULL) {
                pi = "-1";
            }
            if (mList.size() == 0 && adapter == null) {
                relative_refresh.setVisibility(View.VISIBLE);
            } else {
                relative_refresh.setVisibility(View.GONE);
                relative_refresh.setClickable(false);
                relative_refresh.setEnabled(false);
                if (adapter == null) {
                    mContext = getActivity();
                    itemLayoutId = R.layout.common_layout;
                    initAdapter(mContext, itemLayoutId);
                    newCount = mList.size();
                    totalCount = 0;
                    oldCount = newCount;
                    adapter = new CommonAdapter(mContext, mList, itemLayoutId);
                    lv_center.setAdapter(adapter);
                } else if (!listItem.isDataNULL) {
                    newCount = mList.size();
                    totalCount += oldCount;
                    oldCount = newCount;
//                    Y.y("newCount:" + newCount);
//                    Y.y("totalCount:" + totalCount);
//                    Y.y("oldCount:" + oldCount);
                    adapter.addDatas(mList);
                    adapter.notifyDataSetChanged();
                }
                // mList=null;
            }

        } catch (Exception e) {
            if (null == adapter || adapter.isEmpty()) {
                relative_refresh.setVisibility(View.VISIBLE);
                relative_refresh.setClickable(false);
                relative_refresh.setEnabled(true);
            } else {
                relative_refresh.setVisibility(View.GONE);
                relative_refresh.setClickable(false);
                relative_refresh.setEnabled(false);
            }
        }
    }

    public void initAdapter(Context context, int itemLayoutId) {
        this.mContext = context;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
        lv_center.setLoadmoreVisible(false);
        if (null == adapter) {
            pi = "1";
            Message msg = handler.obtainMessage();
            msg.what = HANDLDER_REQUEST;
            handler.sendMessage(msg);
//            Y.y("onRefresh1");
        } else {
            if (null != mList) {
                mList.clear();
            }
            pi = "1";
            adapter.notifyDataSetChanged();
            adapter = null;
            Message msg = handler.obtainMessage();
            msg.what = HANDLDER_REQUEST;
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
            msg.what = HANDLDER_REQUEST;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onLoad(PullableListView2 pullableListView2) {
        try {
            if (pi.equals("-1")) {
                Utils.showToast("没有数据了");
                lv_center.setHasMoreData(false);
                lv_center.setLoadmoreVisible(false);
            } else {
                Message msg = handler.obtainMessage();
                msg.what = HANDLDER_REQUEST;
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
        if (null == adapter || adapter.isEmpty()) {
            relative_refresh.setVisibility(View.VISIBLE);
            relative_refresh.setClickable(true);
            relative_refresh.setEnabled(true);
        } else {
            relative_refresh.setVisibility(View.GONE);
            relative_refresh.setClickable(false);
            relative_refresh.setEnabled(false);
        }
        Intent intentLogin = new Intent(mContext,
                LoginActivity.class);
        startActivity(intentLogin);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in,
                R.anim.slide_left_out);
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
                relative_refresh.setVisibility(View.VISIBLE);
                relative_refresh.setClickable(false);
                relative_refresh.setEnabled(false);
            } catch (Exception e) {
            }
        }
    }
}
