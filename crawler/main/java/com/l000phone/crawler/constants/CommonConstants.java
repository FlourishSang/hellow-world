package com.l000phone.crawler.constants;

/**
 * Description：共通的常量<br/>
 * Copyright (c) ， 2018， Jansonxu <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 徐文波
 * @version : 1.0
 */
public interface CommonConstants {
    /**
     * 运行的模式
     */
    String CRAWLER_JOB_RUN_MODE="crawler.job.run.mode";
    /**
     * dbcp连接池共通的资源文件
     */
    String DBCP_COMMON_FILE_NAME = "dbcp-config.properties";
    /**
     * 连接的实例获取失败时的提示信息
     */
    String CONNECTION_FAILURE_MSG = "connection.failure.msg";

    /**
     * 共通配置信息资源文件名
     */
    String COMMON_CONFIG_FILE_NAME = "conf.properties";

    /**
     * 种子URL
     */
    String CRAWLER_SEED_URL = "crawler.seed.url";

    /**
     * 商品url的前缀
     */
    String CRAWLER_GOODS_URL_PREFIX = "crawler.goods.url.prefix";

    /**
     * 商品列表url的前缀
     */
    String CRAWLER_GOODS_LIST_URL_PREFIX = "crawler.goods.list.url.prefix";
    /**
     * 全部商品品类url的前缀
     */
    String CRAWLER_JD_GOODS_ALL_URL_PREFIX = "crawler.jd.goods.all.url.prefix";

    /**
     * 准备接口名
     */
    String IDOWLOADBIZ = "IDowloadBiz";
    String IPARSEBIZ = "IParseBiz";
    String ISTOREBIZ = "IStoreBiz";
    String IURLPREPOSITORYBIZ = "IUrlPrepositoryBiz";

    /**
     * redis 存储不同优先级的url对应的key
     */
    String CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY = "crawler.url.redis.repository.higher.key";
    String CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY = "crawler.url.redis.repository.lower.key";
    String CRAWLER_URL_REDIS_REPOSITORY_OTHER_KEY = "crawler.url.redis.repository.other.key";
    String CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY = "crawler.url.redis.repository.common.key";

    /**
     * redis相应的配置参数的key
     */
    String CRAWLER_REDIS_MAX_IDLE = "crawler.redis.maxIdle";
    String CRAWLER_REDIS_MAX_TOTAL = "crawler.redis.maxTotal";
    String CRAWLER_REDIS_MAX_WAIT_MILLIS = "crawler.redis.maxWaitMillis";
    String CRAWLER_REDIS_HOST = "crawler.redis.host";
    String CRAWLER_REDIS_PORT ="crawler.redis.port" ;
    String CRAWLER_REDIS_TIMEOUT ="crawler.redis.timeout" ;

    /**
     * 共通url清空flg
     */
    String CRAWLER_URL_CLEAR_FIRST_FLG = "crawler.url.clear.first.flg";
    String CRAWLER_URL_CLEAR_OTHER_FLG = " crawler.url.clear.other.flg";

    /**
     * 运维人员新增的种子url
     */
    String CRAWLER_ADMIN_NEW_ADD_SEED_KEY="crawler.admin.new.add.seed.key";
}
