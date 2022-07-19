package nju.gist;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.FIC.FICResolver;
import nju.gist.FaultResolver.FaultResolver;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import nju.gist.FaultLocalization.TCInfo;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class PendingTest {
    public static final String PATH = "src\\test\\resources\\nju\\gist\\";
    public String filePath = null;
    private FaultResolver faultResolver;

    public void testCSV(String fileName) {
        this.filePath = PATH + fileName;
        this.faultResolver = new FICResolver();
        List<TCInfo> pendingSchemasSize = new FaultLocalization(filePath, faultResolver).getPendingSchemasSize();
        System.out.println(pendingSchemasSize);
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
