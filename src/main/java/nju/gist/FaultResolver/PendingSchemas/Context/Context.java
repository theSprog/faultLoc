package nju.gist.FaultResolver.PendingSchemas.Context;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;
import java.util.Set;

public class Context {
    private IStrategy strategy;
    private final SchemasPath pendingSchemas;

    private final Set<Schema> healthSchemas;
    private final Set<Schema> faultSchemas;

    private final Set<Schema> knownMinFaults;

    public Context(SchemasPath pendingSchemas, Set<Schema> healthSchemas, Set<Schema> faultSchemas, Set<Schema> knownMinFaults) {
        this.pendingSchemas = pendingSchemas;
        this.healthSchemas = healthSchemas;
        this.faultSchemas = faultSchemas;
        this.knownMinFaults = knownMinFaults;
    }

    public void setStrategy(IStrategy strategy){
        this.strategy = strategy;
    }

    public ExecutionResult execute(){
        return strategy.execute(this);
    }

    public SchemasPath getPendingSchemas() {
        return pendingSchemas;
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
}
