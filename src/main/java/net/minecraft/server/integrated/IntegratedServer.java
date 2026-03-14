package net.minecraft.server.integrated;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.crash.CrashReport;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.WorldServerMultiOF;
import net.minecraft.src.WorldServerOF;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.*;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class IntegratedServer extends MinecraftServer
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * The Minecraft instance.
     */
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;
    private static final String __OBFID = "CL_00001129";

    public IntegratedServer(Minecraft par1Minecraft, String par2Str, String par3Str, WorldSettings par4WorldSettings)
    {
        super(new File(par1Minecraft.mcDataDir, "saves"), par1Minecraft.getProxy());
        this.setServerOwner(par1Minecraft.getSession().getUsername());
        this.setFolderName(par2Str);
        this.setWorldName(par3Str);
        this.setDemo(par1Minecraft.isDemo());
        this.canCreateBonusChest(par4WorldSettings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigurationManager(new IntegratedPlayerList(this));
        this.mc = par1Minecraft;
        this.theWorldSettings = par4WorldSettings;
    }

    protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str)
    {
        this.convertMapIfNeeded(par1Str);
        ISaveHandler var7 = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);

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
                    this.worldServers[var15] = new DemoWorldServer(this, var7, par2Str, var16, this.theProfiler);
                } else
                {
                    this.worldServers[var15] = new WorldServerOF(this, var7, par2Str, var16, this.theWorldSettings, this.theProfiler);
                }
            } else
            {
                this.worldServers[var15] = new WorldServerMultiOF(this, var7, par2Str, var16, this.theWorldSettings, this.worldServers[0], this.theProfiler);
            }

            this.worldServers[var15].addWorldAccess(new WorldManager(this, this.worldServers[var15]));
            this.getConfigurationManager().setPlayerManager(this.worldServers);
        }

        this.func_147139_a(this.func_147135_j());
        this.initialWorldChunkLoad();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws IOException
    {
        logger.info("Starting integrated minecraft server version 1.7.2");
        this.setOnlineMode(false);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        logger.info("Generating keypair");
        this.setKeyPair(CryptManager.createNewKeyPair());

        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.func_82749_j());
        this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());

        return true;
    }

    /**
     * Main function called by run() every loop.
     */
    public void tick()
    {
        boolean var1 = this.isGamePaused;
        this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();

        if (!var1 && this.isGamePaused)
        {
            logger.info("Saving and pausing game...");
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

    public WorldSettings.GameType getGameType()
    {
        return this.theWorldSettings.getGameType();
    }

    public EnumDifficulty func_147135_j()
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
        par1CrashReport.getCategory().addCrashSectionCallable("Type", new Callable()
        {
            private static final String __OBFID = "CL_00001130";

            public String call()
            {
                return "Integrated Server (map_client.txt)";
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new Callable()
        {
            private static final String __OBFID = "CL_00001131";

            public String call()
            {
                String var1 = ClientBrandRetriever.getClientModName();

                if (!var1.equals("vanilla"))
                {
                    return "Definitely; Client brand changed to '" + var1 + "'";
                } else
                {
                    var1 = IntegratedServer.this.getServerModName();
                    return !var1.equals("vanilla") ? "Definitely; Server brand changed to '" + var1 + "'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.");
                }
            }
        });
        return par1CrashReport;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(WorldSettings.GameType par1EnumGameType, boolean par2)
    {
        try
        {
            int var6 = -1;

            try
            {
                var6 = HttpUtil.func_76181_a();
            } catch (IOException var5)
            {
            }

            if (var6 <= 0)
            {
                var6 = 25564;
            }

            this.func_147137_ag().addLanEndpoint((InetAddress) null, var6);
            logger.info("Started on " + var6);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), var6 + "");
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(par1EnumGameType);
            this.getConfigurationManager().setCommandsAllowedForAll(par2);
            return var6 + "";
        } catch (IOException var61)
        {
            return null;
        }
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
    public void setGameType(WorldSettings.GameType par1EnumGameType)
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
}
