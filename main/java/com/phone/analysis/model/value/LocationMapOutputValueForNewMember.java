package com.phone.analysis.model.value;

import com.phone.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description map端输出
 * @author: 赵燕钦
 * @create: 2018-12-07 16:38:16
 **/
public class LocationMapOutputValueForNewMember extends StatsOutputValue{
    private String uuid = "";
    private String sessionID="";
    private String u_mid="";

    public String getU_mid() {
        return u_mid;
    }

    public void setU_mid(String u_mid) {
        this.u_mid = u_mid;
    }

    @Override
    public KpiType getKpi() {
        return null;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeUTF(uuid);
        dataOutput.writeUTF(sessionID);
        dataOutput.writeUTF(u_mid);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.uuid = dataInput.readUTF();
        this.sessionID = dataInput.readUTF();
        this.u_mid = dataInput.readUTF();
    }

    public LocationMapOutputValueForNewMember() {
    }

    public LocationMapOutputValueForNewMember(String uuid, String sessionID, String u_mid) {
        this.uuid = uuid;
        this.sessionID = sessionID;
        this.u_mid = u_mid;
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
