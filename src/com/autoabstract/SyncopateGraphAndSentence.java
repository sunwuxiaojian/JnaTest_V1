package com.autoabstract;

import utils.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 1 读取文件内容 2 将内容保存到一个字符串中 3 将分割出段落 4 分割出句子
 * 
 * @author Lee
 * 
 */
public class SyncopateGraphAndSentence {
	private String sourceStr = "";// 存放从文件中得到的内容————一个字符串，格式为“段1\r\n段2……\r\n段n”

	private int[][] paragraphs;// 存放分段信息数组————为n行（段数）三列的数组，格式为：段号 段落起始位置 段落结束位置

	private int[][] sentences;// 存放分句信息数组————为n行（句数）四列的数组，格式为：段号 句号 句子起始位置 句子结束位置

	private String[] paragraphStr;// 存放分段内容————分割段落，paragraphStr[i]为第i+1段。但……原本这玩意儿用过？？被splitParagraph完美取代了……

	private String[] sentencesStr;// 存放分句内容————分割的句子，sentencesStr[i]为第i+1句

	//————构造函数
	public SyncopateGraphAndSentence(String filePath) {
		String sourceStr = FileUtil.readNewsFile(filePath);
		this.setSourceStr(sourceStr);
		/*String[] splitParagraph =*/ this.getParagraphs(this.getSourceStr());// 读取文件的时候用于分割段号—————按"\r\n"分割，splitParagraph[i]为第i+1段
		int[][] paragraphInfo = this.getParagraphsInfo(paragraphStr);//————原来用的splitParagraph
		this.setParagraphs(paragraphInfo);// 将分割出的段落信息存放到paragraphs对象中
		int[][] sentenceInfo = this.getSentencesInParagraph(paragraphStr);//————原来用的splitParagraph
		this.setSentences(sentenceInfo);// 将分割出的句子信息存放到sentences对象中
		//System.out.println("分段信息数组：" + this.getParagraphs().length);
		System.out.println("分段信息数组：" + this.getParagraphStr().length);//————同样可以
		System.out.println("分句内容数组：" + this.getSentencesStr().length);		
	}



	/**
	 * 将读取得到的字符串分割出段落内容
	 * 
	 * @param readStr
	 *            读取出的字符串内容
	 * @return 返回一个段落字符数组
	 */
	private String[] getParagraphs(String sourceStr) {
		String[] paragraphs = sourceStr.split("\r\n");
		this.setParagraphStr(paragraphs);
		return paragraphs;
	}

	/**
	 * 根据分割出的段落字符串定位其在sourceStr中的位置
	 * 
	 * @param splitParagraph
	 *            分割出的段落的内容
	 * @return 返回一个段落和原来内容的关系的数组
	 */
	private int[][] getParagraphsInfo(String[] splitParagraph) {
		// 用于存放分段信息
		// 格式为：段号 段起 段尾
		int[][] paragraphInfo = new int[splitParagraph.length][3];// 用三列矩阵表示分段信息
		String sourceStr = this.getSourceStr();
		for (int i = 0; i < paragraphInfo.length; i++) {
			int paragraphBegin = sourceStr.indexOf(splitParagraph[i]);// 得到每段的起始位置—————为什么可以这样写？出现完全相同的两段怎么办？
			//System.out.println("splitParagraph["+i+"]="+splitParagraph[i]);
			//System.out.println("splitParagraph["+i+"].length()="+splitParagraph[i].length());
			//System.out.println("sourceStr.indexOf(splitParagraph["+i+"])="+sourceStr.indexOf(splitParagraph[i]));
			int paragraphEnd = paragraphBegin + splitParagraph[i].length();// 得到每段的结束位置
			for (int j = 0; j < 3; j++) {
				if (j == 0) {
					paragraphInfo[i][j] = i + 1;// 段号
				} else if (j == 1) {
					paragraphInfo[i][j] = paragraphBegin;// 每段的起始位置
				} else {
					paragraphInfo[i][j] = paragraphEnd;// 每段的结束位置
				}
			}
		}
		return paragraphInfo;
	}

	/**
	 * 根据分割出的段落字符串定位其在sourceStr中的位置
	 * 
	 * @param splitParagraph
	 *            分割出的字符串
	 * @return 返回段落和句子关系的数组
	 */
	private int[][] getSentencesInParagraph(String[] splitParagraph) {
		// 用于存放分句信息
		// 格式： 段号 句号 句子起始位置 句子结束位置
		int[][] sentenceInfo = new int[getSentenceCount(splitParagraph)][4];// 用四列矩阵表示分句信息—————可否用动态数组，不用getSentenceCount？？？
		String sourceStr = this.getSourceStr();// 获得读取字符串的内容
		int sentenceIndex = 0;// 用于记录数组的下标
		StringBuffer sb = new StringBuffer();// 用于存放句子内容
		for (int i = 0; i < splitParagraph.length; i++) {
			String paragraphStr = splitParagraph[i];// 每段的字符串内容
			if (paragraphStr.endsWith("。") || paragraphStr.contains("。")) {// 包含半角句号的按照句号切分
				//String[] sentenceInParagraph = paragraphStr.split("。");
				String[] sentenceInParagraph = paragraphStr.split("\\。|\\？|\\！|\\?|\\!");
				for (int j = 0; j < sentenceInParagraph.length; j++) {
					int sentenceBegin = sourceStr
							.indexOf(sentenceInParagraph[j]);// 句子在sourceStr中的起始位置—————出现完全相同的两句话怎么办？
					//System.out.println("sentenceBegin="+sentenceBegin);
					int sentenceEnd = sentenceBegin
							+ sentenceInParagraph[j].length();// 句子在sourceStr中的结束位置
					//System.out.println("sentenceEnd="+sentenceEnd);
					String subStr = sourceStr.substring(sentenceBegin,
							sentenceEnd);
					//System.out.println("第"+(sentenceIndex+1)+"句subStr="+subStr);
					if (!subStr.trim().equals("")) {// —————句子不为空或者不只是包含空格才执行
						sb.append(subStr + "\r\n");// —————插入分段符，一句话为一段，便于将分出的句子存储在sentencesStr对象中
						for (int k = 0; k < 4; k++) {
							if (k == 0) {
								sentenceInfo[sentenceIndex][k] = i + 1;// 段号————从1开始
							} else if (k == 1) {
								sentenceInfo[sentenceIndex][k] = sentenceIndex + 1;// 句号————从1开始
							} else if (k == 2) {
								sentenceInfo[sentenceIndex][k] = sentenceBegin;// 句子起始位置
							} else {
								sentenceInfo[sentenceIndex][k] = sentenceEnd;// 句子结束位置
							}
						}
						sentenceIndex++;
					}
				}

			} else if (paragraphStr.endsWith("。") || paragraphStr.contains("。")) {// 包含全角句号的按照句号切分
				//String[] sentenceInParagraph = paragraphStr.split("。");
				String[] sentenceInParagraph = paragraphStr.split("\\。|\\？|\\！|\\?|\\!");
				for (int j = 0; j < sentenceInParagraph.length; j++) {
					int sentenceBegin = sourceStr
							.indexOf(sentenceInParagraph[j]);// 句子在sourceStr中的起始位置
					int sentenceEnd = sentenceBegin
							+ sentenceInParagraph[j].length();// 句子在sourceStr中的结束位置
					String subStr = sourceStr.substring(sentenceBegin,
							sentenceEnd);
					if (!subStr.trim().equals("")) {
						sb.append(subStr + "\r\n");
						for (int k = 0; k < 4; k++) {
							if (k == 0) {
								sentenceInfo[sentenceIndex][k] = i + 1;// 段号
							} else if (k == 1) {
								sentenceInfo[sentenceIndex][k] = sentenceIndex + 1;// 句号
							} else if (k == 2) {
								sentenceInfo[sentenceIndex][k] = sentenceBegin;// 句子起始位置
							} else {
								sentenceInfo[sentenceIndex][k] = sentenceEnd;// 句子结束位置
							}
						}
						sentenceIndex++;
					}

				}
			} else {// 不包含句号的就将整段切分成一个句子
				String sentenceInParagraph = paragraphStr;
				int sentenceBegin = sourceStr.indexOf(sentenceInParagraph);// 句子在sourceStr中的起始位置
				//System.out.println("sentenceBegin="+sentenceBegin);
				int sentenceEnd = sentenceBegin + sentenceInParagraph.length();// 句子在sourceStr中的结束位置
				//System.out.println("sentenceEnd="+sentenceEnd);
				String subStr = sourceStr.substring(sentenceBegin, sentenceEnd);
				//System.out.println("第"+(sentenceIndex+1)+"句subStr="+subStr);
				if (!subStr.trim().equals("")) {
					sb.append(subStr + "\r\n");
					for (int k = 0; k < 4; k++) {
						if (k == 0) {
							sentenceInfo[sentenceIndex][k] = i + 1;// 段号
						} else if (k == 1) {
							sentenceInfo[sentenceIndex][k] = sentenceIndex + 1;// 句号
						} else if (k == 2) {
							sentenceInfo[sentenceIndex][k] = sentenceBegin;// 句子起始位置
						} else {
							sentenceInfo[sentenceIndex][k] = sentenceEnd;// 句子结束位置
						}
					}
					sentenceIndex++;
				}
			}
		}
		String[] sentences = getSentences(sb.toString());//—————按"\r\n"分割
		this.setSentencesStr(sentences);// 将分出的句子存储在sentencesStr对象中
		return sentenceInfo;
	}

	/**
	 * 将分割出的句子放到一个String数组中
	 * 
	 * @param sentenceStr
	 *            所有句子内容的字符串
	 * @return 返回的句子数组
	 */
	private String[] getSentences(String sentenceStr) {
		return sentenceStr.split("\r\n");
	}
	
	//—————为什么感觉这样总是很浪费时间和空间啊
	/**
	 * 获得要切分的句子总数
	 * 
	 * @param splitParagraph
	 *            切分出的段落字符
	 * @return 返回句子总数
	 */
	private int getSentenceCount(String[] splitParagraph) {
		int sentenceCount = 0;
		String sourceStr = this.getSourceStr();// 获得读取字符串的内容
		for (int i = 0; i < splitParagraph.length; i++) {
			String paragraphStr = splitParagraph[i];// 每段的字符串内容
			if (paragraphStr.endsWith("。") || paragraphStr.contains("。")) {// 包含半角句号的按照句号切分
				//String[] sentenceInParagraph = paragraphStr.split("。");
				String[] sentenceInParagraph = paragraphStr.split("\\。|\\？|\\！|\\?|\\!");
				for (int j = 0; j < sentenceInParagraph.length; j++) {
					int sentenceBegin = sourceStr
							.indexOf(sentenceInParagraph[j]);// 句子在sourceStr中的起始位置
					int sentenceEnd = sentenceBegin
							+ sentenceInParagraph[j].length();// 句子在sourceStr中的结束位置
					String subStr = sourceStr.substring(sentenceBegin,
							sentenceEnd);
					if (!subStr.trim().equals("")) {
						sentenceCount++;
					}
				}

			} else if (paragraphStr.endsWith("。") || paragraphStr.contains("。")) {// 包含全角句号的按照句号切分
				//String[] sentenceInParagraph = paragraphStr.split("。");
				String[] sentenceInParagraph = paragraphStr.split("\\。|\\？|\\！|\\?|\\!");
				for (int j = 0; j < sentenceInParagraph.length; j++) {
					int sentenceBegin = sourceStr
							.indexOf(sentenceInParagraph[j]);// 句子在sourceStr中的起始位置
					int sentenceEnd = sentenceBegin
							+ sentenceInParagraph[j].length();// 句子在sourceStr中的结束位置
					String subStr = sourceStr.substring(sentenceBegin,
							sentenceEnd);
					if (!subStr.trim().equals("")) {
						sentenceCount++;
					}

				}
			} else {// 不包含句号的就将整段切分成一个句子
				String sentenceInParagraph = paragraphStr;
				int sentenceBegin = sourceStr.indexOf(sentenceInParagraph);// 句子在sourceStr中的起始位置
				int sentenceEnd = sentenceBegin + sentenceInParagraph.length();// 句子在sourceStr中的结束位置
				String subStr = sourceStr.substring(sentenceBegin, sentenceEnd);
				if (!subStr.trim().equals("")) {
					sentenceCount++;
				}
			}
		}
		return sentenceCount;
	}

	public int[][] getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(int[][] paragraphs) {
		this.paragraphs = paragraphs;
	}

	public String[] getParagraphStr() {
		return paragraphStr;
	}

	public void setParagraphStr(String[] paragraphStr) {
		this.paragraphStr = paragraphStr;
	}

	public int[][] getSentences() {
		return sentences;
	}

	public void setSentences(int[][] sentences) {
		this.sentences = sentences;
	}

	public String[] getSentencesStr() {
		return sentencesStr;
	}

	public void setSentencesStr(String[] sentencesStr) {
		this.sentencesStr = sentencesStr;
	}

	public String getSourceStr() {
		return sourceStr;
	}

	public void setSourceStr(String sourceStr) {
		this.sourceStr = sourceStr;
	}
}

