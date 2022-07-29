package nju.gist.FaultResolver.PendingSchemas.adaptAlgo;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.PendingSchemas.Context.Context;
import nju.gist.FaultResolver.PendingSchemas.Context.ExecutionResult;
import nju.gist.FaultResolver.PendingSchemas.Context.IStrategy;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.FaultResolver.RI.RI;
import nju.gist.Tester.Checker;

import java.util.Set;

public class RIStrategy implements IStrategy {
    private final RI ri;

    public RIStrategy(RI ri) {
        this.ri = ri;
    }

    @Override
    public ExecutionResult execute(Context ctx) {
        boolean healthSchemasChanged = false;
        boolean faultSchemasChanged = false;


        SchemasPath pendingSchema = ctx.getPendingSchemasPath();
        Set<Schema> faultSchemas = ctx.getFaultSchemas();
        Set<Schema> healthSchemas = ctx.getHealthSchemas();
        Set<Schema> knownMinFaults = ctx.getKnownMinFaults();
        TestCase faultCase = ctx.getFaultCase();
        Checker checker = ctx.getChecker();

        Schema currentPattern = pendingSchema.getUp().clone();
        while (true) {
            Schema faultPattern = ri.extractOneFaultPattern(currentPattern);
            if (faultPattern == null) {
                healthSchemasChanged = healthSchemas.addAll(SchemasUtil.tcs2Schemas(checker.getHtc(), faultCase));
                checker.clearHtc();

                // more fast method
//                healthSchemasChanged = healthSchemas.add(currentPattern);
                break;
            }
            // 而这句话可能被执行多次, 所以需要用 |= 来查看是否有 changed
            faultSchemasChanged |= faultSchemas.add(faultPattern);
            knownMinFaults.add(faultPattern);
            ri.removeFaultPattern(currentPattern, faultPattern);
        }

        return new ExecutionResult(faultSchemasChanged, healthSchemasChanged, knownMinFaults);
    }
}
