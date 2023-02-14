package nju.gist;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
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
import nju.gist.FaultResolver.SP.SPResolver;
import nju.gist.FaultResolver.TRTResolver.AdderTRTResolver;
import nju.gist.Tester.Productor;
import nju.gist.expdata.*;
import org.junit.Test;
import nju.gist.FaultLocalization.TCInfo;
import nju.gist.FaultLocalization.ADTCInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class SubjectExp {
    public static final String PATH = "src/test/resources/nju/gist/external/";
    private FaultResolver faultResolver;
    private static final String subjectsFilesPath = PATH + "overlap.txt";
    private static final List<String> subjects = parseFilesPath(subjectsFilesPath);
    private static final Logger logger = Logger.getLogger(SubjectExp.class);

    @Test
    public void testPendingSize() {
        Productor.disableSafe();
        FaultLocalization fl;
        String pdXlsxName = PATH + "PendingData.xlsx";
        ExcelWriter excelWriter = EasyExcel.write(pdXlsxName, PendingData.class).build();
        for (int i = 116; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            Map<String, List<TCInfo>> tcInfos = new HashMap<>();

            logger.info("FIC start");
            faultResolver = new FICResolver();
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> FICTCInfo = fl.getPendingSchemasSize();
            tcInfos.put("FIC", FICTCInfo);

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> FICBSTCInfo = fl.getPendingSchemasSize();
            tcInfos.put("FICBS", FICBSTCInfo);

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> AIFLTCInfo = fl.getPendingSchemasSize();
            tcInfos.put("AIFL", AIFLTCInfo);

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> InverseCTDTCInfo = fl.getPendingSchemasSize();
            tcInfos.put("InverseCTD", InverseCTDTCInfo);

            logger.info("RI start");
            faultResolver = new RIResolver();
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> RITCInfo = fl.getPendingSchemasSize();
            tcInfos.put("RI", RITCInfo);

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> SOFOTTCInfo = fl.getPendingSchemasSize();
            tcInfos.put("SOFOT", SOFOTTCInfo);

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> LG1TCInfo = fl.getPendingSchemasSize();
            tcInfos.put("LG1", LG1TCInfo);

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> LG2TCInfo = fl.getPendingSchemasSize();
            tcInfos.put("LG2", LG2TCInfo);

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            fl = new FaultLocalization(subject, faultResolver);
            List<TCInfo> SPTCInfo = fl.getPendingSchemasSize();
            tcInfos.put("SP", SPTCInfo);

            List<PendingData> pdDatas = new ArrayList<>();
            int failCaseNum = FICTCInfo.size();
            assert failCaseNum != 0;
            for (int j = 0; j < failCaseNum; j++) {
                PendingData pdData = new PendingData();
                pdData.fillProjectAndPath(subject);
                pdData.setN(Productor.faultCaseSize);
                pdData.setIndex(j);
                pdData.setTestCase(tcInfos.get("FIC").get(j).tc.toString());

                pdData.setFIC(tcInfos.get("FIC").get(j).pdSize);
                pdData.setFICBS(tcInfos.get("FICBS").get(j).pdSize);
                pdData.setAIFL(Productor.faultCaseSize > 15 ? null : tcInfos.get("AIFL").get(j).pdSize);
                pdData.setInverseCTD(tcInfos.get("InverseCTD").get(j).pdSize);
                pdData.setRI(tcInfos.get("RI").get(j).pdSize);
                pdData.setSOFOT(tcInfos.get("SOFOT").get(j).pdSize);
                pdData.setLG1(tcInfos.get("LG1").get(j).pdSize);
                pdData.setLG2(tcInfos.get("LG2").get(j).pdSize);
                pdData.setSP(tcInfos.get("SP").get(j).pdSize);

                // This three method must be zero for testcase with small size,
                // or can not deal with testcase with big size
                pdData.setComFIL(Productor.faultCaseSize > 15 ? null : BigInteger.ZERO);
                pdData.setTRT(Productor.faultCaseSize > 24 ? null : BigInteger.ZERO);
                pdData.setCMS(BigInteger.ZERO);

                pdDatas.add(pdData);
            }
            WriteSheet writeSheet = EasyExcel.writerSheet(pdDatas.get(0).getProject()).build();
            excelWriter.write(pdDatas, writeSheet);
        }
        excelWriter.finish();
    }

    // precision and recall
    @Test
    public void PRTest() {
        Productor.enableSafe();
        List<PrecisionData> precisions = new ArrayList<>();
        List<RecallData> recalls = new ArrayList<>();
        for (int i = 3; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            Set<MinFault> minFaults = null;
            Set<MinFault> realMinFaults = null;
            Set<MinFault> correctMFS = null;
            PrecisionData pre = new PrecisionData();
            RecallData rec = new RecallData();

            pre.fillProjectAndPath(subject);
            rec.fillProjectAndPath(subject);

            logger.info("FIC start");
            faultResolver = new FICResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFIC(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setFIC((double) correctMFS.size() / realMinFaults.size());

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFICBS(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setFICBS((double) correctMFS.size() / realMinFaults.size());

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setAIFL(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setAIFL((double) correctMFS.size() / realMinFaults.size());

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSOFOT(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setSOFOT((double) correctMFS.size() / realMinFaults.size());

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setTRT(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setTRT((double) correctMFS.size() / realMinFaults.size());

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setComFIL(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setComFIL((double) correctMFS.size() / realMinFaults.size());

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setInverseCTD(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setInverseCTD((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG1(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setLG1((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG2(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setLG2((double) correctMFS.size() / realMinFaults.size());

            logger.info("RI start");
            faultResolver = new RIResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setRI(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setRI((double) correctMFS.size() / realMinFaults.size());

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSP(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setSP((double) correctMFS.size() / realMinFaults.size());

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setCMS(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setCMS((double) correctMFS.size() / realMinFaults.size());

            pre.setN(Productor.faultCaseSize);
            rec.setN(Productor.faultCaseSize);

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
        Productor.disableSafe();    // the only difference between PRTest and noSafePRTest
        List<NoSafePrecisionData> precisions = new ArrayList<>();
        List<NoSafeRecallData> recalls = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            Set<MinFault> minFaults = null;
            Set<MinFault> realMinFaults = null;
            Set<MinFault> correctMFS = null;
            NoSafePrecisionData pre = new NoSafePrecisionData();
            NoSafeRecallData rec = new NoSafeRecallData();

            pre.fillProjectAndPath(subject);
            rec.fillProjectAndPath(subject);

            logger.info("FIC start");
            faultResolver = new FICResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFIC(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setFIC((double) correctMFS.size() / realMinFaults.size());

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setFICBS(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setFICBS((double) correctMFS.size() / realMinFaults.size());

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setAIFL(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setAIFL((double) correctMFS.size() / realMinFaults.size());

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSOFOT(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setSOFOT((double) correctMFS.size() / realMinFaults.size());

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setTRT(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setTRT((double) correctMFS.size() / realMinFaults.size());

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setComFIL(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setComFIL((double) correctMFS.size() / realMinFaults.size());

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setInverseCTD(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setInverseCTD((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG1(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setLG1((double) correctMFS.size() / realMinFaults.size());

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setLG2(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setLG2((double) correctMFS.size() / realMinFaults.size());

            logger.info("RI start");
            faultResolver = new RIResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setRI(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setRI((double) correctMFS.size() / realMinFaults.size());

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            minFaults = new FaultLocalization(subject, faultResolver).localizationByCA();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setSP(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setSP((double) correctMFS.size() / realMinFaults.size());

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            minFaults = new FaultLocalization(subject, faultResolver).localization();
            realMinFaults = faultResolver.getChecker().faults2minFaults();
            correctMFS = getCorrectMFS(minFaults, realMinFaults);
            pre.setCMS(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
            rec.setCMS((double) correctMFS.size() / realMinFaults.size());

            pre.setN(Productor.faultCaseSize);
            rec.setN(Productor.faultCaseSize);

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
        Productor.disableSafe();
        ExcelWriter excelWriter = EasyExcel.write(PATH + "additionalTC.xlsx", AdditionalTCData.class).build();
        for (int i = 0; i < subjects.size(); i++) {
            int failCaseNum;
            int failCaseSize;
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));

            logger.info("FIC start");
            faultResolver = new FICResolver();
            List<ADTCInfo> FICadTC = new FaultLocalization(subject, faultResolver).getAdTC();
            failCaseSize = Productor.faultCaseSize;
            failCaseNum = FICadTC.size();

            logger.info("FICBS start");
            faultResolver = new FICBSResolver();
            List<ADTCInfo> FICBSadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("AIFL start");
            faultResolver = new AIFLResolver();
            List<ADTCInfo> AIFLadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("InverseCTD start");
            faultResolver = new InverseCTDResolver();
            List<ADTCInfo> InverseCTDadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("RI start");
            faultResolver = new RIResolver();
            List<ADTCInfo> RIadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            List<ADTCInfo> SOFOTadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("LG1 start");
            faultResolver = new LGResolver(LGKind.SafeValueLG);
            List<ADTCInfo> LG1adTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("LG2 start");
            faultResolver = new LGResolver(LGKind.AdvLG);
            List<ADTCInfo> LG2adTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            List<ADTCInfo> ComFILadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            List<ADTCInfo> TRTadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("SP start");
            faultResolver = new SPResolver(2);
            List<ADTCInfo> SPadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            List<ADTCInfo> CMSadTC = new FaultLocalization(subject, faultResolver).getAdTC();

            List<AdditionalTCData> tcDatas = new ArrayList<>();
            for (int j = 0; j < failCaseNum; j++) {
                AdditionalTCData tcData = new AdditionalTCData();
                // necessary information
                tcData.fillProjectAndPath(subject);
                tcData.setN(failCaseSize);

                // set every test case information
                tcData.setIndex(j);
                tcData.setTestCase(FICadTC.get(j).tc.toString());

                //set additional test case number of every algorithm
                tcData.setFIC(FICadTC.get(j).additionalTCNum);
                tcData.setFICBS(FICBSadTC.get(j).additionalTCNum);
                tcData.setAIFL(failCaseSize > 15 ? null : AIFLadTC.get(j).additionalTCNum);
                tcData.setInverseCTD(InverseCTDadTC.get(j).additionalTCNum);
                tcData.setRI(RIadTC.get(j).additionalTCNum);
                tcData.setSOFOT(SOFOTadTC.get(j).additionalTCNum);
                tcData.setLG1(LG1adTC.get(j).additionalTCNum);
                tcData.setLG2(LG2adTC.get(j).additionalTCNum);
                tcData.setComFIL(failCaseSize > 15 ? null : ComFILadTC.get(j).additionalTCNum);
                tcData.setTRT(failCaseSize > 24 ? null : TRTadTC.get(j).additionalTCNum);
                tcData.setSP(failCaseSize > 100 ? null : SPadTC.get(j).additionalTCNum);
                tcData.setCMS(CMSadTC.get(j).additionalTCNum);

                tcDatas.add(tcData);
            }
            WriteSheet writeSheet = EasyExcel.writerSheet("subject" + i).build();
            excelWriter.write(tcDatas, writeSheet);
        }
        excelWriter.finish();
    }

    // average additional TestCase
    @Test
    public void additionalTestCaseTestAvg() {
        Productor.disableSafe();
        List<AdditionalTCAvgData> tcDatas = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            String subject = subjects.get(i);
            logger.info(String.format("Enter %d-th subject: %s", i, subject));
            AdditionalTCAvgData tcData = new AdditionalTCAvgData();
            tcData.fillProjectAndPath(subject);
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
            tcData.setAIFL(Productor.faultCaseSize > 15 ? null : avgAdTC);

            logger.info("SOFOT start");
            faultResolver = new SOFOTResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setSOFOT(avgAdTC);

            logger.info("ComFIL start");
            faultResolver = new ComFILResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setComFIL(Productor.faultCaseSize > 15 ? null : avgAdTC);

            logger.info("TRT start");
            faultResolver = new AdderTRTResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setTRT(Productor.faultCaseSize > 24 ? null : avgAdTC);

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
            tcData.setSP(Productor.faultCaseSize > 100 ? null : avgAdTC);

            logger.info("CMS start");
            faultResolver = new PendingSchemasResolver();
            avgAdTC = new FaultLocalization(subject, faultResolver).getAvgAdTC();
            tcData.setCMS(avgAdTC);

            tcData.setN(Productor.faultCaseSize);

            tcDatas.add(tcData);
        }
        String additionalTCXlsxName = PATH + "additionalTCAvg.xlsx";
        EasyExcel.write(additionalTCXlsxName, AdditionalTCAvgData.class).sheet("additionalTC").doWrite(tcDatas);
    }

    @Test
    public void execTimeTest() {
        Productor.disableSafe();
        String timeXlsxName = PATH + "execTime.xlsx";
        ExcelWriter excelWriter = EasyExcel.write(timeXlsxName, TimeData.class).build();

        // count represents number of tests
        for (int count = 1; count <= 10; count++) {
            List<TimeData> timeDatas = new ArrayList<>();
            for (int i = 0; i < subjects.size(); i++) {
                String subject = subjects.get(i);
                logger.info(String.format("Enter %d-th subject: %s", i, subject));
                TimeData time = new TimeData();
                time.fillProjectAndPath(subject);
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
                time.setAIFL(Productor.faultCaseSize > 15 ? null : end - start);

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
                time.setTRT(Productor.faultCaseSize > 24 ? null : end - start);

                logger.info("ComFIL start");
                faultResolver = new ComFILResolver();
                start = System.currentTimeMillis();
                new FaultLocalization(subject, faultResolver).localizationByCA();
                end = System.currentTimeMillis();
                time.setComFIL(Productor.faultCaseSize > 15 ? null : end - start);

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
                new FaultLocalization(subject, faultResolver).localizationByCA();
                end = System.currentTimeMillis();
                time.setSP(Productor.faultCaseSize > 100 ? null : end - start);

                logger.info("CMS start");
                faultResolver = new PendingSchemasResolver();
                start = System.currentTimeMillis();
                new FaultLocalization(subject, faultResolver).localization();
                end = System.currentTimeMillis();
                time.setCMS(end - start);

                time.setN(Productor.faultCaseSize);

                timeDatas.add(time);
            }
            WriteSheet writeSheet = EasyExcel.writerSheet("execTime_" + count).build();
            excelWriter.write(timeDatas, writeSheet);
        }

        // do not forget finish it!
        excelWriter.finish();
    }

    @Test
    public void strategyPendingTest(){
        String strategyXlsxName = PATH + "PendingStrategy.xlsx";
        ExcelWriter excelWriter = EasyExcel.write(strategyXlsxName, StrategyData.class).build();
        Set<MinFault> minFaults = null;
        Set<MinFault> realMinFaults = null;
        Set<MinFault> correctMFS = null;

        Map<PendingSchemasResolver.StrategyKind, Set<Integer>> forbid = new HashMap<>();
        for (PendingSchemasResolver.StrategyKind strategyKind : PendingSchemasResolver.StrategyKind.values()) {
            forbid.put(strategyKind, new HashSet<>());
        }

        // 手动记录会 OOM 的 subject
        forbid.get(PendingSchemasResolver.StrategyKind.FICStrategy).add(18);
        forbid.get(PendingSchemasResolver.StrategyKind.FICBSStrategy).add(18);
        forbid.get(PendingSchemasResolver.StrategyKind.RIStrategy).add(18);

        Long start;
        Long end;
        for (PendingSchemasResolver.StrategyKind strategyKind : PendingSchemasResolver.StrategyKind.values()) {
            List<StrategyData> strategyDatas = new ArrayList<>();
            for (int i = 0; i < subjects.size(); i++) {
                List<Long> execTimes = new ArrayList<>();
                String subject = subjects.get(i);
                logger.info(String.format("%s: Enter %d-th subject: %s", strategyKind, i, subject));
                StrategyData strategyData = new StrategyData();
                strategyData.fillProjectAndPath(subject);

                if(forbid.get(strategyKind).contains(i)){
                    strategyDatas.add(strategyData);
                    continue;
                }

                faultResolver = new PendingSchemasResolver(strategyKind);
                strategyData.setN(Productor.faultCaseSize);

                Productor.enableSafe();
                FaultLocalization faultLocalizationSafe = new FaultLocalization(subject, faultResolver);
                minFaults = faultLocalizationSafe.localization();
                realMinFaults = faultResolver.getChecker().faults2minFaults();
                correctMFS = getCorrectMFS(minFaults, realMinFaults);
                strategyData.setPrecision(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
                strategyData.setRecall((double) correctMFS.size() / realMinFaults.size());
                strategyData.setAdditionalTC(faultLocalizationSafe.getAvgAdTC());

                Productor.disableSafe();
                FaultLocalization faultLocalizationNoSafe = new FaultLocalization(subject, faultResolver);
                minFaults = faultLocalizationNoSafe.localization();
                realMinFaults = faultResolver.getChecker().faults2minFaults();
                correctMFS = getCorrectMFS(minFaults, realMinFaults);
                strategyData.setNoSafePrecision(minFaults.size() == 0 ? null : (double) correctMFS.size() / minFaults.size());
                strategyData.setNoSafeRecall((double) correctMFS.size() / realMinFaults.size());
                strategyData.setNoSafeAdditionalTC(faultLocalizationNoSafe.getAvgAdTC());

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime1(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime2(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime3(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime4(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime5(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime6(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime7(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime8(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime9(end - start);

                start = System.currentTimeMillis();
                faultLocalizationNoSafe.localization();
                end = System.currentTimeMillis();
                execTimes.add(end - start);
                strategyData.setTime10(end - start);

                strategyData.setAvgTime(execTimes.stream().collect(Collectors.averagingLong(Long::longValue)));
                strategyDatas.add(strategyData);
            }
            WriteSheet writeSheet = EasyExcel.writerSheet(strategyKind.toString().replace("Strategy", "")).build();
            excelWriter.write(strategyDatas, writeSheet);
        }
        excelWriter.finish();
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

    private BigInteger avgPendingSize(Map<TestCase, BigInteger> tcMap) {
        BigInteger res = BigInteger.ZERO;
        for (Map.Entry<TestCase, BigInteger> entry : tcMap.entrySet()) {
            BigInteger v = entry.getValue();
            res = res.add(v);
        }
        return res.divide(BigInteger.valueOf(tcMap.size()));
    }


    @Test
    public void test() {
        System.out.println("Done!!");
    }
}
