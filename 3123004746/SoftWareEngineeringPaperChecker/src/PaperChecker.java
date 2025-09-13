import java.nio.file.Path;
import java.nio.file.Paths;

public class PaperChecker {
    public static void main(String[] args) {
        if(args==null || args.length !=3){
            System.err.println("用法：java PaperChecker <原文绝对路径><抄袭版绝对路径><输出绝对路径>");
            System.exit(1);
        }

        Path originalPath = Paths.get(args[0]);
        Path plagiarizedPath = Paths.get(args[1]);
        Path outputPath = Paths.get(args[2]);



    }
}
