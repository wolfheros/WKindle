package com.wolfheros.wkindle;

import java.util.Date;
import java.util.HashSet;

/**
 * this class is for store the news content.
 * Written by James on 8/6/2019
 * */
public class NewsContent {
    private String newsTitle;
    private HashSet<String> newsContent;
    private String newsUrl;
    private String newsAuther;
    private String newsDate;

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    long ID;
    public static NewsContent getInstance(){
        return new NewsContent();
    }
    private NewsContent() {
        this.ID = new Date().getTime();
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public HashSet<String> getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(HashSet<String> newsContent) {
        this.newsContent = newsContent;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getNewsAuther() {
        return newsAuther;
    }

    public void setNewsAuther(String newsAuther) {
        this.newsAuther = newsAuther;
    }
}
