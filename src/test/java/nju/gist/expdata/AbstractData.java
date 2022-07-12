package nju.gist.expdata;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class AbstractData {
    @ColumnWidth(20)
    @ExcelProperty(value = "project", index = 0)
    private String project;

    @ColumnWidth(40)
    @ExcelProperty(value = "path", index = 1)
    private String path;

    @ExcelProperty(value = "N", index = 2)
    private Integer n;
}
