package com.l000phone.crawler.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.domain.price.PriceBean;
import com.l000phone.crawler.domain.price.ProductPrice;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import redis.clients.jedis.Jedis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description：爬虫项目其他共通操作工具类<br/>
 * Copyright (c) ， 2018， Jansonxu <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 徐文波
 * @version : 1.0
 */
public class CrawlerUtils {
    /**
     * 获得全球唯一的一个随机字符串
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 获得产品id
     *
     * @param url ,如：https://item.jd.com/8735304.html
     * @return
     */
    public static String getGoodsId(String url) {
        int beginIndex = url.lastIndexOf('/') + 1;
        int endIndex = url.lastIndexOf('.');
        return url.substring(beginIndex, endIndex);
    }

    /**
     * 获取指定url的顶级域名
     *
     * @return
     */
    public static String getTopDomain(String url) {
        try {
            String host = new URL(url).getHost().toLowerCase();// 此处获取值转换为小写
            Pattern pattern = Pattern.compile("[^\\.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");
            Matcher matcher = pattern.matcher(host);
            while (matcher.find()) {
                return matcher.group();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据指定的xPath，获取对应标签标签体的值
     *
     * @param cleaner
     * @param page
     * @param xPath
     * @return
     */
    public static String getTagTextValueByAttr(HtmlCleaner cleaner, Page page, String xPath) {
        TagNode tagNode = cleaner.clean(page.getContent());
        try {
            Object[] objs = tagNode.evaluateXPath(xPath);
            if (objs != null && objs.length > 0) {
                TagNode node = (TagNode) objs[0];
                String value = node.getText().toString().trim();
                return value;
            }
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据指定的xPath，获取对应标签的属性值
     *
     * @param cleaner
     * @param page
     * @param xPath
     * @return
     */
    public static String getTagAttrValueByAttr(HtmlCleaner cleaner, Page page, String xPath, String arrName) {
        TagNode tagNode = cleaner.clean(page.getContent());
        try {
            Object[] objs = tagNode.evaluateXPath(xPath);
            if (objs != null && objs.length > 0) {
                TagNode node = (TagNode) objs[0];
                String value = node.getAttributeByName(arrName);
                return value;
            }
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析商品的价格
     *
     * @return
     */
    public static double parseProducePrice(String priceUrl) {
        //①根据url，获得内容
        String content = HtmlUtils.downloadPageContentToMemory(priceUrl);

        //②解析内容
        JSONArray jsonArray = JSON.parseArray(content);
        JSONObject obj = new JSONObject();
        obj.put("beans", jsonArray);
        //str: {"beans",[{"op":"1399.00","m":"9999.00","id":"J_7479820","p":"1199.00"}]}
        ProductPrice productPrice = JSON.parseObject(obj.toJSONString(), ProductPrice.class);
        PriceBean bean = productPrice.getBeans().get(0);
        return Double.valueOf(bean.getP());
    }

    /**
     * 获得系统当前时间的毫秒值
     *
     * @return
     */
    public static long getNowTimeMillions() {
        return new Date().getTime();
    }

    /**
     * 解析商品参数
     *
     * @param cleaner
     * @param page
     * @return
     */
    public static JSONObject parseGoodsIntroduction(HtmlCleaner cleaner, Page page) {
        JSONObject goodsIntroduce = new JSONObject();
        String goodsIntoduction = CrawlerUtils.getTagTextValueByAttr(cleaner, page, "//li[@clstag='shangpin|keycount|product|shangpinjieshao_1']");
        JSONObject goodsIntroduceDetail = new JSONObject();
        goodsIntroduce.put(goodsIntoduction, goodsIntroduceDetail);


        TagNode tagNode = cleaner.clean(page.getContent());
        try {
            //关于商品介绍中主要参数
            Object[] objects = tagNode.evaluateXPath("//*[@id=\"detail\"]/div[2]/div[1]/div[1]/ul[1]/li[*]/div");

            if (objects != null && objects.length > 0) {
                for (Object tmpObj : objects) {
                    TagNode node = (TagNode) tmpObj;

                    for (TagNode childNode : node.getChildTags()) {
                        String body = childNode.getText().toString();
                        String[] arr = body.split("：");
                        goodsIntroduceDetail.put(arr[0].trim(), arr[1].trim());
                    }
                }
            }

            //解析商品介绍中中的品牌
            String brand = CrawlerUtils.getTagTextValueByAttr(cleaner, page, "//ul[@id='parameter-brand']");
            if (brand != null){
                String[] arr = brand.split("：");
                goodsIntroduceDetail.put(arr[0].trim(), arr[1].trim());
            }


            //商品介绍中其他的参数
            objects = tagNode.evaluateXPath("//ul[@class='parameter2 p-parameter-list']");
            if (objects != null && objects.length > 0) {
                TagNode parentNode = (TagNode) objects[0];
                for (TagNode child : parentNode.getChildTags()) {
                    String[] arrTmp = child.getText().toString().trim().split("：");
                    goodsIntroduceDetail.put(arrTmp[0].trim(), arrTmp[1].trim());
                }
            }

            return goodsIntroduce;
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析产品的规格与包装
     *
     * @param cleaner
     * @param page
     * @return
     */
    public static JSONObject parseGoodsPkg(HtmlCleaner cleaner, Page page) {
        //解析出每一行的参数
        String pkgInfo = CrawlerUtils.getTagTextValueByAttr(cleaner, page, "//li[@clstag='shangpin|keycount|product|pcanshutab']");
        //System.out.println(pkgInfo);
        JSONObject pkgObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        pkgObj.put(pkgInfo, jsonArray);

        TagNode tagNode = cleaner.clean(page.getContent());
        try {
            Object[] objects = tagNode.evaluateXPath("//div[@class='Ptable-item']");
            if (objects != null && objects.length > 0) {
                for (Object objSpecial : objects) {
                    //没循环一次，构建一个JSONObject的实例，将结果填充到Object中即可
                    JSONObject perLineObj = new JSONObject();
                    TagNode tag = (TagNode) objSpecial;
                    String key = tag.getChildTags()[0].getText().toString();
                    JSONObject perLineDetail = new JSONObject();
                    //建立关联关系
                    perLineObj.put(key, perLineDetail);

                    //填充perLineDetail这个JSONObject实例
                    //根据当前的TagNode获得标签，子标签的特性：<dl class="clearfix"
                    Object[] nowTagAllDlTags = tag.evaluateXPath("//dl[@class='clearfix']");

                    for (int i = 0; i < nowTagAllDlTags.length; i++) {
                        TagNode perDl = (TagNode) nowTagAllDlTags[i];
                        TagNode[] childTags = perDl.getChildTags();

                        //每一个详细参数的key，value
                        List<String> container = new LinkedList<>();
                        container.clear();
                        for (int j = 0; j < childTags.length; j++) {
                            TagNode childTag = childTags[j];
                            if (childTag.getChildTags().length == 0) {
                                container.add(childTag.getText().toString());
                            }
                        }

                        //将结果设置进perLineDetail 实例中
                        perLineDetail.put(container.get(0), container.get(container.size() - 1));
                    }
                    //将结果添加到数组中
                    jsonArray.add(perLineObj);
                }
            }
            return pkgObj;

        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 休眠
     */
    public static void sleep(int randomSeconds){
        int random = (int) (Math.random() * randomSeconds +1);
        try {
            Thread.sleep(random * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前的url在Redis共通仓库中是否存在
     * @param url
     * @return
     */
    public static boolean judgeUrlExists(String url) {
        Jedis jedis=null;
        try {
            String commonRepKey = PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY);
            jedis = JedisUtil.getJedis();
            return jedis.sismember(commonRepKey,url);
        } finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 保存所有爬虫尚未处理的url
     * @param url
     */
    public static void saveNowUrl(String url) {
        Jedis jedis = null;
        try {
            String commonRepKey = PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY);
            jedis = JedisUtil.getJedis();
            jedis.sadd(commonRepKey,url);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }
    /**
     * 获取所有的顶级域名
     */
    public static Set<String> getAllTopDemains(){
        //准备一个容器，存储所有的顶级域名
        Set<String> allTopDemains = new LinkedHashSet<>();
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis();
            Set<String> allUrls = jedis.smembers(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY));
            for (String url:allUrls){
                String nowTopDomain = CrawlerUtils.getTopDomain(url);
                allTopDemains.add(nowTopDomain);
            }
            return allTopDemains;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 获取新的需要爬取的种子
     */
    public static Set<String> getAdminNewAddSeedUrls(){
        Jedis jedis = null;
        Set<String> allUrls = null;
        try {
            jedis = JedisUtil.getJedis();
            allUrls = jedis.smembers(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_ADMIN_NEW_ADD_SEED_KEY));
            return allUrls;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 分布式爬虫第一个，需要清空共通url
     */
    public static void clearCommonUrl(){
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis();
            jedis.del(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY));

        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

}
