package com.my.pulltorefresh.fragment_part;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.my.configs.Configs;
import com.my.item.ListItemChild;
import com.my.item.ViewHolder;
import com.my.other.CommonAdapter;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.utils.PreferencesHelper;
import com.my.utils.ThreadPoolUtil;
import com.my.utils.VolleyUtils;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_homepage_1 extends Fragment implements View.OnClickListener, CallBack_Banner {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    //////////////////////////
    private int bannerSize;
    private ViewPager viewPager;
    RelativeLayout relative_center;
    LinearLayout linear_1, linear_2, linear_3;
    TextView tv_location, tv_count, tv_welcome;
    TextView tv_pop1, tv_pop2, tv_pop3;
    ImageView iv_pop1, iv_pop2, iv_pop3;
    ///////////////////以下为悬停布局
    LinearLayout linear_1_hover, linear_2_hover, linear_3_hover;
    TextView tv_pop1_hover, tv_pop2_hover, tv_pop3_hover;
    ImageView iv_pop1_hover, iv_pop2_hover, iv_pop3_hover;
    ////////////////////
    View viewSplit,viewSplit_hover;
    Animation animation = null;
    ImageView cursor;
    int num = 3;

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
            view = inflater.inflate(R.layout.fragment_homepage1, null);
            view.setMinimumHeight(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight());
            view.setMinimumWidth(((WindowManager) getActivity()
                    .getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth());
            testData();
            initView();
            initEvent();
            initBanner();
            myContainer.addView(view);
        } else {
            myContainer.removeAllViews();
            myContainer = new LinearLayout(getActivity());
            myContainer.addView(view);
        }
        return myContainer;
    }

    private void testData() {
        if (listCity == null) {
            listCity = new ArrayList<>();
            ListItem item = new ListItem();
            item.title = "全部";
            item.flag = "true";
            listCity.add(item);
            item = new ListItem();
            item.title = "重庆";
            listCity.add(item);
            item = new ListItem();
            item.title = "四川";
            listCity.add(item);
        }
        if (listCity2 == null) {
            listCity2 = new ArrayList<>();
            ListItem item = new ListItem();
            item.title = "全部";
            item.flag = "true";
            listCity2.add(item);
            item = new ListItem();
            item.title = "渝中区";
            listCity2.add(item);
            item = new ListItem();
            item.title = "沙坪坝区";
            listCity2.add(item);
        }


        lv_test = (ListView) view.findViewById(R.id.lv_test);
        ArrayList<ListItem> array = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ListItem item = new ListItem();
            item.title = "我是测试" + "  " + i;
            array.add(item);
        }
        CommonAdapter adapterTest = new CommonAdapter(getContext(), array, R.layout.listitem_pop) {
            @Override
            public View getListItemview(ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                viewHolder.setText(R.id.tv_title, item.title);
                return null;
            }
        };
        viewHeader1 = LayoutInflater.from(mContext).inflate(R.layout.lv_hearderview1, null);
        viewHeader2 = LayoutInflater.from(mContext).inflate(R.layout.lv_hearderview2, null);
        hover=view.findViewById(R.id.hover);
        lv_test.addHeaderView(viewHeader1);
        lv_test.addHeaderView(viewHeader2);
        lv_test.setAdapter(adapterTest);
        lv_test.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                Y.y("onScrollStateChanged:" + i);
            }

            @Override
            public void onScroll(AbsListView paramAbsListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    View child0 = paramAbsListView.getChildAt(0);
//                    if (child0 == null || child0.getTop() == 0) {
//
//                    }
//                }
                if (firstVisibleItem >= 1) {
                    hover.setVisibility(View.VISIBLE);
                } else {
                    hover.setVisibility(View.INVISIBLE);
                }
                Y.y("aaaaaaaaaa:" + hover.getVisibility());
                Y.y("onScroll_1:" + firstVisibleItem);
                Y.y("onScroll_2:" + visibleItemCount);
                Y.y("onScroll_3:" + totalItemCount);
            }
        });

    }

    ListView lv_test;
    View viewHeader1,viewHeader2, hover;
    android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int type = msg.arg1;
            viewPager.setCurrentItem(type);
        }
    };

    private void initView() {
        hover=view.findViewById(R.id.hover);
        viewPager = (ViewPager) viewHeader1.findViewById(R.id.viewPager);
        tv_location = (TextView) viewHeader1.findViewById(R.id.tv_location);
        tv_count = (TextView) viewHeader1.findViewById(R.id.tv_count);
        tv_welcome = (TextView) viewHeader1.findViewById(R.id.tv_welcome);

        linear_1 = (LinearLayout) viewHeader2.findViewById(R.id.linear_1);
        linear_2 = (LinearLayout) viewHeader2.findViewById(R.id.linear_2);
        linear_3 = (LinearLayout) viewHeader2.findViewById(R.id.linear_3);

        iv_pop1 = (ImageView) viewHeader2.findViewById(R.id.iv_pop1);
        iv_pop2 = (ImageView) viewHeader2.findViewById(R.id.iv_pop2);
        iv_pop3 = (ImageView) viewHeader2.findViewById(R.id.iv_pop3);


        tv_pop1 = (TextView) viewHeader2.findViewById(R.id.tv_pop1);
        tv_pop2 = (TextView) viewHeader2.findViewById(R.id.tv_pop2);
        tv_pop3 = (TextView) viewHeader2.findViewById(R.id.tv_pop3);

        relative_center = (RelativeLayout) view.findViewById(R.id.relative_center);
        viewSplit = viewHeader2.findViewById(R.id.viewSplit);
        //////////////////////////////////////////
        viewSplit_hover = view.findViewById(R.id.viewSplit_hover);
        linear_1_hover = (LinearLayout) view.findViewById(R.id.linear_1_hover);
        linear_2_hover = (LinearLayout) view.findViewById(R.id.linear_2_hover);
        linear_3_hover = (LinearLayout) view.findViewById(R.id.linear_3_hover);

        iv_pop1_hover = (ImageView) view.findViewById(R.id.iv_pop1_hover);
        iv_pop2_hover = (ImageView) view.findViewById(R.id.iv_pop2_hover);
        iv_pop3_hover = (ImageView) view.findViewById(R.id.iv_pop3_hover);

        tv_pop1_hover = (TextView) view.findViewById(R.id.tv_pop1_hover);
        tv_pop2_hover = (TextView) view.findViewById(R.id.tv_pop2_hover);
        tv_pop3_hover = (TextView) view.findViewById(R.id.tv_pop3_hover);


        linear_1.setOnClickListener(this);
        linear_2.setOnClickListener(this);
        linear_3.setOnClickListener(this);
        linear_1_hover.setOnClickListener(this);
        linear_2_hover.setOnClickListener(this);
        linear_3_hover.setOnClickListener(this);
    }

    private void initEvent() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_1:
                showPop(getContext(), viewSplit);
                break;
            case R.id.linear_1_hover:
                showPop(getContext(), viewSplit_hover);
                break;
            case R.id.linear_2:
            case R.id.linear_2_hover:
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.second_mark_rotate_h);
                if (condition2.equals("1")) {
                    condition2 = "2";
                } else {
                    anim = AnimationUtils.loadAnimation(getContext(), R.anim.second_mark_rotate_n);
                    condition2 = "1";
                }
                iv_pop2.startAnimation(anim);
                iv_pop2_hover.startAnimation(anim);
                Fragment fragment2 = null;
//                changeFragment(fragment2, 1, condition1 + condition2 + condition3);
                break;
            case R.id.linear_3:
            case R.id.linear_3_hover:
                Animation anim3 = AnimationUtils.loadAnimation(getContext(), R.anim.second_mark_rotate_h);
                if (condition3.equals("1")) {
                    condition3 = "2";
                    tv_pop3.setText(getText(R.string.pop3_2));
                    tv_pop3_hover.setText(getText(R.string.pop3_2));
                } else {
                    anim3 = AnimationUtils.loadAnimation(getContext(), R.anim.second_mark_rotate_n);
                    condition3 = "1";
                    tv_pop3.setText(getText(R.string.pop3));
                    tv_pop3_hover.setText(getText(R.string.pop3));
                }
                iv_pop3.startAnimation(anim3);
                iv_pop3_hover.startAnimation(anim3);
                Fragment fragment3 = null;
//                changeFragment(fragment3, 1, condition1 + condition2 + condition3);
                break;
        }
    }

    private ArrayList<ListItem> listCity;
    private ArrayList<ListItem> listCity2;
    private int currentPosition1 = 0;
    private int currentPosition2 = 0;
    private String condition1 = "";
    private String condition2 = "";
    private String condition3 = "";
    private PopupWindow popwindow = null;

    private void showPop(Context context, final View v) {
        // TODO Auto-generated method stub
        popwindow = null;
        View viewPop = LayoutInflater.from(getContext()).inflate(
                R.layout.inflater_pop_lv1, null);
        LinearLayout relative = (LinearLayout) viewPop
                .findViewById(R.id.relative_pop_item);
        relative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popwindow.isShowing()) {
                    popwindow.dismiss();
                }
            }
        });
        // int width_screen = ScreenUtils.getScreenWidth(this);
        // width_screen=ScreenUtils.dip2px(this, width_screen);
        popwindow = new PopupWindow(viewPop,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置整个popupwindow的弹出，收起方式
        popwindow.setAnimationStyle(R.style.popWindowAnim);
        // 需要设置一下此参数，点击外边可消失
        popwindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popwindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popwindow.setFocusable(true);
        // 加载动画
        final Animation animation_n = AnimationUtils.loadAnimation(getContext(),
                R.anim.second_mark_rotate_n);
        // 以下两个属性设置位移动画的停止
        animation_n.setFillEnabled(true);
        animation_n.setFillAfter(true);
        // 加载动画
        final Animation animation_h = AnimationUtils.loadAnimation(getContext(),
                R.anim.second_mark_rotate_h);
        iv_pop1.startAnimation(animation_n);
        // 以下两个属性设置位移动画的停止
//         animation_h.setFillEnabled(true);
//         animation_h.setFillAfter(true);
//         TextView tv_1=view_pop_1.findViewById(R.id.);
//         Animation animation = AnimationUtils.loadAnimation(context,
//         R.anim.myset_show_pop);
//         animation.setFillAfter(false);
//         lv.setAnimationCacheEnabled(false);
//         lv.setAnimation(animation);
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                iv_pop1.startAnimation(animation_h);
            }
        });
        ListView lv1 = (ListView) viewPop.findViewById(R.id.lv_pop1);
        final ListView lv2 = (ListView) viewPop.findViewById(R.id.lv_pop2);
        final CommonAdapter adapter1 = new CommonAdapter(getContext(), listCity, R.layout.listitem_pop) {
            @Override
            public View getListItemview(ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                viewHolder.setText(R.id.tv_title, item.title);
                if (item.flag.equals("true")) {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_blue));
                } else {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_white));
                }
                ((ImageView) viewHolder.getView(R.id.iv_left)).setImageResource(R.drawable.selector_lv_item);
                return null;
            }
        };
        final CommonAdapter adapter2 = new CommonAdapter(getContext(), listCity2, R.layout.listitem_pop) {
            @Override
            public View getListItemview(ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                viewHolder.setText(R.id.tv_title, item.title);
                if (item.flag.equals("true")) {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_blue));
                } else {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_white));
                }
                ((ImageView) viewHolder.getView(R.id.iv_left)).setImageResource(R.drawable.selector_lv_item);
                return null;
            }
        };
        if (listCity2 == null || listCity2.size() <= 0) {
            lv2.setVisibility(View.GONE);
        } else {
            lv2.setVisibility(View.VISIBLE);
        }
        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                listCity.get(currentPosition1).flag = "false";
                listCity.get(i).flag = "true";
                currentPosition1 = i;
                adapter1.notifyDataSetChanged();


                listCity2.clear();
                ListItem item = new ListItem();
                item.title = "我在里面";
                item.flag = "true";
                listCity2.add(item);
                item = new ListItem();
                item.title = "南岸区";
                listCity2.add(item);
                item = new ListItem();
                item.title = "九龙坡区";
                listCity2.add(item);


                listCity2.get(currentPosition2).flag = "false";
                listCity2.get(0).flag = "true";
                currentPosition2 = 0;
                adapter2.notifyDataSetChanged();
            }
        });
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listCity2.get(currentPosition2).flag = "false";
                listCity2.get(i).flag = "true";
                currentPosition2 = i;
                adapter2.notifyDataSetChanged();

                popwindow.dismiss();
                Fragment fragment = null;
//                changeFragment(fragment, 1, condition1 + condition2 + condition3);
            }
        });
        popwindow.showAsDropDown(v);
    }

    private void initBanner() {
        JSONObject jsonObject = new JSONObject();
        HashMap<String, String> params = VolleyUtils.returnParams(jsonObject,
                Configs.banner, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.BASE_SERVER_URL,
                params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("initbanner:" + response);
                VolleyUtils.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtils.flag)) {
                    JSONArray jsonArray = VolleyUtils
                            .optJSONArray(VolleyUtils.dataBodyJSONObject, "bannerList");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        final ArrayList<ListItem> list = new ArrayList<ListItem>();
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            ListItem item = new ListItem();
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            if (null != jsonObject1) {
                                item.date = jsonObject1.optString("startMills", "");
                                item.banner = jsonObject1.optString("image", "");
                                item.string3 = jsonObject1.optString("title", "");
                                item.bannerUrl = jsonObject1.optString("url", "");
                            }
                            list.add(item);
                        }
                        ArrayList<android.support.v4.app.Fragment> listFragments = new ArrayList<android.support.v4.app.Fragment>();
                        bannerSize = list.size();
                        for (int i = 0; i < bannerSize; i++) {
                            ListItem item = list.get(i);
                            Fragment_Banner fragment_banner = Fragment_Banner.newInstance(item, i, bannerSize);
                            listFragments.add(fragment_banner);
                            fragment_banner.setCallBack(Fragment_homepage_1.this);
                        }
                        // TODO Auto-generated method stub
                        GuideFragmentPagerAdapter adapter = new GuideFragmentPagerAdapter(
                                getChildFragmentManager(), listFragments);
                        viewPager.setAdapter(adapter);
                        initGesture();
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
                Configs.banner);
    }

    private void initGesture() {
        ThreadPoolUtil.getInstanceLimitedTaskExecutor(2).submit(new Runnable() {
            @Override
            public void run() {
                while (PreferencesHelper.getBoolean("homepage")) {
                    try {
                        Thread.sleep(5000);
//                        Message message = handler.obtainMessage();
                        Message message = new Message();
                        message.arg1 = (viewPager.getCurrentItem() + 1) % bannerSize;
                        mHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
}
