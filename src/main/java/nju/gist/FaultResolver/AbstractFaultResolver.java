package nju.gist.FaultResolver;

import nju.gist.Common.Schema;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.*;

public abstract class AbstractFaultResolver implements FaultResolver{
    protected List<List<Integer>> Tfail;
    protected List<List<Integer>> Tpass;

    protected List<Integer> faultCase;
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
    protected List<List<Integer>> minFaults;

    // 每个参数能取的值的集合
    // i.e. Values[0] = {2, 3} 代表第 0 个参数能取 3 和 2.
    protected List<Set<Integer>> Values;

    @Override
    public void setTfailAndTpass(List<List<Integer>> Tfail, List<List<Integer>> Tpass) {
        this.Tfail = Tfail;
        this.Tpass = Tpass;
    }

    @Override
    public void setFaultCase(List<Integer> faultCase) {
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

        Productor.setFaultCaseSize(size);
    }

    @Override
    public void initFromCA(List<List<Integer>> faultCaseList, List<List<Integer>> healthCaseList) {
        if(!faultCaseList.isEmpty()) setFaultCase(faultCaseList.get(0));
        else {setFaultCase(healthCaseList.get(0));}

        Values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Values.add(new HashSet<>());
        }

        for (List<Integer> testCase : faultCaseList) {
            for (int i = 0; i < testCase.size(); i++) {
                Values.get(i).add(testCase.get(i));
            }
        }

        for (List<Integer> testCase : healthCaseList) {
            for (int i = 0; i < testCase.size(); i++) {
                Values.get(i).add(testCase.get(i));
            }
        }


    }

    @Override
    public void setChecker(Checker checker) {
        this.checker = checker;
    }
}
