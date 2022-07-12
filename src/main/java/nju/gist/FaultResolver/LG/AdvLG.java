package nju.gist.FaultResolver.LG;

import nju.gist.Common.Comb;
import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;
import org.raistlic.common.permutation.Combination;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdvLG extends LG{
    private final Checker checker;
    private int caseSize;
    TestCase forRelabel;

    public class Edge{
        public int i;
        public boolean i_set;

        public int j;
        public boolean j_set;

        public Edge(int i, boolean i_set, int j, boolean j_set) {
            this.i = i;
            this.i_set = i_set;
            this.j = j;
            this.j_set = j_set;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) return false;
            if(this == obj) return true;
            if(!(obj instanceof Edge)) return false;
            Edge other = (Edge) obj;

            // (src equal src, dst equal dst) or (src equal dst, dst equal src)
            return (this.i == other.i && this.i_set == other.i_set && this.j == other.j && this.j_set == other.j_set)
                    || (this.i == other.j && this.i_set == other.j_set && this.j == other.i && this.j_set == other.i_set);
        }

        @Override
        public int hashCode() {
            return i * j;
        }
    }


    public AdvLG(Checker checker, List<TestCase> Tpass) {
        this.checker = checker;
        assert !Tpass.isEmpty();
        forRelabel = Tpass.get(0);
        caseSize = forRelabel.size();
        assert validFormat(Productor.ParaValues);
    }

    private boolean validFormat(List<Set<Integer>> paraValues) {
        for (Set<Integer> paraValue : paraValues) {
            if(paraValue.size() <= 2) {
                if (paraValue.stream().allMatch(val -> val.equals(1) || val.equals(2))) {
                    continue;
                }else {
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void locateELA(List<TestCase> Tfail, List<MinFault> minFaults) {
        assert !Tfail.isEmpty();
        Set<Edge> edges = locateError(Tfail);
        for (Edge edge : edges) {
            minFaults.add(edge2MinFault(edge));
        }
    }

    private MinFault edge2MinFault(Edge edge) {
        MinFault res = new MinFault(caseSize);
        if (edge.i_set) {
            res.set(edge.i, (forRelabel.get(edge.i) % 2) + 1);
        }else {
            res.set(edge.i, forRelabel.get(edge.i));
        }

        if (edge.j_set) {
            res.set(edge.j, (forRelabel.get(edge.j) % 2) + 1);
        }else {
            res.set(edge.j, forRelabel.get(edge.j));
        }
        return res;
    }

    private Schema tc2s(TestCase testCase) {
        Schema res = new Schema(testCase.size());
        for (int i = 0; i < testCase.size(); i++) {
            if(testCase.get(i).equals(forRelabel.get(i))){   // set relative safe to bool 0
                res.clear(i);
            }else {
                res.set(i);
            }
        }
        return res;
    }

    private TestCase s2tc(Schema schema) {
        TestCase res = new TestCase(schema.size());
        for (int i = 0; i < schema.size(); i++) {
            if(schema.get(i)){
                res.set(i, (forRelabel.get(i) % 2) + 1);
            }else {
                res.set(i, forRelabel.get(i));
            }
        }
        return res;
    }

    /**
     * 1−0 inside Ap,1−1 inside As,1−1 inside B,
     * 1−1 between As and B,
     * 1-0 between As and C0,
     * 1−1 between As and C1
     * @param Tfail
     * @return
     */
    private Set<Edge> locateError(List<TestCase> Tfail) {
        Set<Edge> resE = new HashSet<>();

        Set<Integer> A = new HashSet<>();
        for (TestCase fail : Tfail) {
            Schema faultPattern = tc2s(fail);
            A.addAll(SearchEndPoint(faultPattern));
        }
        Set<Integer> As = new HashSet<>(), Ap = new HashSet<>();
        Set<Edge> Ep = DiscoverEp(A, As, Ap);
        resE.addAll(Ep);    // 1−0 inside Ap done

        Schema fault_11_inB = new Schema(caseSize, true);
        for (Integer a : A) {
            fault_11_inB.clear(a);
        }
        // 1-1 inside B
        Set<Integer> B = locateErrorInTest(fault_11_inB, resE); // 1−1 inside B done

        // C = All - (A+B)
        Set<Integer> C = Stream.iterate(0, n -> n + 1)
                .limit(caseSize)
                .filter(i -> !(A.contains(i) || B.contains(i)))
                .collect(Collectors.toSet());

        Set<Integer> C0 = new HashSet<>();



        for (Integer f : As) {
            // Fix Tf = 1 and Ti = 0 for all i in (A \/ B)\{f}
            Schema T = new Schema(caseSize);
            T.set(f);   // Tf = 1
            List<Integer> Clist = new ArrayList<>(C);
            boolean found = false;
            for (int i = 1; i <= Math.min(2, Clist.size()) && !found; i++) {
                Combination<Integer> combinationIndex = Combination.of(Clist, i);
                for (List<Integer> combIndex : combinationIndex) {
                    for (Integer vertex : combIndex) {
                        T.set(vertex);
                    }
                    boolean pass = checker.executeTestCase(s2tc(T));
                    if(pass){
                        found = true;
                        break;
                    }else {
                        for (Integer vertex : combIndex) {
                            T.clear(vertex);
                        }
                    }
                }
            }

            // single factor fault
            if(!found) {
                resE.add(new Edge(f, true, f, true));
                continue;
            }

            // T is a pass testCase with a potential fault factor f
            Schema T1 = T.clone();
            for (Integer b : B) {
                T1.set(b);

                boolean pass = checker.executeTestCase(s2tc(T1));
                if(!pass){
                    resE.add(new Edge(f, true, b, true));   // 1 - 1 between As and B done
                }

                T1.clear(b);
            }

            // we don't use SearchEndPoint but iterating for convenience
            // C0 is 0 in C which would cause fault, C1 is 1 in C which would cause fault
            Schema T2 = T.clone();
            for (Integer c : C) {
                T2.flip(c);

                boolean pass = checker.executeTestCase(s2tc(T2));
                if(!pass){
                    // 1-0 inside As and C0 done
                    // 1-1 inside As and C1 done
                    resE.add(new Edge(f, true, c, T2.get(c)));

                    // record C0, we don't care C1
                    if(!T2.get(c)){
                        C0.add(c);
                    }
                }

                T2.flip(c);
            }
        }

        // 1-1 in As
        if(As.size() >= 2){
            Schema T = new Schema(caseSize);
            for (Integer c : C0) {
                T.set(c);       // avoid 1-0 inside As and C0
            }

            for (Integer i : As) {
                for (Integer j : As) {
                    if(i < j){
                        T.set(i);
                        T.set(j);

                        boolean pass = checker.executeTestCase(s2tc(T));
                        if(!pass){
                            resE.add(new Edge(i, true, j, true));   // 1-1 in As done
                        }

                        T.clear(j);
                        T.clear(i);
                    }
                }
            }
        }

        return resE;
    }

    private Set<Integer> locateErrorInTest(Schema faultPattern, Set<Edge> resE) {
        Set<Integer> res = new HashSet<>();
        Set<Integer> faultSet = Stream.iterate(0, n -> n + 1).limit(caseSize).filter(faultPattern::get).collect(Collectors.toSet());

        // loopRecord record the loop_fault
        Schema loopRecord = new Schema(caseSize);

        // T is a temp testCase(schema format), and we would use it repeatedly
        Schema T = new Schema(caseSize);
        for (Integer i : faultSet) {
            T.set(i);

            boolean pass = checker.executeTestCase(s2tc(T));

            // it's a loop_fault
            if(!pass){
                res.add(i);
                // record it!
                loopRecord.set(i);
                resE.add(new Edge(i, true, i, true));
            }

            T.clear(i);
        }

        for (Integer i : faultSet) {
            for (Integer j : faultSet) {
                if(i < j){
                    if(containLoop(loopRecord, i) || containLoop(loopRecord, j)) continue;
                    T.set(i);
                    T.set(j);

                    boolean pass = checker.executeTestCase(s2tc(T));
                    if(!pass){
                        res.add(i);
                        resE.add(new Edge(i, true, j, true));
                    }

                    T.clear(j);
                    T.clear(i);
                }
            }
        }
        return res;
    }

    private boolean containLoop(Schema loopRecord, Integer i) {
        return loopRecord.get(i);
    }

    private Set<Edge> DiscoverEp(Set<Integer> A, Set<Integer> As, Set<Integer> Ap) {
        Set<Edge> res = new HashSet<>();
        Schema schema = new Schema(caseSize);

        for (Integer i : A) {
            for (Integer j : A) {
                if(!i.equals(j)){
                    schema.set(i);
                    schema.set(j);
                    boolean pass = checker.executeTestCase(s2tc(schema));
                    if(pass){
                        Ap.add(i);Ap.add(j);
                        res.add(new Edge(i, true, j, false));
                        res.add(new Edge(i, false, j, true));
                    }
                    schema.clear();
                }
            }
        }

        // As = A - Ap;
        As.addAll(A);
        As.removeAll(Ap);
        return res;
    }

    private Set<Integer> SearchEndPoint(Schema faultPattern) {
        Set<Integer> res = new HashSet<>();
        Set<Integer> V1, V2;
        boolean pass;

        if(faultPattern.cardinality() == 1){
            res.add(faultPattern.nextSetBit(0));
            return res;
        }

        Parts parts = new Parts(faultPattern);
        pass = checker.executeTestCase(s2tc(parts.left));
        if(!pass){
            V1 = SearchEndPoint(parts.left);
            if(!V1.isEmpty()){
                res.addAll(V1);
            }
        }

        pass = checker.executeTestCase(s2tc(parts.right));
        if(!pass){
            V2 = SearchEndPoint(parts.right);
            if(!V2.isEmpty()) {
                res.addAll(V2);
            }
        }

        return res;
    }
}
