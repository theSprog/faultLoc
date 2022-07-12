package nju.gist.FaultResolver.PendingSchemas;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;
import org.raistlic.common.permutation.Combination;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiPredicate;

public class SchemasUtil {
    // i.e. 1101 isSuper 0100,
    // 0100 == 1101 & 0100

    /**
     *
     * @return true if schema is super schema of other
     */
    public static boolean isSuperSchema(Schema schema, Schema other) {
        return getAnd(schema, other).equals(other);
    }

    // i.e. 0100 isSub 1101
    // 0100 == 0100 & 1101
    public static boolean isSubSchema(Schema schema, Schema other) {
        return getAnd(schema, other).equals(schema);
    }

    public static Schema getDiffs(Schema low, Schema up) {
        if (!isSubSchema(low, up)) {
            throw new RuntimeException("are you kidding me, low isn't sub-schemas of up");
        }

        Schema diffs = low.clone();
        diffs.xor(up);
        return diffs;
    }

    public static int pathLen(Schema low, Schema up) {
        return getDiffs(low, up).cardinality();
    }

    public static Schema getAnd(Schema low, Schema up) {
        Schema res = (Schema) low.clone();
        res.and(up);
        return res;
    }

    public static Schema getOr(Schema low, Schema up) {
        Schema res = (Schema) low.clone();
        res.or(up);
        return res;
    }

    // 计算输入集合的笛卡尔积
    public static Set<Schema> cartesianProduct(Set<Schema> schemas, Schema faultCasePattern) {
        if (schemas.isEmpty()) {
            throw new RuntimeException("are you kidding me? the schemas is empty!!!");
        }

        return schemas.stream()
                .map(k -> SchemasUtil.split(k, faultCasePattern))
                .reduce(null, SchemasUtil::descartes);
    }

    // 计算两集合的笛卡尔积
    public static Set<Schema> descartes(Set<Schema> A, Set<Schema> B) {
        if (A == null) {
            return B;
        }
        Set<Schema> setRes = new HashSet<>();
        for (Schema a : A) {
            for (Schema b : B) {
                if (a != null && b != null) {
                    setRes.add(getOr(a, b));
                } else if (a != null) {
                    setRes.add((Schema) a.clone());
                } else if (b != null) {
                    setRes.add((Schema) b.clone());
                } else {
                    // 我们定义 null | null == null
                    setRes.add(null);
                }
            }
        }

        return setRes;
    }

    // i.e. 101001 -> {000001, 001000, 100000}
    // 此函数不会修改传入参数
    public static Set<Schema> split(Schema mulBits, Schema faultCasePattern) {
        Set<Schema> res = new HashSet<>();
        int lowestOneBitIndex = mulBits.nextSetBit(0);
        while (lowestOneBitIndex != -1) {
            Schema temp = new Schema(faultCasePattern.size());
            temp.set(lowestOneBitIndex);
            res.add(temp);
            lowestOneBitIndex = mulBits.nextSetBit(lowestOneBitIndex + 1);
        }
        return res;
    }

    // 将所有 schemas 取反, i.e. [1010, 0110] -> [0101, 1001]
    // 此函数不会修改传入参数
    public static Set<Schema> negSchemas(Set<Schema> schemas, Schema faultCasePattern) {
        Set<Schema> res = new HashSet<>();
        for (Schema schema : schemas) {
            res.add(getDiffs(schema, faultCasePattern));
        }
        return res;
    }

    /**
     *
     * @param healthSchemas: healthSchemas is this schema which pass the test case
     * @param faultCasePattern: faultCasePattern provide the size of TRT root
     * @return
     */
    public static Set<Schema> updateLowBound(Set<Schema> healthSchemas, Schema faultCasePattern) {
        simplifyBound(healthSchemas, SchemasUtil::isSubSchema);
        Set<Schema> negHealthSchemas = negSchemas(healthSchemas, faultCasePattern);
        Set<Schema> lowBound = cartesianProduct(negHealthSchemas, faultCasePattern);
        simplifyBound(lowBound, SchemasUtil::isSuperSchema);
        return lowBound;
    }

    public static Set<Schema> updateUpBound(Set<Schema> faultSchemas, Schema faultCasePattern) {
        simplifyBound(faultSchemas, SchemasUtil::isSuperSchema);
        Set<Schema> cartesianProduct = cartesianProduct(faultSchemas, faultCasePattern);
        Set<Schema> upBound = negSchemas(cartesianProduct, faultCasePattern);
        simplifyBound(upBound, SchemasUtil::isSubSchema);
        return upBound;
    }

    /**
     * remove Schema if biPredicate(A, B) is true, A、B are both in bound
     * i.e. if biPredicate == isSubSchema, and A is subSchema of B, we remove A from bound
     * otherwise, if biPredicate == isSuperSchema, and A is superSchema of B, we remove A from bound
     * @param bound
     * @param biPredicate
     */
    public static void simplifyBound(Set<Schema> bound, BiPredicate<Schema, Schema> biPredicate) {
        List<Schema> list = new ArrayList<>();
        for (Schema schema : bound) {
            for (int i = 0; i <= list.size(); i++) {
                // Sentinel condition
                if(i == list.size()){
                    list.add(schema);
                    break;
                } else if(biPredicate.test(list.get(i), schema)){
                    list.set(i, schema);
                    break;
                }else if(biPredicate.test(schema, list.get(i))){
                    break;
                }
            }
        }
        bound.retainAll(list);
    }

    /**
     *
     * @param tc
     * @param faultCase
     * @return
     */
    public static Schema tc2Schema(TestCase tc, TestCase faultCase) {
        assert tc.size() == faultCase.size();
        Schema res = new Schema(tc.size());
        int size = tc.size();
        for (int i = 0; i < size; i++) {
            if(tc.get(i).equals(faultCase.get(i))){
                res.set(i);
            }
        }
        return res;
    }

    /**
     * convert testcases to schemas
     * tc = (0, 2, 3, 0), faultCase = (1, 2, 3, 4) => 0110
     * @param tcs
     * @param faultCase
     * @return
     */
    public static Set<Schema> tcs2Schemas(Set<TestCase> tcs, TestCase faultCase) {
        Set<Schema> res = new HashSet<>();
        for (TestCase tc : tcs) {
            Schema schema = SchemasUtil.tc2Schema(tc, faultCase);
            res.add(schema);
        }
        return res;
    }

    public static Set<Schema> minFaults2Schemas(List<MinFault> mfs, TestCase faultCase) {
        Set<Schema> res = new HashSet<>();
        for (MinFault mf : mfs) {
            Schema schema = SchemasUtil.tc2Schema(new TestCase(mf), faultCase);
            res.add(schema);
        }
        return res;
    }

    /**
     *Advanced version of getPendingSchemasSize
     * @return the size of Pending Schemas, it might be a large number, so we wrap it by BigInteger
     */
    public static BigInteger getPendingSchemasSizeAdv(List<MinFault> minFaults, Set<TestCase> healthTestCases, TestCase faultCase) {
        int size = faultCase.size();
        BigInteger totalSchemasSize = BigInteger.ONE.shiftLeft(size);

        Set<Schema> faultSchemas = minFaults2Schemas(minFaults, faultCase);
        simplifyBound(faultSchemas, SchemasUtil::isSuperSchema);

        Set<Schema> healthSchemas = tcs2Schemas(healthTestCases, faultCase);
        simplifyBound(healthSchemas, SchemasUtil::isSubSchema);
        System.out.println("N=" + healthSchemas.size());
        BigInteger faultSchemasSize = getSchemasSizeByBound(faultSchemas, healthSchemas, size, true);
        BigInteger healthSchemasSize = getSchemasSizeByBound(faultSchemas, healthSchemas, size, false);

        return totalSchemasSize.subtract(healthSchemasSize).subtract(faultSchemasSize);

    }

    private static Set<Schema> lowBoundFromHTC(Set<TestCase> healthTestCases, TestCase faultCase, int size) {
        if(healthTestCases.isEmpty()){
            return new HashSet<>(List.of(new Schema(size, false)));
        }
        Set<Schema> healthSchemas = tcs2Schemas(healthTestCases, faultCase);
        return updateLowBound(healthSchemas, new Schema(size, true));
    }

    private static Set<Schema> upBoundFromMinFault(List<MinFault> minFaults, TestCase faultCase, int size) {
        if(minFaults.isEmpty()) {
            return new HashSet<>(List.of(new Schema(size, true)));
        }
        Set<Schema> faultSchemas = minFaults2Schemas(minFaults, faultCase);
        return updateUpBound(faultSchemas, new Schema(size, true));
    }

    /**
     * another version of getPendingSchemasSize
     * @return the lower bound of Pending Schemas,
     * because the exact Pending Schemas can't compute(it cost too much time!) as the increasing size of faultSchemas and healthSchemas
     */
    public static BigInteger getPendingSchemasSizeLowerBound(List<MinFault> minFaults, Set<TestCase> healthTestCases, TestCase faultCase) {
        int size = faultCase.size();

        Set<Schema> upBounds = upBoundFromMinFault(minFaults, faultCase, size);
        Set<Schema> lowBounds = lowBoundFromHTC(healthTestCases, faultCase, size);

        SchemasPath longest = getLongest(lowBounds, upBounds);
        return longest == null ? BigInteger.ZERO : getSchemaSizeByPath(longest);
    }

    private static SchemasPath getLongest(Set<Schema> lowBounds, Set<Schema> upBounds) {
        int diffNum = -1;
        Schema lower = null;
        Schema upper = null;

        for (Schema lowBound : lowBounds) {
            for (Schema upBound : upBounds) {
                if(isSubSchema(lowBound, upBound)){
                    if (pathLen(lowBound, upBound) > diffNum) {
                        diffNum = pathLen(lowBound, upBound);
                        lower = lowBound;
                        upper = upBound;
                    }
                }
            }
        }

        return diffNum == -1 ? null : new SchemasPath(lower, upper);
    }

    /**
     * formula
     * |A1 U A2 U A3 ... U Am| = ∑|Ai| - ∑| Ai /\ Aj | + ∑ | Ai /\ Aj /\ Ak | - ... (-1)^m ∑ |A1 /\ A2 /\ A3 ... /\ Am|
     * We consider each SchemasPath P as a set Ai, Ai = {x | P.low() \preceq x \preceq P.up()}
     * @param ComputeFault, Since faultSchemas and healthSchemas call different processing ways,
     *                             a variable "ComputeFaultSchemas" is used to make the distinction
     * @return
     */
    private static BigInteger getSchemasSizeByBound(Set<Schema> faultSchemas, Set<Schema> healthSchemas, int size, boolean ComputeFault) {
        // we can't compute too large-size schemas, because it costs too much time
        assert faultSchemas.size() <= 20 : String.format("faultSchemas.size(): %d, it's too large!", faultSchemas.size());
        assert healthSchemas.size() <= 20 : String.format("healthSchemas.size(): %d, it's too large!", healthSchemas.size());
        Schema top = new Schema(size, true);
        Schema bottom = new Schema(size, false);

        BigInteger resSize = BigInteger.ZERO;

        List<SchemasPath> SchemasPaths = new ArrayList<>();
        // positive is the signs of numbers,
        // positive == true => sign is "+", "-" otherwise
        boolean positive = true;

        if(ComputeFault){
            for (Schema faultSchema : faultSchemas) {
                SchemasPath faultSchemaPath = new SchemasPath(faultSchema, top);
                SchemasPaths.add(faultSchemaPath);
            }
        }else {
            for (Schema healthSchema : healthSchemas) {
                SchemasPath healthSchemaPath = new SchemasPath(bottom, healthSchema);
                SchemasPaths.add(healthSchemaPath);
            }
        }

        // derive ∑ | A1 /\ A2 /\ ... /\ Ai|, this is Combination of paths in SchemasPaths
        for (int i = 1; i <= SchemasPaths.size(); i++) {
            Combination<SchemasPath> pathCombination = Combination.of(SchemasPaths, i);
            // tempSize denote result number of each ∑
            BigInteger tempSize = BigInteger.ZERO;
            for (List<SchemasPath> schemasPathList : pathCombination) {
                if(ComputeFault) {
                    Schema faultLower = bottom.clone();
                    for (SchemasPath schemasPath : schemasPathList) {
                        faultLower.or(schemasPath.getLow());
                    }
                    tempSize = tempSize.add(getSchemaSizeByPath(new SchemasPath(faultLower, top)));
                }else {
                    Schema healthUpper = top.clone();
                    for (SchemasPath schemasPath : schemasPathList) {
                        healthUpper.and(schemasPath.getUp());
                    }
                    tempSize = tempSize.add(getSchemaSizeByPath(new SchemasPath(bottom, healthUpper)));
                }
            }

            // if positive = false, we should subtract tempSize
            if(!positive) {
                resSize = resSize.subtract(tempSize);
            }else {
                resSize = resSize.add(tempSize);
            }

            positive = !positive;
        }
        return resSize;
    }

    private static BigInteger getSchemaSizeByPath(SchemasPath path) {
        Schema low = path.getLow();
        Schema up = path.getUp();
        Schema diffs = getDiffs(low, up);
        return BigInteger.ONE.shiftLeft(diffs.cardinality());
    }
}
