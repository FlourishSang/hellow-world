package com.l000phone.crawler.repository;

/**
 * Description：URL仓库模块业务逻辑层接口<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月08日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public interface IUrlPrePositoryBiz {
    /**
     * 向高优先级的容器中存放URL，存放商品列表页面的url
     */
    void pushHighter(String url);

    /**
     * 向低优先级的容器中存放url，存放商品页面的url
     */
    void pushLower(String url);

    /**
     * 向其他的url放入仓库中，后续完善的阶段需要将other中存放的url再进行细分
     */
    void pushOther(String url);

    /**
     * 从容器中获取url
     */
    String poll();

}
