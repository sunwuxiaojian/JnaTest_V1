package com.autoabstract;



/**
 * 根据词元信息统计每个词元出现的次数并且选出M个词元作为摘要组句
 * 
 * @author Lee
 * 
 */
public class FrequencySyncopateWords {
	private String[][] statSynWordFrequency;// 包含频率信息的词元数组信息 格式：词元 起始位置 频率
	private String[][] maxTenSynWord;

	private static int maxSynWords = ConfigManager.MaxSynWords;

	/**
	 * 构造函数
	 * 
	 * @param syncopate
	 *            Syncopate对象
	 * @param noEchoWordCount
	 *            不重复词元个数
	 */
	public FrequencySyncopateWords(SyncopateWordsByIK syncopateWordsByIK, Syncopate syncopate, int noEchoWordCount) {
		String[][] wordDegreeLocation = syncopate.getWordDegreeLocation();// 得到词元数组信息
		String[][] synWord = syncopateWordsByIK.getAllWordsFrequency();
		String[][] maxTenSynWord = this.getMaxTenSynWord(synWord);
		this.setMaxTenSynWord(maxTenSynWord);
		this.setStatSynWordFrequency(statSynWordFrequency(wordDegreeLocation,
				maxTenSynWord));
	}

	/**
	 * 得到 最终选择出的M个（或者小于M个）句子包含频率信息的词元数组信息
	 * 
	 * @param wordDegreeLocation
	 *            所有词元数组信息
	 * @param maxTenSynWord
	 *            得到的前M个词元
	 * @return 返回包含频率信息的词元数组
	 */
	private String[][] statSynWordFrequency(String[][] wordDegreeLocation,
			String[][] maxTenSynWord) {
		// 存放统计频率后的M个词元
		// 格式：词元 起始位置 频率
		String[][] synWordFrequency = new String[getMaxWordCount(wordDegreeLocation,maxTenSynWord)][3];
		int maxTenSynWordIndex = 0;
		for (int i = 0; i < maxTenSynWord.length; i++) {// 先迭代选出的M个词元
			for (int j = 0; j < wordDegreeLocation.length; j++) {
				// 取出和选出词元名称相同词元的起始位置信息
				if (maxTenSynWord[i][0].equals(wordDegreeLocation[j][0])) {
					synWordFrequency[maxTenSynWordIndex][0] = maxTenSynWord[i][0];// 词元
					synWordFrequency[maxTenSynWordIndex][1] = wordDegreeLocation[j][1];// 起始位置
					synWordFrequency[maxTenSynWordIndex][2] = maxTenSynWord[i][1];// 出现频率
					maxTenSynWordIndex++;
				}
			}
		}
		return synWordFrequency;
	}
	
	/**
	 * 获取选出M个词元的总出现频率
	 * 
	 * @param maxTenSynWord
	 * @return
	 */
	private int getMaxWordCount(String[][] wordDegreeLocation,
			String[][] maxTenSynWord) {
		// 存放统计频率后的M个词元个数
		int maxTenSynWordIndex = 0;
		for (int i = 0; i < maxTenSynWord.length; i++) {// 先迭代选出的M个词元
			for (int j = 0; j < wordDegreeLocation.length; j++) {
				// 取出和选出词元名称相同词元的个数
				if (maxTenSynWord[i][0].equals(wordDegreeLocation[j][0])) {
					maxTenSynWordIndex++;
				}
			}
		}
		//System.out.println("maxTenSynWordIndex="+maxTenSynWordIndex);
		return maxTenSynWordIndex;
	}
	
	private String[][] getMaxTenSynWord(String[][] synWord){
		String[][] maxTenSynWord = new String[maxSynWords][2];
		//System.out.println("频率最大"+maxSynWords+"(maxSynWords)个词元");
        for (int i = 0; i < maxSynWords; i++) {
        	maxTenSynWord[i][0] = synWord[i][0];// 存放词元名称
			maxTenSynWord[i][1] = synWord[i][1];// 存放词元出现频率
        	//System.out.println("词元："+synWord[i][0]+";频率："+synWord[i][1]);      
        }
		return maxTenSynWord;
	}

	public String[][] getStatSynWordFrequency() {
		return statSynWordFrequency;
	}

	public void setStatSynWordFrequency(String[][] statSynWordFrequency) {
		this.statSynWordFrequency = statSynWordFrequency;
	}

	public String[][] getMaxTenSynWord() {
		return maxTenSynWord;
	}

	public void setMaxTenSynWord(String[][] maxTenSynWord) {
		this.maxTenSynWord = maxTenSynWord;
	}

}
