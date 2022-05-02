package nju.gist.FaultResolver.PendingSchemas;

import nju.gist.Common.Schema;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiPredicate;

public class SchemasUtil {
    // i.e. 1101 isSuper 0100,
    // 0100 == 1101 & 0100
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

        Schema diffs = (Schema) low.clone();
        diffs.xor(up);
        return diffs;
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

    public static void simplifyBound(Set<Schema> bound, BiPredicate<Schema, Schema> biPredicate) {
        LinkedList<Schema> list = new LinkedList<>();
        for (Schema schema : bound) {
            for (int i = 0; i <= list.size(); i++) {
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
}