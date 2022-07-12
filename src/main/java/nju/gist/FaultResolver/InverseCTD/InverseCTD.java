package nju.gist.FaultResolver.InverseCTD;

import nju.gist.Common.TestCase;
import nju.gist.Tester.Productor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InverseCTD {
    private final List<TestCase> Tfail;
    private final List<TestCase> Tpass;
    // 每个参数能取的值的种类数
    // i.e. Values[0] = {2, 3} 代表第 0 个参数能取 3 和 2.
    private List<Set<Integer>> Values;

    public InverseCTD(List<Set<Integer>> ParaValues, List<TestCase> Tfail, List<TestCase> Tpass) {
        this.Tfail = Tfail;
        this.Tpass = Tpass;
        Values = ParaValues;
    }

    public void createModel() {

    }

    public Set<TestCase> getModifiedTestCases(TestCase faultCase, int i) {
        // factors represents all possible values in i-th parameter
        Set<Integer> factors = Values.get(i);
        // nowFactor represents current value in i-th parameter in faultCase
        Integer nowFactor = faultCase.get(i);
        HashSet<TestCase> res = new HashSet<>();

        if(Productor.TWO_VAL_EXP){
            TestCase temp = new TestCase(faultCase);
            temp.set(i, faultCase.get(i) % 2 + 1);
            res.add(temp);
            return res;
        }

        for (Integer factor : factors) {
            // we do not consider factor which appear in Tfail
            if(!factor.equals(nowFactor) && !appearIn(Tfail, i, factor)){
                TestCase temp = new TestCase(faultCase);
                temp.set(i, factor);
                res.add(temp);
            }
        }

        return res;
    }

    /**
     *
     * @param Tfail
     * @param i
     * @param factor
     * @return true if factor appear in i-th parameter of any testcase in Tfail, false otherwise
     */
    private boolean appearIn(List<TestCase> Tfail, int i, Integer factor) {
        for (List<Integer> testCase : Tfail) {
            if(testCase.get(i).equals(factor)){
                return true;
            }
        }
        return false;
    }
}
