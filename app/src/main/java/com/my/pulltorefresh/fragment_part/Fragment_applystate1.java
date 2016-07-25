package com.my.pulltorefresh.fragment_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gooddream.learningdriving.R;
import com.my.customviews.CustomRoundImageView_new;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.utils.ImageLoaderUtils_nostra13;
import com.my.utils.Utils;

public class Fragment_applystate1 extends Fragment implements View.OnClickListener {
    Context mContext;
    View view;// infalte的布局
    LinearLayout myContainer;// 新建容器
    ////////////////////////////////
    com.nostra13.universalimageloader.core.ImageLoader imageLoader2;
    TextView tv_nick;
    RatingBar ratingbar;
    CustomRoundImageView_new iv_icon;
    TextView tv_type;
    TextView tv_address;
    TextView tv_money;
    TextView tv_content;
    TextView tv_rewrite_type;
    TextView tv_call_vehicle2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        imageLoader2 = ImageLoaderUtils_nostra13.initImageLoader(mContext);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null == view) {
            myContainer = new LinearLayout(mContext);
            view = inflater.inflate(R.layout.fragment_applystate1, null);
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
        Bundle arguments = getArguments();
        ListItem item = (ListItem) arguments.getSerializable("item");
        initView();
        initData(item);
//        initEvent();
        return myContainer;
    }

    private void initData(ListItem item) {
        tv_nick.setText(item.group1Nick);
        ratingbar.setRating(item.group1Star);
        tv_type.setText(item.group1C1+"  "+item.group1ProductName1);
        tv_address.setText("场地："+item.group1Address);
        tv_money.setText(item.group1ProductPrice1);
//        tv_content.setText(item.group1Distance);
        try {
            imageLoader2.displayImage(item.group1IconUrl.split(";")[0],
                    iv_icon, ImageLoaderUtils_nostra13.getFadeOptions(
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon,
                            R.drawable.bg_default_icon));
        } catch (Exception e) {

        }
    }


    private void initView() {
        tv_nick = (TextView) view.findViewById(R.id.tv_nick);
        ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
        iv_icon = (CustomRoundImageView_new) view.findViewById(R.id.iv_icon);
        tv_type = (TextView) view.findViewById(R.id.tv_type);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_money = (TextView) view.findViewById(R.id.tv_money);
        tv_content = (TextView) view.findViewById(R.id.tv_content);

        tv_rewrite_type = (TextView) view.findViewById(R.id.tv_rewrite_type);
        tv_call_vehicle2 = (TextView) view.findViewById(R.id.tv_call_vehicle2);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_rewrite_type:
                break;
            default:
                break;
        }
    }

}
