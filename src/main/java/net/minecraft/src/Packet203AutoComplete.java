package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet203AutoComplete extends Packet
{
    /**
     * Sent by the client containing the text to be autocompleted. Sent by the server with possible completions
     * separated by null (two bytes in UTF-16)
     */
    private String text;

    public Packet203AutoComplete() {}

    public Packet203AutoComplete(String par1Str)
    {
        this.text = par1Str;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.text = readString(par1DataInput, 32767);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        writeString(org.apache.commons.lang3.StringUtils.substring(this.text, 0, 32767), par1DataOutput);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleAutoComplete(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.text.length() * 2;
    }

    public String getText()
    {
        return this.text;
    }

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean isRealPacket()
    {
        return true;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean containsSameEntityIDAs(Packet par1Packet)
    {
        return true;
    }
}
