package com.phone.etl.Utils;

import com.phone.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 用于解析日志的工具类
 * @author: 赵燕钦
 * @create: 2018-11-29 15:09:27
 **/
public class LogParserUtil {

    //Logger类用于打印日志：日志解析类解析日志的产生的记录
    private static Logger logger = Logger.getLogger(LogParserUtil.class);
    public static Map<String,String> parserLog(String log){
        Map<String,String> map = new ConcurrentHashMap<>();
        if(StringUtils.isNotEmpty(log)){
            //拆分日志
            String[] fields = log.split("\\^A");
            if (fields.length==4){//此处判断日志是否有四个字段，满足四个字段的才是完整数据
                map.put(Constants.LOG_IP,fields[0]);
                map.put(Constants.LOG_SERVER_TIME,fields[1].replaceAll("\\.",""));//去掉点
                //参数列表，单独定义方法去处理
                String params = fields[3];
                //处理参数列表的方法
                handleParam(params,map);//两个参数是因为处理完的数据要放入map集合中
                //处理ip
                handleIp(map);
                //处理浏览器信息
                handleAgent(map);
            }
        }
        return map;
    }


    /**
     * 处理浏览器信息,
     * @param map
     */
    private static void handleAgent(Map<String, String> map) {
        if(map.containsKey(Constants.LOG_USERAGENT)){
            //UserAgentParserUtil工具类中封装的是处理浏览器的方法，此处调用该方法
            UserAgentParserUtil.AgentInfo info = UserAgentParserUtil.parserUserAgent(map.get(Constants.LOG_USERAGENT));
            //往map集合中添加名称，版本等信息
            map.put(Constants.LOG_BROWSER_NAME,info.getBrowserName());
            map.put(Constants.LOG_BROWSER_VERSION,info.getBrowserVersion());
            map.put(Constants.LOG_OS_NAME,info.getOsName());
            map.put(Constants.LOG_OS_VERSION,info.getOsVersion());
        }
    }


    /**
     * 处理IP
     * @param map
     */
    private static void handleIp(Map<String, String> map) {
        if(map.containsKey(Constants.LOG_IP)){
            IpParsingUtil.RegionInfo info = IpParsingUtil.ipParser(map.get(Constants.LOG_IP));
            map.put(Constants.LOG_COUNTRY,info.getCountry());
            map.put(Constants.LOG_PROVINCE,info.getProvince());
            map.put(Constants.LOG_CITY,info.getCity());
        }
    }

    /**
     * 处理参数列表。将参数列表放入map集合中
     * @param params
     * @param map
     */
    private static void handleParam(String params, Map<String, String> map) {
        if(StringUtils.isNotEmpty(params)){
            int index = params.indexOf("?");
            if(index>0){
                //先截取再拆分，拆分的结果是键值对(字符串)的数组
                String [] fields = params.substring(index+1).split("&");
                for (String field:fields) {
                    String [] kvs = field.split("=");
                    String k = kvs[0];
                    String v = null;
                    try {
                        v = URLDecoder.decode(kvs[1],"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("url解码异常",e);
                    }
                    if(StringUtils.isNotEmpty(k)){
                        map.put(k,v);
                    }
                }
            }
        }

    }
    }
