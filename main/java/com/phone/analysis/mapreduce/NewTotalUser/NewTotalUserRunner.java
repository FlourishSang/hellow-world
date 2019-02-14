package com.phone.analysis.mapreduce.NewTotalUser;

import com.phone.Utils.JdbcUtil;
import com.phone.Utils.TimeUtil;
import com.phone.analysis.OutputToMysqlFormat;
import com.phone.analysis.mapreduce.services.IDimension;
import com.phone.analysis.mapreduce.services.subClass.IDimensionImpl;
import com.phone.analysis.model.BasicDemension.DateDimension;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.OutputMapWritable;
import com.phone.analysis.model.value.TimeOutputValue;
import com.phone.common.DateEnum;
import com.phone.common.GlobleConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 用户模块map阶段输出的value类型（其他模块是否能够使用）
 * 如果配置文件中，配置了jobhistory,但是服务没有开启，运行mr任务会报错
 * 但是不影响mr的运行结果（如果未开启这个服务，在mr运行结束后，写日志到hdfs会报错）
 * mr-jobhistory-daemon.sh start historyserver
 * @author: 赵燕钦
 * @create: 2018-12-03 11:22:06
 **/
public class NewTotalUserRunner implements Tool {

    private static final Logger logger = Logger.getLogger(NewTotalUserRunner.class);
    private Configuration conf = new Configuration();

    //主函数---入口
    public static void main(String[] args){
        try {
            //mr运行的一个辅助工具类，能运行任何实现了Tool接口的实现类
            ToolRunner.run(new Configuration(),new NewTotalUserRunner(),args);
        } catch (Exception e) {
            logger.warn("NEW_MEMBER TO MYSQL is failed !!!",e);
        }
    }

    @Override
    public void setConf(Configuration conf) {
        conf.addResource("output_mapping.xml");
        conf.addResource("output_writter.xml");
        conf.addResource("core-site.xml");
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        //为结果表中的created赋值，设置到conf中,需要我们传递参数---一定要在job获取前设置参数
        this.setArgs(args, conf);
        Job job = Job.getInstance(conf, "NEW_MEMBER TO MYSQL");
        job.setJarByClass(NewTotalUserRunner.class);

        //设置map相关参数
        job.setMapperClass(NewTotalUserMapper.class);
        job.setMapOutputKeyClass(StatsUserDimension.class);
        job.setMapOutputValueClass(TimeOutputValue.class);
        //设置本地提交集群运行，要使用job.setJar()将打包好的jar包路径写入
        //job.setJar("D:\\javaProject\\GP1814Log_Analystic\\target\\analystic-1.0.jar");

        //设置reduce相关参数
        //设置reduce端的输出格式类
        job.setReducerClass(NewTotalUserReducer.class);
        job.setOutputKeyClass(StatsUserDimension.class);
        job.setOutputValueClass(OutputMapWritable.class);
        job.setOutputFormatClass(OutputToMysqlFormat.class);

        //设置reduce task的数量
        job.setNumReduceTasks(1);

        //设置输入参数
        this.handleInputOutput(job);
        //计算新增总用户昨天和今天的相加

        //对当前任务进行截断：如果job的进程为false，则当前任务没有终止，计算前一天的新增总用户和今天的新增用户
        if (job.waitForCompletion(true)){
            this.calculateNewTotalUser(job);
            return 0;
        }else{
            return 1;
        }
    }

    //该方法是计算新增总用户的（重点）
    private int calculateNewTotalUser(Job job) {
            //1.首先通过RUNNING_DATE获取当前系统的时间(String类型的）
        String n_date = job.getConfiguration().get(GlobleConstants.RUNNING_DATE);
        //通过TimeUtil工具类将时间格式从String类型转换为Long时间戳类型
        long nowday = TimeUtil.parseString2Long(n_date);
        //2.获取前一天的时间, 定义为long类型（时间戳：毫秒级的）今天的时间 - 一天的时间 = 一天前的时间
        long dateOfYesterday = nowday - GlobleConstants.DAY_BEFORE_MILLISECOND;

        //3.获取时间维度--> 当前时间和一天前的时间：传入时间戳和时间枚举类型（天：DAY）
        DateDimension nowday_Dimension = DateDimension.buildDate(nowday,DateEnum.DAY);
        DateDimension yesterday_Dimension = DateDimension.buildDate(dateOfYesterday,DateEnum.DAY);

        //4..1创建获取维度id的对象
        IDimension iDimension = new IDimensionImpl();
        //4.2定义今天和前一天时间维度的id
        int nowday_DimensionID = -1;
        int yesterday_DimensionID = -1;



        //sql.1获取数据库连接，sql语句，定义一个结果集,存放存数据库中查询的结果
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            //4.3通过今天当前创建计算的时间戳和昨天的时间戳获取今天和前一天的时间维度ID
            nowday_DimensionID = iDimension.getIDimensionImplByDimension(nowday_Dimension);
            yesterday_DimensionID = iDimension.getIDimensionImplByDimension(yesterday_Dimension);

            //sql.2 通过JdbcUtil连接数据库工具类获取数据库连接
            conn = JdbcUtil.getConn();


            //5.创建一个map集合,Key:平台维度id和浏览器维度id拼接成的String类型的key,value是newInstallUser和totalInstallUser (Integer类型）
            Map<String,Integer> map = new HashMap<String,Integer>();

            //判断为度id是否大于0,容错处理

            //6.查询今天的新增用户
            if (nowday_DimensionID > 0 ){
                //通过配置文件获取到数据库查询语句，配置文件中为other_mapping中的内容：数据库查询语句（浏览器模块和用户模块都查）
                conf.addResource("other_mapping.xml");
                ps = conn.prepareStatement(conf.get("other_new_total_browser_user_now_sql"));//通过name获取sql查询语句
                ps.setInt(1,nowday_DimensionID);//约束条件是时间维度id,根据今天的时间维度id
                rs = ps.executeQuery();//执行sql语句将结果放到结果集中
                //遍历结果集（查询到的数据库中的数据）
                while(rs.next()){
                    //获取到今天时间戳里的平台维度id，浏览器维度id，新增用户数
                    int platformDimensionId = rs.getInt("platform_dimension_id");
                    int browserDimensionId = rs.getInt("browser_dimension_id");
                    int newInstallUser = rs.getInt("new_install_users");
                    //将platformId和browserDimensionID作为map集合的key,新增用户数为value，将遍历的数据，添加的map集合中
                    String key = platformDimensionId+"_"+browserDimensionId;//只要遍历到了这两个数据就组合
                    map.put(key,newInstallUser);
                }
            }

            //7.查询前一天的新增总用户
            if (yesterday_DimensionID >0){
                ps = conn.prepareStatement(conf.get("other_new_total_browser_user_yesterday_sql"));
                ps.setInt(1,nowday_DimensionID);//根据昨天的时间维度ID获取到一天的数据
                rs = ps.executeQuery();
                while(rs.next()){
                    int platformDimensionId = rs.getInt("platform_dimension_id");
                    int browserDimensionId = rs.getInt("browser_dimension_id");
                    int totalInstallUser = rs.getInt("total_install_users");
                    String key = platformDimensionId+"_"+browserDimensionId;
                    if (map.containsKey(key)){
                        totalInstallUser +=map.get(key);
                    }
                    map.put(key,totalInstallUser);
            }
            }

            //8.更新数据库
            if (map.size()>0){
                //遍历map集合
                for (Map.Entry<String,Integer> entry: map.entrySet()){
                    ps = conn.prepareStatement(conf.get("other_n        ew_total_browser_user_update_sql"));
                    String[] fields = entry.getKey().split("_");//组合key形式:platformDimensionId+"_"+browserDimensionId
                    ps.setInt(1,nowday_DimensionID);
                    ps.setInt(2,Integer.parseInt(fields[0]));//平台id类型为int类型，获取到的平台维度id是字符串，所以要转换成int
                    ps.setInt(3,Integer.parseInt(fields[1]));
                    ps.setInt(4,entry.getValue());

//                    String date = conf.get(GlobleConstants.RUNNING_DATE);
//                    long date2 = Long.parseLong(date);
//                    Date date3 = new Date(date2);
                    ps.setString(5,conf.get(GlobleConstants.RUNNING_DATE));
                    ps.setInt(6,entry.getValue());
                    //执行更新
                    ps.execute();


                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.close(conn,ps,rs);
        }

        return 0;
    }

    /**
     * 参数处理,将接收到的日期存储在conf中，以供后续使用
     * @param args  如果没有传递日期，则默认使用昨天的日期
     * @param conf
     */
    private void setArgs(String[] args, Configuration conf) {
        String date = null;
        for (int i = 0;i < args.length;i++){
            if(args[i].equals("-d")){
                if(i+1 < args.length){
                    date = args[i+1];
                    break;
                }
            }
        }
        //代码到这儿，date还是null，默认用昨天的时间
        if(date == null){
            date = TimeUtil.getYesterday();
        }
        //然后将date设置到时间conf中
        conf.set(GlobleConstants.RUNNING_DATE,date);
    }

    /**
     * 设置输入输出,_SUCCESS文件里面是空的，所以可以直接读取清洗后的数据存储目录
     * @param job
     */
    private void handleInputOutput(Job job) {
        //第二个参数设置为 2018-11-11 切割符为 -：此处我们只获取月份和day,用于拼接输入数据的路径
        String[] fields = job.getConfiguration().get(GlobleConstants.RUNNING_DATE).split("-");
        String month = fields[1];//月份
        String day = fields[2];//天

        try {
            FileSystem fs = FileSystem.get(job.getConfiguration());
            /*
             ** 此处是hdfs输入路径：需要去拼接，通过设置的参数，-d 2018-05-28 或者29 这两个参数，获取到第二个参数，
             *对它进行切分，然后拼接成和hdfs上数据的存储目录一样的路径
             * */
            Path inpath = new Path("/ods/" + month + "/" + day);
            if (fs.exists(inpath)) {
                FileInputFormat.addInputPath(job, inpath);
            } else {
                throw new RuntimeException("输入路径不存在inpath" + inpath.toString());
            }
        } catch (IOException e) {
            logger.warn("设置输入输出路径异常！！！", e);
        }
    }
}
