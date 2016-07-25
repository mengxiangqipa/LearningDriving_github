package com.my.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gooddream.learningdriving.application.ShareApplication;

/**
 * @description 配置信息读写工具类
 */

public class PreferencesHelper {
    // /////////////
    private static final String NAME = "sharepreferences_gooddream";
    private static SharedPreferences sp;
	private static PreferencesHelper preferenceHelper;

    private PreferencesHelper() {

    }

    public static PreferencesHelper getPerferences(Context a) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, 0);
        }
        return preferenceHelper;
    }

    public static PreferencesHelper getPerferences() {
        return preferenceHelper;
    }

    public static void putString(String name, String data) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        Editor e = sp.edit().putString(name, data);
        e.apply();
    }

    public static void putInt(String name, int data) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        Editor e = sp.edit().putInt(name, data);
        e.apply();
    }

    public static void putBoolean(String name, boolean data) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        Editor e = sp.edit().putBoolean(name, data);
        e.apply();
    }

    public static void putLong(String name, long data) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        Editor e = sp.edit().putLong(name, data);
        e.apply();
    }

    public static String getString(String name) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        return sp.getString(name, "");
    }

    public static int getInt(String name) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        return sp.getInt(name, 0);
    }

    public static boolean getBoolean(String name) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        return sp.getBoolean(name, false);
    }

    public static long getLong(String name) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        return sp.getLong(name, 0);
    }

    public static void remove(String name) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferencesHelper();
            sp = ShareApplication.share.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
        }
        Editor edit = sp.edit();
        edit.remove(name);
        edit.apply();
    }
}
