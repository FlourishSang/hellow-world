package com.phone.analysis.mapreduce.LocationInfoWithNewMember;

import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.analysis.model.BasicDemension.KpiDimension;
import com.phone.analysis.model.BasicDemension.LocationDimension;
import com.phone.analysis.model.BasicDemension.PlatformDimension;
import com.phone.analysis.model.key.StatsCommonDimension;
import com.phone.analysis.model.key.StatsLocationDimension;
import com.phone.analysis.model.value.LocationMapOutputValueForNewMember;
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
public class LocationInfoNmMapper extends Mapper<LongWritable,Text, StatsLocationDimension, LocationMapOutputValueForNewMember> {
    private static Logger logger = Logger.getLogger(LocationNmOutputWriter.class);
    StatsLocationDimension outputKey = new StatsLocationDimension();
    LocationMapOutputValueForNewMember outputValue = new LocationMapOutputValueForNewMember();
    //获取自定义的地域信息的kpi名称
    String kpiName = KpiType.LOCAL_INFO_NEWMEMBER.kpiName;





    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        if (StringUtils.isEmpty(line)) {
            return;
        }
        String[] fields = line.split("\u0001");

        //获取事件
        String en = fields[2];
        if(StringUtils.isNotEmpty(en)&&en.equals(Constants.EventEnum.PAGEVIEW.alias)){
            System.out.println("******************"+en);
            String u_mid = fields[4];
            String serverTime = fields[1];
            String country = fields[28];
            String province = fields[29];
            String city = fields[30];
            String servertime = fields[1];
            String pl_name = fields[13];
            String uuid = fields[3];
            String u_sd = fields[5];

            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(u_mid)|| serverTime.equals("null")) {
                logger.info("serverTime或者member不能为空");
                return;
            }



            Boolean select_member_id_from_mysql = findMembers.find(u_mid);
            System.out.println("**********"+select_member_id_from_mysql);
            if (select_member_id_from_mysql==false){//为false时，说明该会员已存在,结束
                return;
            }
            if (select_member_id_from_mysql==true){//为true时，说明该会员不存在，插入到数据库中
                findMembers.insertMember(u_mid);

            }
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
            outputValue.setU_mid(u_mid);
            context.write(outputKey,outputValue);
        }
    }
}
