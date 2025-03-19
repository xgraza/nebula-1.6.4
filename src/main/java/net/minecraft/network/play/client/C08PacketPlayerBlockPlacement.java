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
    private int direction;
    private ItemStack field_149580_e;
    private float field_149577_f;
    private float field_149578_g;
    private float field_149584_h;
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
        this.direction = direction;
        this.field_149580_e = p_i45265_5_ != null ? p_i45265_5_.copy() : null;
        this.field_149577_f = p_i45265_6_;
        this.field_149578_g = p_i45265_7_;
        this.field_149584_h = p_i45265_8_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.posX = p_148837_1_.readInt();
        this.posY = p_148837_1_.readUnsignedByte();
        this.posZ = p_148837_1_.readInt();
        this.direction = p_148837_1_.readUnsignedByte();
        this.field_149580_e = p_148837_1_.readItemStackFromBuffer();
        this.field_149577_f = (float)p_148837_1_.readUnsignedByte() / 16.0F;
        this.field_149578_g = (float)p_148837_1_.readUnsignedByte() / 16.0F;
        this.field_149584_h = (float)p_148837_1_.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.posX);
        p_148840_1_.writeByte(this.posY);
        p_148840_1_.writeInt(this.posZ);
        p_148840_1_.writeByte(this.direction);
        p_148840_1_.writeItemStackToBuffer(this.field_149580_e);
        p_148840_1_.writeByte((int)(this.field_149577_f * 16.0F));
        p_148840_1_.writeByte((int)(this.field_149578_g * 16.0F));
        p_148840_1_.writeByte((int)(this.field_149584_h * 16.0F));
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

    public int func_149568_f()
    {
        return this.direction;
    }

    public ItemStack func_149574_g()
    {
        return this.field_149580_e;
    }

    public float func_149573_h()
    {
        return this.field_149577_f;
    }

    public float func_149569_i()
    {
        return this.field_149578_g;
    }

    public float func_149575_j()
    {
        return this.field_149584_h;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }
}
