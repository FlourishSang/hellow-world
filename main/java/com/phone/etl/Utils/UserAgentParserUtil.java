package com.phone.etl.Utils;




import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @description 解析userAgent的工具类
 * @author: 赵燕钦
 * @create: 2018-11-28 21:38:58
 **/
public class UserAgentParserUtil {
    //日志类：用于打印日志
    private static Logger logger = Logger.getLogger(UserAgentParserUtil.class);
    private static UASparser uaSparser = null;
    //静态代码块
    static{
        try {
            uaSparser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
           logger.error("获取uaSparser异常",e);
        }
    }
    /*
    * 解析userAgent的方法，agentInfo封装的是浏览器的信息以及操作系统的信息
    * */
    public static AgentInfo parserUserAgent(String userAgent){
        AgentInfo agentInfo = null;
        if (StringUtils.isNotEmpty(userAgent)){
            try {
                UserAgentInfo userAgentInfo = uaSparser.parse(userAgent);
                if (userAgentInfo!=null){
                    agentInfo = new AgentInfo();
                    agentInfo.setBrowserName(userAgentInfo.getUaFamily());
                    agentInfo.setBrowserVersion(userAgentInfo.getBrowserVersionInfo());
                    agentInfo.setOsName(userAgentInfo.getOsName());
                    agentInfo.setOsVersion(userAgentInfo.getOsFamily());
                }
            } catch (IOException e) {
                logger.error("解析userAgent异常",e);
            }
        }
        return agentInfo;
    }

    /*
    * 用于封装浏览器信息的内部类
    * */
    public static class AgentInfo{
        //定义属性，浏览器名称，版本，操作系统名称，版本
        private String browserName;
        private String browserVersion;
        private String osName;
        private String osVersion;


        public AgentInfo(){}

        public AgentInfo(String browserName, String browserVersion, String osName, String osVersion) {
            this.browserName = browserName;
            this.browserVersion = browserVersion;
            this.osName = osName;
            this.osVersion = osVersion;
        }

        //get和set方法
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

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        @Override
        public String toString() {
            return "AgentInfo{" +
                    "browserName='" + browserName + '\'' +
                    ", browserVersion='" + browserVersion + '\'' +
                    ", osName='" + osName + '\'' +
                    ", osVersion='" + osVersion + '\'' +
                    '}';
        }
    }
}
