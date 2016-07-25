package com.gooddream.learningdriving.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gooddream.learningdriving.R;
import com.my.customviews.CustomTextSwitcher;
import com.my.customviews.CustomFlowLayout;
import com.my.utils.Utils;

public class TestActivity extends BaseActivity implements CustomFlowLayout.OnCheckedChangeListener_ {
    private EditText et_content;
    private CheckBox checkbox;
    CustomFlowLayout flowLayout;
    LinearLayout linear_evaluate_label1;
    boolean[] labelCheckedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Utils.setTranslucentStatus(this, true);
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        initView();
        String aaaaa;
        initTest();
    }

    int count = 0;
    CustomTextSwitcher autoTextView;

    private void initTest() {
        autoTextView = (CustomTextSwitcher) findViewById(R.id.autoTextView);
        autoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoTextView.next();
                count++;
                autoTextView.setText("滚动就发觉了伐啦" + count);
            }
        });
    }


    private void initView() {
        // TODO Auto-generated method stub
        flowLayout = (CustomFlowLayout) findViewById(R.id.flowLayout);

        String[] arr = new String[]{"我11的", "进房11间", "进11房间", "进111房间", "假111发", "我的", "进房间", "进房间", "进房间", "假发"};
        View[] views = new View[10];
        int _8dp = Utils.dip2px(TestActivity.this, 8);
        int _5dp = Utils.dip2px(TestActivity.this, 5);
        for (int i = 0; i < 10; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = _8dp;
            params.topMargin = _5dp;
            params.bottomMargin = _5dp;
            params.leftMargin = _8dp;
            CheckBox checkBox = (CheckBox) LayoutInflater.from(this).inflate(R.layout.inflater_flowlabel_checkbox2, null);
            checkBox.setText(arr[i]);
            checkBox.setLayoutParams(params);
            views[i] = checkBox;
        }
        flowLayout.addData(views, true);
    }

    @Override
    public void onCheckedChanged_(int position, boolean b, CompoundButton compoundButton) {
        Utils.showToast("选中位置" + position + "选中状态" + b);
    }
}
