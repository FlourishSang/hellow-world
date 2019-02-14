package com.phone.analysis.mapreduce.SessionAnalysis;

import com.phone.analysis.mapreduce.IoOutputWriter;
import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.OutputMapWritable;
import com.phone.analysis.model.value.StatsOutputValue;
import com.phone.common.GlobleConstants;
import com.phone.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @description 用户模块和浏览器模块下的新增用户的输出到表中的实现类
 * @author: 赵燕钦
 * @create: 2018-12-02 23:49:45
 **/
public class SessionOutputWriter implements IoOutputWriter {
    @Override
    public void output(Configuration conf, TopLevelDimension key, StatsOutputValue statsOutputValue, PreparedStatement ps, IDimension iDimension) {
        try {
            StatsUserDimension k= (StatsUserDimension)key;
            OutputMapWritable value = (OutputMapWritable)statsOutputValue;
            //获取活跃用户的值
            int sessions = ((IntWritable) (value).
                    getValue().get(new IntWritable(-1))).get();
            int sessionsLength = ((IntWritable) (value).
                    getValue().get(new IntWritable(-2))).get();
            int i = 0 ;

            //数据库中的插入语句：下列sql语句中的问号接受参数，给问号赋值
//                    <value>insert into `stats_user`(
//                    `date_dimension_id`,
//                    `platform_dimension_id`,
//                    `sessions`,
//                    `sessions_length`,
//                    `created`)
//            values(?,?,?,?,?) on duplicate key update `sessions` = ? ,`sessions_length` = ?
            //++i:代表后面的参数赋值的顺序，给sql语句的问号赋值
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getDt()));//第一个问号：对应字段：时期维度
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getPl()));//第二个问号：对应字段：平台维度
            if(value.getKpi().equals(KpiType.BROWSER_SESSION)){
                ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getBrowserDimension()));//第三个问号：维度id
            }
            //需要在runner类，在运行赋值的时候设置
            ps.setInt(++i,sessions);//第四个问号:指标数字段
            ps.setInt(++i,sessionsLength);//第五个问号：运行时时间字段（表示该指标的计算创建时间）
            ps.setString(++i,conf.get(GlobleConstants.RUNNING_DATE));//第六个问号：指标数
            ps.setInt(++i,sessions);//第七个问号：指标数
            ps.setInt(++i,sessionsLength);//第七个问号：还没求出来？
            ps.addBatch();//添加到批处理中。
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
