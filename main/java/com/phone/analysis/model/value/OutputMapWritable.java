package com.phone.analysis.model.value;

import com.phone.common.KpiType;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description reduce阶段输出的value
 * @author: 赵燕钦
 * @create: 2018-12-02 23:40:04
 **/
public class OutputMapWritable extends StatsOutputValue {
    private KpiType kpi;
    private MapWritable value = new MapWritable();

    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        //注意枚举类型的写法
        WritableUtils.writeEnum(dataOutput,kpi);
        this.value.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        WritableUtils.readEnum(dataInput,KpiType.class);
        this.value.readFields(dataInput);
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }

    public MapWritable getValue() {
        return value;
    }

    public void setValue(MapWritable value) {
        this.value = value;
    }
}
