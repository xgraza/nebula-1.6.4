package wtf.nebula.impl.wdl.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import wtf.nebula.impl.wdl.WDL;

public class GuiWDLBackup extends GuiScreen
{
    private String title = "";
    private GuiScreen parent;
    private GuiTextField commandField;
    private GuiButton backupBtn;
    boolean cmdBox = false;

    public GuiWDLBackup(GuiScreen parent)
    {
        this.parent = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.title = "Backup Options for " + WDL.baseFolderName.replace('@', ':');
        int w = this.width / 2;
        int h = this.height / 4;
        this.backupBtn = new GuiButton(10, w - 100, h + 105, "Backup: ERROR");
        this.buttonList.add(this.backupBtn);
        this.updateBackup(false);
        this.commandField = new GuiTextField(this.fontRenderer, w - 98, h + 126, 196, 17);
        this.buttonList.add(new GuiButton(100, w - 100, h + 150, "Done"));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.enabled)
        {
            if (guibutton.id == 10)
            {
                this.updateBackup(true);
            }
            else if (guibutton.id == 100)
            {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);

        if (this.cmdBox)
        {
            this.commandField.mouseClicked(i, j, k);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
        this.commandField.textboxKeyTyped(c, i);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.commandField.updateCursorCounter();
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int i, int j, float f)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, this.height / 4 - 40, 16777215);
        this.drawString(this.fontRenderer, "Name:", this.width / 2 - 99, 50, 16777215);

        if (this.cmdBox)
        {
            this.commandField.drawTextBox();
        }

        super.drawScreen(i, j, f);
    }

    public void updateBackup(boolean btnClicked)
    {
        this.cmdBox = false;
        String backup = WDL.baseProps.getProperty("Backup");

        if (backup == "off")
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Backup", "folder");
                this.updateBackup(false);
            }
            else
            {
                this.backupBtn.displayString = "Backup: Disabled";
            }
        }
        else if (backup == "folder")
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Backup", "zip");
                this.updateBackup(false);
            }
            else
            {
                this.backupBtn.displayString = "Backup: Copy World Folder";
            }
        }
        else if (backup == "zip")
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Backup", "command");
                this.updateBackup(false);
            }
            else
            {
                this.backupBtn.displayString = "Backup: Zip World Folder";
            }
        }
        else if (backup == "command")
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Backup", "off");
                this.updateBackup(false);
            }
            else
            {
                this.backupBtn.displayString = "Backup: Run the following command";
                this.cmdBox = true;
            }
        }
        else
        {
            WDL.baseProps.setProperty("Backup", "off");
            this.updateBackup(false);
        }
    }
}
