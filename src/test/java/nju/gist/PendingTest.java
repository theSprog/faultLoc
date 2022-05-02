package nju.gist;

import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasResolver;
import org.junit.Test;

import java.util.function.Consumer;

public class PendingTest {
    public static final String PATH = "src\\test\\resources\\nju\\gist\\";
    public String filePath = null;
    private FaultResolver faultResolver;

    private void execute(Consumer<FaultLocalization> flConsumer) {
        this.faultResolver = new PendingSchemasResolver();
        flConsumer.accept(new FaultLocalization(filePath, faultResolver));
    }

    public void testCSV(String fileName) {
        this.filePath = PATH + fileName;
        execute(FaultLocalization::getPendingSchemasSize);
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

    @Test
    public void test04() {
        testCSV("04.csv");
    }

    @Test
    public void test05() {
        testCSV("05.csv");
    }

    @Test
    public void test10() {
        testCSV("10.csv");
    }
}
