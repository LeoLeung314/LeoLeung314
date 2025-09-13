import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Locale;

public class PaperChecker {
    public static void main(String[] args) {

        //一定要给出三个绝对路径，否则结束程序
        if(args==null || args.length !=3){
            System.err.println("用法：java PaperChecker <原文绝对路径><抄袭版绝对路径><输出绝对路径>");
            System.exit(1);
        }


        //获取路径
        Path originalPath = Paths.get(args[0]);
        Path plagiarizedPath = Paths.get(args[1]);
        Path outputPath = Paths.get(args[2]);

        try{
            //文件读取，“UTF-8”形式
            String originalText = readFile(originalPath);
            String plagiarizedText = readFile(plagiarizedPath);

            //统一文本格式，将文本规范化，减少空白、标点等的差异干扰,此处两个字符串采用缩写首字母形式
            String N_O = normalizedText(originalText);
            String N_P = normalizedText(plagiarizedText);

            //相似度计算，使用最长公共子序列（LCS），以“原文长度”为分母
            double similarity = Similarity(N_O,N_P);


            //格式化输出到文件当中，精确到小数点后两位
            String result = String.format(Locale.ROOT,"%.2f",similarity);
            writeFile(outputPath,result);
        }catch (IOException e){
            //防止文件读写异常
            System.err.println("文件读写失败："+e.getMessage());
            System.exit(2);
        }catch (Exception e){
            //避免程序无响应
            System.err.println("程序运行出错："+e.getMessage());
            System.exit(3);
        }

    }


    //以“UTF-8 字符串”形式读取整个文件的内容
    private static String readFile(Path path) throws IOException{
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);

    }

    //将字符串以UTF-8的形式写入文件，若没有上级目录（父目录）则先创建上级目录
    private static void writeFile(Path path,String content) throws IOException{
        Path parent  = path.getParent();
        if(parent!=null){
            Files.createDirectories(parent);
        }
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /*文本规范化，Unicode规范化（NFKC）：统一全角/半角、兼容字符等
    去除空格、换行、制表符、所有Unicode标点，英文转小写
    */
    private static String normalizedText(String text){
        if(text == null){
            return "";
        }
        String normalized = Normalizer.normalize(text,Normalizer.Form.NFKC);
        normalized = normalized.replaceAll("\\s+","");
        normalized = normalized.replaceAll("\\p{P}+","");
        normalized = normalized.toLowerCase(Locale.ROOT);
        return normalized;
    }

    // 计算相似度：LCS(原文，抄袭)/原文长度
    private static double Similarity(String original, String plagiarized){
        if(original.isEmpty()){
            return plagiarized.isEmpty()?1.00:0.00;
        }
        int lcs = LCS(original,plagiarized);
        return  (double) lcs / (double) original.length();
    }


    /*
    * 最长公共子序列：Longest Common Subsequence  此处采用首字母简写
    * 使用二维动态规划实现LCS，避免下标越界*/
    private static int LCS(String original, String plagiarized){

        char[] O_C = original.toCharArray();
        char[] P_C = plagiarized.toCharArray();
        int O_L = O_C.length, P_L = P_C.length;
        if(O_L==0 || P_L==0) return 0;
        int[][] dp = new int[O_L+1][P_L+1];
        for (int i = 1; i <=O_L; i++) {
            for (int j = 1; j <=P_L; j++) {
                if(O_C[i-1]==P_C[j-1]){
                    dp[i][j] = dp[i-1][j-1]+1;
                }else{
                    dp[i][j] = Math.max(dp[i-1][j],dp[i][j-1]);
                }
            }
        }
        return dp[O_L][P_L];
    }

}
