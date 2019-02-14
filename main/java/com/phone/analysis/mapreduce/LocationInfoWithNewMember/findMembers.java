package com.phone.analysis.mapreduce.LocationInfoWithNewMember;

import com.phone.Utils.JdbcUtil;

import java.sql.*;

/**
 * @description 该类用于判断会员id与数据库中的查询到的会员id进行比对，相同则退出，不相同就是新增会员，直接插入数据库
 * @author: 赵燕钦
 * @create: 2018-12-05 00:39:49
 **/
public class findMembers {

       //可以不这样写，可以直接通过outputwriter将新会员的数据，
      //通过获取配置文件将数据插入到member_info中

    //定义该方法去查找数据库中的会员id
    public static boolean find(String u_mid){

        //获取数据库的连接
        Connection conn = JdbcUtil.getConn();
        PreparedStatement ps = null;//用于获取sql语句
        //定义结果集
        ResultSet set = null;
        Boolean result = null;//定义标志

        try {
            //获取sql语句：该sql语句是根据数据中获取的id查询数据库中的id是否存在，存在不是新增会员，不存在则是新增会员
            ps=conn.prepareStatement("select member_id from `member_info` where member_id = ?");
            //将传入的值赋给查询语句中的第一个问号
            ps.setString(1,u_mid);
            //执行sql语句，将结果放入set集合
            set = ps.executeQuery();
            //遍历结果集 ,如果有值，则说明不是新增会员，是新增会员就把他放入到set集合中
            if (set.next()){//获取到了数据就是已存在的会员，不是新会员
                result = false;
            }
            else{//不存在就说明是新会员
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                //关闭资源
                set.close();
                ps.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return result;

    }

    //将新增会员插入到数据库的方法
    public static void insertMember(String u_mid){

        //首先是获取数据库连接
        Connection conn = JdbcUtil.getConn();
        PreparedStatement ps = null;
        try {
            //获取查询语句
            ps = conn.prepareStatement("insert into `member_info`(\n" +
                    "            `member_id`,\n" +
                    "            `last_visit_date`,\n"+
                    "            `member_id_server_date`,\n"+
                    "            `created`)\n" +
                    "            values(?,?,?,?)");

            //setString中1,2,3,4分别按顺序对应查询语句中的问号
            ps.setString(1,u_mid);
            ps.setDate(2,null);
            ps.setDate(3,null);
            ps.setDate(4,null);

            //执行插入语句
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                ps.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }



}
