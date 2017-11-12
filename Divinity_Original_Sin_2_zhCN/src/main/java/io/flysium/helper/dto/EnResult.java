package io.flysium.helper.dto;

/**
 * 英文占比结果
 * 
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class EnResult extends NodeResult {

  private double oldf = 0f;// 旧文本英文占比
  private double newf = 0f;// 新文本英文占比

  public EnResult(double oldf, double newf, String contentuid, String text, String newText) {
    super(contentuid, text, newText);
    this.oldf = oldf;
    this.newf = newf;
  }

  public double getNewf() {
    return newf;
  }

  public double getOldf() {
    return oldf;
  }
};
