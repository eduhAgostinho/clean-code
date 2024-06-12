package cleancode;

import cleancode.argselements.ArgSchemaElement;
import cleancode.argselements.BooleanArg;
import cleancode.argselements.IntegerArg;
import cleancode.argselements.StringArg;
import cleancode.enums.ArgumentFormats;
import cleancode.errors.Error;
import cleancode.errors.ErrorCode;
import cleancode.errors.MissingArgError;
import cleancode.errors.UnexpectedArgError;
import cleancode.exceptions.InvalidFormatArgumentException;
import cleancode.exceptions.MissingArgumentException;
import cleancode.exceptions.UnexpectedArgumentException;
import cleancode.handlers.BooleanArgHandler;
import cleancode.handlers.IntegerArgHandler;
import cleancode.handlers.StringArgHandler;

import java.text.ParseException;
import java.util.*;

public class Args {
    private String schema;
    private String[] schemaArray;

    private List<String> argsArgumentList;
    private String[] argsArgument;
    private final List<Error> errors = new ArrayList<>();

    private Map<Character, ArgSchemaElement> argElements = new HashMap<>();

    private Set<Character> argsFound = new HashSet<>();
    private int currentArgument;

    public Args(String schema, String[] args) throws ParseException {
        this.schemaArray = schema != null ? schema.replace(" ", "").split(",") : new String[0];
        this.argsArgumentList = Arrays.stream(args).toList();

        this.schema = schema;
        this.argsArgument = args;

        parse();
//        parseNew();
    }

    private void parseNew() throws ParseException {
        if (schemaArray.length == 0 && argsArgumentList.isEmpty())
            return;

        validateSchema();

        var argsKeysList = new ArrayList<Character>(List.of());
        var argsKeys = argsArgumentList.stream().findFirst();
        if (argsKeys.isPresent()) {
            for (var i = 0; i < argsKeys.get().length(); i++) {
                var currentKey = argsKeys.get().charAt(i);
                if (currentKey != '-') {
                    argsKeysList.add(currentKey);
                }
            }
        }

        var argumentsMap = new HashMap<Character, String>();
        for (var i = 0; i < argsKeysList.size(); i++) {
            var isValueMissing = argsArgumentList.size() <= i+1;
            var value = isValueMissing ? null :  argsArgumentList.get(i+1);
            argumentsMap.put(argsKeysList.get(i), value);
        }

        for (Map.Entry<Character, String> entry : argumentsMap.entrySet()) {
            Character argKey = entry.getKey();
            String argValue = entry.getValue();

            var chainOfHandlers = new IntegerArgHandler(
                    new StringArgHandler(
                            new BooleanArgHandler(null)
                    ));
            try {
                var element = chainOfHandlers.process(schemaArray, argKey, argValue);
                argElements.put(argKey, element);
                argsFound.add(argKey);
            } catch (MissingArgumentException e) {
                errors.add(new MissingArgError(argKey, e.getArgumentType()));
            } catch (UnexpectedArgumentException e) {
                errors.add(new UnexpectedArgError(argKey));
            } catch (InvalidFormatArgumentException e) {
                throw new ParseException(
                        String.format("Argument: %c has invalid format: %s.",
                                argKey, argValue), 0);
            }
        }
    }

    private void validateSchema() throws ParseException {
        for (String element : schemaArray) {
            if (element.length() > 0) {
                char elementId = element.charAt(0);
                validateSchemaElementId(elementId);
                validateSchemaElementTail(element);
            }
        }
    }

    private void parse() throws ParseException {
        if (schema.length() == 0 && argsArgument.length == 0)
            return;
        parseSchema();
        parseArguments();
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

    private void validateSchemaElementTail(String element) throws ParseException {
        var elementId = element.charAt(0);
        var elementTail = element.length() == 2 ? String.valueOf(element.charAt(1)) : "";
        if (!ArgumentFormats.isValidValue(elementTail)) {
            throw new ParseException(
                    String.format("Argument: %c has invalid format: %s.",
                            elementId, elementTail), 0);
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

    private void parseArguments() {
        for (currentArgument = 0; currentArgument < argsArgument.length; currentArgument++) {
            String arg = argsArgument[currentArgument];
            parseArgument(arg);
        }
    }

    private void parseArgument(String arg) {
        if (arg.startsWith("-"))
            parseElements(arg);
    }

    private void parseElements(String arg) {
        for (int i = 1; i < arg.length(); i++)
            parseElement(arg.charAt(i));
    }

    private void parseElement(char argChar) {
        if (containsTheArgument(argChar)) {
            setArg(argChar);
            argsFound.add(argChar);
        } else {
            errors.add(new UnexpectedArgError(argChar));
        }
    }

    private boolean containsTheArgument(char argChar) {
        return argElements.containsKey(argChar);
    }

    private void setArg(char argChar) {
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

    public String errorMessage() {
        String errorMessage = errors.isEmpty() ? this.getErrorMessageFromArgElements() : this.getErrorMessage();
        return errorMessage != null ? errorMessage : "";
    }

    private String getErrorMessage() {
        var firstError = errors.stream().findFirst().get();
        if (isUnexpectedArgumentError(firstError)) {
            return getUnexpectedArgumentMessage();
        }
        return firstError.getMessage();
    }

    private boolean isUnexpectedArgumentError(Error error) {
        return error.getCode().equals(ErrorCode.UNEXPECTED_ARGUMENT);
    }

    private String getUnexpectedArgumentMessage() {
        StringBuilder message = new StringBuilder("Argument(s) -");
        var unexpectedArgumentErrors = errors.stream().filter(this::isUnexpectedArgumentError).toList();
        for (Error error : unexpectedArgumentErrors) {
            message.append(error.getMessage());
        }
        message.append(" unexpected.");

        return message.toString();
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
        return errors.isEmpty() && argElements.values().stream().allMatch(ArgSchemaElement::isValid);
    }

}