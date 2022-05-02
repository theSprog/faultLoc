package nju.gist.FaultResolver.SOFOT;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;

public class SOFOTResolver extends AbstractFaultResolver {
    private SOFOT sofot;

    @Override
    public List<List<Integer>> findMinFaults() {
        sofot = new SOFOT(checker, faultCase);

        Schema currentPattern = (Schema) faultCasePattern.clone();
        Schema faultPattern = sofot.extractOneFaultPattern(currentPattern);

        if (faultPattern != null) {
            minFaults.add(Productor.genMinFault(faultPattern, faultCase));
        }

        return minFaults;
    }
}