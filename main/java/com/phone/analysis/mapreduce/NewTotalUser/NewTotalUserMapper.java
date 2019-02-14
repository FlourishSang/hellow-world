package com.phone.analysis.mapreduce.NewTotalUser;

import com.phone.analysis.model.BasicDemension.BrowserDimension;
import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.analysis.model.BasicDemension.KpiDimension;
import com.phone.analysis.model.BasicDemension.PlatformDimension;
import com.phone.analysis.model.key.StatsCommonDimension;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.TimeOutputValue;
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
 * @description 需求五：total_user计算规则：同一个维度，前一天的总用户+当天新增用户。(只按照天来统计数据，但是提供按照其他时间维度统计的方式)
 *   最终数据保存：stats_user和stats_device_browser。涉及到的列(除了维度列和created列外)：new_install_users和total_install_users。
 *   涉及到其他表有dimension_platform、dimension_date、dimension_browser。
 * @author: 赵燕钦
 * @create: 2018-11-30 17:38:21
 **/
public class NewTotalUserMapper extends Mapper<LongWritable, Text,StatsUserDimension, TimeOutputValue> {
    //创建日志类的对象
    private static final Logger logger = Logger.getLogger(NewTotalUserMapper.class);
    //key,value对象
    private StatsUserDimension outputKey = new StatsUserDimension();
    private TimeOutputValue outputValue = new TimeOutputValue();

    //KPI需要自己定义
    //获取用户模块下活动用户所有UUID去重后的结果，通过调用枚举类型：指标是自己要定义的，不是从数据中获取的
    private KpiDimension newTotalUserKpi = new KpiDimension(KpiType.NEW_TOTAL_USER.kpiName);
    //获取浏览器模块下的的活动用户的KPI
    private KpiDimension newBrowserTotalUserKpi = new KpiDimension(KpiType.BROWSER_NEW_TOTAL_USER.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //获取一行文本
        String line = value.toString();
        //判断一行文本是否为空
        if (StringUtils.isEmpty(line)) {
            return;
        }
        //代码走到这说明不为空，拆分数据
        String[] fields = line.split("\\u0001");//切分格式etl数据清洗后的自定义格式

        //首先是获取所有事件(该字段包含所有时间，该字段对应数组中的位置下标为2)
        String en = fields[2];
        //所有事件下的会员id的去重个数
        if (StringUtils.isNotEmpty(en) && en.equals(Constants.EventEnum.LAUNCH.alias)){
            //取出我们想要的字段
            String platform = fields[13];
            String serverTime = fields[1];
            String uuid = fields[3];//计算新增总用户需要对launch事件下的UUID进行分析，UUID在数据库中是第3个字段
            String browserName = fields[24];
            String browserVersion = fields[25];

            /*
            * 后边多一条serverTime.equals("null")条件的原因是 数据清洗后的数据有问题，部分数据为空的没有清除掉，
            * 所以此处要进行一次判断，否则会报错
            */
            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(uuid)|| serverTime.equals("null")) {
                logger.info("serverTime或者member不能为空");
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
            statsCommonDimension.setKpi(newTotalUserKpi);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            BrowserDimension browserDimension = new BrowserDimension("", "");
            outputKey.setBrowserDimension(browserDimension);

            outputValue.setId(uuid);
            context.write(outputKey, outputValue);

            //浏览器模块下的赋值
            statsCommonDimension.setKpi(newBrowserTotalUserKpi);
            browserDimension = new BrowserDimension(browserName, browserVersion);
            outputKey.setBrowserDimension(browserDimension);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            context.write(outputKey, outputValue);
            //输出到reducer的outputKey还是StatsUserDimension,outputValue是一个集合
        }
    }
}


