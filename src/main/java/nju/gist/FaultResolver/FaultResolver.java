package nju.gist.FaultResolver;

import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;

import java.util.List;
import java.util.Set;

public interface FaultResolver {
    void setTfailAndTpass(List<TestCase> faultCaseList, List<TestCase> healthCaseList);
    void setFaultCase(TestCase faultCase);
    void setChecker(Checker checker);
    List<Set<Integer>> initFromCA(List<TestCase> faultCaseList, List<TestCase> healthCaseList);
    List<MinFault> findMinFaults();
}
