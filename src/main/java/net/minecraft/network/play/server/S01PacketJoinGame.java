package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

import java.io.IOException;

public class S01PacketJoinGame extends Packet
{
    private int entityId;
    private boolean hardcore;
    private WorldSettings.GameType gameType;
    private int dimension;
    private EnumDifficulty difficulty;
    private int maxPlayers;
    private WorldType worldType;

    public S01PacketJoinGame()
    {
    }

    public S01PacketJoinGame(int entityId, WorldSettings.GameType gameType, boolean hardcore, int dimension, EnumDifficulty difficulty, int maxPlayers, WorldType worldType)
    {
        this.entityId = entityId;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.gameType = gameType;
        this.maxPlayers = maxPlayers;
        this.hardcore = hardcore;
        this.worldType = worldType;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.entityId = buffer.readInt();
        short gameTypeBit = buffer.readUnsignedByte();
        this.hardcore = (gameTypeBit & 8) == 8;
        this.gameType = WorldSettings.GameType.getByID(gameTypeBit & -9);
        this.dimension = buffer.readByte();
        this.difficulty = EnumDifficulty.getDifficultyEnum(buffer.readUnsignedByte());
        this.maxPlayers = buffer.readUnsignedByte();
        this.worldType = WorldType.parseWorldType(buffer.readStringFromBuffer(16));

        if (this.worldType == null)
        {
            this.worldType = WorldType.DEFAULT;
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeInt(this.entityId);
        int gameTypeBit = this.gameType.getID();

        if (this.hardcore)
        {
            gameTypeBit |= 8;
        }

        buffer.writeByte(gameTypeBit);
        buffer.writeByte(this.dimension);
        buffer.writeByte(this.difficulty.getDifficultyId());
        buffer.writeByte(this.maxPlayers);
        buffer.writeStringToBuffer(this.worldType.getWorldTypeName());
    }

    public void processPacket(INetHandlerPlayClient netHandler)
    {
        netHandler.handleJoinGame(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("eid=%d, gameType=%d, hardcore=%b, dimension=%d, difficulty=%s, maxplayers=%d", this.entityId, this.gameType.getID(), this.hardcore, this.dimension, this.difficulty, this.maxPlayers);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public boolean isHardcore()
    {
        return this.hardcore;
    }

    public WorldSettings.GameType getGameType()
    {
        return this.gameType;
    }

    public int getDimension()
    {
        return this.dimension;
    }

    public EnumDifficulty getDifficulty()
    {
        return this.difficulty;
    }

    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    public WorldType getWorldType()
    {
        return this.worldType;
    }

    public void processPacket(INetHandler netHandler)
    {
        this.processPacket((INetHandlerPlayClient) netHandler);
    }
}
