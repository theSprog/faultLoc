package nju.gist.FaultResolver.TRTResolver;
import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Productor;

import java.util.BitSet;
import java.util.List;

public class BasicTRTResolver extends AbstractFaultTRTResolver {
    @Override
    public void setFaultCase(TestCase faultCase) {
        super.setFaultCase(faultCase);

        super.knownTable = new BitSet(1 << size);
        knownTable.set(0);
        for(int i = 0; i < size; i++){
            knownTable.set(1<<i);
        }
    }

    @Override
    public List<MinFault> findMinFaults() {
        if(size > 24) {
            logger.warn(String.format("BasicTRTResolver: N=%d, it's too large to compute, please use feasible resolver", faultCase.size()));
            return minFaults;
        }
        // Basic 默认顺序遍历
        for (int node = 1; node <= maxNode; node++) {
            if(isStamped(node)) continue;

            boolean health = checkAndStamp(node);
            if(!health) {
                MinFault minFault = Productor.genMinFault(node, faultCase);
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
