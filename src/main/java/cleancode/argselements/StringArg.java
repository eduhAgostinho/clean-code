package cleancode.argselements;

import cleancode.errors.Error;

import java.util.ArrayList;
import java.util.List;

public class StringArg implements ArgSchemaElement<String> {

    private char id;
    private String value;
    private final List<Error> errors = new ArrayList<>();

    public StringArg() {
        this.value = "";
    }

    public StringArg(Character id, String parameter) {
        this.id = id;
        this.value = parameter;
    }

    @Override
    public String get() {
        return this.value == null ? "" : this.value;
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
        return "string";
    }

    @Override
    public ArgSchemaElement create(Character id, String parameter) {
        return new StringArg(id, parameter);
    }
}
