package com.l000phone.crawler.utils;

/**
 * Description：实例工厂工具类<br/>
 * Copyright (c) ， 2018， MaoSheng <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月07日
 *
 * @author 桑茂盛
 * @version : 1.0
 */
public class InstanceFactory {
    /**
     * 动态根据获得的接口实现类的权限定名，然后使用反射的机制获取实现类的实例
     */
    public static <T> T getInstance(String key){
        String implFullName = PropertiesManagerUtil.getPropertyValue(key);
        try {
            return (T) Class.forName(implFullName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获得了实现类发生了异常哦！ 异常信息是："+e.getMessage());
        }
    }

}
