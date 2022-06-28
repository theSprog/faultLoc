package nju.gist.FaultResolver.FIC;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;

public class FICResolver extends AbstractFaultResolver {

    @Override
    public List<MinFault> findMinFaults() {
        FIC fic = new FIC(checker, faultCase);
        Schema currentPattern = faultCasePattern.clone();

        while (true){
            Schema faultPattern = fic.extractOneFaultPattern(currentPattern);
            if(faultPattern == null){
                break;
            }

            minFaults.add(Productor.genMinFault(faultPattern, faultCase));
            fic.removeFaultPattern(currentPattern, faultPattern);
        }
        return minFaults;
    }
}
