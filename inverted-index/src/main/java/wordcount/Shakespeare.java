package wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Shakespeare {

    public static class MoviesMapper extends Mapper<Object, Text, Text, IntWritable> {

        Map<String, String> map = new HashMap<>();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            String tycPath = context.getConfiguration().get("tyc");
            FileSystem fileSystem = FileSystem.get(context.getConfiguration());
            FSDataInputStream hdfs = fileSystem.open(new Path(tycPath));
            BufferedReader br = new BufferedReader(new InputStreamReader(hdfs));

            String line = "";
            while ((line = br.readLine()) != null) {
                map.put(line, line);
            }
            br.close();
        }

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            String[] words = value.toString().split(" ");

            for (String word : words) {
                if (map.containsKey(word)) {
                    continue;
                }
                context.write(new Text(word), new IntWritable(1));
            }
        }
    }

    public static class MoviesReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

        HashMap<String, Integer> sortMap = new HashMap<>();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) {
            int sum = 0;

            for (IntWritable value : values) {
                sum += value.get();
            }
            sortMap.put(key.toString(), sum);
        }

        //map按value降序排序
        public HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
            List<Map.Entry<String, Integer>> list =
                    new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            });

            HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
            for (Map.Entry<String, Integer> aa : list) {
                temp.put(aa.getKey(), aa.getValue());
            }
            return temp;
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            HashMap<String, Integer> sort = sortByValue(sortMap);
            for (Map.Entry<String, Integer> entry : sort.entrySet()) {
                context.write(new Text(entry.getKey()), new IntWritable(entry.getValue()));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args)
                .getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("请必须输入三个参数！");
            System.exit(2);
        }

        conf.set("tyc", args[2]);

        Job job = new Job(conf);
        job.setJarByClass(Shakespeare.class);
        job.setMapperClass(MoviesMapper.class);
        job.setCombinerClass(MoviesReducer.class);
        job.setReducerClass(MoviesReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
