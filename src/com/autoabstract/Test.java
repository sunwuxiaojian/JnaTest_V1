package com.autoabstract;

/**
 * 测试
 * 
 * @author Lee
 * 
 */
public class Test {
	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		SyncopateGraphAndSentence syncopateGraphAndSentence = new SyncopateGraphAndSentence(
				"src/test1.txt");//————将文档进行初步的分段和分句
		SyncopateWordsByIK synWordsByIK = new SyncopateWordsByIK(syncopateGraphAndSentence
				.getSourceStr());//将文档进行分词，切分出不包含重复词元的字符串
		String syncopateWordStr = synWordsByIK.getNoSameSegResult();//得到不包含重复词元的字符串
		System.out.println("词元总数syncopateWordStr*************"
				+ syncopateWordStr.split("/").length);
		Syncopate syncopate = new Syncopate(syncopateGraphAndSentence,
				syncopateWordStr);//主要是为了得到词元在文章中的位置信息
		System.out.println("出现词元总数getSynStrAppearTimes："+syncopate.getSynStrAppearTimes());
		FrequencySyncopateWords frequencySynWords = new FrequencySyncopateWords(synWordsByIK,syncopate,
				syncopateWordStr.split("/").length);//得到包含频率信息的词元数组信息和频率M个最高的词元
		GraphSentenceConformity craphSentenceConformity = new GraphSentenceConformity(
				syncopate, frequencySynWords, syncopateGraphAndSentence);//进行权重计算得到最终的摘要结果
		System.out.println("****************摘要结果***************");
		System.out.println(craphSentenceConformity.getAutoAbstractResult());//输出摘要结果
		Long end = System.currentTimeMillis();
		System.out.println("运行时间****************" + (end - start) + "毫秒");
	}
}

