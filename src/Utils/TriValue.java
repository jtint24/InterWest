package Utils;

public enum TriValue {
    TRUE, FALSE, UNKNOWN;

    public boolean unknownIsTrue() {
        return this != TriValue.FALSE;
    }

    public boolean unknownIsFalse() {
        return this == TriValue.TRUE;
    }
}
