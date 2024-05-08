package Elements;

public class ValueWrapper<T> extends Value {
    T wrappedValue;
    Type type;

    public ValueWrapper(T wrappedValue, Type type) {
        this.wrappedValue = wrappedValue;
        this.type = type;
    }

    @Override
    public String toString() {
        return wrappedValue.toString();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ValueWrapper) {

            return ((ValueWrapper<?>) obj).wrappedValue == wrappedValue && ((ValueWrapper<?>) obj).type == type;
        } else {
            return false;
        }
    }
}
