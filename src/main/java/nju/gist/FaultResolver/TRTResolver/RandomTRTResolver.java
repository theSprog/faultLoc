package nju.gist.FaultResolver.TRTResolver;

import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Productor;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

// 加入随机过程
public class RandomTRTResolver extends AbstractFaultTRTResolver {
    @Override
    public void setFaultCase(TestCase faultCase) {
        super.setFaultCase(faultCase);
        super.knownTable = new BitSet(1 << size);
        knownTable.set(0);
        for(int i = 0; i < size; i++){
            knownTable.set(1<<i);
        }
    }

    // 预处理可能刚好命中 minFault, 不过概率极低, 我们直接认为不可能
    @Override
    protected void preProcess() {
        int shotTimes = 1000;
        Random r = new Random();
        for (int i = 0; i < shotTimes; i++) {
            int radNode = r.nextInt(maxNode);
            if(isStamped(radNode)) continue;
            checkAndStamp(radNode);
        }
    }

    @Override
    public List<MinFault> findMinFaults() {
        if(size > 24){
            preProcess();
        }

        // 此处可以改进 node 的遍历方式，现在默认顺序遍历
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
}
