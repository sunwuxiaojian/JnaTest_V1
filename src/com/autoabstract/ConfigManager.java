package com.autoabstract;

/**
 * 权重参数配置
 * 
 * @author Lee
 * 
 */
public class ConfigManager {

	public static final int MaxSynWords = 10;// 取的关键词个数

	public static final int FinalSentencesCount = 5;// 取的句子总数

	public static final double TitleWeight = 2;// 标题权重

	public static final double FirstGraphWeight = 1.5;// 第一段权重

	public static final double LastGraphWeight = 1.4;// 最后一段权重
	
	public static final double FirstSentenceWeight = 1.3;// 第一句权重

	public static final double LastSentenceWeight = 1.2;// 最后一句权重

	public static final double FrequenceWeight = 0.3;// 频率权重

	public static final double PointWeight = 0.5;// 位置权重
	
	public static final double LengthWeight = 30;// 长度权重
	
	public static final double CueWord = 1.1;// 提示词或短语权重

	public static final double DistanceWeight = 0.3;// 距离权重参数

	public static final double DistanceExponentExp = 10;// 距离权重补充值

	public static final double DistanceWeightMultiple = 1.0;// 距离权重倍数
}
