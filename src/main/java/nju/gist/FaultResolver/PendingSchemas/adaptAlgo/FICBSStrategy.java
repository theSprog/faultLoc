package nju.gist.FaultResolver.PendingSchemas.adaptAlgo;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.FIC.FICBS;
import nju.gist.FaultResolver.PendingSchemas.Context.Context;
import nju.gist.FaultResolver.PendingSchemas.Context.ExecutionResult;
import nju.gist.FaultResolver.PendingSchemas.Context.IStrategy;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.Tester.Checker;

import java.util.List;
import java.util.Set;

public class FICBSStrategy implements IStrategy {
    private final FICBS fic_bs;

    public FICBSStrategy(FICBS fic_bs) {
        this.fic_bs = fic_bs;
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
        Checker checker =  ctx.getChecker();

        Schema currentPattern = pendingSchema.getUp().clone();
        while (true) {
            Schema oneFaultPattern = fic_bs.extractOneFaultPattern(currentPattern);
            if (oneFaultPattern == null) {
                Set<TestCase> htc = checker.getHtc();
                Set<Schema> schemas = SchemasUtil.tcs2Schemas(htc, faultCase);
                healthSchemasChanged = healthSchemas.addAll(schemas);
                // 清除上一轮的HSS，否则会造成重复计算，影响性能
                checker.clearHtc();
                break;
//                // 因为这句话只能被执行一次， 所以直接赋值
//                healthSchemasChanged = healthSchemas.add(currentPattern);
//                break;
            }
            // 而这句话可能被执行多次, 所以需要用 |= 来查看是否有 changed
            faultSchemasChanged |= faultSchemas.add(oneFaultPattern);
            knownMinFaults.add(oneFaultPattern);
            fic_bs.removeFaultPattern(currentPattern, oneFaultPattern);
        }

        return new ExecutionResult(faultSchemasChanged, healthSchemasChanged, knownMinFaults);
    }
}
