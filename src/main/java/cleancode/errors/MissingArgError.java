package cleancode.errors;

public class MissingArgError implements Error {
    private final char argumentId;
    private final String argumentType;

    public MissingArgError(Character argumentId, String argumentType) {
        this.argumentId = argumentId;
        this.argumentType = argumentType;
    }

    @Override
    public ErrorCode getCode() {
        return ErrorCode.MISSING_ARGUMENT;
    }

    @Override
    public String getMessage() {
        return String.format("Could not find %s parameter for -%c.", argumentType, argumentId);
    }
}
