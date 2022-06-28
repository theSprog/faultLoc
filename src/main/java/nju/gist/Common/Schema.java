package nju.gist.Common;

import java.util.BitSet;

/**
 *   Schema is an alias of bitset
 */
public class Schema extends BitSet {
    private final int logic_size;

    public Schema(int size) {
        super(size);
        super.clear(0, size);
        logic_size = size;
    }

    /**
     * we don't need real memory size, but logic size
     * i.e. new Schema(1) : real size -> 64 bits but logic size -> 1
     * @return
     */
    @Override
    public int size() {
        return logic_size;
    }

    @Override
    public Schema clone() {
        return (Schema) super.clone();
    }
}
