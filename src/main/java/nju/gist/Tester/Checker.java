package nju.gist.Tester;

import nju.gist.FileResolver.FaultFileResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checker {
    private final List<HashMap<Integer, Integer>> faultMaps;

    public Checker(String faultFilePath) {
        FaultFileResolver faultResolver = new FaultFileResolver();
        faultMaps = faultResolver.getFaultFeatures(faultFilePath);
    }

    /**
     * @param testCase
     * @return true if this testCase pass the execution, false otherwise
     */
    public boolean executeTestCase(List<Integer> testCase) {
        for (HashMap<Integer, Integer> faultMap : faultMaps) {
            if (triggerFail(testCase, faultMap)) {
                return false;
            }
        }
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
