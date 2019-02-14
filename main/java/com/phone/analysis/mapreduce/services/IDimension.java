package com.phone.analysis.mapreduce.services;

import com.phone.analysis.model.BasicDemension.TopLevelDimension;

/**
 * @description 根据传入的维度对象获取维度id
 * @author: 赵燕钦
 * @create: 2018-12-02 23:52:27
 **/
public interface IDimension {
    //根据传入的维度对象获取维度id
    int getIDimensionImplByDimension(TopLevelDimension baseDimension);
}
