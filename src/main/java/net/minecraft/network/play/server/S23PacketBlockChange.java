package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S23PacketBlockChange extends Packet
{
    public int x;
    public int y;
    public int z;
    private Block field_148883_d;
    public int data;
    private static final String __OBFID = "CL_00001287";

    public S23PacketBlockChange() {}

    public S23PacketBlockChange(int p_i45177_1_, int p_i45177_2_, int p_i45177_3_, World p_i45177_4_)
    {
        this.x = p_i45177_1_;
        this.y = p_i45177_2_;
        this.z = p_i45177_3_;
        this.field_148883_d = p_i45177_4_.getBlock(p_i45177_1_, p_i45177_2_, p_i45177_3_);
        this.data = p_i45177_4_.getBlockMetadata(p_i45177_1_, p_i45177_2_, p_i45177_3_);
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readUnsignedByte();
        this.z = p_148837_1_.readInt();
        this.field_148883_d = Block.getBlockById(p_148837_1_.readVarIntFromBuffer());
        this.data = p_148837_1_.readUnsignedByte();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeByte(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeVarIntToBuffer(Block.getIdFromBlock(this.field_148883_d));
        p_148840_1_.writeByte(this.data);
    }

    public void processPacket(INetHandlerPlayClient p_148882_1_)
    {
        p_148882_1_.handleBlockChange(this);
    }

    public String serialize()
    {
        return String.format("type=%d, data=%d, x=%d, y=%d, z=%d", new Object[] {Integer.valueOf(Block.getIdFromBlock(this.field_148883_d)), Integer.valueOf(this.data), Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.z)});
    }

    public Block func_148880_c()
    {
        return this.field_148883_d;
    }

    public int func_148879_d()
    {
        return this.x;
    }

    public int func_148878_e()
    {
        return this.y;
    }

    public int func_148877_f()
    {
        return this.z;
    }

    public int func_148881_g()
    {
        return this.data;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
