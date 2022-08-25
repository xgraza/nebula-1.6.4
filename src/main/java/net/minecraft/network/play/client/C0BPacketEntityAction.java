package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0BPacketEntityAction extends Packet
{
    public int entityId;
    public int action;
    public int aux;

    public C0BPacketEntityAction() {}

    public C0BPacketEntityAction(Entity p_i45259_1_, int p_i45259_2_)
    {
        this(p_i45259_1_, p_i45259_2_, 0);
    }

    public C0BPacketEntityAction(Entity p_i45260_1_, int p_i45260_2_, int p_i45260_3_)
    {
        this.entityId = p_i45260_1_.getEntityId();
        this.action = p_i45260_2_;
        this.aux = p_i45260_3_;
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.entityId = p_148837_1_.readInt();
        this.action = p_148837_1_.readByte();
        this.aux = p_148837_1_.readInt();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.entityId);
        p_148840_1_.writeByte(this.action);
        p_148840_1_.writeInt(this.aux);
    }

    public void processPacket(INetHandlerPlayServer p_149514_1_)
    {
        p_149514_1_.processEntityAction(this);
    }

    public int func_149513_d()
    {
        return this.action;
    }

    public int func_149512_e()
    {
        return this.aux;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }
}
