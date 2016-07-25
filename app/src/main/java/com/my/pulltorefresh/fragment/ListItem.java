package com.my.pulltorefresh.fragment;

import java.util.ArrayList;
import java.util.List;

public class ListItem<T> extends Entity {
    public String pi = "";
    public String flag = "";
    public String msg = "";
    public List mdatas = new ArrayList<T>();
    public List mdatasPackage = new ArrayList<ListItemPackage>();
    public boolean isDataNULL = false;
    public int temp;
    //
    public String banner = "";
    public String bannerUrl = "";
    public String id = "";
    public String name = "";
    public String title = "";
    public String phone = "";

    public String address = "";
    public double latitude;
    public double longitude;

    public String url = "";
    public String string1 = "";
    public String string2 = "";
    public String string3 = "";

    public String date = "";
    public boolean isEmpty = false;
    public boolean hasLeastTwo = false;
    public boolean showMore = false;

    public String group1Type = "";
    public String group2Type = "";
    public String group1IconUrl = "";
    public String group2IconUrl = "";
    public float group1Star = 0f;
    public float group2Star = 0f;
    public String group1Nick = "";
    public String group2Nick = "";
    public String group1Bao = "";
    public String group2Bao = "";
    public int group1Progress = 0;
    public int group2Progress = 0;
    public double group1Latitude = 0;
    public double group2Latitude = 0;
    public double group1Longitude = 0;
    public double group2Longitude = 0;
    public String group1C1 = "";
    public String group2C1 = "";
    public String group1Address = "";
    public String group2Address = "";
    public String group1Count = "";
    public String group2Count = "";
    public String group1Id = "";
    public String group2Id = "";
    public String group1Distance = "";
    public String group2Distance = "";
    public String group1ProductPrice1 = "";
    public String group2ProductPrice1 = "";
    public String group1ProductPrice2 = "";
    public String group2ProductPrice2 = "";
    public String group1ProductName1 = "";
    public String group2ProductName1 = "";
    public String group1ProductName2 = "";
    public String group2ProductName2 = "";
    public List arrayList1 = new ArrayList<ListItem>();
    public List arrayList2 = new ArrayList<ListItem>();
    public String group1Attrs = "";
}
