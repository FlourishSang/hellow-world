package com.phone.analysis.mapreduce.NewUser;

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
 * @description 需求一：计算新增用户的mapper类
 * @author: 赵燕钦
 * @create: 2018-11-30 17:38:21
 **/
public class NewUserMapper extends Mapper<LongWritable, Text,StatsUserDimension, TimeOutputValue> {
    //创建日志类的对象
    private static final Logger logger = Logger.getLogger(NewUserMapper.class);
    //创建公共维度类的对象：
    private StatsUserDimension outputKey = new StatsUserDimension();
    private TimeOutputValue outputValue = new TimeOutputValue();

    //获取用户模块下新增用户的kpi，通过调用枚举类型：指标是自己要定义的，不是从数据中获取的
    private KpiDimension newUserKpi = new KpiDimension(KpiType.NEW_USER.kpiName);
    private KpiDimension newBrowserUserKpi = new KpiDimension(KpiType.BROWSER_NEW_USER.kpiName);

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

        //新增用户规则，lantch事件下的uuid的去重个数
        //获取所有事件
        String en = fields[2];
        //判断切分后uuid是否为空，并且判断事件是否为Launch事件，如果是的话就取出我们想要的字段
        if (StringUtils.isNotEmpty(en) && en.equals(Constants.EventEnum.LAUNCH.alias)) {
            //取出我们想要的字段
            String platform = fields[13];
            String serverTime = fields[1];
            String uuid = fields[3];
            String browserName = fields[24];
            String browserVersion = fields[25];
            //后边多一条的原因数清洗后的数据有问题，部分数据为空的没有清除掉，所以要进行一次判断
            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(uuid)) {
                logger.info("serverTime或者uuid不能为空");
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
            statsCommonDimension.setKpi(newUserKpi);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            BrowserDimension browserDimension = new BrowserDimension("", "");
            outputKey.setBrowserDimension(browserDimension);
            outputValue.setId(uuid);
            outputValue.setTime(time);
            context.write(outputKey, outputValue);

            //浏览器模块下的赋值
            statsCommonDimension.setKpi(newBrowserUserKpi);
            browserDimension = new BrowserDimension(browserName, browserVersion);
            outputKey.setBrowserDimension(browserDimension);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            context.write(outputKey, outputValue);
            //输出到reducer的outputKey还是StatsUserDimension,outputValue
        }
    }
}


