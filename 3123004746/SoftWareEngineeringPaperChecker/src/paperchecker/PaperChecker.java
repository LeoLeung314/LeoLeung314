package paperchecker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Locale;

public class PaperChecker {
    public static void main(String[] args) {

        // 必须给出三个“绝对路径”，否则结束程序
        if (args == null || args.length != 3) {
            System.err.println("用法：java paperchecker.PaperChecker <原文绝对路径> <抄袭版绝对路径> <输出绝对路径>");
            System.exit(1);
        }

        // 校验三个参数均为绝对路径（严格按测试要求）
        Path originalPath = Paths.get(args[0]);
        Path plagiarizedPath = Paths.get(args[1]);
        Path outputPath = Paths.get(args[2]);
        if (!originalPath.isAbsolute() || !plagiarizedPath.isAbsolute() || !outputPath.isAbsolute()) {
            System.err.println("错误：请确保三个参数都是绝对路径。示例：C:\\Users\\you\\data\\orig.txt");
            System.exit(1);
        }
        // 规范化路径（不改变相对/绝对属性）
        originalPath = originalPath.normalize();
        plagiarizedPath = plagiarizedPath.normalize();
        outputPath = outputPath.normalize();

        try {
            // 文件读取（UTF-8）
            String originalText = readFile(originalPath);
            String plagiarizedText = readFile(plagiarizedPath);

            // 文本规范化
            String normalizedOriginal = normalizedText(originalText);
            String normalizedPlagiarized = normalizedText(plagiarizedText);

            // 相似度：LCS(原文, 抄袭)/原文长度
            double similarity = calculateSimilarity(normalizedOriginal, normalizedPlagiarized);

            // 写出两位小数结果
            String result = String.format(Locale.ROOT, "%.2f", similarity);
            writeFile(outputPath, result);
        } catch (IOException e) {
            System.err.println("文件读写失败：" + e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("程序运行出错：" + e.getMessage());
            System.exit(3);
        }
    }

    // 以 UTF-8 读取整个文件
    private static String readFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // 以 UTF-8 写入；若父目录不存在则创建
    private static void writeFile(Path path, String content) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /* 文本规范化：
       - Unicode NFKC
       - 去空白与所有 Unicode 标点
       - 英文转小写 */
    private static String normalizedText(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC);
        normalized = normalized.replaceAll("\\s+", "");
        normalized = normalized.replaceAll("\\p{P}+", "");
        normalized = normalized.toLowerCase(Locale.ROOT);
        return normalized;
    }

    // 计算相似度：LCS(原文, 抄袭)/原文长度
    private static double calculateSimilarity(String original, String plagiarized) {
        if (original.isEmpty()) {
            return plagiarized.isEmpty() ? 1.00 : 0.00;
        }
        int lcs = longestCommonSubsequence(original, plagiarized);
        return (double) lcs / (double) original.length();
    }

    /* LCS：二维动态规划实现，避免下标越界 */
    private static int longestCommonSubsequence(String original, String plagiarized) {
        char[] originalCharArray = original.toCharArray();
        char[] plagiarezedCharArray = plagiarized.toCharArray();
        int originalLength = originalCharArray.length, plagiarezedLength = plagiarezedCharArray.length;
        if (originalLength == 0 || plagiarezedLength == 0) {
            return 0;
        }
        int[][] dp = new int[originalLength + 1][plagiarezedLength + 1];
        for (int i = 1; i <= originalLength; i++) {
            for (int j = 1; j <= plagiarezedLength; j++) {
                if (originalCharArray[i - 1] == plagiarezedCharArray[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[originalLength][plagiarezedLength];
    }
}