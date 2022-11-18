package com.expert;

import com.expert.utils.HttpClientUtil;
import com.expert.utils.WebAccessible;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //需要爬取的信息源网页
        String url = "https://www.eastmoney.com/";
        //网页Html
        String pageHtml = "NG";
        WebAccessible webAccessible = new HttpClientUtil();
        try {
            //请求信息源网页
            pageHtml = webAccessible.requestPage(url);
        } catch (IOException e) {
            System.out.println( "Request for url ["+url+"] failed."+e.getMessage());
        }
        //根据页面Html生成DOM对象
        Document pageDocument = Jsoup.parse(pageHtml);
        //使用Selector定位指定元素
        Element categoryElement = pageDocument.selectFirst("#hq-news-main > div:nth-child(2) > div.hq-news-con-b.first.on > div.hq-news-data > div.nickname > a");
        Element valueElement = pageDocument.selectFirst("#hq-news-main > div:nth-child(2) > div.hq-news-con-b.first > div.hq-news-data > div.hq-news-value.item_nowPrice > span");
        Element rateElement = pageDocument.selectFirst("div > div.hq-news-data > div:nth-child(3) > span");
        Element diffElement = pageDocument.selectFirst("#hq-news-main > div:nth-child(2) > div.hq-news-con-b.first > div.hq-news-data > div:nth-child(4) > span");
        //打印对应元素信息
        System.out.println(categoryElement.text()+" "+valueElement.text()+" "+rateElement.text()+" "+diffElement.text());
    }
}
