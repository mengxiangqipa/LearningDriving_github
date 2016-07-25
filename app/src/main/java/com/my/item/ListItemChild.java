package com.my.item;

import java.util.ArrayList;
import java.util.List;

public class ListItemChild<T> extends Entity {
    public String pi = "1";
    public String flag = "";
    public String msg = "";
    public List mdatas = new ArrayList<T>();
    public boolean isDataNULL = false;
    //
    public String id = "";
    public String name = "";
    public String title = "";
    public String phone = "";
    public int num = 0;
    public int platformReg;
    public String distance = "0";
    public String address = "";
    public double latitude;
    public double longitude;

    public String url = "";
    public String vehicle = "";
    public String date = "";
}
