package io.flysium.game.dos2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import io.flysium.helper.IDocumentToMap;
import io.flysium.helper.IDocumentExtract;
import io.flysium.helper.dto.DiffResult;
import io.flysium.helper.dto.NodeResult;
import io.flysium.helper.utils.TextUtils;

/**
 * 神界：原罪2 xml提取增量文本
 * 
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class DOS2Extract implements IDocumentExtract {

  private final IDocumentToMap documentToMap;

  public DOS2Extract(IDocumentToMap documentToMap) {
    super();
    this.documentToMap = documentToMap;
  }

  /**
   * 从两个xml中提取增量文本xml
   * 
   * @param document
   * @param documentInc
   * @param autoMaxDiffereceLimit 定义差异度超过 xx % 才会添加到增量文本中
   * @param printAll 是否打印结果
   * @return
   * @throws Exception
   */
  @Override
  public Document extractIncrement(Document document, Document documentInc,
      int autoMaxDiffereceLimit, boolean print) throws Exception {
    if (autoMaxDiffereceLimit > 0) {
      double f = autoMaxDiffereceLimit * 1.0 / 100.0;
      List<DiffResult> list =
          TextUtils.diff(documentInc, document, documentToMap, true, false, false);
      List<DiffResult> result = new ArrayList<DiffResult>(list.size());

      for (DiffResult res : list) {
        if (res.getDifference() >= f) {
          result.add(res);
        }
      }
      return extractIncrement(result, false, print);
    }
    Map<String, String> mOri = documentToMap.toMap(document);
    // 获取根节点元素对象
    Element root = documentInc.getRootElement();
    // 子节点
    List<Element> es = root.elements();
    // 使用递归
    Iterator<Element> iterator = es.iterator();
    while (iterator.hasNext()) {
      Element e = iterator.next();
      Attribute attribute = e.attribute("contentuid");
      // System.out.println("属性"+attribute.getName() +":" + attribute.getValue());
      String contentuid = attribute.getValue();
      String newText = e.getText().trim();
      String text = (String) mOri.get(contentuid);
      if (("".equals(text) && "".equals(newText)) || (text.trim().equals(newText))) {
        e.detach();
      } else {
        if (print)
          System.out.println(contentuid + "=" + text + "|" + newText);
      }
    }

    return documentInc;
  }

  /**
   * 根据节点数据，生成增量文本xml
   * 
   * @param list 节点数据
   * @param buildNew 替换新文本
   * @param printAll 是否打印结果
   * @return
   * @throws Exception
   */
  @Override
  public Document extractIncrement(List<? extends NodeResult> list, boolean buildNew, boolean print)
      throws Exception {
    Element root = DocumentHelper.createElement("contentList");
    Iterator<?> it = list.iterator();
    boolean first100 = true, first80 = true, first50 = true, first25 = true, first15 = true,
        first5 = true, first = true;
    if (print) {
      if (buildNew) {
        System.out.println("----------------------------左边是原文本，右边是增量文本-----------------");
      } else {
        System.out.println("----------------------------左边是增量文本，右边是原文本-----------------");
      }
    }
    while (it.hasNext()) {
      NodeResult nodeResult = (NodeResult) it.next();
      Element e = DocumentHelper.createElement("content");
      e.addAttribute("contentuid", nodeResult.getContentuid());
      e.setText(buildNew ? nodeResult.getNewText() : nodeResult.getText());
      if (print) {
        if (nodeResult instanceof DiffResult) {
          DiffResult res = (DiffResult) nodeResult;

          if (first100 && res.getDifference() >= 1.0) {
            System.out.println("----------------------------差异超过100%的文本-----------------");
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
          if (first15 && res.getDifference() <= 0.25 && res.getDifference() > 0.15) {
            System.out.println("\n\n----------------------------差异超过15%的文本-----------------");
            first15 = false;
          }
          if (first5 && res.getDifference() <= 0.15 && res.getDifference() > 0.05) {
            System.out.println("\n\n----------------------------差异超过5%的文本-----------------");
            first5 = false;
          }
          if (first && res.getDifference() < 0.05) {
            System.out.println("\n\n----------------------------差异低于5%的文本-----------------");
            first = false;
          }
        }
        System.out.println(nodeResult.getContentuid() + "="
            + (buildNew ? (nodeResult.getText() + "|" + nodeResult.getNewText())
                : (nodeResult.getNewText() + "|" + nodeResult.getText())));
      }
      root.add(e);
    }
    Document doc = DocumentHelper.createDocument(root);
    return doc;
  }

}
