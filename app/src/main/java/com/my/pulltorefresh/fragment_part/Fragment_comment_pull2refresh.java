package com.my.pulltorefresh.fragment_part;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.gooddream.learningdriving.R;
import com.my.configs.Configs;
import com.my.customviews.CustomFlowLayout;
import com.my.customviews.CustomRoundImageView_new;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.pulltorefresh.fragment.Pull2RefreshListView2VolleyFragment_hover;
import com.my.pulltorefresh.pullableview.PullableListView2;
import com.my.utils.ImageLoaderUtils_nostra13;
import com.my.utils.Utils;
import com.my.utils.Y;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_comment_pull2refresh extends Pull2RefreshListView2VolleyFragment_hover<ListItem>{

    com.nostra13.universalimageloader.core.ImageLoader imageLoader2;
    private Context mContext;
    CustomFlowLayout flowLayout;
    String teacherId;
    String label;

    public Fragment_comment_pull2refresh(int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
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

    public Fragment_comment_pull2refresh() {
        super(Request.Method.GET, Configs.commentList, jsonObjectData, jsonObjectPage);
    }

    public static synchronized Fragment_comment_pull2refresh newInstance(
            int post2get, String inter, JSONObject jsonObjectData, JSONObject jsonObjectPage) {
        Fragment_comment_pull2refresh newInstance = new Fragment_comment_pull2refresh(
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
            teacherId = bundle.getString("teacherId", "");
            label = bundle.getString("label", "");
        }
        mContext = getActivity();
        imageLoader2 = ImageLoaderUtils_nostra13.initImageLoader(getActivity());
    }

    View viewHeader1;

    @Override
    public void onScroll_(AbsListView paramAbsListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Y.y("onScroll_1:" + firstVisibleItem);
        Y.y("onScroll_2:" + visibleItemCount);
        Y.y("onScroll_3:" + totalItemCount);
    }

    @Override
    public void initData() {
        viewHeader1 = LayoutInflater.from(mContext).inflate(R.layout.lv_hearderview_comment, null);
        lv_center.addHeaderView(viewHeader1);


        pullToRefreshLayout.setPullDownContentText("下拉刷新数据");
        //pullToRefreshLayout.setPullUpContentText("宁静以致远");
        pullToRefreshLayout.setPullDownStateText("刷新成功");
        int color = getResources().getColor(R.color.default_text_blue);
        pullToRefreshLayout.setHeadViewBackgroundColor(color);
        // pull.setHeadViewBackgroundResource(R.drawable.allview_refreshing);
        pullToRefreshLayout.setPullDownContentTextSize(14);
        pullToRefreshLayout.setPullDownStateTextSize(12);

        initViewM();
    }

    private boolean hadAdded2 = false;

    private void initViewM() {
        flowLayout = (CustomFlowLayout) viewHeader1.findViewById(R.id.flowLayout);
        if (!TextUtils.isEmpty(label) && flowLayout.getChildCount() <= 0) {
            String[] labelList = label.split(",");
            int length = labelList.length;
            View[] views = new View[length];
            int _8dp = Utils.dip2px(mContext, 8);
            int _5dp = Utils.dip2px(mContext, 5);
            if (length > 0 && !hadAdded2) {
                for (int j = 0; j < length; j++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.rightMargin = _8dp;
                    params.topMargin = _5dp;
                    params.bottomMargin = _5dp;
                    TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.inflater_flowlabel, null);
                    textView.setText(labelList[j]);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    textView.setLayoutParams(params);
//                    flowLayout.addView(textView);
                    views[j] = textView;
                }
                flowLayout.addData(views);
                hadAdded2 = true;
            }
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
        return json.optJSONArray("comments");
    }

    @Override
    public Object parseJson(JSONObject obj) {
        // TODO Auto-generated method stub
        ListItem item = new ListItem();
        item.group1IconUrl = obj.optString("photo_path", "");
        item.group1Nick = obj.optString("student_name", "");
        item.group1Star = Float.parseFloat(obj.optString("star", "0"));
        item.group1ProductName1 = obj.optString("content", "");
        item.title = obj.optString("cmt_time", "");
        return item;
    }

    @Override
    public void initAdapter(Context context, int itemLayoutId) {
        // TODO Auto-generated method stub
        context = getActivity();
        itemLayoutId = R.layout.listitem_comment;
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
            emptyView.setMinimumHeight(Utils.getScreenHeight(mContext) - Utils.getStatusBarHeightPx(getActivity()) - Utils.dip2px(mContext, 50) - viewHeader1.getHeight());
            relative_empty.setGravity(Gravity.CENTER);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stringRequestMethod();
                }
            });
            return emptyView;
        }

        RatingBar ratingbar = viewHolder.getView(R.id.ratingbar);
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
                "".equals(item.group1Nick) ? "****" : item.group1Nick);
        viewHolder.setText(R.id.tv_date,
                "".equals(item.title) ? "评论日期未知" : item.title);
        viewHolder.setText(R.id.tv_content,
                "".equals(item.group1ProductName1) ? "****" : item.group1ProductName1);

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
