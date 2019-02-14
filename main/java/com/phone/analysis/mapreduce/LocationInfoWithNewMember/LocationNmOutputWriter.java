package com.phone.analysis.mapreduce.LocationInfoWithNewMember;


import com.phone.analysis.mapreduce.IoOutputWriter;
import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;
import com.phone.analysis.model.key.StatsLocationDimension;
import com.phone.analysis.model.value.LocationReduceOutputValueForNewMember;
import com.phone.analysis.model.value.StatsOutputValue;
import com.phone.common.GlobleConstants;
import org.apache.hadoop.conf.Configuration;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @description 地域信息真正输出到sql表的类
 * @author: 赵燕钦
 * @create: 2018-12-06 20:16:06
 **/
public class LocationNmOutputWriter implements IoOutputWriter {

    @Override
    public void output(Configuration conf, TopLevelDimension key, StatsOutputValue statsOutputValue, PreparedStatement ps, IDimension iDimension) {
        StatsLocationDimension outputKey = (StatsLocationDimension)key;
        LocationReduceOutputValueForNewMember outputValue = (LocationReduceOutputValueForNewMember) statsOutputValue;
        //获取活跃用户ID
        int active_Users = outputValue.getActive_users();
        //获取会话数
        int sessions_ = outputValue.getSessions();

        //获取新增会员
        int new_members = outputValue.getNew_members();
        //跳出会话次数
        int pop_up_sessions_ = outputValue.getPop_up_sessions();
        int i=0;
        try {
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(outputKey.getStatsCommonDimension().getDt()));
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(outputKey.getStatsCommonDimension().getPl()));
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(outputKey.getLocationDimension()));
            ps.setInt(++i,active_Users);
            ps.setInt(++i,sessions_);
            ps.setInt(++i,new_members);
            ps.setInt(++i,pop_up_sessions_);
            ps.setString(++i,conf.get(GlobleConstants.RUNNING_DATE));
            ps.setInt(++i,active_Users);
            ps.setInt(++i,sessions_);
            ps.setInt(++i,new_members);
            ps.setInt(++i,pop_up_sessions_);
            System.out.println("*************************");

//            System.out.println(ps.toString());

            ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
