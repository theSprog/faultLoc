package nju.gist.FaultResolver.AIFL;

import nju.gist.Common.Comb;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;
import org.raistlic.common.permutation.Combination;

import java.util.*;

public class AIFL {
    private final Checker checker;
    private final TestCase faultCase;
    private final List<Set<Integer>> Values;
    private int Threshold;
    private Set<TestCase> Tfail;
    private Set<TestCase> Tpass;

    public AIFL(Checker checker, TestCase faultCase, List<Set<Integer>> Values) {
        this.checker = checker;
        this.faultCase = faultCase;
        this.Values = Values;
    }

    public void setThreshold(int Threshold){
        this.Threshold = Threshold;
    }

    public void setTfailAndTpass(List<TestCase> Tfail, List<TestCase> Tpass) {
        this.Tfail = new HashSet<>(Tfail);
        this.Tpass = new HashSet<>(Tpass);
    }

    public Set<TestCase> getTfail() {
        // one faultCase for one iteration
        return new HashSet<>(List.of(faultCase));
//        return Tfail;
    }

    public Set<TestCase> getTpass() {
        return Tpass;
    }

    public int getFaultCaseSize() {
        return faultCase.size();
    }

    public int getThreshold() {
        return Threshold;
    }

    public Set<Comb> getCombinationSet(Set<TestCase> T){
        Set<Comb> res = new HashSet<>();

        for (TestCase testCase : T) {
            res.addAll(testCase.powerSet());
        }

        return res;
    }

//    private Set<Comb> getAllCombinations(TestCase testCase) {
//        Set<Comb> res = new HashSet<>();
//        List<Integer> testCaseIndex = new ArrayList<>();
//        for (int i = 0; i < testCase.size(); i++) {
//            testCaseIndex.add(i);
//        }
//
//        for (int i = 1; i <= testCase.size(); i++) {
//            Combination<Integer> combinationIndex = Combination.of(testCaseIndex, i);
//            for (List<Integer> combIndex : combinationIndex) {
//                res.add(new Comb(testCase, combIndex));
//            }
//        }
//
//        return res;
//    }

    /**
     * 求两集合的差集, 注意此函数不会改变输入参数 A B
     * @param A {[1],[2],[1,2]}
     * @param B {[1],{2]}
     * @return  {[1,2]}
     */
    public Set<Comb> minus(Set<Comb> A, Set<Comb> B) {
        Set<Comb> res = new HashSet<>();
        for (Comb a : A) {
            if(!B.contains(a)){
                res.add(a);
            }
        }
        return res;
    }

    /**
     * generate additional test suite for strength-th iteration
     * @param strength
     * @param Tfail
     * @return
     */
    public Set<TestCase> generateAT(int strength, Set<TestCase> Tfail) {
        Set<TestCase> res = new HashSet<>();
        for (TestCase failTestCase : Tfail) {
            res.addAll(mutate(strength, failTestCase));
        }
        return res;
    }

    private Set<TestCase> mutate(int strength, TestCase failTestCase) {
        Set<TestCase> res = new HashSet<>();
        // Find allPositions to replace values
        Set<List<Integer>> allPositions = getAllPossiblePositions(failTestCase.size(), strength);

        for (List<Integer> positions : allPositions) {
            TestCase newTestCase = failTestCase.clone();
            // mutate values in positions
            for (Integer position : positions) {
                Set<Integer> values = Values.get(position);
                for (int value : values) {
                    if(value != newTestCase.get(position)) {
                        newTestCase.set(position, value);
                        break;
                    }
                }
            }
            res.add(newTestCase);
        }

        return res;
    }

    private Set<List<Integer>> getAllPossiblePositions(int size, int strength) {
        Set<List<Integer>> res = new HashSet<>();

        List<Integer> caseIndex = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            caseIndex.add(i);
        }
        Combination<Integer> combinationIndex = Combination.of(caseIndex, strength);
        for (List<Integer> combIndex : combinationIndex) {
            res.add(new ArrayList<>(combIndex));
        }

        return res;
    }

    public Set<TestCase> getATpass(Set<TestCase> AT) {
        Set<TestCase> res = new HashSet<>();
        for (TestCase t : AT) {
            // if t pass the checker, add it to Tpass
            if(checker.executeTestCase(t)) {
                res.add(t);
            }
        }
        return res;
    }
}
