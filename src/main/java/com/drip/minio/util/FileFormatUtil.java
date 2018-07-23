package com.drip.minio.util;


import com.google.api.client.util.Strings;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lenovo on 2018/6/4.
 */
public class FileFormatUtil {


    private static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    private static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public static String getUniqueFileName(String fileName) {

        if(Strings.isNullOrEmpty(fileName)) {
            return System.currentTimeMillis() + "."+ getFileExtension(fileName);
        }
        String str = getFileNameNoEx(fileName);
        str = removeUnValidCharacter(str);
        return  str + "_" + System.currentTimeMillis() + "." + getFileExtension(fileName);
    }

    private static String removeUnValidCharacter(String str){
        return str.replaceAll("[^a-zA-Z0-9]", "").replaceAll("\\s+", "_").replaceAll(",","");
    }

    public static String getFileNamePrefixDir(){
        StringBuffer buf = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        buf.append(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH) + 1;
        buf.append(month >= 10 ? month : "0" + month);
        return buf.toString();
    }

    public static String getFileRelativePathForDelete(String bucketName,String path){
        int length = bucketName.length();
        if(path.length() >= (length + 2)){
            return path.substring(length + 2);
        }
        return "";
    }

    public static String getFileNameFromPath(String path){
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }
}
