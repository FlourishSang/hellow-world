package com.l000phone.crawler.domain.price;

import lombok.Data;

import java.util.List;

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
public class ProductPrice {

    //[{"op":"1399.00","m":"9999.00","id":"J_7479820","p":"1199.00"}]
    private List<PriceBean> beans;
}
