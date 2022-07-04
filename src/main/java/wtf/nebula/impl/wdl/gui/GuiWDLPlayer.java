package wtf.nebula.impl.wdl.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import wtf.nebula.impl.wdl.WDL;

public class GuiWDLPlayer extends GuiScreen
{
    private String title = "";
    private GuiScreen parent;
    private GuiButton healthBtn;
    private GuiButton hungerBtn;
    private GuiButton playerPosBtn;
    private GuiButton pickPosBtn;
    private boolean showPosFields = false;
    private GuiTextField posX;
    private GuiTextField posY;
    private GuiTextField posZ;
    private int posTextY;

    public GuiWDLPlayer(GuiScreen parent)
    {
        this.parent = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.title = "Player Options for " + WDL.baseFolderName.replace('@', ':');
        int w = this.width / 2;
        int h = this.height / 4;
        int hi = h - 15;
        this.healthBtn = new GuiButton(1, w - 100, hi, "Health: ERROR");
        this.buttonList.add(this.healthBtn);
        this.updateHealth(false);
        hi += 22;
        this.hungerBtn = new GuiButton(2, w - 100, hi, "Hunger: ERROR");
        this.buttonList.add(this.hungerBtn);
        this.updateHunger(false);
        hi += 22;
        this.playerPosBtn = new GuiButton(3, w - 100, hi, "Player Position: ERROR");
        this.buttonList.add(this.playerPosBtn);
        hi += 22;
        this.posTextY = hi + 4;
        this.posX = new GuiTextField(this.fontRenderer, w - 87, hi, 50, 16);
        this.posY = new GuiTextField(this.fontRenderer, w - 19, hi, 50, 16);
        this.posZ = new GuiTextField(this.fontRenderer, w + 48, hi, 50, 16);
        this.posX.setMaxStringLength(7);
        this.posY.setMaxStringLength(7);
        this.posZ.setMaxStringLength(7);
        hi += 18;
        this.pickPosBtn = new GuiButton(4, w - 0, hi, 100, 20, "Current position");
        this.buttonList.add(this.pickPosBtn);
        this.updatePlayerPos(false);
        this.updatePosXYZ(false);
        this.buttonList.add(new GuiButton(100, w - 100, h + 150, "Done"));
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
                this.updateHealth(true);
            }
            else if (guibutton.id == 2)
            {
                this.updateHunger(true);
            }
            else if (guibutton.id == 3)
            {
                this.updatePlayerPos(true);
            }
            else if (guibutton.id == 4)
            {
                this.pickPlayerPos();
            }
            else if (guibutton.id == 100)
            {
                if (this.showPosFields)
                {
                    this.updatePosXYZ(true);
                }

                WDL.saveProps();
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

        if (this.showPosFields)
        {
            this.posX.mouseClicked(i, j, k);
            this.posY.mouseClicked(i, j, k);
            this.posZ.mouseClicked(i, j, k);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
        this.posX.textboxKeyTyped(c, i);
        this.posY.textboxKeyTyped(c, i);
        this.posZ.textboxKeyTyped(c, i);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.posX.updateCursorCounter();
        this.posY.updateCursorCounter();
        this.posZ.updateCursorCounter();
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int i, int j, float f)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, this.height / 4 - 40, 16777215);

        if (this.showPosFields)
        {
            this.drawString(this.fontRenderer, "X:", this.width / 2 - 99, this.posTextY, 16777215);
            this.drawString(this.fontRenderer, "Y:", this.width / 2 - 31, this.posTextY, 16777215);
            this.drawString(this.fontRenderer, "Z:", this.width / 2 + 37, this.posTextY, 16777215);
            this.posX.drawTextBox();
            this.posY.drawTextBox();
            this.posZ.drawTextBox();
        }

        super.drawScreen(i, j, f);
    }

    private void updateHealth(boolean btnClicked)
    {
        String playerHealth = WDL.baseProps.getProperty("PlayerHealth");

        if (playerHealth.equals("keep"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("PlayerHealth", "20");
                this.updateHealth(false);
            }
            else
            {
                this.healthBtn.displayString = "Health: Don\'t change";
            }
        }
        else if (playerHealth.equals("20"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("PlayerHealth", "keep");
                this.updateHealth(false);
            }
            else
            {
                this.healthBtn.displayString = "Health: Full";
            }
        }
    }

    private void updateHunger(boolean btnClicked)
    {
        String playerFood = WDL.baseProps.getProperty("PlayerFood");

        if (playerFood.equals("keep"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("PlayerFood", "20");
                this.updateHunger(false);
            }
            else
            {
                this.hungerBtn.displayString = "Hunger: Don\'t change";
            }
        }
        else if (playerFood.equals("20"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("PlayerFood", "keep");
                this.updateHunger(false);
            }
            else
            {
                this.hungerBtn.displayString = "Hunger: Full";
            }
        }
    }

    private void updatePlayerPos(boolean btnClicked)
    {
        String playerPos = WDL.worldProps.getProperty("PlayerPos");
        this.showPosFields = false;
        this.pickPosBtn.drawButton = false;

        if (playerPos.equals("keep"))
        {
            if (btnClicked)
            {
                WDL.worldProps.setProperty("PlayerPos", "xyz");
                this.updatePlayerPos(false);
            }
            else
            {
                this.playerPosBtn.displayString = "Player Position: Don\'t change";
            }
        }
        else if (playerPos.equals("xyz"))
        {
            if (btnClicked)
            {
                WDL.worldProps.setProperty("PlayerPos", "keep");
                this.updatePlayerPos(false);
            }
            else
            {
                this.playerPosBtn.displayString = "Player Position:";
                this.showPosFields = true;
                this.pickPosBtn.drawButton = true;
            }
        }
    }

    private void updatePosXYZ(boolean write)
    {
        if (write)
        {
            try
            {
                int e = Integer.parseInt(this.posX.getText());
                int y = Integer.parseInt(this.posY.getText());
                int z = Integer.parseInt(this.posZ.getText());
                WDL.worldProps.setProperty("PlayerX", String.valueOf(e));
                WDL.worldProps.setProperty("PlayerY", String.valueOf(y));
                WDL.worldProps.setProperty("PlayerZ", String.valueOf(z));
            }
            catch (NumberFormatException var5)
            {
                this.updatePlayerPos(true);
            }
        }
        else
        {
            this.posX.setText(WDL.worldProps.getProperty("PlayerX"));
            this.posY.setText(WDL.worldProps.getProperty("PlayerY"));
            this.posZ.setText(WDL.worldProps.getProperty("PlayerZ"));
        }
    }

    private void pickPlayerPos()
    {
        int x = (int)Math.floor(WDL.tp.posX);
        int y = (int)Math.floor(WDL.tp.posY);
        int z = (int)Math.floor(WDL.tp.posZ);
        this.posX.setText(String.valueOf(x));
        this.posY.setText(String.valueOf(y));
        this.posZ.setText(String.valueOf(z));
    }
}
