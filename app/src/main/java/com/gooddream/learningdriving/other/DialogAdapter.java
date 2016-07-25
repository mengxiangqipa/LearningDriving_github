package com.gooddream.learningdriving.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gooddream.learningdriving.R;

public class DialogAdapter extends BaseAdapter {
    Context context;
    String[] items;
    LayoutInflater inflate;
    boolean isShowCancel = false;

    public DialogAdapter(Context context, boolean isShowCancel, String[] items) {
        // TODO Auto-generated constructor stub
        inflate = LayoutInflater.from(context);
        this.context = context;
        this.items = items;
        this.isShowCancel = isShowCancel;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return (items == null) ? 0 : (isShowCancel == true ? items.length + 1
                : items.length);
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewgroup) {
        // TODO Auto-generated method stub
        if (view == null) {
            view = inflate.inflate(R.layout.listitem_dialog_item, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_dialog_item);
        View view2 = view.findViewById(R.id.view);
        if (isShowCancel) {
            if (position == items.length) {
                view2.setVisibility(View.INVISIBLE);
                tv.setText("取消");
                // tv.setBackgroundColor(context.getResources().getColor(R.color.white));
                tv.setTextColor(context.getResources().getColor(
                        R.color.color_nearby_merchant));
            } else {
                view2.setVisibility(View.VISIBLE);
                tv.setText(items[position]);
                tv.setTextColor(context.getResources().getColor(
                        R.color.color_main_title));
            }
        } else {
            tv.setText(items[position]);
        }
        return view;
    }

}
