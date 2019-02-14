package com.phone.analysis.mapreduce.SessionAnalysis;

import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.OutputMapWritable;
import com.phone.analysis.model.value.TimeOutputValue;
import com.phone.common.GlobleConstants;
import com.phone.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * @description 计算指标的reducer
 * @author: 赵燕钦
 * @create: 2018-12-02 23:42:12
 **/
public class SessionReducer extends Reducer<StatsUserDimension, TimeOutputValue,StatsUserDimension, OutputMapWritable> {
    private static final Logger logger = Logger.getLogger(SessionReducer.class);
    //创建输出到mysql的自定义输出类，用于调用write方法。。。
    private OutputMapWritable outputValue = new OutputMapWritable();

    private Map<String,List<Long>> map = new HashMap<String, List<Long>>();
    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException, IOException {
        this.map.clear();
        //遍历map端的输出value
        for (TimeOutputValue tv:values) {
            //遍历得到map端输出value中的u_sd,stime
            String sessionId = tv.getId();//map端输出value中的sessionId作为map的key
            long serverTime = tv.getTime();//map端输出的value中的serverTime通过遍历得到，并插入到list集合，作为map集合的value

            //如果该map包含键 sessionId，就通过该key获取该map中的value
            if (map.containsKey(tv.getId())){
                    //map.get(sessionId)： 通过key获取到的value是一个list集合
                   List<Long> list = map.get(sessionId);
                   //将long类型的时间戳插入到list集合中，list集合整体作为map的value存储
                    list.add(serverTime);
                    //u_sd作为key，时间戳组成的list集合作为value输出
                    map.put(sessionId,list);
            }else{//如果该map集合不包含sessionId键值
                List<Long> list = new ArrayList<Long>();
                list.add(serverTime);
                map.put(sessionId,list);
            }
        }

        //构造输出的value
        MapWritable mapWritable = new MapWritable();
        mapWritable.put(new IntWritable(-1),new IntWritable(this.map.size()));

        //定义sessionlength：会话时长 以下代码是会话长度的计算逻辑
        int sessionLength = 0;
        //1.遍历map集合
        for (Map.Entry<String,List<Long>> entry : map.entrySet() ) {
            //2.map的value，是list集合，如果该list集合大于2就进行前后会话时长的加减，该集合长度必须要大于2
            if (entry.getValue().size() >= 2) {
                //3.对list集合进行排序，通过list集合的父接口调用sort方法
                Collections.sort(entry.getValue());//默认是升序

                //此处为map中value处的排序后的list集合中的前后时间戳相减求出时间差加入会话长度sessionLlength
                //计算规则为时：map的value：是一个list集合，通过list集合的长度减一 得到排序后的list集合第1位的数值，减去第0位的数值
                sessionLength += (entry.getValue().get(entry.getValue().size() - 1) - entry.getValue().get(0));
            }
        }
        //将毫秒级的时间戳转换成秒级 装进map中进行输出
        if(sessionLength > 0 && sessionLength <= GlobleConstants.DAY_BEFORE_MILLISECOND){
            //对时间戳的减和进行取余
            if (sessionLength % 1000 ==0){
                sessionLength = sessionLength /10000;
            }else{//不足1秒的算作一秒
                sessionLength = sessionLength /1000 + 1;
            }

//            构造输出value
            /*
            * OutputMapWritable:reduce端输出的value，一个是mapWritable另一个是KPI类型对象
            * */
            mapWritable.put(new IntWritable(-2),new IntWritable(sessionLength));
            this.outputValue.setValue(mapWritable);//给reduce端输出value赋值 value是封装好的map集合
            //设置指标的名称，
            this.outputValue.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
            context.write(key,this.outputValue);
        }
    }
}
