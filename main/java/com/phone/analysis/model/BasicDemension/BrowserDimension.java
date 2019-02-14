package com.phone.analysis.model.BasicDemension;




import com.phone.common.GlobleConstants;
import org.apache.commons.lang.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 浏览器维度类
 * @author: 赵燕钦
 * @create: 2018-12-02 22:31:00
 **/
public class BrowserDimension extends TopLevelDimension {
    //定义浏览器属性id，browserName，browserVersion
    private int id;
    private String browserName;
    private String browserVersion;


    public BrowserDimension() {
    }

    public BrowserDimension(String browserName, String browserVersion) {
        this.browserName = browserName;
        this.browserVersion = browserVersion;
    }

    public BrowserDimension(int id, String browserName, String browserVersion) {
        this.id = id;
        this.browserName = browserName;
        this.browserVersion = browserVersion;
    }

    //get,set方法赋值和获取值
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowserDimension that = (BrowserDimension) o;
        return id == that.id &&
                Objects.equals(browserName, that.browserName) &&
                Objects.equals(browserVersion, that.browserVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, browserName, browserVersion);
    }

    @Override
    public String toString() {
        return "BrowserDimension{" +
                "id=" + id +
                ", browserName='" + browserName + '\'' +
                ", browserVersion='" + browserVersion + '\'' +
                '}';
    }

    //比较对象就是比较对象的属性
    @Override
    public int compareTo(TopLevelDimension o) {
        if (this == o) {
            return 0;//返回结果为0表示两个对象相等
        }
        //int类型的属性直接相减，String类型的调用compareTo方法进行比较
        BrowserDimension other = (BrowserDimension) o;
        int tmp = this.id - other.id;
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.browserName.compareTo(other.browserName);
        if (tmp != 0) {
            return tmp;
        }
        return this.browserVersion.compareTo(other.browserVersion);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeInt(id);
        dataOutput.writeUTF(browserName);
        dataOutput.writeUTF(browserVersion);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.id = dataInput.readInt();
        this.browserName = dataInput.readUTF();
        this.browserVersion = dataInput.readUTF();

    }

    //浏览器的getInstance方法，浏览器模式下调用该方法
    public BrowserDimension getInstance(String browserName, String browserVersion) {
        //如果浏览器的名称是空的,就给它一个默认值
        if (StringUtils.isEmpty(browserName)){
            browserName = browserVersion = GlobleConstants.DEFAULT_VALUE;
        }
        //如果浏览器版本是空的,也赋一个默认值
        if (StringUtils.isEmpty(browserVersion)){
            browserVersion = GlobleConstants.DEFAULT_VALUE;
        }

        //不是空的就返回一个本类对象
        return new BrowserDimension(browserName,browserVersion);

    }
}

