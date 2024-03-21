package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;

import nebula.client.Nebula;
import nebula.client.module.impl.player.autoreconnect.AutoReconnectModule;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import static java.lang.String.format;

public class GuiDisconnected extends GuiScreen
{

    private AutoReconnectModule autoReconnect;

    private String field_146306_a;
    private IChatComponent field_146304_f;
    private List field_146305_g;
    private final GuiScreen field_146307_h;
    private static final String __OBFID = "CL_00000693";

    public GuiDisconnected(GuiScreen p_i45020_1_, String p_i45020_2_, IChatComponent p_i45020_3_)
    {
        this.field_146307_h = p_i45020_1_;
        this.field_146306_a = I18n.format(p_i45020_2_, new Object[0]);
        this.field_146304_f = p_i45020_3_;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        if (autoReconnect == null) {
            autoReconnect = Nebula.INSTANCE.module.get(AutoReconnectModule.class);
        }

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.toMenu", new Object[0])));
        this.field_146305_g = this.fontRenderer.listFormattedStringToWidth(this.field_146304_f.getFormattedText(), this.width - 50);

        if (autoReconnect.canReconnect()) {
            autoReconnect.timer().resetTime();
            buttonList.add(new GuiButton(1, width / 2 - 100, this.height / 4 + 120 + 12 + 22,
              "Reconnect to " + autoReconnect.lastServer().serverIP));
        }
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.id == 0)
        {
            this.mc.displayGuiScreen(this.field_146307_h);
        }
        else if (p_146284_1_.id == 1) {
            mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(null), mc, autoReconnect.lastServer()));
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.field_146306_a, this.width / 2, this.height / 2 - 50, 11184810);
        int var4 = this.height / 2 - 30;

        if (this.field_146305_g != null)
        {
            for (Iterator var5 = this.field_146305_g.iterator(); var5.hasNext(); var4 += this.fontRenderer.FONT_HEIGHT)
            {
                String var6 = (String)var5.next();
                this.drawCenteredString(this.fontRenderer, var6, this.width / 2, var4, 16777215);
            }
        }

        if (autoReconnect.canReconnect()) {
            double timeLeft = ((autoReconnect.delay.value() * 1000.0)
              - autoReconnect.timer().timeElapsedMS()) / 1000.0;
            timeLeft = Math.max(timeLeft, 0.0);

            this.drawCenteredString(this.fontRenderer,
              format("Automatically reconnecting in %.1f seconds...", timeLeft),
              this.width / 2,
              30, 11184810);

            if (autoReconnect.timer().ms((long) (autoReconnect.delay.value() * 1000.0), true)) {
                mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(null), mc, autoReconnect.lastServer()));
            }

        }

        super.drawScreen(par1, par2, par3);
    }
}
