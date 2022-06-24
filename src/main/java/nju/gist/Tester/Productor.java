package nju.gist.Tester;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.LG.AdvLG;

import java.util.Arrays;
import java.util.List;

public class Productor {
    public static final int RIGHT = 1;
    public static final int UNKNOWN = 0;

    public static int faultCaseSize = 0;

    public static void setFaultCaseSize(int size){
        faultCaseSize = size;
    }

    public static List<Integer> genTestCase(Schema node, List<Integer> faultCase) {
        return gen(node, faultCase, RIGHT);
    }

    public static List<Integer> genTestCase(int node, List<Integer> faultCase) {
        return gen(node, faultCase, RIGHT);
    }

    public static List<Integer> genMinFault(Schema node, List<Integer> faultCase) {
        return gen(node, faultCase, UNKNOWN);
    }

    public static List<Integer> genSchema(Schema node, List<Integer> testCase) {
        return gen(node, testCase, UNKNOWN);
    }

    public static List<Integer> genMinFault(int node, List<Integer> faultCase) {
        return gen(node, faultCase, UNKNOWN);
    }

    private static List<Integer> gen(Integer node, List<Integer> faultCase, int provided){
        return gen(toSchema(node), faultCase, provided);
    }

    // node to list
    private static List<Integer> gen(Schema node, List<Integer> faultCase, int provided){
        List<Integer> productRes = Arrays.asList(new Integer[faultCaseSize]);
        int bitLen = faultCase.size();

        for (int i = 0; i < bitLen; i++) {
            if(node.get(i)){
                productRes.set(i, faultCase.get(i));
            }else {
                productRes.set(i, provided);
            }
        }
        return productRes;
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

    // specify for advLG
    public static List<Integer> genMinFaultByEdge(int caseSize, AdvLG.Edge edge) {
        List<Integer> productRes = Arrays.asList(new Integer[caseSize]);
        for (int i = 0; i < caseSize; i++) {
            productRes.set(i, UNKNOWN);
        }

        productRes.set(edge.i, edge.i_set ? 2 : 1);
        productRes.set(edge.j, edge.j_set ? 2 : 1);

        return productRes;
    }
}
