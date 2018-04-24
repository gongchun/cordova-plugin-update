package com.n22.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import android.content.Context;
import android.graphics.Bitmap;

public class FileUtil {

	/**
	 * 删除文件或整个文件夹
	 * 
	 * @param path
	 */
	public static void deleteDirs(String path) {
		File f = new File(path);
		if (f.exists()) {
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				for (int i = 0; files != null && i < files.length; i++) {
					deleteSingleFile(files[i]);
				}
			} else {
				deleteSingleFile(f);
			}
		}
	}
	/**
	 * 保存字符串到指定路径
	 * 
	 * 
	 */
	public static void saveAsFileOutputStream(String physicalPath,String content) {
	    File file = new File(physicalPath);
	    boolean b = file.getParentFile().isDirectory();
	    if(!b){
	     File tem = new File(file.getParent());
	     // tem.getParentFile().setWritable(true);
	     tem.mkdirs();// 创建目录
	    }
	    //Log.info(file.getParent()+";"+file.getParentFile().isDirectory());
	    FileOutputStream foutput =null;
	    try {
	     foutput = new FileOutputStream(physicalPath);

	     foutput.write(content.getBytes("UTF-8"));
	     //foutput.write(content.getBytes());
	    }catch(IOException ex) {
	     ex.printStackTrace();
	     throw new RuntimeException(ex);
	    }finally{
	     try {
	      foutput.flush();
	      foutput.close();
	     }catch(IOException ex){
	      ex.printStackTrace();
	      throw new RuntimeException(ex);
	     }
	    }
	     //Log.info("文件保存成功:"+ physicalPath);
	   }
	public static void deleteSingleFile(File f) {
		if (f.isDirectory())
			deleteDirs(f.getAbsolutePath());
		else {
			if (f.exists())
				f.delete();
		}
	}

	public static String loadInput(InputStream input) {
		return loadInput(input, "UTF-8");
	}

	public static String loadInput(InputStream input, String charsetName) {
		if (input != null) {
			BufferedInputStream br = null;
			try {
				br = new BufferedInputStream(input);
				byte[] bf = new byte[1024];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int len = 0;
				while ((len = br.read(bf)) > 0) {
					out.write(bf, 0, len);
				}
				return new String(out.toByteArray(), charsetName);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				CommonUtil.close(br);
			}
		}
		return null;
	}

	/**
	 * 读取文件中的字符串
	 */
	public static String loadFile(String path) {
		File f = new File(path);
		if (!f.exists())
			return null;
		FileReader fileReader = null;
		BufferedReader br = null;
		StringBuilder buffer = new StringBuilder("");
		try {
			fileReader = new FileReader(f);
			br = new BufferedReader(fileReader);
			String str;
			while ((str = br.readLine()) != null) {
				buffer.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtil.close(br);
			CommonUtil.close(fileReader);
		}
		return buffer.toString();
	}

	/**
	 * 以对象流保存至文件
	 * 
	 * @param object
	 * @param path
	 */
	public static void saveObject(Object object, String path) {
		File f = new File(path);
		if (!f.exists())
			createDir(path);
		FileOutputStream ou = null;
		ObjectOutputStream out = null;
		try {
			ou = new FileOutputStream(f);
			out = new ObjectOutputStream(ou);
			out.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtil.close(out);
			CommonUtil.close(ou);
		}
	}

	public static void saveString(String str, String path) {
		writeString(str, path, false);
	}

	public static void createDir(String path) {
		File f = new File(path.substring(0, path.lastIndexOf("/")));
		if (!f.exists())
			f.mkdirs();
	}

	/**
	 * 将字符串存入文件
	 * 
	 * @param str
	 * @param path
	 */
	public static void writeString(String str, String path, boolean isAdd) {
		File f = new File(path);
		if (!f.exists())
			createDir(path);
		FileOutputStream ou = null;
		OutputStreamWriter out = null;
		BufferedWriter writer = null;
		try {
			ou = new FileOutputStream(f, isAdd);
			out = new OutputStreamWriter(ou);
			writer = new BufferedWriter(out);
			writer.write(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtil.close(writer);
			CommonUtil.close(out);
			CommonUtil.close(ou);
		}
	}

	/**
	 * 读取某个文件解析回对象
	 * 
	 * @param path
	 * @return
	 */
	public static Object loadObject(String path) {
		File f = new File(path);
		if (!f.exists())
			return null;
		FileInputStream in = null;
		ObjectInputStream input = null;
		Object obj = null;
		try {
			in = new FileInputStream(f);
			input = new ObjectInputStream(in);
			obj = input.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtil.close(input);
			CommonUtil.close(in);
		}
		return obj;
	}

	public static boolean copyFile(Context context, String oldPath, String newPath) {
		File oldFile = new File(oldPath);
		if (!oldFile.exists())
			return false;

		File dir = new File(newPath.substring(0, newPath.lastIndexOf("/") + 1));
		if (!dir.exists())
			dir.mkdirs();

		InputStream input = null;
		OutputStream output = null;
		try {
			if (oldPath.startsWith("assets:")) {
				input = context.getAssets().open(oldPath.replaceFirst("assets:", "").trim());
			} else {
				input = new FileInputStream(new File(oldPath));
			}
			output = new FileOutputStream(newPath);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = input.read(buffer)) > 0) {
				output.write(buffer, 0, len);
			}
		} catch (Exception e) {
		} finally {
			CommonUtil.close(input);
			CommonUtil.close(output);
		}

		return true;
	}
	
	public static byte[] loadFileByte(InputStream input) {
		if (input != null) {
			BufferedInputStream br = null;
			try {
				br = new BufferedInputStream(input);
				byte[] bf = new byte[1024];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int len = 0;
				while ((len = br.read(bf)) > 0) {
					out.write(bf, 0, len);
				}
				return out.toByteArray();
			} catch (Exception e) {
			} finally {
				CommonUtil.close(br);
			}
		}
		return null;
	}
	
	public static byte[] loadFileByte(String path) {
		File f = new File(path);
		if (!f.exists()) {
			return null;
		}
		FileInputStream input = null;
		byte[] arr = null;
		try {
			input = new FileInputStream(f);
			arr = loadFileByte(input);
		} catch (Exception e) {
		} finally {
			CommonUtil.close(input);
		}
		return arr;
	}
	
	  /** 
     * 根据byte数组，生成文件 
     */  
    public static void getFile(byte[] bfile, String filePath,String fileName) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
                dir.mkdirs();  
            }  
            file = new File(filePath+"/"+fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
    
    public static void copy(String src, String des) throws IOException {  //"F:\\myjava","E:\\myjava"
        File file1=new File(src);  
        File[] fs=file1.listFiles();  
        File file2=new File(des);  
        if(!file2.exists()){  
            file2.mkdirs();  
        }  
        for (File f : fs) {  
            if(f.isFile()){  
                fileCopy(f.getPath(),des+"/"+f.getName()); //调用文件拷贝的方法  
            }else if(f.isDirectory()){  
                copy(f.getPath(),des+"/"+f.getName());  
            }  
        }  
          
    }  
  
    /** 
     * 文件拷贝的方法 
     * @throws IOException 
     */  
    private static void fileCopy(String src, String des) throws IOException {  
      
//        BufferedReader br=null;  
//        PrintStream ps=null;  
//          
//        try {  
//            br=new BufferedReader(new InputStreamReader(new FileInputStream(src)));  
//            ps=new PrintStream(new FileOutputStream(des));  
//            String s=null;  
//            while((s=br.readLine())!=null){  
//                ps.println(s);  
//                ps.flush();  
//            }  
//              
//        } catch (FileNotFoundException e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        } catch (IOException e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        }finally{  
//              
//                try {  
//                    if(br!=null)  
//                    	br.close();  
//                    if(ps!=null)  
//                    	ps.close();  
//                } catch (IOException e) {  
//                    // TODO Auto-generated catch block  
//                    e.printStackTrace();  
//                }  
//                  
//        }  
          
    	InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(des);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }  
    
	//Bitmap对象保存味图片文件
	public static void saveBitmapFile(Bitmap bitmap,String path){
	            File file=new File(path);//将要保存图片的路径
	            try {
	        	    boolean b = file.getParentFile().isDirectory();
	        	    if(!b){
	        	     File tem = new File(file.getParent());
	        	     // tem.getParentFile().setWritable(true);
	        	     tem.mkdirs();// 创建目录
	        	    }
	                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
	                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
	                    bos.flush();
	                    bos.close();
	            } catch (IOException e) {
	                    e.printStackTrace();
	            }
	}
}
