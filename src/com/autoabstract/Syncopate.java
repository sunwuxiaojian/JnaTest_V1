package com.autoabstract;

/**
 * 根据分词结果处理句子
 * 
 * @author Lee
 * 
 */
public class Syncopate {
	private String syncopateStr;// 用于存放分词结果

	private String[] syncopateArray;// 将分词结果分割为词元数组

	private String[][] wordDegreeLocation;// 存放词元出现位置信息
	
	private int synStrAppearTimes;//————添加存储所有词元在所有句子中出现次数的总和

	public Syncopate(SyncopateGraphAndSentence synGraphAndSentence,
			String syncopateWordResult) {
		System.out.println("syncopateWordResult="+syncopateWordResult);
		this.setSyncopateStr(syncopateWordResult);// 将上面手动分词结果放到字符串中保存————分词结果是不包含重复词元的字符串
		this.setSyncopateArray(this.splitSyncopateStr(syncopateWordResult));// 将保存的字符串结果分割为词元数组————按"/"分割，即同SynWordsByIK中的String[] noSameSegResultBefore

		int synStrAppearTimes = this
				.getAllSynStrAppearTimes(synGraphAndSentence);
		this.setSynStrAppearTimes(synStrAppearTimes);
		
		System.out.println("词元出现次数synStrAppearTimes：" + synStrAppearTimes);
		String[][] wordDegreeLocation = this
				.getWordDegreeLocation(synGraphAndSentence);
		this.setWordDegreeLocation(wordDegreeLocation);
	}

	private String[] splitSyncopateStr(String syncopateStr) {
		return syncopateStr.split("/");
	}

	/**
	 * 用于存放词元出现位置信息
	 * 
	 * @param synGraphAndSentence
	 *            SyncopateGraphAndSentence对象
	 * @return 返回词元相关信息字符数组
	 */
	private String[][] getWordDegreeLocation(
			SyncopateGraphAndSentence synGraphAndSentence) {
		// 保存词元相关信息
		// 格式：词元 词元起始位置
		String[][] wordDegree = new String[this.getSynStrAppearTimes()][2];//————修改，提高效率，如果用List的话，会不会更高效
		int synStrAppearTimes = 0;
		String[] syncopateArray = this.getSyncopateArray();//————得到不包含重复词元的数组
		String[] sentenceStr = synGraphAndSentence.getSentencesStr();// 获得分句内容
		int[][] sentences = synGraphAndSentence.getSentences();// 获得分句数组信息
		for (int j = 0; j < sentenceStr.length; j++) {// 迭代句子内容数组
			for (int k = 0; k < sentences.length; k++) {// 迭代句子信息数据
				for (int index = 0; index < 4; index++) {
					if (index == 1 && sentences[k][index] == (j + 1)) {// 定位是否是属于同一句————循环同样是多余的，就用了index=1，而且只用 了k=j的时候
						String sentence = sentenceStr[j];// 得到句子内容————对于每句话，都要将所有的词元进行对比！！！
						for (int i = 0; i < syncopateArray.length; i++) {// 迭代词元数组
							if (sentence.indexOf(syncopateArray[i]) != -1) {// 如果句子中包含词元
								int synWordIndex = sentences[k][index + 1]
										+ sentence.indexOf(syncopateArray[i]);// 句子的起始位置+词元在句子中出现的位置
								int s1 = sentence.indexOf(syncopateArray[i])
										+ syncopateArray[i].length();// 句子中包含词元的下标+词元长度
								int s2 = sentence.length();// 句子的长度
								wordDegree[synStrAppearTimes][0] = syncopateArray[i];//————存词元
								wordDegree[synStrAppearTimes][1] = "" + synWordIndex;//————存词元起始位置（相对于整篇文章），转化为string
								synStrAppearTimes++;
								/*System.out.println("词元："+syncopateArray[i]);
								System.out.println("起始："+synWordIndex);
								System.out.println("synStrAppearTimes："+synStrAppearTimes);*/
								/*System.out.println("s1="+s1);
								System.out.println("s2="+s2);*/
								// 如果存在词元，则从词元截取句子，继续判断子串句子中是否含有该词元，直到找不到为止
								int s3 = 0;
								int s4 = 0;
								if (s1 < s2) {
									String subSentenceStr = sentence.substring(
											s1, s2);
									int synWordIndexStr = sentences[k][index + 1] + s1;
									while (subSentenceStr
											.indexOf(syncopateArray[i]) != -1) {
											synWordIndexStr += subSentenceStr.indexOf(syncopateArray[i]);
										s3 = subSentenceStr
												.indexOf(syncopateArray[i])
												+ syncopateArray[i].length();// 句子中词元的下标+词元长度
										s4 = subSentenceStr.length();
										if (s3 < s2) {
											subSentenceStr = subSentenceStr
													.substring(s3, s4);
										}
										wordDegree[synStrAppearTimes][0] = syncopateArray[i];
										wordDegree[synStrAppearTimes][1] = ""
												+ synWordIndexStr;
										synWordIndexStr += syncopateArray[i].length();
										synStrAppearTimes++;
									}
								}

							}
						}
					}
				}
			}
		}
		return wordDegree;
	}

	/**
	 * 用于得到所有词元在所有句子中出现次数的总和
	 * 
	 * @param synGraphAndSentence
	 *            SyncopateGraphAndSentence对象
	 * @return 返回出现总次数
	 */
	private int getAllSynStrAppearTimes(
			SyncopateGraphAndSentence synGraphAndSentence) {
		int synStrAppearTimes = 0;
		String[] syncopateArray = this.getSyncopateArray();
		String[] sentenceStr = synGraphAndSentence.getSentencesStr();// 获得分句内容————分割的句子，sentencesStr[i]为第i+1句
		int[][] sentences = synGraphAndSentence.getSentences();// 获得分句数组信息————为n行（句数）四列的数组，格式为：段号 句号 句子起始位置 句子结束位置
		//System.out.println("sentenceStr.length="+sentenceStr.length+"----sentences.length="+sentences.length);
		for (int j = 0; j < sentenceStr.length; j++) {// 迭代句子内容数组
			for (int k = 0; k < sentences.length; k++) {// 迭代句子信息数据
				for (int index = 0; index < 4; index++) {
					if (index == 1 && sentences[k][index] == (j + 1)) {// 定位是否是属于同一句————（一一对应？）这里只用了index=1，为什么还用循环？？
						String sentence = sentenceStr[j];// 得到句子内容
						for (int i = 0; i < syncopateArray.length; i++) {// 迭代词元数组
							if (sentence.indexOf(syncopateArray[i]) != -1) {// 如果句子中包含词元————句子里包含词元数？？？？这……
								synStrAppearTimes++;
								int s1 = sentence.indexOf(syncopateArray[i])
										+ syncopateArray[i].length();// 句子中包含词元的下标+词元长度
								//System.out.println("词元"+i+"长度为：" + syncopateArray[i].length());
								//System.out.println("synStrAppearTimes=" + synStrAppearTimes);
								int s2 = sentence.length();// 句子的长度
								// 如果存在词元，则从词元截取句子，继续判断子串句子中是否含有该词元，直到找不到为止
								int s3 = 0;
								int s4 = 0;
								int flag = 0;// 标志位，用于记录截取子串次数
								if (s1 < s2) {
									String subSentenceStr = sentence.substring(
											s1, s2);
									while (subSentenceStr
											.indexOf(syncopateArray[i]) != -1) {
										synStrAppearTimes++;
										s3 = subSentenceStr
												.indexOf(syncopateArray[i])
												+ syncopateArray[i].length();// 句子中词元的下标+词元长度
										s4 = subSentenceStr.length();
										if (s3 < s2) {
											subSentenceStr = subSentenceStr
													.substring(s3, s4);
											flag++;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return synStrAppearTimes;
	}

	public String[] getSyncopateArray() {
		return syncopateArray;
	}

	public void setSyncopateArray(String[] syncopateArray) {
		this.syncopateArray = syncopateArray;
	}

	public String getSyncopateStr() {
		return syncopateStr;
	}

	public void setSyncopateStr(String syncopateStr) {
		this.syncopateStr = syncopateStr;
	}

	public String[][] getWordDegreeLocation() {
		return wordDegreeLocation;
	}

	public void setWordDegreeLocation(String[][] wordDegreeLocation) {
		this.wordDegreeLocation = wordDegreeLocation;
	}

	public int getSynStrAppearTimes() {
		return synStrAppearTimes;
	}

	public void setSynStrAppearTimes(int synStrAppearTimes) {
		this.synStrAppearTimes = synStrAppearTimes;
	}
	
}
