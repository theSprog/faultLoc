package nju.gist;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.NullableObjectConverter;
import nju.gist.Common.MinFault;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AIFL.AIFLResolver;
import nju.gist.FaultResolver.ComFIL.ComFILResolver;
import nju.gist.FaultResolver.FIC.FICBSResolver;
import nju.gist.FaultResolver.FIC.FICResolver;
import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.InverseCTD.InverseCTDResolver;
import nju.gist.FaultResolver.LG.LGKind;
import nju.gist.FaultResolver.LG.LGResolver;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasResolver;
import nju.gist.FaultResolver.RI.RIResolver;
import nju.gist.FaultResolver.SOFOT.SOFOTResolver;
import nju.gist.FaultResolver.SP.SP;
import nju.gist.FaultResolver.SP.SPResolver;
import nju.gist.FaultResolver.TRTResolver.AdderTRTResolver;
import nju.gist.Tester.Productor;
import nju.gist.expdata.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.log4j.Logger;

public class SubjectExp {
    private static final String PATH = "src/test/resources/nju/gist/external/";
    private static final String NEW_LINE = "\n";
    private FaultResolver faultResolver;
    private static String subjectsFilesPath = PATH + "subjects.txt";
    private static List<String> subjects = parseFilesPath(subjectsFilesPath);
    private static final Logger logger = Logger.getLogger(SubjectExp.class);

    @Test
    public void testPendingSize() {
        Map<TestCase, BigInteger> tcMap;
        FaultLocalization fl;
        List<PendingData> pdDatas = new ArrayList<>();
        Productor.disableSafe();
        for (int i = 0; i < 1; i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            String[] project_path = subject.split(PATH)[1].split("/", 2);
            String project = project_path[0];
            String path = project_path[1].replaceAll("[.]csv", "");

//            faultResolver = new FICResolver();
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger FICpdSize = avgPendingSize(tcMap);
//
//            faultResolver = new FICBSResolver();
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger FICBSpdSize = avgPendingSize(tcMap);
//
//            faultResolver = new AIFLResolver();
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger AIFLpdSize = avgPendingSize(tcMap);
//
//            faultResolver = new InverseCTDResolver();
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger InverseCTDpdSize = avgPendingSize(tcMap);
//
//            faultResolver = new RIResolver();
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger RIpdSize = avgPendingSize(tcMap);
//
//            faultResolver = new SOFOTResolver();
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger SOFOTpdSize = avgPendingSize(tcMap);

//            faultResolver = new LGResolver(LGKind.SafeValueLG);
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger LG1pdSize = avgPendingSize(tcMap);

//            faultResolver = new LGResolver(LGKind.AdvLG);
//            fl = new FaultLocalization(subject, faultResolver);
//            tcMap = fl.getPendingSchemasSize();
//            BigInteger LG2pdSize = avgPendingSize(tcMap);

            BigInteger ComFILpdSize = BigInteger.ZERO;
            BigInteger TRTpdSize = BigInteger.ZERO;
            BigInteger CMSpdSize = BigInteger.ZERO;

            PendingData pendingData = new PendingData();
            pendingData.setProject(project);
            pendingData.setPath(path);
            pendingData.setN(faultResolver.getSize());

//            pendingData.setFIC(FICpdSize);

            pdDatas.add(pendingData);
        }
    }

    private BigInteger avgPendingSize(Map<TestCase, BigInteger> tcMap) {
        BigInteger res = BigInteger.ZERO;
        for (Map.Entry<TestCase, BigInteger> entry : tcMap.entrySet()) {
            BigInteger v = entry.getValue();
            res = res.add(v);
        }
        return res.divide(BigInteger.valueOf(tcMap.size()));
    }

    // precision and recall
    @Test
    public void PRTest() {
        List<PrecisionData> precisions = new ArrayList<>();
        List<RecallData> recalls = new ArrayList<>();
        Productor.enableSafe();
        for (int i = 0; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            Set<MinFault> minFaults = null;
            Set<MinFault> realMinFaults = null;
            Set<MinFault> correctMFS = null;
            PrecisionData pre = new PrecisionData();
            RecallData rec = new RecallData();

            fillProjectAndPath(subject, pre);
            fillProjectAndPath(subject, rec);

            logger.info("FIC start");
            faultResolver = new FICResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFIC(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setFIC((double) correctMFS.size() / realMinFaults.size());

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFICBS(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setFICBS((double) correctMFS.size() / realMinFaults.size());

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setAIFL(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setAIFL((double) correctMFS.size() / realMinFaults.size());

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSOFOT(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setSOFOT((double) correctMFS.size() / realMinFaults.size());

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setTRT(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setTRT((double) correctMFS.size() / realMinFaults.size());

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setComFIL(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setComFIL((double) correctMFS.size() / realMinFaults.size());

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setInverseCTD(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setInverseCTD((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG1(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setLG1((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG2(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setLG2((double) correctMFS.size() / realMinFaults.size());

            logger.info("RI start");
            faultResolver = new RIResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setRI(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setRI((double) correctMFS.size() / realMinFaults.size());

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSP(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setSP((double) correctMFS.size() / realMinFaults.size());

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setCMS(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setCMS((double) correctMFS.size() / realMinFaults.size());

            pre.setN(faultResolver.getSize());
            rec.setN(faultResolver.getSize());

            precisions.add(pre);
            recalls.add(rec);
        }
        String preXlsxName = PATH + "Precision.xlsx";
        EasyExcel.write(preXlsxName, PrecisionData.class).sheet("precision").doWrite(precisions);
        String recXlsxName = PATH + "Recall.xlsx";
        EasyExcel.write(recXlsxName, RecallData.class).sheet("recall").doWrite(recalls);

    }

    @Test
    public void noSafePRTest() {
        List<NoSafePrecisionData> precisions = new ArrayList<>();
        List<NoSafeRecallData> recalls = new ArrayList<>();
        Productor.disableSafe();    // the only difference between PRTest and noSafePRTest
        for (int i = 0; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            Set<MinFault> minFaults = null;
            Set<MinFault> realMinFaults = null;
            Set<MinFault> correctMFS = null;
            NoSafePrecisionData pre = new NoSafePrecisionData();
            NoSafeRecallData rec = new NoSafeRecallData();

            fillProjectAndPath(subject, pre);
            fillProjectAndPath(subject, rec);

            logger.info("FIC start");
            faultResolver = new FICResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFIC(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setFIC((double) correctMFS.size() / realMinFaults.size());

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFICBS(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setFICBS((double) correctMFS.size() / realMinFaults.size());

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setAIFL(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setAIFL((double) correctMFS.size() / realMinFaults.size());

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSOFOT(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setSOFOT((double) correctMFS.size() / realMinFaults.size());

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setTRT(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setTRT((double) correctMFS.size() / realMinFaults.size());

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setComFIL(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setComFIL((double) correctMFS.size() / realMinFaults.size());

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setInverseCTD(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setInverseCTD((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG1(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setLG1((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG2(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setLG2((double) correctMFS.size() / realMinFaults.size());

            logger.info("RI start");
            faultResolver = new RIResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setRI(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setRI((double) correctMFS.size() / realMinFaults.size());

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSP(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setSP((double) correctMFS.size() / realMinFaults.size());

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setCMS(minFaults.size() == 0 ? null : (double)correctMFS.size() / minFaults.size());
            rec.setCMS((double) correctMFS.size() / realMinFaults.size());

            pre.setN(faultResolver.getSize());
            rec.setN(faultResolver.getSize());

            precisions.add(pre);
            recalls.add(rec);
        }
        String preXlsxName = PATH + "NoSafePrecision.xlsx";
        EasyExcel.write(preXlsxName, PrecisionData.class).sheet("precision_NoSafe").doWrite(precisions);
        String recXlsxName = PATH + "NoSafeRecall.xlsx";
        EasyExcel.write(recXlsxName, RecallData.class).sheet("recall_NoSafe").doWrite(recalls);
    }

    @Test
    public void additionalTestCaseTest() {
        List<AdditionalTCData> tcDatas = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            AdditionalTCData tcData = new AdditionalTCData();
            fillProjectAndPath(subject, tcData);
            Double avgAdTC = 0d;

            logger.info("FIC start");
            faultResolver = new FICResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setFIC(avgAdTC);

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setFICBS(avgAdTC);

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setAIFL(faultResolver.getSize() > 15 ? null : avgAdTC);

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setSOFOT(avgAdTC);

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setComFIL(faultResolver.getSize() > 15 ? null : avgAdTC);

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setTRT(faultResolver.getSize() > 24 ? null : avgAdTC);

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setInverseCTD(avgAdTC);

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setLG1(avgAdTC);

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setLG2(avgAdTC);

            logger.info("RI start");
            faultResolver = new RIResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setRI(avgAdTC);

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setSP(faultResolver.getSize() > 100 ? null : avgAdTC);

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setCMS(avgAdTC);

            tcData.setN(faultResolver.getSize());

            tcDatas.add(tcData);
        }
        String additionalTCXlsxName = PATH + "additionalTC.xlsx";
        EasyExcel.write(additionalTCXlsxName, AdditionalTCData.class).sheet("additionalTC").doWrite(tcDatas);
    }

    @Test
    public void execTimeTest() {
        List<TimeData> timeDatas = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            TimeData time = new TimeData();

            fillProjectAndPath(subject, time);
            long start = 0;
            long end = 0;

            logger.info("FIC start");
            faultResolver = new FICResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setFIC(end - start);

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setFICBS(end - start);

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localizationByCA();
            end = System.currentTimeMillis();
            time.setAIFL(faultResolver.getSize() > 15 ? null : end - start);

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setSOFOT(end - start);

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setTRT(faultResolver.getSize() > 24 ? null : end - start);

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localizationByCA();
            end = System.currentTimeMillis();
            time.setComFIL(faultResolver.getSize() > 15 ? null : end - start);

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setInverseCTD(end - start);

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setLG1(end - start);

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setLG2(end - start);

            logger.info("RI start");
            faultResolver = new RIResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setRI(end - start);

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setSP(faultResolver.getSize() > 100 ? null : end - start);

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            start = System.currentTimeMillis();
            new FaultLocalization(subject, faultResolver).localization();
            end = System.currentTimeMillis();
            time.setCMS(end - start);

            time.setN(faultResolver.getSize());

            timeDatas.add(time);
        }
        String timeXlsxName = PATH + "execTime.xlsx";
        EasyExcel.write(timeXlsxName, TimeData.class).sheet("execTime").doWrite(timeDatas);
    }

    private void fillProjectAndPath(String subject, AbstractData data) {
        String[] project_path = subject.split(PATH)[1].split("/", 2);
        String project = project_path[0];
        String path = project_path[1].replaceAll("[.]csv", "");
        data.setProject(project);
        data.setPath(path);
    }


    private Set<MinFault> getCorrectMFS(Set<MinFault> minFaults, Set<MinFault> realMinFaults) {
        Set<MinFault> res = new HashSet<>();
        for (MinFault minFault : minFaults) {
            if (realMinFaults.contains(minFault)) {
                res.add(minFault);
            }
        }
        return res;
    }

    private static List<String> parseFilesPath(String subjectsFilesPath) {
        List<String> res = new ArrayList<>();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(subjectsFilesPath))) {
            while ((line = reader.readLine()) != null) {
                if (line.matches(" *#.*") || line.equals("")) continue;
                String subjectPath = PATH + line + ".csv";
                res.add(subjectPath);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        return res;
    }

    @Test
    public void test() {

    }
}
