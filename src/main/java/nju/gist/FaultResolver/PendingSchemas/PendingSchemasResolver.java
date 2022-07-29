package nju.gist.FaultResolver.PendingSchemas;

import nju.gist.Common.MinFault;
import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.FaultResolver.CMS.CMS;
import nju.gist.FaultResolver.FIC.FIC;
import nju.gist.FaultResolver.FIC.FICBS;
import nju.gist.FaultResolver.PendingSchemas.Context.Context;
import nju.gist.FaultResolver.PendingSchemas.Context.ExecutionResult;
import nju.gist.FaultResolver.PendingSchemas.Context.IStrategy;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange.SchemasPath;
import nju.gist.FaultResolver.PendingSchemas.adaptAlgo.*;
import nju.gist.FaultResolver.RI.RI;
import nju.gist.Tester.Productor;
import org.raistlic.common.permutation.Combination;

import java.math.BigInteger;
import java.util.*;

import static nju.gist.FaultResolver.PendingSchemas.SchemasUtil.*;

public class PendingSchemasResolver extends AbstractFaultResolver {
    private Set<Schema> PSS;
    private Set<Schema> faultSchemas;
    private Set<Schema> healthSchemas;

    private Set<Schema> upBound;
    private Set<Schema> lowBound;

    private List<SchemasPath> PendingSchemasPathGroup;
    private List<SchemasPath> FaultSchemasPathGroup;
    private List<SchemasPath> HealthSchemasPathGroup;

    StrategyKind strategyKind;

    public PendingSchemasResolver() {
        this.strategyKind = StrategyKind.CMSStrategy;
    }

    public PendingSchemasResolver(StrategyKind strategyKind) {
        this.strategyKind = strategyKind;
    }

    public enum StrategyKind{
        FICStrategy,
        FICBSStrategy,
        CMSStrategy,
        RIStrategy,
    }


    @Override
    public void setFaultCase(TestCase faultCase) {
        super.setFaultCase(faultCase);
        if (PSS != null) {    // 说明都还没有初始化
            PSS.clear();
            healthSchemas.clear();
            faultSchemas.clear();
            PendingSchemasPathGroup.clear();
            FaultSchemasPathGroup.clear();
            HealthSchemasPathGroup.clear();
        } else {
            PSS = new HashSet<>();
            healthSchemas = new HashSet<>();
            faultSchemas = new HashSet<>();
            PendingSchemasPathGroup = new ArrayList<>();
            FaultSchemasPathGroup = new ArrayList<>();
            HealthSchemasPathGroup = new ArrayList<>();
        }
    }

    public Set<Schema> getUpBound() {
        return upBound;
    }

    public Set<Schema> getLowBound() {
        return lowBound;
    }

    public void updateLowBound() {
        lowBound = SchemasUtil.updateLowBound(healthSchemas, faultCasePattern);
    }

    public void updateUpBound() {
        upBound = SchemasUtil.updateUpBound(faultSchemas, faultCasePattern);
    }

    /**
     * @return true if upBound or lowBound changed, false otherwise
     */
    private boolean stillChanging() {
        for (Schema up : upBound) {
            for (Schema low : lowBound) {
                if (isSuperSchema(up, low)) {
                    boolean changed = processPendingSchemas(low, up);
                    if (changed) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param low
     * @param up
     * @return true if healthSchemas or faultSchemas changed, false otherwise
     * <p>
     * 对 pendingSchemas 做出不同策略的处理
     * If the healthSchemas or faultSchemas are changed, the lowBound and upBound are recalculated.
     */
    private boolean processPendingSchemas(Schema low, Schema up) {
        SchemasPath SchemasPath = new SchemasPath(low, up);

        // Providing Context to strategy
        Context context = new Context(SchemasPath, healthSchemas, faultSchemas, knownMinFaults, faultCase, checker);

        IStrategy strategy;

//        用某种策略处理 pending,
//        StrategyKind strategyKind = StrategyKind.CMSStrategy;

        switch (strategyKind) {
            case FICStrategy:
                strategy = new FICStrategy(new FIC(checker, faultCase));
                break;
            case FICBSStrategy:
                strategy = new FICBSStrategy(new FICBS(checker, faultCase));
                break;
            case CMSStrategy:
                CMS cms = new CMS(checker, faultCase);
                cms.setFSSAndHSS(faultSchemas, healthSchemas);
                cms.setPendingResolver(this);
                strategy = new CMSStrategy(cms);
                break;
            case RIStrategy:
                RI ri = new RI(checker, faultCase, faultCasePattern);
                strategy = new RIStrategy(ri);
                break;
            default:
                throw new RuntimeException("No this strategy");

        }

        context.setStrategy(strategy);
        ExecutionResult exeResult = context.execute();

        // the lowBound and upBound are recalculated
        if (exeResult.healthSchemasChanged()) {
            updateLowBound();
        }
        if (exeResult.faultSchemasChanged()) {
            updateUpBound();
        }

        return exeResult.faultSchemasChanged() || exeResult.healthSchemasChanged();
    }

    private boolean processFaultCase() {
        // 下界全0, 上界全1
        return processPendingSchemas(healthCasePattern, faultCasePattern);
    }

    /**
     * @param low
     * @param up
     * @return the all PendingSchemas on this path
     * i.e. low = 0100, up = 0111
     * and the result would be [0100, 0101, 0110, 0111]
     * we allow that low.equal(up), for example low = up = 0100, and the result would be [0100]
     * 注意此处返回的不是set, 而是 list, 出于性能的考虑
     */
    private List<Schema> getAllPendingFromPath(Schema low, Schema up) {
        Schema diffs = getDiffs(low, up);

        ArrayList<Schema> list = new ArrayList<>(1 << diffs.cardinality());
        list.add(low);

        int diffIndex = diffs.nextSetBit(0);
        while (diffIndex != -1) {
            int curLen = list.size();
            for (int i = 0; i < curLen; i++) {
                Schema temp = list.get(i).clone();
                temp.set(diffIndex);
                list.add(temp);
            }
            diffIndex = diffs.nextSetBit(diffIndex + 1);
        }

        return list;
    }

    /**
     * toy version for count PendingSchemasSize, please use SchemasUtil::getPendingSchemasSizeAdv instead
     * @return
     */
    public long getPendingSchemasSize() {
        long pendingSchemasSize = 1L << size;
        long faultSchemasSize = 0;
        long healthSchemasSize = 0;

        processFaultCase();

        for (Schema faultSchema : faultSchemas) {
            SchemasPath faultSchemaPath = new SchemasPath(faultSchema, faultCasePattern);
            faultSchemasSize += getSchemasSize(faultSchemaPath, FaultSchemasPathGroup);
        }

        for (Schema healthSchema : healthSchemas) {
            SchemasPath healthSchemaPath = new SchemasPath(healthCasePattern, healthSchema);
            healthSchemasSize += getSchemasSize(healthSchemaPath, HealthSchemasPathGroup);
        }

        return pendingSchemasSize - healthSchemasSize - faultSchemasSize;
    }

    private long getSchemasSize(SchemasPath schemasPath, List<SchemasPath> schemasPathGroup) {
        long schemasSize = 0;
        Iterator<Schema> iterator = schemasPath.getIterator();
        while (iterator.hasNext()) {
            Schema schema = iterator.next();
            if (!groupContain(schema, schemasPathGroup)) {
                schemasSize++;
            }
        }
        schemasPathGroup.add(schemasPath);
        return schemasSize;
    }

    // TODO 此函数极其耗时， 大约 92.3% 的时间都花在此函数上
    private boolean groupContain(Schema schema, List<SchemasPath> SchemasPathGroup) {
        for (SchemasPath range : SchemasPathGroup) {
            if (isSubSchema(range.getLow(), schema) && isSuperSchema(range.getUp(), schema)) {
                return true;
            }
        }
        return false;
    }


    public Set<Schema> getPendingSchemas() {
        boolean allowUse = true;

        if(!allowUse){
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

            throw new UnsupportedOperationException(
                    "\n\nATTENTION: This method (" + methodName + ") has the danger of OOM, \n" +
                            "If you really want to call this method, " +
                            "just turn on allowUse !!!\n"
            );
        }
        // 重复调用只会返回第一次一次结果
        // PSS 的清空只会在更换 faultCase 时
        if (!PSS.isEmpty()) {
            return PSS;
        }

        // If this is indeed the first time this function is called
        processFaultCase();
        for (Schema low : lowBound) {
            for (Schema up : upBound) {
                if (isSuperSchema(up, low)) {
                    List<Schema> pds = getAllPendingFromPath(low, up);
                    PSS.addAll(pds);
                }
            }
        }
        return PSS;
    }

    @Override
    public List<MinFault> findMinFaults() {
        // Repeated calls will only return the first-call result
        // minFaults will only be cleared when the faultCase is replaced
        if (!minFaults.isEmpty()) {
            return minFaults;
        }

        boolean changed = processFaultCase();
        while (changed) {
            changed = stillChanging();
        }

        for (Schema minFault : knownMinFaults) {
            minFaults.add(Productor.genMinFault(minFault, faultCase));
        }

        return minFaults;
    }
}
