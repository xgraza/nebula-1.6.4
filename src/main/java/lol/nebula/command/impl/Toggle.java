package lol.nebula.command.impl;

import lol.nebula.Nebula;
import lol.nebula.command.Command;
import lol.nebula.module.Module;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class Toggle extends Command {
    public Toggle() {
        super(new String[]{"toggle", "t"}, "Toggles a module", "<module name>");
    }

    @Override
    public String dispatch(String[] args) {
        if (args.length == 0) return getSyntax();

        Module module = Nebula.getInstance().getModules().get(String.join(" ", args));
        if (module == null) return "Could not find module with that name";
        module.setState(!module.isToggled());
        return module.getTag()
                + " has been "
                + (module.isToggled()
                    ? EnumChatFormatting.GREEN + "enabled"
                    : EnumChatFormatting.RED + "disabled");
    }
}
