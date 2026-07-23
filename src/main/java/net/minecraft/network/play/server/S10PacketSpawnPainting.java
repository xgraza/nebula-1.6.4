package net.minecraft.network.play.server;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S10PacketSpawnPainting extends Packet
{
    private int entityId;
    private int x;
    private int y;
    private int z;
    private int direction;
    private String type;

    public S10PacketSpawnPainting()
    {
    }

    public S10PacketSpawnPainting(EntityPainting entity)
    {
        this.entityId = entity.getEntityId();
        this.x = entity.field_146063_b;
        this.y = entity.field_146064_c;
        this.z = entity.field_146062_d;
        this.direction = entity.hangingDirection;
        this.type = entity.art.title;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.entityId = buffer.readVarIntFromBuffer();
        this.type = buffer.readStringFromBuffer(EntityPainting.EnumArt.maxArtTitleLength);
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.direction = buffer.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeVarIntToBuffer(this.entityId);
        buffer.writeStringToBuffer(this.type);
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeInt(this.direction);
    }

    public void processPacket(INetHandlerPlayClient netHandler)
    {
        netHandler.handleSpawnPainting(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, type=%s, x=%d, y=%d, z=%d", this.entityId, this.type, this.x, this.y, this.z);
    }

    public int getEntityID()
    {
        return this.entityId;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }

    public int getDirection()
    {
        return this.direction;
    }

    public String getType()
    {
        return this.type;
    }

    public void processPacket(INetHandler netHandler)
    {
        this.processPacket((INetHandlerPlayClient) netHandler);
    }
}
