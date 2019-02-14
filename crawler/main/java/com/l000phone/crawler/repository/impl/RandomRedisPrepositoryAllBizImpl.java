package com.l000phone.crawler.repository.impl;

import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.repository.IUrlPrePositoryBiz;
import com.l000phone.crawler.utils.CrawlerUtils;
import com.l000phone.crawler.utils.JedisUtil;
import com.l000phone.crawler.utils.PropertiesManagerUtil;
import redis.clients.jedis.Jedis;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Description：全网分布式版爬虫<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月10日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class RandomRedisPrepositoryAllBizImpl implements IUrlPrePositoryBiz{
    private Random random;
    private Set<String> allTopDomains;

    public RandomRedisPrepositoryAllBizImpl(){
        random = new Random();
        allTopDomains = new LinkedHashSet<>();
    }

    @Override
    public void pushHighter(String url) {
        commonPush(url, PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY));
    }

    @Override
    public void pushLower(String url) {
        commonPush(url, PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY));
    }

    @Override
    public void pushOther(String url) {
        commonPush(url, PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_OTHER_KEY));
    }

    @Override
    public String poll() {
        Jedis jedis = JedisUtil.getJedis();
        //思路：
//        //①从共通的url中取出所有的不同的顶级域名，存储到Set容器中
//        Set<String> allTopDomains = CrawlerUtils.getAllTopDomains();
//
//        //拦截非法的操作
//        if (allTopDomains == null || allTopDomains.size() == 0) {
//            //证明是第一次启动,单独存储种子url
//            allTopDomains.add(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_SEED_URL));
//        }
        //随机获取一个顶级域名
        String[] topDemainArr = allTopDomains.toArray(new String[allTopDomains.size()]);
        int index = random.nextInt(topDemainArr.length);
        String randomTopDomain = topDemainArr[index];

        //先从高优先级的key中获取url
        String key = randomTopDomain +"." +PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY);
        String url = jedis.spop(key);

        //若高优先级的key去完了，从低优先级的key中获取
        if (url == null){
            key = randomTopDomain +"." + PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY);
            url = jedis.spop(key);
        }
        jedis.close();
        return url;
    }

    /**
     * 存储url共通处理
     */
    private void commonPush(String url,String nowUrlLevel){
        //获得顶级域名
        String topDemain = CrawlerUtils.getTopDomain(url);

        //将当前处理的dopDomain加到容器中
        allTopDomains.add(topDemain);
        //获得jedis的实例
        Jedis jedis = JedisUtil.getJedis();
        //组织key
        String key = topDemain + "."+nowUrlLevel;
        //存入
        jedis.sadd(key,url);
        //释放资源
        jedis.close();
    }

}
