package io.flysium.game.dos2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import io.flysium.helper.IDocumentToMap;

/**
 * 神界：原罪2 xml提取文本
 * 
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class DOS2ToMap implements IDocumentToMap {

  @Override
  public Map toMap(Document document) {
    Map<String, String> m = new HashMap<String, String>();
    /**
     * <contentList> <content contentuid=
     * "h00007224gb454g4b8bgb762g7865d9ee3dbb">如果不是这样的话！哈，开玩笑啦，我们在一起很合适，就像面包上的果酱。你想和我组队吗？我敢说你们需要一点野兽风味！</content>
     * <content contentuid="h0001d8b9g13d6g4605g85e9g708fe1e537c8">定制</content> </contentList>
     */
    // 获取根节点元素对象
    Element root = document.getRootElement();
    // 子节点
    List<Element> list = root.elements();
    // 使用递归
    Iterator<Element> iterator = list.iterator();
    while (iterator.hasNext()) {
      Element e = iterator.next();
      Attribute attribute = e.attribute("contentuid");
      m.put(attribute.getValue(), e.getText());
    }
    return m;
  }


}
