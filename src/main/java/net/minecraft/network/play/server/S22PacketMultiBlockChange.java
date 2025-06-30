package net.minecraft.network.play.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S22PacketMultiBlockChange extends Packet
{
    private static final Logger logger = LogManager.getLogger();
    private ChunkCoordIntPair chunkCoords;
    private byte[] blockUpdates;
    private int count;

    public S22PacketMultiBlockChange() {}

    public S22PacketMultiBlockChange(int count, short[] location, Chunk chunk)
    {
        this.chunkCoords = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
        this.count = count;
        int size = 4 * count;

        try
        {
            ByteArrayOutputStream arrOutputStream = new ByteArrayOutputStream(size);
            DataOutputStream dataOutputStream = new DataOutputStream(arrOutputStream);

            for (int i = 0; i < count; ++i)
            {
                int var8 = location[i] >> 12 & 15;
                int var9 = location[i] >> 8 & 15;
                int var10 = location[i] & 255;
                dataOutputStream.writeShort(location[i]);
                dataOutputStream.writeShort((short)((Block.getIdFromBlock(chunk.getBlock(var8, var10, var9)) & 4095) << 4 | chunk.getBlockMetadata(var8, var10, var9) & 15));
            }

            this.blockUpdates = arrOutputStream.toByteArray();

            if (this.blockUpdates.length != size)
            {
                throw new RuntimeException("Expected length " + size + " doesn\'t match received length " + this.blockUpdates.length);
            }
        }
        catch (IOException var11)
        {
            logger.error("Couldn\'t create bulk block update packet", var11);
            this.blockUpdates = null;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.chunkCoords = new ChunkCoordIntPair(p_148837_1_.readInt(), p_148837_1_.readInt());
        this.count = p_148837_1_.readShort() & 65535;
        int var2 = p_148837_1_.readInt();

        if (var2 > 0)
        {
            this.blockUpdates = new byte[var2];
            p_148837_1_.readBytes(this.blockUpdates);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.chunkCoords.chunkXPos);
        p_148840_1_.writeInt(this.chunkCoords.chunkZPos);
        p_148840_1_.writeShort((short)this.count);

        if (this.blockUpdates != null)
        {
            p_148840_1_.writeInt(this.blockUpdates.length);
            p_148840_1_.writeBytes(this.blockUpdates);
        }
        else
        {
            p_148840_1_.writeInt(0);
        }
    }

    public void processPacket(INetHandlerPlayClient p_148923_1_)
    {
        p_148923_1_.handleMultiBlockChange(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("xc=%d, zc=%d, count=%d", new Object[] {Integer.valueOf(this.chunkCoords.chunkXPos), Integer.valueOf(this.chunkCoords.chunkZPos), Integer.valueOf(this.count)});
    }

    public ChunkCoordIntPair getChunkCoords()
    {
        return this.chunkCoords;
    }

    public byte[] getBlockUpdates()
    {
        return this.blockUpdates;
    }

    public int getCount()
    {
        return this.count;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
