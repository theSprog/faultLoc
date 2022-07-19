package nju.gist.expdata;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nju.gist.Common.TestCase;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdditionalTCData extends AbstractData{
    @ExcelProperty(value = "index", index = 3)
    private Integer index;

    @ColumnWidth(20)
    @ExcelProperty("TestCase")
    private String testCase;

    @ExcelProperty("FIC")
    private Integer FIC;

    @ExcelProperty("FICBS")
    private Integer FICBS;

    @ExcelProperty("AIFL")
    private Integer AIFL;

    @ExcelProperty("InverseCTD")
    private Integer InverseCTD;

    @ExcelProperty("RI")
    private Integer RI;

    @ExcelProperty("SOFOT")
    private Integer SOFOT;

    @ExcelProperty("LG1")
    private Integer LG1;

    @ExcelProperty("LG2")
    private Integer LG2;

    @ExcelProperty("ComFIL")
    private Integer ComFIL;

    @ExcelProperty("TRT")
    private Integer TRT;

    @ExcelProperty("SP")
    private Integer SP;

    @ExcelProperty("CMS")
    private Integer CMS;
}
