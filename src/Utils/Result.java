package Utils;

public class Result<T, U> {
    private final T okValue;
    private final U errValue;

    public Result(T okValue, U errValue) {
        this.okValue = okValue;
        this.errValue = errValue;
    }
    public static <T,U> Result<T,U> ok(T val) {
        return new Result<>(val, null);
    }

    public static <T,U> Result<T,U> error(U err) {
        return new Result<>(null, err);
    }

    public boolean isOK() {
        return okValue != null;
    }

    public T getOkValue() {
        if (isOK()) {
            return okValue;
        } else {
            throw new RuntimeException("Accessed value of failed result");
        }
    }

    public U getErrValue() {
        if (!isOK()) {
            return errValue;
        } else {
            throw new RuntimeException("Accessed error of succeeded result");
        }
    }
}
