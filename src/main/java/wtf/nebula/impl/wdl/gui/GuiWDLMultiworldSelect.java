package wtf.nebula.impl.wdl.gui;

import net.minecraft.src.*;
import wtf.nebula.impl.wdl.WDL;

import java.io.File;
import java.util.Properties;

public class GuiWDLMultiworldSelect extends GuiScreen
{
    private GuiButton cancelBtn;
    private GuiTextField newNameField;
    private boolean newWorld = false;
    private int positionID;
    private float yaw;
    private int thirdPersonViewSave;
    private GuiButton[] buttons;
    private String[] worlds;
    private GuiScreen parent;
    EntityPlayerSP cam;

    public GuiWDLMultiworldSelect(GuiScreen parent)
    {
        this.parent = parent;
        EntityClientPlayerMP player = WDL.tp;
        this.cam = new EntityPlayerSP(WDL.mc, WDL.wc, new Session("Camera", ""), player.dimension);
        this.cam.setLocationAndAngles(player.posX, player.posY - (double)player.yOffset, player.posZ, player.rotationYaw, 0.0F);
        this.yaw = player.rotationYaw;
        this.thirdPersonViewSave = WDL.mc.gameSettings.thirdPersonView;
        WDL.mc.gameSettings.thirdPersonView = 0;
        WDL.mc.renderViewEntity = this.cam;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        int w = this.width / 2;
        int h = this.height / 4;
        int columns = this.width / 150;

        if (columns == 0)
        {
            columns = 1;
        }

        int buttonWidth = this.width / columns - 5;
        this.cancelBtn = new GuiButton(100, w - 100, this.height - 30, "Cancel");
        this.buttonList.add(this.cancelBtn);
        String linkedWorlds = WDL.baseProps.getProperty("LinkedWorlds");
        String[] tempWorlds = linkedWorlds.split("[|]");
        String[] tempNames = new String[tempWorlds.length];
        int validWorlds = 0;
        int spaceLeft;

        for (spaceLeft = 0; spaceLeft < tempWorlds.length; ++spaceLeft)
        {
            if (tempWorlds[spaceLeft].isEmpty())
            {
                tempWorlds[spaceLeft] = null;
            }
            else
            {
                Properties wi = WDL.loadWorldProps(tempWorlds[spaceLeft]);

                if (wi == null)
                {
                    tempWorlds[spaceLeft] = null;
                }
                else
                {
                    ++validWorlds;
                    tempNames[spaceLeft] = wi.getProperty("WorldName");
                }
            }
        }

        if (columns > validWorlds + 1)
        {
            columns = validWorlds + 1;
        }

        spaceLeft = (this.width - columns * buttonWidth) / 2;
        this.worlds = new String[validWorlds];
        this.buttons = new GuiButton[validWorlds + 1];
        int var12 = 0;
        int newWorldPos;

        for (newWorldPos = 0; newWorldPos < tempWorlds.length; ++newWorldPos)
        {
            if (tempWorlds[newWorldPos] != null)
            {
                this.worlds[var12] = tempWorlds[newWorldPos];
                this.buttons[var12] = new GuiButton(var12, var12 % columns * buttonWidth + spaceLeft, this.height - 60 - var12 / columns * 21, buttonWidth, 20, tempNames[newWorldPos]);
                this.buttonList.add(this.buttons[var12]);
                ++var12;
            }
        }

        newWorldPos = this.buttons.length - 1;

        if (!this.newWorld)
        {
            this.buttons[newWorldPos] = new GuiButton(newWorldPos, newWorldPos % columns * buttonWidth + spaceLeft, this.height - 60 - newWorldPos / columns * 21, buttonWidth, 20, "< New Name >");
            this.buttonList.add(this.buttons[newWorldPos]);
        }

        this.newNameField = new GuiTextField(this.fontRenderer, newWorldPos % columns * buttonWidth + spaceLeft, this.height - 60 - newWorldPos / columns * 21 + 1, buttonWidth, 18);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.enabled)
        {
            this.newWorld = false;

            if (guibutton.id == this.worlds.length)
            {
                this.newWorld = true;
                this.buttonList.remove(this.buttons[this.worlds.length]);
            }
            else if (guibutton.id == 100)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
            }
            else
            {
                this.worldSelected(this.worlds[guibutton.id]);
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (this.newWorld)
        {
            this.newNameField.mouseClicked(par1, par2, par3);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);

        if (this.newNameField.isFocused())
        {
            this.newNameField.textboxKeyTyped(par1, par2);

            if (par2 == 28)
            {
                String s = this.newNameField.getText();

                if (s != null && !s.isEmpty())
                {
                    this.worldSelected(this.addMultiworld(s));
                }
            }
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.newNameField.updateCursorCounter();
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int i, int j, float f)
    {
        drawRect(this.width / 2 - 120, 0, this.width / 2 + 120, this.height / 16 + 25, -1073741824);

        if (this.parent == null)
        {
            this.drawCenteredString(this.fontRenderer, "World Downloader - Trying To Start Download", this.width / 2, this.height / 16, 16777215);
        }
        else
        {
            this.drawCenteredString(this.fontRenderer, "World Downloader - Trying To Change Options", this.width / 2, this.height / 16, 16777215);
        }

        this.drawCenteredString(this.fontRenderer, "Where are you?", this.width / 2, this.height / 16 + 10, 16711680);
        this.cam.prevRotationPitch = this.cam.rotationPitch = 0.0F;
        this.cam.prevRotationYaw = this.cam.rotationYaw = this.yaw;
        float radius = 0.475F;
        this.cam.lastTickPosY = this.cam.prevPosY = this.cam.posY = WDL.tp.posY;
        this.cam.lastTickPosX = this.cam.prevPosX = this.cam.posX = WDL.tp.posX - (double)radius * Math.sin((double)this.yaw / 180.0D * Math.PI);
        this.cam.lastTickPosZ = this.cam.prevPosZ = this.cam.posZ = WDL.tp.posZ + (double)radius * Math.cos((double)this.yaw / 180.0D * Math.PI);
        float baseSpeed = 1.0F;
        this.yaw = (float)((double)this.yaw + (double)baseSpeed * (1.0D + 0.699999988079071D * Math.cos((double)(this.yaw + 45.0F) / 45.0D * Math.PI)));

        if (this.newWorld)
        {
            this.newNameField.drawTextBox();
        }

        super.drawScreen(i, j, f);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();
        WDL.mc.gameSettings.thirdPersonView = this.thirdPersonViewSave;
        this.mc.renderViewEntity = WDL.tp;
    }

    private void worldSelected(String w)
    {
        WDL.worldName = w;
        WDL.isMultiworld = true;
        WDL.propsFound = true;

        if (this.parent == null)
        {
            WDL.start();
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
        else
        {
            WDL.worldProps = WDL.loadWorldProps(w);
            this.mc.displayGuiScreen(new GuiWDL(this.parent));
        }
    }

    private String addMultiworld(String name)
    {
        String world = name;
        String invalidChars = "\\/:*?\"<>|";
        char[] newProps = invalidChars.toCharArray();
        int newWorlds = newProps.length;
        int newLinkedWorlds;

        for (newLinkedWorlds = 0; newLinkedWorlds < newWorlds; ++newLinkedWorlds)
        {
            char arr$ = newProps[newLinkedWorlds];
            world = world.replace(arr$, '_');
        }

        (new File(this.mc.mcDataDir, "saves/" + WDL.baseFolderName + " - " + world)).mkdirs();
        Properties var11 = new Properties(WDL.baseProps);
        var11.setProperty("WorldName", name);
        String[] var12 = new String[this.worlds.length + 1];

        for (newLinkedWorlds = 0; newLinkedWorlds < this.worlds.length; ++newLinkedWorlds)
        {
            var12[newLinkedWorlds] = this.worlds[newLinkedWorlds];
        }

        var12[var12.length - 1] = world;
        String var13 = "";
        String[] var14 = var12;
        int len$ = var12.length;

        for (int i$ = 0; i$ < len$; ++i$)
        {
            String s = var14[i$];
            var13 = var13 + s + "|";
        }

        WDL.baseProps.setProperty("LinkedWorlds", var13);
        WDL.saveProps(world, var11);
        return world;
    }
}
