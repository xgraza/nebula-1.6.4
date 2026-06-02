package ez.nebula.client.impl.module.player;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

/**
 * @author xgraza
 * @since 06/24/25
 */
@ModuleManifest(name = "AntiDisconnect",
        description = "Shows a yes/no screen before trying to disconnect asking \"Are you sure you want to disconnect?\"",
        category = ModuleCategory.PLAYER)
public final class AntiDisconnectModule extends Module
{
    @ModuleInstance
    public static AntiDisconnectModule INSTANCE;

    public static final class ConfirmDisconnectScreen extends GuiYesNo
    {
        public ConfirmDisconnectScreen(GuiScreen par1GuiScreen)
        {
            super(par1GuiScreen, "Are you sure you want to disconnect?", "", 69420);
        }
    }
}
