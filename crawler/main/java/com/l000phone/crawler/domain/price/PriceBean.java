package com.l000phone.crawler.domain.price;

import lombok.Data;

/**
 * Description：xxx<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月05日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
@Data
public class PriceBean {
    //{"op":"1399.00","m":"9999.00","id":"J_7479820","p":"1199.00"}
    private String op;
    private String m;
    private String id;
    private String p;

}
