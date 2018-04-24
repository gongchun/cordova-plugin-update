package com.n22.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.CoreConnectionPNames;

import com.n22.service.MyHttpClient;

import android.content.Context;
import android.content.Intent;

@SuppressWarnings("deprecation")
public class FileDownLoader {
	public static final String RETURNSUCCESS = "end";
	public static final String RETURNERROR = "error";
	public static final String RETURNNOFILE = "nofiles";
	private static int CONNECTION_TIMEOUT = 180000;
	private static int SO_TIMEOUT = 180000;
	
	Context context;
	private Intent mIntent;
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public String downloadFile(String url ,String path, String fileName) throws IOException{
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier()); 
		File file = new File(path+fileName);
//		HttpClient client = new DefaultHttpClient();
		HttpClient client = MyHttpClient.getNewHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		InputStream is = null;
		FileOutputStream fileOutputStream = null;
		try {
			// 连接超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
			// 读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			long length = entity.getContentLength();
			System.out.println("下载长度："+length);
			is = entity.getContent();
			if(length == -1){
				FileUtil.deleteSingleFile(new File(path+fileName));
				return RETURNNOFILE;
			}
			if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK){
				return RETURNERROR;
			}
			if (is != null) {
				fileOutputStream = new FileOutputStream(file);
//				fileOutputStream = context.openFileOutput(fileName,Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_WRITEABLE);  
				byte[] buf = new byte[1024];
	 
				int ch = -1;
				int count = 0;
				int i = 0;
                
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					//正在下载进度
					mIntent = new Intent();
					mIntent.setAction("SYS_UPDATE");
					i = (int) (((double) count / (double) length) * 100);
	                mIntent.putExtra("progress",i);
	                context.sendBroadcast(mIntent);
					if (length > 0) {
	
					}
	
				}

				fileOutputStream.flush();
				return RETURNSUCCESS;
			}
		} catch (Exception e) {
			System.out.println("FileDownLoader 下载异常");
			FileUtil.deleteSingleFile(new File(path+fileName));
			return RETURNERROR;
		}finally{
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			if(is!=null){
				is.close();
			}
		}
		return null;
	}
	
	public String downloadFiles(String url ,String path, String fileName) throws IOException{
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		URL urlStr = new URL(URLDecoder.decode(url, "UTF-8"));
		HttpURLConnection connection = (HttpURLConnection) urlStr.openConnection();
		System.setProperty("sun.net.client.defaultConnectTimeout", "3000");
		System.setProperty("sun.net.client.defaultReadTimeout", "3000");
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Content-Type", "text/xml");
		
		
		File file = new File(path+fileName);
		InputStream is = null;
		FileOutputStream fileOutputStream = null;
		try {
			long length = connection.getContentLength();
			System.out.println("下载长度："+length+"");
			is = connection.getInputStream();
			if(length == -1){
				FileUtil.deleteSingleFile(new File(path+fileName));
				return RETURNNOFILE;
			}
			if (is != null) {
				fileOutputStream = new FileOutputStream(file);
	
				byte[] buf = new byte[1024];
	 
				int ch = -1;
				int count = 0;
				int i = 0;
                
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					//正在下载进度
					mIntent = new Intent();
					mIntent.setAction("SYS_UPDATE");
					i = (int) (((double) count / (double) length) * 100);
	                mIntent.putExtra("progress",i);
	                context.sendBroadcast(mIntent);
					if (length > 0) {
	
					}
	
				}

				fileOutputStream.flush();
				return RETURNSUCCESS;
			}
		} catch (Exception e) {
			System.out.println("异常");
			FileUtil.deleteSingleFile(new File(path+fileName));
			return RETURNERROR;
		}finally{
			if (fileOutputStream != null) 
				fileOutputStream.close();
			if(is!=null)
				is.close();
		}
		return null;
	}
}
