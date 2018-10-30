package winnercode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author: hongjie
 * @date: 2018/10/24
 */
public class Mars {

    private static final String FILE_PATH = Class.class.getClass().getResource("/").getPath();
    private static final File INPUT_FILE = new File(FILE_PATH, "input.txt");
    private static final File OUTPUT_FILE = new File(FILE_PATH, "output.txt");

    public static boolean put(String line, TreeMap<Integer, Integer> map) {
        if (line == null) {
            return false;
        }
        int i = 0;
        char c;
        int v1 = 0;
        while ((c = line.charAt(i++)) != 32) {
            v1 = v1 * 10 + c - 48;
        }
        int v2 = 0;
        while (i < line.length()) {
            v2 = v2 * 10 + line.charAt(i++) - 48;
        }
        map.put(v2, v1);

        return true;
    }

    public static void main(String[] args) throws Exception {

        long ts = System.currentTimeMillis();

        TreeMap<Integer, Integer> map = new TreeMap<>((item1, item2) -> item1 < item2 ? 1 : item1.equals(item2) ? 0 : -1);

        BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));
//        String line;
        br.readLine();

        while (put(br.readLine(), map)){} ;
//
//        while ((line = br.readLine()) != null) {
//            String[] ss = line.split("\\s+");
//            map.put(Integer.valueOf(ss[1]), Integer.valueOf(ss[0]));
//        }

        int leastCon = 1000000000;
        int total = 0;

        Map.Entry<Integer, Integer> lastEntry = null;
        Integer lastCon = null;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {

            total += entry.getKey() - entry.getValue();
            int currentCon = entry.getKey() - entry.getValue();

            if (lastEntry != null) {

                if (entry.getValue() >= lastEntry.getValue()) {
                    total -= currentCon;
                    leastCon = 0;
                    continue;
                }

                int intersection = entry.getKey() - lastEntry.getValue();

                if (intersection > 0) {
                    total -= intersection;

                    int previousCon = lastCon - intersection;
                    currentCon = currentCon - intersection;

                    if (previousCon <= 0) {
                        leastCon = 0;
                    } else if (previousCon < leastCon) {
                        leastCon = previousCon;
                    }
                }
                if (currentCon < leastCon) {
                    leastCon = currentCon;
                }
            }

            lastEntry = entry;
            lastCon = currentCon;
        }

        writeToFile(total - leastCon);

        System.out.println("Total spend: " + (System.currentTimeMillis() - ts));
    }

    private static void writeToFile(int result) throws IOException {
        System.out.println(result);
        FileWriter fileWriter = new FileWriter(OUTPUT_FILE);
        fileWriter.write(result + "");
        fileWriter.close();
    }

}
