package com.n22.plugin.download;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.n22.thread.SafeThread;
import com.n22.thread.ThreadPool;
import com.n22.utils.FileDownLoader;
import com.n22.utils.FileUtil;
import com.n22.utils.MResource;
import com.n22.utils.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shigongwei on 2018/4/4 0004.
 */

public class N22DownloadActivity extends Activity{
    ProgressBar progressbar;
    TextView hint,tv_versions;
    Handler handler;
    int DOWNLOAD_END_FULLDOSE = 6;
    int DOWNLOAD_END = 1;
    int DOWNLOAD_FAIL = 2;
    String appVersionStr,fileName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(this, "layout", "n22_download"));
        progressbar = (ProgressBar) findViewById(MResource.getIdByName(this, "id", "progress_horizontal_color")) ;
        hint = (TextView)findViewById(MResource.getIdByName(this, "id", "hint")) ;
        tv_versions = (TextView)findViewById(MResource.getIdByName(this, "id", "tv_versions")) ;
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        HashMap<String,String> map = (HashMap<String, String>) bundle.get("map");
        String type = (String) bundle.get("type");
        appVersionStr = map.get("versionCode");
        if(type.equals("incremental")){
            fileName = "www.zip";
            update(map.get("url"),fileName,DOWNLOAD_END);
        }else if(type.equals("full")){
            fileName = "android.apk";
            update(map.get("url"),fileName,DOWNLOAD_END_FULLDOSE);
        }
        handler = new Handler(Looper.getMainLooper()) {
            @SuppressWarnings("static-access")
            @Override
            public void handleMessage(android.os.Message msg) {
                int what = msg.what;
                if (what == DOWNLOAD_END) {
//					下载完成解压
                    Map<String,String> map = new HashMap<String, String>();
                    Log.w("路径==",N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator);
                    map.put("unpackPath",N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"n22/download/");//解压到路径
                    map.put("targetPath",N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"n22/download/"+fileName);//zip路径
                    unpack(map);
                    N22Download.currentCallbackContext.success();
                    finish();
                }
                if(what == DOWNLOAD_END_FULLDOSE){
                    // finish();下载完成准备安装00000000
					Runtime runtime = Runtime.getRuntime();
                    String command1 = "chmod -R 777 " + N22DownloadActivity.this.getFilesDir();
                    try {
                        runtime.exec(command1);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri apkUri = FileProvider.getUriForFile(N22DownloadActivity.this, N22DownloadActivity.this.getPackageName()+".provider", new File(N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"n22/download/"+fileName));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        N22DownloadActivity.this.startActivity(intent);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(new File(N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"n22/download/"+fileName)),"application/vnd.android.package-archive");
                        N22DownloadActivity.this.startActivity(intent);
                    }
                }
                if (what == DOWNLOAD_FAIL) {
                    N22Download.currentCallbackContext.error("error");
                    progressbar.setVisibility(View.VISIBLE);
//					hint.setText("下载失败");
                    Toast.makeText(N22DownloadActivity.this, "下载失败", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    void update(final String url,final String filename,final int type) {
        progressbar.setIndeterminate(false);
//        pBar = new ProgressDialog(cordova.getActivity());
//        pBar.setTitle("正在下载");
//        pBar.setMessage("请稍候...");
//        pBar.setIndeterminate(false);
//        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pBar.setProgress(100);
//        pBar.setCancelable(false);
//        pBar.setCanceledOnTouchOutside(false);
//        pBar.show();
        registerBoradcastReceiver("SYS_UPDATE");
        ThreadPool.excute(new SafeThread(url) {
            @Override
            public void deal() {
                FileDownLoader downloader = new FileDownLoader();
                downloader.setContext(N22DownloadActivity.this);
                try {
                    String result = downloader.downloadFile(url, N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"n22/download/", filename);
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
        this.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("SYS_UPDATE")) {
                int progress = intent.getExtras().getInt("progress");
//                pBar.setProgress(progress);
//				Toast.makeText(cordova.getActivity(), "更新完成="+progress, Toast.LENGTH_SHORT).show();
//				pBar.setProgress(progress);
                hint.setText("正在更新"+appVersionStr+"版本："+progress+"%");
                progressbar.setProgress(progress);
            }
        }
    };

    @SuppressWarnings("unused")
    private void unpack(Map<String,String> message) {
        message.get("targetPath");
        try {
            ZipUtil.commonUnZip(message.get("unpackPath"),message.get("targetPath"),N22DownloadActivity.this);//解压路径，目标路径
            FileUtil.copy(N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"n22/download/www",  N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"www");
            deleteFile(new File(N22DownloadActivity.this.getFilesDir().getAbsolutePath()+ File.separator+"n22/download"));
//            webView.loadUrlIntoView("file:///data/data/"+cordova.getActivity().getPackageName()+"/files/www/web-app/index.html",false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            N22Download.currentCallbackContext.error("error");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);//返回桌面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
