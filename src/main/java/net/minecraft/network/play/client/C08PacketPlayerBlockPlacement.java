package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C08PacketPlayerBlockPlacement extends Packet
{
    private int posX;
    private int posY;
    private int posZ;
    private int side;
    private ItemStack stack;
    private float faceX;
    private float faceY;
    private float faceZ;
    private static final String __OBFID = "CL_00001371";

    public C08PacketPlayerBlockPlacement() {}

    public C08PacketPlayerBlockPlacement(final ItemStack itemStack) {
        this(-1, -1, -1, 255, itemStack, 0.0f, 0.0f, 0.0f);
    }

    public C08PacketPlayerBlockPlacement(int posX, int posY, int posZ, int direction, ItemStack p_i45265_5_, float p_i45265_6_, float p_i45265_7_, float p_i45265_8_)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.side = direction;
        this.stack = p_i45265_5_ != null ? p_i45265_5_.copy() : null;
        this.faceX = p_i45265_6_;
        this.faceY = p_i45265_7_;
        this.faceZ = p_i45265_8_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.posX = p_148837_1_.readInt();
        this.posY = p_148837_1_.readUnsignedByte();
        this.posZ = p_148837_1_.readInt();
        this.side = p_148837_1_.readUnsignedByte();
        this.stack = p_148837_1_.readItemStackFromBuffer();
        this.faceX = (float)p_148837_1_.readUnsignedByte() / 16.0F;
        this.faceY = (float)p_148837_1_.readUnsignedByte() / 16.0F;
        this.faceZ = (float)p_148837_1_.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.posX);
        p_148840_1_.writeByte(this.posY);
        p_148840_1_.writeInt(this.posZ);
        p_148840_1_.writeByte(this.side);
        p_148840_1_.writeItemStackToBuffer(this.stack);
        p_148840_1_.writeByte((int)(this.faceX * 16.0F));
        p_148840_1_.writeByte((int)(this.faceY * 16.0F));
        p_148840_1_.writeByte((int)(this.faceZ * 16.0F));
    }

    public void processPacket(INetHandlerPlayServer p_149572_1_)
    {
        p_149572_1_.processPlayerBlockPlacement(this);
    }

    public int getPosX()
    {
        return this.posX;
    }

    public int getPosY()
    {
        return this.posY;
    }

    public int getPosZ()
    {
        return this.posZ;
    }

    public int getSide()
    {
        return this.side;
    }

    public ItemStack getItemStack()
    {
        return this.stack;
    }

    public float getFaceX()
    {
        return this.faceX;
    }

    public float getFaceY()
    {
        return this.faceY;
    }

    public float getFaceZ()
    {
        return this.faceZ;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }
}
