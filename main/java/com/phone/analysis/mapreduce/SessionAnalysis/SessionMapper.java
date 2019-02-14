package com.phone.analysis.mapreduce.SessionAnalysis;

import com.phone.analysis.model.BasicDemension.BrowserDimension;
import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.analysis.model.BasicDemension.KpiDimension;
import com.phone.analysis.model.BasicDemension.PlatformDimension;
import com.phone.analysis.model.key.StatsCommonDimension;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.TimeOutputValue;
import com.phone.common.DateEnum;
import com.phone.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @description 需求二：计算所有活跃用户：当天所有数据(事件)中，UUID的去重个数，只要上线就是活跃用户
 * @author: 赵燕钦
 * @create: 2018-11-30 17:38:21
 **/
public class SessionMapper extends Mapper<LongWritable, Text,StatsUserDimension, TimeOutputValue> {
    //创建日志类的对象
    private static final Logger logger = Logger.getLogger(SessionMapper.class);
    //创建公共维度类的对象--复制NewUserMapper这里的输出key和value不需要改
    private StatsUserDimension outputKey = new StatsUserDimension();
    private TimeOutputValue outputValue = new TimeOutputValue();

    //KPI需要自己定义
    //获取用户模块下活动用户所有UUID去重后的结果，通过调用枚举类型：指标是自己要定义的，不是从数据中获取的
    private KpiDimension sessionKpi = new KpiDimension(KpiType.SESSION.kpiName);
    //获取浏览器模块下的的活动用户的KPI
    private KpiDimension browserSessionKpi = new KpiDimension(KpiType.BROWSER_SESSION.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //获取一行文本
        String line = value.toString();
        //判断一行文本是否为空
        if (StringUtils.isEmpty(line)){
            return;
        }
        //代码走到这说明不为空，拆分数据
        String[] fields = line.split("\\u0001");//切分格式etl数据清洗后的自定义格式

//        String en = fields[2];
//        if (StringUtils.isNotEmpty(en) && en.equals(Constants.EventEnum.LAUNCH.alias)) {
          //获取想要的字段
           String  platform = fields[13];
            String serverTime = fields[1];
            String u_sd = fields[5];//sessionId
            String browserName = fields[24];
            String browserVersion = fields[25];
            //后边多一条的原因数清洗后的数据有问题，部分数据为空的没有清除掉，所以要进行一次判断
            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(u_sd)|| serverTime.equals("null") || u_sd.equals("null")) {
                logger.info("serverTime或者u_sd不能为空");
                return;
            }

            //构造输出的key
            long time = Long.valueOf(serverTime);
            PlatformDimension pl = PlatformDimension.getInstance(platform);
            DateDimension dateDimension = DateDimension.buildDate(time, DateEnum.DAY);
            //k.getStatsCommonDimension().
            StatsCommonDimension statsCommonDimension = outputKey.getStatsCommonDimension();
            statsCommonDimension.setDt(dateDimension);
            statsCommonDimension.setPl(pl);
            statsCommonDimension.setKpi(sessionKpi);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            BrowserDimension browserDimension = new BrowserDimension("", "");
            outputKey.setBrowserDimension(browserDimension);
            outputValue.setId(u_sd);
            outputValue.setTime(time);//之前这里没写出了，出了bug，导致reduce端输出没有值
            context.write(outputKey, outputValue);

            //浏览器模块下的赋值
            statsCommonDimension.setKpi(browserSessionKpi);
            browserDimension = new BrowserDimension(browserName, browserVersion);
            outputKey.setBrowserDimension(browserDimension);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            context.write(outputKey, outputValue);
            //输出到reducer的outputKey还是StatsUserDimension,outputValue是一个集合
//        }
    }
}


