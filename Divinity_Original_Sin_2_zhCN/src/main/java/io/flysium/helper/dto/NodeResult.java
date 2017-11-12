package io.flysium.helper.dto;

/**
 * 
 * 节点数据
 * 
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class NodeResult {

  protected String contentuid;// contentuid节点值
  protected String text; // 旧文本
  protected String newText;// 新文本

  public NodeResult(String contentuid, String text, String newText) {
    this.contentuid = contentuid;
    this.text = text;
    this.newText = newText;
  }

  public String getContentuid() {
    return contentuid;
  }

  public String getText() {
    return text;
  }

  public String getNewText() {
    return newText;
  }
};
