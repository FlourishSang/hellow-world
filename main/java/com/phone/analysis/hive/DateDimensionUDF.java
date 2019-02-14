package com.phone.analysis.hive;

import com.phone.Utils.TimeUtil;
import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.mapreduce.services.subClass.IDimensionImpl;
import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.common.DateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;


/**
 * @description 获取时间维度的id
 * @author: 赵燕钦
 * @create: 2018-12-09 14:52:37
 **/
public class DateDimensionUDF extends UDF {
    IDimension iDimension = new IDimensionImpl();

    /*
    *获取时间维度类的id
    * */

    public int evaluate(String date){
        if (StringUtils.isEmpty(date)){
            date = TimeUtil.getYesterday();
        }

        DateDimension dt = DateDimension.buildDate(TimeUtil.parseString2Long(date), DateEnum.DAY);
        int id = iDimension.getIDimensionImplByDimension(dt);
        return id;
    }

    //测试能否根据传入的时间对象获取到时间的id
    public static void main(String[] args) {
        System.out.println(new DateDimensionUDF().evaluate("2018-09-16"));
    }


}
