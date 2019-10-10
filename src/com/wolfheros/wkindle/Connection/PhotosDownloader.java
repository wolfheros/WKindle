package com.wolfheros.wkindle.Connection;

import com.wolfheros.wkindle.Utilities.CompressUrl;
import com.wolfheros.wkindle.Debug.TerminalDebug;
import com.wolfheros.wkindle.Utilities.SystemOperation;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/***
 * 下载照片线程 July 1, 2019 by James
 * */
public class PhotosDownloader implements Runnable{
    private String url;
    private PhotosDownloader(String s){
        url = s;
    }
    public static PhotosDownloader getInstance(String s){
        return new PhotosDownloader(s);
    }
    @Override
    public void run() {
        try {
            // if the pictures file already exists.no need to download again.
            if (!(isExists(url))) {
                FileOutputStream fileOutputStream = new FileOutputStream(catFile(url));
                byte[] data = urlConnection();
                if (data == null) {
                    TerminalDebug.systemPrint("Not Get Picture Data.");
                    return;
                }
                fileOutputStream.write(data);
                fileOutputStream.close();
            }else {
                TerminalDebug.systemPrint("图片文件已经存在： " + CompressUrl.cutUrl(url));
            }

        }catch (IOException ioe){
                ioe.printStackTrace();
        }
    }

    /***
     *
     * If pictures is already exists. no need download.
     */
    private boolean isExists(String url){
        return catFile(url).exists();
    }

    /**
     * create file location and locked file position.
     * */
    private File catFile(String url){
        return new File(SystemOperation.FILE_OBJECT_WKindle, CompressUrl.cutUrl(url));
    }

    /**
     *  make a URL connection download pictures bytes.
     * */
    private byte[] urlConnection() throws IOException{
        URL linkUrl = new URL(url);
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        HttpURLConnection httpURLConnection = (HttpURLConnection) linkUrl.openConnection();
        try {
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                TerminalDebug.systemPrint("打开图片链接失败: " + url);
                return null;
            }

            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int byteRead =0;
            byte[] buffer = new byte[1024];
            while ((byteRead = inputStream.read(buffer)) > 0){
                outputStream.write(buffer,0, byteRead);
            }
            inputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        }finally {
            httpURLConnection.disconnect();
        }
    }

}


