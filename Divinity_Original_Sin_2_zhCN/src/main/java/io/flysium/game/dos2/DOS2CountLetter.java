package io.flysium.game.dos2;

import io.flysium.helper.ICountLetter;

/**
 * 
 * 神界：原罪2 计算英文字母占比
 *
 * Date：2017年11月12日
 * 
 * @author SvenAugustus
 * @version 1.0
 * @since JDK 1.7
 */
public class DOS2CountLetter implements ICountLetter {

  @Override
  public double countLetter(String s) {
    String temp = new String(s);
    temp = temp.replace("<font", "");
    temp = temp.replace("</font>", "");
    temp = temp.replace("<br>", "");
    temp = temp.replace("[GEN_CheckMagicPocketGold_6057ad05-9492-4630-9f0a-be548b134c54]", "");
    double f = 0f;
    for (int i = 0; i < temp.length(); i++) {
      if ((temp.charAt(i) >= 'a' && temp.charAt(i) <= 'z')
          || (temp.charAt(i) >= 'A' && temp.charAt(i) <= 'Z')) {
        f += 1;
      }
    }
    return f / temp.length();
  }

}
