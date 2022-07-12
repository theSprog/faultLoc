package nju.gist.Tester;

import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FileResolver.FaultFileResolver;

import java.util.*;

public class Checker {
    private final List<HashMap<Integer, Integer>> faultMaps;

    // executed test case set
    private final Set<TestCase> execSet;
    // health test case(htc)
    private final Set<TestCase> htc;

    public Checker(String faultFilePath) {
        FaultFileResolver faultFileResolver = new FaultFileResolver();
        faultMaps = faultFileResolver.getFaultFeatures(faultFilePath);
        htc = new HashSet<>();
        execSet = new HashSet<>();
    }

    public Set<TestCase> getHtc() {
        return htc;
    }
    public Set<TestCase> getExecSet() {
        return execSet;
    }

    public void clearHtc() {
        htc.clear();
    }
    public void clearExecSet() {
        execSet.clear();
    }

    public int faultNum() {
        return faultMaps.size();
    }
    /**
     * @return true if this testCase pass the execution, false otherwise
     */
    public boolean executeTestCase(TestCase testCase) {
        execSet.add(testCase);
        for (HashMap<Integer, Integer> faultMap : faultMaps) {
            if (triggerFail(testCase, faultMap)) {
                return false;
            }
        }
        // if pass the checker, record it
        htc.add(testCase);
        return true;
    }

    /**
     *
     * @param testCase
     * @param faultMap
     * @return true if this testCase triggers fail, false otherwise
     */
    private boolean triggerFail(List<Integer> testCase, HashMap<Integer, Integer> faultMap) {
        for (Map.Entry<Integer, Integer> Entry : faultMap.entrySet()) {
            if(!testCase.get(Entry.getKey()).equals(Entry.getValue())){
                return false;
            }
        }
        return true;
    }

    public Set<MinFault> faults2minFaults() {
        Set<MinFault> res = new HashSet<>();
        for (HashMap<Integer, Integer> faultMap : faultMaps) {
            MinFault minFault = new MinFault(Productor.faultCaseSize);
            faultMap.forEach(minFault::set);
            res.add(minFault);
        }
        return res;
    }
}
