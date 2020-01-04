package tfidf;

/**
 * @author yuxiaobin
 * @date 2019-12-30 22:29
 */
public class Test {

  public static void main(String[] args) {

    String str = "asd,as \"df'9a-sd()ad.";

    System.out.println(str.replaceAll("[.,:;!?\\-()\"']",""));

    String str1 = "According:Baek_Character_Region_Awareness_for_Text_Detection_CVPR_2019_paper.txt\t5.5880051697755604E-6\n";

    System.out.println(Math.log10(1.0*1293/2));
  }
}
