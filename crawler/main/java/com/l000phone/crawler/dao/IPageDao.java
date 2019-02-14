package com.l000phone.crawler.dao;

import com.l000phone.crawler.domain.Page;

/**
 * Description：将解析后的页面数据保存到db中数据访问层接口<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public interface IPageDao {
    /**
     * 将解析后的产品信息保存到 db中（MySQL。HBASE，es中）
     */
    void save(Page page );
}
