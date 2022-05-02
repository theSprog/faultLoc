package nju.gist.FaultResolver.InverseCTD;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InverseCTD {
    private final List<List<Integer>> Tfail;
    private final List<List<Integer>> Tpass;
    // 每个参数能取的值的种类数
    // i.e. Values[0] = {2, 3} 代表第 0 个参数能取 3 和 2.
    private List<Set<Integer>> Values;

    public InverseCTD(List<Integer> faultCase, List<List<Integer>> Tfail, List<List<Integer>> Tpass) {
        this.Tfail = Tfail;
        this.Tpass = Tpass;
        Values = new ArrayList<>();
        for (int i = 0; i < faultCase.size(); i++) {
            Values.add(new HashSet<>());
        }
    }

    public void createModel() {
        for (List<Integer> testCase : Tfail) {
            for (int i = 0; i < testCase.size(); i++) {
                Values.get(i).add(testCase.get(i));
            }
        }

        for (List<Integer> testCase : Tpass) {
            for (int i = 0; i < testCase.size(); i++) {
                Values.get(i).add(testCase.get(i));
            }
        }
    }

    public Set<List<Integer>> getModifiedTestCases(List<Integer> faultCase, int i) {
        Set<Integer> factors = Values.get(i);
        Integer nowFactor = faultCase.get(i);
        HashSet<List<Integer>> res = new HashSet<>();

        for (Integer factor : factors) {
            if(!factor.equals(nowFactor) && !appearIn(Tfail, i, factor)){
                List<Integer> temp = new ArrayList<>(faultCase);
                temp.set(i, factor);
                res.add(temp);
            }
        }

        return res;
    }

    private boolean appearIn(List<List<Integer>> Tfail, int i, Integer factor) {
        for (List<Integer> testCase : Tfail) {
            if(testCase.get(i).equals(factor)){
                return true;
            }
        }
        return false;
    }
}
