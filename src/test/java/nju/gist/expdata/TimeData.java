package nju.gist.expdata;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TimeData extends AbstractData{
    @ExcelProperty("FIC")
    private Long FIC;

    @ExcelProperty("FICBS")
    private Long FICBS;

    @ExcelProperty("AIFL")
    private Long AIFL;

    @ExcelProperty("InverseCTD")
    private Long InverseCTD;

    @ExcelProperty("RI")
    private Long RI;

    @ExcelProperty("SOFOT")
    private Long SOFOT;

    @ExcelProperty("LG1")
    private Long LG1;

    @ExcelProperty("LG2")
    private Long LG2;

    @ExcelProperty("ComFIL")
    private Long ComFIL;

    @ExcelProperty("TRT")
    private Long TRT;

    @ExcelProperty("SP")
    private Long SP;

    @ExcelProperty("CMS")
    private Long CMS;
}
