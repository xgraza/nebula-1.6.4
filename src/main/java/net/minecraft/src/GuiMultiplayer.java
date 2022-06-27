package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import org.lwjgl.input.Keyboard;

public class GuiMultiplayer extends GuiScreen
{
    /** Number of outstanding ThreadPollServers threads */
    private static int threadsPending;

    /** Lock object for use with synchronized() */
    private static Object lock = new Object();

    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    private GuiScreen parentScreen;

    /** Slot container for the server list */
    private GuiSlotServer serverSlotContainer;
    private ServerList internetServerList;

    /** Index of the currently selected server */
    private int selectedServer = -1;
    private GuiButton field_96289_p;

    /** The 'Join Server' button */
    private GuiButton buttonSelect;

    /** The 'Delete' button */
    private GuiButton buttonDelete;

    /** The 'Delete' button was clicked */
    private boolean deleteClicked;

    /** The 'Add server' button was clicked */
    private boolean addClicked;

    /** The 'Edit' button was clicked */
    private boolean editClicked;

    /** The 'Direct Connect' button was clicked */
    private boolean directClicked;

    /** This GUI's lag tooltip text or null if no lag icon is being hovered. */
    private String lagTooltip;

    /** Instance of ServerData. */
    private ServerData theServerData;
    private LanServerList localNetworkServerList;
    private ThreadLanServerFind localServerFindThread;

    /** How many ticks this Gui is already opened */
    private int ticksOpened;
    private boolean field_74024_A;
    private List listofLanServers = Collections.emptyList();

    public GuiMultiplayer(GuiScreen par1GuiScreen)
    {
        this.parentScreen = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (!this.field_74024_A)
        {
            this.field_74024_A = true;
            this.internetServerList = new ServerList(this.mc);
            this.internetServerList.loadServerList();
            this.localNetworkServerList = new LanServerList();

            try
            {
                this.localServerFindThread = new ThreadLanServerFind(this.localNetworkServerList);
                this.localServerFindThread.start();
            }
            catch (Exception var2)
            {
                this.mc.getLogAgent().logWarning("Unable to start LAN server detection: " + var2.getMessage());
            }

            this.serverSlotContainer = new GuiSlotServer(this);
        }
        else
        {
            this.serverSlotContainer.func_77207_a(this.width, this.height, 32, this.height - 64);
        }

        this.initGuiControls();
    }

    /**
     * Populate the GuiScreen controlList
     */
    public void initGuiControls()
    {
        this.buttonList.add(this.field_96289_p = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.getString("selectServer.edit")));
        this.buttonList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.getString("selectServer.delete")));
        this.buttonList.add(this.buttonSelect = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.getString("selectServer.select")));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.getString("selectServer.direct")));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.getString("selectServer.add")));
        this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.getString("selectServer.refresh")));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.getString("gui.cancel")));
        boolean var1 = this.selectedServer >= 0 && this.selectedServer < this.serverSlotContainer.getSize();
        this.buttonSelect.enabled = var1;
        this.field_96289_p.enabled = var1;
        this.buttonDelete.enabled = var1;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.ticksOpened;

        if (this.localNetworkServerList.getWasUpdated())
        {
            this.listofLanServers = this.localNetworkServerList.getLanServers();
            this.localNetworkServerList.setWasNotUpdated();
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (this.localServerFindThread != null)
        {
            this.localServerFindThread.interrupt();
            this.localServerFindThread = null;
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 2)
            {
                String var2 = this.internetServerList.getServerData(this.selectedServer).serverName;

                if (var2 != null)
                {
                    this.deleteClicked = true;
                    String var3 = I18n.getString("selectServer.deleteQuestion");
                    String var4 = "\'" + var2 + "\' " + I18n.getString("selectServer.deleteWarning");
                    String var5 = I18n.getString("selectServer.deleteButton");
                    String var6 = I18n.getString("gui.cancel");
                    GuiYesNo var7 = new GuiYesNo(this, var3, var4, var5, var6, this.selectedServer);
                    this.mc.displayGuiScreen(var7);
                }
            }
            else if (par1GuiButton.id == 1)
            {
                this.joinServer(this.selectedServer);
            }
            else if (par1GuiButton.id == 4)
            {
                this.directClicked = true;
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.theServerData = new ServerData(I18n.getString("selectServer.defaultName"), "")));
            }
            else if (par1GuiButton.id == 3)
            {
                this.addClicked = true;
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.theServerData = new ServerData(I18n.getString("selectServer.defaultName"), "")));
            }
            else if (par1GuiButton.id == 7)
            {
                this.editClicked = true;
                ServerData var8 = this.internetServerList.getServerData(this.selectedServer);
                this.theServerData = new ServerData(var8.serverName, var8.serverIP);
                this.theServerData.setHideAddress(var8.isHidingAddress());
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.theServerData));
            }
            else if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (par1GuiButton.id == 8)
            {
                this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
            }
            else
            {
                this.serverSlotContainer.actionPerformed(par1GuiButton);
            }
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (this.deleteClicked)
        {
            this.deleteClicked = false;

            if (par1)
            {
                this.internetServerList.removeServerData(par2);
                this.internetServerList.saveServerList();
                this.selectedServer = -1;
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.directClicked)
        {
            this.directClicked = false;

            if (par1)
            {
                this.connectToServer(this.theServerData);
            }
            else
            {
                this.mc.displayGuiScreen(this);
            }
        }
        else if (this.addClicked)
        {
            this.addClicked = false;

            if (par1)
            {
                this.internetServerList.addServerData(this.theServerData);
                this.internetServerList.saveServerList();
                this.selectedServer = -1;
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.editClicked)
        {
            this.editClicked = false;

            if (par1)
            {
                ServerData var3 = this.internetServerList.getServerData(this.selectedServer);
                var3.serverName = this.theServerData.serverName;
                var3.serverIP = this.theServerData.serverIP;
                var3.setHideAddress(this.theServerData.isHidingAddress());
                this.internetServerList.saveServerList();
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        int var3 = this.selectedServer;

        if (par2 == 59)
        {
            this.mc.gameSettings.hideServerAddress = !this.mc.gameSettings.hideServerAddress;
            this.mc.gameSettings.saveOptions();
        }
        else
        {
            if (isShiftKeyDown() && par2 == 200)
            {
                if (var3 > 0 && var3 < this.internetServerList.countServers())
                {
                    this.internetServerList.swapServers(var3, var3 - 1);
                    --this.selectedServer;

                    if (var3 < this.internetServerList.countServers() - 1)
                    {
                        this.serverSlotContainer.func_77208_b(-this.serverSlotContainer.slotHeight);
                    }
                }
            }
            else if (isShiftKeyDown() && par2 == 208)
            {
                if (var3 >= 0 & var3 < this.internetServerList.countServers() - 1)
                {
                    this.internetServerList.swapServers(var3, var3 + 1);
                    ++this.selectedServer;

                    if (var3 > 0)
                    {
                        this.serverSlotContainer.func_77208_b(this.serverSlotContainer.slotHeight);
                    }
                }
            }
            else if (par2 != 28 && par2 != 156)
            {
                super.keyTyped(par1, par2);
            }
            else
            {
                this.actionPerformed((GuiButton)this.buttonList.get(2));
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.lagTooltip = null;
        this.drawDefaultBackground();
        this.serverSlotContainer.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("multiplayer.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);

        if (this.lagTooltip != null)
        {
            this.func_74007_a(this.lagTooltip, par1, par2);
        }
    }

    /**
     * Join server by slot index
     */
    private void joinServer(int par1)
    {
        if (par1 < this.internetServerList.countServers())
        {
            this.connectToServer(this.internetServerList.getServerData(par1));
        }
        else
        {
            par1 -= this.internetServerList.countServers();

            if (par1 < this.listofLanServers.size())
            {
                LanServer var2 = (LanServer)this.listofLanServers.get(par1);
                this.connectToServer(new ServerData(var2.getServerMotd(), var2.getServerIpPort()));
            }
        }
    }

    private void connectToServer(ServerData par1ServerData)
    {
        this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, par1ServerData));
    }

    private static void func_74017_b(ServerData par0ServerData) throws IOException
    {
        ServerAddress var1 = ServerAddress.func_78860_a(par0ServerData.serverIP);
        Socket var2 = null;
        DataInputStream var3 = null;
        DataOutputStream var4 = null;

        try
        {
            var2 = new Socket();
            var2.setSoTimeout(3000);
            var2.setTcpNoDelay(true);
            var2.setTrafficClass(18);
            var2.connect(new InetSocketAddress(var1.getIP(), var1.getPort()), 3000);
            var3 = new DataInputStream(var2.getInputStream());
            var4 = new DataOutputStream(var2.getOutputStream());
            Packet254ServerPing var5 = new Packet254ServerPing(78, var1.getIP(), var1.getPort());
            var4.writeByte(var5.getPacketId());
            var5.writePacketData(var4);

            if (var3.read() != 255)
            {
                throw new IOException("Bad message");
            }

            String var6 = Packet.readString(var3, 256);
            char[] var7 = var6.toCharArray();

            for (int var8 = 0; var8 < var7.length; ++var8)
            {
                if (var7[var8] != 167 && var7[var8] != 0 && ChatAllowedCharacters.allowedCharacters.indexOf(var7[var8]) < 0)
                {
                    var7[var8] = 63;
                }
            }

            var6 = new String(var7);
            int var9;
            int var10;
            String[] var27;

            if (var6.startsWith("\u00a7") && var6.length() > 1)
            {
                var27 = var6.substring(1).split("\u0000");

                if (MathHelper.parseIntWithDefault(var27[0], 0) == 1)
                {
                    par0ServerData.serverMOTD = var27[3];
                    par0ServerData.field_82821_f = MathHelper.parseIntWithDefault(var27[1], par0ServerData.field_82821_f);
                    par0ServerData.gameVersion = var27[2];
                    var9 = MathHelper.parseIntWithDefault(var27[4], 0);
                    var10 = MathHelper.parseIntWithDefault(var27[5], 0);

                    if (var9 >= 0 && var10 >= 0)
                    {
                        par0ServerData.populationInfo = EnumChatFormatting.GRAY + "" + var9 + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + var10;
                    }
                    else
                    {
                        par0ServerData.populationInfo = "" + EnumChatFormatting.DARK_GRAY + "???";
                    }
                }
                else
                {
                    par0ServerData.gameVersion = "???";
                    par0ServerData.serverMOTD = "" + EnumChatFormatting.DARK_GRAY + "???";
                    par0ServerData.field_82821_f = 79;
                    par0ServerData.populationInfo = "" + EnumChatFormatting.DARK_GRAY + "???";
                }
            }
            else
            {
                var27 = var6.split("\u00a7");
                var6 = var27[0];
                var9 = -1;
                var10 = -1;

                try
                {
                    var9 = Integer.parseInt(var27[1]);
                    var10 = Integer.parseInt(var27[2]);
                }
                catch (Exception var25)
                {
                    ;
                }

                par0ServerData.serverMOTD = EnumChatFormatting.GRAY + var6;

                if (var9 >= 0 && var10 > 0)
                {
                    par0ServerData.populationInfo = EnumChatFormatting.GRAY + "" + var9 + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + var10;
                }
                else
                {
                    par0ServerData.populationInfo = "" + EnumChatFormatting.DARK_GRAY + "???";
                }

                par0ServerData.gameVersion = "1.3";
                par0ServerData.field_82821_f = 77;
            }
        }
        finally
        {
            try
            {
                if (var3 != null)
                {
                    var3.close();
                }
            }
            catch (Throwable var24)
            {
                ;
            }

            try
            {
                if (var4 != null)
                {
                    var4.close();
                }
            }
            catch (Throwable var23)
            {
                ;
            }

            try
            {
                if (var2 != null)
                {
                    var2.close();
                }
            }
            catch (Throwable var22)
            {
                ;
            }
        }
    }

    protected void func_74007_a(String par1Str, int par2, int par3)
    {
        if (par1Str != null)
        {
            int var4 = par2 + 12;
            int var5 = par3 - 12;
            int var6 = this.fontRenderer.getStringWidth(par1Str);
            this.drawGradientRect(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
            this.fontRenderer.drawStringWithShadow(par1Str, var4, var5, -1);
        }
    }

    static ServerList getInternetServerList(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.internetServerList;
    }

    static List getListOfLanServers(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.listofLanServers;
    }

    static int getSelectedServer(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.selectedServer;
    }

    static int getAndSetSelectedServer(GuiMultiplayer par0GuiMultiplayer, int par1)
    {
        return par0GuiMultiplayer.selectedServer = par1;
    }

    /**
     * Return buttonSelect GuiButton
     */
    static GuiButton getButtonSelect(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.buttonSelect;
    }

    /**
     * Return buttonEdit GuiButton
     */
    static GuiButton getButtonEdit(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.field_96289_p;
    }

    /**
     * Return buttonDelete GuiButton
     */
    static GuiButton getButtonDelete(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.buttonDelete;
    }

    static void func_74008_b(GuiMultiplayer par0GuiMultiplayer, int par1)
    {
        par0GuiMultiplayer.joinServer(par1);
    }

    static int getTicksOpened(GuiMultiplayer par0GuiMultiplayer)
    {
        return par0GuiMultiplayer.ticksOpened;
    }

    /**
     * Returns the lock object for use with synchronized()
     */
    static Object getLock()
    {
        return lock;
    }

    static int getThreadsPending()
    {
        return threadsPending;
    }

    static int increaseThreadsPending()
    {
        return threadsPending++;
    }

    static void func_82291_a(ServerData par0ServerData) throws IOException
    {
        func_74017_b(par0ServerData);
    }

    static int decreaseThreadsPending()
    {
        return threadsPending--;
    }

    static String getAndSetLagTooltip(GuiMultiplayer par0GuiMultiplayer, String par1Str)
    {
        return par0GuiMultiplayer.lagTooltip = par1Str;
    }
}
