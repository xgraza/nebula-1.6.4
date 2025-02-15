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
    private int x;
    private int y;
    private int z;
    private Block field_148883_d;
    private int field_148884_e;
    private static final String __OBFID = "CL_00001287";

    public S23PacketBlockChange() {}

    public S23PacketBlockChange(int x, int y, int z, World p_i45177_4_)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.field_148883_d = p_i45177_4_.getBlock(x, y, z);
        this.field_148884_e = p_i45177_4_.getBlockMetadata(x, y, z);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readUnsignedByte();
        this.z = p_148837_1_.readInt();
        this.field_148883_d = Block.getBlockById(p_148837_1_.readVarIntFromBuffer());
        this.field_148884_e = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeByte(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeVarIntToBuffer(Block.getIdFromBlock(this.field_148883_d));
        p_148840_1_.writeByte(this.field_148884_e);
    }

    public void processPacket(INetHandlerPlayClient p_148882_1_)
    {
        p_148882_1_.handleBlockChange(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("type=%d, data=%d, x=%d, y=%d, z=%d", new Object[] {Integer.valueOf(Block.getIdFromBlock(this.field_148883_d)), Integer.valueOf(this.field_148884_e), Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.z)});
    }

    public Block getBlock()
    {
        return this.field_148883_d;
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

    public int func_148881_g()
    {
        return this.field_148884_e;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
