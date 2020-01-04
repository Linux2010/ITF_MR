package tfidf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author yuxiaobin
 * @date 2019-12-30 22:29
 */
public class IDF {

  public static class IDFMapper extends Mapper<Object, Text, Text, Text> {

    @Override
    protected void map(Object key, Text value, Context context)
        throws IOException, InterruptedException {
      /*
        1. 接上个reduce的输出为输入: (word:fileName, tf)
        2. 将其拆解为(word, tf:filename)形式， 输出
       */
      Text keyLabel = new Text();
      Text fileNameLabel = new Text();
      if (value.toString() != null && !"".equals(value.toString())) {
        StringTokenizer tokenizer = new StringTokenizer(value.toString());
        String token = tokenizer.nextToken();
        keyLabel.set(token.split(":")[0]);
        if (token.split(":")[1] != null && !"".equals(token.split(":")[1])) {
          fileNameLabel.set(String.join(":", tokenizer.nextToken(), token.split(":")[1]));
        }
        context.write(keyLabel, fileNameLabel);
      }
    }
  }

  public static class IDFReducer extends Reducer<Text, Text, Text, Text> {

    private int totalFileCount = 0;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      //需要获得文件的总个数
      File dir = new File("/Users/mac/workspace/ii/inverted-index/dataset");
      totalFileCount = dir.list().length;
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException {
      if (values == null) {
        return;
      }

      int fileCount = 0;
      List<String> valList = new ArrayList<>();
      for (Text value : values) {
        valList.add(value.toString());
        //统计word一个在多少个文件中出现过
        fileCount++;
      }

      //根据公式计算idf
      Double idf = Math.log10(1.0 * totalFileCount / (fileCount + 1));

      //计算tf-idf，再将输入拆解为(word:fileName,tf-idf)输出
      Text label = new Text();
      for (String value : valList) {
        Double tfidf = Double.parseDouble(value.split(":")[0]) * idf;
        label.set(String.join(":", key.toString(), value.split(":")[1]));
        context.write(label, new Text(String.valueOf(tfidf)));
      }
    }
  }
}
