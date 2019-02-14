package com.phone.etl.mapreduce;

import com.phone.Utils.TimeUtil;
import com.phone.common.GlobleConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * @Description:  ETLDriver
 * @Author: 赵燕钦
 * @create: 2018-11-29 21:15:07
 */
public class Etl2HdfsDriver  implements Tool {
    private static Logger logger = Logger.getLogger(Etl2HdfsDriver.class);
    private Configuration conf = new Configuration();

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(),new Etl2HdfsDriver(),args);
        } catch (Exception e) {
            logger.error("执行etl异常",e);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        //运行的时候 yarn jar xxx.jar 包名.类名 -d 2018-11-11
        //如果不给日期，我们就要指定默认日期
        //默认日期指定当前时间前一天的数据
        Configuration conf = getConf();
        //获取时间，将时间参数加入到conf中
        handleArgs(conf,args);
        Job job = Job.getInstance(conf,"etl to hdfs");
        //设置类路径
        job.setJarByClass(Etl2HdfsDriver.class);
        //设置map相关
        job.setMapperClass(Etl2HdfsMapper.class);
        job.setMapOutputKeyClass(LogWritable.class);
        job.setMapOutputValueClass(NullWritable.class);
        //设置reduce数量
        job.setNumReduceTasks(0);
        //设置输入输出路径
        handleInputOutput(job);
        return job.waitForCompletion(true)?1:0;
    }

    /**
     * 设置输入输出路径
     * @param job
     */
    private void handleInputOutput(Job job) {
        try {
            String [] fields = job.getConfiguration().get(GlobleConstants.RUNNING_DATE).split("-");
            String month = fields [1];
            String day = fields[2];
            FileSystem fs = FileSystem.get(job.getConfiguration());

            //输入路径
            Path inPath = new Path("/logs/2018-11-11");
            // 清洗数据输出路径/ods/11/11：采用拼接的方式
            Path outPath = new Path("/ods/"+month+"/"+day);
            if(fs.exists(inPath)){
                FileInputFormat.addInputPath(job,inPath);
            }else{
                throw new RuntimeException("输入路径不存在"+inPath.toString());
            }
            if(fs.exists(outPath)){
                fs.delete(outPath,true);
            }
            FileOutputFormat.setOutputPath(job,outPath);
        } catch (Exception e) {
            logger.error("设置输入输出路径异常",e);
        }
    }

    /**
     * 处理参数
     * @param conf
     * @param args
     */
    private void handleArgs(Configuration conf, String[] args) {
        String date =null;
        //-d 2018-11-11  -d是第一个参数，2018-11-11是第二个参数
        if(args.length>0){
            for(int i=0;i<args.length;i++){
                if(args[i].equals("-d")){
                    if(i+1<args.length){
                        date = args[i+1];
                        break;
                    }
                }
            }
        }
        //容错处理
        if(StringUtils.isEmpty(date)){
            date = TimeUtil.getYesterday();
        }
        conf.set(GlobleConstants.RUNNING_DATE,date);
    }

    @Override
    public void setConf(Configuration conf) {
        conf.addResource("core-site.xml");
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }
}