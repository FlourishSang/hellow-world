package com.l000phone.crawler.store.biz;

import com.l000phone.crawler.dao.IPageDao;
import com.l000phone.crawler.dao.impl.DefaultPageDaoImpl;
import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.store.IStoreBiz;

/**
 * Description：xxx<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月07日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class RDBMSStoreBizImpl implements IStoreBiz {
    @Override
    public void store(Page page) {

        IPageDao dao = new DefaultPageDaoImpl();
        dao.save(page);
    }
}
