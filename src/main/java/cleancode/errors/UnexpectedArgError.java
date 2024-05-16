package cleancode.errors;

public class UnexpectedArgError implements Error {
    private final Character argumentId;

    public UnexpectedArgError(Character argumentId) {
        this.argumentId = argumentId;
    }

    @Override
    public ErrorCode getCode() {
        return ErrorCode.UNEXPECTED_ARGUMENT;
    }

    @Override
    public String getMessage() {
        return argumentId.toString();
    }
}
