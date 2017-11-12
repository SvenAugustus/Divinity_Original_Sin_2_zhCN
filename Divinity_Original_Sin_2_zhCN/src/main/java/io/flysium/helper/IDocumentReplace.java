package io.flysium.helper;

import java.util.Map;
import org.dom4j.Document;

/**
 * xml替换文本
 * 
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public interface IDocumentReplace {

  /**
   * 替换文本
   * 
   * @param document
   * @param pro 替换配置
   * @return
   */
  public Document replaceAll(Document document, Map<String, String> pro);

}
