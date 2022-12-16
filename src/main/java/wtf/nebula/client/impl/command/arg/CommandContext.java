package wtf.nebula.client.impl.command.arg;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandContext {
    private final Map<String, Argument> argumentMap = new LinkedHashMap<>();
    private final String raw;
    private final List<String> rawArgs;

    public CommandContext(String raw, List<String> rawArgs) {
        this.raw = raw;
        this.rawArgs = rawArgs;
    }

    public <T extends Argument> T get(String name) {
        return (T) argumentMap.getOrDefault(name, new Argument(name, null) {
            @Override
            public boolean parse(String part) {
                setValue(null);
                return false;
            }
        });
    }

    public boolean has(String name) {
        Argument arg = get(name);
        return arg.getValue() != null;
    }

    public void putArg(String name, Argument arg) {
        argumentMap.put(name, arg);
    }

    public String getRaw() {
        return raw;
    }

    public List<String> getRawArgs() {
        return rawArgs;
    }
}
