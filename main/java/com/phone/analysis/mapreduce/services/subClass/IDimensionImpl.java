package com.phone.analysis.mapreduce.services.subClass;

import com.phone.Utils.JdbcUtil;
import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.model.BasicDemension.*;

import java.sql.*;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description IDimension的实现类
 * @author: 赵燕钦
 * @create: 2018-12-02 23:53:39
 **/
public class IDimensionImpl implements IDimension {
    //定义内存缓存，用来缓存维度ID
    //移除最老的
    private Map<String,Integer> cache = new LinkedHashMap<String,Integer>(){
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return this.size() >2500;
        }
    };


    @Override
    public int getIDimensionImplByDimension(TopLevelDimension baseDimension) {
        Connection conn = null;
        //构建缓存的key，根据baseDimension能够确定唯一
        //还可以根据这个baseDimension从缓存中取出来
        String cacheKey = buildcacheKey(baseDimension);
        //判断缓存中是否存在
        if(cache.containsKey(cacheKey)){
            return this.cache.get(cacheKey);
        }
        //如果代码走到这里说明缓存中没有
        //先到数据库中查，如果有返回ID
        //数据中没有插入维度并返回ID
        String sqls [] = null;
        if(baseDimension instanceof KpiDimension){
            sqls = buildKpiSqls(baseDimension);
        }else if (baseDimension instanceof DateDimension){
            sqls = buildDateSqls(baseDimension);
        }else if (baseDimension instanceof PlatformDimension){
            sqls = buildPlSqls(baseDimension);
        }else if (baseDimension instanceof BrowserDimension){
            sqls = buildBrowserSqls(baseDimension);
        }else if (baseDimension instanceof  LocationDimension){
            sqls = buildLocalSqls(baseDimension);
        }else if(baseDimension instanceof EventDimension){
            sqls = buildEventSqls(baseDimension);
        }else if (baseDimension instanceof  PaymentTypeDimension){
            sqls = buildPaymentTypeSqls(baseDimension);
        }else if (baseDimension instanceof  CurrencyTypeDimension){
            sqls = buildCurrentTypeSqls(baseDimension);
        }
        //获取连接
        conn = JdbcUtil.getConn();
        int id =-1;
        id = excuteSql(conn,sqls,baseDimension);
        cache.put(cacheKey,id);
        return id;
    }

    private int excuteSql(Connection conn, String[] sqls, TopLevelDimension baseDimension) {
        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            String selectSql = sqls[1];
//            System.out.println("selectsql"+selectSql);
            //获取ps对象
            ps = conn.prepareStatement(selectSql);
//            System.out.println("sql语句是："+ ps);
            //为ps赋值
            this.setArgs(baseDimension,ps);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            //代码走到这里，说明数据库中没有这个维度，插入维度信息，并返回id
            ps = conn.prepareStatement(sqls[0], Statement.RETURN_GENERATED_KEYS);
            this.setArgs(baseDimension,ps);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if(rs.next()){

                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return -1;
    }

    private void setArgs(TopLevelDimension baseDimension, PreparedStatement ps) {
        try {
            int i = 0;
            if(baseDimension instanceof KpiDimension){
                KpiDimension kpi = (KpiDimension)baseDimension;
                ps.setString(++i,kpi.getKpiName());
            }else if(baseDimension instanceof PlatformDimension){
                PlatformDimension platformDimension = (PlatformDimension)baseDimension;
                ps.setString(++i,platformDimension.getPlatform());
            }else if (baseDimension instanceof BrowserDimension){
                BrowserDimension browserDimension= (BrowserDimension) baseDimension;
                ps.setString(++i,browserDimension.getBrowserName());
                ps.setString(++i,browserDimension.getBrowserVersion());

            }else if(baseDimension instanceof DateDimension){
                DateDimension dateDimension = (DateDimension)baseDimension;
                ps.setInt(++i, dateDimension.getYear());
                ps.setInt(++i, dateDimension.getSeason());
                ps.setInt(++i, dateDimension.getMonth());
                ps.setInt(++i, dateDimension.getWeek());
                ps.setInt(++i, dateDimension.getDay());
                ps.setString(++i, dateDimension.getType());
                ps.setDate(++i, new Date(dateDimension.getCalendar().getTime()));
            }else if (baseDimension instanceof  LocationDimension){
                LocationDimension local = (LocationDimension) baseDimension;
                ps.setString(++i,local.getCountry());
                ps.setString(++i,local.getProvince());
                ps.setString(++i,local.getCity());
            }else if (baseDimension instanceof  EventDimension){
                EventDimension eventDimension = (EventDimension)baseDimension;
                ps.setString(++i,eventDimension.getCategory());
                ps.setString(++i,eventDimension.getAction());
            }
            else if (baseDimension instanceof  PaymentTypeDimension){
                PaymentTypeDimension ptd = (PaymentTypeDimension)baseDimension;
                ps.setString(++i,ptd.getPayment_type());
            }else if (baseDimension instanceof  CurrencyTypeDimension){
                CurrencyTypeDimension ctd = (CurrencyTypeDimension)baseDimension;
                ps.setString(++i,ctd.getCurrency_name());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String[] buildBrowserSqls(TopLevelDimension baseDimension) {
        String insertSql = "INSERT INTO `dimension_browser`(`browser_name`, `browser_version`) VALUES(?,?)";
        String selectSql = "SELECT `id` FROM `dimension_browser` WHERE `browser_name` = ? AND `browser_version` = ?";
        return new String[]{insertSql,selectSql};

    }

    private String[] buildPlSqls(TopLevelDimension baseDimension) {
        String insertSql = "insert into `dimension_platform`(platform_name) values(?)";
        String selectSql = "select id from `dimension_platform` where platform_name = ?";
        return new String[]{insertSql,selectSql};
    }

    private String[] buildDateSqls(TopLevelDimension baseDimension) {
        String insertSql = "INSERT INTO `dimension_date`(`year`, `season`, `month`, `week`, `day`, `type`, `calendar`) VALUES(?, ?, ?, ?, ?, ?, ?)";
        String selectSql = "SELECT `id` FROM `dimension_date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?";
        return new String[]{insertSql,selectSql};
    }

    private String[] buildKpiSqls(TopLevelDimension baseDimension) {
        String insertSql = "insert into `dimension_kpi`(kpi_name) values(?)";
        String selectSql = "select id from `dimension_kpi` where kpi_name = ?";
        return new String []{insertSql,selectSql};
    }
    private String[] buildLocalSqls(TopLevelDimension baseDimension) {
        String query = "select id from `dimension_location` where `country` = ? and `province` = ? and `city` = ? ";
        String insert = "insert into `dimension_location`(`country` , `province` , `city`) values(?,?,?)";
        return new String[]{insert,query};
    }
    private String[] buildEventSqls(TopLevelDimension baseDimension) {
        String query = "select id from `dimension_event` where `category` = ? and `action` = ? ";
        String insert = "insert into `dimension_event`(`category` , `action`) values(?,?)";
        return new String[]{insert,query};
    }
    private String[] buildPaymentTypeSqls(TopLevelDimension baseDimension){
        String query = "select id from `dimension_payment_type` where `payment_type` = ?";
        String insert = "insert into `dimension_payment_type`(`payment_type`) values(?)";
        return new String[]{insert,query};
    }
    private String[] buildCurrentTypeSqls(TopLevelDimension baseDimension){
        String query = "select id from `dimension_currency_type` where `currency_name` = ?";
        String insert = "insert into `dimension_currency_type`(`currency_name`) values(?)";
        return new String[]{insert,query};
    }

    private String buildcacheKey(TopLevelDimension baseDimension) {
        StringBuffer sb = new StringBuffer();
        if(baseDimension instanceof KpiDimension){
            sb.append("kpi_");
            KpiDimension kpi = (KpiDimension)baseDimension;
            sb.append(kpi.getKpiName());
        }else if(baseDimension instanceof PlatformDimension){
            sb.append("pl_");
            PlatformDimension pl = (PlatformDimension)baseDimension;
            sb.append(pl.getPlatform());
        }else if (baseDimension instanceof BrowserDimension){
            sb.append("browser_");
            BrowserDimension browser = (BrowserDimension)baseDimension;
            sb.append(browser.getBrowserName()).append(browser.getBrowserVersion());

        }else if(baseDimension instanceof DateDimension){
            sb.append("dt_");
            DateDimension dt = (DateDimension)baseDimension;
            sb.append(dt.getYear());
            sb.append(dt.getSeason());
            sb.append(dt.getMonth());
            sb.append(dt.getWeek());
            sb.append(dt.getDay());
            sb.append(dt.getType());
        }else if (baseDimension instanceof LocationDimension){
            LocationDimension local = (LocationDimension) baseDimension;
            sb.append("local_");
            sb.append(local.getCountry());
            sb.append(local.getProvince());
            sb.append(local.getCity());
        }else if (baseDimension instanceof EventDimension){
            EventDimension event = (EventDimension)baseDimension;
            sb.append("event_");
            sb.append(event.getCategory());
            sb.append(event.getAction());
        }else if(baseDimension instanceof  PaymentTypeDimension){
            PaymentTypeDimension payment = (PaymentTypeDimension)baseDimension;
            sb.append("payment_");
            sb.append(payment.getPayment_type());
        }else if (baseDimension instanceof  CurrencyTypeDimension){
            CurrencyTypeDimension currency = (CurrencyTypeDimension)baseDimension;
            sb.append("currency");
            sb.append(currency.getCurrency_name());
        }
        return sb !=null ? sb.toString():null;
    }
}
