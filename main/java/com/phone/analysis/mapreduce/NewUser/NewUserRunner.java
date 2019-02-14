package com.phone.analysis.mapreduce.NewUser;

import com.phone.Utils.TimeUtil;
import com.phone.analysis.OutputToMysqlFormat;
import com.phone.analysis.model.key.StatsUserDimension;
import com.phone.analysis.model.value.OutputMapWritable;
import com.phone.analysis.model.value.TimeOutputValue;
import com.phone.common.GlobleConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import javax.lang.model.SourceVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * @description 用户模块map阶段输出的value类型（其他模块是否能够使用）
 * 如果配置文件中，配置了jobhistory,但是服务没有开启，运行mr任务会报错
 * 但是不影响mr的运行结果（如果未开启这个服务，在mr运行结束后，写日志到hdfs会报错）
 * mr-jobhistory-daemon.sh start historyserver
 * @author: 赵燕钦
 * @create: 2018-12-03 11:22:06
 **/
public class NewUserRunner implements Tool {

    private static final Logger logger = Logger.getLogger(NewUserRunner.class);
    private Configuration conf = new Configuration();

    //主函数---入口
    public static void main(String[] args){
        try {
            //mr运行的一个辅助工具类，能运行任何实现了Tool接口的实现类
            ToolRunner.run(new Configuration(),new NewUserRunner(),args);
        } catch (Exception e) {
            logger.warn("NEW_USER TO MYSQL is failed !!!",e);
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
        Job job = Job.getInstance(conf, "NEW_USER TO MYSQL");
        job.setJarByClass(NewUserRunner.class);

        //设置map相关参数
        job.setMapperClass(NewUserMapper.class);
        job.setMapOutputKeyClass(StatsUserDimension.class);
        job.setMapOutputValueClass(TimeOutputValue.class);
        //设置本地提交集群运行，要使用job.setJar()将打包好的jar包路径写入
        //job.setJar("D:\\javaProject\\GP1814Log_Analystic\\target\\analystic-1.0.jar");

        //设置reduce相关参数
        //设置reduce端的输出格式类
        job.setReducerClass(NewUserReducer.class);
        job.setOutputKeyClass(StatsUserDimension.class);
        job.setOutputValueClass(OutputMapWritable.class);
        job.setOutputFormatClass(OutputToMysqlFormat.class);

        //设置reduce task的数量
        job.setNumReduceTasks(1);

        //设置输入参数
        this.handleInputOutput(job);
        return job.waitForCompletion(true)?1:0;
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
