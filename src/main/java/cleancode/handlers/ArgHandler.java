package cleancode.handlers;

import cleancode.argselements.ArgSchemaElement;
import cleancode.exceptions.InvalidFormatArgumentException;
import cleancode.exceptions.MissingArgumentException;
import cleancode.exceptions.UnexpectedArgumentException;

public interface ArgHandler {
    ArgSchemaElement process(String[] schema, char argKey, String argValue)
            throws MissingArgumentException, UnexpectedArgumentException, InvalidFormatArgumentException;
}
