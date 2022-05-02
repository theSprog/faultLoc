package nju.gist.FaultResolver.TNTResolver;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.BitSet;
import java.util.List;

public class BasicTNTResolver extends AbstractFaultTNTResolver {
    @Override
    public void setFaultCase(List<Integer> faultCase) {
        super.setFaultCase(faultCase);

        super.knownTable = new BitSet(1 << size);
        knownTable.set(0);
        for(int i = 0; i < size; i++){
            knownTable.set(1<<i);
        }
    }

    @Override
    public List<List<Integer>> findMinFaults() {
        // Basic 默认顺序遍历
        for (int node = 1; node <= maxNode; node++) {
            if(isStamped(node)) continue;

            boolean health = checkAndStamp(node);
            if(!health) {
                List<Integer> minFault = Productor.genMinFault(node, faultCase);
                minFaults.add(minFault);
            }
        }

        return minFaults;
    }

    @Override
    public void preProcess() {
        // Basic 没有预处理
    }
}
