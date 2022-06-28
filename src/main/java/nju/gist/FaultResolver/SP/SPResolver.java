package nju.gist.FaultResolver.SP;

import nju.gist.Common.Comb;
import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;
import java.util.Set;

public class SPResolver extends AbstractFaultResolver {
    private SP sp;
    private int degree;

    public SPResolver(int degree) {
        this.sp = new SP(degree);
    }

    @Override
    public List<MinFault> findMinFaults() {
        int piHatSize;
        int piSize = 0;

        while (true){
            sp.setTpassAndTfail(Tpass, Tfail);
            Set<Comb> suspiciousCombinations = sp.getAllSuspiciousCombinations();
            piHatSize = suspiciousCombinations.size();
            if(piHatSize != 0 && (piHatSize < piSize || piSize == 0)) {
                List<Comb> piRanked = sp.rank(suspiciousCombinations);
                piSize = piRanked.size();
                List<TestCase> newTestCases = sp.genNewTestCases(piRanked);
                for (TestCase newTestCase : newTestCases) {
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
                for (Comb suspiciousCombination : suspiciousCombinations) {
                    minFaults.add(new MinFault(suspiciousCombination));
                }
                break;
            }
        }
        return minFaults;
    }
}
