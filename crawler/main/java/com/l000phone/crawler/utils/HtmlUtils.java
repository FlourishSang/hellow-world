package com.l000phone.crawler.utils;

import com.alibaba.fastjson.JSON;
import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.domain.comment.CommentBean;
import com.l000phone.crawler.domain.comment.ProductComment;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Description：对html页面操作工具类<br/>
 * Copyright (c) ， 2018， Jansonxu <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月05日
 *
 * @author 徐文波
 * @version : 1.0
 */
public class HtmlUtils {
    /**
     * 根据指定的url，将下载后的页面内容保存到内存中
     *
     * @param url
     * @return
     */
    public static String downloadPageContentToMemory(String url) {
        //使用技术：HttpClient
        //HttpClient:类比作browser
        HttpClient client = HttpClients.createDefault();

        //请求方式：GET,POST,PUT,DELETE等等
        HttpUriRequest request = new HttpGet(url);

        try {
            //类比：在浏览器键入url，敲回车
            HttpResponse response = client.execute(request);

            //判断状态码是200 :服务器端已经将反馈的结果正常传送给客户端了
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 求产品的评论数和好评率
     * @param page
     */

    public static void getCommentCntAndGoodRate(Page page){
        //①根据url，获得内容
        String commentUrl = "https://club.jd.com/comment/productCommentSummaries.action?referenceIds=" + page.getGoodsId() + "&_=" + CrawlerUtils.getNowTimeMillions();
        //System.out.println(commentUrl);

        String content = HtmlUtils.downloadPageContentToMemory(commentUrl);

        //②解析内容
        CommentBean commentBean = null;
        try {
            commentBean = JSON.parseObject(content, ProductComment.class).getCommentsCount().get(0);
        } catch (Exception e) {
           // e.printStackTrace();
            commentBean = new CommentBean();
        }
        System.out.println("评论数："+commentBean.getCommentCount());
        System.out.println("好评率："+commentBean.getGoodRate());
        System.out.println("好："+commentBean.getGoodRateStyle());
        page.setCommentCnt(commentBean.getCommentCount());
        page.setGoodRate(commentBean.getGoodRate());
    }
}
