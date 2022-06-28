package nju.gist.FaultResolver.RI;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;

public class RIResolver extends AbstractFaultResolver {
    private RI ri;

    @Override
    public List<MinFault> findMinFaults() {
        ri = new RI(checker, faultCase, faultCasePattern);

        // faultCasePattern should not be changed
        Schema currentPattern = faultCasePattern.clone();
        while (true) {
            Schema faultPattern = ri.extractOneFaultPattern(currentPattern);
            if (faultPattern == null) {
                break;
            }
            minFaults.add(Productor.genMinFault(faultPattern, faultCase));
            // remove faultPattern from currentPattern
            ri.removeFaultPattern(currentPattern, faultPattern);
        }
        return minFaults;
    }
}
