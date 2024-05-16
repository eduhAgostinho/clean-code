package cleancode.errors;

public interface Error {
    ErrorCode getCode();
    String getMessage();
}
