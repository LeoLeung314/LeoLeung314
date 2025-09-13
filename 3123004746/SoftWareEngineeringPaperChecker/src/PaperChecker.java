import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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


}
