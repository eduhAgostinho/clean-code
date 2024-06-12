package cleancode.exceptions;

public class MissingArgumentException extends Exception {
    private final String argumentType;

    public MissingArgumentException(String argumentType) {
        super("Missing argument of type "+argumentType);
        this.argumentType = argumentType;
    }

    public String getArgumentType() {
        return argumentType;
    }
}
