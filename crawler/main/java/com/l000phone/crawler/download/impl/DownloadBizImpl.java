package com.l000phone.crawler.download.impl;

import com.l000phone.crawler.domain.Page;
import com.l000phone.crawler.download.IDownloadBiz;
import com.l000phone.crawler.utils.HtmlUtils;

/**
 * Description：数据下载模块接口实现类<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class DownloadBizImpl implements IDownloadBiz{
    @Override
    public Page downLoad(String url) {
        Page page = new Page();
        String content = HtmlUtils.downloadPageContentToMemory(url);
        page.setContent(content);
        page.setUrl(url);
        return page;
    }
}
