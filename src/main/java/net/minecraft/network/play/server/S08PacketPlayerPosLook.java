package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook extends Packet
{
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    private boolean field_148935_f;
    private static final String __OBFID = "CL_00001273";

    public S08PacketPlayerPosLook() {}

    public S08PacketPlayerPosLook(double p_i45164_1_, double p_i45164_3_, double p_i45164_5_, float p_i45164_7_, float p_i45164_8_, boolean p_i45164_9_)
    {
        this.x = p_i45164_1_;
        this.y = p_i45164_3_;
        this.z = p_i45164_5_;
        this.yaw = p_i45164_7_;
        this.pitch = p_i45164_8_;
        this.field_148935_f = p_i45164_9_;
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.x = p_148837_1_.readDouble();
        this.y = p_148837_1_.readDouble();
        this.z = p_148837_1_.readDouble();
        this.yaw = p_148837_1_.readFloat();
        this.pitch = p_148837_1_.readFloat();
        this.field_148935_f = p_148837_1_.readBoolean();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeDouble(this.x);
        p_148840_1_.writeDouble(this.y);
        p_148840_1_.writeDouble(this.z);
        p_148840_1_.writeFloat(this.yaw);
        p_148840_1_.writeFloat(this.pitch);
        p_148840_1_.writeBoolean(this.field_148935_f);
    }

    public void processPacket(INetHandlerPlayClient p_148934_1_)
    {
        p_148934_1_.handlePlayerPosLook(this);
    }

    public double func_148932_c()
    {
        return this.x;
    }

    public double func_148928_d()
    {
        return this.y;
    }

    public double func_148933_e()
    {
        return this.z;
    }

    public float func_148931_f()
    {
        return this.yaw;
    }

    public float func_148930_g()
    {
        return this.pitch;
    }

    public boolean func_148929_h()
    {
        return this.field_148935_f;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
