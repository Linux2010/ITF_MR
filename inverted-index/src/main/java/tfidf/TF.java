package tfidf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * @author yuxiaobin
 * @date 2019-12-30 22:29
 */
public class TF {

  public static class TFMapper extends Mapper<Object, Text, Text, Text> {

    private String fileName = "";


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      InputSplit inputSplit = (InputSplit) context.getInputSplit();
      fileName = ((FileSplit) inputSplit).getPath().getName();
    }

    @Override
    protected void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {

      //统计该文本总共有多少分词
      int totalCount = 0;
      List<String> list = new ArrayList<>();

      StringTokenizer tokenizer = new StringTokenizer(
          value.toString().replaceAll("[.,:;!?\\-()\"']", " "));
      Text label = new Text();
      IntWritable count = new IntWritable(1);
      while (tokenizer.hasMoreTokens()) {
        label.set(tokenizer.nextToken().toLowerCase() + ":" + fileName);
        list.add(label + "\t" + count);
        totalCount++;
      }
      /*
         1. 一个文本只包含一条记录
         2. 将分词后的word与其所在文件组成key, 形式为word:fileName
         3. 将totalCount也拼接进value中， 后面reduce计算TF需要用到， 由于是每个文件分别做的mr， 因此需要将totalCount分别带入对应的key中(大致思路)
         4. map端输出，(word:fileName, count:totalCount)
       */
      for (String tuple2 : list) {
        context.write(new Text(tuple2.split("\t")[0]),
            new Text(tuple2.split("\t")[1] + ":" + totalCount));
      }
    }
  }

  public static class TFReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException {

      if (values == null) {
        return;
      }

      int totalCount = 0;
      int sumCount = 0;
      for (Text value : values) {
        sumCount += Integer.valueOf(value.toString().split(":")[0]);
        totalCount = Integer.valueOf(value.toString().split(":")[1]);
      }

      /*
        1. sumCount相当于计算的每个单词在文本中出现的次数
        2. 根据公式计算TF
        3. reduce端输出，(word:fileName, tf)
       */
      Double tf = 1.0 * sumCount / totalCount;
      context.write(new Text(key.toString()),
          new Text(String.valueOf(tf.isInfinite() ? Double.MIN_VALUE : tf.doubleValue())));
    }


    @Override
    public void run(Context context) throws IOException, InterruptedException {
      super.run(context);
    }
  }
}
