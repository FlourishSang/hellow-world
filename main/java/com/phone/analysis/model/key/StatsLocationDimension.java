package com.phone.analysis.model.key;

import com.phone.analysis.model.BasicDemension.LocationDimension;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description mapper端的输出key
 * @author: 赵燕钦
 * @create: 2018-12-06 20:04:39
 **/
public class StatsLocationDimension extends TopLevelDimension {
    private LocationDimension locationDimension  = new LocationDimension();
    private StatsCommonDimension statsCommonDimension = new StatsCommonDimension();

    public StatsLocationDimension() {
    }

    public StatsLocationDimension(LocationDimension locationDimension, StatsCommonDimension statsCommonDimension) {
        this.locationDimension = locationDimension;
        this.statsCommonDimension = statsCommonDimension;
    }

    public LocationDimension getLocationDimension() {
        return locationDimension;
    }

    public void setLocationDimension(LocationDimension locationDimension) {
        this.locationDimension = locationDimension;
    }

    public StatsCommonDimension getStatsCommonDimension() {
        return statsCommonDimension;
    }

    public void setStatsCommonDimension(StatsCommonDimension statsCommonDimension) {
        this.statsCommonDimension = statsCommonDimension;
    }

    @Override
    public int compareTo(TopLevelDimension o) {
        if(this == o){
            return 0;
        }
        StatsLocationDimension other = (StatsLocationDimension) o;
        int tmp = this.locationDimension.compareTo(other.locationDimension);
        if (tmp != 0){
            return tmp;
        }
        return this.statsCommonDimension.compareTo(other.statsCommonDimension);

    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        this.locationDimension.write(dataOutput);
        this.statsCommonDimension.write(dataOutput);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.locationDimension.readFields(dataInput);
        this.statsCommonDimension.readFields(dataInput);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
