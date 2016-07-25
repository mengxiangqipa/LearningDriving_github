package com.map;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.gooddream.learningdriving.R;
import com.gooddream.learningdriving.activity.BaseActivity;
import com.gooddream.learningdriving.application.ShareApplication;
import com.my.configs.ConstantsME;
import com.my.pulltorefresh.fragment.ListItem;
import com.my.utils.PreferencesHelper;
import com.my.utils.Utils;
import com.my.utils.Y;

import android.view.View;


/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class MapActivity extends BaseActivity implements AMap.OnMarkerClickListener {
    private MapView mapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Y.y("MapActivity1");
        setContentView(R.layout.map_activity);
        Y.y("MapActivity2");
        Utils.setTranslucentStatus(this, true);
        Y.y("MapActivity3");
        Utils.setStatusBarTintColor(this,
                getResources().getColor(R.color.color_main_title));
        Y.y("MapActivity4");
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        //  MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        Y.y("MapActivity5");
        mapView = (MapView) findViewById(R.id.map);
        Y.y("MapActivity6");
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        Y.y("MapActivity7");
        initView();
        Y.y("MapActivity8");
        init();
        Y.y("MapActivity9");
        initMarker();
        Y.y("MapActivity10");
        ShareApplication.share.addActivity(this);
        Y.y("MapActivity11");
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("场地位置");
    }

    private void initMarker() {
        Y.y("initMarker 1");
        aMap.setOnMarkerClickListener(this);
        Y.y("initMarker 2");
        double lat = 0, lon = 0;
        if ("".equals(getIntent().getDoubleExtra(ConstantsME.LATITUDE, 0))) {
            lat = 0;
        }
        if ("".equals(getIntent().getDoubleExtra(ConstantsME.LONGITUDE, 0))) {
            lon = 0;
        }
        LatLng latLng = new LatLng(lat, lon);
        Y.y("initMarker 3:" + getIntent().getDoubleExtra(ConstantsME.LATITUDE, 0));
        Y.y("initMarker 3_1:" + getIntent().getDoubleExtra(ConstantsME.LONGITUDE, 0));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                latLng, 18, 0, 30));
        Y.y("initMarker 4:" + (aMap == null));
        aMap.animateCamera(cameraUpdate, 1000, null);
        Y.y("initMarker 5");
        MarkerOptions options = new MarkerOptions();
        Y.y("initMarker 6");
        options.position(latLng);
        Y.y("initMarker 7");
        options.title(getIntent().getStringExtra("title"));
        Y.y("initMarker 8");
        Marker marker = aMap.addMarker(options);
        Y.y("initMarker 9");
        marker.showInfoWindow();

        Y.y("latitude:" + PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL));
        Y.y("longitude:" + PreferencesHelper.getString(ConstantsME.LONGTITUDE_ORIGINAL));
        if (!TextUtils.isEmpty(PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL))) {
            LatLng latLng2 = new LatLng(Double.parseDouble(PreferencesHelper.getString(ConstantsME.LATITUDE_ORIGINAL)), Double.parseDouble(PreferencesHelper.getString(ConstantsME.LONGTITUDE_ORIGINAL)));
            Y.y("11");
            MarkerOptions options2 = new MarkerOptions();
            Y.y("22");
            options2.position(latLng2);
            Y.y("33");
            options2.title("我的位置");
            Y.y("44");
            options2.icon(
                    // BitmapDescriptorFactory
                    // .fromResource(R.drawable.location_marker)
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.mipmap.location_marker)));
            aMap.addMarker(options2);
        }
        Y.y("55");
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
//        CameraPosition cameraPosition = new CameraPosition(latLng, 0, 0, 0);
//        CameraUpdateFactoryDelegate cameraUpdateFactoryDelegate = CameraUpdateFactoryDelegate.newInstance();
////        cameraUpdateFactoryDelegate.cameraPosition =
//                CameraUpdate cameraUpdate = new CameraUpdate(cameraUpdateFactoryDelegate);
        try {
            Y.y("onresume1");
            mapView.onResume();
            Y.y("onresume2");
            LatLng latLng = new LatLng(getIntent().getDoubleExtra(ConstantsME.LATITUDE, 0), getIntent().getDoubleExtra(ConstantsME.LONGITUDE, 0));
            Y.y("onresume3");
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    latLng, 18, 0, 30));
            Y.y("onresume4:" + (aMap == null));
            aMap.animateCamera(cameraUpdate, 1000, null);
            Y.y("onresume5");
        } catch (Exception e) {
            Y.y("onresumeError:" + e.getMessage());
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            Y.y("onPause1");
            mapView.onPause();
            Y.y("onPause2");
        } catch (Exception e) {
            Y.y("onPauseError:" + e.getMessage());
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            Y.y("onSaveInstanceState1");
            mapView.onSaveInstanceState(outState);
            Y.y("onSaveInstanceState2");
        } catch (Exception e) {
            Y.y("onSaveInstanceStateError:" + e.getMessage());
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Y.y("onDestroy1");
            mapView.onDestroy();
            Y.y("onDestroy2");
        } catch (Exception e) {
            Y.y("onDestroyError:" + e.getMessage());
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_back:
                ShareApplication.share.removeActivity(this);
                finish();
                overridePendingTransition(R.anim.slide_left_in,
                        R.anim.slide_right_out);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            marker.showInfoWindow();
        } catch (Exception e) {
        }
        return false;
    }
}
