package com.example.admin.pintu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

/**
 * SP相关工具类
 * Created by admin on 2017/7/28.
 */

public class SPUtils {

    private static SharedPreferences sharedPreferences;

    public static void initSP(Context context) {
        sharedPreferences = context.getSharedPreferences("shared_set", Context.MODE_PRIVATE);
    }

    public static void putValue(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
    }

    public static String getString(String key, String defValue) {
        String temp = sharedPreferences.getString(key, "");
        if (TextUtils.isEmpty(temp)) {
            temp = defValue;
        }
        return temp;
    }

    public static boolean getBool(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public static void clear() {
        Map<String, ?> map = sharedPreferences.getAll();
        Set<String> set = map.keySet();
        for (String aSet : set) {
            sharedPreferences.edit().remove(aSet).apply();
        }
    }


}