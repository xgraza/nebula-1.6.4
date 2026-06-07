package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S1BPacketEntityAttach extends Packet
{
    private int action;
    private int entityId;
    private int ridingEntityId;

    public S1BPacketEntityAttach()
    {
    }

    public S1BPacketEntityAttach(int action, Entity entity, Entity ridingEntity)
    {
        this.action = action;
        this.entityId = entity.getEntityId();
        this.ridingEntityId = ridingEntity != null ? ridingEntity.getEntityId() : -1;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.entityId = p_148837_1_.readInt();
        this.ridingEntityId = p_148837_1_.readInt();
        this.action = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.entityId);
        p_148840_1_.writeInt(this.ridingEntityId);
        p_148840_1_.writeByte(this.action);
    }

    public void processPacket(INetHandlerPlayClient p_149405_1_)
    {
        p_149405_1_.handleEntityAttach(this);
    }

    public int getAction()
    {
        return this.action;
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public int getRidingEntityId()
    {
        return this.ridingEntityId;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
