package nju.gist.FaultResolver.InverseCTD;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;
import java.util.Set;

public class InverseCTDResolver extends AbstractFaultResolver {
    private InverseCTD inverseCTD;

    @Override
    public List<MinFault> findMinFaults() {
        inverseCTD = new InverseCTD(Productor.ParaValues, Tfail, Tpass);
        Schema resPattern = new Schema(faultCase.size());

        for (int i = 0; i < faultCase.size(); i++) {
            Set<TestCase> modifiedTestCases = inverseCTD.getModifiedTestCases(faultCase, i);
            for (TestCase modifiedTestCase : modifiedTestCases) {
                boolean pass = checker.executeTestCase(modifiedTestCase);
                if(pass){
                    // we modified i-th parameter and the fault disappear,
                    // so we capture it as part of fault
                    resPattern.set(i);
                    break;
                }
            }
        }

        minFaults.add(Productor.genMinFault(resPattern, faultCase));
        return minFaults;
    }
}
