package wtf.nebula.impl.wdl.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import wtf.nebula.impl.wdl.WDL;

public class GuiWDLGenerator extends GuiScreen
{
    private String title = "";
    private GuiScreen parent;
    private GuiTextField seedField;
    private GuiButton generatorBtn;
    private GuiButton generateStructuresBtn;

    public GuiWDLGenerator(GuiScreen parent)
    {
        this.parent = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.title = "World Generator Options for " + WDL.baseFolderName.replace('@', ':');
        int w = this.width / 2;
        int h = this.height / 4;
        int hi = h - 15;
        this.seedField = new GuiTextField(this.fontRenderer, this.width / 2 - 70, hi, 168, 18);
        this.seedField.setText("ERROR");
        this.updateSeed(false);
        hi += 22;
        this.generatorBtn = new GuiButton(1, w - 100, hi, "World Generator: ERROR");
        this.buttonList.add(this.generatorBtn);
        this.updateGenerator(false);
        hi += 22;
        this.generateStructuresBtn = new GuiButton(2, w - 100, hi, "Generate Structures: ERROR");
        this.buttonList.add(this.generateStructuresBtn);
        this.updateGenerateStructures(false);
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
                this.updateGenerator(true);
            }
            else if (guibutton.id == 2)
            {
                this.updateGenerateStructures(true);
            }
            else if (guibutton.id == 100)
            {
                this.updateSeed(true);
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
        this.seedField.mouseClicked(i, j, k);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
        this.seedField.textboxKeyTyped(c, i);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.seedField.updateCursorCounter();
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int i, int j, float f)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, this.height / 4 - 40, 16777215);
        this.drawString(this.fontRenderer, "Seed:", this.width / 2 - 99, this.height / 4 - 10, 16777215);
        this.seedField.drawTextBox();
        super.drawScreen(i, j, f);
    }

    private void updateGenerator(boolean btnClicked)
    {
        String generatorName = WDL.worldProps.getProperty("GeneratorName");

        if (generatorName.equals("default"))
        {
            if (btnClicked)
            {
                WDL.worldProps.setProperty("GeneratorName", "flat");
                WDL.worldProps.setProperty("GeneratorVersion", "0");
                this.updateGenerator(false);
            }
            else
            {
                this.generatorBtn.displayString = "World Generator: Default";
            }
        }
        else if (btnClicked)
        {
            WDL.worldProps.setProperty("GeneratorName", "default");
            WDL.worldProps.setProperty("GeneratorVersion", "1");
            this.updateGenerator(false);
        }
        else
        {
            this.generatorBtn.displayString = "World Generator: Flat";
        }
    }

    private void updateGenerateStructures(boolean btnClicked)
    {
        String generateStructures = WDL.worldProps.getProperty("MapFeatures");

        if (generateStructures.equals("true"))
        {
            if (btnClicked)
            {
                WDL.worldProps.setProperty("MapFeatures", "false");
                this.updateGenerateStructures(false);
            }
            else
            {
                this.generateStructuresBtn.displayString = "Generate Structures: ON";
            }
        }
        else if (btnClicked)
        {
            WDL.worldProps.setProperty("MapFeatures", "true");
            this.updateGenerateStructures(false);
        }
        else
        {
            this.generateStructuresBtn.displayString = "Generate Structures: OFF";
        }
    }

    private void updateSeed(boolean write)
    {
        if (write)
        {
            WDL.worldProps.setProperty("RandomSeed", this.seedField.getText());
        }
        else
        {
            this.seedField.setText(WDL.worldProps.getProperty("RandomSeed"));
        }
    }
}
