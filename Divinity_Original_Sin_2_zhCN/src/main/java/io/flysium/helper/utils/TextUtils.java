package io.flysium.helper.utils;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import io.flysium.algorithm.TextDiff;
import io.flysium.helper.ICountLetter;
import io.flysium.helper.IDocumentToMap;
import io.flysium.helper.dto.DiffResult;

/**
 * 
 * 文本工具类
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class TextUtils {

  /***
   * 计算英文字母占比
   * 
   * @param s
   * @return
   */
  public static double countLetter(String s, ICountLetter countLetter) {
    if (countLetter != null) {
      return countLetter.countLetter(s);
    }
    String temp = new String(s);
    double f = 0f;
    for (int i = 0; i < s.length(); i++) {
      if ((temp.charAt(i) >= 'a' && temp.charAt(i) <= 'z')
          || (temp.charAt(i) >= 'A' && temp.charAt(i) <= 'Z')) {
        f += 1;
      }
    }
    return f / temp.length();
  }

  /**
   * 以第一个文本为比较基准，比较两版汉化文本，输出差异程度
   * 
   * @param doc1
   * @param doc2
   * @param documentToMap
   * @param order 是否排序
   * @param asc 从小到大排序，还是从大到小排序
   * @param print 是否打印
   * @return
   */
  public static List<DiffResult> diff(Document doc1, Document doc2, IDocumentToMap documentToMap,
      boolean order, final boolean asc, boolean print) {
    List<DiffResult> list = new LinkedList<DiffResult>();

    Map<String, String> m1 = documentToMap.toMap(doc1);
    Map<String, String> m2 = documentToMap.toMap(doc2);
    if (print) {
      System.out.println("项目数：" + m1.keySet().size());
      System.out.println("项目数：" + m2.keySet().size());
    }

    for (Map.Entry<String, String> e : m1.entrySet()) {
      String contentuid = e.getKey();
      String text1 = (String) m1.get(contentuid);
      String text2 = (String) m2.get(contentuid);
      if (text1 == null || "".equals(text1)) {
        continue;
      }
      if (text2 == null || "".equals(text2)) {
        continue;
      }
      /**
       * 文本有差异，才处理
       */
      if (text1.equals(text2)) {
        continue;
      }
      /**
       * 计算新旧文本的差异程度
       */
      int distance = TextDiff.minDistance(text1, text2);

      list.add(new DiffResult((distance * 1.0) / text1.length(), contentuid, text1, text2));
    }
    if (print) {
      System.out.println("\n\n差异项：" + list.size());
    }
    /**
     * 比较
     */
    if (order) {
      Collections.sort(list, new Comparator<DiffResult>() {

        @Override
        public int compare(DiffResult o1, DiffResult o2) {
          return asc ? __compare(o1, o2) : __compare(o2, o1);
        }

        private int __compare(DiffResult o1, DiffResult o2) {
          int i = (int) ((o1.getDifference() - o2.getDifference()) * 100);
          if (i == 0) {
            return o1.getNewText().compareTo(o2.getNewText());
          }
          return i;
        }

      });
    }
    return list;
  }



  /**
   * 替换空格等
   * 
   * @throws UnsupportedEncodingException
   */
  public static Document replaceSpace(Document document) {
    // 获取根节点元素对象
    Element root = document.getRootElement();
    // 子节点
    List<Element> list = root.elements();
    // 使用递归
    Iterator<Element> iterator = list.iterator();
    while (iterator.hasNext()) {
      Element e = iterator.next();
      String text = e.getText();
      if (text != null && !"".equals(text)) {
        text = text.replaceAll(" ", "");
        e.setText(text);
      }
    }
    return document;
  }


}
