package com.phone.common;

/**
 * @description Kpi的类型
 * @author: 赵燕钦
 * @create: 2018-12-02 23:11:03
 **/
public enum KpiType {
    //下面的指标用到哪个就自定义哪个
    //新增用户
    NEW_USER("new_user"),
    BROWSER_NEW_USER("browser_new_user"),

    //活动用户
    ACTIVE_USER("active_user"),
    BROWSER_ACTIVE_USER("browser_active_user"),

    //活动会员
    ACTIVE_MEMBER("active_member"),
    BROWSER_ACTIVE_MEMBER("browser_active_member"),

    //新增会员
    NEW_MEMBER("new_member"),
    BROWSER_NEW_MEMBER("browser_new_member"),
    MEMBER_INFO("member_info"),

    //新增总用户
    NEW_TOTAL_USER("new_total_user"),
    BROWSER_NEW_TOTAL_USER("browser_new_total_user"),

    //总会员
    TOTAL_MEMBER("total_member"),
    BROWSER_TOTAL_MEMBER("browser_total_member"),

    //会话Kpi
    SESSION("session"),
    BROWSER_SESSION("browser_session"),

    //按小时统计活跃用户
    HOUR_ACTIVE_USER("hour_active_user"),
    BROWSER_HOUR_ACTIVE_USER("browser_hour_active_user"),

    //地域信息KPI
    LOCAL_INFO("local_info"),

    //地域信息下KPI（用于求地域下的新增会员）
    LOCAL_INFO_NEWMEMBER("local_info_nm")


    ;
    public String kpiName;
    KpiType(String kpiName){
        this.kpiName = kpiName;
    }

    /**
     * 根据kpi的name获取对应的指标
     */
    public static KpiType valueOfKpiName(String name){
        for (KpiType kpi : values()){
            if(kpi.kpiName.equals(name)){
                return kpi;
            }
        }
        return null;
    }

}
