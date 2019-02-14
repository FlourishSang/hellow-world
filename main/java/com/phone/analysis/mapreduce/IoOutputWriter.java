package com.phone.analysis.mapreduce;

import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;
import com.phone.analysis.model.value.StatsOutputValue;
import org.apache.hadoop.conf.Configuration;

import java.sql.PreparedStatement;

/**
 * @description 操作结果表的一个接口
 * @author: 赵燕钦
 * @create: 2018-12-02 23:50:58
 **/
public interface IoOutputWriter {
    /**
     * 为每一个kpi的最终结果赋值的接口
     */
    void output(Configuration conf,
                TopLevelDimension key,
                StatsOutputValue statsOutputValue,
                PreparedStatement ps,
                IDimension iDimension);
}
