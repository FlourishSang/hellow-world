package com.l000phone.crawler.schedule;

import com.l000phone.crawler.schedule.job.CrawlerJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;

/**
 * Description：爬虫定时器服务<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月11日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class CrawlerScheduleService {
    public static void main(String[] args){
        //Grep the Scheduler instance from the Factory
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            //and start it off
            scheduler.start();
            String jobName = CrawlerJob.class.getSimpleName();
            String groupName = "crawlerGroup";
            JobDetail jobDetail = new JobDetail(jobName,groupName,CrawlerJob.class);

            // 每天凌晨1点15分url仓库传送一批种子url用作爬虫的下载
            //0 15 10 ? * *"	  *	Fire at 10:15am every day
            //Trigger trigger = new CronTrigger(jobName, groupName, "0 15 01 ? * *");
            Trigger trigger = new CronTrigger(jobName,groupName,"0 14 16 ? * *");
            //指定定时任务
            scheduler.scheduleJob(jobDetail,trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
