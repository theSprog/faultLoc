package nju.gist.Tester;

import nju.gist.Common.Schema;
import nju.gist.FileResolver.FaultFileResolver;

import java.util.*;

public class Checker {
    private final List<HashMap<Integer, Integer>> faultMaps;
    // health test case
    private final Set<List<Integer>> htc;

    public Checker(String faultFilePath) {
        FaultFileResolver faultResolver = new FaultFileResolver();
        faultMaps = faultResolver.getFaultFeatures(faultFilePath);
        htc = new HashSet<>();
    }

    public Set<List<Integer>> getHtc() {
        return htc;
    }

    public void clearHtc() {
        htc.clear();
    }

    /**
     * @return true if this testCase pass the execution, false otherwise
     */
    public boolean executeTestCase(List<Integer> testCase) {
        for (HashMap<Integer, Integer> faultMap : faultMaps) {
            if (triggerFail(testCase, faultMap)) {
                return false;
            }
        }

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
}
