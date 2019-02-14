package com.phone.analysis.hive;

import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.mapreduce.services.subClass.IDimensionImpl;
import com.phone.analysis.model.BasicDemension.EventDimension;
import com.phone.common.GlobleConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @description 获取时间id的UDF
 * @author: 赵燕钦
 * @create: 2018-12-09 15:05:58
 **/
public class EventDimensionUDF extends UDF {
    IDimension iDimension = new IDimensionImpl();
    public int evaluate(String category,String action){
        if (StringUtils.isEmpty(category)){
            category = action = GlobleConstants.DEFAULT_VALUE;
        }
        if (StringUtils.isEmpty(action)){
            action = GlobleConstants.DEFAULT_VALUE;
        }
        int id = -1;
        EventDimension ed = new EventDimension(category,action);

        id = iDimension.getIDimensionImplByDimension(ed);

        return id;
    }
    public static void main(String[] args) {
        System.out.println(new EventDimensionUDF().evaluate("订单事件3","%E4%B8%8B%E5%8D%95%E6%93%8D%E4%BD%9C"));
    }
}
