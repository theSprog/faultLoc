package nju.gist;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasResolver;
import nju.gist.FileResolver.CSVResolver;
import nju.gist.Tester.Checker;

import java.util.List;
import java.util.Set;


public class FaultLocalization {
    private static final String TEST_SET_SUFFIX = ".csv";
    private static final String FAULT_SUFFIX = ".fault";
    private final FaultResolver faultResolver;
    private final List<List<Integer>> Tfail;
    private final List<List<Integer>> Tpass;

    public FaultLocalization(String path, FaultResolver faultResolver) {
        this.faultResolver = faultResolver;

        Checker checker = new Checker(path.replace(TEST_SET_SUFFIX, FAULT_SUFFIX));
        faultResolver.setChecker(checker);

        CSVResolver csvResolver = new CSVResolver(path, checker);
        Tfail = csvResolver.getFaultCaseList();
        Tpass = csvResolver.getHealthCaseList();
        faultResolver.setTfailAndTpass(Tfail, Tpass);
    }

    public void localization() {
        for (List<Integer> faultCase : Tfail) {
            faultResolver.setFaultCase(faultCase);

            long startMil = System.currentTimeMillis();
            List<List<Integer>> minFaults = faultResolver.findMinFaults();
            long endMil = System.currentTimeMillis();

            System.out.println(faultCase + " fails, and the minFaults might be:");
            minFaults.forEach(System.out::println);

            System.out.println("ExecutionTime: " + (endMil - startMil) + "ms");
            System.out.println();
        }
    }

    public void getPendingSchemasSize(){
        // 除了 PendingSchemasResolver, 其他的 faultResolver 没有 pendingSchemas 这个概念
        if(!(faultResolver instanceof PendingSchemasResolver)){
            throw new IllegalCallerException("Only PendingSchemasResolver has method getPendingSchemas");
        }

        for (List<Integer> faultCase : Tfail) {
            faultResolver.setFaultCase(faultCase);

            long startMil = System.currentTimeMillis();
            long pendingSchemasSize = ((PendingSchemasResolver) faultResolver).getPendingSchemasSize();
//            Set<Schema> pendingSchemas = ((PendingSchemasResolver) faultResolver).getPendingSchemas();
            long endMil = System.currentTimeMillis();

            System.out.println(faultCase + " fails, \n" +
                    "the pendingSchemas size is:" + pendingSchemasSize);
//            System.out.println(faultCase + " fails, \n" +
//                    "the pendingSchemas size is:" + pendingSchemas.size());

            System.out.println("ExecutionTime: " + (endMil - startMil) + "ms");
            System.out.println();
        }
    }

    public void localizationByCA() {
        faultResolver.initFromCA(Tfail, Tpass);
        long startMil = System.currentTimeMillis();
        List<List<Integer>> minFaults = faultResolver.findMinFaults();
        long endMil = System.currentTimeMillis();

        System.out.println("the minFaults might be:");
        minFaults.forEach(System.out::println);

        System.out.println("ExecutionTime: " + (endMil - startMil) + "ms");
        System.out.println();
    }
}
