
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TestMain {

    private static final String FILE_PATH = TestMain.class.getClass().getResource("/").getPath();
    private static final File INPUT_FILE = new File(FILE_PATH, "input.txt");
    private static final File OUTPUT_FILE = new File(FILE_PATH, "output.txt");

    private static Map<Pair, Long> C_MAP = new HashMap<>(100000);
    private static int MAX_LINE = 0;

    private static long getMaxValueAfterRemoveOneChild(Pair pair) {
        if (C_MAP.get(pair) != null) {
            return C_MAP.get(pair);
        }

        int length = pair.children.size();
        if (length == 1) {
            return 0;
        }
        if (length == 2) {
            return Math.max(pair.children.first().getValue(), pair.children.last().getValue());
        }
        List<Pair> childrenList = pair.getChildrenList();
        long v = 0;
        long max = 0;
        int d = 1;
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                for (d = 1; d < length; d++) {
                    if (childrenList.get(d).end >= childrenList.get(0).end) {
                        break;
                    }
                }
                v = pair.end - childrenList.get(d).start;
            } else if (i == length - 1) {
                for (d = length - 2; d >= 0; d--) {
                    if (childrenList.get(d).start <= childrenList.get(i).start) {
                        break;
                    }
                }
                v = childrenList.get(d).end - pair.start;
            } else {
                // 大于3个个数
                v = childrenList.get(i - 1).end + pair.getValue() - childrenList.get(i + 1).start;
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
        System.out.println("value: " + result);

        System.out.println("Total spend: " + (System.currentTimeMillis() - ts));
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
        TreeSet<Pair> pairs = loadData();
        long result = 0;
        if (pairs.size() == 1) {
            result = pairs.pollFirst().getValue();
        } else {
            List<Pair> newPairs = merge(pairs);
            if (newPairs.parallelStream().filter(x -> x.unneed == true).findFirst().isPresent()) {
                result = newPairs.parallelStream().mapToLong(x -> x.getValue()).sum();
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

    public TreeSet<Pair> loadData() throws Exception {
        long ts = System.currentTimeMillis();
        TreeSet<Pair> ret = readLines(INPUT_FILE);
//        System.out.println("loadData spend: " + (System.currentTimeMillis() - ts));
        return ret;
    }

    public List<Pair> merge(TreeSet<Pair> intervals) {
        long ts = System.currentTimeMillis();
        List<Pair> ret = new LinkedList<>();
        Iterator<Pair> it = intervals.iterator();
        Pair current = it.next();
        Pair node = new Pair(current.start, current.end, true);
        ret.add(node);
        while (it.hasNext()) {
            Pair p = it.next();
            if (p.start <= node.end) {
                if (p.end > node.end) {
                    node.addChild(p);
                } else {
                    node.addIncluded(p);
                }
            } else {
                node = new Pair(p.start, p.end, true);
                ret.add(node);
            }
        }
//        ret.forEach(x->System.out.println(x.toString()));
//        System.out.println("Merge spend: " + (System.currentTimeMillis() - ts));
        return ret;
    }

    public TreeSet<Pair> readLines(File file) throws Exception {
        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 10 * 1024 * 1024);) {
            String line = null;
            String[] values = null;
            int start, end = 0;
            MAX_LINE = Integer.parseInt(reader.readLine().trim());
            TreeSet<Pair> ret = new TreeSet();
            while ((line = reader.readLine()) != null) {
//                try {
//                    rowNum++;
                    values = line.split(" ");
                    start = Integer.parseInt(values[0].trim());
                    end = Integer.parseInt(values[1].trim());
                    ret.add(new Pair(start, end, false));
//                } catch(Exception e) {
//                    System.out.println("Error Line: " + rowNum);
//                }
            }
            return ret;
        }
    }

    public class Pair implements Comparable<Pair>  {
        int start;
        int end;
        Integer value;
        boolean unneed = false;
        TreeSet<Pair> children = null;
        List<Pair> childrenList = null;
        public Pair(int start, int end, boolean addChild) {
            this.start = start;
            this.end = end;
            if(addChild) {
                if(children == null){
                    children = new TreeSet<>();
                }
                children.add(new Pair(start, end, false));
            }
        }

        public int getValue() {
            if (value != null) {
                return value.intValue();
            }
            value = end - start;
            return value;
        }
        public List<Pair> getChildrenList(){
            if(childrenList == null) {
                childrenList = new ArrayList<>(children);
            }
            return childrenList;
        }
        public void addChild(Pair p) {
            if (children.size() >= 2) {
                Iterator<Pair> it = children.descendingIterator();
                it.next();
                while(it.hasNext() && p.start <= it.next().end) {
                    children.pollLast();
                    unneed = true;
                }
            } else if (children.size() == 1 && p.start == children.first().start) {
                children.pollFirst();
                unneed = true;
            }
            children.add(p);
            end = p.end;
        }

        public void addIncluded(Pair p) {
            unneed = true;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("[start=" + start + ",end=" + end);
            if (children != null) {
                childrenList = getChildrenList();
                sb.append(",children=");
                for(int i=0;i<childrenList.size();i++){
                    sb.append( "{" + childrenList.get(i).start +"," + childrenList.get(i).end + "} ");
                }
            }
            if (unneed) {
                sb.append( ",unneed=true");
            }
            return sb.toString() + "]";
        }

        @Override
        public int compareTo(Pair o) {
            if(start != o.start) {
                return start - o.start;
            }
            return end - o.end;
        }
    }
}