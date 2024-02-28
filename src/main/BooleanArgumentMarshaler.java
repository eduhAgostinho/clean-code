package main;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static main.ArgsException.ErrorCode.INVALID_BOOLEAN;
import static main.ArgsException.ErrorCode.MISSING_BOOLEAN;

public class BooleanArgumentMarshaler implements ArgumentMarshaler {
    private Boolean booleanValue = false;

    public void set(Iterator<String> currentArgument) throws ArgsException {
        String parameter = null;
        try {
            parameter = currentArgument.next();
            booleanValue = Boolean.parseBoolean(parameter);
        } catch (NoSuchElementException e) {
            throw new ArgsException(MISSING_BOOLEAN);
        } catch (NumberFormatException e) {
            throw new ArgsException(INVALID_BOOLEAN, parameter);
        }
    }

    public static boolean getValue(ArgumentMarshaler am) {
        if (am instanceof BooleanArgumentMarshaler)
            return ((BooleanArgumentMarshaler) am).booleanValue;
        else
            return false;
    }
}
