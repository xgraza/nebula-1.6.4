package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

import java.io.IOException;

// really should be called spawn entity bolt... that's all its used for... and all it does...
public class S2CPacketSpawnGlobalEntity extends Packet
{
    private int entityId;
    private int x;
    private int y;
    private int z;
    private int type;

    public S2CPacketSpawnGlobalEntity()
    {
    }

    public S2CPacketSpawnGlobalEntity(Entity entity)
    {
        this.entityId = entity.getEntityId();
        this.x = MathHelper.floor_double(entity.posX * 32.0D);
        this.y = MathHelper.floor_double(entity.posY * 32.0D);
        this.z = MathHelper.floor_double(entity.posZ * 32.0D);

        if (entity instanceof EntityLightningBolt)
        {
            this.type = 1;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.entityId = buffer.readVarIntFromBuffer();
        this.type = buffer.readByte();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeVarIntToBuffer(this.entityId);
        buffer.writeByte(this.type);
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
    }

    public void processPacket(INetHandlerPlayClient netHandler)
    {
        netHandler.handleSpawnGlobalEntity(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f", this.entityId, this.type, (float) this.x / 32.0F, (float) this.y / 32.0F, (float) this.z / 32.0F);
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

    public int getType()
    {
        return this.type;
    }

    public void processPacket(INetHandler netHandler)
    {
        this.processPacket((INetHandlerPlayClient) netHandler);
    }
}
