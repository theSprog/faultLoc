package nju.gist.Common;

import org.raistlic.common.permutation.Combination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TestCase is an alias of ArrayList
 * one testcase represent an abstraction of realistic test cases
 */
public class TestCase extends ArrayList<Integer> {
    public TestCase() {
        super();
    }

    public TestCase(int size) {
        super(size);
        for (int i = 0; i < size; i++) {
            super.add(0);
        }
    }

    public TestCase(Comb comb) {
        super(comb);
    }

    public TestCase(List<Integer> gen) {
        super(gen);
    }

    /**
     * @param t: t-way
     * @return the t-way combination of this testcase
     * (1,2,3,4), t = 2 -> {(1, 2, -, -), (1, -, 3, -), ...}
     */
    public Set<Comb> tWayComb(int t) {
        Set<Comb> res = new HashSet<>();

        List<Integer> testCaseIndex = new ArrayList<>();
        for (int i = 0; i < this.size(); i++) {
            testCaseIndex.add(i);
        }
        Combination<Integer> combinationIndex = Combination.of(testCaseIndex, t);
        for (List<Integer> combIndex : combinationIndex) {
            res.add(new Comb(this, combIndex));
        }

        return res;
    }

    /**
     * @return the power set of this testcase, but exclude emptySet
     */
    public Set<Comb> powerSet() {
        Set<Comb> res = new HashSet<>();

        for (int i = 1; i <= this.size(); i++) {
            res.addAll(tWayComb(i));
        }

        return res;
    }

    @Override
    public TestCase clone() {
        return (TestCase) super.clone();
    }
}
