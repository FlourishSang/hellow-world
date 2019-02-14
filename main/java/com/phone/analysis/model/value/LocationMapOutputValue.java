package com.phone.analysis.model.value;

import com.phone.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description 地域信息的map端输出的value
 * @author: 赵燕钦
 * @create: 2018-12-06 20:14:38
 **/
public class LocationMapOutputValue extends StatsOutputValue{
    private String uuid = "";
    private String sessionID="";


    @Override
    public KpiType getKpi() {
        return null;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeUTF(uuid);
        dataOutput.writeUTF(sessionID);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.uuid = dataInput.readUTF();
        this.sessionID = dataInput.readUTF();
    }

    public LocationMapOutputValue() {
    }

    public LocationMapOutputValue(String uuid, String sessionID) {
        this.uuid = uuid;
        this.sessionID = sessionID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
