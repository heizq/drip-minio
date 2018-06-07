/**
 * 
 */
package com.drip.minio.util;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author heizhiqiang
 * @date 2017-07-03 16:32:00
 *
 */
public class PropertyConfigUtil {
	
	private static final String PROP_FILE_PATH = "application.properties";
	
	private static Map<String,String>  getAppProperty(){
		Map<String,String> map = new HashMap<String,String>();
		try{
		       
			String path = PropertyConfigUtil.class.getClassLoader().getResource(PROP_FILE_PATH).getPath();
			
			Properties properties = new Properties();
			properties.load(new FileInputStream(path));
			for(String key : properties.stringPropertyNames()) {
		      String value = properties.getProperty(key).trim();
		      map.put(key, value);
			}
			return map;
			
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return map;
	}
	
	public static String getPropertyValue(String key){
        return  getAppProperty().get(key);
    }

}
