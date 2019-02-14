package com.l000phone.crawler.repository.impl;

import com.l000phone.crawler.repository.IUrlPrePositoryBiz;
import com.l000phone.crawler.utils.CrawlerUtils;

import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description：全网爬虫单机演示<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月10日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class RandomPrepositoryAllBizImpl implements IUrlPrePositoryBiz{
    private Map<String,Map<String,ConcurrentLinkedQueue<String>>> repository;
    private Random random;
    public RandomPrepositoryAllBizImpl(){
        repository = new LinkedHashMap<>();
        random = new Random();
    }
    @Override
    public void pushHighter(String url) {
        CommonPush(url,"higher");
    }



    @Override
    public void pushLower(String url) {
        CommonPush(url,"lower");
    }

    @Override
    public void pushOther(String url) {
        CommonPush(url,"other");
    }

    @Override
    public String poll() {
        System.out.println(repository);
        //随机获取一个电商平台，然后从该电商平台获取一个url，进行后续的处理
        //随机获取一个key
        Set<String> allTopDomains = repository.keySet();
        String[] topDomainArr = allTopDomains.toArray(new String[allTopDomains.size()]);
        String randomTopDomain = topDomainArr[random.nextInt(topDomainArr.length)];

        //获得当前随机获取的电商平台的所有的url
        Map<String,ConcurrentLinkedQueue<String>>nowPlatForm = repository.get(randomTopDomain);

        //获得高优先级的对应的容器
        String url = null;
        ConcurrentLinkedQueue<String> higher = nowPlatForm.get("higher");
        if (higher != null){
            url = higher.poll();
        }
        if (url == null){
            ConcurrentLinkedQueue<String> lower = nowPlatForm.get("lower");
            if (lower != null){
                url = lower.poll();
            }
        }
        return url;
    }

    /**
     * 共通push处理
     * @param url
     * @param key
     */
    private void CommonPush(String url, String key) {
        //获得当前url所需的顶级域名
        String topDemain = CrawlerUtils.getTopDomain(url);
        //获得当前顶级域名对应的值：类型是Map<String, ConcurrentLinkedQueue<String>>:存储了一个电商平台所有的结果
        Map<String,ConcurrentLinkedQueue<String>> nowPlaformAllUrls = repository.getOrDefault(topDemain,new LinkedHashMap<>());

        //需要建立当前的电商平台的nowPlaformAllUrls与全网的url仓库repository建立关联关系
        repository.put(topDemain,nowPlaformAllUrls);

        //从Map<String, ConcurrentLinkedQueue<String>>获取高优先级对应的值：类型是 ConcurrentLinkedQueue<String>
        ConcurrentLinkedQueue<String> kindsContainer = nowPlaformAllUrls.getOrDefault(key,new ConcurrentLinkedQueue<>());
        //需要建立当前的高优先级的容器higherContainer与当前电商平台的url容器nowPlaformAllUrls之间的关联关系
        nowPlaformAllUrls.put(key,kindsContainer);

        //将当前的url存入ConcurrentLinkedQueeu<String>中即可
        kindsContainer.add(url);

    }

}
