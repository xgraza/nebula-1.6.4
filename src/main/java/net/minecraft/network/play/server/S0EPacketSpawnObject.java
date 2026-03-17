package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

import java.io.IOException;

public class S0EPacketSpawnObject extends Packet
{
    private int id;
    private int x;
    private int y;
    private int z;
    private int field_149015_e;
    private int field_149012_f;
    private int field_149013_g;
    private int field_149021_h;
    private int field_149022_i;
    private int type;
    private int field_149020_k;
    private static final String __OBFID = "CL_00001276";

    public S0EPacketSpawnObject()
    {
    }

    public S0EPacketSpawnObject(Entity p_i45165_1_, int p_i45165_2_)
    {
        this(p_i45165_1_, p_i45165_2_, 0);
    }

    public S0EPacketSpawnObject(Entity p_i45166_1_, int p_i45166_2_, int p_i45166_3_)
    {
        this.id = p_i45166_1_.getEntityId();
        this.x = MathHelper.floor_double(p_i45166_1_.posX * 32.0D);
        this.y = MathHelper.floor_double(p_i45166_1_.posY * 32.0D);
        this.z = MathHelper.floor_double(p_i45166_1_.posZ * 32.0D);
        this.field_149021_h = MathHelper.floor_float(p_i45166_1_.rotationPitch * 256.0F / 360.0F);
        this.field_149022_i = MathHelper.floor_float(p_i45166_1_.rotationYaw * 256.0F / 360.0F);
        this.type = p_i45166_2_;
        this.field_149020_k = p_i45166_3_;

        if (p_i45166_3_ > 0)
        {
            double var4 = p_i45166_1_.motionX;
            double var6 = p_i45166_1_.motionY;
            double var8 = p_i45166_1_.motionZ;
            double var10 = 3.9D;

            if (var4 < -var10)
            {
                var4 = -var10;
            }

            if (var6 < -var10)
            {
                var6 = -var10;
            }

            if (var8 < -var10)
            {
                var8 = -var10;
            }

            if (var4 > var10)
            {
                var4 = var10;
            }

            if (var6 > var10)
            {
                var6 = var10;
            }

            if (var8 > var10)
            {
                var8 = var10;
            }

            this.field_149015_e = (int) (var4 * 8000.0D);
            this.field_149012_f = (int) (var6 * 8000.0D);
            this.field_149013_g = (int) (var8 * 8000.0D);
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.id = p_148837_1_.readVarIntFromBuffer();
        this.type = p_148837_1_.readByte();
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readInt();
        this.z = p_148837_1_.readInt();
        this.field_149021_h = p_148837_1_.readByte();
        this.field_149022_i = p_148837_1_.readByte();
        this.field_149020_k = p_148837_1_.readInt();

        if (this.field_149020_k > 0)
        {
            this.field_149015_e = p_148837_1_.readShort();
            this.field_149012_f = p_148837_1_.readShort();
            this.field_149013_g = p_148837_1_.readShort();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeVarIntToBuffer(this.id);
        p_148840_1_.writeByte(this.type);
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeInt(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeByte(this.field_149021_h);
        p_148840_1_.writeByte(this.field_149022_i);
        p_148840_1_.writeInt(this.field_149020_k);

        if (this.field_149020_k > 0)
        {
            p_148840_1_.writeShort(this.field_149015_e);
            p_148840_1_.writeShort(this.field_149012_f);
            p_148840_1_.writeShort(this.field_149013_g);
        }
    }

    public void processPacket(INetHandlerPlayClient p_149011_1_)
    {
        p_149011_1_.handleSpawnObject(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f", this.id, this.type, (float) this.x / 32.0F, (float) this.y / 32.0F, (float) this.z / 32.0F);
    }

    public int getID()
    {
        return this.id;
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

    public int func_149010_g()
    {
        return this.field_149015_e;
    }

    public int func_149004_h()
    {
        return this.field_149012_f;
    }

    public int func_148999_i()
    {
        return this.field_149013_g;
    }

    public int func_149008_j()
    {
        return this.field_149021_h;
    }

    public int func_149006_k()
    {
        return this.field_149022_i;
    }

    public int getType()
    {
        return this.type;
    }

    public int func_149009_m()
    {
        return this.field_149020_k;
    }

    public void setX(int p_148996_1_)
    {
        this.x = p_148996_1_;
    }

    public void setY(int p_148995_1_)
    {
        this.y = p_148995_1_;
    }

    public void setZ(int p_149005_1_)
    {
        this.z = p_149005_1_;
    }

    public void func_149003_d(int p_149003_1_)
    {
        this.field_149015_e = p_149003_1_;
    }

    public void func_149000_e(int p_149000_1_)
    {
        this.field_149012_f = p_149000_1_;
    }

    public void func_149007_f(int p_149007_1_)
    {
        this.field_149013_g = p_149007_1_;
    }

    public void func_149002_g(int p_149002_1_)
    {
        this.field_149020_k = p_149002_1_;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
