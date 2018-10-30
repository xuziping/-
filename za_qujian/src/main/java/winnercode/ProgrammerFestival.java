package winnercode;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.*;
import java.util.*;

/**
 * Created by za-zhongwei on 2018/10/24.
 */
public class ProgrammerFestival {
    private static final String FILE_PATH = Class.class.getClass().getResource("/").getPath();
    private static final File INPUT_FILE = new File(FILE_PATH, "input.txt");
    private static final File OUTPUT_FILE = new File(FILE_PATH, "output.txt");

    public static void main(String[] args) throws Exception{
        long ts = System.currentTimeMillis();
        //System.out.println("test");
        T1024(INPUT_FILE);
        System.out.println("Total spend: " + (System.currentTimeMillis() - ts));
    }

    static class Policy
    {

        private int end;
        private int begin;
        public int period;

        private int overrideup;
        private int overridedown;
        private int realdevote;

        public Policy(int end, int begin){
            this.begin = begin;
            this.end = end;
            this.period = end - begin;
            this.overrideup = 0;
            this.overridedown = 0;
            this.realdevote = 0;
        }

        public void calcRealVote(){
            realdevote = period - overrideup - overridedown;
        }

        public void recalcPeriod(){
            period = end - begin;
        }
    }

    static class MapKeyComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer v1, Integer v2) {

            return v2.compareTo(v1);
        }
    }

    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static Map<Integer, ProgrammerFestival.Policy> sortMapByKey(Map<Integer, ProgrammerFestival.Policy> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Integer, ProgrammerFestival.Policy> sortMap = new TreeMap<Integer, ProgrammerFestival.Policy>(
                new ProgrammerFestival.MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    public static void T1024(File file) throws Exception{

        Map<Integer, ProgrammerFestival.Policy> map = new HashMap<>();
        String str;
        try{

            //System.out.println("path: "+file.getCanonicalPath());

            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            String s = "";
            while ((s =bReader.readLine()) != null) {

                String[] policyArr = s.split(" ");

                if(policyArr.length < 2)
                    continue;
                //System.out.println("read input: "+s);
                map.put(new Integer(policyArr[1]), new ProgrammerFestival.Policy(new Integer(policyArr[1]), new Integer(policyArr[0])));
            }
            bReader.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }

//        map.put(9, new Policy(9, 6));
//        map.put(5, new Policy(5, 1));
//        map.put(7, new Policy(7, 3));

        //按Key进行排序
        Map<Integer, ProgrammerFestival.Policy> resultMap = sortMapByKey(map);

        int toDeletePolicy = -1;
        ProgrammerFestival.Policy minRealVotePolicy = null;
        ProgrammerFestival.Policy prePolicy = null;
        for (Map.Entry<Integer, ProgrammerFestival.Policy> entry : resultMap.entrySet()) {
            ProgrammerFestival.Policy currentPolicy = entry.getValue();

            // not the first
            if(prePolicy != null)
            {

                if(currentPolicy.end > prePolicy.begin){
                    if(currentPolicy.begin >= prePolicy.begin){
                        toDeletePolicy = currentPolicy.end;
                        break;
                    }
                    else{
                        int override = currentPolicy.end - prePolicy.begin;
                        prePolicy.overridedown = override;
                        currentPolicy.overrideup = override;
                    }
                }

                prePolicy.calcRealVote();

                if(minRealVotePolicy == null){
                    minRealVotePolicy = prePolicy;
                }
                else{
                    if(minRealVotePolicy.realdevote > prePolicy.realdevote)
                        minRealVotePolicy = prePolicy;
                }
            }

            prePolicy = entry.getValue();
        }

        if(toDeletePolicy !=-1){

            //System.out.println("remove the totally be override policy: "+toDeletePolicy);
            resultMap.remove(toDeletePolicy);
        }
        else{
            prePolicy.calcRealVote();
            if(minRealVotePolicy.realdevote > prePolicy.realdevote)
                minRealVotePolicy = prePolicy;

            //System.out.println("remove the min real vote policy: "+minRealVotePolicy.end);
            resultMap.remove(minRealVotePolicy.end);
        }

        prePolicy = null;
        List<Integer> toDeleteList = new ArrayList<>();
        for (Map.Entry<Integer, ProgrammerFestival.Policy> entry : resultMap.entrySet()) {

            ProgrammerFestival.Policy currentPolicy = entry.getValue();

            if(prePolicy != null)
            {
                if(currentPolicy.end > prePolicy.begin){
                    if(currentPolicy.begin >= prePolicy.begin){
                        toDeleteList.add(currentPolicy.end);
                        continue;
                    }
                    else{
                        prePolicy.begin = currentPolicy.begin;
                        toDeleteList.add(currentPolicy.end);
                        prePolicy.recalcPeriod();
                        continue;
                    }
                }
            }

            prePolicy = entry.getValue();
        }


        for (Integer entry : toDeleteList) {

            //System.out.println("remove: "+entry);
            resultMap.remove(entry);
        }

        int totalPeriod = 0;
        for (Map.Entry<Integer, ProgrammerFestival.Policy> entry : resultMap.entrySet()) {

            //System.out.println("end: "+entry.getValue().end+"; period: "+entry.getValue().period);

            totalPeriod += entry.getValue().period;
        }

        System.out.println("totalPeriod: "+totalPeriod);

        FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        osw.write(String.valueOf(totalPeriod));
        osw.flush();
        osw.close();
        fos.close();
    }
}
