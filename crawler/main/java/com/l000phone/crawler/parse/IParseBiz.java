package com.l000phone.crawler.parse;

import com.l000phone.crawler.domain.Page;

/**
 * Description：数据解析业务逻辑层<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public interface IParseBiz {
    /**
     * 对页面资源进行解析（爬虫的重难点！ Cleaner+Path）
     */
    void parse(Page page);
}
