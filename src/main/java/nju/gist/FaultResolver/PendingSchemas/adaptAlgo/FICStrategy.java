package nju.gist.FaultResolver.PendingSchemas.adaptAlgo;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.FIC.FIC;
import nju.gist.FaultResolver.PendingSchemas.Context.Context;
import nju.gist.FaultResolver.PendingSchemas.Context.ExecutionResult;
import nju.gist.FaultResolver.PendingSchemas.Context.IStrategy;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;

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


        SchemasPath pendingSchema = ctx.getPendingSchemas();
        Set<Schema> faultSchemas = ctx.getFaultSchemas();
        Set<Schema> healthSchemas = ctx.getHealthSchemas();
        Set<Schema> knownMinFaults = ctx.getKnownMinFaults();

        // fic 只需要上界, 但注意, 我么不改变原上界, 所以 clone 一份
        Schema currentPattern = (Schema)pendingSchema.getUp().clone();
        while (true) {
            Schema faultPattern = fic.extractOneFaultPattern(currentPattern);
            if (faultPattern == null) {
                // 因为这句话只能被执行一次， 所以直接赋值
                healthSchemasChanged = healthSchemas.add(currentPattern);
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
