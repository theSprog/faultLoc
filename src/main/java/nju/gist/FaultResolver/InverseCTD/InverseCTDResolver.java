package nju.gist.FaultResolver.InverseCTD;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;
import java.util.Set;

public class InverseCTDResolver extends AbstractFaultResolver {
    private InverseCTD inverseCTD;

    @Override
    public List<List<Integer>> findMinFaults() {
        inverseCTD = new InverseCTD(faultCase, Tfail, Tpass);
        inverseCTD.createModel();
        Schema resPattern = new Schema(faultCase.size());

        for (int i = 0; i < faultCase.size(); i++) {
            Set<List<Integer>> modifiedTestCases = inverseCTD.getModifiedTestCases(faultCase, i);
            for (List<Integer> modifiedTestCase : modifiedTestCases) {
                boolean pass = checker.executeTestCase(modifiedTestCase);
                if(pass){
                    resPattern.set(i);
                    break;
                }
            }
        }

        minFaults.add(Productor.genMinFault(resPattern, faultCase));
        return minFaults;
    }
}
