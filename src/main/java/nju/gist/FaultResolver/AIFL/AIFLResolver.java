package nju.gist.FaultResolver.AIFL;

import nju.gist.Common.Comb;
import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AIFLResolver extends AbstractFaultResolver {
    private Set<TestCase> TpassSet;
    private Set<TestCase> TfailSet;

    @Override
    public List<MinFault> findMinFaults() {
        if(faultCase.size() > 15) {
            logger.warn(String.format("AIFLResolver: N=%d, it's too large to compute, please use feasible resolver", faultCase.size()));
            return minFaults;
        }
        AIFL aifl = new AIFL(checker, faultCase, Productor.ParaValues);
        aifl.setTfailAndTpass(Tfail, Tpass);
        aifl.setThreshold(faultCase.size());

        TpassSet = aifl.getTpass();
        TfailSet = aifl.getTfail();

        int n = aifl.getFaultCaseSize();
        Set<Comb> suspSetOld = new HashSet<>();

        // suspSet^(0) = SchemaSet(TfailSet) - SchemaSet(TpassSet)
        Set<Comb> suspSetNew = aifl.minus(aifl.getCombinationSet(TfailSet), aifl.getCombinationSet(TpassSet));

        for (int i = 0; i < n; i++) {
            if(suspSetNew.size() <= aifl.getThreshold() || suspSetOld.equals(suspSetNew)) break;

            Set<TestCase> ATpass = aifl.getATpass(aifl.generateAT(i+1, TfailSet));
            suspSetOld = suspSetNew;
            suspSetNew = aifl.minus(suspSetOld, aifl.getCombinationSet(ATpass));
        }

        for (Comb combination : suspSetNew) {
            minFaults.add(new MinFault(combination));
        }
        return minFaults;
    }
}
