package cleancode.argselements;

import cleancode.errors.Error;
import cleancode.errors.InvalidIntegerError;

import java.util.ArrayList;
import java.util.List;

public class IntegerArg implements ArgSchemaElement<Integer> {

    private char id;
    private Integer value;
    private final List<Error> errors = new ArrayList<>();

    public IntegerArg() {
        value = 0;
    }

    public IntegerArg(Character id, String parameter) {
        try {
            this.id = id;
            this.value = Integer.parseInt(parameter);
        } catch (Exception e) {
            handleException(e, parameter);
        }
    }

    private void handleException(Exception e, String parameter) {
        if (e instanceof NumberFormatException) {
            errors.add(new InvalidIntegerError(id, parameter));
        }
    }

    @Override
    public Integer get() {
        return value == null ? 0 : value;
    }

    @Override
    public Boolean isValid() {
        return errors.isEmpty();
    }

    @Override
    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public String getType() {
        return "integer";
    }

    @Override
    public ArgSchemaElement create(Character id, String parameter) {
        return new IntegerArg(id, parameter);
    }

}
