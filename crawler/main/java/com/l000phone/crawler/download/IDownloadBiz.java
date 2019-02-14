package com.l000phone.crawler.download;

import com.l000phone.crawler.domain.Page;

/**
 * Description：数据下载模块接口<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public interface IDownloadBiz {
    /**
     * 根据指定的URL下载网页资源到内存中，并将页面信息封装到page实体类中
     */
    Page downLoad(String url);
}
