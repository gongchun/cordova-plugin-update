package com.n22.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.os.Environment;
public class ZipUtil {
	public static final String RETURNSUCCESS = "end";
	public static final String RETURNERROR = "error";
	
//	/**
//	 * 解压ZIP文件
//	 * @param zipFileName
//	 * @param outputDirectory
//	 * @throws IOException
//	 */
//	public static String unZip(String zipPath,String zipFileName, String productId)
//			throws IOException {
//		ZipFile zipFile = null;
//		String outputDir;
//		try {
//			zipFile = new ZipFile(zipPath+zipFileName);
//			Enumeration enumeration = zipFile.entries();
//			while (enumeration.hasMoreElements()) {
//				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
//				String name = zipEntry.getName();
//				InputStream in = null;
//				FileOutputStream out = null;
//				if(!CheckUtil.isEmpty(productId)){
//					outputDir = getOutPutDir(name,productId);
//				}else{
//					outputDir = getOutPutDir(name,null);
//				}
//				if (outputDir == null)
//					continue;
//				System.out.println("解压路径"+outputDir+"\n"+name);
//				try {
//					File f = new File(outputDir + File.separator
//							+ name);
//					File dirs = new File(outputDir);
//					if (!dirs.exists()) {
//						dirs.mkdirs();
//					}
//					in = zipFile.getInputStream(zipEntry);
//					out = new FileOutputStream(f);
//
//					int c;
//					byte[] by = new byte[1024];
//
//					while ((c = in.read(by)) != -1) {
//						out.write(by, 0, c);
//					}
//					out.flush();
//				} catch (IOException ex) {
//					ex.printStackTrace();
////					throw new IOException("解压失败：" + ex.toString());
//					return RETURNERROR;
//				} finally {
//					if (in != null) {
//						in.close();
//					}
//					if (out != null) {
//						out.close();
//					}
//				}
//			}
//			FileUtil.deleteSingleFile(new File(zipPath+zipFileName));
//			return RETURNSUCCESS;
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			return RETURNERROR;
////			throw new IOException("解压失败：" + ex.toString());
//		} finally {
//			if (zipFile != null) {
//				try {
//					zipFile.close();
//				} catch (IOException ex) {
//				}
//			}
//		}
//	}
	
	
	/**
	 * 
	 * 解压文件到data目录
	 * 
	 */
	public static String commonUnZip(String zipPath,String fileName,Context context) throws IOException{
		ZipFile zipFile = null;
		zipFile = new ZipFile(fileName);
		Enumeration enumeration = zipFile.entries();
//		String extSD = Environment.getExternalStorageDirectory().getPath();
		String apkName = "";
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				String name = zipPath+zipEntry.getName();
				InputStream in = null;
				FileOutputStream out = null;
				
//				FileOutputStream out =context.openFileOutput(fileName, context.MODE_PRIVATE);
				System.out.println("解压路径"+name);
				if(!name.substring(name.length()-1).equals(File.separator.toString())){
					try {
						File f = new File(name);
						if(f.exists()){
							f.delete();
						}
						FileUtil.createDir(f.getAbsolutePath());
						in =  zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(name);
						int c;
						byte[] by = new byte[1024];
		
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
						if(name.indexOf(".apk")>-1){
							if(apkName.equals("")){
								apkName = name;
							}else{
								apkName += "|" +  name;
							}
						}
					}catch (IOException ex) {
						ex.printStackTrace();
						throw new IOException("解压失败：" + ex.toString());
					} finally {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.close();
						}
					}
				}else{
					File file = new File( name);
					if(!file.exists()){
						file.mkdirs();
					}
				}		
			}
			
			return apkName;
	} 
	/**
	 * 
	 * 解压文件到SD卡目录
	 * 
	 */
	public static String commonUnZip(String zipPath,String fileName) throws IOException{
		ZipFile zipFile = null;
		zipFile = new ZipFile(fileName);
		Enumeration enumeration = zipFile.entries();
		String extSD = Environment.getExternalStorageDirectory().getPath();
		String apkName = "";
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				String name = zipPath+zipEntry.getName();
				InputStream in = null;
				FileOutputStream out = null;
				System.out.println("解压路径"+name);
				if(name.indexOf(".")>-1){
					try {
						File f = new File(extSD + File.separator + name);
						if(f.exists()){
							f.delete();
						}
						FileUtil.createDir(f.getAbsolutePath());
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);
						int c;
						byte[] by = new byte[1024];
		
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
						if(name.indexOf(".apk")>-1){
							if(apkName.equals("")){
								apkName = extSD + File.separator + name;
							}else{
								apkName += "|" + extSD + File.separator + name;
							}
						}
					}catch (IOException ex) {
						ex.printStackTrace();
						throw new IOException("解压失败：" + ex.toString());
					} finally {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.close();
						}
					}
				}else{
					File file = new File(extSD + File.separator + name);
					if(!file.exists()){
						file.mkdirs();
					}
				}		
			}
			
			return apkName;
	} 
	public static String commonUnZip(String fileName) throws IOException{
		ZipFile zipFile = null;
		zipFile = new ZipFile(fileName);
		Enumeration enumeration = zipFile.entries();
		String extSD = Environment.getExternalStorageDirectory().getPath();
		String apkName = "";
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				String name = zipEntry.getName();
				InputStream in = null;
				FileOutputStream out = null;
				if(name.indexOf(".")>-1){
					try {
						File f = new File(extSD + File.separator + name);
						if(f.exists()){
							f.delete();
						}
						FileUtil.createDir(f.getAbsolutePath());
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);
						int c;
						byte[] by = new byte[1024];
		
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
						if(name.indexOf(".apk")>-1){
							if(apkName.equals("")){
								apkName = extSD + File.separator + name;
							}else{
								apkName += "|" + extSD + File.separator + name;
							}
						}
					}catch (IOException ex) {
						ex.printStackTrace();
						throw new IOException("解压失败：" + ex.toString());
					} finally {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.close();
						}
					}
				}else{
					File file = new File(extSD + File.separator + name);
					if(!file.exists()){
						file.mkdirs();
					}
				}		
			}
			
			return apkName;
	} 
//	/**
//	 * 获取解压文件的路径
//	 * @param name
//	 * @param productId
//	 * @return
//	 */
//	public static String getOutPutDir(String name,String productId){
//		if (name.endsWith(".xml")||name.endsWith(".dat")) 
//			return Path.getDataPath()+"dadongfang/product/";
//		else if(!CheckUtil.isEmpty(productId)&&(name.endsWith(".png")||name.endsWith(".jpg")||name.endsWith(".mp4")||name.endsWith(".3gp")||name.endsWith(".pdf")))
//			return Path.getResourcePath()+productId+"/";
//		
//		return null;
//	}
}
