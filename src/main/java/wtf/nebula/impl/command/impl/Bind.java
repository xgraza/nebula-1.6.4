package wtf.nebula.impl.command.impl;

import net.minecraft.src.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.nebula.impl.command.Command;
import wtf.nebula.impl.module.Module;
import wtf.nebula.repository.impl.ModuleRepository;

import java.util.Arrays;
import java.util.List;

public class Bind extends Command {
    public Bind() {
        super(Arrays.asList("bind", "key", "keybind", "keybinding", "binding"), "Binds a module to a key");
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            sendChatMessage("Please provide the key you'd like to bind a module to");
            return;
        }

        int keyCode = Keyboard.getKeyIndex(args.get(0).toUpperCase());

        if (args.size() == 1) {
            sendChatMessage("Please provide a module name to bind to that key");
            return;
        }

        Module module = ModuleRepository.get().moduleByName.getOrDefault(args.get(1), null);
        if (module == null) {
            sendChatMessage("That is not a valid module name");
            return;
        }

        module.setBind(keyCode);

        sendChatMessage(
                "Bound "
                        + EnumChatFormatting.GREEN + module.getName() + EnumChatFormatting.RESET
                        + " to the key " + EnumChatFormatting.GREEN + Keyboard.getKeyName(keyCode));
    }
}
