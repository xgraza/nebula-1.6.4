package wtf.nebula.client.impl.command.arg.impl;

import wtf.nebula.client.impl.command.arg.Argument;

public class StringArgument extends Argument<String> {
    public StringArgument(String name) {
        super(name, String.class);
    }

    @Override
    public boolean parse(String part) {
        setValue(part);
        return true;
    }
}
