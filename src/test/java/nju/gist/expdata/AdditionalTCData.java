package nju.gist.expdata;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdditionalTCData extends AbstractData{
    @ExcelProperty("FIC")
    private Double FIC;

    @ExcelProperty("FICBS")
    private Double FICBS;

    @ExcelProperty("AIFL")
    private Double AIFL;

    @ExcelProperty("InverseCTD")
    private Double InverseCTD;

    @ExcelProperty("RI")
    private Double RI;

    @ExcelProperty("SOFOT")
    private Double SOFOT;

    @ExcelProperty("LG1")
    private Double LG1;

    @ExcelProperty("LG2")
    private Double LG2;

    @ExcelProperty("ComFIL")
    private Double ComFIL;

    @ExcelProperty("TRT")
    private Double TRT;

    @ExcelProperty("SP")
    private Double SP;

    @ExcelProperty("CMS")
    private Double CMS;
}
