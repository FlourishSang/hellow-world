package com.phone.analysis;

import com.phone.Utils.JdbcUtil;
import com.phone.analysis.mapreduce.IoOutputWriter;
import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.mapreduce.services.subClass.IDimensionImpl;
import com.phone.analysis.model.BasicDemension.TopLevelDimension;
import com.phone.analysis.model.value.StatsOutputValue;
import com.phone.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 将分析结果输出到Mysql
 * @author: 赵燕钦
 * @create: 2018-12-03 08:31:23
 **/
public class OutputToMysqlFormat extends OutputFormat<TopLevelDimension, StatsOutputValue> {
    @Override
    public RecordWriter<TopLevelDimension, StatsOutputValue> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        Connection conn = JdbcUtil.getConn();//获取数据库连接
        Configuration conf = context.getConfiguration();//获取sql语句,获取处理不同sql语句的实现类
        IDimension iDimension = new IDimensionImpl();//在数据库中获取ID的方法
        return new OutputToMysqlRecordWriter(conf,conn,iDimension);
    }

    @Override
    public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {
            //不写代码
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new FileOutputCommitter(FileOutputFormat.getOutputPath(context),
                context);
    }

    public static class OutputToMysqlRecordWriter extends RecordWriter<TopLevelDimension,StatsOutputValue>{
        Configuration conf = null;//用于获取sql语句
        Connection conn = null;//用于获取连接
        IDimension iDimension = null;//用于查询维度ID

        //缓存ps语句--想将sql语句进行缓存
        private Map<KpiType, PreparedStatement> map = new HashMap<KpiType,PreparedStatement>();
        //存储KPI对应的sql，进行批处理
        private Map<KpiType,Integer> batch =new HashMap<KpiType,Integer>();

        public OutputToMysqlRecordWriter() {
        }
        public OutputToMysqlRecordWriter(Configuration conf, Connection conn, IDimension iDimension) {
            this.conf = conf;
            this.conn = conn;
            this.iDimension = iDimension;
        }

        @Override
        public void write(TopLevelDimension key, StatsOutputValue value) throws IOException, InterruptedException {
            //获取KPI
            KpiType kpi = value.getKpi();
            //获取ps对象
            PreparedStatement ps = null;
            int count = 1;
            try {
                if (map.containsKey(kpi)){
                    ps = map.get(kpi);
                }else{
//                    conf.addResource("core-site.xml");
                    //conf.set("fs.defaultFS","hdfs://hadoop01:8020");
                    //conf.get("fs.defaultFS");
                    //通过配置文件，output_mapping.xml文件获取sql语句
                    //通过kpiName获取对应的sql语句

                    //连接数据库后通过KPI的名称获取到数据库操作语句
                    //注意：这里自己定义的指标名要和配置文件中的参数对应才能获取到插入语句
                    //hour_active_user 测试
                    ps = conn.prepareStatement(conf.get(kpi.kpiName));
                    //测试：是否获取到sql语句
//                    System.out.println(ps);
                    //缓存语句
                    map.put(kpi,ps);
                }
                batch.put(kpi,count);
                count++;
                //代码走到这里，conf,idimension ,ps ,key,value
                //实际操作数据将数据插入到表中
                String className = conf.get("writter_"+kpi.kpiName);
                //获取处理这个kpi写入表的实现类
                Class<?> classz = Class.forName(className);
                IoOutputWriter writer = (IoOutputWriter)classz.newInstance();
                writer.output(conf,key,value,ps,iDimension);
                if (batch.size()%50==0 || batch.get(kpi)%50==0){
                    ps.executeBatch();
                    batch.remove(kpi);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
            try {
                //循环执行
                for (Map.Entry<KpiType,PreparedStatement> en :map.entrySet()) {
                    en.getValue().executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //循环关闭
                for (Map.Entry<KpiType,PreparedStatement> en :map.entrySet()) {
                    JdbcUtil.close(conn,en.getValue(),null);
                }
            }
        }


    }


}
