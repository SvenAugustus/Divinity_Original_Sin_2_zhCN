package io.flysium.helper.dto;

/**
 * 
 * 差异结果
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class DiffResult extends NodeResult {

  private double difference = 0f;// 新旧文本差异百分比

  public DiffResult(double difference, String contentuid, String text, String newText) {
    super(contentuid, text, newText);
    this.difference = difference;
  }

  public double getDifference() {
    return difference;
  }
};
