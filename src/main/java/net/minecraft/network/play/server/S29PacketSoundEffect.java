package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.Validate;

public class S29PacketSoundEffect extends Packet
{
    private String field_149219_a;
    public int x;
    public int y = Integer.MAX_VALUE;
    public int z;
    private float field_149216_e;
    private int field_149214_f;
    private static final String __OBFID = "CL_00001309";

    public S29PacketSoundEffect() {}

    public S29PacketSoundEffect(String p_i45200_1_, double p_i45200_2_, double p_i45200_4_, double p_i45200_6_, float p_i45200_8_, float p_i45200_9_)
    {
        Validate.notNull(p_i45200_1_, "name", new Object[0]);
        this.field_149219_a = p_i45200_1_;
        this.x = (int)(p_i45200_2_ * 8.0D);
        this.y = (int)(p_i45200_4_ * 8.0D);
        this.z = (int)(p_i45200_6_ * 8.0D);
        this.field_149216_e = p_i45200_8_;
        this.field_149214_f = (int)(p_i45200_9_ * 63.0F);

        if (this.field_149214_f < 0)
        {
            this.field_149214_f = 0;
        }

        if (this.field_149214_f > 255)
        {
            this.field_149214_f = 255;
        }
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149219_a = p_148837_1_.readStringFromBuffer(256);
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readInt();
        this.z = p_148837_1_.readInt();
        this.field_149216_e = p_148837_1_.readFloat();
        this.field_149214_f = p_148837_1_.readUnsignedByte();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeStringToBuffer(this.field_149219_a);
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeInt(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeFloat(this.field_149216_e);
        p_148840_1_.writeByte(this.field_149214_f);
    }

    public String func_149212_c()
    {
        return this.field_149219_a;
    }

    public double getX()
    {
        return (double)((float)this.x / 8.0F);
    }

    public double getY()
    {
        return (double)((float)this.y / 8.0F);
    }

    public double getZ()
    {
        return (double)((float)this.z / 8.0F);
    }

    public float func_149208_g()
    {
        return this.field_149216_e;
    }

    public float func_149209_h()
    {
        return (float)this.field_149214_f / 63.0F;
    }

    public void processPacket(INetHandlerPlayClient p_149213_1_)
    {
        p_149213_1_.handleSoundEffect(this);
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
