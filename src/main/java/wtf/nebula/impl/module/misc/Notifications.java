package wtf.nebula.impl.module.misc;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Notifications extends Module {
    public Notifications() {
        super("Notifications", ModuleCategory.MISC);
        drawn.setValue(false);
        setState(true);
    }

    public void toggleMessage(Module module) {
        // if the nullcheck passes, send a toggle message
        if (!module.nullCheck()) {
            String toggleMsg = !module.getState() ?
                    EnumChatFormatting.GREEN + "enabled" :
                    EnumChatFormatting.RED + "disabled";

            sendChatMessage(module.hashCode(), module.getName() + " " + toggleMsg + EnumChatFormatting.RESET + ".");
        }
    }
}
