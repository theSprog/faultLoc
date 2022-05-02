package nju.gist.FileResolver;

import nju.gist.Tester.Checker;

import java.io.*;
import java.util.*;

public class CSVResolver extends AbstractFileResolver{
    private static final String SEPARATOR = ",";

    private final List<List<Integer>> HealthCaseList;
    private final List<List<Integer>> FaultCaseList;
    private final Checker checker;

    public CSVResolver(String filePath, Checker checker){
        this.checker = checker;
        HealthCaseList = new LinkedList<>();
        FaultCaseList = new LinkedList<>();
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

                List<Integer> testCase = new ArrayList<>();
                for (String conf : confs) {
                    testCase.add(Integer.parseInt(conf.strip()));
                }

                boolean pass = checker.executeTestCase(testCase);
                if(!pass){
                    FaultCaseList.add(testCase);
                }else {
                    HealthCaseList.add(testCase);
                }
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    @Override
    public List<List<Integer>> getFaultCaseList(){
        return FaultCaseList;
    }

    @Override
    public List<List<Integer>> getHealthCaseList() {
        return HealthCaseList;
    }
}
