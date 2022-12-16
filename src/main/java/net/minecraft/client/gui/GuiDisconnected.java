package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.module.miscellaneous.AutoReconnect;
import wtf.nebula.client.utils.client.Timer;

public class GuiDisconnected extends GuiScreen
{
    private String field_146306_a;
    private IChatComponent field_146304_f;
    private List field_146305_g;
    private final GuiScreen field_146307_h;
    private static final String __OBFID = "CL_00000693";

    private final Timer timer = new Timer();

    public GuiDisconnected(GuiScreen p_i45020_1_, String p_i45020_2_, IChatComponent p_i45020_3_)
    {
        this.field_146307_h = p_i45020_1_;
        this.field_146306_a = I18n.format(p_i45020_2_, new Object[0]);
        this.field_146304_f = p_i45020_3_;
    }

    protected void keyTyped(char par1, int par2) {}

    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.toMenu", new Object[0])));
        this.field_146305_g = this.fontRenderer.listFormattedStringToWidth(this.field_146304_f.getFormattedText(), this.width - 50);

        AutoReconnect autoReconnect = Nebula.getInstance().getModuleManager().getModule(AutoReconnect.class);
        if (autoReconnect.isRunning() && autoReconnect.serverData != null) {
            timer.resetTime();
            buttonList.add(new GuiButton(1, width / 2 - 100, ((GuiButton) buttonList.get(0)).yPosition + 23, "Reconnect"));
        }
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.id == 0) {
            this.mc.displayGuiScreen(this.field_146307_h);
        } else if (p_146284_1_.id == 1) {
            AutoReconnect autoReconnect = Nebula.getInstance().getModuleManager().getModule(AutoReconnect.class);
            if (autoReconnect.isRunning() && autoReconnect.serverData != null) {
                mc.displayGuiScreen(new GuiConnecting(this, mc, autoReconnect.serverData));
            }
        }
    }

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

        super.drawScreen(par1, par2, par3);

        AutoReconnect autoReconnect = Nebula.getInstance().getModuleManager().getModule(AutoReconnect.class);
        if (autoReconnect.isRunning() && autoReconnect.serverData != null) {
            if (timer.hasPassed(autoReconnect.delay.getValue() * 1000L, true)) {
                mc.displayGuiScreen(new GuiConnecting(this, mc, autoReconnect.serverData));
                return;
            }

            long timePassed = timer.getTimePassedMs();
            double displayNum = (autoReconnect.delay.getValue() * 1000L - timePassed) / 1000.0;

            drawCenteredString(fontRenderer, "Reconnecting in " + String.format("%.1f", displayNum) + "s...", width / 2, 30, 11184810);
        }
    }
}
