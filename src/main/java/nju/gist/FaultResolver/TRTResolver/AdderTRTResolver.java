package nju.gist.FaultResolver.TRTResolver;

import nju.gist.Common.MinFault;
import nju.gist.Tester.Productor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * if 10001000 is fail, then we assert node in [10001000, 10001111] must be fail,
 * so we skip this node and directly check 10010000
 */
public class AdderTRTResolver extends AbstractFaultTRTResolver {
    private Set<Integer> minFaultNodes;

    @Override
    public List<MinFault> findMinFaults() {
        if(size > 24) {
            logger.warn(String.format("AdderTRTResolver: N=%d, it's too large to compute, please use feasible resolver", faultCase.size()));
            return minFaults;
        }
        this.minFaultNodes = new HashSet<>();

        for (int node = 1; node <= maxNode; node++) {
            boolean health = checkNode(node);
            if(!health) {
                if(isMinFault(node)){
                    minFaultNodes.add(node);

                    MinFault minFault = Productor.genMinFault(node, faultCase);
                    minFaults.add(minFault);
                }

                // i.e. node == 10001000, next node is 10001111, and node+1 would be 10010000
                node = node | (node - 1);
                if(node < 0) break;
            }
        }

        return minFaults;
    }

    private boolean isMinFault(int node) {
        for (Integer minFaultNode : minFaultNodes) {
            if(isAncestor(node, minFaultNode)){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void preProcess() {

    }
}
