package nju.gist;

import nju.gist.FaultResolver.CTA.CTAResolver;
import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.LG.LGKind;
import nju.gist.FaultResolver.LG.LGResolver;
import nju.gist.FaultResolver.SP.SPResolver;
import org.junit.Test;

import java.util.function.Consumer;

/**
 * Unit test for FaultLocalization.
 */
public class FaultLocalizationTest {
    private static final String PATH = "src\\test\\resources\\nju\\gist\\";
    private String filePath;
    private FaultResolver faultResolver;

    // common method
    private void execute(Consumer<FaultLocalization> flConsumer){
        this.faultResolver = new LGResolver(LGKind.SafeValueLG);
        flConsumer.accept(new FaultLocalization(filePath, faultResolver));
    }

    public void testCSV(String fileName){
        this.filePath =PATH + fileName;
        execute(FaultLocalization::localization);
    }

    // ml method
    private void executeML(Consumer<MLFaultLoc> flConsumer){
        try {
            this.faultResolver = new CTAResolver(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert faultResolver != null;
        flConsumer.accept(new MLFaultLoc(filePath, faultResolver));
    }

    private void testML(String fileName) {
        this.filePath =PATH + fileName;
        executeML(MLFaultLoc::localization);
    }

    // sp method
    private void executeSP(Consumer<FaultLocalization> flConsumer){
        this.faultResolver = new SPResolver(1);
        flConsumer.accept(new FaultLocalization(filePath, faultResolver));
    }

    private void testSP(String fileName) {
        this.filePath =PATH + fileName;
        executeSP(FaultLocalization::localizationByCA);
    }

    // advLG method
    private void executeAdvLG(Consumer<FaultLocalization> flConsumer){
        this.faultResolver = new LGResolver(LGKind.AdvLG);
        flConsumer.accept(new FaultLocalization(filePath, faultResolver));
    }

    private void testAdvLG(String fileName) {
        this.filePath =PATH + fileName;
        executeAdvLG(FaultLocalization::localizationByCA);
    }

    @Test
    public void test01(){
        testCSV("01.csv");
    }

    @Test
    public void test02(){
        testCSV("02.csv");
    }

    @Test
    public void test03(){
        testCSV("03.csv");
    }

    @Test
    public void test04(){
        testCSV("04.csv");
    }

    @Test
    public void test05(){
        testCSV("05.csv");
    }

    @Test
    public void test06() {
        testCSV("06.csv");
    }

    @Test
    public void test10(){
        testCSV("10.csv");
    }


    // 机器学习方法
    @Test
    public void testML01(){
        testML("weka01.csv");
    }

    @Test
    public void testML02(){
        testML("weka02.csv");
    }

    @Test
    public void testML03(){
        testML("weka03.csv");
    }

    // sp 统计方法
    @Test
    public void testSP01(){
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
