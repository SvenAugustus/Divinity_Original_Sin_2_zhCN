package io.flysium.sven.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import io.flysium.algorithm.TextDiff;

/**
 * DOS神界原罪2：汉化优化
 * 
 * @author SvenAugustus
 */
public class DOS2zhCNTest {

	// 游戏初步汉化的文本，可以采用3dm汉化文本 http://bbs.3dmgame.com/thread-5280675-1-1.html
	private static final String gameEnglishXML = "E:/app/Steam/steamapps/common/Divinity Original Sin 2/Data/Localization/English/english.xml";
	// 润色文本配置
	private static final String newzhCNProperties = "D:/cache/DOS2zhCN/DOS2zhCN.properties";
	private static final String newzhCNNameProperties = "D:/cache/DOS2zhCN/DOS2zhCN-name.properties";

	public static void main(String[] args) throws Exception {
		boolean compareTwoDOS2XMLAsENzhCN = false;//比较3dm两版汉化文本，分析英文占比及差异程度，如3.3和最新的3.5文本
		boolean buildIncDOS2Xml_AsENzhCN = false;//比较3dm两版汉化文本，并将旧版存在的新版不存在的汉化文本输出为增量文本 english_1.xml
		boolean compareTwoDOS2XML = false;//比较3DM汉化组 和 ak00129 的 汉化差异数据
		boolean auto = true;//自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)
		/**
		 * 比较两版汉化文本，分析英文占比及差异程度，如3.3文本和最新的3.5文本
		 */
		if (compareTwoDOS2XMLAsENzhCN) {
			System.out.println(
					"=========比较两版汉化文本，分析英文占比及差异程度，如3.3文本和最新的3.5文本==========");
			System.out.println(
					"=========           左边是最新的3.5文本 | 右边是3.3文本           ==========");
			main_compareTwoDOS2XML_letterAndText(
					"D:/svenfire/Downloads/english33.xml", gameEnglishXML);
		}
		/**
		 * 比较两版汉化文本，并将旧版存在的新版不存在的汉化文本输出为增量文本 english_1.xml，
		 * 		使用3.3替换汉化程度低一些的3.5文本 
		 */
		if (buildIncDOS2Xml_AsENzhCN) {
			System.out.println(
					"=========比较两版汉化文本，并将旧版存在的新版不存在的汉化文本输出为增量文本 english_1.xml==========");
			main_buildIncDOS2Xml_letterAndText(
					"D:/svenfire/Downloads/english33.xml", gameEnglishXML,
					true);
		}
		/**
		 * 比较3DM汉化组 和 ak00129 的 汉化差异数据
		 * 
		 * 来自  3DM汉化组制作  http://bbs.3dmgame.com/thread-5280675-1-1.html
		 * 来自  ak00129的个人优化版  http://bbs.3dmgame.com/thread-5653091-1-1.html
		 */
		if (compareTwoDOS2XML) {
			System.out
					.println("=========比较3DM汉化组 和 ak00129 的 汉化差异数据==========");
			main_compareTwoDOS2XML(gameEnglishXML,
					"D:/svenfire/Downloads/english-a.xml", true);
		}
		//main_compareTwoDOS2XML(gameEnglishXML, getFilePathInSameDir(gameEnglishXML, "english_1.xml"), true);
		//main_compareTwoDOS2XML(gameEnglishXML, getFilePathInSameDir(gameEnglishXML, "english_2.xml"), true);
		//main_compareTwoDOS2XML(gameEnglishXML, getFilePathInSameDir(gameEnglishXML, "english_3.xml"), true);
		/**
		 * 自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)
		 */
		if (auto) {
			System.out.println(
					"=========自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)==========");
			main_buildIncDOS2Xml_Pro(gameEnglishXML, newzhCNProperties,
					newzhCNNameProperties, true, false);
		}
	}

	/**
	 * 比较两版汉化文本，分析英文占比及差异程度，如3.3文本和最新的3.5文本
	 * 
	 * @param oldFile 旧版本汉化
	 * @param newFile 新版本汉化
	 */
	public static void main_compareTwoDOS2XML_letterAndText(String oldFile,
			String newFile) throws Exception {
		Document docOld = readDocument(oldFile);
		Document docNew = readDocument(newFile);
		/**
		 * 比较两版汉化文本，分析英文占比及差异程度，其中差异过小的过滤
		 */
		List<EnResult> list = compareTwoDOS2XML_letterAndText(docOld, docNew,
				true);
		/**
		 * 分析文本
		 */
		int count = 0;
		boolean first100 = true, first80 = true, first50 = true, first25 = true,
				first0 = true;
		for (EnResult res : list) {
			if (first100 && res.newf >= 1.0) {
				System.out.println(
						"\n\n----------------------------英文字母占比超过100%的文本-----------------");
				first100 = false;
			}
			if (first80 && res.newf < 1.0 && res.newf > 0.8) {
				System.out.println(
						"\n\n----------------------------英文字母占比超过80%的文本-----------------");
				first80 = false;
			}
			if (first50 && res.newf <= 0.8 && res.newf > 0.5) {
				System.out.println(
						"\n\n----------------------------英文字母占比超过50%的文本-----------------");
				first50 = false;
			}
			if (first25 && res.newf <= 0.5 && res.newf > 0.25) {
				System.out.println(
						"\n\n----------------------------英文字母占比超过25%的文本-----------------");
				first25 = false;
			}
			if (first0 && res.newf < 0.25) {
				System.out.println(
						"\n\n----------------------------英文字母占比小于25%的文本-----------------");
				first0 = false;
			}
			count++;
			//System.out.println(distance + " " + res.newf + " " + res.contentuid
			//		+ "=" + res.newText + "|" + res.text);
			System.out.println(
					res.contentuid + "=" + res.newText + "|" + res.text);
		}
		System.out.println("最大差异项：" + count);
	}

	/**
	 * 比较两版汉化文本，并将旧版存在的新版不存在的汉化输出为增量文本english_1.xml，
	 *  如使用3.3替换汉化程度低一些的3.4文本
	 * 
	 * @param oldFile 旧版本汉化
	 * @param newFile 新版本汉化
	 * @throws Exception
	 */
	public static void main_buildIncDOS2Xml_letterAndText(String oldFile,
			String newFile, boolean printAll) throws Exception {
		Document docOld = readDocument(oldFile);
		Document docNew = readDocument(newFile);
		/**
		 * 比较两版汉化文本，分析英文占比及差异程度，其中差异过小的过滤
		 */
		List<EnResult> list = compareTwoDOS2XML_letterAndText(docOld, docNew,
				true);
		/**
		 * 提取增量文本
		 */
		Document docInc = extractIncrement(list, false, true);
		/**
		 * 生成同目录的某文件名的绝对路径
		 */
		String english_1 = getFilePathInSameDir(newFile, "english_1.xml");
		/**
		 * 备份english_1.xml文件
		 */
		backup(english_1);
		/**
		 * 写入english_1.xml
		 */
		writerDocumentToNewDOS2File(docInc, english_1);
	}

	/**
	 * 比较 两个文件 的 汉化差异数据
	 */
	public static void main_compareTwoDOS2XML(String xml, String xml2,
			boolean printAll) throws Exception {
		Document document1 = readDocument(xml);
		Document document2 = readDocument(xml2);
		/**
		 * 以第一个文本为比较基准，比较两版汉化文本，输出差异程度
		 */
		List<DiffResult> list = compareTwoDOS2XML_text(document1, document2,
				true);

		boolean first100 = true, first80 = true, first50 = true, first25 = true;
		for (DiffResult res : list) {
			if (first100 && res.difference >= 1.0) {
				System.out.println(
						"\n\n----------------------------差异超过100%的文本-----------------");
				first100 = false;
			}
			if (first80 && res.difference < 1.0 && res.difference > 0.8) {
				System.out.println(
						"\n\n----------------------------差异超过80%的文本-----------------");
				first80 = false;
			}
			if (first50 && res.difference <= 0.8 && res.difference > 0.5) {
				System.out.println(
						"\n\n----------------------------差异超过50%的文本-----------------");
				first50 = false;
			}
			if (first25 && res.difference <= 0.5 && res.difference > 0.25) {
				System.out.println(
						"\n\n----------------------------差异超过25%的文本-----------------");
				first25 = false;
			}
			//System.out.println(res.difference + "," + res.contentuid + "=" + res.text + "|" + res.newText);
			if (printAll) {
				//if(res.newText.length()<30)
				System.out.println(
						res.contentuid + "=" + res.text + "|" + res.newText);

			} else
				System.out.println(res.contentuid + "=" + res.newText);
		}
	}

	/**
	 * 自动化优化个人的汉化文本,输出增量文本english_2.xml（面板、技能的文本优化） english_3.xml(名字统一文本)
	 * 
	 * @throws Exception
	 */
	public static void main_buildIncDOS2Xml_Pro(String xml,
			String newzhCNProperties, String newzhCNNameProperties,
			boolean printAll, boolean replaceSpace) throws Exception {
		/**
		 * 读取游戏3dm汉化的初步english.xml文件
		 */
		Document document = readDocument(xml);
		/**
		 * 读取properties文件，里面包含新的优化的汉化文本映射
		 */
		//Properties pro = readProperties(newzhCNProperties);
		Map<String, String> pro = readProperties(newzhCNProperties);

		Document documentInc = DocumentHelper.parseText(document.asXML());
		/**
		 * 替换DOS2神界原罪2的汉化文本为新的汉化文本
		 */
		documentInc = replaceDOS2(documentInc, pro);
		String content = documentInc.asXML();
		documentInc = DocumentHelper.parseText(content);
		/**
		 * 提取增量文本
		 */
		documentInc = extractIncrement(document, documentInc, printAll);
		String english_2 = getFilePathInSameDir(xml, "english_2.xml");
		/**
		 * 备份english_2.xml文件
		 */
		backup(english_2);
		/**
		 * documentInc 写入新的DOS2神界原罪2文件,不需要xml头
		 */
		writerDocumentToNewDOS2File(documentInc, english_2);

		System.out.println("\n\n\n------------------------------------------");
		/**
		 * 读取properties文件，里面包含统一名称
		 */
		//Properties proName = readProperties(newzhCNNameProperties);
		Map<String, String> proName = readProperties(newzhCNNameProperties);

		/**
		 * 替换，名称统一
		 */
		String contentToOne = document.asXML();
		Iterator<String> it = proName.keySet().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			//String newName = proName.getProperty(name);
			String newName = proName.get(name);
			if (newName != null && !"".equals(newName)) {
				contentToOne = contentToOne.replaceAll(name, newName);
			}
		}
		Document documentToOne = DocumentHelper.parseText(contentToOne);
		/**
		 * 替换空格
		 */
		if (replaceSpace)
			documentToOne = replaceSpace(documentToOne);
		/**
		 * 提取增量文本
		 */
		documentToOne = extractIncrement(document, documentToOne, printAll);
		String english_3 = getFilePathInSameDir(xml, "english_3.xml");
		/**
		 * 备份english_3.xml文件
		 */
		backup(english_3);
		/**
		 * documentToOne 写入新的DOS2神界原罪2文件,不需要xml头
		 */
		writerDocumentToNewDOS2File(documentToOne, english_3);
	}

	/**
	 * 以新文本为比较基准，比较两版汉化文本，分析英文占比及差异程度，其中差异过小的过滤
	 * 
	 * @param docOld
	 * @param docNew
	 * @param order 是否排序
	 * @return
	 */
	private static List<EnResult> compareTwoDOS2XML_letterAndText(
			Document docOld, Document docNew, boolean order) {
		List<EnResult> list = new LinkedList<EnResult>();

		Map<String, String> mOld = DOS2toMap(docOld);

		//获取根节点元素对象  
		Element root = docNew.getRootElement();
		//子节点
		List<Element> es = root.elements();
		//使用递归  
		Iterator<Element> iterator = es.iterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			Attribute attribute = e.attribute("contentuid");
			//System.out.println("属性"+attribute.getName() +":" + attribute.getValue());  
			String contentuid = attribute.getValue();
			String textNew = e.getText();
			String textOld = (String) mOld.get(contentuid);
			if (textOld == null || "".equals(textOld)) {
				continue;
			}
			/**
			 * 文本有差异，才处理
			 */
			if (textOld.equals(textNew)) {
				continue;
			}
			/**
			 * 计算英文字母占比
			 */
			double newf = countLetter(textNew);
			if (newf > 0) {
				/**
				 * 计算英文字母占比
				 */
				double oldf = countLetter(textOld);
				if (oldf > newf) {
					continue;
				}
				if (newf < 0.25) {
					continue;
				}
				/**
				 * 计算新旧文本的差异程度
				 */
				double distance = TextDiff.minDistance(textOld, textNew) * 1.0
						/ textNew.length();
				if (distance < 0.25 && newf < 0.5) {
					continue;
				}
				list.add(
						new EnResult(oldf, newf, contentuid, textOld, textNew));
			}
		}
		System.out.println("\n\n差异项：" + list.size());
		/**
		 * 倒序比较
		 */
		if (order) {
			Collections.sort(list, new Comparator<EnResult>() {

				@Override
				public int compare(EnResult o1, EnResult o2) {
					int i = (int) ((o2.newf - o1.newf) * 100);// 倒序比较
					if (i == 0) {
						i = o2.getNewText().length() - o1.getNewText().length();
						if (i == 0) {
							return o2.getNewText().compareTo(o1.getNewText());
						}
					}
					return i;
				}
			});
		}
		return list;
	}

	/**
	 * 计算英文字母占比
	 */
	private static double countLetter(String s) {
		String temp = new String(s);
		temp = temp.replace("<font", "");
		temp = temp.replace("</font>", "");
		temp = temp.replace("<br>", "");
		temp = temp.replace(
				"[GEN_CheckMagicPocketGold_6057ad05-9492-4630-9f0a-be548b134c54]",
				"");
		double f = 0f;
		for (int i = 0; i < temp.length(); i++) {
			if ((temp.charAt(i) >= 'a' && temp.charAt(i) <= 'z')
					|| (temp.charAt(i) >= 'A' && temp.charAt(i) <= 'Z')) {
				f += 1;
			}
		}
		return f / temp.length();
	}

	/**
	 * 以第一个文本为比较基准，比较两版汉化文本，输出差异程度
	 * 
	 * @param doc1
	 * @param doc2
	 * @param order 是否排序
	 * @return
	 */
	private static List<DiffResult> compareTwoDOS2XML_text(Document doc1,
			Document doc2, boolean order) {
		List<DiffResult> list = new LinkedList<DiffResult>();

		Map<String, String> m1 = DOS2toMap(doc1);
		Map<String, String> m2 = DOS2toMap(doc2);
		System.out.println("项目数：" + m1.keySet().size());
		System.out.println("项目数：" + m2.keySet().size());

		for (Map.Entry<String, String> e : m1.entrySet()) {
			String contentuid = e.getKey();
			String text1 = (String) m1.get(contentuid);
			String text2 = (String) m2.get(contentuid);
			if (text1 == null || "".equals(text1)) {
				continue;
			}
			if (text2 == null || "".equals(text2)) {
				continue;
			}
			/**
			 * 文本有差异，才处理
			 */
			if (text1.equals(text2)) {
				continue;
			}
			/**
			 * 计算新旧文本的差异程度
			 */
			int distance = TextDiff.minDistance(text1, text2);

			list.add(new DiffResult((distance * 1.0) / text1.length(),
					contentuid, text1, text2));
		}
		System.out.println("\n\n差异项：" + list.size());
		/**
		 * 倒序比较
		 */
		if (order) {
			Collections.sort(list, new Comparator<DiffResult>() {

				@Override
				public int compare(DiffResult o1, DiffResult o2) {
					int i = (int) ((o2.difference - o1.difference) * 100);// 倒序比较
					if (i == 0) {
						return o2.getNewText().compareTo(o1.getNewText());
					}
					return i;
				}
			});
		}
		return list;
	}

	/**
	 * 
	 * 提取增量文本
	 * 
	 * @param list
	 * @param buildNew 替换新文本
	 * @param printAll 打印
	 * @return
	 */
	private static Document extractIncrement(List<? extends NodeResult> list,
			boolean buildNew, boolean printAll) {
		Element root = DocumentHelper.createElement("contentList");
		Iterator<?> it = list.iterator();
		while (it.hasNext()) {
			DOS2zhCNTest.NodeResult nodeResult = (DOS2zhCNTest.NodeResult) it
					.next();
			Element e = DocumentHelper.createElement("content");
			DocumentHelper.createAttribute(e, "contentuid",
					nodeResult.contentuid);
			e.setText(buildNew ? nodeResult.newText : nodeResult.text);
			if (printAll)
				System.out.println(nodeResult.contentuid + "=" + (buildNew
						? (nodeResult.text + "|" + nodeResult.newText)
						: (nodeResult.newText + "|" + nodeResult.text)));
			else
				System.out
						.println(nodeResult.contentuid + "="
								+ (buildNew
										? (nodeResult.newText)
										: (nodeResult.text)));
			root.add(e);
		}
		Document doc = DocumentHelper.createDocument(root);
		return doc;
	}

	/**
	 * 
	 * 提取增量文本
	 * 
	 * @param documentInc
	 * @param mOri
	 * @return
	 */
	private static Document extractIncrement(Document document,
			Document documentInc, boolean printAll) {
		Map<String, String> mOri = DOS2toMap(document);
		//获取根节点元素对象  
		Element root = documentInc.getRootElement();
		//子节点
		List<Element> es = root.elements();
		//使用递归  
		Iterator<Element> iterator = es.iterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			Attribute attribute = e.attribute("contentuid");
			//System.out.println("属性"+attribute.getName() +":" + attribute.getValue());  
			String contentuid = attribute.getValue();
			String newText = e.getText().trim();
			String text = (String) mOri.get(contentuid);
			if (("".equals(text) && "".equals(newText))
					|| (text.trim().equals(newText))) {
				e.detach();
			} else {
				if (printAll)
					System.out.println(contentuid + "=" + text + "|" + newText);
				else
					System.out.println(contentuid + "=" + newText);
			}
		}
		return documentInc;
	}

	/**
	 * 替换DOS2神界原罪2的汉化文本为新的汉化文本
	 * @throws UnsupportedEncodingException 
	 */
	private static Document replaceDOS2(Document document,
			/*Properties pro*/Map<String, String> pro)
			throws UnsupportedEncodingException {
		/**
			<contentList>
				<content contentuid="h00007224gb454g4b8bgb762g7865d9ee3dbb">如果不是这样的话！哈，开玩笑啦，我们在一起很合适，就像面包上的果酱。你想和我组队吗？我敢说你们需要一点野兽风味！</content>
				<content contentuid="h0001d8b9g13d6g4605g85e9g708fe1e537c8">定制</content>
			</contentList>
		*/
		//获取根节点元素对象  
		Element root = document.getRootElement();
		//子节点
		List<Element> list = root.elements();
		//使用递归  
		Iterator<Element> iterator = list.iterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			Attribute attribute = e.attribute("contentuid");
			//System.out.println("属性"+attribute.getName() +":" + attribute.getValue());  
			String contentuid = attribute.getValue();
			//String text = e.getText();
			String newText = (String) pro.get(contentuid);
			if (newText != null && !"".equals(newText)) {
				e.setText(newText);
				//System.out.println(contentuid + "=" + text + "|" + newText);
				//System.out.println(contentuid + "=" + newText);
			}
		}
		return document;
	}

	/**
	 * 替换空格等
	 * 
	 * @throws UnsupportedEncodingException 
	 */
	private static Document replaceSpace(Document document) {
		//获取根节点元素对象  
		Element root = document.getRootElement();
		//子节点
		List<Element> list = root.elements();
		//使用递归  
		Iterator<Element> iterator = list.iterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			String text = e.getText();
			if (text != null && !"".equals(text)) {
				text = text.replaceAll(" ", "");
				e.setText(text);
			}
		}
		return document;
	}

	/**
	 * DOS2神界原罪2的汉化文本转化为Map
	 */
	private static Map<String, String> DOS2toMap(Document document) {
		Map<String, String> m = new HashMap<String, String>();
		/**
			<contentList>
				<content contentuid="h00007224gb454g4b8bgb762g7865d9ee3dbb">如果不是这样的话！哈，开玩笑啦，我们在一起很合适，就像面包上的果酱。你想和我组队吗？我敢说你们需要一点野兽风味！</content>
				<content contentuid="h0001d8b9g13d6g4605g85e9g708fe1e537c8">定制</content>
			</contentList>
		*/
		//获取根节点元素对象  
		Element root = document.getRootElement();
		//子节点
		List<Element> list = root.elements();
		//使用递归  
		Iterator<Element> iterator = list.iterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			Attribute attribute = e.attribute("contentuid");
			m.put(attribute.getValue(), e.getText());
		}
		return m;
	}

	/**
	 * 读取properties文件
	 */
	private static Map<String, String> readProperties(
			String propertiesFilePAth) {
		Map<String, String> m = new HashMap<String, String>();
		//Properties pro = new Properties();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(propertiesFilePAth), "utf-8"));
			//in = new BufferedReader(new FileReader(propertiesFilePAth));
			String line = in.readLine();
			while (line != null) {
				String[] kv = line.split("=");
				if (kv != null && kv.length >= 2) {
					m.put(kv[0], kv[1].trim());
				}
				line = in.readLine();
			}
			//pro.load(in);
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		//return pro;
		return m;
	}

	/**
	 * 读取xml文件
	 */
	private static Document readDocument(String xmlFilePath) {
		//创建SAXReader对象  
		SAXReader reader = new SAXReader();
		//读取文件 转换成Document  
		Document document = null;
		try {
			document = reader.read(new File(xmlFilePath));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * document写入新的DOS2神界原罪2文件,不需要xml头
	 */
	private static void writerDocumentToNewDOS2File(Document document,

			String newFIlePath) throws Exception {
		if (document == null)
			return;
		XMLWriter writer = null;
		try {
			//输出格式  
			OutputFormat format = OutputFormat.createPrettyPrint();
			//设置编码  
			format.setEncoding("UTF-8");
			format.setTrimText(true);
			// 设置换行 为false时输出的xml不分行
			format.setNewlines(true);
			// 生成缩进 
			format.setIndent(true);
			// 指定使用tab键缩进
			format.setIndent("  ");
			// 不在文件头生成  XML 声明 (<?xml version="1.0" encoding="UTF-8"?>) 
			format.setSuppressDeclaration(true);
			// 不在文件头生成  XML 声明 (<?xml version="1.0" encoding="UTF-8"?>)中加入encoding 属性
			format.setOmitEncoding(true);
			//XMLWriter 指定输出文件以及格式 
			writer = new XMLWriter(new OutputStreamWriter(
					new FileOutputStream(new File(newFIlePath)), "UTF-8"),
					format);
			writer.write(document);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * 生成同目录的某文件名的绝对路径
	 * 
	 * @param xml
	 * @param filename
	 * @return
	 */
	private static String getFilePathInSameDir(String xml, String filename) {
		return new File(xml).getParent() + File.separator + filename;
	}

	/**
	 * 备份
	 * 
	 * @param xml
	 */
	private static void backup(String xml) {
		File f = new File(xml);
		File backupDir = new File(f.getParent() + File.separator + "backup");
		backupDir.mkdirs();

		f.renameTo(new File(backupDir.getAbsolutePath(), f.getName() + "."
				+ new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date())));
	}

	static class NodeResult {

		protected String contentuid;
		protected String text;
		protected String newText;

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
	static class DiffResult extends NodeResult {

		private double difference = 0f;

		public DiffResult(double difference, String contentuid, String text,
				String newText) {
			super(contentuid, text, newText);
			this.difference = difference;
		}
		public double getDifference() {
			return difference;
		}
	};
	static class EnResult extends NodeResult {

		private double oldf = 0f;
		private double newf = 0f;

		public EnResult(double oldf, double newf, String contentuid,
				String text, String newText) {
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

}
