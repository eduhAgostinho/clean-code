package cleancode.handlers;

import cleancode.argselements.ArgSchemaElement;
import cleancode.argselements.BooleanArg;

import java.util.Arrays;

public class BooleanArgHandler extends ArgHandlerImp {
    public BooleanArgHandler(ArgHandler argHandler) {
        super(argHandler);
    }

    @Override
    protected String getArgumentType() {
        return "boolean";
    }

    @Override
    protected boolean shouldProcess(String[] schema, Character argKey) {
        return Arrays.stream(schema).anyMatch(element -> element.equals(String.valueOf(argKey)));
    }

    @Override
    protected ArgSchemaElement<Boolean> createArgSchemaElement(char argKey, String argValue) {
        return new BooleanArg(argKey, argValue);
    }
}
