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


        //获取路径，路径为相对于当前工作目录的绝对路径
        Path originalPath = toAbsolute(Paths.get(args[0]));
        Path plagiarizedPath = toAbsolute(Paths.get(args[1]));
        Path outputPath = toAbsolute(Paths.get(args[2]));

        try{
            //文件读取，“UTF-8”形式
            String originalText = readFile(originalPath);
            String plagiarizedText = readFile(plagiarizedPath);

            //统一文本格式，将文本规范化，减少空白、标点等的差异干扰,此处两个字符串采用缩写首字母形式
            String normalizedOriginal = normalizedText(originalText);
            String normalizedPlagiarized = normalizedText(plagiarizedText);

            //相似度计算，使用最长公共子序列（LCS），以“原文长度”为分母
            double similarity = calculateSimilarity(normalizedOriginal,normalizedPlagiarized);


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

    private static Path toAbsolute(Path path){
        //当前工作目录
        Path base = Paths.get(System.getProperty("user.dir"));
        return path.isAbsolute()?path.normalize():base.resolve(path).normalize();
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
    private static double calculateSimilarity(String original, String plagiarized){
        if(original.isEmpty()){
            return plagiarized.isEmpty()?1.00:0.00;
        }
        int lcs = longestCommonSubsequence(original,plagiarized);
        return  (double) lcs / (double) original.length();
    }


    /*
    * 最长公共子序列：Longest Common Subsequence
    * 使用二维动态规划实现LCS，避免下标越界*/
    private static int longestCommonSubsequence(String original, String plagiarized){

        char[] originalCharArray = original.toCharArray();
        char[] plagiarezedCharArray = plagiarized.toCharArray();
        int originalLength = originalCharArray.length, plagiarezedLength = plagiarezedCharArray.length;
        if(originalLength ==0 || plagiarezedLength ==0) {
            return 0;
        }
        int[][] dp = new int[originalLength +1][plagiarezedLength +1];
        for (int i = 1; i <= originalLength; i++) {
            for (int j = 1; j <= plagiarezedLength; j++) {
                if(originalCharArray[i-1]== plagiarezedCharArray[j-1]){
                    dp[i][j] = dp[i-1][j-1]+1;
                }else{
                    dp[i][j] = Math.max(dp[i-1][j],dp[i][j-1]);
                }
            }
        }
        return dp[originalLength][plagiarezedLength];
    }

}
