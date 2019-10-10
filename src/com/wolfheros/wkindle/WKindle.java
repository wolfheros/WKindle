package com.wolfheros.wkindle;

import com.wolfheros.wkindle.Connection.ThreadPools;
import com.wolfheros.wkindle.CreatePDF.PDFCreation1;
import com.wolfheros.wkindle.Debug.TerminalDebug;
import com.wolfheros.wkindle.Utilities.SystemOperation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
*  This is main class, created by James
 *  16/05/2019
* */

public class WKindle {

    private NewsSource newsSource;
    private String newsName;
    private String newsBody;

    private WKindle() { }
    private WKindle(NewsSource ns){
        newsSource = ns;
    }
    private static WKindle getInstance(NewsSource ns){
        return new WKindle(ns);
    }

    /**
     *  Get Map instance, main run().
     * */
    private Map<String, NewsContent> stringMap(){
        return HtmlToString.getInstance(newsSource.newsUrl()).run();
    }

    /**
     * main run methon.
     * */
    private int run(){
        switch (newsSource){
            case BBC:
                // HtmlToString BBCUrltoString = HtmlToString.getInstance(newsSource.newsUrl());
                Map<String, NewsContent> newsContent = stringMap();
                /**
                 *  Its not this way.
                 * */
                // PDFCreation0.getInstance(newsContent).createPDF();
                PDFCreation1.getInstance(newsContent).createPDF();
                TerminalDebug.systemPrint("获取最终数据的大小： "+newsContent.size());
                TerminalDebug.systemPrintMap(newsContent);

            case CNN:

        }
        return 0;
    }

    public static void main(String[] args) throws IOException{
        if (createFile().exists()){
            TerminalDebug.systemPrint("创建输出文件成功");
        }else {
            TerminalDebug.systemPrint("创建输出文件目录失败");
            return;
        }
        getInstance(NewsSource.BBC).run();
        shutDownAll();
    }
    /**
     * create save picture file
     * */
    private static File createFile() throws IOException {
        Runtime.getRuntime().exec(SystemOperation.mkdirFile());
        return SystemOperation.FILE_OBJECT_WKindle;
    }
    /***
     *
     * Shutdown all threads in the program.
     */
    private static void shutDownAll(){

        /**
         * shutdown pictures download threads pool.
         * */
        ThreadPools.getInstance().shutDown();
    }
}
