package nju.gist.FaultResolver.ComFIL;

import nju.gist.Common.Schema;
import nju.gist.Tester.Productor;

import java.util.*;

public class ComFIL{
    /**
     * get powerSet of testCase
     * @param testCase
     * @return powerSet of testCase
     */
    public Set<List<Integer>> powerSet(List<Integer> testCase) {
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

    /**
     * true if cand is SubInteraction of candFI, false otherwise
     * @param cand
     * @param candFI
     * @return
     * eg (1,2,0,1) is SubInteraction (1,2,0,1)
     * (1,0,0,1) is SubInteraction (1,2,0,1)
     * (1,3,0,1) is not SubInteraction (1,2,2,1)
     */
    public boolean isSubInteraction(List<Integer> cand, List<Integer> candFI) {
        if(cand.size() != candFI.size()) return false;

        for (int i = 0; i < cand.size(); i++) {
            if(!cand.get(i).equals(candFI.get(i)) ) {
                if(cand.get(i) != 0 || candFI.get(i) == 0){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * true if cand is SuperInteraction of candFI, false otherwise
     * @param cand
     * @param candFI
     * @return
     * eg (1,2,0,1) is "not" SuperInteraction (1,2,0,1)
     * (1,2,0,1) is SuperInteraction (1,0,0,1)
     * (1,3,0,1) is not SuperInteraction (1,2,2,1)
     */
    public boolean isSuperInteraction(List<Integer> cand, List<Integer> candFI) {
        if(cand.size() != candFI.size()) return false;

        boolean equal = true;
        for (int i = 0; i < cand.size(); i++) {
            if(!cand.get(i).equals(candFI.get(i)) ){
                if(cand.get(i) != 0 && candFI.get(i) == 0){
                    equal = false;
                }else {
                    return false;
                }
            }
        }

        return !equal;
    }
}
