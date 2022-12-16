package wtf.nebula.client.impl.command.arg.impl;

import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.arg.Argument;
import wtf.nebula.client.impl.module.Module;

public class ModuleArgument extends Argument<Module> {
    public ModuleArgument(String name) {
        super(name, Module.class);
    }

    @Override
    public boolean parse(String part) {

        Module module = Nebula.getInstance().getModuleManager()
                .getModuleNameMap()
                .getOrDefault(part.toLowerCase(), null);

        if (module == null) {
            return false;
        }

        setValue(module);

        return true;
    }
}
