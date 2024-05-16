package cleancode.argselements;

import cleancode.errors.Error;

import java.util.List;

public interface ArgSchemaElement<T> {
    T get();
    Boolean isValid();
    List<Error> getErrors();
    String getType();
    ArgSchemaElement create(Character id, String parameter);
}
