package com.phone.Utils;



import com.phone.common.GlobleConstants;

import java.sql.*;

/**
 * @description 用于连接数据库
 * @author: 赵燕钦
 * @create: 2018-12-03 00:10:51
 **/
public class JdbcUtil {
    //静态加载驱动
    static {
        try {
            Class.forName(GlobleConstants.DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     * @return
     */
    public static Connection getConn(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(GlobleConstants.URL,GlobleConstants.USER
                    ,GlobleConstants.PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭mysql的相关对象
     * @param conn
     * @param ps
     * @param rs
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                //do nothing
            }
        }

        if(ps != null){
            try {
                ps.close();
            } catch (SQLException e) {
                //do nothing
            }
        }

        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                //do nothing
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(JdbcUtil.getConn());
    }
}
