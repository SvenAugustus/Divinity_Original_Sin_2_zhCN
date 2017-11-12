package io.flysium.helper.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * 文件工具类
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class FileUtils {


  /**
   * 读取xml文件
   */
  public static Document readDocument(String xmlFilePath) {
    // 创建SAXReader对象
    SAXReader reader = new SAXReader();
    // 读取文件 转换成Document
    Document document = null;
    try {
      document = reader.read(new File(xmlFilePath));
    } catch (DocumentException e) {
      e.printStackTrace();
    }
    return document;
  }

  /**
   * 读取properties文件
   */
  public static Map<String, String> readProperties(String propertiesFilePAth) {
    Map<String, String> m = new HashMap<String, String>();
    // Properties pro = new Properties();
    BufferedReader in = null;
    try {
      in = new BufferedReader(
          new InputStreamReader(new FileInputStream(propertiesFilePAth), "utf-8"));
      // in = new BufferedReader(new FileReader(propertiesFilePAth));
      String line = in.readLine();
      while (line != null) {
        String[] kv = line.split("=");
        if (kv != null && kv.length >= 2) {
          m.put(kv[0], kv[1].trim());
        }
        line = in.readLine();
      }
      // pro.load(in);
    } catch (Exception e2) {
      e2.printStackTrace();
    } finally {
      if (in != null)
        try {
          in.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
    }
    // return pro;
    return m;
  }

  /**
   * document写入新的DOS2神界原罪2文件,不需要xml头
   */
  public static void writerDocumentToNewDOS2File(Document document, String newFIlePath)
      throws Exception {
    if (document == null)
      return;
    XMLWriter writer = null;
    try {
      // 输出格式
      OutputFormat format = OutputFormat.createPrettyPrint();
      // 设置编码
      format.setEncoding("UTF-8");
      format.setTrimText(true);
      // 设置换行 为false时输出的xml不分行
      format.setNewlines(true);
      // 生成缩进
      format.setIndent(true);
      // 指定使用tab键缩进
      format.setIndent("  ");
      // 不在文件头生成 XML 声明 (<?xml version="1.0" encoding="UTF-8"?>)
      format.setSuppressDeclaration(true);
      // 不在文件头生成 XML 声明 (<?xml version="1.0" encoding="UTF-8"?>)中加入encoding 属性
      format.setOmitEncoding(true);
      // XMLWriter 指定输出文件以及格式
      writer = new XMLWriter(
          new OutputStreamWriter(new FileOutputStream(new File(newFIlePath)), "UTF-8"), format);
      writer.write(document);
      writer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (writer != null)
        writer.close();
    }
  }

  /**
   * 生成同目录的某文件名的绝对路径
   * 
   * @param xml
   * @param filename
   * @return
   */
  public static String getFilePathInSameDir(String xml, String filename) {
    return new File(xml).getParent() + File.separator + filename;
  }

  /**
   * 备份
   * 
   * @param xml
   */
  public static void backup(String xml) {
    File f = new File(xml);
    File backupDir = new File(f.getParent() + File.separator + "backup");
    backupDir.mkdirs();

    f.renameTo(new File(backupDir.getAbsolutePath(),
        f.getName() + "." + new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date())));
  }


}
