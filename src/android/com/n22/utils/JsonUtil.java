package com.n22.utils;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
/**
 * @author yang
 */
public class JsonUtil {

	/**
	 * Json转对象
	 */
	public static Object jsonToObject(String json, Class<?> classOfT) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.fromJson(json, classOfT);
	}

	/**
	 * Json转对象
	 */
	public static Object jsonToObject(String json, Type type) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.fromJson(json, type);
	}

	/**
	 * json解析回ArrayList,参数为new TypeToken<ArrayList<T>>() {},必须加泛型
	 */
	public static List<?> jsonToList(String json, TypeToken<?> token) {
		return (List<?>) jsonToObject(json, token.getType());
	}

	/**
	 * 对象转Json
	 */
	public static String objetcToJson(Object object) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.toJson(object);
	}

}
