package com.autoabstract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 整合句子和词元关系，得到一个数组 格式：段号 句号 词元 起始位置 频率
 * 
 * 计算总权重，并摘要
 * 
 * @author Lee
 * 
 */
public class GraphSentenceConformity {

	private String autoAbstractResult;// 存放摘要结果

	private static int finalSentencesCount = ConfigManager.FinalSentencesCount;

	public GraphSentenceConformity(Syncopate syncopate,
			FrequencySyncopateWords frequencySynWords,
			SyncopateGraphAndSentence syncopateGraphAndSentence) {
		// 存放分句信息数组 格式：段号 句号 句子起始位置 句子结束位置
		int[][] sentences = syncopateGraphAndSentence.getSentences();
		// 分割的句子，sentencesStr[i]为第i+1句
		String[] sentencesStr = syncopateGraphAndSentence.getSentencesStr();
		// 包含频率信息的词元数组信息 格式：词元 起始位置 频率
		String[][] statSynWordFrequency = frequencySynWords
				.getStatSynWordFrequency();
		String[] paragraphStr = syncopateGraphAndSentence.getParagraphStr();// 将段落信息取过来以便得到段落总数
		// 关系数组格式：段号 句号 词元 起始位置 频率
		String[][] graphSentencesCon = this.getGraphSentencesConnection(
				sentences, statSynWordFrequency);
		// 格式为：句子，词元，总权重
		//String[][] synWordImportExponent = getSynWordImportExponent(graphSentencesCon, paragraphStr, sentences);
		// 得到所有句子的权重 ，格式为：句子，总权重
		String[][] synWordWeight = getSynWordWeight(graphSentencesCon, paragraphStr, sentences, sentencesStr);
		// 按照各个句子中出现词元总权重的总和选择摘要结果
		//String[][] sumNoSameSynWord = getNoSameSynWordBySum(synWordImportExponent);

		//this.setAutoSummaryResult(autoSummaryResult(sumNoSameSynWord,sentencesStr));
		this.setAutoAbstractResult(autoAbstractResult(synWordWeight,sentencesStr));
	}

	/**
	 * 获得摘要结果
	 * 
	 * @param maxNoSameSynWord
	 *            不重复词元数组
	 * @param sentencesStr
	 *            句子数组
	 * @return 返回摘要
	 */
	private String autoAbstractResult(String[][] maxNoSameSynWord,
			String[] sentencesStr) {
		StringBuffer sb = new StringBuffer();
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < maxNoSameSynWord.length; i++) {
			map.put(i + ":" + maxNoSameSynWord[i][1], sentencesStr[Integer
					.parseInt(maxNoSameSynWord[i][0]) - 1]);
		}
		Iterator<String> iter = map.keySet().iterator();
		double[] d = new double[map.size()];
		int i = 0;
		while (iter.hasNext()) {
			String next = iter.next();
			d[i] = Double.parseDouble(next.substring(next.indexOf(":") + 1));
			i++;
		}
		double temp = 0;
		for (int j = 0; j < d.length; j++) {
			for (int k = 0; k < d.length; k++) {
				if (d[j] > d[k]) {
					temp = d[k];
					d[k] = d[j];
					d[j] = temp;
				}
			}
		}
		int m = maxNoSameSynWord.length;
		int s = sentencesStr.length;
		if (finalSentencesCount < s) {
			if (finalSentencesCount > m) {
				finalSentencesCount = m;
			}
		} else {
			if (s < m) {
				finalSentencesCount = s;
			} else {
				finalSentencesCount = m;
			}
		}
		int[] sentenceIndex = new int[finalSentencesCount];// 限制取到的句子个数
		List<String> index = new ArrayList<String>();
		for (int j = 0; j < d.length; j++) {
			if (j < finalSentencesCount) {
				for (int i1 = 0; i1 < maxNoSameSynWord.length; i1++) {
					if (Double.parseDouble(maxNoSameSynWord[i1][1]) == d[j]) {
						if (index.indexOf(maxNoSameSynWord[i1][0]) != -1) {
							continue;
						}
						sentenceIndex[j] = Integer
								.parseInt(maxNoSameSynWord[i1][0]);
						index.add(maxNoSameSynWord[i1][0]);
						break;
					}
				}
			}
		}
		int temp1 = 0;
		for (int j = 0; j < sentenceIndex.length; j++) {
			for (int k = 0; k < sentenceIndex.length; k++) {
				if (sentenceIndex[j] < sentenceIndex[k]) {
					temp1 = sentenceIndex[k];
					sentenceIndex[k] = sentenceIndex[j];
					sentenceIndex[j] = temp1;
				}
			}
		}
		sb.append(sentencesStr[0] + "\r\n");//默认加上第一句，即标题
		for (int j = 0; j < sentenceIndex.length; j++) {
			sb.append(sentencesStr[sentenceIndex[j] - 1] + "。\r\n");
		}
		return sb.toString();
	}

	/**
	 * 获得相同词元的权重总和数组
	 * 
	 * @param synWordImportExponent
	 *            词元总权重等项数组 格式为：句子，词元，总权重
	 * @return 相同词元但是权重最大词元组成的数组
	 */
	public String[][] getNoSameSynWordBySum(String[][] synWordImportExponent) {
		// 格式为：句子，总权重
		String[][] maxNoSameSynWord = new String[getSynWordSentences(synWordImportExponent)][2];//getSynWordSentences为获得包含词元的句子总数。
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < synWordImportExponent.length; i++) {
			set.add(synWordImportExponent[i][0]);
		}
		Iterator<String> iter = set.iterator();
		int maxNoSameSynWordIndex = 0;
		while (iter.hasNext()) {
			int sentencesIndex = Integer.parseInt(iter.next().toString());
			double sum = 0.0;
			for (int i = 0; i < synWordImportExponent.length; i++) {
				if (sentencesIndex == Integer
						.parseInt(synWordImportExponent[i][0])) {
					sum += Double.parseDouble(synWordImportExponent[i][2]);
				}
			}
			maxNoSameSynWord[maxNoSameSynWordIndex][0] = "" + sentencesIndex;// 句子下标
			maxNoSameSynWord[maxNoSameSynWordIndex][1] = "" + sum;// 总和
			maxNoSameSynWordIndex++;
		}
		
		/*for (int i = 0; i < maxNoSameSynWord.length; i++) {
		System.out.println("句子：" + maxNoSameSynWord[i][0] + "\t总权重："
		+ maxNoSameSynWord[i][1]);
		}*/
		
		return maxNoSameSynWord;
	}

	/**
	 * 获得句子总数
	 * 
	 * @param synWordImportExponent
	 * @return
	 */
	private int getSynWordSentences(String[][] synWordImportExponent) {
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < synWordImportExponent.length; i++) {
			set.add(synWordImportExponent[i][0]);
		}
		return set.size();
	}
	
	// graphSentencesCon关系数组格式：段号 句号 词元 起始位置 频率
	// sentences格式为：段号 句号 句子起始位置 句子结束位置
	private String[][] getSynWordWeight(String[][] graphSentencesCon,
			String[] paragraphStr, int[][] sentences, String[] sentencesStr){
		int paragraphSum = paragraphStr.length;//段数
		int sentenceSum = sentences.length;//句数
		// 格式为：句子，总权重
		String[][] synWordWeight = new String[sentences.length][2];
		//对每一句求权重
		for (int i = 0; i < sentences.length; i++) {
			//统计该句子的权重
			double weight = 0;
			//统计该句子含有的关键词个数
			int count = 0;
			for(int j = 0; j < graphSentencesCon.length; j++){
				//如果关键词在该句
				if(sentences[i][1] == Integer.parseInt(graphSentencesCon[j][1])){
					count++;
				}
			}
			//System.out.println("sentencesStr[sentences[i][1]-1].length()="+sentencesStr[sentences[i][1]-1].length());
			//System.out.println("sentencesStr[i].length()="+sentencesStr[i].length());
			
			//长度权重，长度越长，权值越小
			if(sentencesStr[i].length() > 150||sentencesStr[i].length() < 12){//如果长度大于150或者小于12，则不能作为文摘句
				weight = -50;
				//weight += 1/(sentencesStr[i].length()) * ConstantManager.LengthWeight;
			}else{
				weight += 1/(sentencesStr[i].length()) * ConfigManager.LengthWeight;
			}
			
			//提示短语权重（可以写成一个函数）
			if(sentencesStr[i].indexOf("综上所述") == 0||sentencesStr[i].indexOf("综上") == 0||sentencesStr[i].indexOf("总而言之") == 0||sentencesStr[i].indexOf("总之") == 0){
				weight += ConfigManager.CueWord;
			}
			
			//以下是位置权重
			//如果是标题，则值为零，因为标题默认输出
			if(sentences[i][0] == 1){
				weight = 0;
				/*weight = 0.3 + //标题长度权重和提示词权重默认为0.3
				count * ConfigManager.FrequenceWeight + //含关键词权重
				ConfigManager.TitleWeight * ConfigManager.PointWeight //标题权重
				;*/
			}
			//如果是第一段
			else if(sentences[i][0] == 2){
				//如果是第一句
				if(sentences[i][1] > sentences[i-1][0]){
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.FirstGraphWeight * ConfigManager.PointWeight + //第一段权重
					ConfigManager.FirstSentenceWeight * ConfigManager.PointWeight//第一句权重
					;
				}else if(sentences[i][0] < sentences[i+1][0]){//如果是末句
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.FirstGraphWeight * ConfigManager.PointWeight + //第一段权重
					ConfigManager.LastSentenceWeight * ConfigManager.PointWeight//末句权重
					;
				}else{//其他非首句和末句
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.FirstGraphWeight * ConfigManager.PointWeight//第一段权重
					;
				}
			}
			//如果是末段
			else if(sentences[i][0] == paragraphSum){
				//如果是第一句
				if(sentences[i][0] > sentences[i-1][0]){
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.LastGraphWeight * ConfigManager.PointWeight + //末段权重
					ConfigManager.FirstSentenceWeight * ConfigManager.PointWeight//第一句权重
					;
				}else if(sentences[i][0] == sentenceSum){//如果是末句
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.LastGraphWeight * ConfigManager.PointWeight + //末段权重
					ConfigManager.LastSentenceWeight * ConfigManager.PointWeight//末句权重
					;
				}else{//其他非首句和末句
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.LastGraphWeight * ConfigManager.PointWeight//末段权重
					;
				}
			}
			//如果是非首段和末段
			else{
				//如果是第一句
				if(sentences[i][0] > sentences[i-1][0]){
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.FirstSentenceWeight * ConfigManager.PointWeight//第一句权重
					;
				}else if(sentences[i][0] < sentences[i+1][0]){//如果是末句
					weight += count * ConfigManager.FrequenceWeight + //含关键词权重
					ConfigManager.LastSentenceWeight * ConfigManager.PointWeight//末句权重
					;
				}else{//其他非首句和末句
					weight += count * ConfigManager.FrequenceWeight //含关键词权重
					;
				}
			}
			synWordWeight[i][0] = "" + sentences[i][1];// 句子
			synWordWeight[i][1] = "" + weight;// 总权重
		}
		for(int i = 0; i < synWordWeight.length; i++){
			System.out.println("句子synWordWeight["+i+"][0] = "+synWordWeight[i][0]+"权重synWordWeight["+i+"][1] = "+synWordWeight[i][1]);
			
		}
		return synWordWeight;
	}

	//这是一个词元一条权重的意思
	/**
	 * 获得词的权重等项数组信息
	 * 
	 * @param graphSentencesCon
	 *            格式为：段号，句子，词元，词元起始位置，频率
	 * @param sentences
	 *            格式：段号 句号 句子起始位置 句子结束位置
	 * @param paragraphStr
	 *            句子信息
	 * @return 返回包含权重的句子
	 */
	public String[][] getSynWordImportExponent(String[][] graphSentencesCon,
			String[] paragraphStr, int[][] sentences) {
		/*System.out.println("到底是什么格式？");
		for(int i =0 ;i < graphSentencesCon.length; i++){
			System.out.println(graphSentencesCon[i][0]+"\t"+graphSentencesCon[i][1]+"\t"+graphSentencesCon[i][2]+"\t"+graphSentencesCon[i][3]+"\t"+graphSentencesCon[i][4]+"\n");
		}*/
		int paragraphSum = paragraphStr.length;//段数
		// 格式为：句子，词元，总权重
		String[][] synWordImportExponent = new String[graphSentencesCon.length][3];
		for (int i = 0; i < graphSentencesCon.length; i++) {
			int distanceWeight = 0;// 距离权重
			for (int j = 0; j < sentences.length; j++) {
				if ((Integer.parseInt(graphSentencesCon[i][1]) == sentences[j][1])
						&& (Integer.parseInt(graphSentencesCon[i][0]) == sentences[j][0])) {// 相同句子，相同段落
					int synWordIndex = Integer
							.parseInt(graphSentencesCon[i][3]);// 词元起始下标
					int sentenceBeginIndex = sentences[j][2];
					int sentencesEndIndex = sentences[j][3];
					//if (synWordIndex >= sentenceBeginIndex && synWordIndex < sentencesEndIndex) {// 如果词元在句子中
						// 计算距离权重
					if ((synWordIndex - sentenceBeginIndex) > (sentencesEndIndex - synWordIndex)) {// 正反向匹配词元权重，取最小的
						distanceWeight = sentencesEndIndex - synWordIndex;// 反向权重
					} else {
						distanceWeight = synWordIndex
								- sentenceBeginIndex;// 正向权重
					}
					//}
				}
			}
			double allWeight = 0.0;
			int paragraphIndex = Integer.parseInt(graphSentencesCon[i][0]);// 段落下标
			// 如果是标题，第一段和最后一段；除了计算频率权重，距离权重外，还要计算位置权重
			// 计算公式为：总权重=频率权重（出现次数）*0.3（频率权重参数）+位置权重(标题2,第一段1.5,最后一段1.4)*0.5（位置权重参数）+距离权重（词元离句子首和句子尾的最小距离）*0.2（距离权重参数）
			if (distanceWeight == 0) {
				distanceWeight = 1;
			}
			if (paragraphIndex == 1) {// 标题
				allWeight += Integer.parseInt(graphSentencesCon[i][4])
						* ConfigManager.FrequenceWeight
						+ ConfigManager.TitleWeight
						* ConfigManager.PointWeight
						+ (1.0 / distanceWeight)
						* (distanceWeight + ConfigManager.DistanceExponentExp);// 标题的位置权重为2
			} else if (paragraphIndex == 2) {// 第一段
				allWeight += Integer.parseInt(graphSentencesCon[i][4])
						* ConfigManager.FrequenceWeight
						+ ConfigManager.FirstGraphWeight
						* ConfigManager.PointWeight
						+ (ConfigManager.DistanceWeightMultiple / distanceWeight)
						* (distanceWeight + ConfigManager.DistanceExponentExp);// 第一段的位置权重为1.5
			} else if (paragraphIndex == paragraphSum) {// 最后一段
				allWeight += Integer.parseInt(graphSentencesCon[i][4])
						* ConfigManager.FrequenceWeight
						+ ConfigManager.LastGraphWeight
						* ConfigManager.PointWeight
						+ (ConfigManager.DistanceWeightMultiple / distanceWeight)
						* (distanceWeight + ConfigManager.DistanceExponentExp);// 最后一段的位置权重为1.4
			} else {// 其他段落
				allWeight += Integer.parseInt(graphSentencesCon[i][4])
						* ConfigManager.FrequenceWeight
						+ (ConfigManager.DistanceWeightMultiple / distanceWeight)
						* (distanceWeight + ConfigManager.DistanceExponentExp);// 其他段落的只计算频率权重和距离权重的总和
			}
			synWordImportExponent[i][0] = graphSentencesCon[i][1];// 句子
			synWordImportExponent[i][1] = graphSentencesCon[i][2];// 词元
			synWordImportExponent[i][2] = "" + allWeight;// 总权重
		}
		return synWordImportExponent;
	}

	/**
	 * 获得格式为：段号，句子，词元，起始位置，频率 的数组
	 * 
	 * @param sentences
	 *            分句信息数组 格式为：段号 句号 句子起始位置 句子结束位置
	 * @param statSynWordFrequency
	 *            包含频率信息的词元数组信息 格式：词元 起始位置 频率
	 * @return 返回数组
	 */
	private String[][] getGraphSentencesConnection(int[][] sentences,
			String[][] statSynWordFrequency) {
		// 根据词元长度构造此数组
		//段号，句子号，词元，起始位置，频率
		String[][] graphSentencesCon = new String[statSynWordFrequency.length][5];
		int graphSentencesConIndex = 0;
		for (int i = 0; i < sentences.length; i++) {
			int sentencesBeginIndex = sentences[i][2];// 句子起始位置
			int sentencesEndIndex = sentences[i][3];// 句子结束位置
			for (int j = 0; j < statSynWordFrequency.length; j++) {
				int statSynWordFrequencyIndex = Integer
						.parseInt(statSynWordFrequency[j][1]);// 词元起始位置
					// 如果词元在句子中
				if (statSynWordFrequencyIndex >= sentencesBeginIndex
						&& statSynWordFrequencyIndex < sentencesEndIndex) {
					graphSentencesCon[graphSentencesConIndex][0] = ""
							+ sentences[i][0];
					graphSentencesCon[graphSentencesConIndex][1] = ""
							+ sentences[i][1];
					graphSentencesCon[graphSentencesConIndex][2] = statSynWordFrequency[j][0];
					graphSentencesCon[graphSentencesConIndex][3] = statSynWordFrequency[j][1];
					graphSentencesCon[graphSentencesConIndex][4] = statSynWordFrequency[j][2];
					graphSentencesConIndex++;
				}
			}
		}
		/*for (int i = 0; i < graphSentencesCon.length; i++) {
		System.out.println("序号："+(i+1)+" \t段号：" + graphSentencesCon[i][0] + " \t句号：- "
		+ graphSentencesCon[i][1] + " \t词元：- "
		+ graphSentencesCon[i][2] + " \t起始位置：- "
		+ graphSentencesCon[i][3] + " \t频率：- "
		+ graphSentencesCon[i][4]);
		}*/
		return graphSentencesCon;
	}

	public String getAutoAbstractResult() {
		return autoAbstractResult;
	}

	public void setAutoAbstractResult(String autoAbstractResult) {
		this.autoAbstractResult = autoAbstractResult;
	}
}
