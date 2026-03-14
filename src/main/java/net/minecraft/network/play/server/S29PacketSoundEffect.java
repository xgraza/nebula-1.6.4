package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

public class S29PacketSoundEffect extends Packet
{
    private String name;
    private int x;
    private int y = Integer.MAX_VALUE;
    private int z;
    private float loudness;
    private int field_149214_f;

    public S29PacketSoundEffect()
    {
    }

    public S29PacketSoundEffect(String name, double x, double y, double z, float loudness, float p_i45200_9_)
    {
        Validate.notNull(name, "name");
        this.name = name;
        this.x = (int) (x * 8.0D);
        this.y = (int) (y * 8.0D);
        this.z = (int) (z * 8.0D);
        this.loudness = loudness;
        this.field_149214_f = (int) (p_i45200_9_ * 63.0F);

        if (this.field_149214_f < 0)
        {
            this.field_149214_f = 0;
        }

        if (this.field_149214_f > 255)
        {
            this.field_149214_f = 255;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.name = p_148837_1_.readStringFromBuffer(256);
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readInt();
        this.z = p_148837_1_.readInt();
        this.loudness = p_148837_1_.readFloat();
        this.field_149214_f = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeStringToBuffer(this.name);
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeInt(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeFloat(this.loudness);
        p_148840_1_.writeByte(this.field_149214_f);
    }

    public String getName()
    {
        return this.name;
    }

    public double getX()
    {
        return (float) this.x / 8.0F;
    }

    public double getY()
    {
        return (float) this.y / 8.0F;
    }

    public double getZ()
    {
        return (float) this.z / 8.0F;
    }

    public float getLoudness()
    {
        return this.loudness;
    }

    public float func_149209_h()
    {
        return (float) this.field_149214_f / 63.0F;
    }

    public void processPacket(INetHandlerPlayClient p_149213_1_)
    {
        p_149213_1_.handleSoundEffect(this);
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
