package com.wolfheros.wkindle.CreatePDF;

import com.wolfheros.wkindle.Debug.TerminalDebug;
import com.wolfheros.wkindle.Font.*;
import com.wolfheros.wkindle.NewsContent;
import com.wolfheros.wkindle.Utilities.CompressUrl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import rst.pdfbox.layout.elements.ControlElement;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.ImageElement;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class PDFCreation0 {

    private Map<String,NewsContent> contentMap;
    private NewsContent newsContent;
    private File poctureFile;
    private Document document;
    private PDFCreation0() {
    }

    private PDFCreation0(Map<String,NewsContent> map){
        contentMap = map;

    }
    public static PDFCreation0 getInstance(Map<String, NewsContent> map){
        return new PDFCreation0(map);
    }

    public void createPDF() {
        try {
            float hMargin = 120;
            float vMargin = 50;
            for (String s : contentMap.keySet()
            ) {
                document = new Document(hMargin, hMargin, vMargin, vMargin);
                if (s != null && (contentMap.get(s) != null)) {
                    //map中保存的NewsContent对象
                    newsContent = contentMap.get(s);
                    // 添加题目
                    document.add(addText(removeWinEncode(newsContent.getNewsTitle() +"\n")
                            , loadFont(), 20, true)
                            , new VerticalLayoutHint(Alignment.Left, 0, 0,
                                    0, 0, true));
                    // 添加作者
                    if (newsContent.getNewsAuther() != null) {
                        document.add(addText(removeWinEncode(newsContent.getNewsAuther()+"\n")
                                , loadFont(), 11, false));
                    }
                    //添加时间
                    if (newsContent.getNewsDate() != null) {
                        document.add(addText(removeWinEncode(newsContent.getNewsDate()+"\n")
                                , loadFont(), 11, false));
                    }

                    // document.add(new VerticalSpacer(60));
                    // 添加内容
                    //获取到的hashset
                    HashSet newsSet = newsContent.getNewsContent();
                    Iterator iterator = newsSet.iterator();
                    boolean setPictureDetail = false;
                    // 合成每篇文章
                    while (iterator.hasNext()) {
                        String stringCache = (String) iterator.next();
                        //  去除非法字符。
                        stringCache = removeWinEncode(stringCache);

                        if ((stringCache.startsWith("http"))) {
                            // 添加图片
                            if (pictureExist(stringCache)) {
                                ImageElement imageElement = new ImageElement(poctureFile.getPath());
                                imageElement.setHeight(imageElement.getHeight()/2);
                                imageElement.setWidth(imageElement.getWidth()/2);
                                document.add(imageElement, new VerticalLayoutHint(Alignment.Center, 0, 0,
                                        0, 0, true));
                                setPictureDetail = true;
                            } else {
                                iterator.remove();
                            }
                        } else {
                            //添加内容
                            if (!setPictureDetail) {    //如果是图片描述的话，就紧凑于图片。
                                stringCache += "\n\n";
                                document.add(addText(stringCache, loadFont(), 11, false)
                                        , new VerticalLayoutHint(Alignment.Left, 0, 0,
                                                0, 0));
                                // document.add(new VerticalSpacer(60));
                                setPictureDetail = false;
                            } else {
                                document.add(addText(stringCache, loadFont(), 11, false)
                                        , new VerticalLayoutHint(Alignment.Left, 0, 0,
                                                0, 0));
                                // document.add(new VerticalSpacer(60));
                                setPictureDetail = false;
                            }
                        }
                    }
                     document.add(ControlElement.NEWPAGE);
                }
                OutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.home")
                        + "/WKindle").getPath() + "/BBC.pdf");
                document.save(outputStream);
            }

        }catch (IOException ioe){
            TerminalDebug.systemPrint("输出流出错了");
            ioe.printStackTrace();

        }
    }

    /**
     * 载入新字体
     * */
    private PDFont loadFont() throws IOException{
        PDDocument pdDocument = new PDDocument();
        PDFont pdFont = PDType0Font.load(pdDocument,FontEnum.NotoSans_Light.getFont());
        pdDocument.close();
        return pdFont;

    }

    /**
     *
     * 去除内部非法字符
     * */
    private String removeWinEncode(String s){
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (WinAnsiEncoding.INSTANCE.contains(s.charAt(i))) {
                b.append(s.charAt(i));
            }
        }
        return b.toString();
    }


    private boolean pictureExist(String url){
        boolean exist =true;
        try {
            poctureFile = new File(new File(System.getProperty("user.home")+"/WKindle"), fixJpg(url));
        }catch (NullPointerException ioe){
           exist= false;
            TerminalDebug.systemPrint("读取的图片文件不存在。");
        }

        return exist;
    }
    /**
     * 修复一些结尾不是.jpg的文件
     * */
    private String fixJpg(String url) {
        String cutUrl = CompressUrl.cutUrl(url);
        if (!cutUrl.endsWith(".jpg")){
            cutUrl+=".jpg";
        }
        return cutUrl;
    }
    /***
     *
     * 添加内容
     */
    private Paragraph addText(String text, PDFont font, int size, boolean makeup) throws IOException {
        Paragraph paragraph = new Paragraph();
        if (makeup){
        paragraph.addMarkup("*"+text+"*", 11,
                BaseFont.Helvetica);
            return paragraph;
        }else {
            paragraph.addText(text, size, font);
            return paragraph;
        }
    }
}
