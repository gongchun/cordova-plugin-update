package com.n22.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 * 
 * ShareRefrence存取数据
 */
public class ShareRefrenceUtil {

	public static final String TAG = "framework";

	public static boolean save(Context context,String key, String value) {
		return edit(context).putString(key, value).commit();
	}

	public static boolean save(Context context,String key, int value) {
		return edit(context).putInt(key, value).commit();
	}

	public static boolean save(Context context,String key, boolean value) {
		return edit(context).putBoolean(key, value).commit();
	}

	public static boolean save(Context context,String key, float value) {
		return edit(context).putFloat(key, value).commit();
	}

	public static boolean remove(Context context,String key) {
		return edit(context).remove(key).commit();
	}

	private static Editor edit(Context context) {
		return context.getApplicationContext().getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
	}

	public static Object get(Context context,String key) {
		return context.getApplicationContext().getSharedPreferences(TAG, Context.MODE_PRIVATE).getAll().get(key);
	}

}
