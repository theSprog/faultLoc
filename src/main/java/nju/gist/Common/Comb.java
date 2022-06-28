package nju.gist.Common;

import nju.gist.Tester.Productor;
import java.util.ArrayList;
import java.util.List;

/**
 * Comb is Combination of a testcase
 * i.e. testcase = (1, 2, 3, 4)
 * comb might be (-, 2, -, 4)
 */
public class Comb extends ArrayList<Integer> {
    public Comb(int size) {
        super(size);
        for (int i = 0; i < size; i++) {
            super.add(Productor.UNKNOWN);
        }
    }

    // list to comb
    public Comb(List<Integer> comb) {
        super(comb);
    }

    // clone from testcase
    public Comb(TestCase testCase) {
        super(testCase);
    }

    /*
     clone from part of testcase
     i.e. testcase = (1, 2, 3, 4), index = [1, 2] => (-, 2, 3, -)
     */
    public Comb(List<Integer> testCase, List<Integer> combIndex) {
        this(testCase.size());
        for (Integer index : combIndex) {
            super.set(index, testCase.get(index));
        }
    }

    @Override
    public Comb clone() {
        return (Comb)super.clone();
    }
}
