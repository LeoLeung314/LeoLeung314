package Test;
import org.junit.jupiter.api.Test;
import paperchecker.PaperChecker;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class PaperCheckerE2ETest {

    private static double runOnce(String a, String b) throws Exception {
        Path orig = Files.createTempFile("orig", ".txt");
        Path plag = Files.createTempFile("plag", ".txt");
        Path out  = Files.createTempFile("ans", ".txt");

        Files.writeString(orig, a, StandardCharsets.UTF_8);
        Files.writeString(plag, b, StandardCharsets.UTF_8);

        String[] args = new String[] {
                orig.toAbsolutePath().toString(),
                plag.toAbsolutePath().toString(),
                out.toAbsolutePath().toString()
        };
        PaperChecker.main(args);

        String s = Files.readString(out, StandardCharsets.UTF_8).trim();
        return Double.parseDouble(s);
    }

    @Test
    void identicalChinese() throws Exception {
        double s = runOnce("今天晚上我要去看电影", "今天晚上我要去看电影");
        assertEquals(1.00, s, 1e-9);
    }

    @Test
    void completelyDifferent() throws Exception {
        double s = runOnce("abcdef", "uvwxyz");
        assertEquals(0.00, s, 1e-9);
    }

    @Test
    void mildDeletion() throws Exception {
        double s = runOnce("今天晚上我要去看电影", "我晚上要去看电影");
        assertTrue(s > 0.50 && s < 1.00);
    }

    @Test
    void punctuationWhitespaceOnly() throws Exception {
        double s = runOnce("今 天，晚上！我要 去看 电影。", "今天晚上我要去看电影");
        assertEquals(1.00, s, 1e-9);
    }

    @Test
    void caseOnly() throws Exception {
        double s = runOnce("ABC中文", "abc中文");
        assertEquals(1.00, s, 1e-9);
    }

    @Test
    void fullHalfWidth() throws Exception {
        double s = runOnce("ＡＢＣ中文", "ABC中文");
        assertEquals(1.00, s, 1e-9);
    }

    @Test
    void digitsFullHalfWidth() throws Exception {
        double s = runOnce("１２３45中文", "12345中文");
        assertEquals(1.00, s, 1e-9);
    }

    @Test
    void bothEmpty() throws Exception {
        double s = runOnce("", "");
        assertEquals(1.00, s, 1e-9);
    }

    @Test
    void originalEmptyOnly() throws Exception {
        double s = runOnce("", "abc");
        assertEquals(0.00, s, 1e-9);
    }

    @Test
    void largeTextNearOne() throws Exception {
        String a = "a".repeat(10000);
        String b = "a".repeat(9990);
        double s = runOnce(a, b);
        assertTrue(s >= 0.998 && s <= 1.0);
    }

    @Test
    void punctuationVsEmpty() throws Exception {
        double s = runOnce("，。、！", "");
        assertEquals(1.00, s, 1e-9);
    }

    @Test
    void smallReorder() throws Exception {
        double s = runOnce("ABCDE", "ACBED"); // LCS = 4 => 4/5 = 0.8
        assertEquals(0.80, s, 1e-6);
    }
}