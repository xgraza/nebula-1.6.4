package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;

import lol.nebula.Nebula;
import lol.nebula.module.player.AutoReconnect;
import lol.nebula.util.math.timing.Timer;
import lol.nebula.util.render.font.Fonts;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import static java.lang.String.format;

public class GuiDisconnected extends GuiScreen
{
    private static final AutoReconnect RECONNECT = Nebula.getInstance().getModules().get(AutoReconnect.class);

    private String field_146306_a;
    private IChatComponent field_146304_f;
    private List field_146305_g;
    private final GuiScreen field_146307_h;

    private final Timer timer = new Timer();

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
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.toMenu", new Object[0])));
        this.field_146305_g = this.fontRendererObj.listFormattedStringToWidth(this.field_146304_f.getFormattedText(), this.width - 50);

        if (RECONNECT.isToggled() && RECONNECT.getServerData() != null) {
            timer.resetTime();
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 34, "Reconnect"));
        }
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.id == 0) {
            this.mc.displayGuiScreen(this.field_146307_h);
        } else if (p_146284_1_.id == 1) {
            mc.displayGuiScreen(new GuiConnecting(this, mc, RECONNECT.getServerData()));
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146306_a, this.width / 2, this.height / 2 - 50, 11184810);
        int var4 = this.height / 2 - 30;

        if (this.field_146305_g != null)
        {
            for (Iterator var5 = this.field_146305_g.iterator(); var5.hasNext(); var4 += this.fontRendererObj.FONT_HEIGHT)
            {
                String var6 = (String)var5.next();
                this.drawCenteredString(this.fontRendererObj, var6, this.width / 2, var4, 16777215);
            }
        }

        if (RECONNECT.isToggled() && RECONNECT.getServerData() != null) {
            double timeLeft = Math.max(((RECONNECT.delay.getValue() * 1000.0) - timer.getTimeElapsedMS()) / 1000.0, 0.0);
            Fonts.axiforma.drawCenteredString(format("Reconnecting in %.1f seconds", timeLeft), width / 2.0, 25.0, 11184810);

            if (timeLeft <= 0.0) {
                timer.resetTime();
                mc.displayGuiScreen(new GuiConnecting(this, mc, RECONNECT.getServerData()));
            }
        }

        super.drawScreen(par1, par2, par3);
    }
}
