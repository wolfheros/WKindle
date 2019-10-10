package com.wolfheros.wkindle;

import com.wolfheros.wkindle.Connection.PhotosDownloader;
import com.wolfheros.wkindle.Connection.ThreadPools;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class HtmlToString implements NewsString{

    private static String NEWS_STORY_BODY__INNER = "story-body__inner";
    private static String TRAVEL_STORY_HERO_UNIT = "hero-unit";
    private static String TRAVEL_STORY_BODYCONTECT = "body-content";
    private String stringUrl;

    private HtmlToString() {
    }
    // Pass the url to object;
    private HtmlToString(String url){
        stringUrl = url;
    }

    // get object
    public static HtmlToString getInstance(String url){
        return new HtmlToString(url);
    }

    /**
    * // bbc 主页内筛选这个类型的。
    * <h3 class="media__title">
            <a class="media__link" href="/news/uk-politics-48532869"
                rev="news|headline" >
                    Labour sees off Brexit Party in by-election
            </a>
        </h3>
    *
    **/
    @Override
    public Map<String,NewsContent> run(){
        // Get a Doucument.此处需要垃圾回收 对NewsContent 的对象
        Map<String,NewsContent> newsContentMap = new LinkedHashMap<>();
        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            Document document = Jsoup.connect(stringUrl).get();
            Elements elements = document.getElementsByClass("media__content");
            System.out.println("获取到的数据：" + elements.size());
            for (Element e: elements) {
                // 判断如果存在 视频的网页 就删除这个筛选项。
                // 每次循环都要创建一个新的 newsContent 对象。
                // 获取每一个 h3.media__title 内部链接
                if (e.hasAttr("a[video|headline]")) {
                    /*elements.remove(e);*/
                    System.out.println("发现视频资源：" + e);
                }else {
                    NewsContent newsContent = NewsContent.getInstance();
                    Elements link = e.getElementsByTag("a");
                    for (int i = 0; i < link.size(); i += 2) {
                        String url = link.get(i).attr("href");
                        if (url != null) {
                            if (url.startsWith("http://www.bbc.com/")
                                    && !url.endsWith("culture")
                                    && !url.startsWith("http://www.bbc.com/future/bespoke/")
                                    && !(url.equals("http://www.bbc.com/future"))) {
                                if (!url.startsWith("http://www.bbc.com/travel/gallery/")
                                        && !(url.startsWith("http://www.bbc.com/culture/slideshow-gallery/"))
                                        && !(url.startsWith("http://www.bbc.com/travel/bespoke/"))){

                                    // 将解析出的网址，具有绝对网址的加上
                                    System.out.println("解析出得绝对网址：" + url);
                                    absoluteurlToString(newsContent, url);
                                    newsContentMap.put(newsContent.getNewsUrl(), newsContent);
                                }
                            } else {
                                // 解析出得是相对url， 强制解析出绝对url
                                url = link.attr("abs:href");
                                if (url.startsWith("https://www.bbc.com/sport") |
                                        url.startsWith("https://www.bbc.com/news/live/") |
                                        url.startsWith("https://www.bbc.com/news/business") |
                                        url.startsWith("https://www.bbc.co.uk/")) {
                                    continue;
                                }
                                System.out.println("解析出得相对网址：" + url);
                                relativeUrlToString(newsContent, url);
                                newsContentMap.put(newsContent.getNewsUrl(), newsContent);
                            }
                        }
                    }
                }
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        return newsContentMap;
    }

    // 从相对链接地址里解析出内容。
    private void relativeUrlToString(NewsContent newsContent, String url) throws IOException{
        Document document = Jsoup.connect(url).get();
        //  获取所有的子元素，嵌套元素
        Elements allElements = document.getElementsByClass("story-body");
        // 如果获取不到就跳过
        if (allElements.size() == 0) {
            return;
        }
        Elements noNullElements = allElements.get(0).getAllElements();
        HashSet<String> hashSet = new LinkedHashSet<>();
        String newsTitle = null;
        String newsAuther = null;
        String newsDate = null;
        for (Element e1 : noNullElements) {
            if (e1.hasClass("story-body")){
                // 获取到标题
                // 获取class 里的字符串
                newsTitle = e1.getElementsByClass("story-body__h1").text();
                System.out.println("获取的标题：" + newsTitle);
            }else if (e1.hasClass("byline")){
                // 作者
                newsAuther = e1.getElementsByClass("byline__name").text();
                System.out.println("获取的作者：" + newsAuther);
            }else if (e1.hasClass("mini-info-list-wrap")){
                // 设置新闻日期
                newsDate = e1.getElementsByClass("date date--v2").text();
                System.out.println("获取的日期：" + newsDate);
            }else if (e1.hasClass(NEWS_STORY_BODY__INNER)){
                hashSetContent(e1,hashSet,NEWS_STORY_BODY__INNER);
            }
        }
        // 设定文章题目、作者、内容
        // 设置新闻Url地址
        newsContent.setNewsUrl(url);
        newsContent.setNewsTitle(newsTitle);
        newsContent.setNewsAuther(newsAuther);
        newsContent.setNewsDate(newsDate);
        newsContent.setNewsContent(hashSet);
        // 未回收垃圾

    }


    /**
     *  获取单一子元素对象，并添加相应的内容。
     *  同时下载相应的内部图片。
     *
     * */
    private void hashSetContent(Element element, HashSet<String> hashSet, String slector){
        // 获取这个元素内所有的子元素
        Elements bodyElements = element.getElementsByClass(slector).first().children();
        for (Element e2: bodyElements
        ) {
            // 获取新闻里内部图片和嵌套在图片中的解释
            if ( e2.selectFirst("img") != null){
                String imageUrl = e2.selectFirst("img").absUrl("src");
                if (!imageUrl.endsWith(".png")) {
                    hashSet.add(imageUrl);
                    // 直接下载照片
                    downloadPhotos(imageUrl);
                    System.out.println("添加图片网址成功");

                    if (e2.selectFirst("img").attr("alt") != null) {
                        hashSet.add(e2.selectFirst("img").attr("alt"));
                        System.out.println("添加图片内容备注成功");
                    }
                }
            }else if (e2.select("p") != null){  // include this element.
                // 获取到了内容
                // 选出包藏在p 元素中字符的链接
                Element aElement = e2.select("a[href]").first();
                if (aElement != null ){
                    if (aElement.attr("href").endsWith(".jpg")) {
                        String imageUrl = aElement.attr("href");
                        hashSet.add(imageUrl);
                        // 直接下载照片
                        downloadPhotos(imageUrl);
                        System.out.println("添加内嵌元素图片网址成功");
                    }
                    hashSet.add(aElement.text());
                }else {
                    hashSet.add(e2.text());
                }
            }
        }
    }


    /**
     * Downloader methor
     * */

    private void downloadPhotos(String imagUrl){
        ThreadPools.getInstance().downloadThreadPools(PhotosDownloader.getInstance(imagUrl));
    }

    /***
     *
     * 外界进程下载
     * */

    private void absoluteurlToString(NewsContent newsContent, String url) throws IOException{
        HashSet<String> hashSet = new LinkedHashSet<>();
        Document document = Jsoup.connect(url).get();
        Elements bodyElements = document.getElementsByClass(TRAVEL_STORY_HERO_UNIT);
        String storyTitle = null;
        String storyAuther = null;
        String storyDate = null;
        System.out.println("绝对地址是： " + url);

        // got image url:
        hashSetContent(bodyElements.first(),hashSet,"hero-unit-image-wrapper");
        // got title sting
        storyTitle =  document.getElementsByClass("hero-unit-lining").first().text();
        //got auther
        storyAuther = document.getElementsByClass("bottom-unit").first().select("index-body").text();
        // got date
        storyDate= document.getElementsByClass("publication-date index-body").text();
        // news Content
        hashSetContent(document,hashSet,TRAVEL_STORY_BODYCONTECT);
        // 设置新闻Url地址
        newsContent.setNewsUrl(url);
        newsContent.setNewsDate(storyDate);
        newsContent.setNewsAuther(storyAuther);
        newsContent.setNewsTitle(storyTitle);
        newsContent.setNewsContent(hashSet);
    }

}
