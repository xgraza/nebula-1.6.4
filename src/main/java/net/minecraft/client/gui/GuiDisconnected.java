package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import us.nebula.impl.cheat.player.AutoReconnectCheat;
import us.nebula.util.math.Timer;

import java.util.List;

public class GuiDisconnected extends GuiScreen
{
    private final GuiScreen parent;
    private final String reason;
    private final IChatComponent reasonChatComponent;

    private final Timer reconnectTimer = new Timer();
    private List<String> splitText;

    public GuiDisconnected(GuiScreen parent, String reason, IChatComponent reasonComponent)
    {
        this.parent = parent;
        this.reason = I18n.format(reason);
        this.reasonChatComponent = reasonComponent;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        if (AutoReconnectCheat.INSTANCE.getLastServer() != null)
        {
            buttonList.add(new GuiButton(1, width / 2 - 100, this.height / 4 + 120 + 12, "Reconnect"));
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 142 + 12, I18n.format("gui.toMenu", new Object[0])));
        } else
        {
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.toMenu", new Object[0])));
        }
        if (AutoReconnectCheat.INSTANCE.isToggled())
        {
            reconnectTimer.resetTime();
        }
        this.splitText = this.fontRenderer.listFormattedStringToWidth(this.reasonChatComponent.getFormattedText(), this.width - 50);
    }

    protected void actionPerformed(GuiButton guiButton)
    {
        if (guiButton.id == 0)
        {
            this.mc.displayGuiScreen(this.parent);
        } else if (guiButton.id == 1)
        {
            mc.displayGuiScreen(new GuiConnecting(this, mc, AutoReconnectCheat.INSTANCE.getLastServer()));
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.reason, this.width / 2, this.height / 2 - 50, 11184810);
        int posY = this.height / 2 - 30;

        if (this.splitText != null)
        {
            for (final String var6 : splitText)
            {
                posY += this.fontRenderer.FONT_HEIGHT;
                this.drawCenteredString(this.fontRenderer, var6, this.width / 2, posY, 16777215);
            }
        }

        if (AutoReconnectCheat.INSTANCE.isToggled()
                && AutoReconnectCheat.INSTANCE.getLastServer() != null)
        {
            final long reconnectDelay = AutoReconnectCheat.INSTANCE.delaySetting.getValue() * 1000L;
            final double elapsedTime = reconnectTimer.getTimeElapsedMS();
            String timeFormatted = "";
            if (elapsedTime > reconnectDelay)
            {
                timeFormatted = "Reconnecting now...";
            } else
            {
                timeFormatted = "Reconnecting in "
                        + String.format("%.2f", (reconnectDelay - elapsedTime) / 1000.0)
                        + "s";
            }
            drawCenteredString(fontRenderer, timeFormatted, width / 2, 6, 11184810);

            if (reconnectTimer.hasElapsed(reconnectDelay + 50L))
            {
                reconnectTimer.resetTime();
                mc.displayGuiScreen(new GuiConnecting(this, mc, AutoReconnectCheat.INSTANCE.getLastServer()));
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
