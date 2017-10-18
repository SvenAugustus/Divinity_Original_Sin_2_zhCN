package io.flysium.algorithm.text;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 文本比较 - Smith Waterman算法
 *
 * @author Sven Augustus
 */
public class SmithWaterman {

	private static final int SPACE = -4; //空格匹配的得分
	private static final int MATCH = 3; //两个字母相同的得分
	private static final int UNMACH = -1; //两个字母不同的得分

	private SmithWaterman() {
	}

	/**
	 * Smith-Waterman算法寻找两个字符串中匹配度最高的子串
	 *
	 * @param s1 第一个字符串
	 * @param s2 第二个字符串
	 * @return 匹配度最高的子串
	 */
	public static SeqResult find(String s1, String s2) {
		//initMatrix(s1.length(), s2.length());
		Info info = new Info();
		int i, j;
		info.H = new int[s1.length() + 1][s2.length() + 1];
		info.isEmpty = new int[s1.length() + 1][s2.length() + 1];
		for (i = 0; i <= s1.length(); i++)
			for (j = 0; j <= s2.length(); j++)
				info.isEmpty[i][j] = 1;
		calculateMatrix(info, s1, s2, s1.length(), s2.length());
		findMaxIndex(info, info.H.length, info.H[0].length);
		traceBack(info, s1, s2, info.maxIndexM, info.maxIndexN);
		ArrayList<Character> arr1 = new ArrayList<Character>();
		ArrayList<Character> arr2 = new ArrayList<Character>();
		while (!info.stk1.empty())
			arr1.add(info.stk1.pop());
		while (!info.stk2.empty())
			arr2.add(info.stk2.pop());
		return new SeqResult(arr1.toString(), arr2.toString());
	}

	private static int max(int a, int b, int c) {
		return Math.max(Math.max(Math.max(a, b), c), 0);
	}

	private static void calculateMatrix(Info info, String s1, String s2, int m,
										int n) {//计算得分矩阵
		if (m == 0)
			info.H[m][n] = 0;
		else if (n == 0)
			info.H[m][n] = 0;
		else {
			if (info.isEmpty[m - 1][n - 1] == 1)
				calculateMatrix(info, s1, s2, m - 1, n - 1);
			if (info.isEmpty[m][n - 1] == 1)
				calculateMatrix(info, s1, s2, m, n - 1);
			if (info.isEmpty[m - 1][n] == 1)
				calculateMatrix(info, s1, s2, m - 1, n);
			if (s1.charAt(m - 1) == s2.charAt(n - 1))
				info.H[m][n] = max(info.H[m - 1][n - 1] + MATCH,
						info.H[m][n - 1] + SPACE, info.H[m - 1][n] + SPACE);
			else
				info.H[m][n] = max(info.H[m - 1][n - 1] + UNMACH,
						info.H[m][n - 1] + SPACE, info.H[m - 1][n] + SPACE);
		}
		info.isEmpty[m][n] = 0;
	}

	private static void findMaxIndex(Info info, int m, int n) {//找到得分矩阵H中得分最高的元组的下标
		int curM, curN, i, j, max;
		curM = 0;
		curN = 0;
		max = info.H[0][0];
		for (i = 0; i < m; i++)
			for (j = 0; j < n; j++)
				if (info.H[i][j] > max) {
					max = info.H[i][j];
					curM = i;
					curN = j;
				}
		info.maxIndexM = curM;
		info.maxIndexN = curN;
	}

	private static void traceBack(Info info, String s1, String s2, int m,
								  int n) {//回溯 寻找最相似子序列
		if (info.H[m][n] == 0)
			return;
		if (info.H[m][n] == info.H[m - 1][n] + SPACE) {
			info.stk1.add(s1.charAt(m - 1));
			info.stk2.add('-');
			traceBack(info, s1, s2, m - 1, n);
		} else if (info.H[m][n] == info.H[m][n - 1] + SPACE) {
			info.stk1.add('-');
			info.stk2.add(s2.charAt(n - 1));
			traceBack(info, s1, s2, m, n - 1);
		} else {
			info.stk1.push(s1.charAt(m - 1));
			info.stk2.push(s2.charAt(n - 1));
			traceBack(info, s1, s2, m - 1, n - 1);
		}
	}

	private static class Info { // 工作内存
		private int[][] H;
		private int[][] isEmpty;
		private int maxIndexM, maxIndexN;
		private Stack<Character> stk1, stk2;

		public Info() {
			super();
			stk1 = new Stack<Character>();
			stk2 = new Stack<Character>();
		}
	}

	public static class SeqResult { // 结果
		private String subSq1, subSq2; //相似度最高的两个子串

		public SeqResult(String subSq1, String subSq2) {
			super();
			this.subSq1 = subSq1;
			this.subSq2 = subSq2;
		}

		public String getSubSq1() {
			return subSq1;
		}

		public String getSubSq2() {
			return subSq2;
		}
	}


}
