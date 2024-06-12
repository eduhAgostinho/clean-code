package cleancode.handlers;

import cleancode.argselements.ArgSchemaElement;
import cleancode.argselements.IntegerArg;

import java.util.Arrays;

public class IntegerArgHandler extends ArgHandlerImp {

    public IntegerArgHandler(ArgHandler argHandler) {
        super(argHandler);
    }

    @Override
    protected boolean shouldProcess(String[] schema, Character argKey){
        return Arrays.stream(schema).anyMatch(element -> element.equals(argKey+"#"));
    }

    @Override
    protected String getArgumentType() {
        return "integer";
    }

    @Override
    protected ArgSchemaElement<Integer> createArgSchemaElement(char argKey, String argValue) {
        return new IntegerArg(argKey, argValue);
    }

}
