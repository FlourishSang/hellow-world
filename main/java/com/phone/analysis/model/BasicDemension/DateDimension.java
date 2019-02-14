package com.phone.analysis.model.BasicDemension;

import com.phone.Utils.TimeUtil;
import com.phone.common.DateEnum;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * @description 时间维度类
 * @author: 赵燕钦
 * @create: 2018-11-30 19:27:27
 **/
public class DateDimension extends TopLevelDimension {

    private int id;
    private int year;
    private int season;
    private int month;
    private int week;
    private int day;
    private String type;
    private Date calendar = new Date();//计算指标的日期

    public DateDimension(){}

    //无id的构造方法
    public DateDimension(int year, int season, int month, int week, int day, String type, Date calendar) {
        this.year = year;
        this.season = season;
        this.month = month;
        this.week = week;
        this.day = day;
        this.type = type;
        this.calendar = calendar;
    }


    //有id的构造方法
    public DateDimension(int id, int year, int season, int month, int week, int day, String type, Date calendar) {
        this.id = id;
        this.year = year;
        this.season = season;
        this.month = month;
        this.week = week;
        this.day = day;
        this.type = type;
        this.calendar = calendar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCalendar() {
        return calendar;
    }

    public void setCalendar(Date calendar) {
        this.calendar = calendar;
    }

    //重写compareTo方法，对时间维度类的对象进行排序，比较的是时间维度类的属性。
    @Override
    public int compareTo(TopLevelDimension o) {
        if(this == o){
            return 0;
        }
        DateDimension other = (DateDimension)o;
        int tmp = this.id - other.id;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.year - other.year;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.season - other.season;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.month - other.month;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.week - other.week;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.day - other.day;
        if(tmp != 0){
            return tmp;
        }
        return this.type.compareTo(other.type);
    }

    /*
    * 序列化：把对象转换为字节序列的过程称为对象的序列化。
    * 反序列化：把字节序列恢复为对象的过程称为对象的反序列化
    *
    * */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeInt(this.year);
        dataOutput.writeInt(this.season);
        dataOutput.writeInt(this.month);
        dataOutput.writeInt(this.week);
        dataOutput.writeInt(this.day);
        dataOutput.writeUTF(this.type);
        dataOutput.writeLong(this.calendar.getTime());
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.year = dataInput.readInt();
        this.season =dataInput.readInt();
        this.month = dataInput.readInt();
        this.week = dataInput.readInt();
        this.day= dataInput.readInt();
        this.type = dataInput.readUTF();
        this.calendar.setTime(dataInput.readLong());
    }

    /**
     *
     * @param time long类型的时间戳， 毫秒级
     *             type:时间枚举的类型
     * @return
     */
    public static DateDimension buildDate(long time, DateEnum type){
        //getDateInfo：获取日期信息
        //1.给当前年份赋值
        int year = TimeUtil.getDateInfo(time,DateEnum.YEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        //如果是年维度，日期就为该年的第一天
        if(type.equals(DateEnum.YEAR)){
            //给当前年份设置值
            calendar.setTimeInMillis(time);
            return new DateDimension(year,0,0,0,1,type.dateType,calendar.getTime());
        }
        //2.给当前季度赋值
        int season = TimeUtil.getDateInfo(time,DateEnum.SEASON);
        if(type.equals(DateEnum.SEASON)){
            /**
             * 1 1
             * 2 4
             * 3 7
             * 4 10
             */
            int month = season*3 -2;
            calendar.set(year,month-1,1);
            return new DateDimension(year,season,month,0,1,type.dateType,calendar.getTime());
        }
        //3.给当前月份赋值
        int month = TimeUtil.getDateInfo(time,DateEnum.MONTH);
        if(type.equals(DateEnum.MONTH)){
            calendar.set(year,month-1,1);
            return new DateDimension(year,season,month,0,1,type.dateType,calendar.getTime());
        }
        //4.给当前的周数赋值
        int week = TimeUtil.getDateInfo(time,DateEnum.WEEK);
        if(type.equals(DateEnum.WEEK)){
            //获取该周的第一天
            long firstDayOfWeek = TimeUtil.getFirstDayOfWeek(time);
            year = TimeUtil.getDateInfo(time,DateEnum.YEAR);
            season = TimeUtil.getDateInfo(time,DateEnum.SEASON);
            month = TimeUtil.getDateInfo(time,DateEnum.MONTH);
            int day = TimeUtil.getDateInfo(firstDayOfWeek,DateEnum.DAY);
            calendar.set(year,month-1,day);
            return new DateDimension(year,season,month,week,day,type.dateType,calendar.getTime());
        }
        //5.给当前的天数赋值
        int day = TimeUtil.getDateInfo(time,DateEnum.DAY);
        if (type.equals(DateEnum.DAY)){
            calendar.set(year,month-1,day);
            return new DateDimension(year,season,month,week,day,type.dateType,calendar.getTime());
        }
        //获取到的时间不是枚举类型中的日期则抛出异常
        throw   new RuntimeException("该日期类型，不支持获取时间维度对象"+type.dateType);
    }

}
