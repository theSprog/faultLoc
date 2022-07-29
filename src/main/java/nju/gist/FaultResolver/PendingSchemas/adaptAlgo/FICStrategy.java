package nju.gist.FaultResolver.PendingSchemas.adaptAlgo;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.FIC.FIC;
import nju.gist.FaultResolver.PendingSchemas.Context.Context;
import nju.gist.FaultResolver.PendingSchemas.Context.ExecutionResult;
import nju.gist.FaultResolver.PendingSchemas.Context.IStrategy;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.Tester.Checker;

import java.util.List;
import java.util.Set;

public class FICStrategy implements IStrategy {
    private final FIC fic;

    public FICStrategy(FIC fic) {
        this.fic = fic;
    }

    @Override
    public ExecutionResult execute(Context ctx) {
        boolean healthSchemasChanged = false;
        boolean faultSchemasChanged = false;


        SchemasPath pendingSchemaPath = ctx.getPendingSchemasPath();
        Set<Schema> faultSchemas = ctx.getFaultSchemas();
        Set<Schema> healthSchemas = ctx.getHealthSchemas();
        Set<Schema> knownMinFaults = ctx.getKnownMinFaults();
        TestCase faultCase = ctx.getFaultCase();
        Checker checker = ctx.getChecker();

        // fic only need upperBound, but we don't change the upperBound, so we clone it
        Schema currentPattern = pendingSchemaPath.getUp().clone();
        while (true) {
            Schema faultPattern = fic.extractOneFaultPattern(currentPattern);
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
            fic.removeFaultPattern(currentPattern, faultPattern);
        }

        return new ExecutionResult(faultSchemasChanged, healthSchemasChanged, knownMinFaults);
    }
}
