package com.l000phone.crawler.domain;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Description：Page实体类 （对产品页面解析后的结果的封装）<br/>
 * Copyright (c) ， 2018， Jansonxu <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 徐文波
 * @version : 1.0
 */
@Data
public class Page {
    /**
     * 产品的唯一标识 ~>String，UUID ，全球唯一的字符串
     */
    private String id;

    /**
     * 产品编号 ~>String
     */
    private String goodsId;

    /**
     * 来源  ~>String （商家的网站的顶级域名，如：jd.com，taobao.com）
     */
    private String source;


    /**
     * url~>String
     */
    private String url;

    /**
     * 页面内容 ~>String （产品url对应的页面的详细内容，别的属性都是来自于该属性）
     *
     * 注意：建表时，该属性不需要映射为相应的字段。为了辅助计算其他的属性值。
     */
    private String content;

    /**
     * 标题~>String
     */
    private String title;

    /**
     * 图片url ~>String
     */
    private String imageUrl;


    /**
     * 售价 ~>Double
     */
    private double price;

    /**
     * 评论数~>int
     */
    private int commentCnt;

    /**
     * 好评率 ~>Double
     */
    private double goodRate;

    /**
     * 参数 ~>String, 向表中相应的字段处存入一个json对象格式的数据
     */
    private String params;

    /**
     * 一个类中，若是类的属性石集合类型，一般需要进行手动初始化
     * 1、 若当前页面是商品列表页面，存放的是列表页面中所有的商品URL
     * 2、若当前页面是品类列表页面，存放的是所有商品品类的URL
     * @return
     */
    private List<String> urls = new LinkedList<>();

    @Override
    public String toString() {
        return "Page{" +
                "id='" + id + '\n' +
                ", goodsId='" + goodsId + '\n' +
                ", source='" + source + '\n' +
                ", url='" + url + '\n' +
                ", title='" + title + '\n' +
                ", imageUrl='" + imageUrl + '\n' +
                ", price=" + price + '\n'+
                ", commentCnt=" + commentCnt + '\n'+
                ", goodRate=" + goodRate + '\n'+
                ", params='" + params + '\n' +
                '}';
    }

}
