package cleancode;

import cleancode.argselements.ArgSchemaElement;
import cleancode.argselements.BooleanArg;
import cleancode.argselements.IntegerArg;
import cleancode.argselements.StringArg;
import cleancode.errors.Error;
import cleancode.errors.ErrorCode;
import cleancode.errors.MissingArgError;

import java.text.ParseException;
import java.util.*;

public class Args {
    private String schema;
    private String[] argsArgument;
    private boolean valid = true;
    private final List<Error> errors = new ArrayList<>();
    private Set<Character> unexpectedArguments = new TreeSet<>();

    private Map<Character, ArgSchemaElement> argElements = new HashMap<>();

    private Set<Character> argsFound = new HashSet<>();
    private int currentArgument;
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";
    private ErrorCode errorCode = ErrorCode.OK;

    public Args(String schema, String[] args) throws ParseException {
        this.schema = schema;
        this.argsArgument = args;
        valid = parse();
    }

    private boolean parse() throws ParseException {
        if (schema.length() == 0 && argsArgument.length == 0)
            return true;
        parseSchema();
        try {
            parseArguments();
        } catch (Exception e) {
        }
        return valid;
    }

    private boolean parseSchema() throws ParseException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);

        if (isBooleanSchemaElement(elementTail))
            parseBooleanSchemaElement(elementId);
        else if (isStringSchemaElement(elementTail))
            parseStringSchemaElement(elementId);
        else if (isIntegerSchemaElement(elementTail)) {
            parseIntegerSchemaElement(elementId);
        } else {
            throw new ParseException(
                    String.format("Argument: %c has invalid format: %s.",
                            elementId, elementTail), 0);
        }
    }

    private void validateSchemaElementId(char elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException(
                    "Bad character:" + elementId + "in Args format: " + schema, 0);
        }
    }

    private void parseBooleanSchemaElement(char elementId) {
        argElements.put(elementId, new BooleanArg());
    }

    private void parseIntegerSchemaElement(char elementId) {
        argElements.put(elementId, new IntegerArg());
    }

    private void parseStringSchemaElement(char elementId) {
        argElements.put(elementId, new StringArg());
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }

    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private boolean parseArguments() throws Exception {
        for (currentArgument = 0; currentArgument < argsArgument.length; currentArgument++) {
            String arg = argsArgument[currentArgument];
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg) throws Exception {
        if (arg.startsWith("-"))
            parseElements(arg);
    }

    private void parseElements(String arg) throws Exception {
        for (int i = 1; i < arg.length(); i++)
            parseElement(arg.charAt(i));
    }

    private void parseElement(char argChar) throws Exception {
        if (setArgument(argChar))
            argsFound.add(argChar);
        else {
            unexpectedArguments.add(argChar);
            errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
            valid = false;
        }
    }

    private boolean setArgument(char argChar) throws Exception {
        if (containsTheArgument(argChar)) {
            setArg(argChar);
            return true;
        }
        return false;
    }

    private boolean containsTheArgument(char argChar) {
        return argElements.containsKey(argChar);
    }

    private void setArg(char argChar) throws Exception {
        currentArgument++;
        String typeArgument = "";
        try {
            var argElement = argElements.get(argChar);

            typeArgument = argElement.getType();
            String parameter = argsArgument[currentArgument];
            var newArgElement = argElement.create(argChar, parameter);
            argElements.put(argChar, newArgElement);
        } catch (ArrayIndexOutOfBoundsException e) {
            errors.add(new MissingArgError(argChar, typeArgument));
        }
    }

    public int cardinality() {
        return argsFound.size();
    }

    public String usage() {
        if (schema.length() > 0)
            return "-[" + schema + "]";
        else
            return "";
    }

    public String errorMessage() throws Exception {
        if (!errors.isEmpty()) {
            return errors.stream().findFirst().get().getMessage();
        }

        var errorMessageFromArgElements = getErrorMessageFromArgElements();
        if (errorMessageFromArgElements != null) {
            return errorMessageFromArgElements;
        }

        switch (errorCode) {
            case OK:
                throw new Exception("TILT: Should not get here.");
            case UNEXPECTED_ARGUMENT:
                return unexpectedArgumentMessage();
            case MISSING_STRING:
                return String.format("Could not find string parameter for -%c.",
                        errorArgumentId);
            case INVALID_INTEGER:
                return String.format("Argument -%c expects an integer but was '%s'.",
                        errorArgumentId, errorParameter);
            case MISSING_INTEGER:
                return String.format("Could not find integer parameter for -%c.",
                        errorArgumentId);
        }
        return "";
    }

    private String getErrorMessageFromArgElements() {
        var elementsInvalid = argElements.values()
                .stream().filter(arg -> !arg.isValid());
        var errors = elementsInvalid.map(ArgSchemaElement::getErrors)
                .findFirst();
        if (errors.isPresent()) {
            var error = ((List<Error>) errors.get()).stream().findFirst();
            if (error.isPresent()) {
                return error.get().getMessage();
            }
        }
        return null;
    }

    private String unexpectedArgumentMessage() {
        StringBuffer message = new StringBuffer("Argument(s) -");
        for (char c : unexpectedArguments) {
            message.append(c);
        }
        message.append(" unexpected.");

        return message.toString();
    }

    public String getString(char arg) {
        return (String) argElements.get(arg).get();
    }

    public Integer getInt(char arg) {
        return (Integer) argElements.get(arg).get();
    }

    public Boolean getBoolean(char arg) {
        return (Boolean) argElements.get(arg).get();
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid && errors.isEmpty() && argElements.values().stream().allMatch(ArgSchemaElement::isValid);
    }

}