package nju.gist.FaultResolver.PendingSchemas.Context;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;
import nju.gist.Tester.Checker;

import java.util.List;
import java.util.Set;

public class Context {
    private IStrategy strategy;
    private final SchemasPath pendingSchemasPath;

    private final Set<Schema> healthSchemas;
    private final Set<Schema> faultSchemas;

    private final Set<Schema> knownMinFaults;

    private final TestCase faultCase;
    private final Checker checker;

    public Context(SchemasPath pendingSchemasPath,
                   Set<Schema> healthSchemas,
                   Set<Schema> faultSchemas,
                   Set<Schema> knownMinFaults,
                   TestCase faultCase,
                   Checker checker) {
        this.pendingSchemasPath = pendingSchemasPath;
        this.healthSchemas = healthSchemas;
        this.faultSchemas = faultSchemas;
        this.knownMinFaults = knownMinFaults;
        this.faultCase = faultCase;
        this.checker = checker;
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

    public TestCase getFaultCase() {
        return faultCase;
    }

    public Checker getChecker() {
        return checker;
    }
}
