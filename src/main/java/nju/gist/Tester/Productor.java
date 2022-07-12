package nju.gist.Tester;

import nju.gist.Common.Comb;
import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Productor {
    // RIGHT is default safe value
    private static final int RIGHT = 1;
    public static final int UNKNOWN = 0;

    public static int faultCaseSize = 0;
    // The set of values that each parameter can take
    // i.e. Values[0] = {2, 3} means that the 0th parameter can take 2 and 3.
    public static List<Set<Integer>> ParaValues;

    private static boolean has_safe = false;
    private static boolean safe_enabled = false;
    public static boolean TWO_VAL_EXP = true;
    private static List<Integer> Safes;

    public static void SetParaValues(List<Set<Integer>> Values){
        Productor.ParaValues = Values;
        Productor.faultCaseSize = Values.size();
    }

    public static void SetSafes(List<Integer> safes){
        has_safe = true;
        Productor.Safes = safes;
        assert Safes.size() == ParaValues.size();
    }

    public static void clearSafes() {
        has_safe = false;
        Productor.Safes = null;
    }

    public static TestCase genTestCase(Schema node, TestCase faultCase) {
        return new TestCase(gen(node, faultCase, RIGHT));
    }

    public static TestCase genTestCase(int node, TestCase faultCase) {
        return new TestCase(gen(node, faultCase, RIGHT));
    }

    public static TestCase genTestCase(Comb comb) {
        return new TestCase(gen(toSchema(comb), new TestCase(comb), RIGHT));
    }

    public static MinFault genMinFault(Schema node, TestCase faultCase) {
        return new MinFault(gen(node, faultCase, UNKNOWN));
    }

    public static MinFault genMinFault(int node, TestCase faultCase) {
        return new MinFault(gen(node, faultCase, UNKNOWN));
    }

    private static ArrayList<Integer> gen(Integer node, TestCase faultCase, int provided){
        return gen(toSchema(node), faultCase, provided);
    }

    // generate TestCase from schema pattern
    private static ArrayList<Integer> gen(Schema node, TestCase faultCase, int provided){
        TestCase productRes = new TestCase(faultCaseSize);

        for (int i = 0; i < faultCaseSize; i++) {
            if(node.get(i)){
                productRes.set(i, faultCase.get(i));
            }else {
                if(has_safe && safe_enabled && provided != UNKNOWN) {
                    // if user provided safe values, use it
                    productRes.set(i, Safes.get(i));
                }else {
                    // else set to default safe values
                    if(provided == faultCase.get(i)){
                        productRes.set(i, selectAnother(faultCase, i));
                    }else {
                        productRes.set(i, provided);
                    }
                }
            }
        }
        return productRes;
    }

    private static Integer selectAnother(TestCase faultCase, int i) {
        // if we know the system be binary option
        if(TWO_VAL_EXP){
            return faultCase.get(i) % 2 + 1;
        }
        Set<Integer> vals = ParaValues.get(i);
        for (Integer val : vals) {
            if(!faultCase.get(i).equals(val)){
                return val;
            }
        }
        // if we arrive here, it must be an error, it must be fixed
        assert false;
        return RIGHT;
    }

    public static Schema toSchema(Integer node) {
        Schema res = new Schema(32 - Integer.numberOfLeadingZeros(node));
        while (node != 0){
            int lowestOneBitIndex = Integer.numberOfTrailingZeros(node);
            res.set(lowestOneBitIndex);
            node = node ^ (1 << lowestOneBitIndex);
        }
        return res;
    }

    /**
     * comb to schema, 0 set to bool 0, non-0 set to bool 1
     * (0, 2, 2, 1) -> 0111
     * @param comb
     * @return
     */
    public static Schema toSchema(Comb comb) {
        Schema res = new Schema(comb.size());
        for (int i = 0; i < comb.size(); i++) {
            // if comb[i] is 0, Schema bit is also set to 0, in other words, use default 0
            if(comb.get(i).equals(0)) continue;
            // else set to 1
            res.set(i);
        }
        return res;
    }

    public static void disableSafe() {
        safe_enabled = false;
    }

    public static void enableSafe() {
        safe_enabled = true;
    }
}
