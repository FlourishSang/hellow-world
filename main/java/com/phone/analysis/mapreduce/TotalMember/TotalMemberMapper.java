package com.phone.analysis.mapreduce.TotalMember;

import com.phone.Utils.JdbcUtil;
import com.phone.analysis.model.BasicDemension.BrowserDimension;
import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.analysis.model.BasicDemension.KpiDimension;
import com.phone.analysis.model.BasicDemension.PlatformDimension;
import com.phone.analysis.model.key.StatsCommonDimension;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.TimeOutputValue;
import com.phone.common.DateEnum;
import com.phone.common.GlobleConstants;
import com.phone.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;

/**
 * @description 需求四：新增会员统计类似新增用户统计，也是统计新增u_mid的个数，在新增用户统计中，我们是统计launch事件中，
 *                      uuid的唯一个数，那么在新增会员中，我们是统计"所有事件中"的第一次访问网站的会员id的去重个数(第一次
 *                      访问网站定义为: 在日志收集模块上线后，第一次访问网站的均作为新会员)。
 * @author: 赵燕钦
 * @create: 2018-11-30 17:38:21
 **/
public class TotalMemberMapper extends Mapper<LongWritable, Text,StatsUserDimension, TimeOutputValue> {
    //创建日志类的对象
    private static final Logger logger = Logger.getLogger(TotalMemberMapper.class);
    //创建公共维度类的对象--复制NewUserMapper这里的输出key和value不需要改
    private StatsUserDimension outputKey = new StatsUserDimension();
    private TimeOutputValue outputValue = new TimeOutputValue();

    //KPI需要自己定义
    //获取用户模块下活动用户所有UUID去重后的结果，通过调用枚举类型：指标是自己要定义的，不是从数据中获取的
    private KpiDimension newMemberKpi = new KpiDimension(KpiType.NEW_MEMBER.kpiName);
    //获取浏览器模块下的的活动用户的KPI
    private KpiDimension newBrowserMemberKpi = new KpiDimension(KpiType.BROWSER_NEW_MEMBER.kpiName);

    private Connection conn = null;

    @Override
    //删除一天中的会员量
    //conf.get()返回一个字符串  里面是执行类也是一条sql语句 类似 k，v
    protected void setup(Context context) throws IOException, InterruptedException {
        conn= JdbcUtil.getConn();
        findMember.deleteByDay(context.getConfiguration(),conn);
    }
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //通过配置文件获取当前创建时间created（插入到数据库中）
        Configuration conf = context.getConfiguration();
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
        //所有事件下的会员id的去重个数   后面的这个判断是哪个事件不在需要&& en.equals(Constants.EventEnum.PAGEVIEW.alias)
        if (StringUtils.isNotEmpty(en)){
            //取出我们想要的字段
            String platform = fields[13];
            String serverTime = fields[1];
            String u_mid = fields[4];
            String browserName = fields[24];
            String browserVersion = fields[25];
            String last_visit_date =fields[6];//此处获取的是string类型的日期

            //后边多一条serverTime.equals("null")条件的原因是 数据清洗后的数据有问题，部分数据为空的没有清除掉，所以此处要进行一次判断，否则会报错
            if (StringUtils.isEmpty(serverTime) || StringUtils.isEmpty(u_mid)|| serverTime.equals("null")) {
                logger.info("serverTime或者member不能为空");
                return;
            }

            //之前获取的到String类型的last_visit_date，先转换为long类型的，再转换为Date类型然后传入插入数据的方法中
            long lvd1 = Long.parseLong(last_visit_date);
            Date  lvd2= new Date(lvd1);
            Date member_id_server_time = null;
            //通过RUNNING_DATE获取到当前创建时间
            Date  create_date = Date.valueOf(conf.get(GlobleConstants.RUNNING_DATE));
            //调用自定义的find方法，获取布尔类型的返回值
            Boolean select_member_id_from_mysql = findMember.find(u_mid);
            if (select_member_id_from_mysql==false){//为false时，说明该会员已存在,结束
                return;
            }if (select_member_id_from_mysql==true){//为true时，说明该会员不存在，插入到数据库中
                findMember.insertMember(u_mid,lvd2,member_id_server_time,create_date);
            }



            //构造输出的key
            long time = Long.valueOf(serverTime);
            PlatformDimension pl = PlatformDimension.getInstance(platform);
            DateDimension dateDimension = DateDimension.buildDate(time, DateEnum.DAY);
            //k.getStatsCommonDimension().
            StatsCommonDimension statsCommonDimension = outputKey.getStatsCommonDimension();
            statsCommonDimension.setDt(dateDimension);
            statsCommonDimension.setPl(pl);
            statsCommonDimension.setKpi(newMemberKpi);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            BrowserDimension browserDimension = new BrowserDimension("", "");
            outputKey.setBrowserDimension(browserDimension);

            outputValue.setId(u_mid);
            context.write(outputKey, outputValue);

            //浏览器模块下的赋值
            statsCommonDimension.setKpi(newBrowserMemberKpi);
            browserDimension = new BrowserDimension(browserName, browserVersion);
            outputKey.setBrowserDimension(browserDimension);
            outputKey.setStatsCommonDimension(statsCommonDimension);
            context.write(outputKey, outputValue);
            //输出到reducer的outputKey还是StatsUserDimension,outputValue是一个集合
        }
    }
}


