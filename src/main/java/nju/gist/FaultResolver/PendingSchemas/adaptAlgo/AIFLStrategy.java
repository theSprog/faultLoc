package nju.gist.FaultResolver.PendingSchemas.adaptAlgo;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.AIFL.AIFL;
import nju.gist.FaultResolver.PendingSchemas.Context.Context;
import nju.gist.FaultResolver.PendingSchemas.Context.ExecutionResult;
import nju.gist.FaultResolver.PendingSchemas.Context.IStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AIFLStrategy implements IStrategy {
    private final AIFL aifl;

    private final Set<List<Integer>> Tfail;
    private final Set<List<Integer>> Tpass;
    private final int Threshold;
    private List<List<Integer>> suspMinFaults;

    public AIFLStrategy(AIFL aifl) {
        this.aifl = aifl;

        this.Tpass = aifl.getTpass();
        this.Tfail = aifl.getTfail();
        this.Threshold = aifl.getThreshold();
        this.suspMinFaults = aifl.getSuspMinFaults();
    }

    @Override
    public ExecutionResult execute(Context ctx) {
        Set<Schema> knownMinFaults = ctx.getKnownMinFaults();

        int n = aifl.getFaultCaseSize();

        Set<List<Integer>> suspSetOld = new HashSet<>();

        // suspSet^(0) = SchemaSet(Tfail) - SchemaSet(Tpass)
        Set<List<Integer>> suspSetNew = aifl.minus(aifl.getSchemaSet(Tfail), aifl.getSchemaSet(Tpass));

        for (int i = 0; i < n; i++) {
            if(suspSetNew.size() <= Threshold || suspSetOld.equals(suspSetNew)) break;

            Set<List<Integer>> ATpass = aifl.getATpass(aifl.generateAT(i+1, Tfail));
            suspSetOld = suspSetNew;
            suspSetNew = aifl.minus(suspSetOld, aifl.getSchemaSet(ATpass));
        }

        suspMinFaults.addAll(suspSetNew);
        return new ExecutionResult(false, false, knownMinFaults);
    }
}
