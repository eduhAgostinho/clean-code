package cleancode.argselements;

import cleancode.errors.Error;

import java.util.ArrayList;
import java.util.List;

public class BooleanArg implements ArgSchemaElement<Boolean> {

    private char id;
    private Boolean value;
    private final List<Error> errors = new ArrayList<>();

    public BooleanArg() {
        value = false;
    }

    public BooleanArg(Character id, String parameter) {
        this.id = id;
        this.value =  Boolean.parseBoolean(parameter);
    }

    @Override
    public Boolean get() {
        return value != null && value;
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
        return "boolean";
    }

    @Override
    public ArgSchemaElement create(Character id, String parameter) {
        return new BooleanArg(id, parameter);
    }
}
