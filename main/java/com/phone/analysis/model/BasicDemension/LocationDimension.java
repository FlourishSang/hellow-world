package com.phone.analysis.model.BasicDemension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @description 地域信息维度表，地域维度id,courtry,province,city
 * @author: 赵燕钦
 * @create: 2018-12-06 18:01:05
 **/
public class LocationDimension extends TopLevelDimension{
    private int id;
    private String country;
    private String province;
    private String city;

    public LocationDimension() {
    }

    public LocationDimension(String country, String province, String city) {
        this.country = country;
        this.province = province;
        this.city = city;
    }

    public LocationDimension(int id, String country, String province, String city) {
        this.id = id;
        this.country = country;
        this.province = province;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    @Override
    public int compareTo(TopLevelDimension o) {
        if (this==o) {
            return 0;
        }
        LocationDimension other = (LocationDimension)o;
        int tmp =this.id-other.id;
        if (tmp ==0){
            return tmp;
        }
        tmp = this.country.compareTo(other.country);
        if (tmp==0){
            return tmp;
        }
        tmp = this.province.compareTo(other.province);
        if (tmp ==0){
            return tmp;
        }
        return this.city.compareTo(other.city);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(country);
        dataOutput.writeUTF(province);
        dataOutput.writeUTF(city);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.id = dataInput.readInt();
        this.country = dataInput.readUTF();
        this.province =dataInput.readUTF();
        this.city = dataInput.readUTF();
    }
}
