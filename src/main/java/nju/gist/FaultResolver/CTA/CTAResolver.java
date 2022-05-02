package nju.gist.FaultResolver.CTA;

import nju.gist.FaultResolver.AbstractFaultResolver;

import java.util.List;

public class CTAResolver extends AbstractFaultResolver {
    private CTA cta;

    public CTAResolver(String filePath) {
        cta = new CTA(filePath);
    }

    @Override
    public List<List<Integer>> findMinFaults() {
        String tree = null;
        try {
            tree = cta.constructClassifier();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cta.parseTree(tree);
    }
}
