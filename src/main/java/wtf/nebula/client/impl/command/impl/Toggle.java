package wtf.nebula.client.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.ModuleArgument;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Toggle extends Command {
    public Toggle() {
        super(new String[]{"toggle", "t", "enable", "disable"},
                new ModuleArgument("name"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        ModuleArgument module = ctx.get("name");

        if (module.getValue() instanceof ToggleableModule) {
            ToggleableModule toggleableModule = (ToggleableModule) module.getValue();

            toggleableModule.setRunning(!toggleableModule.isRunning());
            return toggleableModule.getLabel() + " " + (
                    toggleableModule.isRunning()
                            ? (EnumChatFormatting.GREEN + "enabled")
                            : (EnumChatFormatting.RED + "disabled")) + EnumChatFormatting.GRAY + ".";
        } else {
            return "This module is not toggleable.";
        }
    }
}
