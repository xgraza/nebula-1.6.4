package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet250CustomPayload extends Packet
{
    /** Name of the 'channel' used to send data */
    public String channel;

    /** Length of the data to be read */
    public int length;

    /** Any data */
    public byte[] data;

    public Packet250CustomPayload() {}

    public Packet250CustomPayload(String par1Str, byte[] par2ArrayOfByte)
    {
        this.channel = par1Str;
        this.data = par2ArrayOfByte;

        if (par2ArrayOfByte != null)
        {
            this.length = par2ArrayOfByte.length;

            if (this.length > 32767)
            {
                throw new IllegalArgumentException("Payload may not be larger than 32k");
            }
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.channel = readString(par1DataInput, 20);
        this.length = par1DataInput.readShort();

        if (this.length > 0 && this.length < 32767)
        {
            this.data = new byte[this.length];
            par1DataInput.readFully(this.data);
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        writeString(this.channel, par1DataOutput);
        par1DataOutput.writeShort((short)this.length);

        if (this.data != null)
        {
            par1DataOutput.write(this.data);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleCustomPayload(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.channel.length() * 2 + 2 + this.length;
    }
}
