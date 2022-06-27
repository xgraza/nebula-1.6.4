package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet100OpenWindow extends Packet
{
    public int windowId;
    public int inventoryType;
    public String windowTitle;
    public int slotsCount;

    /**
     * If false, the client will look up a string like "window.minecart". If true, the client uses what the server
     * provides.
     */
    public boolean useProvidedWindowTitle;
    public int field_111008_f;

    public Packet100OpenWindow() {}

    public Packet100OpenWindow(int par1, int par2, String par3Str, int par4, boolean par5)
    {
        this.windowId = par1;
        this.inventoryType = par2;
        this.windowTitle = par3Str;
        this.slotsCount = par4;
        this.useProvidedWindowTitle = par5;
    }

    public Packet100OpenWindow(int par1, int par2, String par3Str, int par4, boolean par5, int par6)
    {
        this(par1, par2, par3Str, par4, par5);
        this.field_111008_f = par6;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleOpenWindow(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.windowId = par1DataInput.readByte() & 255;
        this.inventoryType = par1DataInput.readByte() & 255;
        this.windowTitle = readString(par1DataInput, 32);
        this.slotsCount = par1DataInput.readByte() & 255;
        this.useProvidedWindowTitle = par1DataInput.readBoolean();

        if (this.inventoryType == 11)
        {
            this.field_111008_f = par1DataInput.readInt();
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.windowId & 255);
        par1DataOutput.writeByte(this.inventoryType & 255);
        writeString(this.windowTitle, par1DataOutput);
        par1DataOutput.writeByte(this.slotsCount & 255);
        par1DataOutput.writeBoolean(this.useProvidedWindowTitle);

        if (this.inventoryType == 11)
        {
            par1DataOutput.writeInt(this.field_111008_f);
        }
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return this.inventoryType == 11 ? 8 + this.windowTitle.length() : 4 + this.windowTitle.length();
    }
}
