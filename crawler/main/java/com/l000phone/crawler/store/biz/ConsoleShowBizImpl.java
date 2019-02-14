package com.l000phone.crawler.store.biz;

import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.store.IStoreBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description：xxx<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月07日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class ConsoleShowBizImpl implements IStoreBiz{
    private Logger logger = LoggerFactory.getLogger(ConsoleShowBizImpl.class);
    @Override
    public void store(Page page) {
        logger.info("线程名：{}，url：{}，售价(注：列表页面为0.0)：{}", Thread.currentThread().getName(), page.getUrl(), page.getPrice());

        //System.out.println("------------");
        //System.out.println("线程名："+Thread.currentThread().getName()+",   "+page.getUrl() +"  价格为："+page.getPrice());
        //System.out.println("当前页面的url:" + page.getUrl());

        //若当前页面是商品列表页面或者是所有品类的列表页面，显示页面中每个元素的url
        //  List<String> urls = page.getUrls();
        //  if (urls != null && urls.size() > 0) {
        //      //Jdk >= 1.8 函数式编程
        //      urls.forEach(perEle -> System.out.println(perEle));
        //  }
    }
}
