package nju.gist.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * MinFault is minimal fault combination of a testcase
 * i.e. testcase = (1, 2, 3, 4)
 * MinFault might be (1, 2, -, -)
 */
public class MinFault extends Comb {
    public MinFault(int size) {
        super(size);
    }

    public MinFault(Comb combination) {
        super(combination);
    }

    public MinFault(List<Integer> gen) {
        super(gen);
    }
}
