package com.wolfheros.wkindle.CreatePDF;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.property.AreaBreakType;
import com.wolfheros.wkindle.Debug.TerminalDebug;
import com.wolfheros.wkindle.Font.FontEnum;
import com.wolfheros.wkindle.NewsContent;
import com.wolfheros.wkindle.Utilities.CompressUrl;
import com.wolfheros.wkindle.Utilities.SystemOperation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class PDFCreation1 {

    private Map<String,NewsContent> contentMap;
    private NewsContent newsContent;
    private File poctureFile;
    //private Document document;
    // 字体文件选择
    private int TITLE_FONT = 0;
    private int CONTENT_FONT = 1;
    //头部文件标示
    private int HEADER_TITLE = 2;
    private int HEADER_AUTHER = 3;
    private int HEADER_DATE = 4;
    private int CONTENT =5;

    private PDFCreation1() {
    }

    private PDFCreation1(Map<String,NewsContent> map){
        contentMap = map;

    }
    public static PDFCreation1 getInstance(Map<String, NewsContent> map){
        return new PDFCreation1(map);
    }

    public void createPDF() {
        try {
            Document document = createDocument();
            for (String s : contentMap.keySet()) {
                Paragraph paragraph = new Paragraph();
                // each key means a News title.
                if (s != null && (contentMap.get(s) != null)) {

                    //map中保存的NewsContent对象
                    newsContent = contentMap.get(s);
                    // 添加内容, 添加标题、作者、日期
                    document.add(addContents(new Paragraph(),newsContent.getNewsTitle() + "\n", HEADER_TITLE))
                            .add(addContents(new Paragraph(),newsContent.getNewsAuther() + "\n", HEADER_AUTHER))
                            .add(addContents(new Paragraph(),newsContent.getNewsDate()+ "\n", HEADER_DATE));

                    //获取到的hashset
                    HashSet newsSet = newsContent.getNewsContent();
                    Iterator iterator = newsSet.iterator();
                    boolean setPictureDetail = false;
                    // 合成每篇文章
                    while (iterator.hasNext()) {
                        String stringCache = (String) iterator.next();
                        if ((stringCache.startsWith("http"))) {
                            // 添加图片
                            if (pictureExist(stringCache)) {
                                document.add(addImage(new Paragraph(),stringCache));
                                setPictureDetail = true;
                            } else {
                                iterator.remove();
                            }
                        } else {
                            //添加内容
                            if (!setPictureDetail) {    //如果是图片描述的话，就紧凑于图片。
                                stringCache += "\n\n";
                                document.add(addContents(new Paragraph(),stringCache,CONTENT));
                                setPictureDetail = false;
                            } else {
                                document.add(addContents(new Paragraph(),stringCache,CONTENT));
                                setPictureDetail = false;
                            }
                        }
                    }
                    // 从新开始一页。
                }
                //document.add(paragraph);
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

        }catch (IOException ioe){
            TerminalDebug.systemPrint("输出流出错了");
            ioe.printStackTrace();

        }
    }
    /**
     * add image
     * */
    private Paragraph addImage(Paragraph paragraph,String url) throws IOException{
        ImageData imageData = ImageDataFactory.create(SystemOperation.STRING_WKindle+File.separator+CompressUrl.cutUrl(url));
        Image image = new Image(imageData);
        image.setAutoScale(true);
        return paragraph.add(image);
    }

    // 添加头部文件
    private Paragraph addContents(Paragraph paragraph, String string, int header) throws IOException{
        if (string != null){
            if (header == HEADER_TITLE) {
                paragraph.add(new Text(string).setFontSize(20).setFont(loadFont(TITLE_FONT)));
            }else if (header == HEADER_AUTHER || header == HEADER_DATE){
                paragraph.add(new Text(string).setFontSize(12).setFont(loadFont(TITLE_FONT)));
            }else{
                paragraph.add(new Text(string).setFontSize(14).setFont(loadFont(CONTENT_FONT)));
            }
        }
        return paragraph;
    }

    /**
     * Set a customer Font.
     * */
    private PdfFont loadFont(int i) throws IOException{
        if (i == CONTENT_FONT) {
            // 返回题目字体
            return PdfFontFactory.createFont(FontEnum.NotoSans_Light.getFont().getPath()
                    , PdfEncodings.UTF8, true);
        }
        // 返回内容字体
        return PdfFontFactory.createFont(FontEnum.NotoSans_Italic.getFont().getPath()
                , PdfEncodings.UTF8, true);
    }

    /***
     * Create a document
     * */
    private Document createDocument() throws IOException{
        File newFile = new File(SystemOperation.FILE_OBJECT_WKindle, "BBC.pdf");
        /*if (!newFile.getParentFile().mkdirs()){
           TerminalDebug.systemPrint("获取和创建文件出错。");
        }*/
        // Create a Writer.
        // Create a PDF document.
        // Create a document
        Document document = new Document(new PdfDocument(new PdfWriter(newFile)));
        // set the default font
        document.setFont(loadFont(CONTENT_FONT));
        return document;
    }

    private boolean pictureExist(String url){
        boolean exist =true;
        try {
            poctureFile = new File(getFileLocation(), fixJpg(CompressUrl.cutUrl(url)));
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
        return CompressUrl.fixJpg(url);
    }

    /**
     * return file location
     * */

    private File getFileLocation(){
        return SystemOperation.FILE_OBJECT_WKindle;
    }
}
