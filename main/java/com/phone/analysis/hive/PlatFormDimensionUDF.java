package com.phone.analysis.hive;

import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.mapreduce.services.subClass.IDimensionImpl;
import com.phone.analysis.model.BasicDemension.PlatformDimension;
import com.phone.common.GlobleConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @description 平台维度类的UDF
 * @author: 赵燕钦
 * @create: 2018-12-09 14:37:10
 **/
public class PlatFormDimensionUDF extends UDF {
    //多态，父类引用指向子类对象，向上转型
    IDimension iDimension = new IDimensionImpl();
    /*
    * 平台维度的id
    * */
    public int evaluate(String platform){
        if (StringUtils.isEmpty(platform)){
            platform = GlobleConstants.DEFAULT_VALUE;
        }
        int id = -1;

        //创建平台维度类的对象，并将传入的平台名称传入
        try {
            PlatformDimension pl = new PlatformDimension(platform);
            id = iDimension.getIDimensionImplByDimension(pl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }


//    通过编写main方法进行测试，是否能获取到id
    public static void main(String[] args) {
        System.out.println(new PlatFormDimensionUDF().evaluate("midasske"));
    }
}
