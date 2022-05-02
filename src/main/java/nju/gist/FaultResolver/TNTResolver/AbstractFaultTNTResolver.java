package nju.gist.FaultResolver.TNTResolver;

import nju.gist.FaultResolver.AbstractFaultResolver;
import nju.gist.Tester.Productor;

import java.util.BitSet;
import java.util.List;

public abstract class AbstractFaultTNTResolver extends AbstractFaultResolver {
    protected static final boolean UP = true;
    protected static final boolean DOWN = false;

    // 有的 TNT 需要 knownTable, 有的不需要, 留给子类选择
    protected BitSet knownTable;

    protected Integer maxNode;

    @Override
    public void setFaultCase(List<Integer> faultCase) {
        super.setFaultCase(faultCase);
        this.maxNode = (1 << size) - 1;
    }

    protected boolean checkNode(int node) {
        List<Integer> testCase = Productor.genTestCase(node, faultCase);
        return checker.executeTestCase(testCase);
    }

    protected boolean checkAndStamp(int node){
        boolean pass = checkNode(node);
        if (!pass) {
            // not pass, stamp all fathers
            stamp(node, UP);
        } else {
            // pass, stamp all sons
            stamp(node, DOWN);
        }
        return pass;
    }

    protected boolean isStamped(int node) {
        return knownTable.get(node);
    }

    protected void stamp(int node, boolean direct) {
        if (isStamped(node)) return;

        knownTable.set(node);

        int seed = 1;
        int relative;
        for (int i = 0; i < size; i++) {
            relative = node ^ seed;
            if (direct == UP && relative > node) {
                // 祸及先辈
                stamp(relative, UP);
            } else if (direct == DOWN && relative < node) {
                // 福泽子孙
                stamp(relative, DOWN);
            }
            seed = seed << 1;
        }
    }

    /**
     *
     * @param node
     * @param other
     * @return true if node is the ancestor of other, false otherwise
     */
    protected boolean isAncestor(int node, int other){
        return (node > other) && ((node&other) == other);
    }

    protected boolean isDescendant(int node, int other){
        return isAncestor(other, node);
    }

    protected void preProcess() {
        // pass, 这是一个废案
    }

    public abstract List<List<Integer>> findMinFaults();
}
