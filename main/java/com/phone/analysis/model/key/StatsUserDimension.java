package com.phone.analysis.model.key;

import com.phone.analysis.model.BasicDemension.BrowserDimension;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 用于用户模块和浏览器模块map端和reduce端输出的key
 * @author: 赵燕钦
 * @create: 2018-12-02 22:57:09
 **/

//这里是公共的基础维度类StatsCommonDimension封装（时间，KPI，平台）和浏览器维度类 再次封装
//所有指标都需要的是公共的基础维度类StatsCommonDimension
public class StatsUserDimension extends TopLevelDimension {
    private BrowserDimension browserDimension = new BrowserDimension();
    private StatsCommonDimension statsCommonDimension = new StatsCommonDimension();
    public StatsUserDimension() {
    }

    //名称和版本构造函数
    public StatsUserDimension(BrowserDimension browserDimension, StatsCommonDimension statsCommonDimension) {
        this.browserDimension = browserDimension;
        this.statsCommonDimension = statsCommonDimension;
    }

    @Override
    public int compareTo(TopLevelDimension o) {
        if(this == o){
            return 0;
        }
        StatsUserDimension other = (StatsUserDimension)o;
        int tmp = this.browserDimension.compareTo(other.browserDimension);
        if (tmp != 0){
            return tmp;
        }

        return this.statsCommonDimension.compareTo(other.statsCommonDimension);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.browserDimension.write(dataOutput);
        this.statsCommonDimension.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.browserDimension.readFields(dataInput);
        this.statsCommonDimension.readFields(dataInput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsUserDimension that = (StatsUserDimension) o;
        return Objects.equals(browserDimension, that.browserDimension) &&
                Objects.equals(statsCommonDimension, that.statsCommonDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(browserDimension, statsCommonDimension);
    }

    @Override
    public String toString() {
        return "StatsUserDimension{" +
                "browserDimension=" + browserDimension +
                ", statsCommonDimension=" + statsCommonDimension +
                '}';
    }

    public BrowserDimension getBrowserDimension() {
        return browserDimension;
    }

    public void setBrowserDimension(BrowserDimension browserDimension) {
        this.browserDimension = browserDimension;
    }

    public StatsCommonDimension getStatsCommonDimension() {
        return statsCommonDimension;
    }

    public void setStatsCommonDimension(StatsCommonDimension statsCommonDimension) {
        this.statsCommonDimension = statsCommonDimension;
    }
}
