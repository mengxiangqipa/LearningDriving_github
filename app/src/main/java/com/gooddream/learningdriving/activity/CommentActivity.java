package com.gooddream.learningdriving.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.my.configs.Configs;
import com.my.pulltorefresh.fragment_part.Fragment_comment_pull2refresh;
import com.my.utils.Y;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hm on 2016/7/22.
 */
public class CommentActivity extends BaseFragmentActivity {
    String teacherId;
    String label;

    @Override
    public Fragment initFragment() {
        JSONObject jsonObjectData = new JSONObject();
        JSONObject jsonObjectPage = new JSONObject();
        try {

            jsonObjectData.put("teacherId", getIntent().getStringExtra("teacherId"));

            jsonObjectPage.put("pageIndex", 0);
            jsonObjectPage.put("pageSize", 20);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Fragment_comment_pull2refresh fragment = Fragment_comment_pull2refresh.newInstance(Request.Method.GET, Configs.commentList, jsonObjectData, jsonObjectPage);
        Bundle bundle = new Bundle();
        bundle.putString("teacherId", teacherId);
        bundle.putString("label", label);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initIntent() {
        super.initIntent();
        teacherId = getIntent().getStringExtra("teacherId");
        label = getIntent().getStringExtra("label");
        Y.y("我在comment："+teacherId);
        Y.y("我在comment："+label);
    }
    @Override
    public String initTitle() {
        return "评价详情";
    }
}
