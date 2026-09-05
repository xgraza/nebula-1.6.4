package net.minecraft.client.gui;

import ez.nebula.client.impl.module.player.AntiDisconnectModule;
import ez.nebula.client.impl.module.player.AutoReconnectModule;
import ez.nebula.client.impl.module.render.HUDModule;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import ez.nebula.client.BuildConfig;
import ez.nebula.client.Environment;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.worlddownloader.WorldDownloader;
import ez.nebula.client.worlddownloader.WorldDownloaderGUIScreen;

public class GuiIngameMenu extends GuiScreen
{
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        byte var1 = -16;
        boolean var2 = true;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + var1, I18n.format("menu.returnToMenu")));

        if (!this.mc.isIntegratedServerRunning())
        {
            this.buttonList.get(0).displayString = I18n.format("menu.disconnect");
        }

        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + var1, I18n.format("menu.returnToGame")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + var1, 98, 20, I18n.format("menu.options")));
        if (mc.isSingleplayer() && !mc.getIntegratedServer().getPublic())
        {
            this.buttonList.add(new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + var1, 98, 20, I18n.format("menu.shareToLan")));

        } else
        {
            GuiButton var3;
            this.buttonList.add(var3 = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + var1, 98, 20, "Reconnect"));
            var3.enabled = AutoReconnectModule.INSTANCE.getLastServer() != null;
        }
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + var1, 98, 20, I18n.format("gui.achievements")));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + var1, 98, 20, I18n.format("gui.stats")));

        if (!this.mc.isIntegratedServerRunning())
        {
            GuiButton wdlDownload = new GuiButton(50, this.width / 2 - 100, this.height / 4 + 72 + var1, "WDL bug!");
            wdlDownload.displayString = WorldDownloader.INSTANCE.isDownloading() ? "Stop downloading" : "Download this world...";
            this.buttonList.add(wdlDownload);
            this.buttonList.get(0).yPosition = this.height / 4 + 144 + var1;
            this.buttonList.get(2).yPosition = this.height / 4 + 120 + var1;
            this.buttonList.get(3).yPosition = this.height / 4 + 120 + var1;
        }
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        switch (p_146284_1_.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 1:
            {
                if (AntiDisconnectModule.INSTANCE.isToggled())
                {
                    mc.displayGuiScreen(new AntiDisconnectModule.ConfirmDisconnectScreen(this));
                } else
                {
                    p_146284_1_.enabled = false;
                    disconnectFromServer();
                }
            }

            case 2:
            case 3:
            default:
                break;

            case 4:
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.func_146107_m()));
                break;

            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.func_146107_m()));
                break;

            case 7:
                if (mc.isSingleplayer() && !mc.getIntegratedServer().getPublic())
                {
                    this.mc.displayGuiScreen(new GuiShareToLan(this));
                } else
                {
                    if (p_146284_1_.enabled)
                    {
                        mc.theWorld.sendQuittingDisconnectingPacket();
                        mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(null), mc, AutoReconnectModule.INSTANCE.getLastServer()));
                    }
                }
                break;

            case 50:
                if (WorldDownloader.INSTANCE.isDownloading())
                {
                    WorldDownloader.INSTANCE.stop();
                    this.mc.displayGuiScreen(null);
                    this.mc.setIngameFocus();
                    return;
                }
                this.mc.displayGuiScreen(new WorldDownloaderGUIScreen(this));
                break;
        }
    }

    @Override
    public void confirmClicked(boolean par1, int par2)
    {
        if (par2 == 69420)
        {
            if (par1)
            {
                disconnectFromServer();
            } else
            {
                mc.displayGuiScreen(this);
            }
        }
    }

    private void disconnectFromServer()
    {
        WorldDownloader.INSTANCE.stop();
        this.mc.theWorld.sendQuittingDisconnectingPacket();
        this.mc.loadWorld(null);
        this.mc.displayGuiScreen(new GuiMainMenu());
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Game menu", this.width / 2, 40, 16777215);
        super.drawScreen(par1, par2, par3);

        if (BuildConfig.ENV == Environment.PRIVATE)
        {
            final String text = "Nebula A.S.S version - please don't distribute!";
            Fonts.POPPINS.drawStringShadow(text, width - Fonts.POPPINS.getStringWidth(text) - 2, height - Fonts.POPPINS.getFontHeight() - 2, HUDModule.INSTANCE.getBaseColor(10));
        }
    }
}
