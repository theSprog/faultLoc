package nju.gist.FaultResolver.FIC;

import nju.gist.Common.Schema;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;
import java.util.List;

public class FIC {
    private final Checker checker;
    private final List<Integer> faultCase;
    private final Integer size;

    public FIC(Checker checker, List<Integer> faultCase) {
        this.checker = checker;
        this.faultCase = faultCase;
        this.size = faultCase.size();
    }

    public Checker getChecker() {
        return checker;
    }

    public void removeFaultPattern(Schema node, Schema faultPattern){
        node.xor(faultPattern);
    }

    /**
     * @param node 输入的测试用例模式
     * @return null if node hasn't fault else faultPattern;
     * 注意, 这个方法结束后会不改变输入的 node, 因为操作是在内部备份上完成的
     */
    public Schema extractOneFaultPattern(Schema node) {
        // node 已经无故障
        boolean pass = checker.executeTestCase(Productor.genTestCase(node, faultCase));
        if (pass) {
            return null;
        }

        Schema faultPattern = (Schema) node.clone();

        for (int i = 0; i < size; i++) {
            if (faultPattern.get(i)) {
                faultPattern.clear(i);
                List<Integer> testCase = Productor.genTestCase(faultPattern, faultCase);
                pass = checker.executeTestCase(testCase);

                if (pass) {   // 破坏了 fault, 说明 i 是组成故障之一
                    faultPattern.set(i);
                }
            }
        }
        return faultPattern;
    }
}
