package net.minecraft.wdl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockNote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;

public class WDL
{
    public static boolean DEBUG = false;
    public static Minecraft mc = (Minecraft)stealAndGetField(Minecraft.class, Minecraft.class);
    public static WorldClient wc;
    public static NetworkManager nm = null;
    public static EntityClientPlayerMP tp;
    public static McoServer mcos;
    public static Container windowContainer;
    public static int lastX = 0;
    public static int lastY = 0;
    public static int lastZ = 0;
    public static Entity lastEntity;
    public static SaveHandler saveHandler;
    public static IChunkLoader chunkLoader;
    public static HashSet<ChunkPosition> newTileEntities = new HashSet();
    public static GuiScreen guiToShowAsync = null;
    public static boolean downloading = false;
    public static boolean isMultiworld = false;
    public static boolean propsFound = false;
    public static boolean startOnChange = false;
    public static boolean saving = false;
    public static boolean worldLoadingDeferred = false;
    public static String worldName = "WorldDownloaderERROR";
    public static String baseFolderName = "WorldDownloaderERROR";
    public static Properties baseProps;
    public static Properties worldProps;
    public static Properties defaultProps = new Properties();

    public static void start()
    {
        wc = mc.theWorld;

        if (isMultiworld && worldName.isEmpty())
        {
            guiToShowAsync = new GuiWDLMultiworldSelect((GuiScreen)null);
        }
        else if (!propsFound)
        {
            guiToShowAsync = new GuiWDLMultiworld((GuiScreen)null);
        }
        else
        {
            worldProps = loadWorldProps(worldName);
            saveHandler = (SaveHandler)mc.getSaveLoader().getSaveLoader(getWorldFolderName(worldName), true);
            chunkLoader = saveHandler.getChunkLoader(wc.provider);
            newTileEntities = new HashSet();

            if (baseProps.getProperty("ServerName").isEmpty())
            {
                baseProps.setProperty("ServerName", getServerName());
            }

            startOnChange = true;
            downloading = true;
            chatMsg("Download started");
        }
    }

    public static void stop()
    {
        if (downloading)
        {
            downloading = false;
            startOnChange = false;
            chatMsg("Download stopped");
            startSaveThread();
        }
    }

    private static void startSaveThread()
    {
        chatMsg("Save started.");
        saving = true;
        WDLSaveAsync saver = new WDLSaveAsync();
        Thread thread = new Thread(saver, "WDL Save Thread");
        thread.start();
    }

    public static void onWorldLoad()
    {
        if (!mc.isIntegratedServerRunning())
        {
            if (downloading)
            {
                if (!saving)
                {
                    chatMsg("World change detected. Download will start once current save completes.");
                    startSaveThread();
                }
            }
            else
            {
                loadWorld();
            }
        }
    }

    public static void loadWorld()
    {
        worldName = "";
        wc = mc.theWorld;
        tp = mc.thePlayer;
        windowContainer = tp.openContainer;
        NetworkManager newNM = tp.sendQueue.getNetworkManager();

        if (nm != newNM)
        {
            chatDebug("onWorldLoad: different server!");
            nm = newNM;
            loadBaseProps();

            if (baseProps.getProperty("AutoStart").equals("true"))
            {
                start();
            }
            else
            {
                startOnChange = false;
            }
        }
        else
        {
            chatDebug("onWorldLoad: same server!");

            if (startOnChange)
            {
                start();
            }
        }
    }

    public static void onWorldUnload() {}

    public static void onSaveComplete()
    {
        mc.getSaveLoader().flushCache();
        saveHandler.flush();
        wc = null;

        if (downloading)
        {
            chatMsg("Save complete. Starting download again.");
            loadWorld();
        }
        else
        {
            chatMsg("Save complete. Your single player file is ready to play!");
        }
    }

    public static void onChunkNoLongerNeeded(Chunk unneededChunk)
    {
        if (unneededChunk != null && unneededChunk.isModified)
        {
            chatDebug("onChunkNoLongerNeeded: " + unneededChunk.xPosition + ", " + unneededChunk.zPosition);
            saveChunk(unneededChunk);
        }
    }

    public static void onItemGuiOpened()
    {
        if (mc.objectMouseOver != null)
        {
            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
            {
                lastEntity = mc.objectMouseOver.entityHit;
            }
            else
            {
                lastEntity = null;
                lastX = mc.objectMouseOver.blockX;
                lastY = mc.objectMouseOver.blockY;
                lastZ = mc.objectMouseOver.blockZ;
            }
        }
    }

    public static void onItemGuiClosed()
    {
        String saveName = "";
        int inventorySize;

        if (lastEntity != null)
        {
            if (lastEntity instanceof EntityMinecart && windowContainer instanceof ContainerChest)
            {
                EntityMinecart var8 = (EntityMinecart)lastEntity;

                if (var8 instanceof EntityMinecartChest)
                {
                    EntityMinecartChest var11 = (EntityMinecartChest)var8;

                    for (inventorySize = 0; inventorySize < var11.getSizeInventory(); ++inventorySize)
                    {
                        var11.setInventorySlotContents(inventorySize, windowContainer.getSlot(inventorySize).getStack());
                        saveName = "Storage Minecart contents";
                    }
                }
            }
            else
            {
                if (lastEntity instanceof EntityVillager && windowContainer instanceof ContainerMerchant)
                {
                    EntityVillager var7 = (EntityVillager)lastEntity;
                    chatDebug("Saving villager offers is not yet supported.");
                    saveName = "Villager offers";
                    return;
                }

                chatMsg("Unsupported entity cannot be saved:" + EntityList.getEntityString(lastEntity));
            }

            chatDebug("Saved " + saveName + ".");
        }
        else
        {
            TileEntity te = wc.getTileEntity(lastX, lastY, lastZ);

            if (te == null)
            {
                chatDebug("onItemGuiClosed could not get TE at " + lastX + " " + lastY + " " + lastZ);
            }
            else
            {
                if (windowContainer instanceof ContainerChest && te instanceof TileEntityChest)
                {
                    if (windowContainer.inventorySlots.size() > 63)
                    {
                        ChunkPosition var10 = new ChunkPosition(lastX, lastY, lastZ);
                        TileEntityChest tec2;
                        TileEntity var9;
                        ChunkPosition var12;
                        TileEntityChest var13;

                        if ((var9 = wc.getTileEntity(lastX, lastY, lastZ + 1)) instanceof TileEntityChest && ((TileEntityChest)var9).func_145980_j() == ((TileEntityChest)te).func_145980_j())
                        {
                            var13 = (TileEntityChest)te;
                            tec2 = (TileEntityChest)var9;
                            var12 = new ChunkPosition(lastX, lastY, lastZ + 1);
                        }
                        else if ((var9 = wc.getTileEntity(lastX, lastY, lastZ - 1)) instanceof TileEntityChest && ((TileEntityChest)var9).func_145980_j() == ((TileEntityChest)te).func_145980_j())
                        {
                            var13 = (TileEntityChest)var9;
                            tec2 = (TileEntityChest)te;
                            var12 = new ChunkPosition(lastX, lastY, lastZ - 1);
                        }
                        else if ((var9 = wc.getTileEntity(lastX + 1, lastY, lastZ)) instanceof TileEntityChest && ((TileEntityChest)var9).func_145980_j() == ((TileEntityChest)te).func_145980_j())
                        {
                            var13 = (TileEntityChest)te;
                            tec2 = (TileEntityChest)var9;
                            var12 = new ChunkPosition(lastX + 1, lastY, lastZ);
                        }
                        else
                        {
                            if (!((var9 = wc.getTileEntity(lastX - 1, lastY, lastZ)) instanceof TileEntityChest) || ((TileEntityChest)var9).func_145980_j() != ((TileEntityChest)te).func_145980_j())
                            {
                                chatMsg("Could not save this chest!");
                                return;
                            }

                            var13 = (TileEntityChest)var9;
                            tec2 = (TileEntityChest)te;
                            var12 = new ChunkPosition(lastX - 1, lastY, lastZ);
                        }

                        copyItemStacks(windowContainer, var13, 0);
                        copyItemStacks(windowContainer, tec2, 27);
                        newTileEntities.add(var10);
                        newTileEntities.add(var12);
                        saveName = "Double Chest contents";
                    }
                    else
                    {
                        copyItemStacks(windowContainer, (TileEntityChest)te, 0);
                        newTileEntities.add(new ChunkPosition(lastX, lastY, lastZ));
                        saveName = "Chest contents";
                    }
                }
                else if (windowContainer instanceof ContainerChest && te instanceof TileEntityEnderChest)
                {
                    InventoryEnderChest inventoryEnderChest = tp.getInventoryEnderChest();
                    inventorySize = inventoryEnderChest.getSizeInventory();
                    int containerSize = windowContainer.inventorySlots.size();

                    for (int i = 0; i < containerSize && i < inventorySize; ++i)
                    {
                        inventoryEnderChest.setInventorySlotContents(i, windowContainer.getSlot(i).getStack());
                    }

                    saveName = "Ender Chest contents";
                }
                else if (windowContainer instanceof ContainerBrewingStand)
                {
                    copyItemStacks(windowContainer, (TileEntityBrewingStand)te, 0);
                    newTileEntities.add(new ChunkPosition(lastX, lastY, lastZ));
                    saveName = "Brewing Stand contents";
                }
                else if (windowContainer instanceof ContainerDispenser)
                {
                    copyItemStacks(windowContainer, (TileEntityDispenser)te, 0);
                    newTileEntities.add(new ChunkPosition(lastX, lastY, lastZ));
                    saveName = "Dispenser contents";
                }
                else
                {
                    if (!(windowContainer instanceof ContainerFurnace))
                    {
                        chatDebug("onItemGuiClosed unhandled TE: " + te);
                        return;
                    }

                    copyItemStacks(windowContainer, (TileEntityFurnace)te, 0);
                    newTileEntities.add(new ChunkPosition(lastX, lastY, lastZ));
                    saveName = "Furnace contents";
                }

                chatDebug("Saved " + saveName + ".");
            }
        }
    }

    public static void onBlockEvent(int x, int y, int z, Block block, int event, int param)
    {
        if (block == Blocks.noteblock)
        {
            TileEntityNote newTE = new TileEntityNote();
            newTE.field_145879_a = (byte)(param % 25);
            wc.setTileEntity(x, y, z, newTE);
            newTileEntities.add(new ChunkPosition(x, y, z));
            chatDebug("onBlockEvent: Note Block: " + x + " " + y + " " + z + " pitch: " + param + " - " + newTE);
        }
    }

    public static void importTileEntities(Chunk chunk)
    {
        File chunkSaveLocation = (File)stealAndGetField(chunkLoader, File.class);
        DataInputStream dis = RegionFileCache.getChunkInputStream(chunkSaveLocation, chunk.xPosition, chunk.zPosition);

        try
        {
            NBTTagCompound e = CompressedStreamTools.read(dis);
            NBTTagCompound levelNBT = e.getCompoundTag("Level");
            NBTTagList tileEntitiesNBT = levelNBT.getTagList("TileEntities", 10);

            if (tileEntitiesNBT != null)
            {
                for (int i = 0; i < tileEntitiesNBT.tagCount(); ++i)
                {
                    NBTTagCompound tileEntityNBT = tileEntitiesNBT.getCompoundTagAt(i);
                    TileEntity te = TileEntity.createAndLoadEntity(tileEntityNBT);
                    String entityType = null;

                    if ((entityType = isImportableTileEntity(te)) != null)
                    {
                        if (!newTileEntities.contains(new ChunkPosition(te.xCoord, te.yCoord, te.zCoord)))
                        {
                            wc.setTileEntity(te.xCoord, te.yCoord, te.zCoord, te);
                            chatDebug("Loaded TE: " + entityType + " at " + te.xCoord + " " + te.yCoord + " " + te.zCoord);
                        }
                        else
                        {
                            chatDebug("Dropping old TE: " + entityType + " at " + te.xCoord + " " + te.yCoord + " " + te.zCoord);
                        }
                    }
                    else
                    {
                        chatDebug("Old TE is not importable: " + entityType + " at " + te.xCoord + " " + te.yCoord + " " + te.zCoord);
                    }
                }
            }
        }
        catch (Exception var10)
        {
            ;
        }
    }

    public static String isImportableTileEntity(TileEntity te)
    {
        Block block = wc.getBlock(te.xCoord, te.yCoord, te.zCoord);
        return block instanceof BlockChest && te instanceof TileEntityChest ? "TileEntityChest" : (block instanceof BlockDispenser && te instanceof TileEntityDispenser ? "TileEntityDispenser" : (block instanceof BlockFurnace && te instanceof TileEntityFurnace ? "TileEntityFurnace" : (block instanceof BlockNote && te instanceof TileEntityNote ? "TileEntityNote" : (block instanceof BlockBrewingStand && te instanceof TileEntityBrewingStand ? "TileEntityBrewingStand" : null))));
    }

    public static void saveEverything()
    {
        saveProps();

        try
        {
            saveHandler.checkSessionLock();
        }
        catch (MinecraftException var5)
        {
            throw new RuntimeException("WorldDownloader: Couldn\'t get session lock for saving the world!");
        }

        NBTTagCompound playerNBT = new NBTTagCompound();
        tp.writeToNBT(playerNBT);
        applyOverridesToPlayer(playerNBT);
        ISaveHandler saveHAndler = wc.getSaveHandler();
        AnvilSaveConverter saveConverter = (AnvilSaveConverter)mc.getSaveLoader();
        int saveVersion = saveConverter.getSaveVersion();
        wc.getWorldInfo().setSaveVersion(saveVersion);
        NBTTagCompound worldInfoNBT = wc.getWorldInfo().cloneNBTCompound(playerNBT);
        applyOverridesToWorldInfo(worldInfoNBT);
        savePlayer(playerNBT);
        saveWorldInfo(worldInfoNBT);
        saveChunks();
    }

    public static void savePlayer(NBTTagCompound playerNBT)
    {
        chatDebug("Saving player data...");

        try
        {
            File e = new File(saveHandler.getWorldDirectory(), "players");
            File playerFile = new File(e, tp.func_142021_k() + ".dat.tmp");
            File playerFileOld = new File(e, tp.func_142021_k() + ".dat");
            CompressedStreamTools.writeCompressed(playerNBT, new FileOutputStream(playerFile));

            if (playerFileOld.exists())
            {
                playerFileOld.delete();
            }

            playerFile.renameTo(playerFileOld);
        }
        catch (Exception var4)
        {
            throw new RuntimeException("Couldn\'t save the player!");
        }

        chatDebug("Player data saved.");
    }

    public static void saveWorldInfo(NBTTagCompound worldInfoNBT)
    {
        chatDebug("Saving world metadata...");
        File saveDirectory = saveHandler.getWorldDirectory();
        NBTTagCompound dataNBT = new NBTTagCompound();
        dataNBT.setTag("Data", worldInfoNBT);

        try
        {
            File e = new File(saveDirectory, "level.dat_new");
            File dataFileBackup = new File(saveDirectory, "level.dat_old");
            File dataFileOld = new File(saveDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(dataNBT, new FileOutputStream(e));

            if (dataFileBackup.exists())
            {
                dataFileBackup.delete();
            }

            dataFileOld.renameTo(dataFileBackup);

            if (dataFileOld.exists())
            {
                dataFileOld.delete();
            }

            e.renameTo(dataFileOld);

            if (e.exists())
            {
                e.delete();
            }
        }
        catch (Exception var6)
        {
            throw new RuntimeException("Couldn\'t save the world metadata!");
        }

        chatDebug("World data saved.");
    }

    public static void saveChunks()
    {
        chatDebug("Saving chunks...");
        LongHashMap longHM = (LongHashMap)stealAndGetField(wc.getChunkProvider(), LongHashMap.class);
        LongHashMap.Entry[] hashArray = (LongHashMap.Entry[])((LongHashMap.Entry[])((LongHashMap.Entry[])stealAndGetField(longHM, LongHashMap.Entry[].class)));
        WDLSaveProgressReporter progressReporter = new WDLSaveProgressReporter();
        progressReporter.start();
        int length = hashArray.length;

        for (int i = 0; i < length; ++i)
        {
            for (LongHashMap.Entry entry = hashArray[i]; entry != null; entry = entry.nextEntry)
            {
                Chunk c = (Chunk)entry.getValue();

                if (c != null && c.isModified)
                {
                    saveChunk(c);

                    try
                    {
                        ThreadedFileIOBase.threadedIOInstance.waitForFinish();
                    }
                    catch (Exception var8)
                    {
                        chatMsg("Threw exception waiting for asynchronous IO to finish. Hmmm.");
                    }
                }
                else
                {
                    chatMsg("Didn\'t save chunk " + c.xPosition + " " + c.zPosition + " because isModified is false!");
                }
            }
        }

        chatDebug("Chunk data saved.");
    }

    public static void saveChunk(Chunk c)
    {
        importTileEntities(c);
        c.isTerrainPopulated = true;

        try
        {
            chunkLoader.saveChunk(wc, c);
        }
        catch (Exception var2)
        {
            chatMsg("Chunk at chunk position " + c.xPosition + "," + c.zPosition + " can\'t be saved!");
        }
    }

    public static void loadBaseProps()
    {
        baseFolderName = getBaseFolderName();
        baseProps = new Properties(defaultProps);

        try
        {
            baseProps.load(new FileReader(new File(mc.mcDataDir, "saves/" + baseFolderName + "/WorldDownloader.txt")));
            propsFound = true;
        }
        catch (FileNotFoundException var1)
        {
            propsFound = false;
        }
        catch (Exception var2)
        {
            ;
        }

        if (baseProps.getProperty("LinkedWorlds").isEmpty())
        {
            isMultiworld = false;
            worldProps = new Properties(baseProps);
        }
        else
        {
            isMultiworld = true;
        }
    }

    public static Properties loadWorldProps(String theWorldName)
    {
        Properties ret = new Properties(baseProps);

        if (!theWorldName.isEmpty())
        {
            String folder = getWorldFolderName(theWorldName);

            try
            {
                ret.load(new FileReader(new File(mc.mcDataDir, "saves/" + folder + "/WorldDownloader.txt")));
            }
            catch (Exception var4)
            {
                return null;
            }
        }

        return ret;
    }

    public static void saveProps()
    {
        saveProps(worldName, worldProps);
    }

    public static void saveProps(String theWorldName, Properties theWorldProps)
    {
        if (theWorldName.length() > 0)
        {
            String baseFolder = getWorldFolderName(theWorldName);

            try
            {
                theWorldProps.store(new FileWriter(new File(mc.mcDataDir, "saves/" + baseFolder + "/WorldDownloader.txt")), "");
            }
            catch (Exception var5)
            {
                ;
            }
        }
        else if (!isMultiworld)
        {
            baseProps.putAll(theWorldProps);
        }

        File baseFolder1 = new File(mc.mcDataDir, "saves/" + baseFolderName);
        baseFolder1.mkdirs();

        try
        {
            baseProps.store(new FileWriter(new File(baseFolder1, "WorldDownloader.txt")), "");
        }
        catch (Exception var4)
        {
            ;
        }
    }

    public static void applyOverridesToPlayer(NBTTagCompound playerNBT)
    {
        String health = worldProps.getProperty("PlayerHealth");

        if (!health.equals("keep"))
        {
            short food = Short.parseShort(health);
            playerNBT.setShort("Health", food);
        }

        String food1 = worldProps.getProperty("PlayerFood");

        if (!food1.equals("keep"))
        {
            int playerPos = Integer.parseInt(food1);
            playerNBT.setInteger("foodLevel", playerPos);
            playerNBT.setInteger("foodTickTimer", 0);

            if (playerPos == 20)
            {
                playerNBT.setFloat("foodSaturationLevel", 5.0F);
            }
            else
            {
                playerNBT.setFloat("foodSaturationLevel", 0.0F);
            }

            playerNBT.setFloat("foodExhaustionLevel", 0.0F);
        }

        String playerPos1 = worldProps.getProperty("PlayerPos");

        if (playerPos1.equals("xyz"))
        {
            int x = Integer.parseInt(worldProps.getProperty("PlayerX"));
            int y = Integer.parseInt(worldProps.getProperty("PlayerY"));
            int z = Integer.parseInt(worldProps.getProperty("PlayerZ"));
            NBTTagList pos = playerNBT.getTagList("Pos", 6);
            pos.removeTag(0);
            pos.removeTag(0);
            pos.removeTag(0);
            pos.appendTag(new NBTTagDouble((double)x + 0.5D));
            pos.appendTag(new NBTTagDouble((double)y + 0.621D));
            pos.appendTag(new NBTTagDouble((double)x + 0.5D));
            NBTTagList motion = playerNBT.getTagList("Motion", 6);
            motion.removeTag(0);
            motion.removeTag(0);
            motion.removeTag(0);
            motion.appendTag(new NBTTagDouble(0.0D));
            motion.appendTag(new NBTTagDouble(-1.0E-4D));
            motion.appendTag(new NBTTagDouble(0.0D));
            NBTTagList rotation = playerNBT.getTagList("Rotation", 5);
            rotation.removeTag(0);
            rotation.removeTag(0);
            rotation.appendTag(new NBTTagFloat(0.0F));
            rotation.appendTag(new NBTTagFloat(0.0F));
        }
    }

    public static void applyOverridesToWorldInfo(NBTTagCompound worldInfoNBT)
    {
        String baseName = baseProps.getProperty("ServerName");
        String worldName = worldProps.getProperty("WorldName");

        if (worldName.isEmpty())
        {
            worldInfoNBT.setString("LevelName", baseName);
        }
        else
        {
            worldInfoNBT.setString("LevelName", baseName + " - " + worldName);
        }

        String gametypeOption = worldProps.getProperty("GameType");

        if (gametypeOption.equals("keep"))
        {
            if (tp.capabilities.isCreativeMode)
            {
                worldInfoNBT.setInteger("GameType", 1);
            }
            else
            {
                worldInfoNBT.setInteger("GameType", 0);
            }
        }
        else if (gametypeOption.equals("survival"))
        {
            worldInfoNBT.setInteger("GameType", 0);
        }
        else if (gametypeOption.equals("creative"))
        {
            worldInfoNBT.setInteger("GameType", 1);
        }
        else if (gametypeOption.equals("hardcore"))
        {
            worldInfoNBT.setInteger("GameType", 0);
            worldInfoNBT.setBoolean("hardcore", true);
        }

        String timeOption = worldProps.getProperty("Time");

        if (!timeOption.equals("keep"))
        {
            long randomSeed = (long)Integer.parseInt(timeOption);
            worldInfoNBT.setLong("Time", randomSeed);
        }

        String randomSeed1 = worldProps.getProperty("RandomSeed");
        long seed = 0L;

        if (!randomSeed1.isEmpty())
        {
            try
            {
                seed = Long.parseLong(randomSeed1);
            }
            catch (NumberFormatException var16)
            {
                seed = (long)randomSeed1.hashCode();
            }
        }

        worldInfoNBT.setLong("RandomSeed", seed);
        boolean mapFeatures = Boolean.parseBoolean(worldProps.getProperty("MapFeatures"));
        worldInfoNBT.setBoolean("MapFeatures", mapFeatures);
        String generatorName = worldProps.getProperty("GeneratorName");
        worldInfoNBT.setString("generatorName", generatorName);
        int generatorVersion = Integer.parseInt(worldProps.getProperty("GeneratorVersion"));
        worldInfoNBT.setInteger("generatorVersion", generatorVersion);
        String weather = worldProps.getProperty("Weather");

        if (weather.equals("sunny"))
        {
            worldInfoNBT.setBoolean("raining", false);
            worldInfoNBT.setInteger("rainTime", 0);
            worldInfoNBT.setBoolean("thundering", false);
            worldInfoNBT.setInteger("thunderTime", 0);
        }

        if (weather.equals("rain"))
        {
            worldInfoNBT.setBoolean("raining", true);
            worldInfoNBT.setInteger("rainTime", 24000);
            worldInfoNBT.setBoolean("thundering", false);
            worldInfoNBT.setInteger("thunderTime", 0);
        }

        if (weather.equals("thunderstorm"))
        {
            worldInfoNBT.setBoolean("raining", true);
            worldInfoNBT.setInteger("rainTime", 24000);
            worldInfoNBT.setBoolean("thundering", true);
            worldInfoNBT.setInteger("thunderTime", 24000);
        }

        String spawn = worldProps.getProperty("Spawn");
        int x;
        int y;
        int z;

        if (spawn.equals("player"))
        {
            x = (int)Math.floor(tp.posX);
            y = (int)Math.floor(tp.posY);
            z = (int)Math.floor(tp.posZ);
            worldInfoNBT.setInteger("SpawnX", x);
            worldInfoNBT.setInteger("SpawnY", y);
            worldInfoNBT.setInteger("SpawnZ", z);
            worldInfoNBT.setBoolean("initialized", true);
        }
        else if (spawn.equals("xyz"))
        {
            x = Integer.parseInt(worldProps.getProperty("SpawnX"));
            y = Integer.parseInt(worldProps.getProperty("SpawnY"));
            z = Integer.parseInt(worldProps.getProperty("SpawnZ"));
            worldInfoNBT.setInteger("SpawnX", x);
            worldInfoNBT.setInteger("SpawnY", y);
            worldInfoNBT.setInteger("SpawnZ", z);
            worldInfoNBT.setBoolean("initialized", true);
        }
    }

    public static String getServerName()
    {
        try
        {
            if (mc.func_147104_D() != null)
            {
                return mc.func_147104_D().serverName;
            }

            if (mcos != null)
            {
                return "MCRealm: " + URLDecoder.decode(mcos.field_148810_b, "UTF-8");
            }
        }
        catch (Exception var1)
        {
            ;
        }

        return "Unidentified Server";
    }

    public static String getBaseFolderName()
    {
        return getServerName().replaceAll("\\W+", "_");
    }

    public static String getWorldFolderName(String theWorldName)
    {
        return theWorldName.isEmpty() ? baseFolderName : baseFolderName + " - " + theWorldName;
    }

    public static void copyItemStacks(Container c, IInventory i, int startInContainerAt)
    {
        int containerSize = c.inventorySlots.size();
        int inventorySize = i.getSizeInventory();
        int nc = startInContainerAt;

        for (int ni = 0; nc < containerSize && ni < inventorySize; ++nc)
        {
            ItemStack is = c.getSlot(nc).getStack();
            i.setInventorySlotContents(ni, is);
            ++ni;
        }
    }

    public static void chatMsg(String msg)
    {
        mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText("\u00a7c[WorldDL]\u00a76 " + msg));
    }

    public static void chatDebug(String msg)
    {
        if (DEBUG)
        {
            mc.ingameGUI.getChatGui().printChatMessage(new ChatComponentText("\u00a72[WorldDL]\u00a76 " + msg));
        }
    }

    public static Field stealField(Class typeOfClass, Class typeOfField)
    {
        Field[] fields = typeOfClass.getDeclaredFields();
        Field[] arr$ = fields;
        int len$ = fields.length;
        int i$ = 0;

        while (true)
        {
            if (i$ < len$)
            {
                Field f = arr$[i$];

                if (f.getType() != typeOfField)
                {
                    ++i$;
                    continue;
                }

                try
                {
                    f.setAccessible(true);
                    return f;
                }
                catch (Exception var8)
                {
                    ;
                }
            }

            throw new RuntimeException("WorldDownloader: Couldn\'t steal Field of type \"" + typeOfField + "\" from class \"" + typeOfClass + "\" !");
        }
    }

    public static Object stealAndGetField(Object object, Class typeOfField)
    {
        Class typeOfObject;

        if (object instanceof Class)
        {
            typeOfObject = (Class)object;
            object = null;
        }
        else
        {
            typeOfObject = object.getClass();
        }

        try
        {
            Field e = stealField(typeOfObject, typeOfField);
            return e.get(object);
        }
        catch (Exception var4)
        {
            throw new RuntimeException("WorldDownloader: Couldn\'t get Field of type \"" + typeOfField + "\" from object \"" + object + "\" !");
        }
    }

    public static void handleServerSeedMessage(String msg)
    {
        if (downloading && msg.startsWith("Seed: "))
        {
            String seed = msg.substring(6);
            worldProps.setProperty("RandomSeed", seed);
            chatMsg("Setting single-player world seed to " + seed);
        }
    }

    static
    {
        defaultProps.setProperty("ServerName", "");
        defaultProps.setProperty("WorldName", "");
        defaultProps.setProperty("LinkedWorlds", "");
        defaultProps.setProperty("AutoStart", "false");
        defaultProps.setProperty("Backup", "off");
        defaultProps.setProperty("BackupPath", "");
        defaultProps.setProperty("BackupsToKeep", "1");
        defaultProps.setProperty("BackupCommand", "");
        defaultProps.setProperty("GameType", "keep");
        defaultProps.setProperty("Time", "keep");
        defaultProps.setProperty("Weather", "keep");
        defaultProps.setProperty("MapFeatures", "false");
        defaultProps.setProperty("RandomSeed", "");
        defaultProps.setProperty("GeneratorName", "flat");
        defaultProps.setProperty("GeneratorVersion", "0");
        defaultProps.setProperty("Spawn", "player");
        defaultProps.setProperty("SpawnX", "8");
        defaultProps.setProperty("SpawnY", "127");
        defaultProps.setProperty("SpawnZ", "8");
        defaultProps.setProperty("PlayerPos", "keep");
        defaultProps.setProperty("PlayerX", "8");
        defaultProps.setProperty("PlayerY", "127");
        defaultProps.setProperty("PlayerZ", "8");
        defaultProps.setProperty("PlayerHealth", "20");
        defaultProps.setProperty("PlayerFood", "20");
        baseProps = new Properties(defaultProps);
        worldProps = new Properties(baseProps);
    }
}
