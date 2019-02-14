package com.phone.analysis.model.key;

import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.analysis.model.BasicDemension.KpiDimension;
import com.phone.analysis.model.BasicDemension.PlatformDimension;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 将公共的基础维度类封装起来
 * @author: 赵燕钦
 * @create: 2018-12-02 22:58:27
 **/
public class StatsCommonDimension extends TopLevelDimension {

    //创建基础维度类的对象，用于封装（维度类的公共部分）
    public PlatformDimension pl = new PlatformDimension();
    public DateDimension dt = new DateDimension();
    public KpiDimension kpi = new KpiDimension();

    public StatsCommonDimension() {
    }

    public StatsCommonDimension(PlatformDimension pl, KpiDimension kpi) {
        this.pl = pl;
        this.kpi = kpi;
    }

    @Override
    public int compareTo(TopLevelDimension o) {
        if (this ==o){
            return 0;
        }
        StatsCommonDimension other = (StatsCommonDimension)o;
        int tmp = this.pl.compareTo(pl);
        if (tmp!=0){
            return tmp;
        }
        tmp = this.dt.compareTo(other.dt);
        if(tmp != 0){
            return tmp;
        }
        return this.kpi.compareTo(other.kpi);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        this.pl.write(dataOutput);
        this.dt.write(dataOutput);
        this.kpi.write(dataOutput);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.pl.readFields(dataInput);
        this.dt.readFields(dataInput);
        this.kpi.readFields(dataInput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsCommonDimension that = (StatsCommonDimension) o;
        return Objects.equals(pl, that.pl) &&
                Objects.equals(dt, that.dt) &&
                Objects.equals(kpi, that.kpi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pl, dt, kpi);
    }

    @Override
    public String toString() {
        return "StatsCommonDimension{" +
                "pl=" + pl +
                ", dt=" + dt +
                ", kpi=" + kpi +
                '}';
    }

    public PlatformDimension getPl() {
        return pl;
    }

    public void setPl(PlatformDimension pl) {
        this.pl = pl;
    }

    public DateDimension getDt() {
        return dt;
    }

    public void setDt(DateDimension dt) {
        this.dt = dt;
    }

    public KpiDimension getKpi() {
        return kpi;
    }

    public void setKpi(KpiDimension kpi) {
        this.kpi = kpi;
    }
}
