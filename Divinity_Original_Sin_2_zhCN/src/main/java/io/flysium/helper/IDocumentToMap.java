package io.flysium.helper;

import java.util.Map;
import org.dom4j.Document;

/**
 * xml提取文本
 * 
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public interface IDocumentToMap {

  /**
   * 提取转换为Map
   * 
   * @param doc
   * @return
   */
  public Map toMap(Document doc);

}
