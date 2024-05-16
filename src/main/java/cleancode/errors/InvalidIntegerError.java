package cleancode.errors;

public class InvalidIntegerError implements Error {
    private final char argumentId;
    private final String parameter;

    public InvalidIntegerError(Character argumentId, String parameter) {
        this.argumentId = argumentId;
        this.parameter = parameter;
    }

    @Override
    public ErrorCode getCode() {
        return ErrorCode.INVALID_INTEGER;
    }

    @Override
    public String getMessage() {
        return String.format("Argument -%c expects an integer but was '%s'.",
                argumentId, parameter);
    }
}
