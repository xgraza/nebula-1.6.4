package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C0BPacketEntityAction extends Packet
{
    private int entityID;
    private int action;
    private int aux;

    public C0BPacketEntityAction()
    {

    }

    public C0BPacketEntityAction(Entity entity, int action)
    {
        this(entity, action, 0);
    }

    public C0BPacketEntityAction(Entity entity, int action, int aux)
    {
        this.entityID = entity.getEntityId();
        this.action = action;
        this.aux = aux;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.entityID = buffer.readInt();
        this.action = buffer.readByte();
        this.aux = buffer.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeInt(this.entityID);
        buffer.writeByte(this.action);
        buffer.writeInt(this.aux);
    }

    public void processPacket(INetHandlerPlayServer p_149514_1_)
    {
        p_149514_1_.processEntityAction(this);
    }

    public int getAction()
    {
        return this.action;
    }

    public int getAux()
    {
        return this.aux;
    }

    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer) handler);
    }
}
