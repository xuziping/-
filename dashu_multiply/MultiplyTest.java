import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author za-xuzhiping
 * @Date 2019/6/27
 * @Time 11:32
 */
/**
 multiply num input "12345", "67"
 output: ""

 */

/**
 13 * 21 =

 13
 26
 273


 15 * 27 =

 105
 30
 405

 19* 19=
 171
 19
 381
 */
public class MultiplyTest {

    public String multiply(String str1, String str2) {
        if("0".equals(str1) || "0".equals(str2)) {
            return "0";
        }
        int negativeCount = 0;
        if(str1.startsWith("-")) {
            str1 = str1.substring(1);
            negativeCount++;
        }
        if(str2.startsWith("-")) {
            str2 = str2.substring(1);
            negativeCount++;
        }
        String[] number1 = str1.split("");
        String[] number2 = str2.split("");

        // 标志乘法行之间的进位
        int plusNumber = 0;
        // 倒序存入中间结果记录
        List<Integer> tmpValues = new ArrayList<Integer>();
        for (int i = number1.length - 1; i >= 0; i--) {
            for (int j = number2.length - 1, index = 0; j >= 0; j--, index++) {
                addNumber(tmpValues, index+plusNumber, Integer.valueOf(number1[i]) * Integer.valueOf(number2[j]));
            }
            plusNumber++;
        }
        return getResult(tmpValues, negativeCount);
    }

    private String getResult(List<Integer> tmpValues, int negativeCount) {
        StringBuilder ret = new StringBuilder();
        for(int i=tmpValues.size()-1; i>=0; i--){
            if(ret.length()==0 && Integer.valueOf(0).equals(tmpValues.get(i))){
                continue;
            }
            ret.append(tmpValues.get(i));
        }
        return (negativeCount % 2 == 0 ? "" : "-") + ret.toString();
    }

    /**
     * 以倒序方式存储乘法中间结果
     * @param values
     * @param index
     * @param value
     */
    private void addNumber(List<Integer> values, int index, Integer value) {
        if (index >= values.size()) {
            if(value < 10) {
                values.add(value);
                return;
            } else {
                values.add(value % 10);
                addNumber(values, index+1, value /10);
                return;
            }
        }
        Integer existedValue = values.get(index);
        Integer total = existedValue + value;
        Integer currentValue = total % 10;
        values.set(index, currentValue);
        Integer plus = total / 10;
        addNumber(values, index + 1, plus);
    }

    public static void main(String args[]) {
        MultiplyTest instance = new MultiplyTest();
        Assert.assertEquals("540", instance.multiply("12", "45"));
        Assert.assertEquals("55603788064", instance.multiply("1229302", "45232"));
        Assert.assertEquals("0", instance.multiply("1222", "0"));
        Assert.assertEquals("0", instance.multiply("0", "4523"));
        Assert.assertEquals("389364196", instance.multiply("-39203", "-9932"));
        Assert.assertEquals("4364696750238885060000", instance.multiply("39203902230", "111333222000"));
        Assert.assertEquals("-390199961616497", instance.multiply("9999230239", "-39023"));
//        Assert.assertEquals("540", instance.multiply("12", "45"));
        System.out.println("Finished");
    }
}
