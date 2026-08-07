package net.minecraft.client.network;

import com.google.common.base.Charsets;
import ez.nebula.client.impl.module.combat.VelocityModule;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.*;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.event.network.EventDisconnect;
import ez.nebula.client.api.listener.event.player.EventPlayerDeath;
import ez.nebula.client.worlddownloader.WorldDownloader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class NetHandlerPlayClient implements INetHandlerPlayClient
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The NetworkManager instance used to communicate with the server (used only by handlePlayerPosLook to update
     * positioning and handleJoinGame to inform the server of the client distribution/mods)
     */
    private final NetworkManager netManager;

    /**
     * Reference to the Minecraft instance, which many handler methods operate on
     */
    private final Minecraft gameController;

    /**
     * Reference to the current ClientWorld instance, which many handler methods operate on
     */
    private WorldClient clientWorldController;

    /**
     * True if the client has finished downloading terrain and may spawn. Set upon receipt of S08PacketPlayerPosLook,
     * reset upon respawning
     */
    public boolean doneLoadingTerrain;

    /**
     * Origin of the central MapStorage serving as a public reference for WorldClient. Not used in this class
     */
    public MapStorage mapStorageOrigin = new MapStorage(null);

    /**
     * A mapping from player names to their respective GuiPlayerInfo (specifies the clients response time to the server)
     */
    public Map<String, GuiPlayerInfo> playerInfoMap = new HashMap<>();

    /**
     * An ArrayList of GuiPlayerInfo (includes all the players' GuiPlayerInfo on the current server)
     */
    public List<GuiPlayerInfo> playerInfoList = new ArrayList<>();
    public int currentServerMaxPlayers = 20;

    /**
     * Seems to be either null (integrated server) or an instance of either GuiMultiplayer (when connecting to a server)
     * or GuiScreenReamlsTOS (when connecting to MCO server)
     */
    private final GuiScreen guiScreenServer;
    private boolean field_147308_k = false;

    /**
     * Just an ordinary random number generator, used to randomize audio pitch of item/orb pickup and randomize both
     * particlespawn offset and velocity
     */
    private final Random avRandomizer = new Random();

    public NetHandlerPlayClient(Minecraft mc, GuiScreen screen, NetworkManager netManager)
    {
        this.gameController = mc;
        this.guiScreenServer = screen;
        this.netManager = netManager;
    }

    /**
     * Clears the WorldClient instance associated with this NetHandlerPlayClient
     */
    public void cleanup()
    {
        this.clientWorldController = null;
    }

    /**
     * For scheduled network tasks. Used in NetHandlerPlayServer to send keep-alive packets and in NetHandlerLoginServer
     * for a login-timeout
     */
    public void onNetworkTick()
    {
    }

    /**
     * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
     * WorldClient and sets the player initial dimension
     */
    public void handleJoinGame(S01PacketJoinGame packet)
    {
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packet.getGameType(), false, packet.isHardcore(), packet.getWorldType()), packet.getDimension(), packet.getDifficulty(), this.gameController.mcProfiler);
        this.clientWorldController.isClient = true;
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.thePlayer.dimension = packet.getDimension();
        this.gameController.thePlayer.setEntityId(packet.getEntityId());
        this.currentServerMaxPlayers = packet.getMaxPlayers();
        this.gameController.playerController.setGameType(packet.getGameType());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager.scheduleOutboundPacket(new C17PacketCustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
    }

    /**
     * Spawns an instance of the objecttype indicated by the packet and sets its position and momentum
     */
    public void handleSpawnObject(S0EPacketSpawnObject packet)
    {
        double x = (double) packet.getX() / 32.0D;
        double y = (double) packet.getY() / 32.0D;
        double z = (double) packet.getZ() / 32.0D;
        Entity entity = null;

        if (packet.getType() == 10)
        {
            entity = EntityMinecart.createMinecart(this.clientWorldController, x, y, z, packet.func_149009_m());
        } else if (packet.getType() == 90)
        {
            Entity var9 = this.clientWorldController.getEntityByID(packet.func_149009_m());

            if (var9 instanceof EntityPlayer)
            {
                entity = new EntityFishHook(this.clientWorldController, x, y, z, (EntityPlayer) var9);
            }

            packet.func_149002_g(0);
        } else if (packet.getType() == 60)
        {
            entity = new EntityArrow(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 61)
        {
            entity = new EntitySnowball(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 71)
        {
            entity = new EntityItemFrame(this.clientWorldController, (int) x, (int) y, (int) z, packet.func_149009_m());
            packet.func_149002_g(0);
        } else if (packet.getType() == 77)
        {
            entity = new EntityLeashKnot(this.clientWorldController, (int) x, (int) y, (int) z);
            packet.func_149002_g(0);
        } else if (packet.getType() == 65)
        {
            entity = new EntityEnderPearl(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 72)
        {
            entity = new EntityEnderEye(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 76)
        {
            entity = new EntityFireworkRocket(this.clientWorldController, x, y, z, null);
        } else if (packet.getType() == 63)
        {
            entity = new EntityLargeFireball(this.clientWorldController, x, y, z, (double) packet.getMotionX() / 8000.0D, (double) packet.getMotionY() / 8000.0D, (double) packet.getMotionZ() / 8000.0D);
            packet.func_149002_g(0);
        } else if (packet.getType() == 64)
        {
            entity = new EntitySmallFireball(this.clientWorldController, x, y, z, (double) packet.getMotionX() / 8000.0D, (double) packet.getMotionY() / 8000.0D, (double) packet.getMotionZ() / 8000.0D);
            packet.func_149002_g(0);
        } else if (packet.getType() == 66)
        {
            entity = new EntityWitherSkull(this.clientWorldController, x, y, z, (double) packet.getMotionX() / 8000.0D, (double) packet.getMotionY() / 8000.0D, (double) packet.getMotionZ() / 8000.0D);
            packet.func_149002_g(0);
        } else if (packet.getType() == 62)
        {
            entity = new EntityEgg(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 73)
        {
            entity = new EntityPotion(this.clientWorldController, x, y, z, packet.func_149009_m());
            packet.func_149002_g(0);
        } else if (packet.getType() == 75)
        {
            entity = new EntityExpBottle(this.clientWorldController, x, y, z);
            packet.func_149002_g(0);
        } else if (packet.getType() == 1)
        {
            entity = new EntityBoat(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 50)
        {
            entity = new EntityTNTPrimed(this.clientWorldController, x, y, z, null);
        } else if (packet.getType() == 51)
        {
            entity = new EntityEnderCrystal(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 2)
        {
            entity = new EntityItem(this.clientWorldController, x, y, z);
        } else if (packet.getType() == 70)
        {
            entity = new EntityFallingBlock(this.clientWorldController, x, y, z, Block.getBlockById(packet.func_149009_m() & 65535), packet.func_149009_m() >> 16);
            packet.func_149002_g(0);
        }

        if (entity != null)
        {
            entity.serverPosX = packet.getX();
            entity.serverPosY = packet.getY();
            entity.serverPosZ = packet.getZ();
            entity.rotationPitch = (float) (packet.getPitch() * 360) / 256.0F;
            entity.rotationYaw = (float) (packet.getYaw() * 360) / 256.0F;
            Entity[] var12 = entity.getParts();

            if (var12 != null)
            {
                int var10 = packet.getID() - entity.getEntityId();
                for (Entity e : var12)
                {
                    e.setEntityId(e.getEntityId() + var10);
                }
            }

            entity.setEntityId(packet.getID());
            this.clientWorldController.addEntityToWorld(packet.getID(), entity);

            if (packet.func_149009_m() > 0)
            {
                if (packet.getType() == 60)
                {
                    Entity shootingEntity = this.clientWorldController.getEntityByID(packet.func_149009_m());

                    if (shootingEntity instanceof EntityLivingBase)
                    {
                        assert entity instanceof EntityArrow;
                        EntityArrow var14 = (EntityArrow) entity;
                        var14.shootingEntity = shootingEntity;
                    }
                }

                entity.setVelocity((double) packet.getMotionX() / 8000.0D, (double) packet.getMotionY() / 8000.0D, (double) packet.getMotionZ() / 8000.0D);
            }
        }
    }

    /**
     * Spawns an experience orb and sets its value (amount of XP)
     */
    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packet)
    {
        EntityXPOrb entity = new EntityXPOrb(this.clientWorldController, packet.getX(), packet.getY(), packet.getZ(), packet.getValue());
        entity.serverPosX = packet.getX();
        entity.serverPosY = packet.getY();
        entity.serverPosZ = packet.getZ();
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.setEntityId(packet.getEntityID());
        this.clientWorldController.addEntityToWorld(packet.getEntityID(), entity);
    }

    /**
     * Handles globally visible entities. Used in vanilla for lightning bolts
     */
    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packet)
    {
        double x = (double) packet.getX() / 32.0D;
        double y = (double) packet.getY() / 32.0D;
        double z = (double) packet.getZ() / 32.0D;
        EntityLightningBolt entity = null;

        if (packet.getType() == 1)
        {
            entity = new EntityLightningBolt(this.clientWorldController, x, y, z);
        }

        if (entity != null)
        {
            entity.serverPosX = packet.getX();
            entity.serverPosY = packet.getY();
            entity.serverPosZ = packet.getZ();
            entity.rotationYaw = 0.0F;
            entity.rotationPitch = 0.0F;
            entity.setEntityId(packet.getEntityID());
            this.clientWorldController.addWeatherEffect(entity);
        }
    }

    /**
     * Handles the spawning of a painting object
     */
    public void handleSpawnPainting(S10PacketSpawnPainting packet)
    {
        EntityPainting entity = new EntityPainting(this.clientWorldController,
                packet.getX(),
                packet.getY(),
                packet.getZ(),
                packet.getDirection(),
                packet.getType());
        this.clientWorldController.addEntityToWorld(packet.getEntityID(), entity);
    }

    /**
     * Sets the velocity of the specified entity to the specified value
     */
    public void handleEntityVelocity(S12PacketEntityVelocity packet)
    {
        Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());
        if (entity != null)
        {
            entity.setVelocity((double) packet.getX() / 8000.0D,
                    (double) packet.getY() / 8000.0D,
                    (double) packet.getZ() / 8000.0D);
        }
    }

    /**
     * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
     * changed -> Registers any changes locally
     */
    public void handleEntityMetadata(S1CPacketEntityMetadata packet)
    {
        final Entity entity = this.clientWorldController.getEntityByID(packet.getEntityId());
        if (entity != null && packet.getChangedProperties() != null)
        {
            entity.getDataWatcher().updateWatchedObjectsFromList(packet.getChangedProperties());
        }

        if (entity instanceof EntityPlayer)
        {
            for (final DataWatcher.WatchableObject object : packet.getChangedProperties())
            {
                // 6 = health, object is 0.0f-1.0f (or max health)
                if (object.getDataValueId() == 6 && ((float) object.getObject()) == 0.0f)
                {
                    EventBus.dispatch(new EventPlayerDeath((EntityPlayer) entity));
                }
            }
        }
    }

    /**
     * Handles the creation of a nearby player entity, sets the position and held item
     */
    public void handleSpawnPlayer(S0CPacketSpawnPlayer p_147237_1_)
    {
        double var2 = (double) p_147237_1_.func_148942_f() / 32.0D;
        double var4 = (double) p_147237_1_.func_148949_g() / 32.0D;
        double var6 = (double) p_147237_1_.func_148946_h() / 32.0D;
        float var8 = (float) (p_147237_1_.func_148941_i() * 360) / 256.0F;
        float var9 = (float) (p_147237_1_.func_148945_j() * 360) / 256.0F;
        EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.gameController.theWorld, p_147237_1_.func_148948_e());
        var10.prevPosX = var10.lastTickPosX = var10.serverPosX = p_147237_1_.func_148942_f();
        var10.prevPosY = var10.lastTickPosY = var10.serverPosY = p_147237_1_.func_148949_g();
        var10.prevPosZ = var10.lastTickPosZ = var10.serverPosZ = p_147237_1_.func_148946_h();
        int var11 = p_147237_1_.func_148947_k();

        if (var11 == 0)
        {
            var10.inventory.mainInventory[var10.inventory.currentItem] = null;
        } else
        {
            var10.inventory.mainInventory[var10.inventory.currentItem] = new ItemStack(Item.getItemById(var11), 1, 0);
        }

        var10.setPositionAndRotation(var2, var4, var6, var8, var9);
        this.clientWorldController.addEntityToWorld(p_147237_1_.func_148943_d(), var10);
        List var12 = p_147237_1_.func_148944_c();

        if (var12 != null)
        {
            var10.getDataWatcher().updateWatchedObjectsFromList(var12);
        }
    }

    /**
     * Updates an entity's position and rotation as specified by the packet
     */
    public void handleEntityTeleport(S18PacketEntityTeleport p_147275_1_)
    {
        Entity var2 = this.clientWorldController.getEntityByID(p_147275_1_.func_149451_c());

        if (var2 != null)
        {
            var2.serverPosX = p_147275_1_.func_149449_d();
            var2.serverPosY = p_147275_1_.func_149448_e();
            var2.serverPosZ = p_147275_1_.func_149446_f();
            double var3 = (double) var2.serverPosX / 32.0D;
            double var5 = (double) var2.serverPosY / 32.0D + 0.015625D;
            double var7 = (double) var2.serverPosZ / 32.0D;
            float var9 = (float) (p_147275_1_.func_149450_g() * 360) / 256.0F;
            float var10 = (float) (p_147275_1_.func_149447_h() * 360) / 256.0F;
            var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
        }
    }

    /**
     * Updates which hotbar slot of the player is currently selected
     */
    public void handleHeldItemChange(S09PacketHeldItemChange p_147257_1_)
    {
        if (p_147257_1_.getSlotIndex() >= 0 && p_147257_1_.getSlotIndex() < InventoryPlayer.getHotbarSize())
        {
            this.gameController.thePlayer.inventory.currentItem = p_147257_1_.getSlotIndex();
        }
    }

    /**
     * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
     * subclassing of the packet allows for the specification of a subset of this data (e.g. only rel. position, abs.
     * rotation or both).
     */
    public void handleEntityMovement(S14PacketEntity p_147259_1_)
    {
        Entity var2 = p_147259_1_.func_149065_a(this.clientWorldController);

        if (var2 != null)
        {
            var2.serverPosX += p_147259_1_.func_149062_c();
            var2.serverPosY += p_147259_1_.func_149061_d();
            var2.serverPosZ += p_147259_1_.func_149064_e();
            double var3 = (double) var2.serverPosX / 32.0D;
            double var5 = (double) var2.serverPosY / 32.0D;
            double var7 = (double) var2.serverPosZ / 32.0D;
            float var9 = p_147259_1_.func_149060_h() ? (float) (p_147259_1_.func_149066_f() * 360) / 256.0F : var2.rotationYaw;
            float var10 = p_147259_1_.func_149060_h() ? (float) (p_147259_1_.func_149063_g() * 360) / 256.0F : var2.rotationPitch;
            var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
        }
    }

    /**
     * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
     * rotation of the entity itself
     */
    public void handleEntityHeadLook(S19PacketEntityHeadLook p_147267_1_)
    {
        Entity var2 = p_147267_1_.func_149381_a(this.clientWorldController);

        if (var2 != null)
        {
            float var3 = (float) (p_147267_1_.func_149380_c() * 360) / 256.0F;
            var2.setRotationYawHead(var3);
        }
    }

    /**
     * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
     * longer registered as required to monitor them. The latter  happens when distance between the player and item
     * increases beyond a certain treshold (typically the viewing distance)
     */
    public void handleDestroyEntities(S13PacketDestroyEntities p_147238_1_)
    {
        for (int var2 = 0; var2 < p_147238_1_.func_149098_c().length; ++var2)
        {
            this.clientWorldController.removeEntityFromWorld(p_147238_1_.func_149098_c()[var2]);
        }
    }

    /**
     * Handles changes in player positioning and rotation such as when travelling to a new dimension, (re)spawning,
     * mounting horses etc. Seems to immediately reply to the server with the clients post-processing perspective on the
     * player positioning
     */
    public void handlePlayerPosLook(S08PacketPlayerPosLook p_147258_1_)
    {
        EntityClientPlayerMP var2 = this.gameController.thePlayer;
        double var3 = p_147258_1_.getX();
        double var5 = p_147258_1_.getY();
        double var7 = p_147258_1_.getZ();
        float var9 = p_147258_1_.getYaw();
        float var10 = p_147258_1_.getPitch();
        var2.ySize = 0.0F;
        var2.motionX = var2.motionY = var2.motionZ = 0.0D;
        var2.setPositionAndRotation(var3, var5, var7, var9, var10);
        this.netManager.scheduleOutboundPacket(new C03PacketPlayer.C06PacketPlayerPosLook(var2.posX, var2.boundingBox.minY, var2.posY, var2.posZ, p_147258_1_.getYaw(), p_147258_1_.getPitch(), p_147258_1_.isOnGround()));

        if (!this.doneLoadingTerrain)
        {
            this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
            this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
            this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen(null);
        }
    }

    /**
     * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
     * requires an update, the server sends S23PacketBlockChange and if 64 or more blocks are changed, the server sends
     * S21PacketChunkData
     */
    public void handleMultiBlockChange(S22PacketMultiBlockChange packet)
    {
        int var2 = packet.getChunkCoords().chunkXPos * 16;
        int var3 = packet.getChunkCoords().chunkZPos * 16;

        if (packet.getBlockUpdates() != null)
        {
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.getBlockUpdates()));
            try
            {
                for (int i = 0; i < packet.getCount(); ++i)
                {
                    short var6 = stream.readShort();
                    short var7 = stream.readShort();
                    int var8 = var7 >> 4 & 4095;
                    int var9 = var7 & 15;
                    int var10 = var6 >> 12 & 15;
                    int var11 = var6 >> 8 & 15;
                    int var12 = var6 & 255;
                    this.clientWorldController.onBlockChange(var10 + var2, var12, var11 + var3, Block.getBlockById(var8), var9);
                }
            } catch (IOException ignored)
            {

            }
        }
    }

    /**
     * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
     */
    public void handleChunkData(S21PacketChunkData packet)
    {
        if (packet.isFull())
        {
            if (packet.getSects() == 0)
            {
                this.clientWorldController.doPreChunk(packet.getX(), packet.getZ(), false);
                return;
            }

            this.clientWorldController.doPreChunk(packet.getX(), packet.getZ(), true);
        }

        this.clientWorldController.invalidateBlockReceiveRegion(packet.getX() << 4, 0, packet.getZ() << 4, (packet.getX() << 4) + 15, 256, (packet.getZ() << 4) + 15);
        Chunk var2 = this.clientWorldController.getChunkFromChunkCoords(packet.getX(), packet.getZ());
        var2.fillChunk(packet.getDeflatedChunkData(), packet.getSects(), packet.getAdd(), packet.isFull());
        this.clientWorldController.markBlockRangeForRenderUpdate(packet.getX() << 4, 0, packet.getZ() << 4, (packet.getX() << 4) + 15, 256, (packet.getZ() << 4) + 15);

        if (!packet.isFull() || !(this.clientWorldController.provider instanceof WorldProviderSurface))
        {
            var2.resetRelightChecks();
        }
    }

    /**
     * Updates the block and metadata and generates a blockupdate (and notify the clients)
     */
    public void handleBlockChange(S23PacketBlockChange packet)
    {
        this.clientWorldController.onBlockChange(packet.getX(), packet.getY(), packet.getZ(), packet.getType(), packet.getData());
    }

    /**
     * Closes the network channel
     */
    public void handleDisconnect(S40PacketDisconnect p_147253_1_)
    {
        WorldDownloader.INSTANCE.stop();
        this.netManager.closeChannel(p_147253_1_.func_149165_c());
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent p_147231_1_)
    {
        WorldDownloader.INSTANCE.stop();
        EventBus.dispatch(new EventDisconnect(false, p_147231_1_));
        this.gameController.loadWorld(null);
        this.gameController.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", p_147231_1_));
    }

    public void addToSendQueue(Packet p_147297_1_)
    {
        this.netManager.scheduleOutboundPacket(p_147297_1_);
    }

    public void handleCollectItem(S0DPacketCollectItem p_147246_1_)
    {
        Entity var2 = this.clientWorldController.getEntityByID(p_147246_1_.func_149354_c());
        Object var3 = this.clientWorldController.getEntityByID(p_147246_1_.func_149353_d());

        if (var3 == null)
        {
            var3 = this.gameController.thePlayer;
        }

        if (var2 != null)
        {
            if (var2 instanceof EntityXPOrb)
            {
                this.clientWorldController.playSoundAtEntity(var2, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            } else
            {
                this.clientWorldController.playSoundAtEntity(var2, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.gameController.theWorld, var2, (Entity) var3, -0.5F));
            this.clientWorldController.removeEntityFromWorld(p_147246_1_.func_149354_c());
        }
    }

    /**
     * Prints a chatmessage in the chat GUI
     */
    public void handleChat(S02PacketChat p_147251_1_)
    {
        String var2 = p_147251_1_.getMessage().getFormattedText();
        //WDL.handleServerSeedMessage(var2);
        this.gameController.ingameGUI.getChatGui().printChatMessage(p_147251_1_.getMessage());
    }

    /**
     * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt
     * or receiving a critical hit by normal or magical means
     */
    public void handleAnimation(S0BPacketAnimation p_147279_1_)
    {
        Entity var2 = this.clientWorldController.getEntityByID(p_147279_1_.func_148978_c());

        if (var2 != null)
        {
            if (p_147279_1_.func_148977_d() == 0)
            {
                EntityLivingBase var3 = (EntityLivingBase) var2;
                var3.swingItem();
            } else if (p_147279_1_.func_148977_d() == 1)
            {
                var2.performHurtAnimation();
            } else if (p_147279_1_.func_148977_d() == 2)
            {
                EntityPlayer var4 = (EntityPlayer) var2;
                var4.wakeUpPlayer(false, false, false);
            } else if (p_147279_1_.func_148977_d() == 4)
            {
                this.gameController.effectRenderer.addEffect(new EntityCrit2FX(this.gameController.theWorld, var2));
            } else if (p_147279_1_.func_148977_d() == 5)
            {
                EntityCrit2FX var5 = new EntityCrit2FX(this.gameController.theWorld, var2, "magicCrit");
                this.gameController.effectRenderer.addEffect(var5);
            }
        }
    }

    /**
     * Retrieves the player identified by the packet, puts him to sleep if possible (and flags whether all players are
     * asleep)
     */
    public void handleUseBed(S0APacketUseBed p_147278_1_)
    {
        p_147278_1_.func_149091_a(this.clientWorldController).sleepInBedAt(p_147278_1_.func_149092_c(), p_147278_1_.func_149090_d(), p_147278_1_.func_149089_e());
    }

    /**
     * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
     * entities Datawatchers with the entity metadata specified in the packet
     */
    public void handleSpawnMob(S0FPacketSpawnMob p_147281_1_)
    {
        double var2 = (double) p_147281_1_.func_149023_f() / 32.0D;
        double var4 = (double) p_147281_1_.func_149034_g() / 32.0D;
        double var6 = (double) p_147281_1_.func_149029_h() / 32.0D;
        float var8 = (float) (p_147281_1_.func_149028_l() * 360) / 256.0F;
        float var9 = (float) (p_147281_1_.func_149030_m() * 360) / 256.0F;
        EntityLivingBase var10 = (EntityLivingBase) EntityList.createEntityByID(p_147281_1_.func_149025_e(), this.gameController.theWorld);
        var10.serverPosX = p_147281_1_.func_149023_f();
        var10.serverPosY = p_147281_1_.func_149034_g();
        var10.serverPosZ = p_147281_1_.func_149029_h();
        var10.rotationYawHead = (float) (p_147281_1_.func_149032_n() * 360) / 256.0F;
        Entity[] var11 = var10.getParts();

        if (var11 != null)
        {
            int var12 = p_147281_1_.func_149024_d() - var10.getEntityId();

            for (int var13 = 0; var13 < var11.length; ++var13)
            {
                var11[var13].setEntityId(var11[var13].getEntityId() + var12);
            }
        }

        var10.setEntityId(p_147281_1_.func_149024_d());
        var10.setPositionAndRotation(var2, var4, var6, var8, var9);
        var10.motionX = (float) p_147281_1_.func_149026_i() / 8000.0F;
        var10.motionY = (float) p_147281_1_.func_149033_j() / 8000.0F;
        var10.motionZ = (float) p_147281_1_.func_149031_k() / 8000.0F;
        this.clientWorldController.addEntityToWorld(p_147281_1_.func_149024_d(), var10);
        List var14 = p_147281_1_.func_149027_c();

        if (var14 != null)
        {
            var10.getDataWatcher().updateWatchedObjectsFromList(var14);
        }
    }

    public void handleTimeUpdate(S03PacketTimeUpdate p_147285_1_)
    {
        this.gameController.theWorld.incrementTotalWorldTime(p_147285_1_.func_149366_c());
        this.gameController.theWorld.setWorldTime(p_147285_1_.func_149365_d());
    }

    public void handleSpawnPosition(S05PacketSpawnPosition p_147271_1_)
    {
        this.gameController.thePlayer.setSpawnChunk(new ChunkCoordinates(p_147271_1_.func_149360_c(), p_147271_1_.func_149359_d(), p_147271_1_.func_149358_e()), true);
        this.gameController.theWorld.getWorldInfo().setSpawnPosition(p_147271_1_.func_149360_c(), p_147271_1_.func_149359_d(), p_147271_1_.func_149358_e());
    }

    public void handleEntityAttach(S1BPacketEntityAttach packet)
    {
        Object var2 = this.clientWorldController.getEntityByID(packet.getEntityId());
        Entity var3 = this.clientWorldController.getEntityByID(packet.getRidingEntityId());

        if (packet.getAction() == 0)
        {
            boolean var4 = false;

            if (packet.getEntityId() == this.gameController.thePlayer.getEntityId())
            {
                var2 = this.gameController.thePlayer;

                if (var3 instanceof EntityBoat)
                {
                    ((EntityBoat) var3).setIsBoatEmpty(false);
                }

                var4 = ((Entity) var2).ridingEntity == null && var3 != null;
            } else if (var3 instanceof EntityBoat)
            {
                ((EntityBoat) var3).setIsBoatEmpty(true);
            }

            if (var2 == null)
            {
                return;
            }

            ((Entity) var2).mountEntity(var3);

            if (var4)
            {
                GameSettings var5 = this.gameController.gameSettings;
                this.gameController.ingameGUI.func_110326_a(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(var5.keyBindSneak.getKeyCode())), false);
            }
        } else if (packet.getAction() == 1 && var2 != null && var2 instanceof EntityLiving)
        {
            if (var3 != null)
            {
                ((EntityLiving) var2).setLeashedToEntity(var3, false);
            } else
            {
                ((EntityLiving) var2).clearLeashed(false, false);
            }
        }
    }

    /**
     * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
     * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch
     * (spawn particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke
     * particles), Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
     */
    public void handleEntityStatus(S19PacketEntityStatus p_147236_1_)
    {
        Entity var2 = p_147236_1_.func_149161_a(this.clientWorldController);

        if (var2 != null)
        {
            var2.handleHealthUpdate(p_147236_1_.func_149160_c());
        }
    }

    public void handleUpdateHealth(S06PacketUpdateHealth p_147249_1_)
    {
        this.gameController.thePlayer.setPlayerSPHealth(p_147249_1_.func_149332_c());
        this.gameController.thePlayer.getFoodStats().setFoodLevel(p_147249_1_.func_149330_d());
        this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(p_147249_1_.func_149331_e());
    }

    public void handleSetExperience(S1FPacketSetExperience p_147295_1_)
    {
        this.gameController.thePlayer.setXPStats(p_147295_1_.func_149397_c(), p_147295_1_.func_149396_d(), p_147295_1_.func_149395_e());
    }

    public void handleRespawn(S07PacketRespawn p_147280_1_)
    {
        if (p_147280_1_.func_149082_c() != this.gameController.thePlayer.dimension)
        {
            this.doneLoadingTerrain = false;
            Scoreboard var2 = this.clientWorldController.getScoreboard();
            this.clientWorldController = new WorldClient(this, new WorldSettings(0L, p_147280_1_.func_149083_e(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), p_147280_1_.func_149080_f()), p_147280_1_.func_149082_c(), p_147280_1_.func_149081_d(), this.gameController.mcProfiler);
            this.clientWorldController.setWorldScoreboard(var2);
            this.clientWorldController.isClient = true;
            this.gameController.loadWorld(this.clientWorldController);
            this.gameController.thePlayer.dimension = p_147280_1_.func_149082_c();
        }

        this.gameController.setDimensionAndSpawnPlayer(p_147280_1_.func_149082_c());
        this.gameController.playerController.setGameType(p_147280_1_.func_149083_e());
    }

    /**
     * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the packet.
     */
    public void handleExplosion(S27PacketExplosion p_147283_1_)
    {
        Explosion var2 = new Explosion(this.gameController.theWorld, null, p_147283_1_.func_149148_f(), p_147283_1_.func_149143_g(), p_147283_1_.func_149145_h(), p_147283_1_.getSize());
        var2.affectedBlockPositions = p_147283_1_.func_149150_j();
        var2.doExplosionB(true);
        if (!VelocityModule.INSTANCE.isToggled() || !VelocityModule.INSTANCE.explosionSetting.getValue())
        {
            this.gameController.thePlayer.motionX += p_147283_1_.getX();
            this.gameController.thePlayer.motionY += p_147283_1_.getY();
            this.gameController.thePlayer.motionZ += p_147283_1_.getZ();
        }
    }

    /**
     * Displays a GUI by ID. In order starting from id 0: Chest, Workbench, Furnace, Dispenser, Enchanting table,
     * Brewing stand, Villager merchant, Beacon, Anvil, Hopper, Dropper, Horse
     */
    public void handleOpenWindow(S2DPacketOpenWindow p_147265_1_)
    {
        EntityClientPlayerMP var2 = this.gameController.thePlayer;

        switch (p_147265_1_.func_148899_d())
        {
            case 0:
                var2.displayGUIChest(new InventoryBasic(p_147265_1_.func_148902_e(), p_147265_1_.func_148900_g(), p_147265_1_.func_148898_f()));
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 1:
                var2.displayGUIWorkbench(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 2:
                TileEntityFurnace var4 = new TileEntityFurnace();

                if (p_147265_1_.func_148900_g())
                {
                    var4.func_145951_a(p_147265_1_.func_148902_e());
                }

                var2.func_146101_a(var4);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 3:
                TileEntityDispenser var7 = new TileEntityDispenser();

                if (p_147265_1_.func_148900_g())
                {
                    var7.func_146018_a(p_147265_1_.func_148902_e());
                }

                var2.func_146102_a(var7);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 4:
                var2.displayGUIEnchantment(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ), p_147265_1_.func_148900_g() ? p_147265_1_.func_148902_e() : null);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 5:
                TileEntityBrewingStand var5 = new TileEntityBrewingStand();

                if (p_147265_1_.func_148900_g())
                {
                    var5.func_145937_a(p_147265_1_.func_148902_e());
                }

                var2.func_146098_a(var5);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 6:
                var2.displayGUIMerchant(new NpcMerchant(var2), p_147265_1_.func_148900_g() ? p_147265_1_.func_148902_e() : null);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 7:
                TileEntityBeacon var8 = new TileEntityBeacon();
                var2.func_146104_a(var8);

                if (p_147265_1_.func_148900_g())
                {
                    var8.func_145999_a(p_147265_1_.func_148902_e());
                }

                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 8:
                var2.displayGUIAnvil(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 9:
                TileEntityHopper var3 = new TileEntityHopper();

                if (p_147265_1_.func_148900_g())
                {
                    var3.func_145886_a(p_147265_1_.func_148902_e());
                }

                var2.func_146093_a(var3);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 10:
                TileEntityDropper var6 = new TileEntityDropper();

                if (p_147265_1_.func_148900_g())
                {
                    var6.func_146018_a(p_147265_1_.func_148902_e());
                }

                var2.func_146102_a(var6);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 11:
                Entity var9 = this.clientWorldController.getEntityByID(p_147265_1_.func_148897_h());

                if (var9 != null && var9 instanceof EntityHorse)
                {
                    var2.displayGUIHorse((EntityHorse) var9, new AnimalChest(p_147265_1_.func_148902_e(), p_147265_1_.func_148900_g(), p_147265_1_.func_148898_f()));
                    var2.openContainer.windowId = p_147265_1_.func_148901_c();
                }
        }
    }

    /**
     * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
     */
    public void handleSetSlot(S2FPacketSetSlot p_147266_1_)
    {
        EntityClientPlayerMP var2 = this.gameController.thePlayer;

        if (p_147266_1_.func_149175_c() == -1)
        {
            var2.inventory.setItemStack(p_147266_1_.func_149174_e());
        } else
        {
            boolean var3 = false;

            if (this.gameController.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative var4 = (GuiContainerCreative) this.gameController.currentScreen;
                var3 = var4.func_147056_g() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (p_147266_1_.func_149175_c() == 0 && p_147266_1_.func_149173_d() >= 36 && p_147266_1_.func_149173_d() < 45)
            {
                ItemStack var5 = var2.inventoryContainer.getSlot(p_147266_1_.func_149173_d()).getStack();

                if (p_147266_1_.func_149174_e() != null && (var5 == null || var5.stackSize < p_147266_1_.func_149174_e().stackSize))
                {
                    p_147266_1_.func_149174_e().animationsToGo = 5;
                }

                var2.inventoryContainer.putStackInSlot(p_147266_1_.func_149173_d(), p_147266_1_.func_149174_e());
            } else if (p_147266_1_.func_149175_c() == var2.openContainer.windowId && (p_147266_1_.func_149175_c() != 0 || !var3))
            {
                var2.openContainer.putStackInSlot(p_147266_1_.func_149173_d(), p_147266_1_.func_149174_e());
            }
        }
    }

    /**
     * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
     * and confirms if it is the case.
     */
    public void handleConfirmTransaction(S32PacketConfirmTransaction packet)
    {
        Container var2 = null;
        EntityClientPlayerMP var3 = this.gameController.thePlayer;

        if (packet.getID() == 0)
        {
            var2 = var3.inventoryContainer;
        } else if (packet.getID() == var3.openContainer.windowId)
        {
            var2 = var3.openContainer;
        }

        if (var2 != null && !packet.isAccepted())
        {
            this.addToSendQueue(new C0FPacketConfirmTransaction(packet.getID(), packet.getUID(), true));
        }
    }

    /**
     * Handles the placement of a specified ItemStack in a specified container/inventory slot
     */
    public void handleWindowItems(S30PacketWindowItems p_147241_1_)
    {
        EntityClientPlayerMP var2 = this.gameController.thePlayer;

        if (p_147241_1_.func_148911_c() == 0)
        {
            var2.inventoryContainer.putStacksInSlots(p_147241_1_.func_148910_d());
        } else if (p_147241_1_.func_148911_c() == var2.openContainer.windowId)
        {
            var2.openContainer.putStacksInSlots(p_147241_1_.func_148910_d());
        }
    }

    /**
     * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
     */
    public void handleSignEditorOpen(S36PacketSignEditorOpen p_147268_1_)
    {
        Object var2 = this.clientWorldController.getTileEntity(p_147268_1_.func_149129_c(), p_147268_1_.func_149128_d(), p_147268_1_.func_149127_e());

        if (var2 == null)
        {
            var2 = new TileEntitySign();
            ((TileEntity) var2).setWorldObj(this.clientWorldController);
            ((TileEntity) var2).xCoord = p_147268_1_.func_149129_c();
            ((TileEntity) var2).yCoord = p_147268_1_.func_149128_d();
            ((TileEntity) var2).zCoord = p_147268_1_.func_149127_e();
        }

        this.gameController.thePlayer.func_146100_a((TileEntity) var2);
    }

    /**
     * Updates a specified sign with the specified text lines
     */
    public void handleUpdateSign(S33PacketUpdateSign packet)
    {
        boolean exists = false;

        if (this.gameController.theWorld.blockExists(packet.getX(), packet.getY(), packet.getZ()))
        {
            TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packet.getX(), packet.getY(), packet.getZ());
            if (tileEntity instanceof TileEntitySign)
            {
                TileEntitySign tileEntitySign = (TileEntitySign) tileEntity;
                if (tileEntitySign.func_145914_a())
                {
                    for (int i = 0; i < 4; ++i)
                    {
                        tileEntitySign.lines[i] = packet.getLines()[i];
                    }
                    tileEntitySign.onInventoryChanged();
                }
                exists = true;
            }
        }

        if (!exists && this.gameController.thePlayer != null)
        {
            this.gameController.thePlayer.addChatMessage(new ChatComponentText("Unable to locate sign at " + packet.getX() + ", " + packet.getY() + ", " + packet.getZ()));
        }
    }

    /**
     * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
     * beacons, skulls, flowerpot
     */
    public void handleUpdateTileEntity(S35PacketUpdateTileEntity p_147273_1_)
    {
        if (this.gameController.theWorld.blockExists(p_147273_1_.getX(), p_147273_1_.getY(), p_147273_1_.getZ()))
        {
            TileEntity var2 = this.gameController.theWorld.getTileEntity(p_147273_1_.getX(), p_147273_1_.getY(), p_147273_1_.getZ());

            if (var2 != null)
            {
                if (p_147273_1_.func_148853_f() == 1 && var2 instanceof TileEntityMobSpawner)
                {
                    var2.readFromNBT(p_147273_1_.func_148857_g());
                } else if (p_147273_1_.func_148853_f() == 2 && var2 instanceof TileEntityCommandBlock)
                {
                    var2.readFromNBT(p_147273_1_.func_148857_g());
                } else if (p_147273_1_.func_148853_f() == 3 && var2 instanceof TileEntityBeacon)
                {
                    var2.readFromNBT(p_147273_1_.func_148857_g());
                } else if (p_147273_1_.func_148853_f() == 4 && var2 instanceof TileEntitySkull)
                {
                    var2.readFromNBT(p_147273_1_.func_148857_g());
                } else if (p_147273_1_.func_148853_f() == 5 && var2 instanceof TileEntityFlowerPot)
                {
                    var2.readFromNBT(p_147273_1_.func_148857_g());
                }
            }
        }
    }

    /**
     * Sets the progressbar of the opened window to the specified value
     */
    public void handleWindowProperty(S31PacketWindowProperty p_147245_1_)
    {
        EntityClientPlayerMP var2 = this.gameController.thePlayer;

        if (var2.openContainer != null && var2.openContainer.windowId == p_147245_1_.func_149182_c())
        {
            var2.openContainer.updateProgressBar(p_147245_1_.func_149181_d(), p_147245_1_.func_149180_e());
        }
    }

    public void handleEntityEquipment(S04PacketEntityEquipment p_147242_1_)
    {
        Entity var2 = this.clientWorldController.getEntityByID(p_147242_1_.func_149389_d());

        if (var2 != null)
        {
            var2.setCurrentItemOrArmor(p_147242_1_.func_149388_e(), p_147242_1_.func_149390_c());
        }
    }

    /**
     * Resets the ItemStack held in hand and closes the window that is opened
     */
    public void handleCloseWindow(S2EPacketCloseWindow p_147276_1_)
    {
        this.gameController.thePlayer.closeScreenNoPacket();
    }

    /**
     * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
     * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
     * accessing a (Ender)Chest
     */
    public void handleBlockAction(S24PacketBlockAction p_147261_1_)
    {
        this.gameController.theWorld.addBlockEvent(p_147261_1_.func_148867_d(), p_147261_1_.func_148866_e(), p_147261_1_.func_148865_f(), p_147261_1_.func_148868_c(), p_147261_1_.func_148869_g(), p_147261_1_.func_148864_h());
    }

    /**
     * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
     */
    public void handleBlockBreakAnim(S25PacketBlockBreakAnim p_147294_1_)
    {
        this.gameController.theWorld.destroyBlockInWorldPartially(p_147294_1_.func_148845_c(), p_147294_1_.func_148844_d(), p_147294_1_.func_148843_e(), p_147294_1_.func_148842_f(), p_147294_1_.func_148846_g());
    }

    public void handleMapChunkBulk(S26PacketMapChunkBulk packet)
    {
        for (int var2 = 0; var2 < packet.getSize(); ++var2)
        {
            int x = packet.getChunkPosX(var2);
            int z = packet.getChunkPosZ(var2);
            this.clientWorldController.doPreChunk(x, z, true);
            this.clientWorldController.invalidateBlockReceiveRegion(x << 4, 0, z << 4, (x << 4) + 15, 256, (z << 4) + 15);
            Chunk var5 = this.clientWorldController.getChunkFromChunkCoords(x, z);
            var5.fillChunk(packet.func_149256_c(var2), packet.getChunkSectionData()[var2], packet.func_149257_f()[var2], true);
            this.clientWorldController.markBlockRangeForRenderUpdate(x << 4, 0, z << 4, (x << 4) + 15, 256, (z << 4) + 15);

            if (!(this.clientWorldController.provider instanceof WorldProviderSurface))
            {
                var5.resetRelightChecks();
            }
        }
    }

    public void handleChangeGameState(S2BPacketChangeGameState p_147252_1_)
    {
        EntityClientPlayerMP var2 = this.gameController.thePlayer;
        int var3 = p_147252_1_.func_149138_c();
        float var4 = p_147252_1_.func_149137_d();
        int var5 = MathHelper.floor_float(var4 + 0.5F);

        if (var3 >= 0 && var3 < S2BPacketChangeGameState.field_149142_a.length && S2BPacketChangeGameState.field_149142_a[var3] != null)
        {
            var2.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.field_149142_a[var3]));
        }

        if (var3 == 1)
        {
            this.clientWorldController.getWorldInfo().setRaining(true);
            this.clientWorldController.setRainStrength(0.0F);
        } else if (var3 == 2)
        {
            this.clientWorldController.getWorldInfo().setRaining(false);
            this.clientWorldController.setRainStrength(1.0F);
        } else if (var3 == 3)
        {
            this.gameController.playerController.setGameType(WorldSettings.GameType.getByID(var5));
        } else if (var3 == 4)
        {
            this.gameController.displayGuiScreen(new GuiWinGame());
        } else if (var3 == 5)
        {
            GameSettings var6 = this.gameController.gameSettings;

            if (var4 == 0.0F)
            {
                this.gameController.displayGuiScreen(new GuiScreenDemo());
            } else if (var4 == 101.0F)
            {
                this.gameController.ingameGUI.getChatGui().printChatMessage(new ChatComponentTranslation("demo.help.movement", GameSettings.getKeyDisplayString(var6.keyBindForward.getKeyCode()), GameSettings.getKeyDisplayString(var6.keyBindLeft.getKeyCode()), GameSettings.getKeyDisplayString(var6.keyBindBack.getKeyCode()), GameSettings.getKeyDisplayString(var6.keyBindRight.getKeyCode())));
            } else if (var4 == 102.0F)
            {
                this.gameController.ingameGUI.getChatGui().printChatMessage(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(var6.keyBindJump.getKeyCode())));
            } else if (var4 == 103.0F)
            {
                this.gameController.ingameGUI.getChatGui().printChatMessage(new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(var6.keyBindInventory.getKeyCode())));
            }
        } else if (var3 == 6)
        {
            this.clientWorldController.playSound(var2.posX, var2.posY + (double) var2.getEyeHeight(), var2.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        } else if (var3 == 7)
        {
            this.clientWorldController.setRainStrength(var4);
        } else if (var3 == 8)
        {
            this.clientWorldController.setThunderStrength(var4);
        }
    }

    /**
     * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
     * MapItemRenderer for it
     */
    public void handleMaps(S34PacketMaps p_147264_1_)
    {
        MapData var2 = ItemMap.func_150912_a(p_147264_1_.func_149188_c(), this.gameController.theWorld);
        var2.updateMPMapData(p_147264_1_.func_149187_d());
        this.gameController.entityRenderer.getMapItemRenderer().func_148246_a(var2);
    }

    public void handleEffect(S28PacketEffect p_147277_1_)
    {
        if (p_147277_1_.func_149244_c())
        {
            this.gameController.theWorld.playBroadcastSound(p_147277_1_.func_149242_d(), p_147277_1_.func_149240_f(), p_147277_1_.func_149243_g(), p_147277_1_.func_149239_h(), p_147277_1_.func_149241_e());
        } else
        {
            this.gameController.theWorld.playAuxSFX(p_147277_1_.func_149242_d(), p_147277_1_.func_149240_f(), p_147277_1_.func_149243_g(), p_147277_1_.func_149239_h(), p_147277_1_.func_149241_e());
        }
    }

    /**
     * Updates the players statistics or achievements
     */
    public void handleStatistics(S37PacketStatistics p_147293_1_)
    {
        boolean var2 = false;
        StatBase var5;
        int var6;

        for (Iterator var3 = p_147293_1_.func_148974_c().entrySet().iterator(); var3.hasNext(); this.gameController.thePlayer.func_146107_m().func_150873_a(this.gameController.thePlayer, var5, var6))
        {
            Entry var4 = (Entry) var3.next();
            var5 = (StatBase) var4.getKey();
            var6 = ((Integer) var4.getValue()).intValue();

            if (var5.isAchievement() && var6 > 0)
            {
                if (this.field_147308_k && this.gameController.thePlayer.func_146107_m().writeStat(var5) == 0)
                {
                    this.gameController.guiAchievement.func_146256_a((Achievement) var5);

                    if (var5 == AchievementList.openInventory)
                    {
                        this.gameController.gameSettings.showInventoryAchievementHint = false;
                        this.gameController.gameSettings.saveOptions();
                    }
                }

                var2 = true;
            }
        }

        if (!this.field_147308_k && !var2 && this.gameController.gameSettings.showInventoryAchievementHint)
        {
            this.gameController.guiAchievement.func_146255_b(AchievementList.openInventory);
        }

        this.field_147308_k = true;

        if (this.gameController.currentScreen instanceof IProgressMeter)
        {
            ((IProgressMeter) this.gameController.currentScreen).func_146509_g();
        }
    }

    public void handleEntityEffect(S1DPacketEntityEffect p_147260_1_)
    {
        Entity var2 = this.clientWorldController.getEntityByID(p_147260_1_.getEntityId());

        if (var2 instanceof EntityLivingBase)
        {
            PotionEffect var3 = new PotionEffect(p_147260_1_.getPotionId(), p_147260_1_.getDuration(), p_147260_1_.getAmplifier());
            var3.setPotionDurationMax(p_147260_1_.isInfinite());
            ((EntityLivingBase) var2).addPotionEffect(var3);
        }
    }

    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect p_147262_1_)
    {
        Entity var2 = this.clientWorldController.getEntityByID(p_147262_1_.func_149076_c());

        if (var2 instanceof EntityLivingBase)
        {
            ((EntityLivingBase) var2).removePotionEffectClient(p_147262_1_.func_149075_d());
        }
    }

    public void handlePlayerListItem(S38PacketPlayerListItem packet)
    {
        GuiPlayerInfo info = this.playerInfoMap.get(packet.getName());

        if (info == null && packet.func_149121_d())
        {
            info = new GuiPlayerInfo(packet.getName());
            this.playerInfoMap.put(packet.getName(), info);
            this.playerInfoList.add(info);
        }

        if (info != null && !packet.func_149121_d())
        {
            this.playerInfoMap.remove(packet.getName());
            this.playerInfoList.remove(info);
        }

        if (info != null && packet.func_149121_d())
        {
            info.responseTime = packet.func_149120_e();
        }
    }

    public void handleKeepAlive(S00PacketKeepAlive p_147272_1_)
    {
        this.addToSendQueue(new C00PacketKeepAlive(p_147272_1_.getKey()));
    }

    /**
     * Allows validation of the connection state transition. Parameters: from, to (connection state). Typically throws
     * IllegalStateException or UnsupportedOperationException if validation fails
     */
    public void onConnectionStateTransition(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_)
    {
        throw new IllegalStateException("Unexpected protocol change!");
    }

    public void handlePlayerAbilities(S39PacketPlayerAbilities p_147270_1_)
    {
        EntityClientPlayerMP var2 = this.gameController.thePlayer;
        var2.capabilities.isFlying = p_147270_1_.func_149106_d();
        var2.capabilities.isCreativeMode = p_147270_1_.func_149103_f();
        var2.capabilities.disableDamage = p_147270_1_.func_149112_c();
        var2.capabilities.allowFlying = p_147270_1_.func_149105_e();
        var2.capabilities.setFlySpeed(p_147270_1_.func_149101_g());
        var2.capabilities.setPlayerWalkSpeed(p_147270_1_.func_149107_h());
    }

    /**
     * Displays the available command-completion options the server knows of
     */
    public void handleTabComplete(S3APacketTabComplete packet)
    {
        String[] candidates = packet.getCandidates();

        if (this.gameController.currentScreen instanceof GuiChat)
        {
            ((GuiChat) this.gameController.currentScreen).handleServerTabComplete(candidates);
        }
    }

    public void handleSoundEffect(S29PacketSoundEffect p_147255_1_)
    {
        this.gameController.theWorld.playSound(p_147255_1_.getX(), p_147255_1_.getY(), p_147255_1_.getZ(), p_147255_1_.getName(), p_147255_1_.getLoudness(), p_147255_1_.func_149209_h(), false);
    }

    /**
     * Handles packets that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
     * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the
     * player instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
     * resourcepack for the client to load.
     */
    public void handleCustomPayload(S3FPacketCustomPayload packet)
    {
        if ("MC|TrList".equals(packet.getChannel()))
        {
            ByteBuf var2 = Unpooled.wrappedBuffer(packet.getPayload());

            try
            {
                int var3 = var2.readInt();
                GuiScreen var4 = this.gameController.currentScreen;

                if (var4 instanceof GuiMerchant && var3 == this.gameController.thePlayer.openContainer.windowId)
                {
                    IMerchant var5 = ((GuiMerchant) var4).func_147035_g();
                    MerchantRecipeList var6 = MerchantRecipeList.func_151390_b(new PacketBuffer(var2));
                    var5.setRecipes(var6);
                }
            } catch (IOException var7)
            {
                LOGGER.error("Couldn't load trade info", var7);
            }
        } else if ("MC|Brand".equals(packet.getChannel()))
        {
            this.gameController.thePlayer.setServerBrand(new String(packet.getPayload(), Charsets.UTF_8));
            //System.out.println(gameController.thePlayer.getServerBrand());
        } else if ("MC|RPack".equals(packet.getChannel()))
        {
            final String var8 = new String(packet.getPayload(), Charsets.UTF_8);

            if (this.gameController.gameSettings.serverTextures)
            {
                if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().acceptsTextures())
                {
                    this.gameController.getResourcePackRepository().func_148526_a(var8);
                } else if (this.gameController.getCurrentServerData() == null || this.gameController.getCurrentServerData().func_147410_c())
                {
                    this.gameController.displayGuiScreen(new GuiYesNo(new GuiScreen()
                    {
                        public void confirmClicked(boolean par1, int par2)
                        {
                            this.mc = Minecraft.getMinecraft();

                            if (this.mc.getCurrentServerData() != null)
                            {
                                this.mc.getCurrentServerData().setAcceptsTextures(par1);
                                ServerList.func_147414_b(this.mc.getCurrentServerData());
                            }

                            if (par1)
                            {
                                this.mc.getResourcePackRepository().func_148526_a(var8);
                            }

                            this.mc.displayGuiScreen(null);
                        }
                    }, I18n.format("multiplayer.texturePrompt.line1"), I18n.format("multiplayer.texturePrompt.line2"), 0));
                }
            }
        }
    }

    /**
     * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
     */
    public void handleScoreboardObjective(S3BPacketScoreboardObjective p_147291_1_)
    {
        Scoreboard var2 = this.clientWorldController.getScoreboard();
        ScoreObjective var3;

        if (p_147291_1_.func_149338_e() == 0)
        {
            var3 = var2.addScoreObjective(p_147291_1_.func_149339_c(), IScoreObjectiveCriteria.field_96641_b);
            var3.setDisplayName(p_147291_1_.func_149337_d());
        } else
        {
            var3 = var2.getObjective(p_147291_1_.func_149339_c());

            if (p_147291_1_.func_149338_e() == 1)
            {
                var2.func_96519_k(var3);
            } else if (p_147291_1_.func_149338_e() == 2)
            {
                var3.setDisplayName(p_147291_1_.func_149337_d());
            }
        }
    }

    /**
     * Either updates the score with a specified value or removes the score for an objective
     */
    public void handleUpdateScore(S3CPacketUpdateScore p_147250_1_)
    {
        Scoreboard var2 = this.clientWorldController.getScoreboard();
        ScoreObjective var3 = var2.getObjective(p_147250_1_.func_149321_d());

        if (p_147250_1_.func_149322_f() == 0)
        {
            Score var4 = var2.func_96529_a(p_147250_1_.func_149324_c(), var3);
            var4.func_96647_c(p_147250_1_.func_149323_e());
        } else if (p_147250_1_.func_149322_f() == 1)
        {
            var2.func_96515_c(p_147250_1_.func_149324_c());
        }
    }

    /**
     * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below
     * name)
     */
    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard p_147254_1_)
    {
        Scoreboard var2 = this.clientWorldController.getScoreboard();

        if (p_147254_1_.func_149370_d().length() == 0)
        {
            var2.func_96530_a(p_147254_1_.func_149371_c(), null);
        } else
        {
            ScoreObjective var3 = var2.getObjective(p_147254_1_.func_149370_d());
            var2.func_96530_a(p_147254_1_.func_149371_c(), var3);
        }
    }

    /**
     * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
     * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
     */
    public void handleTeams(S3EPacketTeams p_147247_1_)
    {
        Scoreboard var2 = this.clientWorldController.getScoreboard();
        ScorePlayerTeam var3;

        if (p_147247_1_.func_149307_h() == 0)
        {
            var3 = var2.createTeam(p_147247_1_.func_149312_c());
        } else
        {
            var3 = var2.getTeam(p_147247_1_.func_149312_c());
        }

        if (p_147247_1_.func_149307_h() == 0 || p_147247_1_.func_149307_h() == 2)
        {
            var3.setTeamName(p_147247_1_.func_149306_d());
            var3.setNamePrefix(p_147247_1_.func_149311_e());
            var3.setNameSuffix(p_147247_1_.func_149309_f());
            var3.func_98298_a(p_147247_1_.func_149308_i());
        }

        Iterator var4;
        String var5;

        if (p_147247_1_.func_149307_h() == 0 || p_147247_1_.func_149307_h() == 3)
        {
            var4 = p_147247_1_.func_149310_g().iterator();

            while (var4.hasNext())
            {
                var5 = (String) var4.next();
                var2.func_151392_a(var5, p_147247_1_.func_149312_c());
            }
        }

        if (p_147247_1_.func_149307_h() == 4)
        {
            var4 = p_147247_1_.func_149310_g().iterator();

            while (var4.hasNext())
            {
                var5 = (String) var4.next();
                var2.removePlayerFromTeam(var5, var3);
            }
        }

        if (p_147247_1_.func_149307_h() == 1)
        {
            var2.removeTeam(var3);
        }
    }

    /**
     * Spawns a specified number of particles at the specified location with a randomized displacement according to
     * specified bounds
     */
    public void handleParticles(S2APacketParticles p_147289_1_)
    {
        if (p_147289_1_.func_149222_k() == 0)
        {
            double var2 = p_147289_1_.func_149227_j() * p_147289_1_.func_149221_g();
            double var4 = p_147289_1_.func_149227_j() * p_147289_1_.func_149224_h();
            double var6 = p_147289_1_.func_149227_j() * p_147289_1_.func_149223_i();
            this.clientWorldController.spawnParticle(p_147289_1_.func_149228_c(), p_147289_1_.func_149220_d(), p_147289_1_.func_149226_e(), p_147289_1_.func_149225_f(), var2, var4, var6);
        } else
        {
            for (int var15 = 0; var15 < p_147289_1_.func_149222_k(); ++var15)
            {
                double var3 = this.avRandomizer.nextGaussian() * (double) p_147289_1_.func_149221_g();
                double var5 = this.avRandomizer.nextGaussian() * (double) p_147289_1_.func_149224_h();
                double var7 = this.avRandomizer.nextGaussian() * (double) p_147289_1_.func_149223_i();
                double var9 = this.avRandomizer.nextGaussian() * (double) p_147289_1_.func_149227_j();
                double var11 = this.avRandomizer.nextGaussian() * (double) p_147289_1_.func_149227_j();
                double var13 = this.avRandomizer.nextGaussian() * (double) p_147289_1_.func_149227_j();
                this.clientWorldController.spawnParticle(p_147289_1_.func_149228_c(), p_147289_1_.func_149220_d() + var3, p_147289_1_.func_149226_e() + var5, p_147289_1_.func_149225_f() + var7, var9, var11, var13);
            }
        }
    }

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and knockback resistance as well as reinforcement spawning chance.
     */
    public void handleEntityProperties(S20PacketEntityProperties p_147290_1_)
    {
        Entity var2 = this.clientWorldController.getEntityByID(p_147290_1_.func_149442_c());

        if (var2 != null)
        {
            if (!(var2 instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
            } else
            {
                BaseAttributeMap var3 = ((EntityLivingBase) var2).getAttributeMap();
                Iterator var4 = p_147290_1_.func_149441_d().iterator();

                while (var4.hasNext())
                {
                    S20PacketEntityProperties.Snapshot var5 = (S20PacketEntityProperties.Snapshot) var4.next();
                    IAttributeInstance var6 = var3.getAttributeInstanceByName(var5.func_151409_a());

                    if (var6 == null)
                    {
                        var6 = var3.registerAttribute(new RangedAttribute(var5.func_151409_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    var6.setBaseValue(var5.func_151410_b());
                    var6.removeAllModifiers();
                    Iterator var7 = var5.func_151408_c().iterator();

                    while (var7.hasNext())
                    {
                        AttributeModifier var8 = (AttributeModifier) var7.next();
                        var6.applyModifier(var8);
                    }
                }
            }
        }
    }

    /**
     * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
     */
    public NetworkManager getNetworkManager()
    {
        return this.netManager;
    }
}
