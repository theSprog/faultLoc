package nju.gist.FaultResolver.TRTResolver;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.FIC.FIC;
import nju.gist.Tester.Productor;

import java.util.*;

import static nju.gist.FaultResolver.PendingSchemas.SchemasUtil.*;

public class SubTRTResolver extends AbstractFaultTRTResolver {
    private Set<Schema> HuntingGround;
    private FIC fic;

    @Override
    protected void preProcess() {

    }

    @Override
    public void setFaultCase(TestCase faultCase) {
        super.setFaultCase(faultCase);
        HuntingGround = new HashSet<>();
        fic = new FIC(checker, faultCase);
    }

    @Override
    public List<MinFault> findMinFaults() {
        for (int node = maxNode; node > 0; node--) {
            boolean health = checkNode(node);
            if (!health) {
                asHuntee(node);
            } else {
                // 101001111 -> 101000000 ->(node--) 100111111
                node = (node + 1) & node;
            }
        }

        for (Schema winner : HuntingGround) {
            MinFault minFault = Productor.genMinFault(winner, faultCase);
            minFaults.add(minFault);
        }

        return minFaults;
    }

    private void asHuntee(int node) {
        // HuntingGround 全是猎人, 看是否能 “吃掉” 进来的 node
        boolean match = HuntingGround.stream().anyMatch(hunter -> isSubSchema(hunter, Productor.toSchema(node)));
        if(!match){
            Schema faultPattern = fic.extractOneFaultPattern(Productor.toSchema(node));
            HuntingGround.add(faultPattern);
        }
    }
}
