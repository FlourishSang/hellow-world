package com.l000phone.crawler.curator;

import com.l000phone.crawler.utils.MailUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Description：爬虫监控服务类<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月11日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class CuratorService implements Watcher{
    private Logger logger = LoggerFactory.getLogger(CuratorService.class);
    private CuratorFramework client;

    /**
     *容器，用于存储指定znode下所有字znode的名字
     */
    private List<String> initAllZnodes;

    public CuratorService(){
        String zookeeperConnectionString = "hadoop:2181,hadoop02:2181,hadoop03:2181";

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.newClient(zookeeperConnectionString,retryPolicy);
        //注意： 在start方法之后书写具体的操作
        client.start();
        try {
            initAllZnodes = client.getChildren().usingWatcher(this).forPath("/curators");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 当前所监控的父的znode下若是子znode发生了变化：新增，删除，修改
     * 下述方法都会触发执行
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        List<String> newZodeInfos = null;
        try {
            newZodeInfos = client.getChildren().usingWatcher(this).forPath("/curators");
            //概述：根据初始化容器的长度与最新的容器的长度进行比对，就可以推导出当前爬虫集群得状态：新增，宕机，变更
            //思想：那个容器中元素多，就循环遍历哪个容器
            //新增
            if (newZodeInfos.size() > initAllZnodes.size()){
                //明确显示新增了那个节点爬虫
                for (String nowZNode : newZodeInfos){
                    if (! initAllZnodes.contains(nowZNode)){
                        logger.info("新增爬虫节点{}",nowZNode);
                    }
                }
            }else if (newZodeInfos.size()<initAllZnodes.size()){
                //宕机
                //明确显示哪个爬虫节点宕机了
                for (String initZNode: initAllZnodes){
                    if (!newZodeInfos.contains(initZNode)){

                        logger.info("爬虫节点【{}】宕机了哦！要赶紧向运维人员发送E-mail啊！....", initZNode);

                        MailUtil.sendWarningEmail("爬虫宕机警告！", "爬虫节点【" + initZNode + "】宕机了哦！请您赶紧采取应对措施！...", "188437271@qq.com", "茂盛");

                        //分布式爬虫的 HA
                        Process ps = Runtime.getRuntime().exec("/opt/crawler/crawler.sh");
                        ps.waitFor();
                        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
                        String line = null;
                        while ((line = br.readLine() )!= null){
                            logger.info(line);
                        }
                    }
                }
            }else{
                //容器中爬虫的个数未发生变化（不用处理）
                //爬虫集群正常运行
                //宕机了，当时马上重启了，总的爬虫未发生变化
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //要达到每次都是与上一次比较的效果，需要动态替换
        initAllZnodes = newZodeInfos;
    }
    private void start(){
        while (true){

        }
    }

    public static void main(String[] args) {
        //监控服务启动
        new CuratorService().start();
    }
}
