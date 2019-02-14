package com.l000phone.crawler.parse.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.parse.IParseBiz;
import com.l000phone.crawler.utils.CrawlerUtils;
import com.l000phone.crawler.utils.HtmlUtils;
import com.l000phone.crawler.utils.PropertiesManagerUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.LinkedList;
import java.util.List;


/**
 * Description：数据解析模块业务逻辑层接口实现类 ，（京东商城<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class JDParseBizImpl implements IParseBiz{
    private Logger logger = LoggerFactory.getLogger(JDParseBizImpl.class);
    @Override
    public void parse(Page page) {
        long startTime = System.currentTimeMillis();
        String urlType ="";
        //判断page的类型
        String url = page.getUrl();
        //html共同解析需要的HrmlCleaner实例
        HtmlCleaner cleaner = new HtmlCleaner();

        //①若是商品页面，解析商品
        if (url.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_URL_PREFIX))){
            urlType = "商品";
            parseSingleGoods(page,cleaner);
        }else if (url.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_LIST_URL_PREFIX))){
            urlType = "列表";
            //若商品列表页面，解析商品列表
            parseGoodsList(page,cleaner);
        }else if(url.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_JD_GOODS_ALL_URL_PREFIX))) { //③若是品类列表页面，解析品类列表 {
            //若是品类列表页面，解析品类列表
            urlType = "所有品类";
            parseAllCategroies(page,cleaner);
        }
        long endTime = System.currentTimeMillis();

        logger.info("页面类型：{}，url：{}，解析时间：{}ms", urlType, url, (endTime - startTime));
    }

    /**
     * 解析品类列表页面，解析品类列表
     * @param page
     * @param cleaner
     */
    private void parseAllCategroies(Page page, HtmlCleaner cleaner) {
        TagNode root = cleaner.clean(page.getContent());
        //下述数组中的每个元素是大品类以及包含的所有小的品类
        try {
            Object[] objects = root.evaluateXPath("//dl[@class='clearfix']");
            if (objects != null && objects.length> 0){
                for (Object obj : objects){
                    TagNode perBigCategory = (TagNode) obj;
                    Object[] nowAllSonCategories = perBigCategory.evaluateXPath("//dd/a");
                    if (nowAllSonCategories != null && nowAllSonCategories.length>0){
                        for (Object objSon : nowAllSonCategories){
                            TagNode sonNode = (TagNode) objSon;
                            String nowSonCatogoryUrl = "https:"+sonNode.getAttributeByName("href");
                            page.getUrls().add(nowSonCatogoryUrl);

                        }
                    }
                }
            }

        } catch (XPatherException e) {
            e.printStackTrace();
        }
    }

    /**
     * 若是商品列表，解析商品列表
     * @param page
     * @param cleaner
     */
    private void parseGoodsList(Page page, HtmlCleaner cleaner) {
        TagNode root = cleaner.clean(page.getContent());

        try {
            Object[] objects = root.evaluateXPath("//*[@id=\"plist\"]/ul/li[*]/div/div[1]/a");
            if (objects != null && objects.length >0){
                for (Object obj : objects){
                    TagNode node = (TagNode) obj;
                    String pageUrl = node.getAttributeByName("href");
                    page.getUrls().add("https:"+pageUrl);
                }
            }
            //将当前列表页面下一页的列表页面的URL也添加到容器中
            String nextPagelistUrl = CrawlerUtils.getTagAttrValueByAttr(cleaner,page,"//a[@class='fp-next']", "href");
            if (nextPagelistUrl != null && !nextPagelistUrl.trim().isEmpty()){
                //System.out.println("nextPagelistUrl为："+nextPagelistUrl);
                page.getUrls().add("https://list.jd.com"+nextPagelistUrl);

            }

        } catch (XPatherException e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析单个商品的
     * @param page
     * @param cleaner
     */
    private void parseSingleGoods(Page page, HtmlCleaner cleaner) {
        //产品的唯一标识，
        page.setId(CrawlerUtils.getUUID());
        //产品编号 string
        page.setGoodsId(CrawlerUtils.getGoodsId(page.getUrl()));
        //来源（商家的网站的顶级域名，如：jd.com，taobao.com）
        page.setSource(CrawlerUtils.getTopDomain(page.getUrl()));

        //url~>String
        //        //页面内容 ~>String （产品url对应的页面的详细内容，别的属性都是来自于该属性）

        //标题~>String
        page.setTitle(CrawlerUtils.getTagTextValueByAttr(cleaner,page,"//div[@class='sku-name']"));

        //图片url ~> String
        String url= CrawlerUtils.getTagAttrValueByAttr(cleaner,page,"//img[@id='spec-img']","data-origin");
        if (url == null ||url.trim().isEmpty()){
            url= CrawlerUtils.getTagAttrValueByAttr(cleaner,page,"//img[@id='spec-img']","src");
            if (url == null ||url.trim().isEmpty()){
                url = CrawlerUtils.getTagAttrValueByAttr(cleaner,page,"//img[@id='spec-img']","jqimg");
            }
        }

        page.setImageUrl("https:"+url);
        //售价 ~>Double
        double price = 0;
        try {
            price = CrawlerUtils.parseProducePrice("https://p.3.cn/prices/mgets?skuIds=J_" + page.getGoodsId());
        } catch (Exception e) {
            //e.printStackTrace();
        }  page.setPrice(price);
        //评论数~>int
        //好评率 ~>Double
        HtmlUtils.getCommentCntAndGoodRate(page);
        //参数 ~>String
        //步骤：
        //准备一个jsonObject的实例，和jsonarray的实例
        JSONObject Obj = new JSONObject();
        JSONArray array = new JSONArray();
        Obj.put("产品参数",array);
        //解析出【商品介绍】
        JSONObject goodIntroduce = CrawlerUtils.parseGoodsIntroduction(cleaner,page);
        //解析出【规格与包装】
        JSONObject pkg = CrawlerUtils.parseGoodsPkg(cleaner,page);

        //将解析后的结果封装到步骤①的jsonobject的实例中
        array.add(goodIntroduce);
        array.add(pkg);
        //将最终的结果设置为Page实例的属性params的值
        page.setParams(Obj.toJSONString());
    }
}
