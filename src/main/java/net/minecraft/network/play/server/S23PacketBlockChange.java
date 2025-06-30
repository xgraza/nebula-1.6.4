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
    private Block type;
    private int data;

    public S23PacketBlockChange() {}

    public S23PacketBlockChange(int x, int y, int z, World world)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = world.getBlock(x, y, z);
        this.data = world.getBlockMetadata(x, y, z);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer packetBuf) throws IOException
    {
        this.x = packetBuf.readInt();
        this.y = packetBuf.readUnsignedByte();
        this.z = packetBuf.readInt();
        this.type = Block.getBlockById(packetBuf.readVarIntFromBuffer());
        this.data = packetBuf.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer packetBuf) throws IOException
    {
        packetBuf.writeInt(this.x);
        packetBuf.writeByte(this.y);
        packetBuf.writeInt(this.z);
        packetBuf.writeVarIntToBuffer(Block.getIdFromBlock(this.type));
        packetBuf.writeByte(this.data);
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
        return String.format("type=%d, data=%d, x=%d, y=%d, z=%d", new Object[] {Integer.valueOf(Block.getIdFromBlock(this.type)), Integer.valueOf(this.data), Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.z)});
    }

    public Block getType()
    {
        return this.type;
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

    public int getData()
    {
        return this.data;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
