package com.l000phone.crawler.repository.impl;

import com.l000phone.crawler.repository.IUrlPrePositoryBiz;

import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description：url仓库模块业务逻辑层接口实现类，单机版爬虫使用链表来存储url<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月08日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class UrlQueuePrepositoryBizImpl implements IUrlPrePositoryBiz{

    private ConcurrentLinkedQueue<String> higherLevel;
    private ConcurrentLinkedQueue<String> lowerLevel;
    private ConcurrentLinkedQueue<String> other;

    public UrlQueuePrepositoryBizImpl(){
        higherLevel = new ConcurrentLinkedQueue<>();
        lowerLevel = new ConcurrentLinkedQueue<>();
        other = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void pushHighter(String url) {
        higherLevel.add(url);
    }

    @Override
    public void pushLower(String url) {
        lowerLevel.add(url);
    }

    @Override
    public void pushOther(String url) {
        other.add(url);
    }

    @Override
    public String poll() {
        //先从高优先级的url仓库中获取一个url
        String url = higherLevel.poll();
        //若不存在，就从低优先级的容器中取出一个url
        if (url == null){
            url = lowerLevel.poll();
        }//从其他other容器中取出其他类别的商品进行解析，此处省略，供下期项目完善时，再来追加该功能
        return url;
    }
}
