package nju.gist.FaultResolver.ComFIL;

import nju.gist.Common.Comb;
import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComFILResolver extends AbstractFaultResolver {
    private ComFIL comFIL;

    @Override
    public List<MinFault> findMinFaults() {
        if(faultCase.size() > 15) {
            logger.warn(String.format("ComFILResolver: N=%d, it's too large to compute, please use feasible resolver", faultCase.size()));
            return minFaults;
        }
        Set<Comb> resolve = resolve();
        for (Comb combination : resolve) {
            minFaults.add(new MinFault(combination));
        }
        return minFaults;
    }

    private Set<Comb> resolve(){
        comFIL = new ComFIL();
        Set<Comb> faultCasePowerSet= new HashSet<>();
        for (TestCase faultCase : Tfail) {
            faultCasePowerSet = comFIL.powerSet(faultCase);
        }

        Set<Comb> tPassPowerSet = new HashSet<>();
        for (TestCase tpass : Tpass) {
            tPassPowerSet.addAll(comFIL.powerSet(tpass));
        }

        Set<Comb> candFIS = comFIL.minus(faultCasePowerSet, tPassPowerSet);
        Set<Comb> copy = new HashSet<>(candFIS);
        for (Comb candFI : candFIS) {
            boolean pass = checker.executeTestCase(Productor.genTestCase(candFI));
            if(pass){
                copy.removeIf(cand -> comFIL.isSubInteraction(cand, candFI));
            }else {
                copy.removeIf(cand -> comFIL.isSuperInteraction(cand, candFI));
            }
        }

        return copy;
    }
}
