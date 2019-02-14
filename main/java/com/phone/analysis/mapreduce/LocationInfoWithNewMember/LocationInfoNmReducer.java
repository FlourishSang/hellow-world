package com.phone.analysis.mapreduce.LocationInfoWithNewMember;

import com.phone.analysis.model.key.StatsLocationDimension;
import com.phone.analysis.model.value.LocationMapOutputValue;
import com.phone.analysis.model.value.LocationMapOutputValueForNewMember;
import com.phone.analysis.model.value.LocationReduceOutputValueForNewMember;
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
public class LocationInfoNmReducer extends Reducer<StatsLocationDimension, LocationMapOutputValueForNewMember, StatsLocationDimension, LocationReduceOutputValueForNewMember> {

    private static Logger logger = Logger.getLogger(LocationInfoNmReducer.class);
    LocationReduceOutputValueForNewMember outputValue = new LocationReduceOutputValueForNewMember();
    Set set1 = new HashSet();
    Set set2 = new HashSet();
    Map<String,Integer> map = new HashMap();
    //map中key为sessionId


    @Override
    protected void reduce(StatsLocationDimension key, Iterable<LocationMapOutputValueForNewMember> values, Context context) throws IOException, InterruptedException {
        map.clear();
        set1.clear();
        set2.clear();

        //活跃用户计算逻辑
        for(LocationMapOutputValueForNewMember tp : values) {
            if (StringUtils.isNotEmpty(tp.getUuid().trim())) {
                set1.add(tp.getUuid());
            }

            if (StringUtils.isNotEmpty(tp.getU_mid().trim())) {
                set2.add(tp.getU_mid());
            }

            if (StringUtils.isNotEmpty(tp.getSessionID().trim())) {
                if (map.containsKey(tp.getSessionID())) {
                    this.map.put(tp.getSessionID(), 2);
                } else {
                    this.map.put(tp.getSessionID(), 1);
                }
            }
        }


        int mount = 0;
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            if(entry.getValue()==1){
                mount++;
            }
        }


        //输出value赋值，kpi，active_User,sessionId,pop_up_sessions
        outputValue.setKpi(KpiType.valueOfKpiName(key.getStatsCommonDimension().getKpi().getKpiName()));
        outputValue.setActive_users(set1.size());
        outputValue.setSessions(map.size());
        outputValue.setPop_up_sessions(mount);
        outputValue.setNew_members(set2.size());
        context.write(key,outputValue);

    }
}
