package com.wolfheros.wkindle;

/***
 * Source , by james, 17/05/2019
 * */

public enum NewsSource{
    /**
     * Free News website;
     * */
    BBC {
        public String newsUrl() {
            return "https://www.bbc.com/";
        }
    },
    CNN{
        public String newsUrl() {
            return "https://www.cnn.com";
        }
    };
    /**
     * 抽象方法，用于实例实现。
     * */
    public abstract String newsUrl();

    /* AsiaJapan{
        String string = "https://asahichinese-j.com";
    },
    Routers{
        String string = "https://www.reuters.com";
    },


    *//**
     * Paid News websites;
     * *//*
    New_York_Time{
        String string = "https://www.nytimes.com";
    },
    WSJ{
        String string = "https://www.wsj.com";
    }*/
}
