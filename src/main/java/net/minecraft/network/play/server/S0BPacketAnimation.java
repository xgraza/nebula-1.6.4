package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0BPacketAnimation extends Packet
{
    private int field_148981_a;
    private int field_148980_b;
    private static final String __OBFID = "CL_00001282";

    public S0BPacketAnimation() {}

    public S0BPacketAnimation(Entity p_i45172_1_, int p_i45172_2_)
    {
        this.field_148981_a = p_i45172_1_.getEntityId();
        this.field_148980_b = p_i45172_2_;
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_148981_a = p_148837_1_.readVarIntFromBuffer();
        this.field_148980_b = p_148837_1_.readUnsignedByte();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeVarIntToBuffer(this.field_148981_a);
        p_148840_1_.writeByte(this.field_148980_b);
    }

    public void processPacket(INetHandlerPlayClient p_148979_1_)
    {
        p_148979_1_.handleAnimation(this);
    }

    public String serialize()
    {
        return String.format("id=%d, type=%d", new Object[] {Integer.valueOf(this.field_148981_a), Integer.valueOf(this.field_148980_b)});
    }

    public int func_148978_c()
    {
        return this.field_148981_a;
    }

    public int func_148977_d()
    {
        return this.field_148980_b;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
