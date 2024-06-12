package cleancode.handlers;

import cleancode.argselements.ArgSchemaElement;
import cleancode.argselements.StringArg;

import java.util.Arrays;

public class StringArgHandler extends ArgHandlerImp {

    public StringArgHandler(ArgHandler argHandler) {
        super(argHandler);
    }

    @Override
    protected String getArgumentType() {
        return "string";
    }

    @Override
    protected boolean shouldProcess(String[] schema, Character argKey) {
        return Arrays.stream(schema).anyMatch(element -> element.equals(argKey+"*"));
    }

    @Override
    protected ArgSchemaElement<String> createArgSchemaElement(char argKey, String argValue) {
        return new StringArg(argKey, argValue);
    }
}
