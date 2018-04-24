package com.n22.plugin.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.json.JSONException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.n22.utils.FileUtil;
import com.n22.utils.JsonUtil;
import com.n22.utils.ZipUtil;

/**
 * This class echoes a string called from JavaScript.
 */
public class N22Download extends CordovaPlugin {
	public static CallbackContext currentCallbackContext;

	@Override
	public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
		// TODO Auto-generated method stub
		super.initialize(cordova, webView);

	}

	@Override
	public void onStart() {
		super.onStart();
//		webView.loadUrlIntoView("file://"+cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"www/web-app/index.html",false);
		Log.w("路径=",cordova.getActivity().getFilesDir().getAbsolutePath());
		if (!new File(cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"www/index.html").exists()) {
			copyAssetsDir2Phone(cordova.getActivity(),"www");
			Log.d("www", "不存在");
			webView.loadUrlIntoView("file://"+cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"www/index.html", false);
			return;
		}else{//data目录下存在www文件夹 则首次加载data文件下 html
			if(!webView.getUrl().contains(cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator)){
				webView.loadUrlIntoView("file://"+cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"www/index.html", false);
			}
		}
	}

	@Override
	public boolean execute(String action, String args, CallbackContext callbackContext) throws JSONException {
		Toast.makeText(cordova.getActivity(), "进入方法", Toast.LENGTH_LONG).show();
		currentCallbackContext = callbackContext;
		if (action.equals("file")) {
//            this.file(map);
//			Bundle bundle = new Bundle();
//			bundle.putSerializable("MAP", map);
//			Intent intent=new Intent();
//			intent.setAction("com.n22.plugin.download");
//			intent.putExtras(bundle);
//			cordova.getActivity().startActivity(intent);
//			return true;
		}else if(action.equals("unpack")){
//			this.unpack(map);
//			return true;
		}else if(action.equals("incremental")){//增量下载
			HashMap<String,String> map = (HashMap<String, String>) JsonUtil.jsonToObject(args, HashMap.class);
			intentDownload("incremental",map);
			return true;
		}else if(action.equals("full")){
			HashMap<String,String> map = (HashMap<String, String>) JsonUtil.jsonToObject(args, HashMap.class);
			intentDownload("full",map);
		}
		return false;
	}


	private void intentDownload(String type,HashMap<String,String> map) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("map", map);
		bundle.putString("type",type);
		Intent intent=new Intent();
		intent.setAction("com.n22.plugin.download");
		intent.putExtras(bundle);
		cordova.getActivity().startActivity(intent);
	}

	@SuppressWarnings("unused")
	private void unpack(Map<String,String> message) {
		message.get("targetPath");
		try {
			ZipUtil.commonUnZip(message.get("unpackPath"),message.get("targetPath"),cordova.getActivity());//解压路径，目标路径

			FileUtil.copy("/data/data/"+cordova.getActivity().getPackageName()+"/files/n22/download/www/junlong",  "/data/data/"+cordova.getActivity().getPackageName()+"/files/www/web-app");
			webView.loadUrlIntoView("file:///data/data/"+cordova.getActivity().getPackageName()+"/files/www/web-app/index.html",false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			currentCallbackContext.error("error");
		}
	}



	/**
	 *  从assets目录中复制整个文件夹内容,考贝到 /data/data/包名/files/目录中
	 *  @param  activity  activity 使用CopyFiles类的Activity
	 *  @param  filePath  String  文件路径,如：/assets/aa
	 */
	public static void copyAssetsDir2Phone(Activity activity, String filePath){
		try {
			String[] fileList = activity.getAssets().list(filePath);
			if(fileList.length>0) {//如果是目录
				File file=new File(activity.getFilesDir().getAbsolutePath()+ File.separator+filePath);
				file.mkdirs();//如果文件夹不存在，则递归
				for (String fileName:fileList){
					filePath=filePath+File.separator+fileName;

					copyAssetsDir2Phone(activity,filePath);

					filePath=filePath.substring(0,filePath.lastIndexOf(File.separator));
					Log.e("oldPath",filePath);
				}
			} else {//如果是文件
				InputStream inputStream=activity.getAssets().open(filePath);
				File file=new File(activity.getFilesDir().getAbsolutePath()+ File.separator+filePath);
				Log.i("copyAssets2Phone","file:"+file);
//				if(!file.exists() || file.length()==0) {
				FileOutputStream fos=new FileOutputStream(file);
				int len=-1;
				byte[] buffer=new byte[1024];
				while ((len=inputStream.read(buffer))!=-1){
					fos.write(buffer,0,len);
				}
				fos.flush();
				inputStream.close();
				fos.close();
//					Toast.makeText(activity,"模型文件复制完毕",Toast.LENGTH_LONG).show();
//				} else {
//					Toast.makeText(activity,"模型文件已存在，无需复制",Toast.LENGTH_LONG).show();
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
