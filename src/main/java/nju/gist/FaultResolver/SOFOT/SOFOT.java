package nju.gist.FaultResolver.SOFOT;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.List;

public class SOFOT {
    private final Checker checker;
    private final TestCase faultCase;
    private final Integer size;

    public SOFOT(Checker checker, TestCase faultCase) {
        this.checker = checker;
        this.faultCase = faultCase;
        this.size = faultCase.size();
    }

    public Schema extractOneFaultPattern(Schema node) {
        // node 已经无故障
        boolean pass = checker.executeTestCase(Productor.genTestCase(node, faultCase));
        if (pass) {
            return null;
        }

        Schema faultPattern = (Schema) node.clone();
        Schema res = new Schema(size);

        for (int i = 0; i < size; i++) {
            if (faultPattern.get(i)) {
                faultPattern.clear(i);
                TestCase testCase = Productor.genTestCase(faultPattern, faultCase);
                pass = checker.executeTestCase(testCase);
                faultPattern.set(i);

                if (pass) {   // 破坏了 fault, 说明 i 是组成故障之一
                    res.set(i);
                }
            }
        }
        return res.cardinality() == 0 ? null : res;
    }
}
