package nju.gist.FaultResolver;

import nju.gist.Tester.Checker;

import java.util.BitSet;
import java.util.List;

public interface FaultResolver {
    void setTfailAndTpass(List<List<Integer>> faultCaseList, List<List<Integer>> healthCaseList);
    void setFaultCase(List<Integer> faultCase);
    void setChecker(Checker checker);
    void initFromCA(List<List<Integer>> faultCaseList, List<List<Integer>> healthCaseList);
    List<List<Integer>> findMinFaults();
}
