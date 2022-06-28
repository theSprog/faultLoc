package nju.gist.FaultResolver.ComFIL;

import nju.gist.Common.Comb;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Productor;
import org.raistlic.common.permutation.Combination;

import java.util.*;

public class ComFIL{
    /**
     * get powerSet of testCase
     * @param testCase
     * @return powerSet of testCase
     */
    public Set<Comb> powerSet(TestCase testCase) {
        Set<Comb> res = new HashSet<>();

        List<Integer> testCaseIndex = new ArrayList<>();
        for (int i = 0; i < testCase.size(); i++) {
            testCaseIndex.add(i);
        }

        for (int i = 1; i <= testCase.size(); i++) {
            Combination<Integer> posIndexs = Combination.of(testCaseIndex, i);
            for (List<Integer> pos : posIndexs) {
                res.add(new Comb(testCase, pos));

            }
        }

        return res;
    }

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
     * true if cand is SubInteraction of candFI, false otherwise
     * @param cand
     * @param candFI
     * @return
     * eg (1,2,0,1) is SubInteraction (1,2,0,1)
     * (1,0,0,1) is SubInteraction (1,2,0,1)
     * (1,3,0,1) is not SubInteraction (1,2,2,1)
     */
    public boolean isSubInteraction(List<Integer> cand, List<Integer> candFI) {
        assert cand.size() == candFI.size();
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
        assert cand.size() == candFI.size();
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
