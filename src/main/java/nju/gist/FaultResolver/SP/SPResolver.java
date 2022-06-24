package nju.gist.FaultResolver.SP;

import nju.gist.FaultResolver.AbstractFaultResolver;

import java.util.List;
import java.util.Set;

public class SPResolver extends AbstractFaultResolver {
    private SP sp;
    private int degree;

    public SPResolver(int degree) {
        this.sp = new SP(degree);
    }

    @Override
    public List<List<Integer>> findMinFaults() {
        int piHatSize;  // Π‘
        int piSize = 0;

        while (true){
            sp.setTpassAndTfail(Tpass, Tfail);
            Set<List<Integer>> suspiciousCombinations = sp.getAllSuspiciousCombinations();
            piHatSize = suspiciousCombinations.size();
            if(piHatSize != 0 && (piHatSize < piSize || piSize == 0)) {
                List<List<Integer>> piRanked = sp.rank(suspiciousCombinations);
                piSize = piRanked.size();
                List<List<Integer>> newTestCases = sp.genNewTestCases(piRanked);
                for (List<Integer> newTestCase : newTestCases) {
                    boolean pass = checker.executeTestCase(newTestCase);
                    if(pass){
                        Tpass.add(newTestCase);
                    }
                }
                continue;
            }

            // The program will still run to this point if piHatSize == 0 initially
            // that means the minimal degree of MFSs is greater than we provided
            if(piHatSize == piSize){
                minFaults.addAll(suspiciousCombinations);
                break;
            }
        }
        return minFaults;
    }
}
