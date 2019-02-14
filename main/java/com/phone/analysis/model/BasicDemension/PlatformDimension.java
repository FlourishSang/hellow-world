package com.phone.analysis.model.BasicDemension;

import com.phone.common.GlobleConstants;
import jodd.util.StringUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 平台维度类，包含平台id和平台名称
 * @author: 赵燕钦
 * @create: 2018-11-30 17:56:18
 **/
public class PlatformDimension extends TopLevelDimension {
    private int id;
    private String platform;

    //有参无参构造方法
    public PlatformDimension(){}
    public PlatformDimension(String platform){
        this.platform = platform;
    }
    public PlatformDimension(int id,String platform){
        this.id = id;
        this.platform = platform;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /*
    * 
    * */
    public static PlatformDimension getInstance(String platformName){

        String pl= StringUtil.isEmpty(platformName)? GlobleConstants.DEFAULT_VALUE:platformName;
        return new PlatformDimension(pl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,platform);
    }

    @Override
    public boolean equals(Object obj) {
        if (this ==obj)
            return true;
        if (obj==null || getClass() !=obj.getClass())
            return false;
        PlatformDimension pfd = (PlatformDimension)obj;
        return id == pfd.id &&
                Objects.equals(platform,pfd.platform);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /*
    * 序列化与反序列化
    * 注意write与read的顺序
    *
    * */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        //空指针异常
//        System.out.println("platform: "+platform);
        dataOutput.writeUTF(this.platform);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

        this.id = dataInput.readInt();
        this.platform = dataInput.readUTF();
    }


    @Override
    public int compareTo(TopLevelDimension o) {
        if (this ==o){
            return 0;
        }
        PlatformDimension other = (PlatformDimension)o;
        int tmp = this.id -other.id;
        if (tmp!=0){
            return tmp;
        }
        return this.platform.compareTo(other.platform);
    }
}
