package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook extends Packet
{
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private static final String __OBFID = "CL_00001273";

    public S08PacketPlayerPosLook() {}

    public S08PacketPlayerPosLook(double p_i45164_1_, double p_i45164_3_, double p_i45164_5_, float p_i45164_7_, float p_i45164_8_, boolean p_i45164_9_)
    {
        this.posX = p_i45164_1_;
        this.posY = p_i45164_3_;
        this.posZ = p_i45164_5_;
        this.yaw = p_i45164_7_;
        this.pitch = p_i45164_8_;
        this.onGround = p_i45164_9_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.posX = p_148837_1_.readDouble();
        this.posY = p_148837_1_.readDouble();
        this.posZ = p_148837_1_.readDouble();
        this.yaw = p_148837_1_.readFloat();
        this.pitch = p_148837_1_.readFloat();
        this.onGround = p_148837_1_.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeDouble(this.posX);
        p_148840_1_.writeDouble(this.posY);
        p_148840_1_.writeDouble(this.posZ);
        p_148840_1_.writeFloat(this.yaw);
        p_148840_1_.writeFloat(this.pitch);
        p_148840_1_.writeBoolean(this.onGround);
    }

    public void processPacket(INetHandlerPlayClient p_148934_1_)
    {
        p_148934_1_.handlePlayerPosLook(this);
    }

    public double getX()
    {
        return this.posX;
    }

    public double getY()
    {
        return this.posY;
    }

    public double getZ()
    {
        return this.posZ;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public boolean isOnGround()
    {
        return this.onGround;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
