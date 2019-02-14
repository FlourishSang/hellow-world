package com.phone.analysis.hive;

import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.mapreduce.services.subClass.IDimensionImpl;
import com.phone.analysis.model.BasicDemension.PaymentTypeDimension;
import com.phone.common.GlobleConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @description 支付类型UDF函数
 * @author: 赵燕钦
 * @create: 2018-12-11 14:54:42
 **/
public class PaymentDimensionUDF extends UDF {
    IDimension iDimension = new IDimensionImpl();

    public int evaluate(String payment_type) {
        //如果支付方式为空，就给一个默认值
        if (StringUtils.isEmpty(payment_type)) {
            payment_type = GlobleConstants.DEFAULT_VALUE;
        }

        int id = -1;
        try {
            PaymentTypeDimension paymentTypeDimension = new PaymentTypeDimension(payment_type);
            id = iDimension.getIDimensionImplByDimension(paymentTypeDimension);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void main(String[] args) {
        System.out.println(new PaymentDimensionUDF().evaluate("支付宝1111"));
    }
}
