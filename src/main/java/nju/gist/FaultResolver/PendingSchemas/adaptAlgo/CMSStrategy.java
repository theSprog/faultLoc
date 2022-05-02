package nju.gist.FaultResolver.PendingSchemas.adaptAlgo;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.CMS.CMS;
import nju.gist.FaultResolver.PendingSchemas.Context.Context;
import nju.gist.FaultResolver.PendingSchemas.Context.ExecutionResult;
import nju.gist.FaultResolver.PendingSchemas.Context.IStrategy;

import java.util.List;
import java.util.Set;

public class CMSStrategy implements IStrategy {
    private final CMS cms;

    public CMSStrategy(CMS cms) {
        this.cms = cms;
    }

    @Override
    public ExecutionResult execute(Context ctx) {
        cms.updateFSS(ctx.getPendingSchemas().getUp());
        cms.updateHSS(ctx.getPendingSchemas().getLow());

        Set<Schema> knownMinFaults = ctx.getKnownMinFaults();

        while (true) {
            Set<Schema> Cxs = cms.CMXS();
            Set<Schema> Cns = cms.CMNS();
            List<Schema> list = cms.getLongest(Cns, Cxs);
            if(list == null) {
                knownMinFaults.addAll(cms.getFSS());
                break;
            }
            int fsIndex = cms.biSearch(list);
            if(fsIndex == -1){
                cms.updateHSS(list.get(list.size()-1));
            }else {
                Schema fs = list.get(fsIndex);
                Schema MFS = cms.narrowFs(fs);
                cms.updateFSS(MFS);
            }
        }
        return new ExecutionResult(false, false, knownMinFaults);
    }
}
