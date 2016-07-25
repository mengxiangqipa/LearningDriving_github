package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.CoachDetailActivity;
import com.gooddream.learningdriving.activity.LoginDialogActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.gooddream.learningdriving.interfaces.CallBack_Banner;
import com.my.configs.Configs;
import com.my.configs.ConstantsME;
import com.my.customviews.CustomTextSwitcher;
import com.my.customviews.CustomRoundImageView_new;
import com.my.other.GuideFragmentPagerAdapter;
import com.my.pulltorefresh.PullToRefreshLayout;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.pulltorefresh.fragment.Pull2RefreshExpandableListViewVolleyFragment;
import com.my.pulltorefresh.fragment.ViewHolder;
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
import java.util.List;

/**
 * 群约
 *
 * @author Administrator
 */
public class Fragment_choose_coach_pull2refresh_expanable extends
        Pull2RefreshExpandableListViewVolleyFragment<ListItem> implements OnClickListener, CallBack_Banner {
    String type = "1";

    Context mContext;
    com.nostra13.universalimageloader.core.ImageLoader imageLoader2;
    private int bannerSize;
    private int adScrollBarSize;
    private int positionADscrollbar;
    private ViewPager viewPager;
    LinearLayout linear_1, linear_2, linear_3;
    TextView tv_location, tv_count;
    CustomTextSwitcher tv_welcome;
    TextView tv_pop1, tv_pop2, tv_pop3;
    ImageView iv_pop1, iv_pop2, iv_pop3;
    ///////////////////以下为悬停布局
    LinearLayout linear_1_hover, linear_2_hover, linear_3_hover;
    TextView tv_pop1_hover, tv_pop2_hover, tv_pop3_hover;
    ImageView iv_pop1_hover, iv_pop2_hover, iv_pop3_hover;
    ////////////////////
    View viewSplit, viewSplit_hover;
    Animation animation = null;
    ImageView cursor;
    int num = 3;

    public static Fragment_choose_coach_pull2refresh_expanable newInstance(int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        Fragment_choose_coach_pull2refresh_expanable newInstance = new Fragment_choose_coach_pull2refresh_expanable(post2get, inter, jsonObjectData, jsonObjectPage);
        final Bundle bundle = new Bundle();
//        bundle.putString("funid", funid);
        newInstance.setArguments(bundle);
        return newInstance;
    }

    public Fragment_choose_coach_pull2refresh_expanable(int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        super(post2get, inter, jsonObjectData, jsonObjectPage);
    }

    static JSONObject jsonObjectData;
    static JSONObject jsonObjectPage;

    static {
        jsonObjectData = new JSONObject();
        jsonObjectPage = new JSONObject();
        try {
            jsonObjectData.put("latitude",
                    PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL));
            jsonObjectData.put("longitude",
                    PreferencesHelper.getString(ConstantsME.LONGTITUDE_ORIGINAL));
            JSONObject jsonObjectAreaName = new JSONObject();
            jsonObjectAreaName.put("areaName", "");
            jsonObjectAreaName.put("productName", "");
            jsonObjectAreaName.put("projectType", "C1");
            JSONObject jsonObjectOrder = new JSONObject();
            jsonObjectOrder.put("starLevel", "desc");
//            jsonObjectOrder.put("passRate", "desc");
            jsonObjectData.put("searchFilter", jsonObjectAreaName);
            jsonObjectData.put("order", jsonObjectOrder);

            jsonObjectPage.put("pageIndex", 0);
            jsonObjectPage.put("pageSize", 20);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Fragment_choose_coach_pull2refresh_expanable() {
        super(Request.Method.GET, Configs.coachList, jsonObjectData, jsonObjectPage);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        PreferencesHelper.putBoolean("homepage", true);
        Bundle bundle = getArguments() != null ? getArguments() : null;
        if (null != bundle) {

        }
        mContext = getActivity();
        imageLoader2 = ImageLoaderUtils_nostra13.initImageLoader(mContext);
    }

    View viewHeader1, viewHeader2, hover;
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
                    Y.y("mHandler:" + type);
                    break;
                case 51:
//                    Y.y("mHandler:" + msg);
                    int position = msg.arg1;
//                    Y.y("mHandler:"+listADscrollBar.get(position).string1);
                    tv_welcome.makeView();


                    tv_welcome.next();
                    tv_welcome.setText(listADscrollBar.get(position).string1);
                    tv_welcome.post(new Runnable() {
                        @Override
                        public void run() {
                            positionADscrollbar = (positionADscrollbar + 1) % adScrollBarSize;
                        }
                    });
//                    Y.y("mHandler:"+"yaya1");
//                    Y.y("mHandler:"+"yaya2");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void firstRequestHideHover() {
        hover.setVisibility(View.INVISIBLE);
    }

    @Override
    public void addHover(RelativeLayout relative_hover) {
        hover = LayoutInflater.from(mContext).inflate(R.layout.hover, null);
        relative_hover.addView(hover);
        hover.setVisibility(View.INVISIBLE);
    }

    @Override
    public void addHoverTitle(RelativeLayout relative_hover_title) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.hover_title1, null);
        v.setMinimumWidth(Utils.getScreenWidth(mContext));
        relative_hover_title.addView(v);
    }

    private RelativeLayout relative_hover_bottom;

    @Override
    public void addHoverBottom(final RelativeLayout relative_hover_bottom) {
        this.relative_hover_bottom = relative_hover_bottom;
        if (PreferencesHelper.getBoolean(ConstantsME.showBottomBar) && PreferencesHelper.getBoolean(ConstantsME.LOGINED) && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))
//                && relative_hover_bottom.getChildCount() <= 1
                ) {
            relative_hover_bottom.removeAllViews();
            relative_hover_bottom.setVisibility(View.VISIBLE);
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.hover_bottom_bar, null);
            CustomRoundImageView_new iv_icon = (CustomRoundImageView_new) inflate.findViewById(R.id.iv_icon);
            TextView tv_1 = (TextView) inflate.findViewById(R.id.tv_1);
            TextView tv_2 = (TextView) inflate.findViewById(R.id.tv_2);
            TextView tv_3 = (TextView) inflate.findViewById(R.id.tv_3);
            TextView tv_4 = (TextView) inflate.findViewById(R.id.tv_4);
            try {
                imageLoader2.displayImage(PreferencesHelper.getString(ConstantsME.teacherUrl).split(";")[0],
                        iv_icon, ImageLoaderUtils_nostra13.getFadeOptions(
                                R.drawable.bg_default_icon,
                                R.drawable.bg_default_icon,
                                R.drawable.bg_default_icon));
            } catch (Exception e) {

            }
            tv_1.setText(PreferencesHelper.getString(ConstantsME.teacherName));
            tv_2.setText(PreferencesHelper.getString(ConstantsME.teacherType));
            tv_3.setText(PreferencesHelper.getString(ConstantsME.teacherProductName));
            tv_4.setText("¥" + PreferencesHelper.getString(ConstantsME.teacherFee));
            inflate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),
                            CoachDetailActivity.class);
                    intent.putExtra("id", PreferencesHelper.getString(ConstantsME.teacherId));
                    intent.putExtra(ConstantsME.fromBottomBar, true);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
//                    PreferencesHelper.putBoolean(ConstantsME.showBottomBar, false);
                    relative_hover_bottom.removeAllViews();
                }
            });
            inflate.setMinimumWidth(Utils.getScreenWidth(mContext));
            inflate.setMinimumHeight(Utils.dip2px(mContext, 40));
            relative_hover_bottom.addView(inflate);
        } else {
            relative_hover_bottom.setVisibility(View.INVISIBLE);
            relative_hover_bottom.removeAllViews();
        }
    }

    @Override
    public void onScroll_(AbsListView paramAbsListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem >= 1) {
            hover.setVisibility(View.VISIBLE);
        } else {
            hover.setVisibility(View.INVISIBLE);
        }
//        Y.y("onScroll_1:" + firstVisibleItem + "    " + visibleItemCount + "   " + totalItemCount);
    }

    @Override
    public void initData() {
        viewHeader1 = LayoutInflater.from(mContext).inflate(R.layout.lv_hearderview1, null);
        viewHeader2 = LayoutInflater.from(mContext).inflate(R.layout.lv_hearderview2, null);
        lv_center_expanable.addHeaderView(viewHeader1);
        lv_center_expanable.addHeaderView(viewHeader2);

        pullToRefreshLayout.setPullDownContentText("下拉刷新数据");
        //pullToRefreshLayout.setPullUpContentText("宁静以致远");
        pullToRefreshLayout.setPullDownStateText("刷新成功");
        int color = getResources().getColor(R.color.default_text_blue);
        pullToRefreshLayout.setHeadViewBackgroundColor(color);
        // pull.setHeadViewBackgroundResource(R.drawable.allview_refreshing);
        pullToRefreshLayout.setPullDownContentTextSize(14);
        pullToRefreshLayout.setPullDownStateTextSize(12);
        lv_center_expanable.setCanPullUp(false);

        initPopData();
        initViewM();
        initDataM();
        initBanner();
        initADscrollBar();
    }

    private void initDataM() {
        String address = PreferencesHelper.getString(ConstantsME.ADDRESS);
        if (!TextUtils.isEmpty(address)) {
            tv_location.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
            tv_location.setText(address);
        }
        tv_welcome.setText("好梦学车成就梦想");
    }

    private void initViewM() {
        viewPager = (ViewPager) viewHeader1.findViewById(R.id.viewPager);
        tv_location = (TextView) viewHeader1.findViewById(R.id.tv_location);
        tv_count = (TextView) viewHeader1.findViewById(R.id.tv_count);
        tv_welcome = (CustomTextSwitcher) viewHeader1.findViewById(R.id.tv_welcome);

        linear_1 = (LinearLayout) viewHeader2.findViewById(R.id.linear_1);
        linear_2 = (LinearLayout) viewHeader2.findViewById(R.id.linear_2);
        linear_3 = (LinearLayout) viewHeader2.findViewById(R.id.linear_3);

        iv_pop1 = (ImageView) viewHeader2.findViewById(R.id.iv_pop1);
        iv_pop2 = (ImageView) viewHeader2.findViewById(R.id.iv_pop2);
        iv_pop3 = (ImageView) viewHeader2.findViewById(R.id.iv_pop3);


        tv_pop1 = (TextView) viewHeader2.findViewById(R.id.tv_pop1);
        tv_pop2 = (TextView) viewHeader2.findViewById(R.id.tv_pop2);
        tv_pop3 = (TextView) viewHeader2.findViewById(R.id.tv_pop3);
        viewSplit = viewHeader2.findViewById(R.id.viewSplit);
        //////////////////////////////////////////
        viewSplit_hover = hover.findViewById(R.id.viewSplit_hover);
        linear_1_hover = (LinearLayout) hover.findViewById(R.id.linear_1_hover);
        linear_2_hover = (LinearLayout) hover.findViewById(R.id.linear_2_hover);
        linear_3_hover = (LinearLayout) hover.findViewById(R.id.linear_3_hover);

        iv_pop1_hover = (ImageView) hover.findViewById(R.id.iv_pop1_hover);
        iv_pop2_hover = (ImageView) hover.findViewById(R.id.iv_pop2_hover);
        iv_pop3_hover = (ImageView) hover.findViewById(R.id.iv_pop3_hover);

        tv_pop1_hover = (TextView) hover.findViewById(R.id.tv_pop1_hover);
        tv_pop2_hover = (TextView) hover.findViewById(R.id.tv_pop2_hover);
        tv_pop3_hover = (TextView) hover.findViewById(R.id.tv_pop3_hover);


        linear_1.setOnClickListener(this);
        linear_2.setOnClickListener(this);
        linear_3.setOnClickListener(this);
        linear_1_hover.setOnClickListener(this);
        linear_2_hover.setOnClickListener(this);
        linear_3_hover.setOnClickListener(this);
    }

    private void initPopData() {
        if (listCity == null || listCity.size() <= 0) {
            listCity = new ArrayList<>();
            ListItem item = new ListItem();
            item.title = "重庆";
            item.flag = "true";
            listCity.add(item);
        }
        if (listCity2 == null || listCity2.size() <= 0) {
            listCity2 = new ArrayList<>();
            if (!"".equals(PreferencesHelper.getString(ConstantsME.cityList))) {
                try {
                    JSONArray array = new JSONArray(PreferencesHelper.getString(ConstantsME.cityList));
                    if (null != array) {
                        int lenth = array.length();
                        for (int i = 0; i < lenth; i++) {
                            JSONObject jsonObject = array.optJSONObject(i);
                            String areaName = jsonObject.optString("areaName", "");
                            int teacherCount = jsonObject.optInt("teacherCount", 0);
                            ListItem item1 = new ListItem();
                            if (i == 0) {
                                item1.flag = "true";
                            }
                            item1.title = areaName;
                            item1.temp = teacherCount;
                            listCity2.add(item1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (listOther == null || listOther.size() <= 0) {
            listOther = new ArrayList<>();
            ListItem item = new ListItem();
            item.title = "星级优先";
            item.flag = "true";
            listOther.add(item);
            item = new ListItem();
            item.title = "通过率优先";
            listOther.add(item);
        }
        if (listVehicleType == null || listVehicleType.size() <= 0) {
            listVehicleType = new ArrayList<>();
            if (!"".equals(PreferencesHelper.getString(ConstantsME.vehicleTypeList))) {
                try {
                    JSONArray array = new JSONArray(PreferencesHelper.getString(ConstantsME.vehicleTypeList));
                    if (null != array) {
                        int lenth = array.length();
                        for (int i = 0; i < lenth; i++) {
                            String str = array.optString(i);
                            for (int k = 0; k < 2; k++) {
                                ListItem item1 = new ListItem();
                                if (i == 0 && k == 0) {
                                    item1.flag = "true";
                                }
                                item1.msg = str;
                                if (k == 0) {
                                    item1.address = "C1";
                                } else {
                                    item1.address = "C2";
                                }
                                item1.title = item1.address + " " + item1.msg;
                                listVehicleType.add(item1);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<ListItem> listCity;
    private ArrayList<ListItem> listCity2;
    private ArrayList<ListItem> listOther;
    private ArrayList<ListItem> listVehicleType;
    private ArrayList<ListItem> listADscrollBar;
    private int currentPosition1 = 0;
    private int currentPosition2 = 0;
    private int currentPositionEvaluate = 0;
    private int currentPositionVehicleType = 0;
    private String condition1 = "";
    private String condition2 = "starLevel";
    private String condition3 = "";
    private String condition4 = "C1";
    private PopupWindow popwindow = null;

    private void showPop(Context context, final View v) {
        // TODO Auto-generated method stub
        popwindow = null;
        View viewPop = LayoutInflater.from(getContext()).inflate(
                R.layout.inflater_pop_lv1, null);
        LinearLayout relative = (LinearLayout) viewPop
                .findViewById(R.id.relative_pop_item);
        relative.setOnClickListener(new OnClickListener() {

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
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
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
        if (v.getId() == R.id.viewSplit) {
            iv_pop1.startAnimation(animation_n);
        } else {
            iv_pop1_hover.startAnimation(animation_n);
        }
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
                if (v.getId() == R.id.viewSplit) {
                    iv_pop1.startAnimation(animation_h);
                } else {
                    iv_pop1_hover.startAnimation(animation_h);
                }
            }
        });
        ListView lv1 = (ListView) viewPop.findViewById(R.id.lv_pop1);
        final ListView lv2 = (ListView) viewPop.findViewById(R.id.lv_pop2);
        final com.my.other.CommonAdapter adapter1 = new com.my.other.CommonAdapter(getContext(), listCity, R.layout.listitem_pop_1) {
            @Override
            public View getListItemview(com.my.item.ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
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
        final com.my.other.CommonAdapter adapter2 = new com.my.other.CommonAdapter(getContext(), listCity2, R.layout.listitem_pop2) {
            @Override
            public View getListItemview(com.my.item.ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                viewHolder.setText(R.id.tv_title, item.title);
                viewHolder.setText(R.id.tv_content, item.temp + "");
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
//        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                listCity.get(currentPosition1).flag = "false";
//                listCity.get(i).flag = "true";
//                currentPosition1 = i;
//                adapter1.notifyDataSetChanged();
//
//
//                listCity2.clear();
//                ListItemChild item = new ListItemChild();
//                item.title = "我在里面";
//                item.flag = "true";
//                listCity2.add(item);
//                item = new ListItemChild();
//                item.title = "南岸区";
//                listCity2.add(item);
//                item = new ListItemChild();
//                item.title = "九龙坡区";
//                listCity2.add(item);
//
//
//                listCity2.get(currentPosition2).flag = "false";
//                listCity2.get(0).flag = "true";
//                currentPosition2 = 0;
//                adapter2.notifyDataSetChanged();
//            }
//        });
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentPosition2 == i) {
                    popwindow.dismiss();
                    return;
                }
                listCity2.get(currentPosition2).flag = "false";
                listCity2.get(i).flag = "true";
                currentPosition2 = i;
                adapter2.notifyDataSetChanged();
                popwindow.dismiss();

                condition1 = i == 0 ? "" : listCity2.get(i).title;
                Fragment fragment = null;
//                changeFragment(fragment, 1, condition1 + condition2 + condition3);
                constructJSONObject(condition1, condition2, condition3, condition4);
                refreshData(jsonObjectData, jsonObjectPage);
            }
        });
        popwindow.showAsDropDown(v);
    }

    private void showPop2(Context context, final View v) {
        // TODO Auto-generated method stub
        popwindow = null;
        View viewPop = LayoutInflater.from(getContext()).inflate(
                R.layout.inflater_pop_lv2, null);
        LinearLayout relative = (LinearLayout) viewPop
                .findViewById(R.id.relative_pop_item);
        relative.setOnClickListener(new OnClickListener() {

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
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
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
        if (v.getId() == R.id.viewSplit) {
            iv_pop2.startAnimation(animation_n);
        } else {
            iv_pop2_hover.startAnimation(animation_n);
        }
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                if (v.getId() == R.id.viewSplit) {
                    iv_pop2.startAnimation(animation_h);
                } else {
                    iv_pop2_hover.startAnimation(animation_h);
                }
            }
        });
        ListView lv = (ListView) viewPop.findViewById(R.id.lv_pop);
        final com.my.other.CommonAdapter adapter1 = new com.my.other.CommonAdapter(getContext(), listOther, R.layout.listitem_pop_1) {
            @Override
            public View getListItemview(com.my.item.ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                viewHolder.setText(R.id.tv_title, item.title);
                ((TextView) viewHolder.getView(R.id.tv_title)).setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
                if (item.flag.equals("true")) {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_blue));
                } else {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_white));
                }
                ((ImageView) viewHolder.getView(R.id.iv_left)).setImageResource(R.drawable.selector_lv_item);
                return null;
            }
        };
        lv.setAdapter(adapter1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentPositionEvaluate == i) {
                    popwindow.dismiss();
                    return;
                }
                listOther.get(currentPositionEvaluate).flag = "false";
                listOther.get(i).flag = "true";
                currentPositionEvaluate = i;
                adapter1.notifyDataSetChanged();
                popwindow.dismiss();
                condition2 = "passRate";
//                Fragment fragment = null;
////                changeFragment(fragment, 1, condition1 + condition2 + condition3);
                constructJSONObject(condition1, condition2, condition3, condition4);
                refreshData(jsonObjectData, jsonObjectPage);
            }
        });
        popwindow.showAsDropDown(v);
    }

    private void showPop3(Context context, final View v) {
        // TODO Auto-generated method stub
        popwindow = null;
        View viewPop = LayoutInflater.from(getContext()).inflate(
                R.layout.inflater_pop_lv2, null);
        LinearLayout relative = (LinearLayout) viewPop
                .findViewById(R.id.relative_pop_item);
        relative.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popwindow.isShowing()) {
                    popwindow.dismiss();
                }
            }
        });
        popwindow = new PopupWindow(viewPop,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
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
        if (v.getId() == R.id.viewSplit) {
            iv_pop3.startAnimation(animation_n);
        } else {
            iv_pop3_hover.startAnimation(animation_n);
        }
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                if (v.getId() == R.id.viewSplit) {
                    iv_pop3.startAnimation(animation_h);
                } else {
                    iv_pop3_hover.startAnimation(animation_h);
                }
            }
        });
        ListView lv = (ListView) viewPop.findViewById(R.id.lv_pop);
        final com.my.other.CommonAdapter adapter1 = new com.my.other.CommonAdapter(getContext(), listVehicleType, R.layout.listitem_pop_1) {
            @Override
            public View getListItemview(com.my.item.ViewHolder viewHolder, View view, ListItem item, int position, ViewGroup parent) {
                viewHolder.setText(R.id.tv_title, item.title);
                ((TextView) viewHolder.getView(R.id.tv_title)).setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
                if (item.flag.equals("true")) {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_blue));
                } else {
                    ((ImageView) viewHolder.getView(R.id.iv_left)).setBackgroundColor(getResources().getColor(R.color.gooddream_white));
                }
                ((ImageView) viewHolder.getView(R.id.iv_left)).setImageResource(R.drawable.selector_lv_item);
                return null;
            }
        };
        lv.setAdapter(adapter1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentPositionVehicleType == i) {
                    popwindow.dismiss();
                    return;
                }
                listVehicleType.get(currentPositionVehicleType).flag = "false";
                listVehicleType.get(i).flag = "true";
                currentPositionVehicleType = i;
                adapter1.notifyDataSetChanged();
                popwindow.dismiss();


                condition3 = listVehicleType.get(i).msg;
                condition4 = listVehicleType.get(i).address;
//                Fragment fragment = null;
//                changeFragment(fragment, 1, condition1 + condition2 + condition3);
                constructJSONObject(condition1, condition2, condition3, condition4);
                refreshData(jsonObjectData, jsonObjectPage);
            }
        });
        popwindow.showAsDropDown(v);
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.banner + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
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
                            fragment_banner.setCallBack(Fragment_choose_coach_pull2refresh_expanable.this);
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
                Configs.banner);
    }

    private void initADscrollBar() {
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
        Y.y("initADscrollBar:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.ad_scrollBar + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("initADscrollBar:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray jsonArray = VolleyUtilsTemp
                            .optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "list");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        listADscrollBar = new ArrayList<ListItem>();
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            ListItem item = new ListItem();
                            item.string1 = jsonArray.optString(i, "");
                            listADscrollBar.add(item);
                        }
                    }
                    String student_total = VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "student_total");
                    if (null != student_total && !"".equals(student_total)) {
                        tv_count.setText("已帮助" + student_total + "人拿证");
                    }
                    adScrollBarSize = listADscrollBar.size();
                    if (!hasStart) {
                        initGesture();
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
                Configs.banner);
    }

    boolean hasStart = false;
    int timeTick = 0;
    int timeTickMaxBanner = 5;
    int timeTickMaxADscrollBar = 3;

    private void initGesture() {
        ThreadPoolUtil.getInstanceLimitedTaskExecutor(1).submit(new Runnable() {
            @Override
            public void run() {
                while (PreferencesHelper.getBoolean("homepage")) {
                    try {
                        hasStart = true;
                        Thread.sleep(1000);
                        timeTick++;
//                        Y.y("initGesture:" + timeTick);
                        if (0 == (timeTick % timeTickMaxADscrollBar)) {
                            Message message = mHandler.obtainMessage();
                            message.what = 51;
                            message.arg1 = (positionADscrollbar + 1) % adScrollBarSize;
                            mHandler.sendMessage(message);
                        }
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
            case R.id.linear_1:
                if (listCity2.size() == 0) {
                    stringRequestCity(0);
                    return;
                }
                showPop(getContext(), viewSplit);

                break;
            case R.id.linear_1_hover:
                if (listCity2.size() == 0) {
                    stringRequestCity(1);
                    return;
                }
                showPop(getContext(), viewSplit_hover);
                break;
            case R.id.linear_2:
                showPop2(getContext(), viewSplit);
                break;
            case R.id.linear_2_hover:
                showPop2(getContext(), viewSplit_hover);
                break;
            case R.id.linear_3:
                if (listVehicleType.size() == 0) {
                    stringRequestVehicleType(0);
                    return;
                }
                showPop3(getContext(), viewSplit);
                break;
            case R.id.linear_3_hover:
                if (listVehicleType.size() == 0) {
                    stringRequestVehicleType(1);
                    return;
                }
                showPop3(getContext(), viewSplit_hover);
                break;
        }
    }

    private void parseTemp2(ListItem<ListItem> listItem, JSONObject obj, JSONObject obj0, JSONObject obj1) {
        if (obj0 != null) {
            listItem.group1IconUrl = obj0.optString("photoPath", "");
            listItem.group1Star = Float.valueOf(("".equals(obj0.optString("starLevel", ""))) ? "0" : obj0.optString("starLevel", ""));
            listItem.group1Nick = obj0.optString("nickName", "");
            listItem.group1Bao = obj0.optString("safeLabel", "");
            listItem.group1Progress = Integer.valueOf(("".equals(obj0.optString("passRate", ""))) ? "0" : obj0.optString("passRate", ""));
            listItem.group1Id = obj0.optString("id", "");

            listItem.group1Address = obj.optString("areaName", "");
            listItem.group1Distance = obj.optString("distance", "");
            listItem.group1Latitude = Double.valueOf(("".equals(obj.optString("latitude", ""))) ? "0" : obj.optString("latitude", ""));
            listItem.group1Longitude = Double.valueOf(("".equals(obj.optString("longitude", ""))) ? "0" : obj.optString("longitude", ""));
            listItem.group1Count = Integer.valueOf(("".equals(obj.optString("teacherCount", ""))) ? "0" : obj.optString("teacherCount", "")) + "";

            JSONArray arrayProduct = obj0.optJSONArray("products");
            if (arrayProduct != null && arrayProduct.length() > 0) {
                int len = arrayProduct.length();
                for (int i = 0; i < len; i++) {
                    JSONObject jsonObject1 = arrayProduct.optJSONObject(i);
                    if (jsonObject1 != null) {
                        ListItem itemTemp = new ListItem();
                        itemTemp.group1C1 = jsonObject1.optString("projectType", "");
                        itemTemp.group1ProductName1 = jsonObject1.optString("productName", "");
                        itemTemp.group1ProductPrice1 = jsonObject1.optString("productPrice", "");
                        listItem.arrayList1.add(itemTemp);
                    }
                }
            }
        }
        if (obj1 != null) {
            listItem.group2IconUrl = obj1.optString("photoPath", "");
            listItem.group2Star = Float.valueOf(("".equals(obj1.optString("starLevel", ""))) ? "0" : obj1.optString("starLevel", ""));
            listItem.group2Nick = obj1.optString("nickName", "");
            listItem.group2Bao = obj1.optString("safeLabel", "");
            listItem.group2Progress = Integer.valueOf(("".equals(obj1.optString("passRate", ""))) ? "0" : obj1.optString("passRate", ""));
            listItem.group2Id = obj1.optString("id", "");

            listItem.group2Address = obj.optString("areaName", "");
            listItem.group2Distance = obj.optString("distance", "");
            listItem.group2Latitude = Double.valueOf(("".equals(obj.optString("latitude", ""))) ? "0" : obj.optString("latitude", ""));
            listItem.group2Longitude = Double.valueOf(("".equals(obj.optString("longitude", ""))) ? "0" : obj.optString("longitude", ""));
            listItem.group2Count = Integer.valueOf(("".equals(obj.optString("teacherCount", ""))) ? "0" : obj.optString("teacherCount", "")) + "";

            JSONArray arrayProduct = obj1.optJSONArray("products");
            if (arrayProduct != null && arrayProduct.length() > 0) {
                int len = arrayProduct.length();
                for (int i = 0; i < len; i++) {
                    JSONObject jsonObject1 = arrayProduct.optJSONObject(i);
                    if (jsonObject1 != null) {
                        ListItem itemTemp = new ListItem();
                        itemTemp.group1C1 = jsonObject1.optString("projectType", "");
                        itemTemp.group1ProductName1 = jsonObject1.optString("productName", "");
                        itemTemp.group1ProductPrice1 = jsonObject1.optString("productPrice", "");
                        listItem.arrayList2.add(itemTemp);
                    }
                }
            }
        }
    }

    private ListItem parseTemp(JSONObject obj, JSONObject obj0) {
        ListItem<ListItem> listItem = new ListItem<>();
        if (obj0 != null) {
            listItem.group1Type = obj0.optString("", "");
            listItem.group1IconUrl = obj0.optString("photoPath", "");
            listItem.group1Star = Float.valueOf(("".equals(obj0.optString("starLevel", ""))) ? "0" : obj0.optString("starLevel", ""));
            listItem.group1Nick = obj0.optString("nickName", "");
            listItem.group1Bao = obj0.optString("safeLabel", "");
            listItem.group1Progress = Integer.valueOf(("".equals(obj0.optString("passRate", ""))) ? "0" : obj0.optString("passRate", ""));
            listItem.group1Id = obj0.optString("id", "");

            listItem.group1Address = obj.optString("areaName", "");
            listItem.group1Distance = obj.optString("distance", "");
            listItem.group1Latitude = Double.valueOf(("".equals(obj.optString("latitude", ""))) ? "0" : obj.optString("latitude", ""));
            listItem.group1Longitude = Double.valueOf(("".equals(obj.optString("longitude", ""))) ? "0" : obj.optString("longitude", ""));
            listItem.group1Count = Integer.valueOf(("".equals(obj.optString("teacherCount", ""))) ? "0" : obj.optString("teacherCount", "")) + "";

            JSONArray arrayProduct = obj0.optJSONArray("products");
            if (arrayProduct != null && arrayProduct.length() > 0) {
                int len = arrayProduct.length();
                for (int i = 0; i < len; i++) {
                    JSONObject jsonObject1 = arrayProduct.optJSONObject(0);
                    if (jsonObject1 != null) {
                        ListItem itemTemp = new ListItem();
                        itemTemp.group1C1 = jsonObject1.optString("projectType", "");
                        itemTemp.group1ProductName1 = jsonObject1.optString("productName", "");
                        itemTemp.group1ProductPrice1 = jsonObject1.optString("productPrice", "");
                        listItem.arrayList1.add(itemTemp);
                    }
                }
            }
        }
        return listItem;
    }

    @Override
    public ListItem<ListItem> parseJson1(JSONObject obj) {
        // TODO Auto-generated method stub
        ListItem<ListItem> listItem = new ListItem<ListItem>();
        JSONArray jsonArray = obj.optJSONArray("teachers");
        if (null != jsonArray && jsonArray.length() > 0) {
            int len2 = jsonArray.length();
            if (len2 > 2) {
                listItem.hasLeastTwo = true;
                listItem.showMore = true;
                JSONObject obj0 = jsonArray.optJSONObject(0);
                JSONObject obj1 = jsonArray.optJSONObject(1);
                parseTemp2(listItem, obj, obj0, obj1);
                for (int k = 2; k < len2; k++) {
                    JSONObject obj2 = jsonArray.optJSONObject(k);
                    ListItem ob = parseTemp(obj, obj2);
//                    Object ob = parseJson2(obj2, listItem);
                    listItem.mdatas.add(ob);
                    ob = null;
                }
            } else if (len2 == 2) {
                listItem.hasLeastTwo = true;
                listItem.showMore = false;
                JSONObject obj0 = jsonArray.optJSONObject(0);
                JSONObject obj1 = jsonArray.optJSONObject(1);
                parseTemp2(listItem, obj, obj0, obj1);
            } else if (len2 == 1) {
                listItem.hasLeastTwo = false;
                listItem.showMore = false;
                JSONObject obj0 = jsonArray.optJSONObject(0);
                parseTemp2(listItem, obj, obj0, null);
            }
        }
        return listItem;
    }

    @Override
    public boolean parseJson1Intercept() {
        return true;
    }


    @Override
    public void initAdapter(Context context, int mItemLayoutIdGroup,
                            int mItemLayoutIdChild) {
        // TODO Auto-generated method stub
        context = getActivity();
        mItemLayoutIdGroup = R.layout.listitem_expanable_group;
        mItemLayoutIdChild = R.layout.listitem_expanable_child;
        super.initAdapter(context, mItemLayoutIdGroup, mItemLayoutIdChild);
    }

    @Override
    public View getChildListItemview(final ViewHolder viewHolder, View view,
                                     final ListItem item, final int position, final int groupPosition) {
        // TODO Auto-generated method stub
        //上半布局
        dealViewUp(viewHolder, item);
        //底部隐藏
        dealViewBottom(viewHolder, item, position);
        int lenth = 0;
        if (null != mList && null != mList.get(groupPosition) && null != mList.get(groupPosition).mdatas) {
            lenth = mList.get(groupPosition).mdatas.size();
        }
        Y.y("child:" + lenth + "    groupPosition:" + groupPosition);
        if (position >= 0 && position == lenth - 1) {
            viewHolder.getView(R.id.linear_more_hide).setVisibility(View.VISIBLE);
            viewHolder.getView(R.id.view_hide).setVisibility(View.VISIBLE);
        } else {
            viewHolder.getView(R.id.linear_more_hide).setVisibility(View.GONE);
            viewHolder.getView(R.id.view_hide).setVisibility(View.GONE);
        }
        viewHolder.getView(R.id.linear_more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lv_center_expanable.isGroupExpanded(groupPosition)) {
                    lv_center_expanable.collapseGroup(groupPosition);
                    adapter.notifyDataSetChanged();
                    lv_center_expanable
                            .setSelectedGroup(groupPosition);
                } else {
                    lv_center_expanable
                            .expandGroup(groupPosition);
                }


            }
        });
        viewHolder.getView(R.id.relative_first).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),
                        CoachDetailActivity.class);
                intent.putExtra("id", item.group1Id);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
            }
        });
        viewHolder.getView(R.id.tv_ensure).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferencesHelper.getBoolean(ConstantsME.LOGINED) && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
//                    Intent intentWebview = new Intent(mContext,
//                            ApplyStateActivity.class);
//                    startActivity(intentWebview);
//                    getActivity().overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);
                    Intent intent = new Intent(getContext(),
                            CoachDetailActivity.class);
                    intent.putExtra("id", item.group1Id);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    Intent intentLogin = new Intent(mContext,
                            LoginDialogActivity.class);
                    intentLogin.putExtra(ConstantsME.fromDialog, true);
                    intentLogin.putExtra("id", item.group1Id);
                    startActivityForResult(intentLogin, 10086);
//                    getActivity().overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);
                }
            }
        });

        return null;
    }

    @Override
    public View getGroupListItemview(final ViewHolder viewHolder, View view,
                                     final ListItem<ListItem> item, final int position) {
        // TODO Auto-generated method stub
        if (position == 0 && item.isEmpty) {
            View emptyView = LayoutInflater.from(mContext).inflate(R.layout.inflater_empty, null);
            RelativeLayout relative_empty = (RelativeLayout) emptyView.findViewById(R.id.relative_empty);
            relative_empty.setMinimumHeight(Utils.getScreenHeight(mContext) - Utils.getStatusBarHeightPx((Activity) mContext) - Utils.dip2px(mContext, 100) - viewHeader1.getHeight() - viewHeader2.getHeight());
            relative_empty.setGravity(Gravity.CENTER);
            relative_empty.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!hasStart) {
                        initPopData();
                        initBanner();
                        initADscrollBar();
                    }
                    stringRequestMethod();
                }
            });
            return emptyView;
        }
        //上半布局
        dealViewUp(viewHolder, item);
        //下半布局开始
        dealViewMiddle(viewHolder, item);
        //底部隐藏
        dealViewBottom(viewHolder, item, position, 1);
        viewHolder.getView(R.id.linear_more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                lv_center_expanable.collapseGroup(0);
//                lv_center_expanable.collapseGroup(0);
                if (lv_center_expanable.isGroupExpanded(position)) {
                    Y.y("准备收起");
                    boolean b = lv_center_expanable.collapseGroup(position);
                    Y.y("准备收起:" + b);
                } else {
                    Y.y("准备展开");
                    boolean a = lv_center_expanable
                            .expandGroup(position);
                    Y.y("准备展开:" + a);
//                    viewHolder.getView(R.id.linear_more).setVisibility(View.INVISIBLE);
                }
            }
        });
        viewHolder.getView(R.id.relative_first).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),
                        CoachDetailActivity.class);
                intent.putExtra("id", item.group1Id);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
            }
        });
        viewHolder.getView(R.id.relative_second).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),
                        CoachDetailActivity.class);
                intent.putExtra("id", item.group2Id);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        viewHolder.getView(R.id.tv_ensure).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferencesHelper.getBoolean(ConstantsME.LOGINED) && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
//                    Intent intentWebview = new Intent(mContext,
//                            ApplyStateActivity.class);
//                    startActivity(intentWebview);
//                    getActivity().overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);
                    Intent intent = new Intent(getContext(),
                            CoachDetailActivity.class);
                    intent.putExtra("id", item.group1Id);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    Intent intentLogin = new Intent(mContext,
                            LoginDialogActivity.class);
                    intentLogin.putExtra(ConstantsME.fromDialog, true);
                    intentLogin.putExtra("id", item.group1Id);
                    startActivityForResult(intentLogin, 10086);
//                    getActivity().overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);
                }
            }
        });
        viewHolder.getView(R.id.tv_ensure_second).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferencesHelper.getBoolean(ConstantsME.LOGINED) && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
                    //                    Intent intentWebview = new Intent(mContext,
//                            ApplyStateActivity.class);
//                    startActivity(intentWebview);
//                    getActivity().overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);
                    Intent intent = new Intent(getContext(),
                            CoachDetailActivity.class);
                    intent.putExtra("id", item.group2Id);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in,
                            R.anim.slide_left_out);
                } else {
                    Intent intentLogin = new Intent(mContext,
                            LoginDialogActivity.class);
                    intentLogin.putExtra(ConstantsME.fromDialog, true);
                    intentLogin.putExtra("id", item.group2Id);
                    startActivityForResult(intentLogin, 10086);
//                    getActivity().overridePendingTransition(R.anim.slide_right_in,
//                            R.anim.slide_left_out);
                }
            }
        });
        return null;
    }

    private void dealViewUp(ViewHolder viewHolder, ListItem item) {
        RatingBar ratingbar = viewHolder.getView(R.id.ratingbar);
        ProgressBar progressBar = viewHolder.getView(R.id.progressBar);
        try {
            ListItem itemT = (ListItem) item.arrayList1.get(0);
            viewHolder.setText(R.id.tv_c1,
                    "".equals(itemT.group1C1) ? "C1" : itemT.group1C1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ratingbar.setRating(item.group1Star);
        try {
            imageLoader2.displayImage(item.group1IconUrl.split(";")[0],
                    (CustomRoundImageView_new) viewHolder.getView(R.id.iv_icon), ImageLoaderUtils_nostra13.getFadeOptions(
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon));
        } catch (Exception e) {

        }
        viewHolder.setText(R.id.tv_title,
                "".equals(item.group1Nick) ? "nick" : item.group1Nick);
        LinearLayout linearLayout = viewHolder.getView(R.id.linear_add_bao_pei);
        String[] split = item.group1Bao.split(",");
        if (split != null && !"".equals(item.group1Bao) && split.length > 0 && linearLayout.getChildCount() <= 1) {
            int lenth = split.length;
            int[] color = new int[]{R.color.gooddream_blue, R.color.gooddream_orange, R.color.gooddream_black};
            int[] backgroud = new int[]{R.drawable.shape_white_3px_stroke_1dp_blue, R.drawable.shape_white_3px_stroke_1dp_orange, R.drawable.shape_white_3px_stroke_1dp_blue};
            int _1dp = Utils.dip2px(mContext, 1);
            int _2dp = Utils.dip2px(mContext, 2);
            int _4dp = Utils.dip2px(mContext, 4);
            int _5dp = Utils.dip2px(mContext, 5);
            int _15dp = Utils.dip2px(mContext, 15);
            for (int i = 0; i < lenth; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(_15dp, _15dp);
                params.bottomMargin = _5dp;
                params.leftMargin = _4dp;
                TextView textView = new TextView(mContext);
                textView.setText(split[i]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                textView.setTextColor(getResources().getColor(color[i]));
                textView.setBackgroundResource(backgroud[i]);
                textView.setPadding(_2dp, _1dp, _2dp, _1dp);
                textView.setWidth(_15dp);
                textView.setHeight(_15dp);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(params);
                linearLayout.addView(textView);
            }
        }
        LinearLayout linear_add_normal_vip = viewHolder.getView(R.id.linear_add_normal_vip);
        List arrayList1 = item.arrayList1;
        if (arrayList1 != null && arrayList1.size() > 0 && linear_add_normal_vip.getChildCount() <= 0) {
            int size = arrayList1.size();
            int _10dp = Utils.dip2px(mContext, 10);
            int _20dp = Utils.dip2px(mContext, 20);
            for (int i = 0; i < size; i++) {
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.inflater_linear_addview, null);
                TextView tv_title = (TextView) inflate.findViewById(R.id.tv_title);
                TextView tv_content = (TextView) inflate.findViewById(R.id.tv_content);
                Y.y("引入布局:" + (tv_title == null));
                ListItem itemT = (ListItem) arrayList1.get(i);
                tv_title.setText(itemT.group1ProductPrice1);
                tv_content.setText(itemT.group1ProductName1);
                if (i == 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = _10dp;
                    inflate.setLayoutParams(params);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = _20dp;
                    inflate.setLayoutParams(params);
                }
                linear_add_normal_vip.addView(inflate);
            }
        }
        viewHolder.setText(R.id.tv_progress, item.group1Progress + "%");
        progressBar.setProgress(item.group1Progress);
        viewHolder.setText(R.id.tv_distance,
                "".equals(item.group1Distance) ? "距离未知" : item.group1Distance);
        viewHolder.setText(R.id.tv_location,
                "".equals(item.group1Address) ? "?" : item.group1Address);
        viewHolder.setText(R.id.tv_count,
                "".equals(item.group1Count) ? "共有?个教练" : "共有" + item.group1Count + "个教练");
    }

    private void dealViewMiddle(ViewHolder viewHolder, ListItem item) {
        RatingBar ratingbar_second = viewHolder.getView(R.id.ratingbar_second);
        ProgressBar progressBar_second = viewHolder.getView(R.id.progressBar_second);
        try {
            ListItem itemT = (ListItem) item.arrayList2.get(0);
            viewHolder.setText(R.id.tv_c1_second,
                    "".equals(itemT.group1C1) ? "C1" : itemT.group1C1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ratingbar_second.setRating(item.group2Star);
        try {
            imageLoader2.displayImage(item.group2IconUrl.split(";")[0],
                    (CustomRoundImageView_new) viewHolder.getView(R.id.iv_icon_second), ImageLoaderUtils_nostra13.getFadeOptions(
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon));
        } catch (Exception e) {

        }
        LinearLayout linear_add_bao_pei_second = viewHolder.getView(R.id.linear_add_bao_pei_second);
        String[] split_second = item.group2Bao.split(",");
        if (split_second != null && !"".equals(item.group2Bao) && split_second.length > 0 && linear_add_bao_pei_second.getChildCount() <= 1) {
            int lenth = split_second.length;
            int[] color = new int[]{R.color.gooddream_blue, R.color.gooddream_orange, R.color.gooddream_black};
            int[] backgroud = new int[]{R.drawable.shape_white_3px_stroke_1dp_blue, R.drawable.shape_white_3px_stroke_1dp_orange, R.drawable.shape_white_3px_stroke_1dp_blue};
            int _1dp = Utils.dip2px(mContext, 1);
            int _2dp = Utils.dip2px(mContext, 2);
            int _4dp = Utils.dip2px(mContext, 4);
            int _5dp = Utils.dip2px(mContext, 5);
            int _15dp = Utils.dip2px(mContext, 15);
            for (int i = 0; i < lenth; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(_15dp, _15dp);
                params.bottomMargin = _5dp;
                params.leftMargin = _4dp;
                TextView textView = new TextView(mContext);
                textView.setText(split_second[i]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                textView.setTextColor(getResources().getColor(color[i]));
                textView.setBackgroundResource(backgroud[i]);
                textView.setPadding(_2dp, _1dp, _2dp, _1dp);
                textView.setWidth(_15dp);
                textView.setHeight(_15dp);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(params);
                linear_add_bao_pei_second.addView(textView);
            }
        }
        LinearLayout linear_add_normal_vip_second = viewHolder.getView(R.id.linear_add_normal_vip_second);
        List arrayList_second = item.arrayList2;
        if (arrayList_second != null && arrayList_second.size() > 0 && linear_add_normal_vip_second.getChildCount() <= 0) {
            int size = arrayList_second.size();
            int _10dp = Utils.dip2px(mContext, 10);
            int _20dp = Utils.dip2px(mContext, 20);
            for (int i = 0; i < size; i++) {
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.inflater_linear_addview, null);
                TextView tv_title = (TextView) inflate.findViewById(R.id.tv_title);
                TextView tv_content = (TextView) inflate.findViewById(R.id.tv_content);
                Y.y("引入布局:" + (tv_title == null));
                ListItem itemT = (ListItem) arrayList_second.get(i);
                tv_title.setText(itemT.group1ProductPrice1);
                tv_content.setText(itemT.group1ProductName1);
                if (i == 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = _10dp;
                    inflate.setLayoutParams(params);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = _20dp;
                    inflate.setLayoutParams(params);
                }
                linear_add_normal_vip_second.addView(inflate);
            }
        }
        viewHolder.setText(R.id.tv_title_second,
                "".equals(item.group2Nick) ? "nick" : item.group2Nick);
        viewHolder.setText(R.id.tv_progress_second, item.group2Progress + "%");
        progressBar_second.setProgress(item.group2Progress);
        viewHolder.setText(R.id.tv_distance_second,
                "".equals(item.group2Distance) ? "距离未知" : item.group2Distance);
    }

    private void dealViewBottom(ViewHolder viewHolder, ListItem item, int position) {
        dealViewBottom(viewHolder, item, position, 0);
    }

    private void dealViewBottom(ViewHolder viewHolder, ListItem item, int position, int flag) {
        //判断是否有2个及以上
        try {
            if (item.hasLeastTwo) {
                viewHolder.getView(R.id.relative_second).setVisibility(View.VISIBLE);
            } else {
                viewHolder.getView(R.id.relative_second).setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
        try {
            if (lv_center_expanable.isGroupExpanded(position)) {
                viewHolder.getView(R.id.linear_more_hide).setVisibility(View.GONE);
                viewHolder.getView(R.id.view_hide).setVisibility(View.GONE);
            } else {
                viewHolder.getView(R.id.linear_more_hide).setVisibility(View.VISIBLE);
                viewHolder.getView(R.id.linear_more).setVisibility(View.VISIBLE);
                viewHolder.getView(R.id.view_hide).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
        try {
            if (flag == 1) {
                if (item.showMore) {
                    viewHolder.getView(R.id.linear_more).setVisibility(View.VISIBLE);
                } else {
                    viewHolder.getView(R.id.linear_more).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        super.onRefresh(pullToRefreshLayout);
        if (!hasStart) {
            initPopData();
            initBanner();
            initADscrollBar();
        }
    }

    boolean firstOnresume = true;

    @Override
    public void onResume() {
        super.onResume();
        PreferencesHelper.putBoolean("homepage", true);
        if (firstOnresume && !"".equals(PreferencesHelper.getString(ConstantsME.USERID))) {
            stringRequestHovor();
        } else {
            if (refreshSuccess()) {
                addHoverBottom(relative_hover_bottom);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferencesHelper.putBoolean("homepage", false);
    }

    private void constructJSONObject(String condition1_, String condition2_, String condition3_, String condition4_) {
        try {
            firstRequestHideHover();
            if (TextUtils.isEmpty(condition3_)) {
                if (listVehicleType != null) {
                    condition3_ = listVehicleType.get(0).msg;
                }
            }
            if (TextUtils.isEmpty(condition4_)) {
                if (listVehicleType != null) {
                    condition4_ = listVehicleType.get(0).address;
                }
            }
            jsonObjectData.put("latitude",
                    PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL));
            jsonObjectData.put("longitude",
                    PreferencesHelper.getString(ConstantsME.LONGTITUDE_ORIGINAL));
            JSONObject jsonObjectAreaName = new JSONObject();
            jsonObjectAreaName.put("areaName", condition1_);//"沙坪坝"
            jsonObjectAreaName.put("productName", condition3_);//"C1"
            jsonObjectAreaName.put("projectType", condition4_);//"C1"
            JSONObject jsonObjectOrder = new JSONObject();
            if ("starLevel".equals(condition2_)) {
                jsonObjectOrder.put("starLevel", "desc");//星级
            } else {
                jsonObjectOrder.put("passRate", "desc");//通过率
            }
            jsonObjectData.put("searchFilter", jsonObjectAreaName);
            jsonObjectData.put("order", jsonObjectOrder);

            jsonObjectPage.put("pageIndex", 0);
            jsonObjectPage.put("pageSize", 20);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //浮层
    private void stringRequestHovor() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ConstantsME.deviceId, PreferencesHelper.getString(ConstantsME.deviceId));
            jsonObject.put(ConstantsME.version, Utils.getVersion(mContext));
            JSONObject jsonObjectData = new JSONObject();

            jsonObjectData.put("uuid", PreferencesHelper.getString(ConstantsME.USERID));

            jsonObject.put("data", jsonObjectData);
            jsonObject.put("page", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject params = VolleyUtilsTemp.returnParamsJSONobject(jsonObject, true);
        Y.y("stringRequestHovor:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.myCoachHovor + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("stringRequestHovor:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    firstOnresume = false;
                    JSONObject jsonObject1 = VolleyUtilsTemp.optJSONObject(VolleyUtilsTemp.dataBodyJSONObject, "product");
                    if (jsonObject1 != null) {
                        PreferencesHelper.putString(ConstantsME.teacherName, VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teacherNickName"));
                        PreferencesHelper.putString(ConstantsME.teacherType, jsonObject1.optString("projectType"));
                        PreferencesHelper.putString(ConstantsME.teacherProductName, jsonObject1.optString("productName"));
                        PreferencesHelper.putString(ConstantsME.teacherFee, jsonObject1.optString("productPrice"));
                        PreferencesHelper.putString(ConstantsME.teacherUrl, VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "photoPath"));
                        PreferencesHelper.putString(ConstantsME.teacherProductId, jsonObject1.optString("productId"));
                        PreferencesHelper.putBoolean(ConstantsME.showBottomBar, true);
                        PreferencesHelper.putString(ConstantsME.teacherId, VolleyUtilsTemp.optString(VolleyUtilsTemp.dataBodyJSONObject, "teacherId"));
                    } else {
                        PreferencesHelper.putBoolean(ConstantsME.showBottomBar, false);
                    }
                    if (refreshSuccess()) {
                        addHoverBottom(relative_hover_bottom);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Y.y("stringRequestHovor:" + error);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.myCoachHovor);
    }

    private void stringRequestCity(final int flag) {
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
        Y.y("stringRequestCity:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.cityList + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("stringRequestCity:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray jsonArray = VolleyUtilsTemp
                            .optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "list");
                    if (null != jsonArray && jsonArray.length() > 1) {
                        PreferencesHelper.putString(ConstantsME.cityList, jsonArray.toString());
                        initPopData();
                        if (flag == 0) {
                            showPop(getContext(), viewSplit);
                        } else {
                            showPop(getContext(), viewSplit_hover);
                        }
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Y.y("stringRequestCity:" + error);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.cityList);
    }

    private void stringRequestVehicleType(final int flag) {
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
        Y.y("stringRequestVehicleType:" + params.toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Configs.BASE_SERVER_URL + Configs.vehicleTypeList + "?jsonRequest=" + params.toString(), null, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Y.y("stringRequestVehicleType:" + response);
                VolleyUtilsTemp.parseJson(response);
                if (Configs.SUCCESS.equals(VolleyUtilsTemp.flag)) {
                    JSONArray jsonArray = VolleyUtilsTemp
                            .optJSONArray(VolleyUtilsTemp.dataBodyJSONObject, "list");
                    if (null != jsonArray && jsonArray.length() > 1) {
                        PreferencesHelper.putString(ConstantsME.vehicleTypeList, jsonArray.toString());
                        initPopData();
                        if (flag == 0) {
                            showPop3(getContext(), viewSplit);
                        } else {
                            showPop3(getContext(), viewSplit_hover);
                        }
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Y.y("stringRequestCity:" + error);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        ShareApplication.getInstance().addToRequestQueue(stringRequest,
                Configs.vehicleTypeList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Y.y("下拉onActivityResult：" + requestCode + "   " + resultCode + "     " + data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 10086) {
                Utils.showToast("登录成功");
                Intent intent = new Intent(getContext(),
                        CoachDetailActivity.class);
                intent.putExtra("id", data.getStringExtra("id"));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_in,
                        R.anim.slide_left_out);
            }
        }
    }
}
