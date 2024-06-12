package cleancode.enums;

import java.util.Arrays;

public enum ArgumentFormats {
    INTEGER("#"),
    STRING("*"),
    BOOLEAN("");

    private final String value;

    ArgumentFormats(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidValue(String value) {
        return Arrays.stream(ArgumentFormats.values()).anyMatch(
            format -> format.getValue().equals(String.valueOf(value))
        );
    }

}
