package com.autoabstract;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

/**
 * 调用IK分词对文章进行分词处理
 * 
 * @author Lee
 * 
 */
public class SyncopateWordsByIK {
	private String noSameSegResult;// 存放切分出的不重复的词元
	private String[][] allWordsFrequency;//存放所有词元的频率
	private List<String> allWords;//存放所有词语的List,方便用hashmap找出频率

	//————构造函数
	public SyncopateWordsByIK(String srcString) {//————参数srcString为一个字符串，格式为“段1\r\n段2……\r\n段n”(sourceStr)
		System.out.println("Length = " + srcString.length());
		
		IKSegmentation ikSeg = new IKSegmentation(new StringReader(srcString),true);
		long begin = System.currentTimeMillis();
		String segResult = getSegResult(ikSeg);
		String noSameSegResult = getNoSameSegResult(segResult);//————获得不含重复词元的字符串
		this.setNoSameSegResult(noSameSegResult);
		String[][] allWordsFrequency = getAllWordsFrequency(allWords);//按照降序排列
		this.setAllWordsFrequency(allWordsFrequency);
		long end = System.currentTimeMillis();
		System.out.println("分词耗时 : " + (end - begin) + "毫秒");
	}

	/**
	 * 获得切分出的不重复的词元字符串
	 * 
	 * @param segResult
	 * @return
	 */
	public String getNoSameSegResult(String segResult) {
		String[] segArray = segResult.split("/");//————按添加的"/"进行分词，分出来是初步的，含有重复的词
		System.out.println("包含重复词元的词元总数="+segArray.length);
		
		List<String> index = new ArrayList<String>();
		allWords = new ArrayList<String>();//存放所有词语的List,方便用hashmap找出频率
		for (int i = 0; i < segArray.length; i++) {
			allWords.add(segArray[i]);//存储所有词语，包含重复的，方便用hashmap找出频率
			if (index.indexOf(segArray[i]) != -1) {//————如果返回-1说明不包含，如果不是-1，则index包含了（即重复了），跳过
				continue;
			}
			index.add(segArray[i]);//————用于判断是否包含重复的词
		}
		
		System.out.println("不重复词元的总数index="+index.size());
		
		StringBuffer noSameSegResult = new StringBuffer();
		for (int i = 0; i < index.size(); i++) {//————这样应该也行吧？代码还更加简洁
			if (index.get(i) != null) {
				noSameSegResult.append(index.get(i)).append("/");
			}
		}
		
		return noSameSegResult.toString();
	}

	/**
	 * 获得原始切分出的字符串（可能包含重复词元）
	 * 
	 * @param ikSeg
	 * @return
	 */
	public String getSegResult(IKSegmentation ikSeg) {
		StringBuffer sb = new StringBuffer();
		try {
			Lexeme l = null;//使用lucene
			//getBeginPosition()
			//getEndPosition()
			while ((l = ikSeg.next()) != null) {
				if(l.getLength() > 1){//至少取两个字
					if (!l.getLexemeText().toString().trim().equals("")) {//————getLexemeText()!!!
						sb.append(l.getLexemeText().toString().trim()).append("/");//————getLexemeText()才是取文本!!!加入"/"方便切分不重复的词元
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("sb="+sb);
		return sb.toString();
	}
	
	/**
	 * 获得所有词元的频率
	 * 但这样很不科学，比如分词有一个“搜索”，则会把其他如“搜索引擎”等纳入其频率，显然不合理。但是这样的情况很少，影响可以忽略。
	 * 
	 * @param list
	 * @return
	 */
	public String[][] getAllWordsFrequency(List<String> list){
        Map<String, Integer> map = new HashMap<String, Integer>(); 
        for (String temp : list) { 
            Integer count = map.get(temp); 
            map.put(temp, (count == null) ? 1 : count + 1); 
        } 
  
        List<Map.Entry<String, Integer>> list1 = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list1, new Comparator<Map.Entry<String, Integer>>() {   
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
                return (o2.getValue() - o1.getValue()); 
                //return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        
        String[][] allWordsFrequency = new String[list1.size()][2];
        for (int i = 0; i < list1.size(); i++) {
            String id = list1.get(i).toString();
            String[] s = id.split("=");
            allWordsFrequency[i][0]=s[0];
            allWordsFrequency[i][1]=s[1];
            //System.out.println(id);
        }
        
        System.out.println("频率最大10个词元");
        for (int i = 0; i < 10; i++) {
        	System.out.println("词元："+allWordsFrequency[i][0]+"\t频率："+allWordsFrequency[i][1]);   
        }
   
        return allWordsFrequency;
	}

	public String getNoSameSegResult() {
		return noSameSegResult;
	}

	public void setNoSameSegResult(String noSameSegResult) {
		this.noSameSegResult = noSameSegResult;
	}

	public String[][] getAllWordsFrequency() {
		return allWordsFrequency;
	}

	public void setAllWordsFrequency(String[][] allWordsFrequency) {
		this.allWordsFrequency = allWordsFrequency;
	}
	
}

