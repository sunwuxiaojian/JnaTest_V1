package library;
/*
 * Created by liujiang on 2016/10/31.
 */

import com.sun.jna.Library;

public interface CLibrary extends Library {

    /**
     * 初始化程序
     * @param sInitDirPath 初始化目录（存放data文件夹的路径）
     * @param encoding 编码 GBk 0 utf-8 1
     * @param sLicenceCode 默认为“0”
     * @return 成功 true 失败 false
     */
    boolean NLPIR_Init(String sInitDirPath,int encoding,String sLicenceCode );

    /**
     * 退出程序并释放资源
     * @return 成功 true 失败 false
     */
    boolean NLPIR_Exit();

    /**
     * 从文档中导入用户词典
     * @param sFilename 文档路径
     * @param bOverwrite 是否覆盖现有的词典 true 覆盖 false 追加
     * @return 成功加载的词条的个数
     */
    int NLPIR_ImportUserDict(String sFilename,boolean bOverwrite);

    /**
     * 对一个段落进行处理 返回的为处理好的字符串
     * @param sParagraph 段落
     * @param bPOSTagged 是否需要词性标注 默认是1 标注 0 不标注
     * @return 处理好的字符串
     */
    String NLPIR_ParagraphProcess(String sParagraph,int bPOSTagged);

    /**
     * 对文本文档进行分词处理
     * @param sSourceFilename 源文档地址
     * @param sResultFilename 处理结果保存在文档内 文档地址 输出格式在配置文件中指定
     * @param bPOStagged 是否需要词性标记
     * @return 处理速度
     */
    double  NLPIR_FileProcess(String sSourceFilename,String sResultFilename,int bPOStagged);

    /**
     * 加入用户词语到用户词典
     * @param sWord  要加入的词语
     * @return 1 成功 0 失败
     */
    int NLPIR_AddUserWord(String sWord);

    /**
     * 保存用户词典到硬盘
     * @return 1 成功 0 失败
     */
    int NLPIR_SaveTheUsrDic();

    /**
     * 从用户词典中删除词条
     * @param sWord
     * @return -1 失败 词典中不存在 其他 成功
     */
    int NLPIR_DelUsrWord(String sWord);

    /**
     * 从段落中提取关键字
     * @param sLine 输入的文本
     * @param nMaxKeyLimit 关键字的最大数量
     * @param bWeightOut 输出结果中是否带权重
     * @return  成功："科学发展观#宏观经济 " or "科学发展观/23.80/12#宏观经济/12.20/21" with weight   这里每个关键词采采用#分割，权重分别为信息熵权重与词频权重 失败：null
     */
    String NLPIR_GetKeyWords(String sLine,int nMaxKeyLimit,boolean  bWeightOut);

    /**
     * 从文件中提取关键字
     * @param sTextFile 文件路径
     * @param nMaxKeyLimit 关键字的最大数量
     * @param bWeightOut 输出结果中是否带权重
     * @return  成功："科学发展观#宏观经济 " or "科学发展观/23.80/12#宏观经济/12.20/21" with weight   这里每个关键词采采用#分割，权重分别为信息熵权重与词频权重 失败：null
     */
    String NLPIR_GetFileKeyWords(String sTextFile,int nMaxKeyLimit,boolean bWeightOut);

    /**
     * 从文本文件中导入关键词黑名单（永远不作为关键词输出）
     * @param sFilename 文件命
     * @return 导入的词条的数量
     */
    int NLPIR_ImportKeyBlackList(String sFilename);

    /**
     *获取输入文本的词，词性，词频，按照词频大小排序
     * @param sFileName 文本文件的全路径
     * @return 返回的是词频统计结果形式如下：张华平/nr/10#博士/n/9#分词/n/8
     */
    String NLPIR_FileWordFreqStat(String sFileName);

    /**
     *获取输入文本的词，词性，词频，按照词频大小排序
     * @param text 输入的文本内容
     * @return 返回的是词频统计结果形式如下：张华平/nr/10#博士/n/9#分词/n/8
     */
    String NLPIR_WordFreqStat(String text);

    /**
     * 当前的切分结果过大时，如“中华人民共和国”
     * 需要执行该函数，将切分结果细分为“中华 人民 共和国”
     * 细分粒度最大为三个汉字
     * @param sline 输入的字符串
     * @return 返回细粒度分词，如果不能细分 返回空字符串""
     */
    String NLPIR_FinerSegment(String sline);
    

}
