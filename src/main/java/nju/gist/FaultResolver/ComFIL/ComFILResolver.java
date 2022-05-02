package nju.gist.FaultResolver.ComFIL;

import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ComFILResolver extends AbstractFaultResolver {
    private ComFIL comFIL;

    @Override
    public List<List<Integer>> findMinFaults() {
        Set<List<Integer>> resolve = resolve();
        minFaults.addAll(resolve);
        return minFaults;
    }

    private Set<List<Integer>> resolve(){
        comFIL = new ComFIL();

        Set<List<Integer>> faultCasePowerSet = comFIL.powerSet(faultCase);
        Set<List<Integer>> tPassPowerSet = new HashSet<>();
        for (List<Integer> tpass : Tpass) {
            tPassPowerSet.addAll(comFIL.powerSet(tpass));
        }

        Set<List<Integer>> candFIS = comFIL.minus(faultCasePowerSet, tPassPowerSet);
        Set<List<Integer>> copy = new HashSet<>(candFIS);
        for (List<Integer> candFI : candFIS) {
            boolean pass = checker.executeTestCase(candFI);
            if(pass){
                copy.removeIf(cand -> comFIL.isSubInteraction(cand, candFI));
            }else {
                copy.removeIf(cand -> comFIL.isSuperInteraction(cand, candFI));
            }
        }

        return copy;
    }
}
