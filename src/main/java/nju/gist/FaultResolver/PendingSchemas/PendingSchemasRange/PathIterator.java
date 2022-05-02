package nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange;

import nju.gist.Common.Schema;
import java.util.Iterator;

public class PathIterator implements Iterator<Schema> {
    private final Schema low;
    private final Schema up;
    private final Schema diffs;

    // 目前来看 long 应该是够用了
    private long curPattern;

    private final int[] mapTable;
    private final int guard;

    public PathIterator(Schema low, Schema up) {
        this.low = low;
        this.up = up;

        this.diffs = (Schema) low.clone();
        this.diffs.xor(up);

        mapTable = new int[diffs.cardinality()];
        for (int i = 0, fromIndex = 0; i < mapTable.length; i++) {
            int setBitIndex = diffs.nextSetBit(fromIndex);
            mapTable[i] = setBitIndex;
            fromIndex = setBitIndex + 1;
        }

        // 从全 0 开始, 一直到 diffs, 即全 1
        this.curPattern = 0;
        this.guard = (1 << diffs.cardinality());
    }

    @Override
    public boolean hasNext() {
        return curPattern != guard;
    }

    @Override
    public Schema next() {
        Schema res = (Schema)low.clone();

        long temp = curPattern;
        int cursor = 0;
        while (temp != 0){
            if(temp % 2 != 0){
                res.set(mapTable[cursor]);
            }
            cursor++;
            temp = temp >> 1;
        }
        curPattern++;

        return res;
    }
}
