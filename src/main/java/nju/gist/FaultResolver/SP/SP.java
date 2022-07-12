package nju.gist.FaultResolver.SP;


import nju.gist.Common.Comb;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Productor;
import org.raistlic.common.permutation.Combination;

import java.util.*;

public class SP {
    /*
    A Combination is also a TestCase, but the difference is
    all combinations each contain "degree" factors from TestCase,
    and other positions is replaced placeholder "DUMMY_VALUE"
    i.e. (1,2,3,4) is a TestCase, and (-, 3, 2, 4) is a Combination
     */
    private static final int DUMMY_VALUE = Productor.UNKNOWN;
    // degree denotes the t of t-way
    private final int degree;

    private List<TestCase> tpass;
    private List<TestCase> tfail;

    // the number of failCase among testSet
    private int failCaseSize;

    // the size of one fault Case
    private int faultCaseSize;

    private int piSize;

    // The set of values that can be taken by each parameter
    // i.e. Values[0] = {2, 3} represents that the 0th parameter can take 3 and 2
    private List<Set<Integer>> Values;

    private class pair {
        int value;
        double rho;

        public pair(Integer value, double rho) {
            this.value = value;
            this.rho = rho;
        }
    }

    // rhoList[0] represents rho(a), rhoList[1] represents rho(b), ...
    // rhoList[0]'s value and rho represent rho(a <- value) = rho
    private List<Set<pair>> rhoList;

    private class piItem {
        public Comb suspComb;
        public double rhoC;
        public int Rc;
        public double rhoE;
        public int Re;
        public int R;   // equals to Rc + Re

        public piItem(Comb suspComb, double rhoC, double rhoE) {
            this.suspComb = suspComb;
            this.rhoC = rhoC;
            this.rhoE = rhoE;
        }
    }

    public SP(int degree) {
        this.degree = degree;
    }

    public void setTpassAndTfail(List<TestCase> tpass, List<TestCase> tfail) {
        this.tpass = tpass;
        this.tfail = tfail;
        this.failCaseSize = tfail.size();
        this.piSize = 0;
        this.faultCaseSize = tfail.get(0).size();

        if(faultCaseSize < degree) {
            throw new RuntimeException("the degree cannot be greater than the size of faultCase");
        }

        // if the method is executed for the first time
        if(this.Values == null) {
            this.Values = Productor.ParaValues;
            this.rhoList = new ArrayList<>();
        }else {
            this.rhoList.clear();
        }
    }

    /**
     * @return TwayComb(Tfail) - TwayComb(Tpass)
     */
    public Set<Comb> getAllSuspiciousCombinations(){
        Set<Comb> res = new HashSet<>();
        for (TestCase failCase : tfail) {
            res.addAll(failCase.tWayComb(degree));
        }
        res.removeIf(this::containedInPass);
        this.piSize = res.size();
        return res;
    }

    private boolean containedInPass(Comb comb) {
        for (TestCase passCase : tpass) {
            if(containedIn(comb, passCase)){
                return true;
            }
        }
        return false;
    }

    /**
     *(1,2,3,4) contain (-, 2, 3, -)
     * @param comb
     * @param testCase
     * @return true if comb is contained in passed test case(namely parameter testCase), false otherwise
     */
    private boolean containedIn(Comb comb, TestCase testCase) {
        assert comb.size() == testCase.size();
        int size = comb.size();
        for (int i = 0; i < size; i++) {
            if(comb.get(i) != DUMMY_VALUE && !comb.get(i).equals(testCase.get(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * produce a ranking R of all the t-way combinations
     * @param pi
     * @return
     */
    public List<Comb> rank(Set<Comb> pi) {
        List<Comb> res = new ArrayList<>();
        // just return an empty list
        if(piSize == 0) return res;

        for (int i = 0; i < faultCaseSize; i++) {
            rhoList.add(new HashSet<>());
            for (Integer value : Values.get(i)) {
                rhoList.get(i).add(new pair(value, rho(i, value, pi)));
            }
        }

        List<piItem> itemTable = new ArrayList<>();
        for (Comb suspComb : pi) {
            double rhoC = rhoC(suspComb, pi);
            double rhoE = rhoE(suspComb, pi, tfail, tpass);
            itemTable.add(new piItem(suspComb, rhoC, rhoE));
        }

        itemTable.sort((o1, o2) -> (o1.rhoC == o2.rhoC) ? 0 : ((o1.rhoC - o2.rhoC) > 0 ? -1 : 1));
        for (int i = 0; i < itemTable.size(); i++) {
            if(i == 0) {
                // we rank the first item as number one
                itemTable.get(i).Rc = 1;
            }else {
                if(itemTable.get(i).rhoC == itemTable.get(i-1).rhoC){
                    itemTable.get(i).Rc = itemTable.get(i-1).Rc;
                }else {
                    itemTable.get(i).Rc = itemTable.get(i-1).Rc + 1;
                }
            }
        }

        itemTable.sort((o1, o2) -> (o1.rhoE == o2.rhoE) ? 0 : ((o1.rhoE - o2.rhoE) > 0 ? 1 : -1));
        for (int i = 0; i < itemTable.size(); i++) {
            if(i == 0) {
                itemTable.get(i).Re = 1;
            }else {
                if(itemTable.get(i).rhoE == itemTable.get(i-1).rhoE){
                    itemTable.get(i).Re = itemTable.get(i-1).Re;
                }else {
                    itemTable.get(i).Re = itemTable.get(i-1).Re + 1;
                }
            }
            // R is Re + Rc, it has been able to represent the sequential relationship
            itemTable.get(i).R = itemTable.get(i).Re + itemTable.get(i).Rc;
        }

        itemTable.sort((o1, o2) -> (o1.R == o2.R) ? 0 : ((o1.R - o2.R) > 0 ? 1 : -1));
        for (piItem piItem : itemTable) {
            res.add(piItem.suspComb);
        }
        return res;
    }

    private double rhoE(Comb suspComb, Set<Comb> pi, List<TestCase> tfail, List<TestCase> tpass) {
        double rhoE = Double.MAX_VALUE;
        for (TestCase failCase : tfail) {
            if(containedIn(suspComb, failCase)){
                Comb complementaryComb = getComplementaryComb(suspComb, failCase);
                double rhoC = rhoC(complementaryComb, pi);
                rhoE = Math.min(rhoE, rhoC);
            }
        }
        for (TestCase passCase : tpass) {
            if(containedIn(suspComb, passCase)){
                Comb complementaryComb = getComplementaryComb(suspComb, passCase);
                double rhoC = rhoC(complementaryComb, pi);
                rhoE = Math.min(rhoE, rhoC);
            }
        }

        return rhoE;
    }

    /**
     * (1, 2, 3, 4) is testCase and (-, 2, 3, -) is suspComb
     * so this function would return (1, -, -, 4)
     * @param suspComb
     * @param testCase
     * @return
     */
    private Comb getComplementaryComb(Comb suspComb, TestCase testCase) {
        assert containedIn(suspComb, testCase);

        int size = suspComb.size();
        Comb res = new Comb(size);

        for (int i = 0; i < size; i++) {
            // suspComb[i] != DUMMY_VALUE
            if(!suspComb.get(i).equals(DUMMY_VALUE)){
                res.set(i, DUMMY_VALUE);
            }else {
                res.set(i, testCase.get(i));
            }
        }
        return res;
    }

    private double rho(int componentIndex, Integer value, Set<Comb> pi){
        int failContainComponentCount = 0;
        int passContainComponentCount = 0;
        int containComponentCount = 0;
        for (TestCase failCase : tfail) {
            if(failCase.get(componentIndex).equals(value)){
                failContainComponentCount++;
            }
        }
        for (TestCase passCase : tpass) {
            if(passCase.get(componentIndex).equals(value)){
                passContainComponentCount++;
            }
        }
        containComponentCount = failContainComponentCount + passContainComponentCount;

        int piContainComponentCount = 0;
        for (Comb suspComb : pi) {
            if(suspComb.get(componentIndex).equals(value)){
                piContainComponentCount++;
            }
        }

        double u = (double) failContainComponentCount / failCaseSize;
        double v = (double) failContainComponentCount / containComponentCount;
        double w = (double) piContainComponentCount / piSize;

        return (1d/3d) * (u+v+w);
    }

    private double rhoC(Comb comb, Set<Comb> pi){
        double rhoCOfComb = 0;
        for (int i = 0; i < comb.size(); i++) {
            if(comb.get(i) != DUMMY_VALUE){
                for (pair pair : rhoList.get(i)) {
                    if(pair.value == comb.get(i)){
                        rhoCOfComb += pair.rho;
                        break;
                    }
                }
            }
        }
        return rhoCOfComb/degree;
    }


    public List<TestCase> genNewTestCases(List<Comb> piRanked) {
        List<TestCase> res = new ArrayList<>();
        for (Comb suspComb : piRanked) {
            Comb tempComb = suspComb.clone();
            for (int i = 0; i < suspComb.size(); i++) {
                if(suspComb.get(i) == DUMMY_VALUE){
                    tempComb.set(i, valueOfMinRho(i));
                }
            }
            res.add(new TestCase(tempComb));
        }
        return res;
    }

    private Integer valueOfMinRho(int index) {
        Set<pair> pairs = rhoList.get(index);
        pair min = Collections.min(pairs, (o1, o2) -> (o1.rho == o2.rho) ? 0 : ((o1.rho - o2.rho) > 0 ? 1 : -1));
        return min.value;
    }

    /**
     *
     * @param suspiciousCombination
     * @param d
     * @return smaller combinations(d is degree of combinations)
     */
    public Set<Comb> smallerCombs(Comb suspiciousCombination, int d) {
        Set<Comb> res = new HashSet<>();
        List<Integer> testCaseIndex = new ArrayList<>();
        for (int i = 0; i < suspiciousCombination.size(); i++) {
            if(suspiciousCombination.get(i) != 0) {
                testCaseIndex.add(i);
            }
        }
        Combination<Integer> combinationIndex = Combination.of(testCaseIndex, d);
        for (List<Integer> combIndex : combinationIndex) {
            res.add(new Comb(new TestCase(suspiciousCombination), combIndex));
        }
        return res;
    }
}
