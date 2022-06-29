package nju.gist.FileResolver;

import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;

import java.io.*;
import java.util.*;

public class CSVResolver extends AbstractFileResolver{
    private static final String SEPARATOR = ",";

    private final List<TestCase> HealthCaseList;
    private final List<TestCase> FaultCaseList;
    private final Checker checker;

    public CSVResolver(String filePath, Checker checker){
        this.checker = checker;
        HealthCaseList = new ArrayList<>();
        FaultCaseList = new ArrayList<>();
        parseCSV(filePath);
    }

    private void parseCSV(String filePath) {
        File csv = new File(filePath);
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            while ((line = reader.readLine()) != null){
                if(line.matches(COMMENTS_LINE) || line.equals(BLANK_LINE)) continue;

                String[] confs = line.split(SEPARATOR);

                TestCase tc = new TestCase();
                for (String conf : confs) {
                    tc.add(Integer.parseInt(conf.strip()));
                }

                boolean pass = checker.executeTestCase(tc);
                if(!pass){
                    FaultCaseList.add(tc);
                }else {
                    HealthCaseList.add(tc);
                }
            }

            reader.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    @Override
    public List<TestCase> getFaultCaseList(){
        return FaultCaseList;
    }

    @Override
    public List<TestCase> getHealthCaseList() {
        return HealthCaseList;
    }
}
