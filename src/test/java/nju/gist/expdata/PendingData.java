package nju.gist.expdata;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
@Data
public class PendingData extends AbstractData{
    @ExcelProperty("index")
    private Integer index;

    @ColumnWidth(20)
    @ExcelProperty("TestCase")
    private String testCase;

    @ExcelProperty("FIC")
    private BigInteger FIC;

    @ExcelProperty("FICBS")
    private BigInteger FICBS;

    @ExcelProperty("AIFL")
    private BigInteger AIFL;

    @ExcelProperty("InverseCTD")
    private BigInteger InverseCTD;

    @ExcelProperty("RI")
    private BigInteger RI;

    @ExcelProperty("SOFOT")
    private BigInteger SOFOT;

    @ExcelProperty("LG1")
    private BigInteger LG1;

    @ExcelProperty("LG2")
    private BigInteger LG2;

    @ExcelProperty("ComFIL")
    private BigInteger ComFIL;

    @ExcelProperty("TRT")
    private BigInteger TRT;

    @ExcelProperty("SP")
    private BigInteger SP;

    @ExcelProperty("CMS")
    private BigInteger CMS;
}
