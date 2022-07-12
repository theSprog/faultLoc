package nju.gist.FaultResolver.LG;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SafeValueLG extends LG{
    private final Checker checker;
    private final Set<Schema> knownMinFaults;
    // record the 1-way fault interaction
    private Set<Schema> loops;

    public SafeValueLG(Checker checker, Set<Schema> knownMinFaults) {
        this.checker = checker;
        this.knownMinFaults = knownMinFaults;
        this.loops = new HashSet<>();
    }

    @Override
    public void locate(TestCase testCase) {
        Schema faultPattern = new Schema(testCase.size(), true);
        locateError(faultPattern, testCase);
    }

    private void locateError(Schema faultPattern, TestCase testCase) {
        if(faultPattern.cardinality() == 1) {
            boolean pass = checker.executeTestCase(Productor.genTestCase(faultPattern, testCase));
            if(!pass){
                knownMinFaults.add(faultPattern);
                loops.add(faultPattern);
            }
        }else {
            Parts parts = new Parts(faultPattern);
            Schema leftPattern = parts.left;
            Schema rightPattern = parts.right;

            boolean leftPass = checker.executeTestCase(Productor.genTestCase(leftPattern, testCase));
            if(!leftPass){
                locateError(leftPattern, testCase);
            }

            boolean rightPass = checker.executeTestCase(Productor.genTestCase(rightPattern, testCase));
            if(!rightPass){
                locateError(rightPattern, testCase);
            }

            acrossLocate(leftPattern, rightPattern, testCase);
        }
    }

    private void acrossLocate(Schema leftPattern, Schema rightPattern, TestCase testCase) {
        int c1 = leftPattern.cardinality();
        int c2 = rightPattern.cardinality();

        for (int i = 0; i < c1; i++) {
            for (int j = 0; j < c2; j++) {
                Schema edgeSchema = getEdgeSchema(leftPattern, i, rightPattern, j);
                if(containLoop(edgeSchema, loops)) continue;
                boolean pass = checker.executeTestCase(Productor.genTestCase(edgeSchema, testCase));
                if(!pass){
                    knownMinFaults.add(edgeSchema);
                }
            }
        }
    }

    /**
     *
     * @param edgeSchema the edge we took
     * @param loops the loop(1-way interaction) we found
     * @return  true if edge contains loop(2-way contains 1-way), false otherwise
     */
    private boolean containLoop(Schema edgeSchema, Set<Schema> loops) {
        for (Schema loop : loops) {
            if(SchemasUtil.isSuperSchema(edgeSchema, loop)){
                return true;
            }
        }
        return false;
    }

    private Schema getEdgeSchema(Schema leftPattern, int i, Schema rightPattern, int j) {
        Schema schema = new Schema(leftPattern.size());

        int leftIth = -1;
        for (int i1 = 0; i1 <= i; i1++) {
            leftIth = leftPattern.nextSetBit(leftIth+1);
        }

        int rightJth = -1;
        for (int j1 = 0; j1 <= j; j1++) {
            rightJth = rightPattern.nextSetBit(rightJth+1);
        }

        schema.set(leftIth);
        schema.set(rightJth);

        return schema;
    }
}
