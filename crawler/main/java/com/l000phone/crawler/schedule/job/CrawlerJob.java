package com.l000phone.crawler.schedule.job;


import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.repository.IUrlPrePositoryBiz;
import com.l000phone.crawler.utils.CrawlerUtils;
import com.l000phone.crawler.utils.InstanceFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Set;

/**
 * Description：爬虫定时器具体任务类<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月11日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class CrawlerJob implements Job {
    /**
     * url仓库
     */
    private IUrlPrePositoryBiz urlPrepository;
    public CrawlerJob(){
        this.urlPrepository = InstanceFactory.getInstance(CommonConstants.IURLPREPOSITORYBIZ);
    }

    /**
     * 定时器到了指定的时点，下述方法会自动触发执行
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //前提：
        //只要定时器启动了，证明所有的爬虫将上一次的任务都已经执行完毕了，正在空转，此时，需要清空
        //redis中的key common-url，否则，就不能爬取相同的商品了 （否则：即使价格不同，也不能爬取）
        CrawlerUtils.clearCommonUrl();
        //步骤：
        //获得运维人员新增的种子url
        Set<String> allSeeUrls = CrawlerUtils.getAdminNewAddSeedUrls();
        //将所有的新增的种子url添加到url仓库中
        for (String url :allSeeUrls){
            urlPrepository.pushHighter(url);
        }
        //需要清空本次运维人员添加的新的url，（否则，爬虫，一直处理相同的类似的url）
        CrawlerUtils.clearCommonUrl();
    }
}
