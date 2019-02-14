package com.phone.analysis.mapreduce.HourlyActiveUser;

import com.phone.Utils.TimeUtil;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.OutputMapWritable;
import com.phone.analysis.model.value.TimeOutputValue;
import com.phone.common.DateEnum;
import com.phone.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @description 计算指标的reducer
 * @author: 赵燕钦
 * @create: 2018-12-02 23:42:12
 **/
public class HourlyActiveUserReducer extends Reducer<StatsUserDimension, TimeOutputValue,StatsUserDimension, OutputMapWritable> {
    private static final Logger logger = Logger.getLogger(HourlyActiveUserReducer.class);
    private OutputMapWritable outputValue = new OutputMapWritable();
    //我们要求的是按小时分析，所以按小时来写， 第几个小时：对应的活跃用户的数量
    private MapWritable mapWritable = new MapWritable();
    private Map<Integer,Set> map = new HashMap();

    //对map集合初始化，将24个小时全部存进去，这样的在下面用到就可以与之相对应
    //基本写map里面套集合的，基本都在value中for循环赋值  然后遍历集合得到我们想要的值
    // 根据hour值获取set.add用户 最后遍历集合 得到key值对应的set.size  也就是不同小时对应的不同的活跃会员数量
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        for(int i=0;i<24;i++){
            map.put(i,new HashSet());
            mapWritable.put(new IntWritable(i),new IntWritable(0));
        }
    }

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException, IOException {
        for(TimeOutputValue en : values){
            int hour = TimeUtil.getDateInfo(en.getTime(), DateEnum.HOUR);
            map.get(hour).add(en.getId()); //获取hour及其对应的id
        }
        for(Map.Entry<Integer,Set> m : map.entrySet()){
            //这里我们之前初始化过所以里面key存储着0-23 所以在ps赋值的时候我们应该 同样对应0-23
            mapWritable.put(new IntWritable(m.getKey()),new IntWritable(m.getValue().size()));
        }
        outputValue.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
        outputValue.setValue(mapWritable);
        context.write(key,outputValue);
    }
}
