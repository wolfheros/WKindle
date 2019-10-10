package com.wolfheros.wkindle.Utilities;


/**
 *
 * Cut ImageUrl to a file name String.
 * */
public class CompressUrl {

    public static String cutUrl(String url){
        String[] urls = url.split("/");
        return urls[urls.length-1];
    }

    /**
     * 修复一些结尾不是.jpg的文件
     * */
    public static String fixJpg(String url) {
        String cutUrl = cutUrl(url);
        if (!cutUrl.endsWith(".jpg")){
            cutUrl+=".jpg";
        }
        return cutUrl;
    }
}
