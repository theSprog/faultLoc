package nju.gist.Common;

import java.util.BitSet;

/**
 *   Schema is an alias of bitset
 */
public class Schema extends BitSet {
    // "int" might be too large, even "short" is also large, but for convenience I choose it
    // otherwise there would be a lot of verbose type conversion code
    private final int logic_size;

    public Schema(int size) {
        super(size);
        super.clear(0, size);
        logic_size = size;
    }

    /**
     * Construct a Schema and set all bits to 0 or to 1 according to the flag
     * @param size,
     * @param flag, set all bits to 1 if true, else all bits to 0
     */
    public Schema(int size, boolean flag) {
        super(size);
        if(flag) {
            super.set(0, size);
        }else {
            super.clear(0, size);
        }
        logic_size = size;
    }

    /**
     * we don't need real memory size, just logic size enough, so we override
     * i.e. new Schema(10) : real size -> 64 bits but logic size -> 10
     * @return logic size
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
