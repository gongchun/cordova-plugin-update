package com.n22.utils;

import java.io.Closeable;

public class CommonUtil {
    
	/**
	 * 关闭流
	 * @param c
	 */
	public static void close(Closeable c){
    	try{
    		if(c != null){
    			c.close();
    		}
    	}catch(Exception e){   		
    		e.printStackTrace();
    	}
    }
	
}
