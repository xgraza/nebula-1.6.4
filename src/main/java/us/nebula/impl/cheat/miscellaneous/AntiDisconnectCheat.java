package us.nebula.impl.cheat.miscellaneous;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "AntiDisconnect",
        description = "Are you sure you want to disconnect?",
        category = CheatCategory.MISCELLANEOUS)
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
