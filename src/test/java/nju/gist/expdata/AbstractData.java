package nju.gist.expdata;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import nju.gist.SubjectExp;

@Data
public class AbstractData {
    @ColumnWidth(10)
    @ExcelProperty(value = "project", index = 0)
    private String project;

    @ColumnWidth(30)
    @ExcelProperty(value = "path", index = 1)
    private String path;

    @ExcelProperty(value = "N", index = 2)
    private Integer n;

    public void fillProjectAndPath(String subject){
        String[] project_path = subject.split(SubjectExp.PATH)[1].split("/", 2);
        String project = project_path[0];
        String path = project_path[1].replaceAll("[.]csv", "");
        setProject(project);
        setPath(path);
    }
}
