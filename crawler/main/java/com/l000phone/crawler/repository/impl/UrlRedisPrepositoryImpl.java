package com.l000phone.crawler.repository.impl;

import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.repository.IUrlPrePositoryBiz;
import com.l000phone.crawler.utils.JedisUtil;
import com.l000phone.crawler.utils.PropertiesManagerUtil;
import redis.clients.jedis.Jedis;

/**
 * Description：xxx<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月08日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class UrlRedisPrepositoryImpl implements IUrlPrePositoryBiz {
    @Override
    public void pushHighter(String url) {
        Jedis jedis = JedisUtil.getJedis();
        jedis.sadd(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY),url);
        jedis.close();
    }

    @Override
    public void pushLower(String url) {
        Jedis jedis = JedisUtil.getJedis();
        jedis.sadd(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY),url);
        jedis.close();
    }

    @Override
    public void pushOther(String url) {
        Jedis jedis = JedisUtil.getJedis();
        jedis.sadd(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_OTHER_KEY),url);
        jedis.close();
    }

    @Override
    public String poll() {
        Jedis jedis = JedisUtil.getJedis();

        try {
            String url = jedis.spop(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY));
            if (url == null){
                url = jedis.spop(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY));

            }
            return url;
        } finally {
            jedis.close();
        }
    }
}
