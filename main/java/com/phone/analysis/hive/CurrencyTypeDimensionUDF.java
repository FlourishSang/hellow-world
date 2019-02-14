package com.phone.analysis.hive;

import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.mapreduce.services.subClass.IDimensionImpl;
import com.phone.analysis.model.BasicDemension.CurrencyTypeDimension;
import com.phone.common.GlobleConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @description 货币类型基础维度类UDF
 * @author: 赵燕钦
 * @create: 2018-12-11 15:17:37
 **/
public class CurrencyTypeDimensionUDF extends UDF {
    IDimension iDimension = new IDimensionImpl();

    public  int evaluate(String currency_name){
        if (StringUtils.isEmpty(currency_name)){
            currency_name = GlobleConstants.DEFAULT_VALUE;
        }

        int id =-1;

        try {
            CurrencyTypeDimension ctd = new CurrencyTypeDimension(currency_name);
            id = iDimension.getIDimensionImplByDimension(ctd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  id;
    }

    public static void main(String[] args) {
        System.out.println(new CurrencyTypeDimensionUDF().evaluate("RMB"));
    }

}
