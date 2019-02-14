package com.phone.analysis.model.value;

import com.phone.common.KpiType;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description reudce端输出
 * @author: 赵燕钦
 * @create: 2018-12-06 22:14:56
 **/
public class LocationReduceOutputValue extends StatsOutputValue{

    private int active_users;//活跃用户数
    private int sessions;//会话个数
    private int pop_up_sessions;//跳出会话数
    private KpiType kpi;

    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeInt(active_users);
        dataOutput.writeInt(sessions);
        dataOutput.writeInt(pop_up_sessions);
        //通过使用枚举写出该Kpi
        WritableUtils.writeEnum(dataOutput,kpi);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.active_users = dataInput.readInt();
        this.sessions = dataInput.readInt();
        this.pop_up_sessions = dataInput.readInt();
        this.kpi = WritableUtils.readEnum(dataInput,KpiType.class);
    }

    public int getActive_users() {
        return active_users;
    }

    public void setActive_users(int active_users) {
        this.active_users = active_users;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public int getPop_up_sessions() {
        return pop_up_sessions;
    }

    public void setPop_up_sessions(int pop_up_sessions) {
        this.pop_up_sessions = pop_up_sessions;
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }
}
