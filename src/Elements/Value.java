package Elements;

import Regularity.DFANode;

import java.util.ArrayList;
import java.util.BitSet;

public abstract class Value {
    public abstract Type getType();

    public BitSet toBoolString() {
        // TODO: replace with a better encoding format
        byte[] bytes = toString().getBytes();

        return BitSet.valueOf(bytes);
    }
}
