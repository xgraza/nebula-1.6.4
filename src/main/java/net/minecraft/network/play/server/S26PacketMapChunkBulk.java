package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class S26PacketMapChunkBulk extends Packet
{
    private int[] chunkXPosition;
    private int[] chunkZPosition;
    private int[] chunkSectionData;
    private int[] field_149262_d;
    private byte[] field_149263_e;
    private byte[][] field_149260_f;
    private int size;
    private boolean full;
    private static byte[] field_149268_i = new byte[0];

    public S26PacketMapChunkBulk()
    {
    }

    public S26PacketMapChunkBulk(List<Chunk> chunkList)
    {
        int size = chunkList.size();
        this.chunkXPosition = new int[size];
        this.chunkZPosition = new int[size];
        this.chunkSectionData = new int[size];
        this.field_149262_d = new int[size];
        this.field_149260_f = new byte[size][];
        this.full = !chunkList.isEmpty() && !chunkList.get(0).worldObj.provider.hasNoSky;
        int length = 0;

        for (int i = 0; i < size; ++i)
        {
            Chunk chunk = chunkList.get(i);
            S21PacketChunkData.Extracted data = S21PacketChunkData.func_149269_a(chunk, true, 65535);

            if (field_149268_i.length < length + data.deflatedChunkData.length)
            {
                byte[] var7 = new byte[length + data.deflatedChunkData.length];
                System.arraycopy(field_149268_i, 0, var7, 0, field_149268_i.length);
                field_149268_i = var7;
            }

            System.arraycopy(data.deflatedChunkData, 0, field_149268_i, length, data.deflatedChunkData.length);
            length += data.deflatedChunkData.length;
            this.chunkXPosition[i] = chunk.xPosition;
            this.chunkZPosition[i] = chunk.zPosition;
            this.chunkSectionData[i] = data.field_150280_b;
            this.field_149262_d[i] = data.field_150281_c;
            this.field_149260_f[i] = data.deflatedChunkData;
        }

        Deflater var11 = new Deflater(-1);

        try
        {
            var11.setInput(field_149268_i, 0, length);
            var11.finish();
            this.field_149263_e = new byte[length];
            this.size = var11.deflate(this.field_149263_e);
        } finally
        {
            var11.end();
        }
    }

    public static int func_149258_c()
    {
        return 5;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        short var2 = buffer.readShort();
        this.size = buffer.readInt();
        this.full = buffer.readBoolean();
        this.chunkXPosition = new int[var2];
        this.chunkZPosition = new int[var2];
        this.chunkSectionData = new int[var2];
        this.field_149262_d = new int[var2];
        this.field_149260_f = new byte[var2][];

        if (field_149268_i.length < this.size)
        {
            field_149268_i = new byte[this.size];
        }

        buffer.readBytes(field_149268_i, 0, this.size);
        byte[] chunkByteData = new byte[S21PacketChunkData.chunkSize() * var2];
        Inflater inflater = new Inflater();
        inflater.setInput(field_149268_i, 0, this.size);

        try
        {
            inflater.inflate(chunkByteData);
        } catch (DataFormatException var12)
        {
            throw new IOException("Bad compressed data format");
        } finally
        {
            inflater.end();
        }

        int var5 = 0;

        for (int var6 = 0; var6 < var2; ++var6)
        {
            this.chunkXPosition[var6] = buffer.readInt();
            this.chunkZPosition[var6] = buffer.readInt();
            this.chunkSectionData[var6] = buffer.readShort();
            this.field_149262_d[var6] = buffer.readShort();
            int var7 = 0;
            int var8 = 0;
            int var9;

            for (var9 = 0; var9 < 16; ++var9)
            {
                var7 += this.chunkSectionData[var6] >> var9 & 1;
                var8 += this.field_149262_d[var6] >> var9 & 1;
            }

            var9 = 2048 * 4 * var7 + 256;
            var9 += 2048 * var8;

            if (this.full)
            {
                var9 += 2048 * var7;
            }

            this.field_149260_f[var6] = new byte[var9];
            System.arraycopy(chunkByteData, var5, this.field_149260_f[var6], 0, var9);
            var5 += var9;
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeShort(this.chunkXPosition.length);
        buffer.writeInt(this.size);
        buffer.writeBoolean(this.full);
        buffer.writeBytes(this.field_149263_e, 0, this.size);

        for (int i = 0; i < this.chunkXPosition.length; ++i)
        {
            buffer.writeInt(this.chunkXPosition[i]);
            buffer.writeInt(this.chunkZPosition[i]);
            buffer.writeShort((short) (this.chunkSectionData[i] & 65535));
            buffer.writeShort((short) (this.field_149262_d[i] & 65535));
        }
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleMapChunkBulk(this);
    }

    public int getChunkPosX(int i)
    {
        return this.chunkXPosition[i];
    }

    public int getChunkPosZ(int i)
    {
        return this.chunkZPosition[i];
    }

    public int getSize()
    {
        return this.chunkXPosition.length;
    }

    public byte[] func_149256_c(int p_149256_1_)
    {
        return this.field_149260_f[p_149256_1_];
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        StringBuilder var1 = new StringBuilder();

        for (int var2 = 0; var2 < this.chunkXPosition.length; ++var2)
        {
            if (var2 > 0)
            {
                var1.append(", ");
            }

            var1.append(String.format("{x=%d, z=%d, sections=%d, adds=%d, data=%d}", this.chunkXPosition[var2], this.chunkZPosition[var2], this.chunkSectionData[var2], this.field_149262_d[var2], this.field_149260_f[var2].length));
        }

        return String.format("size=%d, chunks=%d[%s]", this.size, this.chunkXPosition.length, var1);
    }

    public int[] getChunkSectionData()
    {
        return this.chunkSectionData;
    }

    public int[] func_149257_f()
    {
        return this.field_149262_d;
    }

    public boolean isFull()
    {
        return full;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
