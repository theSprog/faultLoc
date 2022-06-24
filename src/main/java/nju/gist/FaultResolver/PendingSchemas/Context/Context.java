package nju.gist.FaultResolver.PendingSchemas.Context;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;

import java.util.List;
import java.util.Set;

public class Context {
    private IStrategy strategy;
    private final SchemasPath pendingSchemasPath;

    private final Set<Schema> healthSchemas;
    private final Set<Schema> faultSchemas;

    private final Set<Schema> knownMinFaults;

    private final List<Integer> faultCase;

    public Context(SchemasPath pendingSchemasPath,
                   Set<Schema> healthSchemas,
                   Set<Schema> faultSchemas,
                   Set<Schema> knownMinFaults,
                   List<Integer> faultCase) {
        this.pendingSchemasPath = pendingSchemasPath;
        this.healthSchemas = healthSchemas;
        this.faultSchemas = faultSchemas;
        this.knownMinFaults = knownMinFaults;
        this.faultCase = faultCase;
    }

    public void setStrategy(IStrategy strategy){
        this.strategy = strategy;
    }

    public ExecutionResult execute(){
        return strategy.execute(this);
    }

    public SchemasPath getPendingSchemasPath() {
        return pendingSchemasPath;
    }

    public Set<Schema> getHealthSchemas() {
        return healthSchemas;
    }

    public Set<Schema> getFaultSchemas() {
        return faultSchemas;
    }

    public Set<Schema> getKnownMinFaults() {
        return knownMinFaults;
    }

    public List<Integer> getFaultCase() {
        return faultCase;
    }
}
