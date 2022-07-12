package nju.gist.FileResolver;

import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * parse safe value file for test suite
 */
public class SafeResolver extends AbstractFileResolver{
    private static final String SEPARATOR = ",";
    private boolean hasSafes;
    private File safeFile;
    private final Checker checker;
    TestCase safeTC;

    public SafeResolver(String path, Checker checker) {
        safeFile = new File(path);
        this.checker = checker;
        if (!safeFile.exists()){
            hasSafes = false;
            logger.warn("safe file do not exist, it might influence the result of localization!!");
        }else {
            hasSafes = true;
        }
    }

    public List<Integer> getSafes() {
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(safeFile))) {
            while ((line = reader.readLine()) != null) {
                if (line.matches(COMMENTS_LINE) || line.equals(BLANK_LINE)) continue;

                String[] safeValues = line.split(SEPARATOR);

                // safe test case, which must be passed testcase
                safeTC = new TestCase();
                for (String safeValue : safeValues) {
                    safeTC.add(Integer.parseInt(safeValue.strip()));
                }

                assert checker.executeTestCase(safeTC);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

        return safeTC;
    }

    public boolean hasSafes() {
        return hasSafes;
    }
}
