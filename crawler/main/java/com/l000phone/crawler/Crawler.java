package com.l000phone.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.domain.Page;

import com.l000phone.crawler.download.IDownloadBiz;
import com.l000phone.crawler.download.impl.DownloadBizImpl;
import com.l000phone.crawler.parse.IParseBiz;
import com.l000phone.crawler.parse.impl.JDParseBizImpl;
import com.l000phone.crawler.repository.IUrlPrePositoryBiz;
import com.l000phone.crawler.store.IStoreBiz;
import com.l000phone.crawler.store.biz.ConsoleShowBizImpl;
import com.l000phone.crawler.store.biz.RDBMSStoreBizImpl;
import com.l000phone.crawler.utils.CrawlerUtils;
import com.l000phone.crawler.utils.HtmlUtils;
import com.l000phone.crawler.utils.InstanceFactory;
import com.l000phone.crawler.utils.PropertiesManagerUtil;
import com.sun.rmi.rmid.ExecPermission;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.htmlcleaner.HtmlCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Description：爬虫项目入口类（包含main方法）<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class Crawler {
    private Logger logger = LoggerFactory.getLogger(Crawler.class);

    private IDownloadBiz downloadBiz;
    private IParseBiz parseBiz;
    private IStoreBiz storeBiz;


    /**
     * URL仓库
     */
    private IUrlPrePositoryBiz urlPrepository;

    public Crawler() {

    }

    public Crawler(IDownloadBiz downloadBiz, IParseBiz parseBiz, IStoreBiz storeBiz) {
        this.downloadBiz = downloadBiz;
        this.parseBiz = parseBiz;
        this.storeBiz = storeBiz;


        //初始化到容器
        urlPrepository = InstanceFactory.getInstance(CommonConstants.IURLPREPOSITORYBIZ);
        //添加种子URL
        String seedUrl = PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_SEED_URL);
        urlPrepository.pushHighter(seedUrl);

    }

    /**
     * 根据指定的URL下载网页资源到内存中，并将页面信息封装到Page实体类中
     */
    public Page dowload(String url) {
        return downloadBiz.downLoad(url);
    }

    /**
     * 对页面资源进行解析，
     */
    public void parse(Page page) {
        parseBiz.parse(page);
    }

    /**
     * 将解析后的结果存储起来
     */
    public void store(Page page) {
        storeBiz.store(page);

    }

    /**
     * 启动爬虫
     */
    private void start() {
        //爬虫自检
        checkSelf();

        //注册到zookeeper中
        register2ZK();

        //准备一个线程池的实例
        ExecutorService threadPool = Executors.newFixedThreadPool(8);

        while (true) {
            //从URL仓库中获取一个url
            String url = urlPrepository.poll();

            if (url != null) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        crawling(url);
                    }
                });
            } else {

                logger.info("暂时没有新的url可供爬取哦！稍等。。。。");
                //动态休息1~2秒钟
                CrawlerUtils.sleep(2);
            }
        }
    }

    /**
     * 将当前爬虫注册到zookeeper指定的目录curators之下
     */
    private void register2ZK() {
        String zookeeperConnectionString = "hadoop:2181,hadoop02:2181,hadoop03:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString,retryPolicy);
        client.start();
        String ipAddr = null;
        try {
            ipAddr = InetAddress.getLocalHost().getHostAddress()+"-"+System.currentTimeMillis();

        client.create().withMode(CreateMode.EPHEMERAL).forPath("/curators/"+ipAddr,"爬虫进程对应的zNode".getBytes());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 爬虫进行自检（就是对爬虫各个组件（或是模板）进行检测
     */
    private void checkSelf() {
        logger.info("===============================================↓  爬虫自检开始  ↓===============================================");

        //url仓库实现类检测
        commonCheckDealWith(urlPrepository, "url仓库实现类实例未注入！", "url仓库尚未初始化，爬虫终止运行！", "url仓库实现类实例正常注入！。。。");

        //下载自检
        commonCheckDealWith(downloadBiz, "Download实现类实例未注入！", "Download尚未初始化，爬虫终止运行！", "Download实现类实例正常注入！。。。");

        //解析自检
        commonCheckDealWith(parseBiz, "Parser实现类实例未注入！", "Parser未初始化，爬虫终止运行！", "Parser实现类实例正常注入！。。。");

        //存储自检
        commonCheckDealWith(storeBiz, "Store实现类实例未注入！", "Store未初始化，爬虫终止运行！", "Store实现类实例正常注入！。。。");

        logger.info("===============================================↑  爬虫自检结束  ↑===============================================");
    }
    /**
     * 共通的自检处理
     */
    private <T> void commonCheckDealWith(T component,String errorMsg,String throwableMsg,String normalMsg){
        if (component == null){
            logger.error(errorMsg);
            throw new ExceptionInInitializerError(throwableMsg);
        }else{
            logger.info(normalMsg);
        }
    }

    /**
     * 爬虫正在爬取操作
     * @param url
     */
    private void crawling(String url) {
        //判断该url是否被处理过
        if (url != null && !url.trim().isEmpty()){
            if (CrawlerUtils.judgeUrlExists(url)){
                return;
            }else{
                CrawlerUtils.saveNowUrl(url);
            }
        }
        //下载
        Page page = dowload(url);
        //解析
        parse(page);
        //判断，若当前的页面是列表页面的话，将当前列表页面中所有的商品的URL以及下一页的列表的页面的URL添加URL仓库中
        List<String> urls = page.getUrls();
        //①若是商品页面，解析商品
        if (urls != null && urls.size()>0){
            //通过循环判断容器中每个url的类型，然后添加到不同的容器中
            for (String urlTmp :urls){
                //若是高优先级的url添加到高优先级的容器中
                //若商品列表页面
                if (urlTmp.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_URL_PREFIX))){
                    urlPrepository.pushLower(urlTmp);
                }else if (urlTmp.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_LIST_URL_PREFIX))){
                    //若是商品列表页面
                    urlPrepository.pushHighter(urlTmp);
                }else {
                    urlPrepository.pushOther(urlTmp);
                }
            }
        }
        //存储
        store(page);
        //动态休息1-2秒钟
        CrawlerUtils.sleep(2);
    }

    /**
     * 入口方法
     */
    public static void main(String[] args) {
        //清空功能common-url
        //若标志值是0，进行清空操作
        if(args != null && PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_CLEAR_FIRST_FLG).equalsIgnoreCase(args[0].trim())){
            CrawlerUtils.clearCommonUrl();
        }
        //启动爬虫
        IDownloadBiz downloadBiz = InstanceFactory.getInstance(CommonConstants.IDOWLOADBIZ);
        IParseBiz parseBiz = InstanceFactory.getInstance(CommonConstants.IPARSEBIZ);
        IStoreBiz storeBiz = InstanceFactory.getInstance(CommonConstants.ISTOREBIZ);

        new Crawler(downloadBiz,
                parseBiz,
                storeBiz).start();
    }
}
