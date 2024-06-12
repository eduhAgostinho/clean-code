package cleancode.handlers;

import cleancode.argselements.ArgSchemaElement;
import cleancode.exceptions.InvalidFormatArgumentException;
import cleancode.exceptions.MissingArgumentException;
import cleancode.exceptions.UnexpectedArgumentException;

import java.util.Arrays;

public abstract class ArgHandlerImp implements ArgHandler {
    private final ArgHandler next;

    abstract protected String getArgumentType();
    abstract protected boolean shouldProcess(String[] schema, Character argKey);
    abstract protected ArgSchemaElement createArgSchemaElement(char argKey, String argValue);

    protected ArgHandlerImp(ArgHandler argHandler) {
        this.next = argHandler;
    }

    public ArgSchemaElement process(String[] schema, char argKey, String argValue)
            throws UnexpectedArgumentException, MissingArgumentException, InvalidFormatArgumentException {
        validateKey(schema, argKey);
        if (shouldProcess(schema, argKey)) {
            validateValue(argValue);
            return createArgSchemaElement(argKey, argValue);
        }

        return getNext()
                .process(schema, argKey, argValue);
    }

    protected void validateKey(String[] schema, char argKey) throws UnexpectedArgumentException {
        var keyExistsInSchema = Arrays.stream(schema).anyMatch(element ->
                element.length() == 0 ? element.equals(String.valueOf(argKey)) : element.charAt(0) == argKey
        );
        if (!keyExistsInSchema) {
            throw new UnexpectedArgumentException();
        }
    }

    protected void validateValue(String argValue) throws MissingArgumentException  {
        if (argValue == null) {
            throw new MissingArgumentException(getArgumentType());
        }
    }

    private ArgHandler getNext() throws InvalidFormatArgumentException {
        checkIfNextExists();
        return this.next;
    }

    private void checkIfNextExists() throws InvalidFormatArgumentException {
        if (this.next == null) {
            throw new InvalidFormatArgumentException();
        }
    }

    private String getElementTail() {
        return "";
    }

}
