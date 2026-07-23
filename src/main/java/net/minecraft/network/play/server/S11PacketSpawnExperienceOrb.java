package net.minecraft.network.play.server;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

import java.io.IOException;

public class S11PacketSpawnExperienceOrb extends Packet
{
    private int entityId;
    private int x;
    private int y;
    private int z;
    private int value;

    public S11PacketSpawnExperienceOrb()
    {
    }

    public S11PacketSpawnExperienceOrb(EntityXPOrb entity)
    {
        this.entityId = entity.getEntityId();
        this.x = MathHelper.floor_double(entity.posX * 32.0D);
        this.y = MathHelper.floor_double(entity.posY * 32.0D);
        this.z = MathHelper.floor_double(entity.posZ * 32.0D);
        this.value = entity.getXpValue();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.entityId = buffer.readVarIntFromBuffer();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.value = buffer.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeVarIntToBuffer(this.entityId);
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeShort(this.value);
    }

    public void processPacket(INetHandlerPlayClient netHandle)
    {
        netHandle.handleSpawnExperienceOrb(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, value=%d, x=%.2f, y=%.2f, z=%.2f", this.entityId, this.value, (float) this.x / 32.0F, (float) this.y / 32.0F, (float) this.z / 32.0F);
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

    public int getValue()
    {
        return this.value;
    }

    public void processPacket(INetHandler netHandler)
    {
        this.processPacket((INetHandlerPlayClient) netHandler);
    }
}
