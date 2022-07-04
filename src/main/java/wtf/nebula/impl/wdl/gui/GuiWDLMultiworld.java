package wtf.nebula.impl.wdl.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import wtf.nebula.impl.wdl.WDL;

public class GuiWDLMultiworld extends GuiScreen
{
    private GuiScreen parent;
    private GuiButton multiworldEnabledBtn;
    boolean newMultiworldState = false;

    public GuiWDLMultiworld(GuiScreen parent)
    {
        this.parent = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        int w = this.width / 2;
        int h = this.height / 4;
        int hi = h + 115;
        this.multiworldEnabledBtn = new GuiButton(1, w - 100, hi, "Multiworld support: ERROR");
        this.buttonList.add(this.multiworldEnabledBtn);
        this.updateMultiworldEnabled(false);
        this.buttonList.add(new GuiButton(100, w - 100, h + 150, "OK"));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.enabled)
        {
            if (guibutton.id == 1)
            {
                this.updateMultiworldEnabled(true);
            }
            else if (guibutton.id == 100)
            {
                if (this.newMultiworldState)
                {
                    this.mc.displayGuiScreen(new GuiWDLMultiworldSelect(this.parent));
                }
                else
                {
                    WDL.baseProps.setProperty("LinkedWorlds", "");
                    WDL.saveProps();
                    WDL.propsFound = true;

                    if (this.parent != null)
                    {
                        this.mc.displayGuiScreen(new GuiWDL(this.parent));
                    }
                    else
                    {
                        WDL.start();
                        this.mc.displayGuiScreen((GuiScreen)null);
                        this.mc.setIngameFocus();
                    }
                }
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
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
    public void drawScreen(int i, int j, float f)
    {
        this.drawDefaultBackground();
        drawRect(this.width / 2 - 160, this.height / 4 - 60, this.width / 2 + 160, this.height / 4 + 180, -1342177280);
        this.drawCenteredString(this.fontRenderer, "Multiworld Support", this.width / 2, this.height / 4 - 40, 16711680);
        this.drawString(this.fontRenderer, "Multiworld support is required if at least one of the", this.width / 2 - 150, this.height / 4 - 15, 16777215);
        this.drawString(this.fontRenderer, " following conditions is met:", this.width / 2 - 150, this.height / 4 - 5, 16777215);
        this.drawString(this.fontRenderer, "- \"Multiworld\" is mentioned on the server\'s website", this.width / 2 - 150, this.height / 4 + 15, 16777215);
        this.drawString(this.fontRenderer, "- The server has more than 3 dimensions (or worlds)", this.width / 2 - 150, this.height / 4 + 35, 16777215);
        this.drawString(this.fontRenderer, "- The server has other dimensions than the official ones", this.width / 2 - 150, this.height / 4 + 55, 16777215);
        this.drawString(this.fontRenderer, "   (Earth, Nether, The End)", this.width / 2 - 150, this.height / 4 + 65, 16777215);
        drawRect(this.width / 2 - 102, this.height / 4 + 113, this.width / 2 + 102, this.height / 4 + 137, -65536);
        super.drawScreen(i, j, f);
    }

    private void updateMultiworldEnabled(boolean btnClicked)
    {
        if (!this.newMultiworldState)
        {
            if (btnClicked)
            {
                this.newMultiworldState = true;
                this.updateMultiworldEnabled(false);
            }
            else
            {
                this.multiworldEnabledBtn.displayString = "Multiworld support: Disabled";
            }
        }
        else if (btnClicked)
        {
            this.newMultiworldState = false;
            this.updateMultiworldEnabled(false);
        }
        else
        {
            this.multiworldEnabledBtn.displayString = "Multiworld support: Enabled";
        }
    }
}
