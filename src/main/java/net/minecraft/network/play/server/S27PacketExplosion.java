package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class S27PacketExplosion extends Packet
{
    private double x;
    private double y;
    private double z;
    private float size;
    private List<ChunkPosition> field_149155_e;
    private float motionX;
    private float motionY;
    private float motionZ;
    private static final String __OBFID = "CL_00001300";

    public S27PacketExplosion()
    {
    }

    public S27PacketExplosion(double x, double y, double z, float sizze, List<ChunkPosition> p_i45193_8_, Vec3 p_i45193_9_)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = sizze;
        this.field_149155_e = new ArrayList<>(p_i45193_8_);

        if (p_i45193_9_ != null)
        {
            this.motionX = (float) p_i45193_9_.xCoord;
            this.motionY = (float) p_i45193_9_.yCoord;
            this.motionZ = (float) p_i45193_9_.zCoord;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.x = p_148837_1_.readFloat();
        this.y = p_148837_1_.readFloat();
        this.z = p_148837_1_.readFloat();
        this.size = p_148837_1_.readFloat();
        int var2 = p_148837_1_.readInt();
        this.field_149155_e = new ArrayList<>(var2);
        int var3 = (int) this.x;
        int var4 = (int) this.y;
        int var5 = (int) this.z;

        for (int var6 = 0; var6 < var2; ++var6)
        {
            int var7 = p_148837_1_.readByte() + var3;
            int var8 = p_148837_1_.readByte() + var4;
            int var9 = p_148837_1_.readByte() + var5;
            this.field_149155_e.add(new ChunkPosition(var7, var8, var9));
        }

        this.motionX = p_148837_1_.readFloat();
        this.motionY = p_148837_1_.readFloat();
        this.motionZ = p_148837_1_.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeFloat((float) this.x);
        p_148840_1_.writeFloat((float) this.y);
        p_148840_1_.writeFloat((float) this.z);
        p_148840_1_.writeFloat(this.size);
        p_148840_1_.writeInt(this.field_149155_e.size());
        int var2 = (int) this.x;
        int var3 = (int) this.y;
        int var4 = (int) this.z;

        for (ChunkPosition o : this.field_149155_e)
        {
            int var7 = o.xCoord - var2;
            int var8 = o.field_151327_b - var3;
            int var9 = o.yCoord - var4;
            p_148840_1_.writeByte(var7);
            p_148840_1_.writeByte(var8);
            p_148840_1_.writeByte(var9);
        }

        p_148840_1_.writeFloat(this.motionX);
        p_148840_1_.writeFloat(this.motionY);
        p_148840_1_.writeFloat(this.motionY);
    }

    public void processPacket(INetHandlerPlayClient p_149151_1_)
    {
        p_149151_1_.handleExplosion(this);
    }

    public float getX()
    {
        return this.motionX;
    }

    public void setX(float x)
    {
        this.motionX = x;
    }

    public float getY()
    {
        return this.motionY;
    }

    public void setY(float y)
    {
        this.motionY = y;
    }

    public float getZ()
    {
        return this.motionZ;
    }

    public void setZ(float z)
    {
        this.motionY = z;
    }

    public double func_149148_f()
    {
        return this.x;
    }

    public double func_149143_g()
    {
        return this.y;
    }

    public double func_149145_h()
    {
        return this.z;
    }

    public float getSize()
    {
        return this.size;
    }

    public List func_149150_j()
    {
        return this.field_149155_e;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
