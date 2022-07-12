package nju.gist;

import nju.gist.FaultResolver.AIFL.AIFLResolver;
import nju.gist.FaultResolver.CTA.CTAResolver;
import nju.gist.FaultResolver.ComFIL.ComFILResolver;
import nju.gist.FaultResolver.FIC.FICBSResolver;
import nju.gist.FaultResolver.FIC.FICResolver;
import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.InverseCTD.InverseCTDResolver;
import nju.gist.FaultResolver.LG.LGKind;
import nju.gist.FaultResolver.LG.LGResolver;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasResolver;
import nju.gist.FaultResolver.RI.RI;
import nju.gist.FaultResolver.RI.RIResolver;
import nju.gist.FaultResolver.SOFOT.SOFOTResolver;
import nju.gist.FaultResolver.SP.SPResolver;
import org.junit.Test;


/**
 * Unit test for FaultLocalization.
 */
public class FaultLocalizationTest {
    private static final String PATH = "src\\test\\resources\\nju\\gist\\";
    private String filePath;
    private FaultResolver faultResolver;

    // common method
    public void testCSV(String fileName) {
        this.filePath = PATH + fileName;
        this.faultResolver = new FICBSResolver();
        new FaultLocalization(filePath, faultResolver).localization();
    }

    // only for ml method
    private void testML(String fileName) {
        this.filePath = PATH + fileName;
        try {
            this.faultResolver = new CTAResolver(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert faultResolver != null;
        new MLFaultLoc(filePath, faultResolver).localization();
    }

    // only for sp method
    private void testSP(String fileName) {
        this.filePath = PATH + fileName;
        this.faultResolver = new SPResolver(2);
        new FaultLocalization(filePath, faultResolver).localizationByCA();
    }

    // only for AdvLG method
    private void testAdvLG(String fileName) {
        this.filePath = PATH + fileName;
        this.faultResolver = new LGResolver(LGKind.AdvLG);
        new FaultLocalization(filePath, faultResolver).localizationByCA();
    }

    @Test
    public void test01() {
        testCSV("01.csv");
    }

    @Test
    public void test02() {
        testCSV("02.csv");
    }

    @Test
    public void test03() {
        testCSV("03.csv");
    }

    // 如果错误在顶端
    @Test
    public void test04() {
        testCSV("04.csv");
    }

    // 综合测试
    @Test
    public void test05() {
        testCSV("05.csv");
    }

    // 多个独立错误
    @Test
    public void test06() {
        testCSV("06.csv");
    }

    // N 过大时的表现
    @Test
    public void test10() {
        testCSV("10.csv");
    }


    // 机器学习方法
    @Test
    public void testML01() {
        testML("weka01.csv");
    }

    @Test
    public void testML02() {
        testML("weka02.csv");
    }

    @Test
    public void testML03() {
        testML("weka03.csv");
    }

    // sp 统计方法
    @Test
    public void testSP01() {
        testSP("sp01.csv");
    }

    // advLG method, g must be 2
    @Test
    public void testAdvLG01() {
        testAdvLG("advLG01.csv");
    }

    @Test
    public void testAdvLG02() {
        testAdvLG("advLG02.csv");
    }

}
