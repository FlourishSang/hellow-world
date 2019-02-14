package com.phone.analysis.model.BasicDemension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @description: 指标维度类
 * @author: 赵燕钦
 * @create: 2018-12-02 22:18:27
 **/
public class KpiDimension extends TopLevelDimension {
    //定义KPI的属性 kpi的id和KPI的名称
    private int id;
    private String kpiName;

    //构造函数
    public KpiDimension() {
    }

    public KpiDimension(String kpiName) {
        this.kpiName = kpiName;
    }

    public KpiDimension(int id, String kpiName) {
        this.id = id;
        this.kpiName = kpiName;
    }

    @Override
    public int compareTo(TopLevelDimension o) {
        if (this == o){
            return 0;//返回0代表两个对象相等
        }
        KpiDimension other = (KpiDimension)o;
        int tmp =this.id - other.id;
        //返回值为0
        if(tmp!=0){
            return tmp;
        }
        return this.kpiName.compareTo(other.kpiName);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeInt(id);
        dataOutput.writeUTF(kpiName);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.kpiName = dataInput.readUTF();
    }

    @Override
    public String toString() {
        return "KpiDimension{" +
                "id=" + id +
                ", kpiName='" + kpiName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        //如果两个对象相等返回true
        if (this == o) return true;
        //如果对象不为空，且对象不相等，返回false
        if (o == null || getClass() != o.getClass()) return false;
        //向下转型
        KpiDimension that = (KpiDimension) o;
        return id == that.id &&
                Objects.equals(kpiName, that.kpiName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kpiName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }
}
