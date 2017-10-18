package io.flysium.algorithm;

import io.flysium.algorithm.text.EditDistance;
import io.flysium.algorithm.text.SmithWaterman;

import java.io.IOException;

/**
 * @author Sven Augustus
 */
public class TextDiff {

	private TextDiff() {
	}

	/**
	 * 计算两个长度差不多的字符串的差距，距离表示从一个字符串最少改几个字符能变成另一个。
	 * 越小越相近。适用任意两个字符串的比较。
	 *
	 * @param s1 第一个字符串
	 * @param s2 第二个字符串
	 * @return 距离
	 */
	public static int minDistance(String s1, String s2) {
		return EditDistance.minDistance(s1, s2);
	}

	/**
	 * Smith-Waterman算法寻找两个字符串中匹配度最高的子串
	 *
	 * @param s1 第一个字符串
	 * @param s2 第二个字符串
	 * @return 匹配度最高的子串
	 */
	public static SmithWaterman.SeqResult findSimilarSubSequences(String s1, String s2) {
		return SmithWaterman.find(s1, s2);
	}

	public static void main(String[] args) throws IOException {
		String s1 = "哈哈，你好啊Xia";
		String s2 = "哈哈，您好啊Sven";
		System.out.println("distance = " + minDistance(s1, s2));
		SmithWaterman.SeqResult res = findSimilarSubSequences(s1, s2);
		System.out.println("The subSequences with greatest similarity are "
				+ res.getSubSq1() + " and " + res.getSubSq2());
	}

}
