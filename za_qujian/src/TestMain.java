import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestMain {

    private static final String FILE_PATH = TestMain.class.getClass().getResource("/").getPath();
    private static final File INPUT_FILE = new File(FILE_PATH, "input.txt");
    private static final File OUTPUT_FILE = new File(FILE_PATH, "output.txt");

    private static final int MAX_SIZE = 1000000000;
    private static Map<Pair, Long> C_MAP = new HashMap<>(100000);
    private static int MAX_LINE = 0;
    private static boolean hasDirty = false;

    private static long getMaxValueAfterRemoveOneChild(Pair pair) {
        if (C_MAP.get(pair) != null) {
            return C_MAP.get(pair);
        }

        int length = pair.children.size();
        if (length == 1) {
            return 0;
        }
        if (length == 2) {
            return Math.max(pair.children.get(0).getValue(), pair.children.get(1).getValue());
        }
        long v = 0;
        long max = 0;
        int d = 1;
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                for (d = 1; d < length; d++) {
                    if (pair.children.get(d).end >= pair.children.get(0).end) {
                        break;
                    }
                }
                v = pair.end - pair.children.get(d).start;
            } else if (i == length - 1) {
                for (d = length - 2; d >= 0; d--) {
                    if (pair.children.get(d).start <= pair.children.get(i).start) {
                        break;
                    }
                }
                v = pair.children.get(d).end - pair.start;
            } else {
                // 大于3个个数
                v = pair.children.get(i - 1).end + pair.getValue() - pair.children.get(i + 1).start;
            }
            if (v >= max) {
                max = v;
            }
        }
        C_MAP.put(pair, max);
        return max;
    }

    public static void main(String[] args) throws Exception {
        long ts = System.currentTimeMillis();
        TestMain main = new TestMain();
        long result = main.process();
        write2File(OUTPUT_FILE, "" + result);
//        System.out.println("value: " + result);

        System.out.println(System.currentTimeMillis() - ts);
//        System.out.println("SPEND TIME: " + (System.currentTimeMillis() - ts));
//        BitMap bitMap = new BitMap(MAX_LENGTH);
//        BitMap doubleBitMap = new BitMap(MAX_LENGTH);
//        for(String line: sortedLines) {
//            String[] values = line.split(" ");
//            int start = Integer.parseInt(values[0].trim());
//            int end = Integer.parseInt(values[1].trim());
//            if (start <= end && start > 0 && end <= MAX_LENGTH ){
//                for(int i = start; i <= end; i++) {
//                    if(bitMap.getBit(i) != 0) {
//                        doubleBitMap.setBit(i);
//                    } else {
//                        bitMap.setBit(i);
//                    }
//                }
//            }
//        }
    }


    public static void write2File(File file, String data) throws Exception {
        try (FileWriter writer = new FileWriter(file);
             BufferedWriter out = new BufferedWriter(writer)
        ) {
            out.write(data);
            out.flush();
        }
    }

    public long process() throws Exception {
        List<Pair> pairs = loadData();
        long result = 0;
        if (pairs.size() == 1) {
            result = pairs.get(0).getValue();
        } else {
            List<Pair> newPairs = merge(pairs);
            if (hasDirty || newPairs.parallelStream().filter(x -> x.unneed == true).findFirst().isPresent()) {
                result = newPairs.parallelStream().mapToLong(x -> x.getValue()).sum();
//                for(Pair p: newPairs){
//                    result += p.getValue();
//                }
            } else if (newPairs.size() > 1) {
                newPairs = newPairs.parallelStream().sorted((x, y) -> {
                    return (int) ((x.getValue() - getMaxValueAfterRemoveOneChild(x)) -
                            (y.getValue() - getMaxValueAfterRemoveOneChild(y)));
                }).collect(Collectors.toList());
                result = getMaxValueAfterRemoveOneChild(newPairs.get(0))
                        + newPairs.subList(1, newPairs.size()).parallelStream().mapToLong(x -> x.getValue()).sum();
                ;
            } else {
                result = getMaxValueAfterRemoveOneChild(newPairs.get(0));
            }
        }
        return result;
    }

    public List<Pair> loadData() throws Exception {
        long ts = System.currentTimeMillis();
        List<Pair> ret = readLines(INPUT_FILE);
        System.out.println("loadData spend: " + (System.currentTimeMillis() - ts));
        return ret;
    }

//    public List<Pair> trimPairs(List<Pair> pairs) throws Exception {
//        List<Pair> ret = new ArrayList<>();
//        if (pairs != null && pairs.size() > 0) {
//            for (Pair p : pairs) {
//                boolean isIncluded = false;
//                for (Pair r : ret) {
//                    if (r.start <= p.start && r.end >= p.end) {
//                        isIncluded = true;
//                    }
//                    if (r.start <= p.start) {
//
//                    }
//                    if (r.end >= r.end) {
//
//                    }
//                }
//                if (!isIncluded) {
//                    ret.add(p);
//                }
//            }
//        }
//        return ret;
//    }
//
//    static public class BitMap {
//        final int[] BIT_VALUE = {0x00000001, 0x00000002, 0x00000004, 0x00000008, 0x00000010, 0x00000020,
//                0x00000040, 0x00000080, 0x00000100, 0x00000200, 0x00000400, 0x00000800, 0x00001000, 0x00002000, 0x00004000,
//                0x00008000, 0x00010000, 0x00020000, 0x00040000, 0x00080000, 0x00100000, 0x00200000, 0x00400000, 0x00800000,
//                0x01000000, 0x02000000, 0x04000000, 0x08000000, 0x10000000, 0x20000000, 0x40000000, 0x80000000};
//        long length;
//        int[] bitsMap;
//
//        public BitMap(long length) {
//            this.length = length;
//            bitsMap = new int[(int) (length >> 5) + ((length & 31) > 0 ? 1 : 0)];
//        }
//
//        public int getBit(long index) {
//            if (index < 0 || index > length) {
//                throw new IllegalArgumentException("length value illegal!");
//            }
//            int intData = (int) bitsMap[(int) ((index - 1) >> 5)];
//            return ((intData & BIT_VALUE[(int) ((index - 1) & 31)])) >>> ((index - 1) & 31);
//        }
//
//        public void setBit(long index) {
//            if (index < 0 || index > length) {
//                throw new IllegalArgumentException("length value illegal!");
//            }
//            int belowIndex = (int) ((index - 1) >> 5);
//            int offset = (int) ((index - 1) & 31);
//            int inData = bitsMap[belowIndex];
//            bitsMap[belowIndex] = inData | BIT_VALUE[offset];
//        }
//    }

    public List<Pair> merge(List<Pair> intervals) {
        long ts = System.currentTimeMillis();
        List<Pair> ret = new ArrayList<>(MAX_LINE);
        intervals = intervals.parallelStream().sorted((x, y) -> {
            return x.start - y.start;
        }).collect(Collectors.toList());
        Pair current = new Pair(intervals.get(0).start, intervals.get(0).end);
        current.addChild(intervals.get(0));
        ret.add(current);
        for (int i = 1; i < intervals.size(); i++) {
            Pair p = intervals.get(i);
            if (p.start <= current.end) {
                if (p.end > current.end) {
                    current.addChild(p);
                } else {
                    current.addIncluded(p);
                }
            } else {
                current = new Pair(p.start, p.end);
                current.addChild(p);
                ret.add(current);
            }
        }
//        ret.forEach(x->System.out.println(x.toString()));
        System.out.println("Merge spend: " + (System.currentTimeMillis() - ts));
        return ret;
    }

    public List<Pair> readLines(File file) throws Exception {
        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 30 * 1024 * 1024);) {
            String line = null;
            String[] values = null;
            int start, end = 0;
            MAX_LINE = Integer.parseInt(reader.readLine().trim());
            List<Pair> ret = new ArrayList<>(MAX_LINE);
            Map<Integer, Pair> cached = new HashMap<>(MAX_LINE);
            while ((line = reader.readLine()) != null) {
                values = line.split(" ");
                start = Integer.parseInt(values[0].trim());
                end = Integer.parseInt(values[1].trim());
                if(cached.get(start) != null){
                    hasDirty = true;
                    Pair v = cached.get(start);
                    if(v.end < end) {
                        v.end = end;
                    }
                } else {
                    Pair v = new Pair(start, end);
                    ret.add(v);
//                    cached.put(start, v);
                }
            }
            return ret;
        }
    }

    public class Pair {
        int start;
        int end;
        Integer value;
        boolean unneed = false;
        List<Pair> children;

        public Pair(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getValue() {
            if (value != null) {
                return value.intValue();
            }
            value = end - start;
            return value;
        }

        public void addChild(Pair p) {
            if (children == null) {
                children = new ArrayList<>(MAX_LINE / 10);
            }
            if (children.size() >= 2) {
                for (int i = children.size() - 2; i >= 0; i--) {
                    if (p.start <= children.get(i).end) {
                        Pair unneed = children.get(i + 1);
                        children.remove(unneed);
                        addIncluded(unneed);
                    }
                }
            } else if (children.size() == 1 && p.start == children.get(0).start) {
                Pair unneed = children.get(0);
                children.remove(unneed);
                addIncluded(unneed);
            }

            children.add(p);
            end = p.end;
        }

        public void addIncluded(Pair p) {
            unneed = true;
        }

//        public String toString() {
//            final StringBuilder sb = new StringBuilder("[start=" + start + ",end=" + end);
//            if (children != null) {
//                sb.append(",children=");
//                for(int i=0;i<children.size();i++){
//                    sb.append( "{" + children.get(i).start +"," + children.get(i).end + "} ");
//                }
//            }
//            if (unneed != null) {
//                sb.append( ",unneed=");
//                unneed.forEach(x->{
//                    sb.append( "{" + x.start +"," + x.end + "} ");
//                });
//            }
//            return sb.toString() + "]";
//        }
    }
}