package com.phone.analysis.mapreduce.HourlyActiveUser;

import com.phone.analysis.mapreduce.IoOutputWriter;
import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.OutputMapWritable;
import com.phone.analysis.model.value.StatsOutputValue;
import com.phone.common.GlobleConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;

/**
 * @description 用户模块和浏览器模块下的新增用户的输出到表中的实现类
 * @author: 赵燕钦
 * @create: 2018-12-02 23:49:45
 **/
public class HourlyActiveUserOutputWriter implements IoOutputWriter {
    private static final Logger logger = Logger.getLogger(HourlyActiveUserOutputWriter.class);
    @Override
    public void output(Configuration conf, TopLevelDimension key, StatsOutputValue statsOutputValue, PreparedStatement ps, IDimension iDimension) {
        StatsUserDimension outputKey = (StatsUserDimension) key;
        OutputMapWritable outputValue = (OutputMapWritable) statsOutputValue;
        try {
            int i = 0;
            ps.setInt(++i, iDimension.getIDimensionImplByDimension(outputKey.getStatsCommonDimension().getDt()));
            ps.setInt(++i, iDimension.getIDimensionImplByDimension(outputKey.getStatsCommonDimension().getPl()));
            ps.setInt(++i, iDimension.getIDimensionImplByDimension(outputKey.getStatsCommonDimension().getKpi()));
            //在reduce端 我们初始化了key 0-23 相对应的是0-23小时的set.size 用户个数
            for (int j = 0; j < 24; j++) {
                ps.setInt(++i,((IntWritable)(outputValue.getValue().get(new IntWritable(j)))).get());
            }
            ps.setString(++i, conf.get(GlobleConstants.RUNNING_DATE));
            for (int j = 0; j < 24; j++) {
                ps.setInt(++i, ((IntWritable) (outputValue.getValue().get(new IntWritable(j)))).get());
            }
//            System.out.println(ps.toString());
            ps.addBatch();
        }catch (Exception e){
            logger.warn("ps赋值失败",e);
        }
    }
}
