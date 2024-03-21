package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S12PacketEntityVelocity extends Packet
{
    private int id;
    private int x;
    private int y;
    private int z;
    private static final String __OBFID = "CL_00001328";

    public S12PacketEntityVelocity() {}

    public S12PacketEntityVelocity(Entity p_i45219_1_)
    {
        this(p_i45219_1_.getEntityId(), p_i45219_1_.motionX, p_i45219_1_.motionY, p_i45219_1_.motionZ);
    }

    public S12PacketEntityVelocity(int p_i45220_1_, double p_i45220_2_, double p_i45220_4_, double p_i45220_6_)
    {
        this.id = p_i45220_1_;
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

        this.x = (int)(p_i45220_2_ * 8000.0D);
        this.y = (int)(p_i45220_4_ * 8000.0D);
        this.z = (int)(p_i45220_6_ * 8000.0D);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.id = p_148837_1_.readInt();
        this.x = p_148837_1_.readShort();
        this.y = p_148837_1_.readShort();
        this.z = p_148837_1_.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.id);
        p_148840_1_.writeShort(this.x);
        p_148840_1_.writeShort(this.y);
        p_148840_1_.writeShort(this.z);
    }

    public void processPacket(INetHandlerPlayClient p_149413_1_)
    {
        p_149413_1_.handleEntityVelocity(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, x=%.2f, y=%.2f, z=%.2f", new Object[] {Integer.valueOf(this.id), Float.valueOf((float)this.x / 8000.0F), Float.valueOf((float)this.y / 8000.0F), Float.valueOf((float)this.z / 8000.0F)});
    }

    public int getEntityId()
    {
        return this.id;
    }

    public int getX()
    {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ()
    {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
