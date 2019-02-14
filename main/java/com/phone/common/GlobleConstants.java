package com.phone.common;

/**
 * @description
 * @author: 赵燕钦
 * @create: 2018-11-28 19:21:53
 **/
//定义全局常量
public class GlobleConstants {
      //使用Linux主机上的Mysql
//    public static final String DEFAULT_VALUE = "unknwn";
//    public static final String RUNNING_DATE = "running_date";
//    public static final String URL = "jdbc:mysql://Mini1:3306/bigdata";
//    public static final String DRIVER = "com.mysql.jdbc.Driver";
//    public static final String USER = "root";
//    public static final String PASSWORD = "Root123@";

    //使用Windows上的mysql
    public static final String DEFAULT_VALUE = "unknown";
    public static final String RUNNING_DATE = "running_date";
    //判断当前时间的前多长时间(此处单位为毫秒。使用long类型，计算前一天则为 (1 天= 24 * 60 * 60 * 1000（毫秒） )
    public static final long DAY_BEFORE_MILLISECOND= 86400000l;
    public static final String URL = "jdbc:mysql://Murphy:3306/bigdata";//这里的数据库要更换,更换数据库的时候再去更换
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String USER = "root";
    public static final String PASSWORD = "123456";

}
