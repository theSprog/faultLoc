package nju.gist.FaultResolver.CMS;

import nju.gist.Common.Schema;
import nju.gist.Common.TestCase;
import nju.gist.FaultResolver.PendingSchemas.PendingSchemasResolver;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;
import nju.gist.Tester.Checker;
import nju.gist.Tester.Productor;

import java.util.*;

import static nju.gist.FaultResolver.PendingSchemas.SchemasUtil.*;

public class CMS {
    private Set<Schema> FSS;
    private Set<Schema> HSS;

    private final Checker checker;
    private final TestCase faultCase;
    private PendingSchemasResolver pendingSchemasResolver;

    public CMS(Checker checker, TestCase faultCase) {
        this.checker = checker;
        this.faultCase = faultCase;
    }

    public void setPendingResolver(PendingSchemasResolver pendingSchemasResolver) {
        this.pendingSchemasResolver = pendingSchemasResolver;
    }

    public void setFSSAndHSS(Set<Schema> faultSchemas, Set<Schema> healthSchemas) {
        this.FSS = faultSchemas;
        this.HSS = healthSchemas;
    }

    public Set<Schema> getFSS() {
        return FSS;
    }

    public Set<Schema> getHSS() {
        return HSS;
    }

    public List<Schema> getLongest(Set<Schema> cns, Schema candiMFS){
        assert candiMFS != null;
        return getLongest(cns, new HashSet<>(List.of(candiMFS)));
    }

    public List<Schema> getLongest(Set<Schema> cns, Set<Schema> cxs){
        int diffNum = -1;
        List<Schema> longest = new ArrayList<>(2);

        for (Schema cx : cxs) {
            for (Schema cn : cns) {
                if(isSubSchema(cn, cx)){
                    Schema diff = SchemasUtil.getDiffs(cn, cx);
                    if (diff.cardinality() > diffNum) {
                        diffNum = diff.cardinality();
                        longest.clear();
                        longest.add(cn);
                        longest.add(cx);
                    }
                }
            }
        }

        return diffNum == -1 ? null : toList(longest.get(0), longest.get(1));
    }

    private List<Schema> toList(Schema low, Schema up) {
        List<Schema> res = new ArrayList<>();
        res.add((Schema)low.clone());

        Schema diff = (Schema)low.clone();
        diff.xor(up);
        Schema prev = low;

        for (int i = 0, nextSetIndex = 0; i < diff.cardinality(); i++) {
            Schema temp = (Schema)prev.clone();
            nextSetIndex = diff.nextSetBit(nextSetIndex);
            temp.set(nextSetIndex);
            res.add(temp);
            nextSetIndex++;
            prev = temp;
        }

        return res;
    }


    public int biSearch(List<Schema> list) {
        // list all pass
        if(list.isEmpty()) return -1;

        if(isHealthy(list.get(list.size()-1))){
            return -1;
        }
        if(!isHealthy(list.get(0))){
            return 0;
        }

        int fIndex = 0;
        int head = 0;
        int tail = list.size() - 1;
        while (tail >= head) {
            int mid = (head + tail) / 2;
            Schema schema = list.get(mid);
            if (!isHealthy(schema)) {
                tail = mid - 1;
                fIndex = mid;
            }else {
                head  = mid + 1;
            }
        }
        return fIndex;
    }

    private boolean isHealthy(Schema schema) {
        return checker.executeTestCase(Productor.genTestCase(schema, faultCase));
    }

    public Schema narrowFs(Schema fs) {
        Schema CandiMFS = (Schema) fs.clone();
        // hss 代表局部的健康节点
        Set<Schema> hss = new HashSet<>(List.of(new Schema(fs.cardinality())));

        while (true){
            Set<Schema> Cns = updateLocalHSS(hss, fs);
            List<Schema> list = getLongest(Cns, CandiMFS);
            // 实际上，list 必不可能为 null
            if(list == null || list.size() == 1){
                return CandiMFS;
            }

            list.remove(CandiMFS);
            int fsIndex = biSearch(list);

            if(fsIndex > -1){
                return narrowFs(list.get(fsIndex));
            }
            // 说明 list 没有 fault 了
            hss.add(list.get(list.size() - 1));
        }
    }

    private Set<Schema> updateLocalFSS(Set<Schema> fss, Schema faultCasePattern) {
        return updateUpBound(fss, faultCasePattern);
    }

    private Set<Schema> updateLocalHSS(Set<Schema> hss, Schema faultCasePattern) {
        return updateLowBound(hss, faultCasePattern);
    }

    public Set<Schema> CMXS() {
        return pendingSchemasResolver.getUpBound();
    }


    public Set<Schema> CMNS() {
        return pendingSchemasResolver.getLowBound();
    }

    public void updateFSS(Schema schema) {
        if (FSS.add(schema)) {
            pendingSchemasResolver.updateUpBound();
        }
    }

    public void updateHSS(Schema schema) {
        if (HSS.add(schema)) {
            pendingSchemasResolver.updateLowBound();
        }
    }
}
