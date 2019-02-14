package com.l000phone.crawler.utils;

import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.constants.DeployMode;

import java.io.IOException;
import java.util.Properties;

/**
 * Description：资源文件操作工具类<br/>
 * Copyright (c) ， 2018， Jansonxu <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 徐文波
 * @version : 1.0
 */
public class PropertiesManagerUtil {
    /**
     * 操作资源文件的容器
     */
    private  static Properties properties;
    public static DeployMode mode;

    static{
        properties = new Properties();
        try {
            properties.load(PropertiesManagerUtil.class.getClassLoader().getResourceAsStream(CommonConstants.COMMON_CONFIG_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mode = DeployMode.valueOf(getPropertyValue(CommonConstants.CRAWLER_JOB_RUN_MODE).toUpperCase());
    }

    /**
     * 获得资源文件中指定的key值
     * @param key
     * @return
     */
    public static String getPropertyValue(String key){
        return properties.getProperty(key);
    }

    /**
     * 将其他资源文件中的数据合并到当前Properties实例中
     */
    public static void loadOtherProperties(Properties otherProperties){
        properties.putAll(otherProperties);
    }
}
