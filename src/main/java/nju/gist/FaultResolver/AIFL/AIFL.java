package nju.gist.FaultResolver.AIFL;

import nju.gist.Common.Schema;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.*;

public class AIFL {
    private final Checker checker;
    private final List<Integer> faultCase;
    private final List<Set<Integer>> Values;
    private int Threshold;
    private Set<List<Integer>> Tfail;
    private Set<List<Integer>> Tpass;

    public AIFL(Checker checker, List<Integer> faultCase, List<Set<Integer>> Values) {
        this.checker = checker;
        this.faultCase = faultCase;
        this.Values = Values;
    }

    public void setThreshold(int Threshold){
        this.Threshold = Threshold;
    }

    public void setTfailAndTpass(List<List<Integer>> Tfail, List<List<Integer>> Tpass) {
        this.Tfail = new HashSet<>(Tfail);
        this.Tpass = new HashSet<>(Tpass);
    }

    public Set<List<Integer>> getTfail() {
        return Tfail;
    }

    public Set<List<Integer>> getTpass() {
        return Tpass;
    }

    public int getFaultCaseSize() {
        return faultCase.size();
    }

    public int getThreshold() {
        return Threshold;
    }

    public Set<List<Integer>> getSchemaSet(Set<List<Integer>> T){
        Set<List<Integer>> res = new HashSet<>();

        for (List<Integer> testCase : T) {
            res.addAll(getAllSchemaFromTestCase(testCase));
        }

        return res;
    }

    private Set<List<Integer>> getAllSchemaFromTestCase(List<Integer> testCase) {
        Set<List<Integer>> res = new HashSet<>();

        Schema casePattern = new Schema(testCase.size());
        casePattern.set(0, testCase.size());

        ArrayList<Schema> list = new ArrayList<>(1 << testCase.size());
        list.add(new Schema(testCase.size()));

        int factorIndex = casePattern.nextSetBit(0);
        while (factorIndex != -1) {
            int curLen = list.size();
            for (int i = 0; i < curLen; i++) {
                Schema temp = (Schema) list.get(i).clone();
                temp.set(factorIndex);
                res.add(Productor.genSchema(temp, testCase));
                list.add(temp);
            }
            factorIndex = casePattern.nextSetBit(factorIndex + 1);
        }

        return res;
    }

    /**
     * 求两集合的差集, 注意此函数不会改变输入参数 A B
     * @param A {[1],[2],[1,2]}
     * @param B {[1],{2]}
     * @return  {[1,2]}
     */
    public Set<List<Integer>> minus(Set<List<Integer>> A, Set<List<Integer>> B) {
        Set<List<Integer>> res = new HashSet<>();
        for (List<Integer> a : A) {
            if(!B.contains(a)){
                res.add(a);
            }
        }
        return res;
    }

    public Set<List<Integer>> generateAT(int strength, Set<List<Integer>> Tfail) {
        Set<List<Integer>> res = new HashSet<>();
        for (List<Integer> failTestCase : Tfail) {
            res.addAll(mutate(strength, failTestCase));
        }
        return res;
    }

    private Set<List<Integer>> mutate(int strength, List<Integer> failTestCase) {
        Set<List<Integer>> res = new HashSet<>();
        // 找到哪些地方应该替换
        Set<Schema> allPositions = getAllPossiblePositions(failTestCase.size(), strength);

        for (Schema position : allPositions) {
            int nextSetBitIndex = position.nextSetBit(0);
            List<Integer> newTestCase = new ArrayList<>(failTestCase);
            while (nextSetBitIndex != -1) {
                int prepareValue = 0;
                Set<Integer> values = Values.get(nextSetBitIndex);
                for (int value : values) {
                    if(value != newTestCase.get(nextSetBitIndex)) {
                        prepareValue = value;
                    }
                }
                newTestCase.set(nextSetBitIndex, prepareValue);
                nextSetBitIndex = position.nextSetBit(nextSetBitIndex + 1);
            }
            res.add(newTestCase);
        }

        return res;
    }

    private Set<Schema> getAllPossiblePositions(int size, int strength) {
        Set<Schema> res = new HashSet<>();

        long s = (1L << strength) - 1;
        while (s < (1L << size)) {
            Schema Schema = new Schema(size);
            long temp = s;
            for (int i = 0; i < size; i++) {
                if(temp % 2 == 1) Schema.set(i);
                temp = temp >> 1;
                if(temp == 0) break;
            }
            res.add(Schema);

            // don't ask me what this is, I don't know, but it works !!!
            long x = s & -s;
            long y = s + x;
            s = ((s & ~y) / x >> 1) | y;
        }

        return res;
    }

    public Set<List<Integer>> getATpass(Set<List<Integer>> AT) {
        Set<List<Integer>> res = new HashSet<>();
        for (List<Integer> t : AT) {
            if(checker.executeTestCase(t)) {
                res.add(t);
            }
        }
        return res;
    }
}
