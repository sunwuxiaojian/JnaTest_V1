package utils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by liujiang on 2017/2/15.
 */
public class FileUtil {
    public static Set<String> readWordByLine(String filePath) {
        Set<String> wordSet = new HashSet<String>(); // 初如化停用词集
        BufferedReader StopWordFileBr = null;
        try {
            StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "utf-8"));
            String stopWord ;
            for (; (stopWord = StopWordFileBr.readLine()) != null;) {
                wordSet.add(stopWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wordSet;
    }

    /**
     * 读取文件并放到一个字符串中
     *
     * @param sourceFilePath
     *            源文件路径
     * @return 返回一个字符串
     */
    public static  String readNewsFile(String sourceFilePath) {
        StringBuffer sourceStr = new StringBuffer();
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.isFile()) {
            System.out.println("请确保你选择的是一个文件或者文件路径要正确！");
            return "";
        }
        if (!sourceFile.exists()) {
            System.out.println("您的文件路径不正确，请查找文件是否存在！");
            return "";
        }
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(
                    sourceFile), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String lineStr = in.readLine();
            while (lineStr != null) {
                if (!lineStr.trim().equals("")) {//————段不为空，或不都是空格
                    if (lineStr != null) {
                        sourceStr.append(lineStr + "\r\n");// 读取的时添加\r\n是为了方便分段
                    }
                }
                lineStr = in.readLine();
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourceStr.toString();
    }
}
