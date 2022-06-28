package wtf.nebula.impl.command.impl;

import wtf.nebula.impl.command.Command;
import wtf.nebula.impl.module.Module;
import wtf.nebula.repository.impl.ModuleRepository;

import java.util.Arrays;
import java.util.List;

public class Toggle extends Command {
    public Toggle() {
        super(Arrays.asList("toggle", "on", "off", "t"), "Toggles a module");
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            sendChatMessage("Please provide a module name");
            return;
        }

        Module module = ModuleRepository.get().moduleByName.getOrDefault(args.get(0), null);
        if (module == null) {
            sendChatMessage("Please provide a valid module name");
            return;
        }

        module.setState(!module.getState());

        sendChatMessage("Toggled module successfully.");
    }
}
