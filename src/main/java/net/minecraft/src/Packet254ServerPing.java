package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet254ServerPing extends Packet
{
    private static final int field_140051_d = (new Packet250CustomPayload()).getPacketId();

    /** Always 1, unless readByte threw an exception. */
    public int readSuccessfully;
    public String field_140052_b;
    public int field_140053_c;

    public Packet254ServerPing() {}

    public Packet254ServerPing(int par1, String par2Str, int par3)
    {
        this.readSuccessfully = par1;
        this.field_140052_b = par2Str;
        this.field_140053_c = par3;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        try
        {
            this.readSuccessfully = par1DataInput.readByte();

            try
            {
                par1DataInput.readByte();
                readString(par1DataInput, 255);
                par1DataInput.readShort();
                this.readSuccessfully = par1DataInput.readByte();

                if (this.readSuccessfully >= 73)
                {
                    this.field_140052_b = readString(par1DataInput, 255);
                    this.field_140053_c = par1DataInput.readInt();
                }
            }
            catch (Throwable var3)
            {
                this.field_140052_b = "";
            }
        }
        catch (Throwable var4)
        {
            this.readSuccessfully = 0;
            this.field_140052_b = "";
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(1);
        par1DataOutput.writeByte(field_140051_d);
        Packet.writeString("MC|PingHost", par1DataOutput);
        par1DataOutput.writeShort(3 + 2 * this.field_140052_b.length() + 4);
        par1DataOutput.writeByte(this.readSuccessfully);
        Packet.writeString(this.field_140052_b, par1DataOutput);
        par1DataOutput.writeInt(this.field_140053_c);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleServerPing(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 3 + this.field_140052_b.length() * 2 + 4;
    }

    public boolean func_140050_d()
    {
        return this.readSuccessfully == 0;
    }
}
