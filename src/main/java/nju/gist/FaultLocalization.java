package nju.gist;

import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.LG.LGResolver;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.FaultResolver.SP.SPResolver;
import nju.gist.FileResolver.CSVResolver;
import nju.gist.FileResolver.SafeResolver;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.*;


public class FaultLocalization {
    private static final String TEST_SET_SUFFIX = ".csv";
    private static final String FAULT_SUFFIX = ".fault";
    private static final String SAFE_SUFFIX = ".safe";
    private final FaultResolver faultResolver;
    private final List<TestCase> Tfail;
    private final List<TestCase> Tpass;
    private static final Logger logger = Logger.getLogger(FaultLocalization.class);


    public FaultLocalization(String path, FaultResolver faultResolver) {
        this.faultResolver = faultResolver;

        Checker checker = new Checker(path.replace(TEST_SET_SUFFIX, FAULT_SUFFIX));
        faultResolver.setChecker(checker);

        CSVResolver csvResolver = new CSVResolver(path, checker);
        Tfail = csvResolver.getFaultCaseList();
        Tpass = csvResolver.getHealthCaseList();
        faultResolver.setTfailAndTpass(Tfail, Tpass);

        SafeResolver safeResolver = new SafeResolver(path.replace(TEST_SET_SUFFIX, SAFE_SUFFIX), checker);
        if(safeResolver.hasSafes()){
            Productor.SetSafes(safeResolver.getSafes());
        }else {
            Productor.clearSafes();
        }
    }

    // one faultCase one time, if locate by covering array, use localizationByCA instead
    public Set<MinFault> localization() {
        Set<MinFault> minFaults = new HashSet<>();
        faultResolver.getChecker().clearExecSet();
        for (TestCase faultCase : Tfail) {
            faultResolver.setFaultCase(faultCase);

//            logger.info("start finding MinFaults");
//            long startMil = System.currentTimeMillis();
            minFaults.addAll(faultResolver.findMinFaults());
//            long endMil = System.currentTimeMillis();
//            logger.info("over finding MinFaults" + "ExecutionTime: " + (endMil - startMil) + "ms");
        }
        return minFaults;
    }

    public Map<TestCase, BigInteger> getPendingSchemasSize(){
        Map<TestCase, BigInteger> res = new HashMap<>();

        for (TestCase faultCase : Tfail) {
            faultResolver.setFaultCase(faultCase);
            List<MinFault> minFaults = faultResolver.findMinFaults();
            Set<TestCase> healthTestCases = faultResolver.getHealthTestCases();

//            long startMil = System.currentTimeMillis();
//            logger.debug("start computing pendingSchema size");
            BigInteger pdSize;
            if (faultResolver instanceof LGResolver || faultResolver instanceof SPResolver){
                pdSize = SchemasUtil.getPendingSchemasSizeLGorSP(minFaults, healthTestCases, faultCase);
                pdSize = pdSize.compareTo(BigInteger.ZERO) > 0 ? pdSize : BigInteger.ZERO;
            }else {
                pdSize = SchemasUtil.getPendingSchemasSizeAdv(minFaults, healthTestCases, faultCase);
            }
//            logger.debug("over computing pendingSchema size");
//            long endMil = System.currentTimeMillis();
            res.put(faultCase, pdSize);
        }
        return res;
    }

    public Set<MinFault> localizationByCA() {
        faultResolver.getChecker().clearExecSet();
        assert !Tfail.isEmpty();
        // just for initialization, it is a mistake on design of this project's architecture
        faultResolver.setFaultCase(Tfail.get(0));
        return new HashSet<>(faultResolver.findMinFaults());
    }

    public Double getAvgAdTC() {
        int size = 0;
        faultResolver.getChecker().clearExecSet();
        for (TestCase faultCase : Tfail) {
            // setFaultCase will clear execSet
            faultResolver.setFaultCase(faultCase);
            faultResolver.findMinFaults();
            size += faultResolver.getChecker().getExecSet().size();
        }
        return size / (double) Tfail.size();
    }
}
