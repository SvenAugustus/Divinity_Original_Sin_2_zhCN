package io.flysium.helper;

import java.util.List;
import org.dom4j.Document;
import io.flysium.helper.dto.NodeResult;

/**
 * xml提取增量文本
 * 
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public interface IDocumentExtract {

  /**
   * 从两个xml中提取增量文本xml
   * 
   * @param document
   * @param documentInc
   * @param autoMaxDiffereceLimit 定义差异度超过 xx % 才会添加到增量文本中
   * @param print 是否打印结果
   * @return
   * @throws Exception
   */
  public Document extractIncrement(Document document, Document documentInc,
      int autoMaxDiffereceLimit, boolean print) throws Exception;


  /**
   * 根据节点数据，生成增量文本xml
   * 
   * @param list 节点数据
   * @param buildNew 替换新文本
   * @param print 是否打印结果
   * @return
   * @throws Exception
   */
  public Document extractIncrement(List<? extends NodeResult> list, boolean buildNew, boolean print)
      throws Exception;


}
