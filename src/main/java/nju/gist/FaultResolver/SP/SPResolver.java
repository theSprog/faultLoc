package nju.gist.FaultResolver.SP;

import nju.gist.Common.Comb;
import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;
import java.util.Set;

public class SPResolver extends AbstractFaultResolver {
    private final SP sp;
    private final int degree;

    public SPResolver(int degree) {
        this.sp = new SP(degree);
        this.degree = degree;
    }

    @Override
    public List<MinFault> findMinFaults() {
        if(size > 100 || size < degree) return minFaults;
        // piHat is new suspicious combinations,
        // it should be gradually decreasing for iteratively generated passing test cases
        int piHatSize;
        // pi is previous suspicious combinations,
        // if pi == piHat means that the number of suspicious combinations can no longer be lowered
        int piSize = 0;

        while (true){
            sp.setTpassAndTfail(Tpass, Tfail);
            Set<Comb> suspiciousCombinations = sp.getAllSuspiciousCombinations();
            piHatSize = suspiciousCombinations.size();
            // if new suspicious combinations presence and
            // it has been reduced by the last iteration (piHatSize < piSize)
            // or this is first iteration
            if(piHatSize != 0 && (piHatSize < piSize || piSize == 0)) {
                piSize = suspiciousCombinations.size();
                List<Comb> piRanked = sp.rank(suspiciousCombinations);
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
            if(piHatSize == 0 || piHatSize == piSize){
                for (Comb suspiciousCombination : suspiciousCombinations) {
                    for (int i = 1; i <= degree; i++) {
                        Set<Comb> Ri = sp.smallerCombs(suspiciousCombination, i);
                        for (Comb smallerComb : Ri) {
                            minFaults.add(new MinFault(smallerComb));
                        }
                    }
                }
                break;
            }
        }
        return minFaults;
    }
}
