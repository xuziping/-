
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @author za-xuzhiping
 * @Date 2018/10/25
 * @Time 16:31
 */
public class GenerateData {

    private static final String FILE_PATH = TestMain.class.getClass().getResource("/").getPath();
    private static final File INPUT_FILE = new File(FILE_PATH, "input.txt");
    private static final File OUTPUT_FILE = new File(FILE_PATH, "output.txt");

    public static void main(String[] args) throws Exception {
        write2File(INPUT_FILE);
    }

    private static final int MAX_LINE = 1000000;
    private static final int MAX_COUNT = 1000000000;

    public static void write2File(File file) throws Exception {
        try (FileWriter writer = new FileWriter(file);
             BufferedWriter out = new BufferedWriter(writer)
        ) {
            out.write(MAX_LINE + "\r\n");
            for(int i = 0; i<MAX_LINE; i++) {
//                int c = (int)(Math.random()* (MAX_COUNT));
//                int d = (int)(Math.random()* (MAX_COUNT-c));

                int c = i;
                int d = (int)(Math.random()*10);
                out.write( c+ " " + (c+d) +"\r\n");
                out.flush();
            }
        }
    }
}
