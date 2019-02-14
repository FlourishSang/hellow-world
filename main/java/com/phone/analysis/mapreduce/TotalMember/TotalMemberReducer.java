package com.phone.analysis.mapreduce.TotalMember;

import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.OutputMapWritable;
import com.phone.analysis.model.value.TimeOutputValue;
import com.phone.common.KpiType;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * @description 计算指标的reducer
 * @author: 赵燕钦
 * @create: 2018-12-02 23:42:12
 **/
public class TotalMemberReducer extends Reducer<StatsUserDimension, TimeOutputValue,StatsUserDimension, OutputMapWritable> {
//    private static final Logger logger = Logger.getLogger(TotalMemberReducer.class);
//    private OutputMapWritable v = new OutputMapWritable();
//    //用于UUID去重的集合:用于key的去重 ：活动用户就是求得UUID的去重数
//    private Set set = new HashSet<>();
//    //{1:8000}
//    private MapWritable map = new MapWritable();
//
//    @Override
//    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException, IOException {
//        for (TimeOutputValue tv:values) {
//            this.set.add(tv.getId());
//        }
//        //设置KPI
//        this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
//        //通过集合的size设置新增用户的uuid个数
//        this.map.put(new IntWritable(1),new IntWritable(set.size()));
//        v.setValue(map);
//        //此处的write方法调用的是OutputToMySQLFormat中的write方法
//        context.write(key,v);
//        //清空集合
//        this.set.clear();
//    }

    private Logger logger = Logger.getLogger(TotalMemberReducer.class);
    private  OutputMapWritable v = new OutputMapWritable();
    MapWritable map = new MapWritable();
    //k，v 是Id 和  时间集合
    private Map<String, List<Long>> li = new HashMap<String, List<Long>>();
    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException {
        // 将id写入 map集合 map集合key去重
        for(TimeOutputValue t:values){
            if(li.containsKey(t.getId())){
                li.get(t.getId()).add(t.getTime());
            }else{
                List<Long>list = new ArrayList<Long>();
                list.add(t.getTime());
                li.put(t.getId(),list);
            }
            //循环输出  用于插入member_info  用于
            //存储会员信息
            for(Map.Entry<String,List<Long>> en :li.entrySet()){
                v.setKpi(KpiType.MEMBER_INFO);
                this.map.put(new IntWritable(-2),new Text(en.getKey()));
                Collections.sort(en.getValue());
                this.map.put(new IntWritable(-3),new LongWritable(en.getValue().get(0)));
                this.v.setValue(map);
                context.write(key,v);
            }
            this.v.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
            map.put(new IntWritable(1),new IntWritable(li.size()));
            v.setValue(map);
            context.write(key,v);
        }
    }

}
