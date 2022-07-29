package nju.gist.expdata;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StrategyData extends AbstractData{
    @ColumnWidth(15)
    @ExcelProperty(value = "Precision")
    Double Precision;

    @ExcelProperty(value = "Recall")
    Double Recall;

    @ColumnWidth(20)
    @ExcelProperty(value = "ADTC")
    Double AdditionalTC;

    @ColumnWidth(20)
    @ExcelProperty(value = "NoSafePrecision")
    Double NoSafePrecision;

    @ColumnWidth(20)
    @ExcelProperty(value = "NoSafeRecall")
    Double NoSafeRecall;

    @ColumnWidth(20)
    @ExcelProperty(value = "NoSafeADTC")
    Double NoSafeAdditionalTC;

    @ExcelProperty(value = "Time1")
    Long Time1;

    @ExcelProperty(value = "Time2")
    Long Time2;

    @ExcelProperty(value = "Time3")
    Long Time3;

    @ExcelProperty(value = "Time4")
    Long Time4;

    @ExcelProperty(value = "Time5")
    Long Time5;

    @ExcelProperty(value = "Time6")
    Long Time6;

    @ExcelProperty(value = "Time7")
    Long Time7;

    @ExcelProperty(value = "Time8")
    Long Time8;

    @ExcelProperty(value = "Time9")
    Long Time9;

    @ExcelProperty(value = "Time10")
    Long Time10;

    @ExcelProperty(value = "AvgTime")
    Double AvgTime;
}
