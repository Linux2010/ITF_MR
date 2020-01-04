package tfidf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author yuxiaobin
 * @date 2019-12-30 22:29
 */
public class Query {

  public static void main(String[] args) throws IOException {
    Map<String, Map<String, Double>> map = new HashMap<>();
    BufferedReader br = new BufferedReader(
        new FileReader(args[0]));

    String line;
    //构建可供查询的索引
    //Map<String, Map<String, Double>> key为查询的单词， value的map存放的是key: 单词及所在文本, value: tf-idf
    while ((line = br.readLine()) != null) {
      String[] strArr = line.split("\t");
      String[] keyArr = strArr[0].split(":");
      if (strArr.length != 2 || keyArr.length != 2) {
        continue;
      }
      if (map.get(keyArr[0]) == null) {
        //如果不存在则新建
        Map<String, Double> sub = new TreeMap<>();
        sub.put(strArr[0], Double.valueOf(strArr[1]));
        map.put(keyArr[0], sub);
      } else {
        Map<String, Double> sub = map.get(keyArr[0]);
        sub.put(strArr[0], Double.valueOf(strArr[1]));
      }
    }

    System.out.println("Your input word: " + args[1]);
    if (map.get(args[1]) == null) {
      System.out.println("Not found!");
    } else {
      List<Map.Entry<String, Double>> list = new ArrayList<>(map.get(args[1]).entrySet());
      Collections.sort(list, new Comparator<Entry<String, Double>>() {
        @Override
        public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
          return o2.getValue().compareTo(o1.getValue());
        }
      });
      System.out.println("the following documents contain the input word: ");
      for (Entry<String, Double> entry : list) {
        System.out.println(entry.getKey().split(":")[1] + "\t" + "TF-IDF: " + entry.getValue());
      }
    }
  }
}

