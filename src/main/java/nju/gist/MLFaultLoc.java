package nju.gist;

import nju.gist.FaultResolver.FaultResolver;

import java.util.List;

public class MLFaultLoc {
    private final FaultResolver faultResolver;
    private String filePath;

    public MLFaultLoc(String filePath, FaultResolver faultResolver) {
        this.faultResolver = faultResolver;
        this.filePath = filePath;
    }

    public void localization() {
        long startMil = System.currentTimeMillis();
        List<List<Integer>> minFaults = faultResolver.findMinFaults();
        long endMil = System.currentTimeMillis();

        System.out.println("the minFaults might be:");
        minFaults.forEach(System.out::println);

        System.out.println("ExecutionTime: " + (endMil - startMil) + "ms");
        System.out.println();
    }
}
