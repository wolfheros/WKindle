package com.wolfheros.wkindle.Connection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread Pool for download pictures.
 *
 * */
public final class ThreadPools {
    private  ExecutorService executorService = Executors.newFixedThreadPool(5);
    private  ExecutorService singleService = Executors.newCachedThreadPool();
    private static ThreadPools singleThreadPools = new ThreadPools();

    public void shutDown(){
        singleService.shutdown();
        executorService.shutdown();
    }

    /**
     * Download pools thread.
     * */
    public void downloadThreadPools(Runnable runnable){
        executorService.submit(runnable);
        //executorService.shutdown();
        /*try {
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
        }catch (InterruptedException inter){
            inter.printStackTrace();
        }*/
    }

    /**
     * Single thread downloader
     * */
    public void downloadThread(Runnable runnable){
        singleService.submit(runnable);
        /*//singleService.shutdown();
        try {
            if (!(singleService.isShutdown())){
                singleService.awaitTermination(1000,TimeUnit.MILLISECONDS);
            }
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }*/
    }

    public static ThreadPools getInstance(){
        return singleThreadPools;
    }
}
