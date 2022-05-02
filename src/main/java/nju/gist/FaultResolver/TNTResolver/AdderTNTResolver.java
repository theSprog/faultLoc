package nju.gist.FaultResolver.TNTResolver;

import nju.gist.Tester.Productor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdderTNTResolver extends AbstractFaultTNTResolver {
    private Set<Integer> minFaultNodes;

    @Override
    public List<List<Integer>> findMinFaults() {
        this.minFaultNodes = new HashSet<>();

        for (int node = 1; node <= maxNode; node++) {
            boolean health = checkNode(node);
            if(!health) {
                if(isMinFault(node)){
                    minFaultNodes.add(node);

                    List<Integer> minFault = Productor.genMinFault(node, faultCase);
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
