package nju.gist.FaultResolver.FIC;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;
import java.util.List;

public class FIC {
    private final Checker checker;
    private final TestCase faultCase;
    private final Integer size;

    public FIC(Checker checker, TestCase faultCase) {
        this.checker = checker;
        this.faultCase = faultCase;
        this.size = faultCase.size();
    }

    public void removeFaultPattern(Schema node, Schema faultPattern){
        node.xor(faultPattern);
    }

    /**
     * @param node 输入的测试用例模式
     * @return null if node hasn't any fault or can't locate fault else faultPattern;
     * when we can't  locate fault we must return null, or the while(true) will not end
     * 注意, 这个方法结束后会不改变输入的 node, 因为操作是在内部备份上完成的
     */
    public Schema extractOneFaultPattern(Schema node) {
        // node 已经无故障
        boolean pass = checker.executeTestCase(Productor.genTestCase(node, faultCase));
        if (pass) {
            return null;
        }

        Schema faultPattern = node.clone();

        for (int i = 0; i < size; i++) {
            if (faultPattern.get(i)) {
                faultPattern.clear(i);
                TestCase testCase = Productor.genTestCase(faultPattern, faultCase);
                pass = checker.executeTestCase(testCase);

                if (pass) {   // 破坏了 fault, 说明 i 是组成故障之一
                    faultPattern.set(i);
                }
            }
        }

//        if(faultPattern.isEmpty()) return null;
//        return faultPattern;

        return faultPattern.isEmpty() ? null : faultPattern;
    }
}
