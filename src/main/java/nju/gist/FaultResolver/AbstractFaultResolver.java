package nju.gist.FaultResolver;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.*;

public abstract class AbstractFaultResolver implements FaultResolver{
    protected List<TestCase> Tfail;
    protected List<TestCase> Tpass;

    protected TestCase faultCase;
    protected Checker checker;

    // all 1
    protected Schema faultCasePattern;

    // all 0
    protected Schema healthCasePattern;

    // 参数规模
    protected int size;

    // the pattern of MFS, such as 1001 or 1100 where 1 denotes existence of factor
    protected Set<Schema> knownMinFaults;

    // the real MFS of faultCase, such as {2,0,0,3} or {3,3,0,0} where 0 denotes -
    protected List<MinFault> minFaults;

    @Override
    public void setTfailAndTpass(List<TestCase> Tfail, List<TestCase> Tpass) {
        this.Tfail = Tfail;
        this.Tpass = Tpass;
        Productor.SetParaValues(initFromCA(Tfail, Tpass));
    }

    @Override
    public void setFaultCase(TestCase faultCase) {
        this.faultCase = faultCase;
        this.size = faultCase.size();
        this.checker.clearHtc();

        // 如果是第一次调用，则初始化，否则不变
        if(this.faultCasePattern == null || this.faultCasePattern.length() != this.size){
            this.faultCasePattern = new Schema(size);
            this.faultCasePattern.set(0, size);

            this.healthCasePattern = new Schema(size);
            this.healthCasePattern.clear(0, size);
        }

        // 复用则只需要 clear 上一次的结果即可
        if (this.minFaults != null){
            this.minFaults.clear();
            this.knownMinFaults.clear();
        }else { // 否则就是第一次调用，初始化
            this.minFaults = new LinkedList<>();
            this.knownMinFaults = new HashSet<>();
        }
    }

    @Override
    public List<Set<Integer>> initFromCA(List<TestCase> faultCaseList, List<TestCase> healthCaseList) {
        if(!faultCaseList.isEmpty()) setFaultCase(faultCaseList.get(0));
        else {setFaultCase(healthCaseList.get(0));}

        List<Set<Integer>> Values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Values.add(new HashSet<>());
        }

        for (TestCase testCase : faultCaseList) {
            for (int i = 0; i < testCase.size(); i++) {
                Values.get(i).add(testCase.get(i));
            }
        }

        for (TestCase testCase : healthCaseList) {
            for (int i = 0; i < testCase.size(); i++) {
                Values.get(i).add(testCase.get(i));
            }
        }

        return Values;
    }

    @Override
    public void setChecker(Checker checker) {
        this.checker = checker;
    }
}
