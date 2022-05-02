package nju.gist.FaultResolver.PendingSchemas.Context;

import nju.gist.Common.Schema;

import java.util.Set;

public class ExecutionResult {
    private final boolean faultSchemasChanged;
    private final boolean healthSchemasChanged;

    public ExecutionResult(boolean faultSchemasChanged, boolean healthSchemasChanged, Set<Schema> foundMinfaults) {
        this.faultSchemasChanged = faultSchemasChanged;
        this.healthSchemasChanged = healthSchemasChanged;
    }

    public boolean faultSchemasChanged() {
        return faultSchemasChanged;
    }

    public boolean healthSchemasChanged() {
        return healthSchemasChanged;
    }
}
