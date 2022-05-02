package nju.gist.FaultResolver.RI;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.List;

public class RI {
    private final Checker checker;
    private final List<Integer> faultCase;
    private final Schema faultCasePattern;
    private final Integer size;

    private Schema S1;
    private Schema S2;

    public RI(Checker checker, List<Integer> faultCase, Schema faultCasePattern) {
        this.checker = checker;
        this.faultCase = faultCase;
        this.faultCasePattern = faultCasePattern;
        this.size = faultCasePattern.size();
    }

    // RI algorithm
    public Schema extractOneFaultPattern(Schema currentPattern) {
        boolean pass = checker.executeTestCase(Productor.genTestCase(currentPattern, faultCase));
        if (pass) {
            return null;
        }

        Schema S = (Schema) currentPattern.clone();
        Schema S_related = new Schema(S.size());
        while (true) {
            Schema errorFactor = isolate(S, currentPattern);
            S_related.or(errorFactor);
            pass = checker.executeTestCase(Productor.genTestCase(S_related, faultCase));
            if (pass) {
                S = SchemasUtil.getDiffs(S_related, currentPattern);
            } else {
                return S_related;
            }
        }
    }

    public void removeFaultPattern(Schema currentPattern, Schema faultPattern) {
        int lowestOneBitIndex = faultPattern.nextSetBit(0);
        while (lowestOneBitIndex != -1){
            currentPattern.clear(lowestOneBitIndex);
            lowestOneBitIndex = faultPattern.nextSetBit(lowestOneBitIndex + 1);
        }
    }

    private Schema genTnext(Schema currentPattern, Schema S_unrelated){
        Schema temp = SchemasUtil.getOr(S1, S_unrelated);
        Schema Tnext = (Schema) currentPattern.clone();

        int lowestOneBitIndex = temp.nextSetBit(0);
        while (lowestOneBitIndex != -1){
            Tnext.clear(lowestOneBitIndex);
            lowestOneBitIndex = temp.nextSetBit(lowestOneBitIndex + 1);
        }

        return Tnext;
    }

    private Schema isolate(Schema susp_schema, Schema currentPattern) {
        Schema S = (Schema) susp_schema.clone();
        Schema S_unrelated = new Schema(susp_schema.size());

        while(S.cardinality() != 1) {
            divide(S);
            Schema T_next = genTnext(currentPattern, S_unrelated);
            boolean pass = checker.executeTestCase(Productor.genTestCase(T_next, faultCase));
            if(pass) {
                S = S1;
            }else {
                S = S2;
                S_unrelated.or(S1);
            }
        }

        return S;
    }

    private void divide(Schema S){
        S1 = new Schema(S.size());
        int S1_size = S.cardinality() / 2;
        int lowestOneBitIndex = S.nextSetBit(0);
        while (S1_size != 0){
            S1.set(lowestOneBitIndex);
            lowestOneBitIndex = S.nextSetBit(lowestOneBitIndex + 1);
            S1_size--;
        }
        S2 = SchemasUtil.getDiffs(S1, S);
    }
}
