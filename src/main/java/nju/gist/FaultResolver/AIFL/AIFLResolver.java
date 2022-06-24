package nju.gist.FaultResolver.AIFL;

import nju.gist.FaultResolver.AbstractFaultResolver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AIFLResolver extends AbstractFaultResolver {
    private Set<List<Integer>> TpassSet;
    private Set<List<Integer>> TfailSet;

    @Override
    public List<List<Integer>> findMinFaults() {
        AIFL aifl = new AIFL(checker, faultCase, Values);
        aifl.setTfailAndTpass(Tfail, Tpass);
        aifl.setThreshold(faultCase.size());

        TpassSet = aifl.getTpass();
        TfailSet = aifl.getTfail();

        int n = aifl.getFaultCaseSize();
        Set<List<Integer>> suspSetOld = new HashSet<>();

        // suspSet^(0) = SchemaSet(TfailSet) - SchemaSet(TpassSet)
        Set<List<Integer>> suspSetNew = aifl.minus(aifl.getSchemaSet(TfailSet), aifl.getSchemaSet(TpassSet));

        for (int i = 0; i < n; i++) {
            if(suspSetNew.size() <= aifl.getThreshold() || suspSetOld.equals(suspSetNew)) break;

            Set<List<Integer>> ATpass = aifl.getATpass(aifl.generateAT(i+1, TfailSet));
            suspSetOld = suspSetNew;
            suspSetNew = aifl.minus(suspSetOld, aifl.getSchemaSet(ATpass));
        }

        minFaults.addAll(suspSetNew);
        return null;
    }
}
