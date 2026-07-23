package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFireworkStarterFX;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.src.DynamicLights;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.impl.module.render.NoRenderModule;
import ez.nebula.client.api.listener.event.world.EventAddEntity;
import ez.nebula.client.worlddownloader.WorldDownloader;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class WorldClient extends World
{
    /**
     * The packets that need to be sent to the server.
     */
    private final NetHandlerPlayClient sendQueue;

    /**
     * The ChunkProviderClient instance
     */
    private ChunkProviderClient clientChunkProvider;

    /**
     * The hash set of entities handled by this client. Uses the entity's ID as the hash set's key.
     */
    private final IntHashMap<Entity> entityHashSet = new IntHashMap<>();

    /**
     * Contains all entities for this client, both spawned and non-spawned.
     */
    private final Set<Entity> entityList = new HashSet<>();

    /**
     * Contains all entities for this client that were not spawned due to a non-present chunk. The game will attempt to
     * spawn up to 10 pending entities with each subsequent tick until the spawn queue is empty.
     */
    private final Set<Entity> entitySpawnQueue = new HashSet<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Set<ChunkCoordIntPair> previousActiveChunkSet = new HashSet<>();
    public boolean renderItemInFirstPerson = false;

    public WorldClient(NetHandlerPlayClient p_i45063_1_, WorldSettings p_i45063_2_, int p_i45063_3_, EnumDifficulty p_i45063_4_, Profiler p_i45063_5_)
    {
        super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(p_i45063_3_), p_i45063_2_, p_i45063_5_);
        this.sendQueue = p_i45063_1_;
        this.difficultySetting = p_i45063_4_;
        this.mapStorage = p_i45063_1_.mapStorageOrigin;
        this.setSpawnLocation(8, 64, 8);
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        super.tick();
        this.incrementTotalWorldTime(this.getTotalWorldTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
        {
            this.setWorldTime(this.getWorldTime() + 1L);
        }

        this.theProfiler.startSection("reEntryProcessing");

        for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); ++i)
        {
            Entity entity = this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(entity);

            if (!this.loadedEntityList.contains(entity))
            {
                this.spawnEntityInWorld(entity);
            }
        }

        this.theProfiler.endStartSection("connection");
        this.sendQueue.onNetworkTick();
        this.theProfiler.endStartSection("chunkCache");
        this.clientChunkProvider.unloadQueuedChunks();
        this.theProfiler.endStartSection("blocks");
        this.func_147456_g();
        this.theProfiler.endSection();
    }

    /**
     * Invalidates an AABB region of blocks from the receive queue, in the event that the block has been modified
     * client-side in the intervening 80 receive ticks.
     */
    public void invalidateBlockReceiveRegion(int par1, int par2, int par3, int par4, int par5, int par6)
    {
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    protected void func_147456_g()
    {
        super.func_147456_g();
        this.previousActiveChunkSet.retainAll(this.activeChunkSet);

        if (this.previousActiveChunkSet.size() == this.activeChunkSet.size())
        {
            this.previousActiveChunkSet.clear();
        }

        int var1 = 0;
        for (ChunkCoordIntPair var3 : this.activeChunkSet)
        {
            if (!this.previousActiveChunkSet.contains(var3))
            {
                int var4 = var3.chunkXPos * 16;
                int var5 = var3.chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                Chunk chunk = this.getChunkFromChunkCoords(var3.chunkXPos, var3.chunkZPos);
                this.func_147467_a(var4, var5, chunk);
                this.theProfiler.endSection();
                this.previousActiveChunkSet.add(var3);
                ++var1;

                if (var1 >= 10)
                {
                    return;
                }
            }
        }
    }

    public void doPreChunk(int chunkX, int chunkZ, boolean load)
    {
        if (load)
        {
            final Chunk chunk = this.clientChunkProvider.loadChunk(chunkX, chunkZ);
            if (chunk != null)
            {
                WorldDownloader.INSTANCE.addChunk(chunk);
            }
        } else
        {
            this.clientChunkProvider.unloadChunk(chunkX, chunkZ);
        }

        if (!load)
        {
            this.markBlockRangeForRenderUpdate(chunkX * 16, 0, chunkZ * 16, chunkX * 16 + 15, 256, chunkZ * 16 + 15);
        }
    }

    /**
     * Called to place all entities as part of a world
     */
    public boolean spawnEntityInWorld(Entity entity)
    {
        boolean spawned = super.spawnEntityInWorld(entity);
        this.entityList.add(entity);

        if (!spawned)
        {
            this.entitySpawnQueue.add(entity);
        } else if (entity instanceof EntityMinecart)
        {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart) entity));
        }

        return spawned;
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(Entity entity)
    {
        super.removeEntity(entity);
        this.entityList.remove(entity);
    }

    protected void onEntityAdded(Entity entity)
    {
        super.onEntityAdded(entity);
        this.entitySpawnQueue.remove(entity);
    }

    protected void onEntityRemoved(Entity entity)
    {
        super.onEntityRemoved(entity);
        boolean added = false;

        if (this.entityList.contains(entity))
        {
            if (entity.isEntityAlive())
            {
                this.entitySpawnQueue.add(entity);
                added = true;
            } else
            {
                this.entityList.remove(entity);
            }
        }

        if (RenderManager.instance.getEntityRenderObject(entity).isStaticEntity() && !added)
        {
            this.mc.renderGlobal.onStaticEntitiesChanged();
        }
    }

    /**
     * Add an ID to Entity mapping to entityHashSet
     */
    public void addEntityToWorld(int id, Entity entity)
    {
        Entity existingEntity = this.getEntityByID(id);
        EventBus.dispatch(new EventAddEntity(id, entity, existingEntity != null));

        if (existingEntity != null)
        {
            this.removeEntity(existingEntity);
        }

        this.entityList.add(entity);
        entity.setEntityId(id);

        if (!this.spawnEntityInWorld(entity))
        {
            this.entitySpawnQueue.add(entity);
        }

        this.entityHashSet.addKey(id, entity);

        if (RenderManager.instance.getEntityRenderObject(entity).isStaticEntity())
        {
            this.mc.renderGlobal.onStaticEntitiesChanged();
        }
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int id)
    {
        return id == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : (Entity) this.entityHashSet.lookup(id);
    }

    public Entity removeEntityFromWorld(int id)
    {
        Entity entity = this.entityHashSet.removeObject(id);

        if (entity != null)
        {
            this.entityList.remove(entity);
            this.removeEntity(entity);
        }

        return entity;
    }

    public boolean onBlockChange(int x, int y, int z, Block block, int meta)
    {
        this.invalidateBlockReceiveRegion(x, y, z, x, y, z);
        return super.setBlock(x, y, z, block, meta, 3);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket()
    {
        this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
        super.updateWeather();
    }

    public void doVoidFogParticles(int x, int y, int z)
    {
        if (NoRenderModule.INSTANCE.isToggled()
                && NoRenderModule.INSTANCE.voidParticlesSetting.getValue())
        {
            return;
        }
        int randomMax = 16;
        for (int i = 0; i < 100; ++i)
        {
            int randX = x + this.rand.nextInt(randomMax) - this.rand.nextInt(randomMax);
            int randY = y + this.rand.nextInt(randomMax) - this.rand.nextInt(randomMax);
            int randZ = z + this.rand.nextInt(randomMax) - this.rand.nextInt(randomMax);
            Block block = this.getBlock(randX, randY, randZ);

            if (block.getMaterial() == Material.air)
            {
                if (this.rand.nextInt(8) > randY && this.provider.getWorldHasVoidParticles())
                {
                    this.spawnParticle("depthsuspend", (float) randX + this.rand.nextFloat(), (float) randY + this.rand.nextFloat(), (float) randZ + this.rand.nextFloat(), 0.0D, 0.0D, 0.0D);
                }
            } else
            {
                block.randomDisplayTick(this, randX, randY, randZ, ThreadLocalRandom.current());
            }
        }
    }

    /**
     * also releases skins.
     */
    public void removeAllEntities()
    {
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (Entity entity : unloadedEntityList)
        {
            int ccX = entity.chunkCoordX;
            int ccZ = entity.chunkCoordZ;
            if (entity.addedToChunk && this.chunkExists(ccX, ccZ))
            {
                this.getChunkFromChunkCoords(ccX, ccZ).removeEntity(entity);
            }
        }

        for (Entity entity : unloadedEntityList)
        {
            onEntityRemoved(entity);
        }

        this.unloadedEntityList.clear();

        int i;
        for (i = 0; i < this.loadedEntityList.size(); ++i)
        {
            final Entity entity = this.loadedEntityList.get(i);

            if (entity.ridingEntity != null)
            {
                if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity)
                {
                    continue;
                }

                entity.ridingEntity.riddenByEntity = null;
                entity.ridingEntity = null;
            }

            if (entity.isDead)
            {
                int ccX = entity.chunkCoordX;
                int ccZ = entity.chunkCoordZ;

                if (entity.addedToChunk && this.chunkExists(ccX, ccZ))
                {
                    this.getChunkFromChunkCoords(ccX, ccZ).removeEntity(entity);
                }

                this.loadedEntityList.remove(i--);
                this.onEntityRemoved(entity);
            }
        }
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report)
    {
        CrashReportCategory reportCategory = super.addWorldInfoToCrashReport(report);
        reportCategory.addCrashSectionCallable("Forced entities", () ->
                WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList);
        reportCategory.addCrashSectionCallable("Retry entities", () ->
                WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue);
        reportCategory.addCrashSectionCallable("Server brand", () -> WorldClient.this.mc.thePlayer.getServerBrand());
        reportCategory.addCrashSectionCallable("Server type", () -> WorldClient.this.mc.getIntegratedServer() == null
                ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        return reportCategory;
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(double x, double y, double z, String location, float loudness, float par9, boolean delayed)
    {
        double distanceSq = this.mc.renderViewEntity.getDistanceSq(x, y, z);
        PositionedSoundRecord sound = new PositionedSoundRecord(new ResourceLocation(location), loudness, par9, (float) x, (float) y, (float) z);

        if (delayed && distanceSq > 100.0D)
        {
            double var14 = Math.sqrt(distanceSq) / 40.0D;
            this.mc.getSoundHandler().playDelayedSound(sound, (int) (var14 * 20.0D));
        } else
        {
            this.mc.getSoundHandler().playSound(sound);
        }
    }

    public void makeFireworks(double par1, double par3, double par5, double par7, double par9, double par11, NBTTagCompound par13NBTTagCompound)
    {
        this.mc.effectRenderer.addEffect(new EntityFireworkStarterFX(this, par1, par3, par5, par7, par9, par11, this.mc.effectRenderer, par13NBTTagCompound));
    }

    public void setWorldScoreboard(Scoreboard par1Scoreboard)
    {
        this.worldScoreboard = par1Scoreboard;
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long time)
    {
        if (time < 0L)
        {
            time = -time;
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
        } else
        {
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
        }

        super.setWorldTime(time);
    }

    @Override
    public void addBlockEvent(int par1, int par2, int par3, Block par4, int par5, int par6)
    {
        super.addBlockEvent(par1, par2, par3, par4, par5, par6);
    }

    /**
     * Any Light rendered on a 1.8 Block goes through here
     */
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue)
    {
        int combinedLight = super.getLightBrightnessForSkyBlocks(x, y, z, lightValue);

        if (Config.isDynamicLights())
        {
            if (this.renderItemInFirstPerson)
            {
                combinedLight = DynamicLights.getCombinedLight(this.mc.renderViewEntity, combinedLight);
            }

            if (!this.getBlock(x, y, z).isOpaqueCube())
            {
                combinedLight = DynamicLights.getCombinedLight(x, y, z, combinedLight);
            }
        }

        return combinedLight;
    }
}
