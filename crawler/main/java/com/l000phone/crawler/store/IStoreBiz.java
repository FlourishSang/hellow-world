package com.l000phone.crawler.store;

import com.l000phone.crawler.domain.Page;

/**
 * Description：xxx<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public interface IStoreBiz {
    /**
     * 将解析后的结果存储起来
     */
    void store(Page page);
}
