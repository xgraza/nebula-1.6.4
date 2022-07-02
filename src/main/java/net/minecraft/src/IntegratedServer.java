package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import net.minecraft.server.MinecraftServer;

public class IntegratedServer extends MinecraftServer
{
    /** The Minecraft instance. */
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private final ILogAgent serverLogAgent;

    /** Instance of IntegratedServerListenThread. */
    private IntegratedServerListenThread theServerListeningThread;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;

    public IntegratedServer(Minecraft par1Minecraft, String par2Str, String par3Str, WorldSettings par4WorldSettings)
    {
        super(new File(par1Minecraft.mcDataDir, "saves"));
        this.serverLogAgent = new LogAgent("Minecraft-Server", " [SERVER]", (new File(par1Minecraft.mcDataDir, "output-server.log")).getAbsolutePath());
        this.setServerOwner(par1Minecraft.getSession().getUsername());
        this.setFolderName(par2Str);
        this.setWorldName(par3Str);
        this.setDemo(par1Minecraft.isDemo());
        this.canCreateBonusChest(par4WorldSettings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigurationManager(new IntegratedPlayerList(this));
        this.mc = par1Minecraft;
        this.serverProxy = par1Minecraft.getProxy();
        this.theWorldSettings = par4WorldSettings;
        Reflector.callVoid(Reflector.ModLoader_registerServer, new Object[] {this});

        try
        {
            this.theServerListeningThread = new IntegratedServerListenThread(this);
        }
        catch (IOException var6)
        {
            throw new Error();
        }
    }

    protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str)
    {
        this.convertMapIfNeeded(par1Str);
        ISaveHandler var7 = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);

        if (Reflector.DimensionManager.exists())
        {
            Object var8 = this.isDemo() ? new DemoWorldServer(this, var7, par2Str, 0, this.theProfiler, this.getLogAgent()) : new WorldServerOF(this, var7, par2Str, 0, this.theWorldSettings, this.theProfiler, this.getLogAgent());
            Integer[] var9 = (Integer[])((Integer[])Reflector.call(Reflector.DimensionManager_getStaticDimensionIDs, new Object[0]));
            Integer[] var10 = var9;
            int var11 = var9.length;

            for (int var12 = 0; var12 < var11; ++var12)
            {
                int var13 = var10[var12].intValue();
                Object var14 = var13 == 0 ? var8 : new WorldServerMulti(this, var7, par2Str, var13, this.theWorldSettings, (WorldServer)var8, this.theProfiler, this.getLogAgent());
                ((WorldServer)var14).addWorldAccess(new WorldManager(this, (WorldServer)var14));

                if (!this.isSinglePlayer())
                {
                    ((WorldServer)var14).getWorldInfo().setGameType(this.getGameType());
                }

                if (Reflector.EventBus.exists())
                {
                    Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, new Object[] {var14});
                }
            }

            this.getConfigurationManager().setPlayerManager(new WorldServer[] {(WorldServer)var8});
        }
        else
        {
            this.worldServers = new WorldServer[3];
            this.timeOfLastDimensionTick = new long[this.worldServers.length][100];

            for (int var15 = 0; var15 < this.worldServers.length; ++var15)
            {
                byte var16 = 0;

                if (var15 == 1)
                {
                    var16 = -1;
                }

                if (var15 == 2)
                {
                    var16 = 1;
                }

                if (var15 == 0)
                {
                    if (this.isDemo())
                    {
                        this.worldServers[var15] = new DemoWorldServer(this, var7, par2Str, var16, this.theProfiler, this.getLogAgent());
                    }
                    else
                    {
                        this.worldServers[var15] = new WorldServerOF(this, var7, par2Str, var16, this.theWorldSettings, this.theProfiler, this.getLogAgent());
                    }
                }
                else
                {
                    this.worldServers[var15] = new WorldServerMulti(this, var7, par2Str, var16, this.theWorldSettings, this.worldServers[0], this.theProfiler, this.getLogAgent());
                }

                this.worldServers[var15].addWorldAccess(new WorldManager(this, this.worldServers[var15]));
                this.getConfigurationManager().setPlayerManager(this.worldServers);
            }
        }

        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws IOException
    {
        this.serverLogAgent.logInfo("Starting integrated minecraft server version 1.6.4");
        this.setOnlineMode(false);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        this.serverLogAgent.logInfo("Generating keypair");
        this.setKeyPair(CryptManager.createNewKeyPair());
        Object var1;

        if (Reflector.FMLCommonHandler_handleServerAboutToStart.exists())
        {
            var1 = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);

            if (!Reflector.callBoolean(var1, Reflector.FMLCommonHandler_handleServerAboutToStart, new Object[] {this}))
            {
                return false;
            }
        }

        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.func_82749_j());
        this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());

        if (Reflector.FMLCommonHandler_handleServerStarting.exists())
        {
            var1 = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);

            if (Reflector.FMLCommonHandler_handleServerStarting.getReturnType() == Boolean.TYPE)
            {
                return Reflector.callBoolean(var1, Reflector.FMLCommonHandler_handleServerStarting, new Object[] {this});
            }

            Reflector.callVoid(var1, Reflector.FMLCommonHandler_handleServerStarting, new Object[] {this});
        }

        return true;
    }

    /**
     * Main function called by run() every loop.
     */
    public void tick()
    {
        boolean var1 = this.isGamePaused;
        this.isGamePaused = this.theServerListeningThread.isGamePaused();

        if (!var1 && this.isGamePaused)
        {
            this.serverLogAgent.logInfo("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (!this.isGamePaused)
        {
            super.tick();
        }
    }

    public boolean canStructuresSpawn()
    {
        return false;
    }

    public EnumGameType getGameType()
    {
        return this.theWorldSettings.getGameType();
    }

    /**
     * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
     */
    public int getDifficulty()
    {
        return this.mc.gameSettings.difficulty;
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.theWorldSettings.getHardcoreEnabled();
    }

    protected File getDataDirectory()
    {
        return this.mc.mcDataDir;
    }

    public boolean isDedicatedServer()
    {
        return false;
    }

    /**
     * Gets the IntergratedServerListenThread.
     */
    public IntegratedServerListenThread getServerListeningThread()
    {
        return this.theServerListeningThread;
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport par1CrashReport)
    {
        this.mc.crashed(par1CrashReport);
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
    {
        par1CrashReport = super.addServerInfoToCrashReport(par1CrashReport);
        par1CrashReport.getCategory().addCrashSectionCallable("Type", new CallableType3(this));
        par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new CallableIsModded(this));
        return par1CrashReport;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        super.addServerStatsToSnooper(par1PlayerUsageSnooper);
        par1PlayerUsageSnooper.addData("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(EnumGameType par1EnumGameType, boolean par2)
    {
        try
        {
            String var3 = this.theServerListeningThread.func_71755_c();
            this.getLogAgent().logInfo("Started on " + var3);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), var3);
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(par1EnumGameType);
            this.getConfigurationManager().setCommandsAllowedForAll(par2);
            return var3;
        }
        catch (IOException var4)
        {
            return null;
        }
    }

    public ILogAgent getLogAgent()
    {
        return this.serverLogAgent;
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer()
    {
        super.stopServer();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown()
    {
        super.initiateShutdown();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Returns true if this integrated server is open to LAN
     */
    public boolean getPublic()
    {
        return this.isPublic;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(EnumGameType par1EnumGameType)
    {
        this.getConfigurationManager().setGameType(par1EnumGameType);
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled()
    {
        return true;
    }

    public int func_110455_j()
    {
        return 4;
    }

    public NetworkListenThread getNetworkThread()
    {
        return this.getServerListeningThread();
    }
}
