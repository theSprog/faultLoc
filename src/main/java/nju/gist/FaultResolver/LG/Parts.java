package nju.gist.FaultResolver.LG;

import nju.gist.Common.Schema;
import nju.gist.FaultResolver.PendingSchemas.SchemasUtil;

/**
 * Partition A into (A1, A2) of approximately equal sizes
 */
public class Parts {
        Schema faultPattern;
        Schema left;
        Schema right;

        public Parts(Schema faultPattern) {
            this.faultPattern = faultPattern;
            partition();
        }

        private void partition() {
            left = new Schema(faultPattern.size());

            int totalOneNum = faultPattern.cardinality();
            int nextSetBitIndex = faultPattern.nextSetBit(0);
            for (int i = 0; i < totalOneNum/2; i++) {
                left.set(nextSetBitIndex);
                nextSetBitIndex = faultPattern.nextSetBit(nextSetBitIndex+1);
            }

            right = SchemasUtil.getDiffs(left, faultPattern);
        }
}
