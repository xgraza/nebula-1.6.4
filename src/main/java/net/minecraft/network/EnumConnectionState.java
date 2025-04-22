package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Map;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import org.apache.logging.log4j.LogManager;

public enum EnumConnectionState
{
    HANDSHAKING(-1)
    {
        {
            this.registerServerboundPacket(0, C00Handshake.class);
        }
    },
    PLAY(0)
    {
        {
            this.registerClientboundPacket(0, S00PacketKeepAlive.class);
            this.registerClientboundPacket(1, S01PacketJoinGame.class);
            this.registerClientboundPacket(2, S02PacketChat.class);
            this.registerClientboundPacket(3, S03PacketTimeUpdate.class);
            this.registerClientboundPacket(4, S04PacketEntityEquipment.class);
            this.registerClientboundPacket(5, S05PacketSpawnPosition.class);
            this.registerClientboundPacket(6, S06PacketUpdateHealth.class);
            this.registerClientboundPacket(7, S07PacketRespawn.class);
            this.registerClientboundPacket(8, S08PacketPlayerPosLook.class);
            this.registerClientboundPacket(9, S09PacketHeldItemChange.class);
            this.registerClientboundPacket(10, S0APacketUseBed.class);
            this.registerClientboundPacket(11, S0BPacketAnimation.class);
            this.registerClientboundPacket(12, S0CPacketSpawnPlayer.class);
            this.registerClientboundPacket(13, S0DPacketCollectItem.class);
            this.registerClientboundPacket(14, S0EPacketSpawnObject.class);
            this.registerClientboundPacket(15, S0FPacketSpawnMob.class);
            this.registerClientboundPacket(16, S10PacketSpawnPainting.class);
            this.registerClientboundPacket(17, S11PacketSpawnExperienceOrb.class);
            this.registerClientboundPacket(18, S12PacketEntityVelocity.class);
            this.registerClientboundPacket(19, S13PacketDestroyEntities.class);
            this.registerClientboundPacket(20, S14PacketEntity.class);
            this.registerClientboundPacket(21, S14PacketEntity.S15PacketEntityRelMove.class);
            this.registerClientboundPacket(22, S14PacketEntity.S16PacketEntityLook.class);
            this.registerClientboundPacket(23, S14PacketEntity.S17PacketEntityLookMove.class);
            this.registerClientboundPacket(24, S18PacketEntityTeleport.class);
            this.registerClientboundPacket(25, S19PacketEntityHeadLook.class);
            this.registerClientboundPacket(26, S19PacketEntityStatus.class);
            this.registerClientboundPacket(27, S1BPacketEntityAttach.class);
            this.registerClientboundPacket(28, S1CPacketEntityMetadata.class);
            this.registerClientboundPacket(29, S1DPacketEntityEffect.class);
            this.registerClientboundPacket(30, S1EPacketRemoveEntityEffect.class);
            this.registerClientboundPacket(31, S1FPacketSetExperience.class);
            this.registerClientboundPacket(32, S20PacketEntityProperties.class);
            this.registerClientboundPacket(33, S21PacketChunkData.class);
            this.registerClientboundPacket(34, S22PacketMultiBlockChange.class);
            this.registerClientboundPacket(35, S23PacketBlockChange.class);
            this.registerClientboundPacket(36, S24PacketBlockAction.class);
            this.registerClientboundPacket(37, S25PacketBlockBreakAnim.class);
            this.registerClientboundPacket(38, S26PacketMapChunkBulk.class);
            this.registerClientboundPacket(39, S27PacketExplosion.class);
            this.registerClientboundPacket(40, S28PacketEffect.class);
            this.registerClientboundPacket(41, S29PacketSoundEffect.class);
            this.registerClientboundPacket(42, S2APacketParticles.class);
            this.registerClientboundPacket(43, S2BPacketChangeGameState.class);
            this.registerClientboundPacket(44, S2CPacketSpawnGlobalEntity.class);
            this.registerClientboundPacket(45, S2DPacketOpenWindow.class);
            this.registerClientboundPacket(46, S2EPacketCloseWindow.class);
            this.registerClientboundPacket(47, S2FPacketSetSlot.class);
            this.registerClientboundPacket(48, S30PacketWindowItems.class);
            this.registerClientboundPacket(49, S31PacketWindowProperty.class);
            this.registerClientboundPacket(50, S32PacketConfirmTransaction.class);
            this.registerClientboundPacket(51, S33PacketUpdateSign.class);
            this.registerClientboundPacket(52, S34PacketMaps.class);
            this.registerClientboundPacket(53, S35PacketUpdateTileEntity.class);
            this.registerClientboundPacket(54, S36PacketSignEditorOpen.class);
            this.registerClientboundPacket(55, S37PacketStatistics.class);
            this.registerClientboundPacket(56, S38PacketPlayerListItem.class);
            this.registerClientboundPacket(57, S39PacketPlayerAbilities.class);
            this.registerClientboundPacket(58, S3APacketTabComplete.class);
            this.registerClientboundPacket(59, S3BPacketScoreboardObjective.class);
            this.registerClientboundPacket(60, S3CPacketUpdateScore.class);
            this.registerClientboundPacket(61, S3DPacketDisplayScoreboard.class);
            this.registerClientboundPacket(62, S3EPacketTeams.class);
            this.registerClientboundPacket(63, S3FPacketCustomPayload.class);
            this.registerClientboundPacket(64, S40PacketDisconnect.class);
            this.registerServerboundPacket(0, C00PacketKeepAlive.class);
            this.registerServerboundPacket(1, C01PacketChatMessage.class);
            this.registerServerboundPacket(2, C02PacketUseEntity.class);
            this.registerServerboundPacket(3, C03PacketPlayer.class);
            this.registerServerboundPacket(4, C03PacketPlayer.C04PacketPlayerPosition.class);
            this.registerServerboundPacket(5, C03PacketPlayer.C05PacketPlayerLook.class);
            this.registerServerboundPacket(6, C03PacketPlayer.C06PacketPlayerPosLook.class);
            this.registerServerboundPacket(7, C07PacketPlayerDigging.class);
            this.registerServerboundPacket(8, C08PacketPlayerBlockPlacement.class);
            this.registerServerboundPacket(9, C09PacketHeldItemChange.class);
            this.registerServerboundPacket(10, C0APacketAnimation.class);
            this.registerServerboundPacket(11, C0BPacketEntityAction.class);
            this.registerServerboundPacket(12, C0CPacketInput.class);
            this.registerServerboundPacket(13, C0DPacketCloseWindow.class);
            this.registerServerboundPacket(14, C0EPacketClickWindow.class);
            this.registerServerboundPacket(15, C0FPacketConfirmTransaction.class);
            this.registerServerboundPacket(16, C10PacketCreativeInventoryAction.class);
            this.registerServerboundPacket(17, C11PacketEnchantItem.class);
            this.registerServerboundPacket(18, C12PacketUpdateSign.class);
            this.registerServerboundPacket(19, C13PacketPlayerAbilities.class);
            this.registerServerboundPacket(20, C14PacketTabComplete.class);
            this.registerServerboundPacket(21, C15PacketClientSettings.class);
            this.registerServerboundPacket(22, C16PacketClientStatus.class);
            this.registerServerboundPacket(23, C17PacketCustomPayload.class);
        }
    },
    STATUS(1)
    {
        {
            this.registerServerboundPacket(0, C00PacketServerQuery.class);
            this.registerClientboundPacket(0, S00PacketServerInfo.class);
            this.registerServerboundPacket(1, C01PacketPing.class);
            this.registerClientboundPacket(1, S01PacketPong.class);
        }
    },
    LOGIN(2)
    {
        {
            this.registerClientboundPacket(0, S00PacketDisconnect.class);
            this.registerClientboundPacket(1, S01PacketEncryptionRequest.class);
            this.registerClientboundPacket(2, S02PacketLoginSuccess.class);
            this.registerServerboundPacket(0, C00PacketLoginStart.class);
            this.registerServerboundPacket(1, C01PacketEncryptionResponse.class);
        }
    };
    private static final TIntObjectMap<EnumConnectionState> field_150764_e = new TIntObjectHashMap<>();
    private static final Map<Class<? extends Packet>, EnumConnectionState> packetProtocolMap = Maps.newHashMap();
    private final int type;
    private final BiMap<Integer, Class<? extends Packet>> serverboundPacketMap = HashBiMap.create();
    private final BiMap<Integer, Class<? extends Packet>> clientboundPacketMap = HashBiMap.create();

    EnumConnectionState(int type)
    {
        this.type = type;
    }

    protected void registerServerboundPacket(int packetId, Class<? extends Packet> packetClass)
    {
        if (this.serverboundPacketMap.containsKey(packetId))
        {
            final String var3 = "Serverbound packet ID " + packetId + " is already assigned to " + this.serverboundPacketMap.get(Integer.valueOf(packetId)) + "; cannot re-assign to " + packetClass;
            LogManager.getLogger().fatal(var3);
            throw new IllegalArgumentException(var3);
        }
        else if (this.serverboundPacketMap.containsValue(packetClass))
        {
            final String var3 = "Serverbound packet " + packetClass + " is already assigned to ID " + this.serverboundPacketMap.inverse().get(packetClass) + "; cannot re-assign to " + packetId;
            LogManager.getLogger().fatal(var3);
            throw new IllegalArgumentException(var3);
        }
        else
        {
            this.serverboundPacketMap.put(packetId, packetClass);
        }
    }

    protected EnumConnectionState registerClientboundPacket(int packetId, Class<? extends Packet> packetClass)
    {
        String var3;

        if (this.clientboundPacketMap.containsKey(packetId))
        {
            var3 = "Clientbound packet ID " + packetId + " is already assigned to " + this.clientboundPacketMap.get(Integer.valueOf(packetId)) + "; cannot re-assign to " + packetClass;
            LogManager.getLogger().fatal(var3);
            throw new IllegalArgumentException(var3);
        }
        else if (this.clientboundPacketMap.containsValue(packetClass))
        {
            var3 = "Clientbound packet " + packetClass + " is already assigned to ID " + this.clientboundPacketMap.inverse().get(packetClass) + "; cannot re-assign to " + packetId;
            LogManager.getLogger().fatal(var3);
            throw new IllegalArgumentException(var3);
        }
        else
        {
            this.clientboundPacketMap.put(packetId, packetClass);
            return this;
        }
    }

    public BiMap<Integer, Class<? extends Packet>> getServerboundPacketMap()
    {
        return this.serverboundPacketMap;
    }

    public BiMap<Integer, Class<? extends Packet>> getClientboundPacketMap()
    {
        return this.clientboundPacketMap;
    }

    public BiMap<Integer, Class<? extends Packet>> getReceivablePackets(boolean clientSide)
    {
        return clientSide ? this.getClientboundPacketMap() : this.getServerboundPacketMap();
    }

    public BiMap<Integer, Class<? extends Packet>> getSendablePackets(boolean clientSide)
    {
        return clientSide ? this.getServerboundPacketMap() : this.getClientboundPacketMap();
    }

    public int getType()
    {
        return this.type;
    }

    public static EnumConnectionState getStateForType(int type)
    {
        return field_150764_e.get(type);
    }

    public static EnumConnectionState getStateForPacket(Packet packet)
    {
        return packetProtocolMap.get(packet.getClass());
    }

    static {
        for (final EnumConnectionState state : values())
        {
            field_150764_e.put(state.getType(), state);

            final Iterable<Class<? extends Packet>> iterable = Iterables.concat(
                    state.getClientboundPacketMap().values(),
                    state.getServerboundPacketMap().values());
            for (final Class<? extends Packet> packetClass : iterable)
            {
                if (packetProtocolMap.containsKey(packetClass) && packetProtocolMap.get(packetClass) != state)
                {
                    throw new Error("Packet " + packetClass + " is already assigned to protocol " + packetProtocolMap.get(packetClass) + " - can\'t reassign to " + state);
                }

                packetProtocolMap.put(packetClass, state);
            }
        }
    }
}
