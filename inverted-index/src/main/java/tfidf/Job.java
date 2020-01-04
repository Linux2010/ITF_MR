package tfidf;


import java.io.IOException;
import java.util.Date;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import tfidf.IDF.IDFMapper;
import tfidf.IDF.IDFReducer;
import tfidf.TF.TFMapper;
import tfidf.TF.TFReducer;

/**
 * @author yuxiaobin
 * @date 2019-12-30 22:29
 */
public class Job {

  public static void main(String[] args)
      throws InterruptedException, IOException, ClassNotFoundException {
    //执行两个mapreduce任务
    runTf(args);
    runIdf(args);
    System.out.println(new Date());
  }

  public static void runTf(String[] args)
      throws InterruptedException, IOException, ClassNotFoundException {
    Configuration conf = new Configuration();
    org.apache.hadoop.mapreduce.Job job = org.apache.hadoop.mapreduce.Job.getInstance(conf);

    job.setJarByClass(TF.class);

    job.setMapperClass(TFMapper.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    FileInputFormat.setInputPaths(job, new Path(args[0]));

    job.setReducerClass(TFReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    job.waitForCompletion(true);
  }

  public static void runIdf(String[] args)
      throws InterruptedException, IOException, ClassNotFoundException {
    Configuration conf = new Configuration();
    org.apache.hadoop.mapreduce.Job job = org.apache.hadoop.mapreduce.Job.getInstance(conf);

    job.setJarByClass(IDF.class);

    job.setMapperClass(IDFMapper.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    FileInputFormat.setInputPaths(job, new Path(args[2]));

    job.setReducerClass(IDFReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileOutputFormat.setOutputPath(job, new Path(args[3]));

    job.waitForCompletion(true);
  }
}
