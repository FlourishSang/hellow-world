package com.phone.analysis.mapreduce.LocationInfo;

import com.phone.analysis.model.key.StatsLocationDimension;
import com.phone.analysis.model.value.LocationReduceOutputValue;
import com.phone.analysis.model.value.LocationMapOutputValue;
import com.phone.common.KpiType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @description 地域信息reducer
 * @author: 赵燕钦
 * @create: 2018-12-06 22:11:52
 **/
public class LocationInfoReducer extends Reducer<StatsLocationDimension, LocationMapOutputValue, StatsLocationDimension, LocationReduceOutputValue> {

    private static Logger logger = Logger.getLogger(LocationInfoReducer.class);
    LocationReduceOutputValue outputValue = new LocationReduceOutputValue();
    Set set = new HashSet();
    Map<String,Integer> map = new HashMap();
    //map中key为sessionId
    @Override
    protected void reduce(StatsLocationDimension key, Iterable<LocationMapOutputValue> values, Context context) throws IOException, InterruptedException {
        map.clear();
        set.clear();

        for(LocationMapOutputValue tp : values){
            if(StringUtils.isNotEmpty(tp.getUuid().trim())){
                set.add(tp.getUuid());
            }

            if(map.containsKey(tp.getSessionID())){
                map.put(tp.getSessionID(),1);
            }else{
                map.put(tp.getSessionID(),2);
            }
        }

        //跳出会话数计算逻辑
        int mount = 0;
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            if(entry.getValue()==2){
                mount++;
            }
        }
        //输出value赋值，kpi，active_User,sessionId,pop_up_sessions
        outputValue.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
        outputValue.setActive_users(set.size());
        outputValue.setSessions(map.size());
        outputValue.setPop_up_sessions(mount);
        context.write(key,outputValue);

    }


}
