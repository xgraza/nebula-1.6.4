package net.minecraft.client.gui;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;

public class GuiDownloadTerrain extends GuiScreen
{
    private NetHandlerPlayClient field_146594_a;
    private int field_146593_f;
    private static final String __OBFID = "CL_00000708";

    public GuiDownloadTerrain(NetHandlerPlayClient p_i45023_1_)
    {
        this.field_146594_a = p_i45023_1_;
    }

    protected void keyTyped(char par1, int par2) {}

    public void initGui()
    {
        this.buttonList.clear();
    }

    public void updateScreen()
    {
        ++this.field_146593_f;

        if (this.field_146593_f % 20 == 0)
        {
            this.field_146594_a.addToSendQueue(new C00PacketKeepAlive());
        }

        if (this.field_146594_a != null)
        {
            this.field_146594_a.onNetworkTick();
        }
    }

    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.downloadingTerrain", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
