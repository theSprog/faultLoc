package nju.gist.FaultResolver.FIC;

import nju.gist.Common.Schema;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;
import java.util.List;

public class FICBS {
    private final Checker checker;
    private final List<Integer> faultCase;
    private final Integer size;

    private Schema Clow;
    private Schema Chigh;
    private Schema Ccand;

    public FICBS(Checker checker, List<Integer> faultCase) {
        this.checker = checker;
        this.faultCase = faultCase;
        this.size = faultCase.size();

        Clow = new Schema(size);
        Chigh = new Schema(size);
    }

    public void removeFaultPattern(Schema node, Schema faultPattern){
        deduct(node, faultPattern);
    }

    /**
     * @param node 输入的测试用例模式
     * @return null if node hasn't fault else faultPattern;
     * 注意, 这个方法结束后会不改变输入的 node, 因为操作是在内部备份上完成的
     */
    public Schema extractOneFaultPattern(Schema node) {
        boolean pass = checker.executeTestCase(Productor.genTestCase(node, faultCase));
        if (pass) {
            return null;
        }

        Schema tempNode = (Schema) node.clone();
        // faultPattern 已经发现的故障因子
        Schema faultPattern = new Schema(size);
        // U 已经排除的安全因子
        Schema U = new Schema(size);

        while (true){
            Schema factor = extractOneFactor(tempNode, U, faultPattern);

            faultPattern.or(factor);

            if(!checker.executeTestCase(Productor.genTestCase(faultPattern, faultCase))) {
                return faultPattern;
            }
        }
    }

    private Schema extractOneFactor(Schema node, Schema U, Schema factor) {
        Ccand = (Schema) node.clone();
        deduct(Ccand, U);
        deduct(Ccand, factor);

        while (Ccand.cardinality() > 1){
            partition(Ccand);

            Schema temp = (Schema) node.clone();
            deduct(temp, union(Clow, U));

            boolean pass = checker.executeTestCase(Productor.genTestCase(temp, faultCase));

            if(pass){
                Ccand = (Schema) Clow.clone();
            }else {
                Ccand = (Schema) Chigh.clone();
                U.or(Clow);
            }
        }

        return Ccand;
    }

    /**
     * deduct(A, B) : A = A-B
     * deduct will change the first para
     */
    private void deduct(Schema Ccand, Schema U) {
        if(U == null) return;

        int setBitIndex = 0;
        for (int i = 0; i < U.cardinality(); i++) {
            setBitIndex = U.nextSetBit(setBitIndex);
            Ccand.clear(setBitIndex);
            setBitIndex++;
        }
    }

    private void partition(Schema Ccand) {
        int num = Ccand.cardinality();

        Clow.clear();
        Chigh.clear();

        int setBitIndex = 0;
        for (int i = 0; i < (num+1)/2; i++) {
            setBitIndex = Ccand.nextSetBit(setBitIndex);
            Clow.set(setBitIndex);
            setBitIndex++;
        }
        for (int i = (num+1)/2; i < num; i++) {
            setBitIndex = Ccand.nextSetBit(setBitIndex);
            Chigh.set(setBitIndex);
            setBitIndex++;
        }
    }

    private Schema union(Schema a, Schema b){
        Schema res = (Schema) a.clone();
        res.or(b);
        return res;
    }
}
