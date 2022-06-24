package nju.gist.FaultResolver.SP;


import java.util.*;

public class SP {
    private static final int DUMMY_VALUE = 0;
    // degree denotes the t of t-way
    private final int degree;

    private List<List<Integer>> tpass;
    private List<List<Integer>> tfail;

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
        public List<Integer> suspComb;
        public double rhoC;
        public int Rc;
        public double rhoE;
        public int Re;
        public int R;   // equals to Rc + Re

        public piItem(List<Integer> suspComb, double rhoC, double rhoE) {
            this.suspComb = suspComb;
            this.rhoC = rhoC;
            this.rhoE = rhoE;
        }
    }

    public SP(int degree) {
        this.degree = degree;
    }

    public void setTpassAndTfail(List<List<Integer>> tpass, List<List<Integer>> tfail) {
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
            this.Values = new ArrayList<>();
            this.rhoList = new ArrayList<>();
            for (int i = 0; i < faultCaseSize; i++) {
                Values.add(new HashSet<>());
            }

            for (List<Integer> testCase : tfail) {
                for (int i = 0; i < testCase.size(); i++) {
                    Values.get(i).add(testCase.get(i));
                }
            }
            for (List<Integer> testCase : tpass) {
                for (int i = 0; i < testCase.size(); i++) {
                    Values.get(i).add(testCase.get(i));
                }
            }
        }else {
            this.rhoList.clear();
        }
    }

    /**
     * @return TwayComb(Tfail) - TwayComb(Tpass)
     */
    public Set<List<Integer>> getAllSuspiciousCombinations(){
        Set<List<Integer>> res = new HashSet<>();
        for (List<Integer> failCase : tfail) {
            Set<List<Integer>> combs = getTwayCombinations(degree, failCase);
            res.addAll(combs);
        }
        res.removeIf(this::containedInPass);
        this.piSize = res.size();
        return res;
    }

    /**
     * all combinations each contain <degree> factors from failCase
     * @param degree
     * @param failCase
     * @return
     */
    private Set<List<Integer>> getTwayCombinations(int degree, List<Integer> failCase) {
        Set<List<Integer>> res = new HashSet<>();
        int size = failCase.size();
        long s = (1L << degree) - 1;

        while (s < (1L << size)) {
            List<Integer> comb = new ArrayList<>(failCase);

            // we cannot change s in the loop, so we need temp
            long temp = s;
            for (int i = 0; i < size; i++) {
                if (temp % 2 == 0) comb.set(size-(i+1), DUMMY_VALUE);
                temp = temp >> 1;
            }
            res.add(comb);

            // don't ask me what this is, I don't know, but it works !!!
            long x = s & -s;
            long y = s + x;
            s = ((s & ~y) / x >> 1) | y;
        }
        return res;
    }

    private boolean containedInPass(List<Integer> comb) {
        for (List<Integer> passCase : tpass) {
            if(containedIn(comb, passCase)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param comb
     * @param passCase
     * @return true if comb contained in passCase, false otherwise
     */
    private boolean containedIn(List<Integer> comb, List<Integer> passCase) {
        int size = comb.size();
        for (int i = 0; i < size; i++) {
            if(comb.get(i) != DUMMY_VALUE && !comb.get(i).equals(passCase.get(i))){
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
    public List<List<Integer>> rank(Set<List<Integer>> pi) {
        ArrayList<List<Integer>> res = new ArrayList<>();
        // just return an empty list
        if(piSize == 0) return res;

        for (int i = 0; i < faultCaseSize; i++) {
            rhoList.add(new HashSet<>());
            for (Integer value : Values.get(i)) {
                rhoList.get(i).add(new pair(value, rho(i, value, pi)));
            }
        }

        List<piItem> itemTable = new ArrayList<>();
        for (List<Integer> suspComb : pi) {
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
            itemTable.get(i).R = itemTable.get(i).Re + itemTable.get(i).Rc;
        }

        itemTable.sort((o1, o2) -> (o1.R == o2.R) ? 0 : ((o1.R - o2.R) > 0 ? 1 : -1));
        for (piItem piItem : itemTable) {
            res.add(piItem.suspComb);
        }
        return res;
    }

    private double rhoE(List<Integer> suspComb, Set<List<Integer>> pi, List<List<Integer>> tfail, List<List<Integer>> tpass) {
        double rhoE = Double.MAX_VALUE;
        for (List<Integer> failCase : tfail) {
            if(containedIn(suspComb, failCase)){
                List<Integer> complementaryComb = getComplementaryComb(suspComb, failCase);
                double rhoC = rhoC(complementaryComb, pi);
                rhoE = Math.min(rhoE, rhoC);
            }
        }
        for (List<Integer> passCase : tpass) {
            if(containedIn(suspComb, passCase)){
                List<Integer> complementaryComb = getComplementaryComb(suspComb, passCase);
                double rhoC = rhoC(complementaryComb, pi);
                rhoE = Math.min(rhoE, rhoC);
            }
        }

        return rhoE;
    }

    private List<Integer> getComplementaryComb(List<Integer> suspComb, List<Integer> testCase) {
        List<Integer> res = new ArrayList<>();
        int size = suspComb.size();

        for (int i = 0; i < size; i++) {
            if(!suspComb.get(i).equals(DUMMY_VALUE)){
                res.add(DUMMY_VALUE);
            }else {
                res.add(testCase.get(i));
            }
        }
        return res;
    }

    private double rho(int componentIndex, Integer value, Set<List<Integer>> pi){
        int failContainComponentCount = 0;
        int passContainComponentCount = 0;
        int containComponentCount = 0;
        for (List<Integer> failCase : tfail) {
            if(failCase.get(componentIndex).equals(value)){
                failContainComponentCount++;
            }
        }
        for (List<Integer> passCase : tpass) {
            if(passCase.get(componentIndex).equals(value)){
                passContainComponentCount++;
            }
        }
        containComponentCount = failContainComponentCount + passContainComponentCount;

        int piContainComponentCount = 0;
        for (List<Integer> suspComb : pi) {
            if(suspComb.get(componentIndex).equals(value)){
                piContainComponentCount++;
            }
        }

        double u = (double) failContainComponentCount / failCaseSize;
        double v = (double) failContainComponentCount / containComponentCount;
        double w = (double) piContainComponentCount / piSize;

        return (1d/3d) * (u+v+w);
    }

    private double rhoC(List<Integer> comb, Set<List<Integer>> pi){
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


    public List<List<Integer>> genNewTestCases(List<List<Integer>> piRanked) {
        List<List<Integer>> res = new ArrayList<>();
        for (List<Integer> suspComb : piRanked) {
            List<Integer> tempComb = new ArrayList<>(suspComb);
            for (int i = 0; i < suspComb.size(); i++) {
                if(suspComb.get(i) == DUMMY_VALUE){
                    tempComb.set(i, valueOfMinRho(i));
                }
            }
            res.add(tempComb);
        }
        return res;
    }

    private Integer valueOfMinRho(int index) {
        Set<pair> pairs = rhoList.get(index);
        pair min = Collections.min(pairs, (o1, o2) -> (o1.rho == o2.rho) ? 0 : ((o1.rho - o2.rho) > 0 ? 1 : -1));
        return min.value;
    }
}
