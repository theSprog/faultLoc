package nju.gist.FaultResolver.LG;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.List;

public class LGResolver extends AbstractFaultResolver {
    private final LGKind lgKind;
    private LG lg;

    public LGResolver(LGKind lgKind) {
        this.lgKind = lgKind;
    }

    @Override
    public List<MinFault> findMinFaults() {
        lg = lgKind == LGKind.SafeValueLG ?
                new SafeValueLG(checker, knownMinFaults) :
                new AdvLG(checker, Tpass);

        if(lgKind == LGKind.SafeValueLG){
            lg.locate(faultCase);
            for (Schema knownMinFault : knownMinFaults) {
                minFaults.add(Productor.genMinFault(knownMinFault, faultCase));
            }
        }else {
            lg.locateELA(Tfail, minFaults);
        }

        return minFaults;
    }
}
