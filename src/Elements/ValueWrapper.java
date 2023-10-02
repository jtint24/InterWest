package Elements;

public class ValueWrapper<T> extends Value {
    T wrappedValue;

    public ValueWrapper(T wrappedValue, Type type) {
        this.wrappedValue = wrappedValue;
        this.type = type;
    }

    @Override
    public String toString() {
        return wrappedValue.toString();
    }
}
