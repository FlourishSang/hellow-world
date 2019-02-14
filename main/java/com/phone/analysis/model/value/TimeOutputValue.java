package com.phone.analysis.model.value;

import com.phone.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description mapper端输出value 有的是整型，有的是string类型，所以此处将这两个属性封装起来。用到哪个就调用哪个
 * @author: 赵燕钦
 * @create: 2018-12-02 23:08:29
 **/
public class TimeOutputValue extends StatsOutputValue {
    private String id;//泛指，可以是uuid,可以是u_mid,可以是s_id,或者是u_sd
    private long time;//时间戳，时长...

    @Override
    public KpiType getKpi() {
        return null;
    }

    @Override
    public void write(DataOutput dataOutput) throws  IOException {
        dataOutput.writeUTF(this.id);
        dataOutput.writeLong(this.time);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readUTF();
        this.time = dataInput.readLong();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
