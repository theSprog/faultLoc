package nju.gist;

import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.FaultResolver;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasResolver;
import org.junit.Test;
import org.raistlic.common.permutation.Combination;
import org.raistlic.common.permutation.Permutation;
import java.math.BigInteger;
import java.util.List;


public class SubjectExp {
    private static final String PATH = "src\\test\\resources\\nju\\gist\\external";
    private String filePath;
    private FaultResolver faultResolver;

    @Test
    public void testPendingSize() {
        String subjectsFilesPath = PATH + "\\" + "subjects.txt";
        List<String> subjects = parseFilesPath(subjectsFilesPath);
    }

    private List<String> parseFilesPath(String subjectsFilesPath) {
        return null;
    }

    @Test
    public void test() {
        TestCase tc = new TestCase(5);
        for (int i = 0; i < 5; i++) {
            tc.set(i, i);
        }
        System.out.println(tc);

        TestCase tc2 = tc.clone();
        for (int i = 0; i < 5; i++) {
            tc2.set(i, i-1);
        }
        System.out.println(tc2);
    }
}
