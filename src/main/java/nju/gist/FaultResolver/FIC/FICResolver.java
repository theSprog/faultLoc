package nju.gist.FaultResolver.FIC;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;

public class FICResolver extends AbstractFaultResolver {

    @Override
    public List<List<Integer>> findMinFaults() {
//        FIC fic = new FIC(checker, faultCase);
        FICBS fic_bs = new FICBS(checker, faultCase);

        Schema currentPattern = (Schema)faultCasePattern.clone();
        while (true){
            Schema faultPattern = fic_bs.extractOneFaultPattern(currentPattern);
            if(faultPattern == null){
                break;
            }

            minFaults.add(Productor.genMinFault(faultPattern, faultCase));
            fic_bs.removeFaultPattern(currentPattern, faultPattern);
        }
        return minFaults;
    }
}
