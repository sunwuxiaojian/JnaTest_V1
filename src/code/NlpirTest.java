package code;

import library.CLibrary;

public class NlpirTest {
	private static CLibrary tool = CLibrary.Instance;

	public static void main(String[] args) throws Exception {
		boolean initResult = tool.NLPIR_Init("D:\\NLPIR",1,"0");
		if(!initResult) return;
		String result ;
		String src = "中国人民万岁";
		result = tool.NLPIR_ParagraphProcess(src,0);
//		result = tool.NLPIR_GetFileKeyWords("F:\\ideaWorkspace\\JnaTest_V1\\bin/test.txt",20,false);
		System.out.println(result);

	}

}
