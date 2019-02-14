package com.phone.analysis.mapreduce.LocationInfo;

import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.analysis.model.BasicDemension.KpiDimension;
import com.phone.analysis.model.BasicDemension.LocationDimension;
import com.phone.analysis.model.BasicDemension.PlatformDimension;
import com.phone.analysis.model.key.StatsCommonDimension;
import com.phone.analysis.model.key.StatsLocationDimension;
import com.phone.analysis.model.value.LocationMapOutputValue;
import com.phone.common.Constants;
import com.phone.common.DateEnum;
import com.phone.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @description 地域信息的mapper类
 * @author: 赵燕钦
 * @create: 2018-12-06 19:37:14
 **/
public class LocationInfoMapper extends Mapper<LongWritable,Text, StatsLocationDimension, LocationMapOutputValue> {
    private static Logger logger = Logger.getLogger(LocationOutputWriter.class);
    StatsLocationDimension outputKey = new StatsLocationDimension();
    LocationMapOutputValue outputValue = new LocationMapOutputValue();
    //获取自定义的地域信息的kpi名称
    String kpiName = KpiType.LOCAL_INFO.kpiName;





    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (StringUtils.isEmpty(line)) {
            return;
        }
        String[] fields = line.split("\u0001");

        //获取事件
        String en = fields[2];
        if(StringUtils.isNotEmpty(en)&&en.equals(Constants.EventEnum.PAGEVIEW.alias)){
            String country = fields[28];
            String province = fields[29];
            String city = fields[30];
            String servertime = fields[1];
            String pl_name = fields[13];
            String uuid = fields[3];
            String u_sd = fields[5];

            long stime = Long.valueOf(servertime);
            StatsCommonDimension statsCommonDimension = new StatsCommonDimension();
            LocationDimension locationDimension =new LocationDimension(country,province,city);
            DateDimension dateDimension = DateDimension.buildDate(stime, DateEnum.DAY);
            PlatformDimension platformDimension = new PlatformDimension(pl_name);
            KpiDimension kpiDimension = new KpiDimension(kpiName);
            statsCommonDimension.setKpi(kpiDimension);
            statsCommonDimension.setPl(platformDimension);
            statsCommonDimension.setDt(dateDimension);
            outputKey.setLocationDimension(locationDimension);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            outputValue.setSessionID(u_sd);
            outputValue.setUuid(uuid);
            context.write(outputKey,outputValue);
        }
    }
}
