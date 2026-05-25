package us.nebula.client.cheat.impl.player;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "AntiDisconnect",
        description = "Shows a yes/no screen before trying to disconnect asking \"Are you sure you want to disconnect?\"",
        category = CheatCategory.PLAYER)
public final class AntiDisconnectCheat extends Cheat
{
    @CheatInstance
    public static AntiDisconnectCheat INSTANCE;

    public static final class ConfirmDisconnectScreen extends GuiYesNo
    {
        public ConfirmDisconnectScreen(GuiScreen par1GuiScreen)
        {
            super(par1GuiScreen, "Are you sure you want to disconnect?", "", 69420);
        }
    }
}
