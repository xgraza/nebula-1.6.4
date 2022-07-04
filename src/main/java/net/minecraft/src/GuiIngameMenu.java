package net.minecraft.src;

import wtf.nebula.impl.wdl.WDL;
import wtf.nebula.impl.wdl.gui.GuiWDL;

public class GuiIngameMenu extends GuiScreen
{
    /** Also counts the number of updates, not certain as to why yet. */
    private int updateCounter2;

    /** Counts the number of screen updates. */
    private int updateCounter;

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.updateCounter2 = 0;
        this.buttonList.clear();
        byte var1 = -16;
        boolean var2 = true;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + var1, I18n.getString("menu.returnToMenu")));

        if (!this.mc.isIntegratedServerRunning())
        {
            ((GuiButton)this.buttonList.get(0)).displayString = I18n.getString("menu.disconnect");
        }

        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + var1, I18n.getString("menu.returnToGame")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + var1, 98, 20, I18n.getString("menu.options")));
        GuiButton var3;
        this.buttonList.add(var3 = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + var1, 98, 20, I18n.getString("menu.shareToLan")));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + var1, 98, 20, I18n.getString("gui.achievements")));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + var1, 98, 20, I18n.getString("gui.stats")));
        var3.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();

        if (!this.mc.isIntegratedServerRunning())
        {
            GuiButton var4 = new GuiButton(50, this.width / 2 - 100, this.height / 4 + 72 + var1, 170, 20, "WDL bug!");
            var4.displayString = WDL.downloading ? (WDL.saving ? "Still saving..." : "Stop download") : "Download this world";
            this.buttonList.add(var4);
            var4.enabled = !WDL.downloading || WDL.downloading && !WDL.saving;
            GuiButton var5 = new GuiButton(51, this.width / 2 + 71, this.height / 4 + 72 + var1, 28, 20, "...");
            this.buttonList.add(var5);
            var5.enabled = !WDL.downloading || WDL.downloading && !WDL.saving;
            ((GuiButton)this.buttonList.get(0)).yPosition = this.height / 4 + 144 + var1;
            ((GuiButton)this.buttonList.get(2)).yPosition = this.height / 4 + 120 + var1;
            ((GuiButton)this.buttonList.get(3)).yPosition = this.height / 4 + 120 + var1;
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 1:
                par1GuiButton.enabled = false;
                WDL.stop();
                this.mc.statFileWriter.readStat(StatList.leaveGameStat, 1);
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient)null);
                this.mc.displayGuiScreen(new GuiMainMenu());

            case 2:
            case 3:
            default:
                break;

            case 4:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
                this.mc.sndManager.resumeAllSounds();
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
                break;

            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
                break;

            case 7:
                this.mc.displayGuiScreen(new GuiShareToLan(this));
                break;

            case 50:
                if (WDL.downloading)
                {
                    WDL.stop();
                }
                else
                {
                    WDL.start();
                }

                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
                break;

            case 51:
                this.mc.displayGuiScreen(new GuiWDL(this));
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCounter;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Game menu", this.width / 2, 40, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
