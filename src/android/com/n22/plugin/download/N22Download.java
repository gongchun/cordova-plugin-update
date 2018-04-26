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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.n22.thread.SafeThread;
import com.n22.thread.ThreadPool;
import com.n22.utils.FileDownLoader;
import com.n22.utils.FileUtil;
import com.n22.utils.JsonUtil;
import com.n22.utils.ZipUtil;

/**
 * This class echoes a string called from JavaScript.
 */
public class N22Download extends CordovaPlugin {
	public  CallbackContext currentCallbackContext;
	Handler handler;
	int DOWNLOAD_END_FULLDOSE = 6;
	int DOWNLOAD_END = 1;
	int DOWNLOAD_FAIL = 2;
	String appVersionStr,fileName,message;
	ProgressDialog pd;
	@Override
	public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
		// TODO Auto-generated method stub
		super.initialize(cordova, webView);
		handler = new Handler(Looper.getMainLooper()) {
			@SuppressWarnings("static-access")
			@Override
			public void handleMessage(android.os.Message msg) {
				int what = msg.what;
				if (what == DOWNLOAD_END) {
//					下载完成解压
					Map<String,String> map = new HashMap<String, String>();
					Log.w("路径==",cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator);
					map.put("unpackPath",cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"n22/download/");//解压到路径
					map.put("targetPath",cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"n22/download/"+fileName);//zip路径
					unpack(map);
					pd.dismiss();
					currentCallbackContext.success();
				}
				if(what == DOWNLOAD_END_FULLDOSE){
					// finish();下载完成准备安装00000000
					Runtime runtime = Runtime.getRuntime();
					String command1 = "chmod -R 777 " + cordova.getActivity().getFilesDir();
					try {
						runtime.exec(command1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (Build.VERSION.SDK_INT >= 24) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						Uri apkUri = FileProvider.getUriForFile(cordova.getActivity(), cordova.getActivity().getPackageName()+".provider", new File(cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"n22/download/"+fileName));
						intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
						cordova.getActivity().startActivity(intent);
					}else{
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.fromFile(new File(cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"n22/download/"+fileName)),"application/vnd.android.package-archive");
						cordova.getActivity().startActivity(intent);
					}
				}
				if (what == DOWNLOAD_FAIL) {
					currentCallbackContext.error("error");
					pd.dismiss();
//					hint.setText("下载失败");
					Toast.makeText(cordova.getActivity(), "下载失败", Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	@Override
	public void onStart() {
		super.onStart();
//		webView.loadUrlIntoView("file://"+cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"www/web-app/index.html",false);
//		Log.w("路径=",cordova.getActivity().getFilesDir().getAbsolutePath());
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
//			intentDownload("incremental",map);
			fileName = "www.zip";
			message = "增量"+map.get("versionCode")+"版本";
			update(map,fileName,DOWNLOAD_END);
			return true;
		}else if(action.equals("full")){
			HashMap<String,String> map = (HashMap<String, String>) JsonUtil.jsonToObject(args, HashMap.class);
//			intentDownload("full",map);
			fileName = "android.apk";
			message = "全量"+map.get("versionCode")+"版本";
			update(map,fileName,DOWNLOAD_END_FULLDOSE);
			return true;
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

	void update(final HashMap<String,String> map, final String filename, final int type) {
//		progressbar.setIndeterminate(false);
        pd = new ProgressDialog(cordova.getActivity(), ProgressDialog.THEME_HOLO_LIGHT);
		pd.setTitle("正在下载");
		pd.setMessage(message);
		pd.setIndeterminate(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setProgress(100);
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		registerBoradcastReceiver("SYS_UPDATE");
		ThreadPool.excute(new SafeThread(map.get("url")) {
			@Override
			public void deal() {
				FileDownLoader downloader = new FileDownLoader();
				downloader.setContext(cordova.getActivity());
				try {
					String result = downloader.downloadFile(map.get("url"), cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"n22/download/", filename);
					if(result.equals(FileDownLoader.RETURNSUCCESS)){
						System.out.println("下载完成");
						android.os.Message message = android.os.Message.obtain();
						message.what = type;
						handler.sendMessage(message);
					}else{
						System.out.println("下载程序异常1");
						android.os.Message message = android.os.Message.obtain();
						message.what = DOWNLOAD_FAIL;
						handler.sendMessage(message);
					}

				} catch (Exception e) {
					System.out.println("下载程序异常");
					android.os.Message message = android.os.Message.obtain();
					message.what = DOWNLOAD_FAIL;
					handler.sendMessage(message);
					e.printStackTrace();
				}

			}
		});
	}

	public void registerBoradcastReceiver(String action) {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(action);
		System.out.println("reg:");
		cordova.getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("SYS_UPDATE")) {
				int progress = intent.getExtras().getInt("progress");
                pd.setProgress(progress);
//				Toast.makeText(cordova.getActivity(), "更新完成="+progress, Toast.LENGTH_SHORT).show();
//				pBar.setProgress(progress);
//				hint.setText("正在更新"+appVersionStr+"版本："+progress+"%");
//				progressbar.setProgress(progress);
			}
		}
	};

	@SuppressWarnings("unused")
	private void unpack(Map<String,String> message) {
		message.get("targetPath");
		try {
			ZipUtil.commonUnZip(message.get("unpackPath"),message.get("targetPath"),cordova.getActivity());//解压路径，目标路径
			FileUtil.copy(cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"n22/download/www",  cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"www");
			deleteFile(new File(cordova.getActivity().getFilesDir().getAbsolutePath()+ File.separator+"n22/download"));
//            webView.loadUrlIntoView("file:///data/data/"+cordova.getActivity().getPackageName()+"/files/www/web-app/index.html",false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			currentCallbackContext.error("error");
		}
	}

	//flie：要删除的文件夹的所在位置
	private void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				deleteFile(f);
			}
			file.delete();//如要保留文件夹，只删除文件，请注释这行
		} else if (file.exists()) {
			file.delete();
		}
	}
}
