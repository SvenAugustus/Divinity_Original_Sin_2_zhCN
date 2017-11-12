package io.flysium;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import io.flysium.algorithm.TextDiff;
import io.flysium.game.dos2.DOS2CountLetter;
import io.flysium.game.dos2.DOS2Extract;
import io.flysium.game.dos2.DOS2Replace;
import io.flysium.game.dos2.DOS2ToMap;
import io.flysium.helper.ICountLetter;
import io.flysium.helper.IDocumentExtract;
import io.flysium.helper.IDocumentReplace;
import io.flysium.helper.IDocumentToMap;
import io.flysium.helper.dto.DiffResult;
import io.flysium.helper.dto.EnResult;
import io.flysium.helper.utils.FileUtils;
import io.flysium.helper.utils.TextUtils;

/**
 * 神界：原罪2 汉化调整
 * 
 * @author SvenAugustus
 */
public class DOS2zhCNMain {

  private static ICountLetter countLetter = new DOS2CountLetter();
  private static IDocumentToMap documentToMap = new DOS2ToMap();
  private static IDocumentReplace documentReplace = new DOS2Replace();
  private static IDocumentExtract documentExtract = new DOS2Extract(documentToMap);

  public static void main(String[] args) throws Exception {
    /**
     * 参数定义
     */
    // 写入新的 english_1.xml、english_2.xml、english_3.xml前是否备份原文件
    boolean backup = true;
    // 定义差异度超过 xx % 才会添加到增量文本中
    int autoMaxDiffereceLimit = 5;
    // 游戏初步汉化的文本，可以采用3dm汉化文本 http://bbs.3dmgame.com/thread-5280675-1-1.html
    String gameEnglishXML =
        "E:/app/Steam/steamapps/common/Divinity Original Sin 2/Data/Localization/English/english.xml";
    // 润色文本配置
    String newzhCNProperties = "D:/cache/DOS2zhCN/DOS2zhCN.properties";
    String newzhCNNameProperties = "D:/cache/DOS2zhCN/DOS2zhCN-name.properties";

    ///////// 主功能开关
    // 自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)
    boolean auto = true;

    ///////// 辅助功能开关
    // 比较3DM汉化组 和 ak00129 的 汉化差异数据
    boolean compareTwoDOS2XML = false;
    // 比较3dm两版汉化文本，分析英文占比及差异程度，如3.3和最新的3.5文本
    boolean compareTwoDOS2XMLAsENzhCN = false;
    // 比较3dm两版汉化文本，并将旧版存在的新版不存在的汉化文本输出为增量文本 english_1.xml
    boolean buildIncDOS2Xml_AsENzhCN = false;

    /**
     * 主功能：自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)
     */
    if (auto) {
      System.out.println(
          "=========自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)==========");
      main_buildIncDOS2Xml_Pro(gameEnglishXML, newzhCNProperties, newzhCNNameProperties, true,
          false, autoMaxDiffereceLimit, backup);
    }

    /**
     * 以下是辅助功能
     */
    /*
     * 比较3DM汉化组 和 ak00129 的 汉化差异数据
     * 
     * 来自 3DM汉化组制作 http://bbs.3dmgame.com/thread-5280675-1-1.html 来自 ak00129的个人优化版
     * http://bbs.3dmgame.com/thread-5653091-1-1.html
     */
    if (compareTwoDOS2XML) {
      System.out.println("=========比较3DM汉化组 和 ak00129 的 汉化差异数据==========");
      main_compareTwoDOS2XML(gameEnglishXML, "D:/svenfire/Downloads/english-a.xml", true);
      // main_compareTwoDOS2XML(gameEnglishXML, getFilePathInSameDir(gameEnglishXML,
      // "english_1.xml"),
      // true);
      // main_compareTwoDOS2XML(gameEnglishXML, getFilePathInSameDir(gameEnglishXML,
      // "english_2.xml"),
      // true);
      // main_compareTwoDOS2XML(gameEnglishXML, getFilePathInSameDir(gameEnglishXML,
      // "english_3.xml"),
      // true);
    }
    // 比较两版汉化文本，分析英文占比及差异程度，如3.3文本和最新的3.5文本
    if (compareTwoDOS2XMLAsENzhCN) {
      System.out.println("=========比较两版汉化文本，分析英文占比及差异程度，如3.3文本和最新的3.5文本==========");
      System.out.println("=========           左边是最新的3.5文本 | 右边是3.3文本           ==========");
      main_compareTwoDOS2XML_letterAndText("D:/svenfire/Downloads/english33.xml",
          "D:/svenfire/Downloads/english35.xml");
    }
    // 比较两版汉化文本，并将旧版存在的新版不存在的汉化文本输出为增量文本 english_1.xml， 使用3.3替换汉化程度低一些的3.5文本
    if (buildIncDOS2Xml_AsENzhCN) {
      System.out.println("=========比较两版汉化文本，并将旧版存在的新版不存在的汉化文本输出为增量文本 english_1.xml==========");
      main_buildIncDOS2Xml_letterAndText("D:/svenfire/Downloads/english33.xml", gameEnglishXML,
          true, backup);
    }
  }

  /**
   * 自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)
   * 
   * @throws Exception
   */
  private static void main_buildIncDOS2Xml_Pro(String xml, String newzhCNProperties,
      String newzhCNNameProperties, boolean printAll, boolean replaceSpace,
      int autoMaxDiffereceLimit, boolean backup) throws Exception {
    System.out.println("-------------------以下是english_2.xml（面板、技能的文本优化）-----------------------");
    /**
     * 读取游戏3dm汉化的初步english.xml文件
     */
    Document document = FileUtils.readDocument(xml);
    /**
     * 读取properties文件，里面包含新的优化的汉化文本映射
     */
    Map<String, String> pro = FileUtils.readProperties(newzhCNProperties);

    Document documentInc = DocumentHelper.parseText(document.asXML());
    /**
     * 替换DOS2神界原罪2的汉化文本为新的汉化文本
     */
    documentInc = documentReplace.replaceAll(documentInc, pro);
    String content = documentInc.asXML();
    documentInc = DocumentHelper.parseText(content);
    /**
     * 提取增量文本
     */
    documentInc =
        documentExtract.extractIncrement(document, documentInc, autoMaxDiffereceLimit, printAll);
    String english_2 = FileUtils.getFilePathInSameDir(xml, "english_2.xml");
    /**
     * 备份english_2.xml文件
     */
    if (backup)
      FileUtils.backup(english_2);
    /**
     * documentInc 写入新的DOS2神界原罪2文件,不需要xml头
     */
    FileUtils.writerDocumentToNewDOS2File(documentInc, english_2);

    System.out.println("\n\n\n-------------------以下是english_3.xml(名字统一文本)-----------------------");
    /**
     * 读取properties文件，里面包含统一名称
     */
    Map<String, String> proName = FileUtils.readProperties(newzhCNNameProperties);

    /**
     * 替换，名称统一
     */
    String contentToOne = document.asXML();
    Iterator<String> it = proName.keySet().iterator();
    while (it.hasNext()) {
      String name = (String) it.next();
      String newName = proName.get(name);
      if (newName != null && !"".equals(newName)) {
        contentToOne = contentToOne.replaceAll(name, newName);
      }
    }
    Document documentToOne = DocumentHelper.parseText(contentToOne);
    /**
     * 替换空格
     */
    if (replaceSpace)
      documentToOne = TextUtils.replaceSpace(documentToOne);
    /**
     * 提取增量文本
     */
    documentToOne =
        documentExtract.extractIncrement(document, documentToOne, autoMaxDiffereceLimit, printAll);
    String english_3 = FileUtils.getFilePathInSameDir(xml, "english_3.xml");
    /**
     * 备份english_3.xml文件
     */
    if (backup)
      FileUtils.backup(english_3);
    /**
     * documentToOne 写入新的DOS2神界原罪2文件,不需要xml头
     */
    FileUtils.writerDocumentToNewDOS2File(documentToOne, english_3);
  }


  /**
   * 比较 两个文件 的 汉化差异数据
   */
  private static List<DiffResult> main_compareTwoDOS2XML(String xml, String xml2, boolean printAll)
      throws Exception {
    Document document1 = FileUtils.readDocument(xml);
    Document document2 = FileUtils.readDocument(xml2);
    /**
     * 以第一个文本为比较基准，比较两版汉化文本，输出差异程度
     */
    List<DiffResult> list = TextUtils.diff(document1, document2, documentToMap, true, false, true);

    boolean first100 = true, first80 = true, first50 = true, first25 = true;
    for (DiffResult res : list) {
      if (first100 && res.getDifference() >= 1.0) {
        System.out.println("\n----------------------------差异超过100%的文本-----------------");
        first100 = false;
      }
      if (first80 && res.getDifference() < 1.0 && res.getDifference() > 0.8) {
        System.out.println("\n\n----------------------------差异超过80%的文本-----------------");
        first80 = false;
      }
      if (first50 && res.getDifference() <= 0.8 && res.getDifference() > 0.5) {
        System.out.println("\n\n----------------------------差异超过50%的文本-----------------");
        first50 = false;
      }
      if (first25 && res.getDifference() <= 0.5 && res.getDifference() > 0.25) {
        System.out.println("\n\n----------------------------差异超过25%的文本-----------------");
        first25 = false;
      }
      if (printAll) {
        System.out.println(res.getContentuid() + "=" + res.getText() + "|" + res.getNewText());
      } else
        System.out.println(res.getContentuid() + "=" + res.getNewText());
    }
    return list;
  }

  /**
   * 比较两版汉化文本，分析英文占比及差异程度，如3.3文本和最新的3.5文本
   * 
   * @param oldFile 旧版本汉化
   * @param newFile 新版本汉化
   */
  private static void main_compareTwoDOS2XML_letterAndText(String oldFile, String newFile)
      throws Exception {
    Document docOld = FileUtils.readDocument(oldFile);
    Document docNew = FileUtils.readDocument(newFile);
    /**
     * 比较两版汉化文本，分析英文占比及差异程度，其中差异过小的过滤
     */
    List<EnResult> list = compareTwoDOS2XML_letterAndText(docOld, docNew, documentToMap, true);
    /**
     * 分析文本
     */
    int count = 0;
    boolean first100 = true, first80 = true, first50 = true, first25 = true, first0 = true;
    for (EnResult res : list) {
      if (first100 && res.getNewf() >= 1.0) {
        System.out.println("\n\n----------------------------英文字母占比超过100%的文本-----------------");
        first100 = false;
      }
      if (first80 && res.getNewf() < 1.0 && res.getNewf() > 0.8) {
        System.out.println("\n\n----------------------------英文字母占比超过80%的文本-----------------");
        first80 = false;
      }
      if (first50 && res.getNewf() <= 0.8 && res.getNewf() > 0.5) {
        System.out.println("\n\n----------------------------英文字母占比超过50%的文本-----------------");
        first50 = false;
      }
      if (first25 && res.getNewf() <= 0.5 && res.getNewf() > 0.25) {
        System.out.println("\n\n----------------------------英文字母占比超过25%的文本-----------------");
        first25 = false;
      }
      if (first0 && res.getNewf() < 0.25) {
        System.out.println("\n\n----------------------------英文字母占比小于25%的文本-----------------");
        first0 = false;
      }
      count++;
      System.out.println(res.getContentuid() + "=" + res.getNewf() + "|" + res.getText());
    }
    System.out.println("最大差异项：" + count);
  }

  /**
   * 比较两版汉化文本，并将旧版存在的新版不存在的汉化输出为增量文本english_1.xml， 如使用3.3替换汉化程度低一些的3.4文本
   * 
   * @param oldFile 旧版本汉化
   * @param newFile 新版本汉化
   * @throws Exception
   */
  private static void main_buildIncDOS2Xml_letterAndText(String oldFile, String newFile,
      boolean printAll, boolean backup) throws Exception {
    Document docOld = FileUtils.readDocument(oldFile);
    Document docNew = FileUtils.readDocument(newFile);
    /**
     * 比较两版汉化文本，分析英文占比及差异程度，其中差异过小的过滤
     */
    List<EnResult> list = compareTwoDOS2XML_letterAndText(docOld, docNew, documentToMap, true);
    /**
     * 提取增量文本
     */
    Document docInc = documentExtract.extractIncrement(list, false, true);
    /**
     * 生成同目录的某文件名的绝对路径
     */
    String english_1 = FileUtils.getFilePathInSameDir(newFile, "english_1.xml");
    /**
     * 备份english_1.xml文件
     */
    if (backup)
      FileUtils.backup(english_1);
    /**
     * 写入english_1.xml
     */
    FileUtils.writerDocumentToNewDOS2File(docInc, english_1);
  }

  /**
   * 以新文本为比较基准，比较两版汉化文本，分析英文占比及差异程度，其中差异过小的过滤
   * 
   * @param docOld
   * @param docNew
   * @param order 是否排序
   * @return
   */
  private static List<EnResult> compareTwoDOS2XML_letterAndText(Document docOld, Document docNew,
      IDocumentToMap document2Map, boolean order) {
    List<EnResult> list = new LinkedList<EnResult>();

    Map<String, String> mOld = document2Map.toMap(docOld);

    // 获取根节点元素对象
    Element root = docNew.getRootElement();
    // 子节点
    List<Element> es = root.elements();
    // 使用递归
    Iterator<Element> iterator = es.iterator();
    while (iterator.hasNext()) {
      Element e = iterator.next();
      Attribute attribute = e.attribute("contentuid");
      String contentuid = attribute.getValue();
      String textNew = e.getText();
      String textOld = (String) mOld.get(contentuid);
      if (textOld == null || "".equals(textOld)) {
        continue;
      }
      /**
       * 文本有差异，才处理
       */
      if (textOld.equals(textNew)) {
        continue;
      }
      /**
       * 计算英文字母占比
       */
      double newf = countLetter.countLetter(textNew);
      if (newf > 0) {
        /**
         * 计算英文字母占比
         */
        double oldf = countLetter.countLetter(textOld);
        if (oldf >= newf) {
          continue;
        }
        if (oldf > 0.7) {
          continue;
        }
        if (newf < 0.25) {
          continue;
        }
        /**
         * 计算新旧文本的差异程度
         */
        double distance = TextDiff.minDistance(textOld, textNew) * 1.0 / textNew.length();
        if (distance < 0.25 && newf < 0.5) {
          continue;
        }
        if (distance < 0.2) {
          continue;
        }
        list.add(new EnResult(oldf, newf, contentuid, textOld, textNew));
      }
    }
    System.out.println("\n\n差异项：" + list.size());
    /**
     * 倒序比较
     */
    if (order) {
      Collections.sort(list, new Comparator<EnResult>() {

        @Override
        public int compare(EnResult o1, EnResult o2) {
          int i = (int) ((o2.getNewf() - o1.getNewf()) * 100);// 倒序比较
          if (i == 0) {
            i = o2.getNewText().length() - o1.getNewText().length();
            if (i == 0) {
              return o2.getNewText().compareTo(o1.getNewText());
            }
          }
          return i;
        }
      });
    }
    return list;
  }



}
