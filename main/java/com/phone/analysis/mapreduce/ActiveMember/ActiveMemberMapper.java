package com.phone.analysis.mapreduce.ActiveMember;

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
 * @description 需求三：计算所有活跃会员：活跃会员(active_member)计算规则：
 *                      计算当天(确定时间维度信息)的pageview事件的数据中memberid的
 *                      去重个数。
 * @author: 赵燕钦
 * @create: 2018-11-30 17:38:21
 **/
public class ActiveMemberMapper extends Mapper<LongWritable, Text,StatsUserDimension, TimeOutputValue> {
    //创建日志类的对象
    private static final Logger logger = Logger.getLogger(ActiveMemberMapper.class);
    //创建公共维度类的对象--复制NewUserMapper这里的输出key和value不需要改
    private StatsUserDimension outputKey = new StatsUserDimension();
    private TimeOutputValue outputValue = new TimeOutputValue();

    //KPI需要自己定义
    //获取用户模块下活动用户所有UUID去重后的结果，通过调用枚举类型：指标是自己要定义的，不是从数据中获取的
    private KpiDimension activeMemberKpi = new KpiDimension(KpiType.ACTIVE_MEMBER.kpiName);
    //获取浏览器模块下的的活动用户的KPI
    private KpiDimension activeBrowserMemberKpi = new KpiDimension(KpiType.BROWSER_ACTIVE_MEMBER.kpiName);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //获取一行文本
        String line = value.toString();
        //判断一行文本是否为空
        if (StringUtils.isEmpty(line)) {
            return;
        }
        //代码走到这说明不为空，拆分清洗后的数据
        String[] fields = line.split("\\u0001");//切分格式etl数据清洗后的自定义格式


        //获取所有事件
        String en = fields[2];
        //判断事件是否为空，并且判断是否是pageview事件
        if (StringUtils.isNotEmpty(en) && en.equals(Constants.EventEnum.PAGEVIEW.alias)) {
            //取出我们想要的字段
            String platform = fields[13];
            String serverTime = fields[1];
            String member_id = fields[4];//计算活跃会员需要对会员id进行分析，会员id在数据库中是第4个字段
            String browserName = fields[24];
            String browserVersion = fields[25];
            //后边多一条的原因数清洗后的数据有问题，部分数据为空的没有清除掉，所以要进行一次判断
            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(member_id)|| serverTime.equals("null")) {
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
            statsCommonDimension.setKpi(activeMemberKpi);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            BrowserDimension browserDimension = new BrowserDimension("", "");
            outputKey.setBrowserDimension(browserDimension);
            outputValue.setId(member_id);
            context.write(outputKey, outputValue);

            //浏览器模块下的赋值
            statsCommonDimension.setKpi(activeBrowserMemberKpi);
            browserDimension = new BrowserDimension(browserName, browserVersion);
            outputKey.setBrowserDimension(browserDimension);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            context.write(outputKey, outputValue);
            //输出到reducer的outputKey还是StatsUserDimension,outputValue是一个集合
        }
    }
}


