package wtf.nebula.impl.wdl.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import wtf.nebula.impl.wdl.WDL;

public class GuiWDLWorld extends GuiScreen
{
    private String title = "";
    private GuiScreen parent;
    private GuiButton gameModeBtn;
    private GuiButton timeBtn;
    private GuiButton weatherBtn;
    private GuiButton spawnBtn;
    private GuiButton pickSpawnBtn;
    private boolean showSpawnFields = false;
    private GuiTextField spawnX;
    private GuiTextField spawnY;
    private GuiTextField spawnZ;
    private int spawnTextY;

    public GuiWDLWorld(GuiScreen parent)
    {
        this.parent = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.title = "World Options for " + WDL.baseFolderName.replace('@', ':');
        int w = this.width / 2;
        int h = this.height / 4;
        int hi = h - 15;
        this.gameModeBtn = new GuiButton(1, w - 100, hi, "Game Mode: ERROR");
        this.buttonList.add(this.gameModeBtn);
        this.updateGameMode(false);
        hi += 22;
        this.timeBtn = new GuiButton(2, w - 100, hi, "Time: ERROR");
        this.buttonList.add(this.timeBtn);
        this.updateTime(false);
        hi += 22;
        this.weatherBtn = new GuiButton(3, w - 100, hi, "Weather: ERROR");
        this.buttonList.add(this.weatherBtn);
        this.updateWeather(false);
        hi += 22;
        this.spawnBtn = new GuiButton(4, w - 100, hi, "Spawn Position: ERROR");
        this.buttonList.add(this.spawnBtn);
        hi += 22;
        this.spawnTextY = hi + 4;
        this.spawnX = new GuiTextField(this.fontRenderer, w - 87, hi, 50, 16);
        this.spawnY = new GuiTextField(this.fontRenderer, w - 19, hi, 50, 16);
        this.spawnZ = new GuiTextField(this.fontRenderer, w + 48, hi, 50, 16);
        this.spawnX.setMaxStringLength(7);
        this.spawnY.setMaxStringLength(7);
        this.spawnZ.setMaxStringLength(7);
        hi += 18;
        this.pickSpawnBtn = new GuiButton(5, w - 0, hi, 100, 20, "Current position");
        this.buttonList.add(this.pickSpawnBtn);
        this.updateSpawn(false);
        this.updateSpawnXYZ(false);
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
                this.updateGameMode(true);
            }
            else if (guibutton.id == 2)
            {
                this.updateTime(true);
            }
            else if (guibutton.id == 3)
            {
                this.updateWeather(true);
            }
            else if (guibutton.id == 4)
            {
                this.updateSpawn(true);
            }
            else if (guibutton.id == 5)
            {
                this.pickSpawn();
            }
            else if (guibutton.id == 100)
            {
                if (this.showSpawnFields)
                {
                    this.updateSpawnXYZ(true);
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

        if (this.showSpawnFields)
        {
            this.spawnX.mouseClicked(i, j, k);
            this.spawnY.mouseClicked(i, j, k);
            this.spawnZ.mouseClicked(i, j, k);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
        this.spawnX.textboxKeyTyped(c, i);
        this.spawnY.textboxKeyTyped(c, i);
        this.spawnZ.textboxKeyTyped(c, i);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.spawnX.updateCursorCounter();
        this.spawnY.updateCursorCounter();
        this.spawnZ.updateCursorCounter();
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int i, int j, float f)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, this.height / 4 - 40, 16777215);

        if (this.showSpawnFields)
        {
            this.drawString(this.fontRenderer, "X:", this.width / 2 - 99, this.spawnTextY, 16777215);
            this.drawString(this.fontRenderer, "Y:", this.width / 2 - 31, this.spawnTextY, 16777215);
            this.drawString(this.fontRenderer, "Z:", this.width / 2 + 37, this.spawnTextY, 16777215);
            this.spawnX.drawTextBox();
            this.spawnY.drawTextBox();
            this.spawnZ.drawTextBox();
        }

        super.drawScreen(i, j, f);
    }

    private void updateGameMode(boolean btnClicked)
    {
        String gameType = WDL.baseProps.getProperty("GameType");

        if (gameType.equals("keep"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("GameType", "creative");
                this.updateGameMode(false);
            }
            else
            {
                this.gameModeBtn.displayString = "Game Mode: Don\'t change";
            }
        }
        else if (gameType.equals("creative"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("GameType", "survival");
                this.updateGameMode(false);
            }
            else
            {
                this.gameModeBtn.displayString = "Game Mode: Creative";
            }
        }
        else if (gameType.equals("survival"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("GameType", "hardcore");
                this.updateGameMode(false);
            }
            else
            {
                this.gameModeBtn.displayString = "Game Mode: Survival";
            }
        }
        else if (gameType.equals("hardcore"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("GameType", "keep");
                this.updateGameMode(false);
            }
            else
            {
                this.gameModeBtn.displayString = "Game Mode: Survival Hardcore";
            }
        }
    }

    private void updateTime(boolean btnClicked)
    {
        String time = WDL.baseProps.getProperty("Time");

        if (time.equals("keep"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Time", "23000");
                this.updateTime(false);
            }
            else
            {
                this.timeBtn.displayString = "Time: Don\'t change";
            }
        }
        else if (time.equals("23000"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Time", "0");
                this.updateTime(false);
            }
            else
            {
                this.timeBtn.displayString = "Time: Sunrise";
            }
        }
        else if (time.equals("0"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Time", "6000");
                this.updateTime(false);
            }
            else
            {
                this.timeBtn.displayString = "Time: Morning";
            }
        }
        else if (time.equals("6000"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Time", "11500");
                this.updateTime(false);
            }
            else
            {
                this.timeBtn.displayString = "Time: Noon";
            }
        }
        else if (time.equals("11500"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Time", "12500");
                this.updateTime(false);
            }
            else
            {
                this.timeBtn.displayString = "Time: Evening";
            }
        }
        else if (time.equals("12500"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Time", "18000");
                this.updateTime(false);
            }
            else
            {
                this.timeBtn.displayString = "Time: Sunset";
            }
        }
        else if (time.equals("18000"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Time", "keep");
                this.updateTime(false);
            }
            else
            {
                this.timeBtn.displayString = "Time: Midnight";
            }
        }
    }

    private void updateWeather(boolean btnClicked)
    {
        String weather = WDL.baseProps.getProperty("Weather");

        if (weather.equals("keep"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Weather", "sunny");
                this.updateWeather(false);
            }
            else
            {
                this.weatherBtn.displayString = "Weather: Don\'t change";
            }
        }
        else if (weather.equals("sunny"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Weather", "rain");
                this.updateWeather(false);
            }
            else
            {
                this.weatherBtn.displayString = "Weather: Sunny";
            }
        }
        else if (weather.equals("rain"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Weather", "thunderstorm");
                this.updateWeather(false);
            }
            else
            {
                this.weatherBtn.displayString = "Weather: Rain";
            }
        }
        else if (weather.equals("thunderstorm"))
        {
            if (btnClicked)
            {
                WDL.baseProps.setProperty("Weather", "keep");
                this.updateWeather(false);
            }
            else
            {
                this.weatherBtn.displayString = "Weather: Thunderstorm";
            }
        }
    }

    private void updateSpawn(boolean btnClicked)
    {
        String spawn = WDL.worldProps.getProperty("Spawn");
        this.showSpawnFields = false;
        this.pickSpawnBtn.drawButton = false;

        if (spawn.equals("auto"))
        {
            if (btnClicked)
            {
                WDL.worldProps.setProperty("Spawn", "player");
                this.updateSpawn(false);
            }
            else
            {
                this.spawnBtn.displayString = "Spawn Position: Automatic";
            }
        }
        else if (spawn.equals("player"))
        {
            if (btnClicked)
            {
                WDL.worldProps.setProperty("Spawn", "xyz");
                this.updateSpawn(false);
            }
            else
            {
                this.spawnBtn.displayString = "Spawn Position: Player position";
            }
        }
        else if (spawn.equals("xyz"))
        {
            if (btnClicked)
            {
                WDL.worldProps.setProperty("Spawn", "auto");
                this.updateSpawn(false);
            }
            else
            {
                this.spawnBtn.displayString = "Spawn Position:";
                this.showSpawnFields = true;
                this.pickSpawnBtn.drawButton = true;
            }
        }
    }

    private void updateSpawnXYZ(boolean write)
    {
        if (write)
        {
            try
            {
                int e = Integer.parseInt(this.spawnX.getText());
                int y = Integer.parseInt(this.spawnY.getText());
                int z = Integer.parseInt(this.spawnZ.getText());
                WDL.worldProps.setProperty("SpawnX", String.valueOf(e));
                WDL.worldProps.setProperty("SpawnY", String.valueOf(y));
                WDL.worldProps.setProperty("SpawnZ", String.valueOf(z));
            }
            catch (NumberFormatException var5)
            {
                this.updateSpawn(true);
            }
        }
        else
        {
            this.spawnX.setText(WDL.worldProps.getProperty("SpawnX"));
            this.spawnY.setText(WDL.worldProps.getProperty("SpawnY"));
            this.spawnZ.setText(WDL.worldProps.getProperty("SpawnZ"));
        }
    }

    private void pickSpawn()
    {
        int x = (int)Math.floor(WDL.tp.posX);
        int y = (int)Math.floor(WDL.tp.posY);
        int z = (int)Math.floor(WDL.tp.posZ);
        this.spawnX.setText(String.valueOf(x));
        this.spawnY.setText(String.valueOf(y));
        this.spawnZ.setText(String.valueOf(z));
    }
}
