package nju.gist;

import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasResolver;
import nju.gist.FileResolver.CSVResolver;
import nju.gist.Tester.Checker;

import java.math.BigInteger;
import java.util.List;


public class FaultLocalization {
    private static final String TEST_SET_SUFFIX = ".csv";
    private static final String FAULT_SUFFIX = ".fault";
    private final FaultResolver faultResolver;
    private final List<TestCase> Tfail;
    private final List<TestCase> Tpass;

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
        for (TestCase faultCase : Tfail) {
            faultResolver.setFaultCase(faultCase);

            long startMil = System.currentTimeMillis();
            List<MinFault> minFaults = faultResolver.findMinFaults();
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

        for (TestCase faultCase : Tfail) {
            faultResolver.setFaultCase(faultCase);

            long startMil = System.currentTimeMillis();
//            long pendingSchemasSize = ((PendingSchemasResolver) faultResolver).getPendingSchemasSize();
//            Set<Schema> pendingSchemas = ((PendingSchemasResolver) faultResolver).getPendingSchemas();
            BigInteger res = ((PendingSchemasResolver) faultResolver).getPendingSchemasSizeAdv();
            long endMil = System.currentTimeMillis();

//            System.out.println(faultCase + " fails, \n" +
//                    "the pendingSchemas size is: " + pendingSchemasSize);
//            System.out.println(faultCase + " fails, \n" +
//                    "the pendingSchemas size is: " + pendingSchemas.size());
            System.out.println(faultCase + " fails, \n" +
                    "the pendingSchemas size is: " + res);

            System.out.println("ExecutionTime: " + (endMil - startMil) + "ms");
            System.out.println();
        }
    }

    public void localizationByCA() {
        long startMil = System.currentTimeMillis();
        List<MinFault> minFaults = faultResolver.findMinFaults();
        long endMil = System.currentTimeMillis();

        System.out.println("the minFaults might be:");
        minFaults.forEach(System.out::println);

        System.out.println("ExecutionTime: " + (endMil - startMil) + "ms");
        System.out.println();
    }
}
