package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S12PacketEntityVelocity extends Packet
{
    public int entityId;
    public int motionX;
    public int motionY;
    public int motionZ;
    private static final String __OBFID = "CL_00001328";

    public S12PacketEntityVelocity() {}

    public S12PacketEntityVelocity(Entity p_i45219_1_)
    {
        this(p_i45219_1_.getEntityId(), p_i45219_1_.motionX, p_i45219_1_.motionY, p_i45219_1_.motionZ);
    }

    public S12PacketEntityVelocity(int p_i45220_1_, double p_i45220_2_, double p_i45220_4_, double p_i45220_6_)
    {
        this.entityId = p_i45220_1_;
        double var8 = 3.9D;

        if (p_i45220_2_ < -var8)
        {
            p_i45220_2_ = -var8;
        }

        if (p_i45220_4_ < -var8)
        {
            p_i45220_4_ = -var8;
        }

        if (p_i45220_6_ < -var8)
        {
            p_i45220_6_ = -var8;
        }

        if (p_i45220_2_ > var8)
        {
            p_i45220_2_ = var8;
        }

        if (p_i45220_4_ > var8)
        {
            p_i45220_4_ = var8;
        }

        if (p_i45220_6_ > var8)
        {
            p_i45220_6_ = var8;
        }

        this.motionX = (int)(p_i45220_2_ * 8000.0D);
        this.motionY = (int)(p_i45220_4_ * 8000.0D);
        this.motionZ = (int)(p_i45220_6_ * 8000.0D);
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.entityId = p_148837_1_.readInt();
        this.motionX = p_148837_1_.readShort();
        this.motionY = p_148837_1_.readShort();
        this.motionZ = p_148837_1_.readShort();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.entityId);
        p_148840_1_.writeShort(this.motionX);
        p_148840_1_.writeShort(this.motionY);
        p_148840_1_.writeShort(this.motionZ);
    }

    public void processPacket(INetHandlerPlayClient p_149413_1_)
    {
        p_149413_1_.handleEntityVelocity(this);
    }

    public String serialize()
    {
        return String.format("id=%d, x=%.2f, y=%.2f, z=%.2f", new Object[] {Integer.valueOf(this.entityId), Float.valueOf((float)this.motionX / 8000.0F), Float.valueOf((float)this.motionY / 8000.0F), Float.valueOf((float)this.motionZ / 8000.0F)});
    }

    public int func_149412_c()
    {
        return this.entityId;
    }

    public int func_149411_d()
    {
        return this.motionX;
    }

    public int func_149410_e()
    {
        return this.motionY;
    }

    public int func_149409_f()
    {
        return this.motionZ;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
