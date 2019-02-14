package com.l000phone.crawler.utils;

import com.l000phone.crawler.constants.CommonConstants;
import com.l000phone.crawler.constants.DeployMode;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbcp.managed.BasicManagedDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Description：对DBCP连接池操作的工具类<br/>
 * Copyright (c) ， 2018， Jansonxu <br/>
 * This program is protected by copyright laws. <br/>
 * Date：2018年12月04日
 *
 * @author 徐文波
 * @version : 1.0
 */
public class DBCPUtils {
    /**
     * 数据源类型的属性 （就是连接池）
     */
    private static DataSource ds;

    /**
     * 操作资源文件的Map集合
     */
    private static Properties properties;

    static {
        properties = new Properties();
        //获得模式名（通过共通的资源文件管理器工具类来获取）
        DeployMode deployMode = PropertiesManagerUtil.mode;

        //        if ("test".equals(deployMode)) {
        //            try {
        //                properties.load(DBCPUtils.class.getClassLoader().getResourceAsStream("test/dbcp-config.properties"));
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        } else if ("dev".equals(deployMode)) {
        //            try {
        //                properties.load(DBCPUtils.class.getClassLoader().getResourceAsStream("dev/dbcp-config.properties"));
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        } else {
        //            try {
        //                properties.load(DBCPUtils.class.getClassLoader().getResourceAsStream("production/dbcp-config.properties"));
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }

        //上述代码显得很臃肿，可以进行如下的优化
        String resourceName = deployMode.toString().toLowerCase() + File.separator + CommonConstants.DBCP_COMMON_FILE_NAME;
        try {
            properties.load(DBCPUtils.class.getClassLoader().getResourceAsStream(resourceName));


            //初始化连接池
            ds = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得连接池的实例
     *
     * @return
     */
    public static DataSource getDataSource() {
        return ds;
    }

    /**
     * 获得连接的实例
     *
     * @return
     */
    public static Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(PropertiesManagerUtil.getPropertyValue(CommonConstants.CONNECTION_FAILURE_MSG));
        }
    }

}
