package nju.gist.FaultResolver.PendingSchemas.PendingSchemasRange;

import nju.gist.Common.Schema;
import java.util.Iterator;

public class SchemasPath {
    private final Schema low;
    private final Schema up;

    public SchemasPath(Schema low, Schema up) {
        if(!low.stream().allMatch(up::get)){
            throw new RuntimeException("are you kidding me, this up isn't the super-schema of low");
        }
        this.low = low;
        this.up = up;
    }

    public Schema getLow() {
        return low;
    }

    public Schema getUp() {
        return up;
    }

    public Iterator<Schema> getIterator(){
        return new PathIterator(low, up);
    }
}
