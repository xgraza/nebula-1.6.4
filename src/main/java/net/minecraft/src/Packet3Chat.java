package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet3Chat extends Packet
{
    /** The message being sent. */
    public String message;
    private boolean isServer;

    public Packet3Chat()
    {
        this.isServer = true;
    }

    public Packet3Chat(ChatMessageComponent par1ChatMessageComponent)
    {
        this(par1ChatMessageComponent.toJson());
    }

    public Packet3Chat(ChatMessageComponent par1ChatMessageComponent, boolean par2)
    {
        this(par1ChatMessageComponent.toJson(), par2);
    }

    public Packet3Chat(String par1Str)
    {
        this(par1Str, true);
    }

    public Packet3Chat(String par1Str, boolean par2)
    {
        this.isServer = true;

        if (par1Str.length() > 32767)
        {
            par1Str = par1Str.substring(0, 32767);
        }

        this.message = par1Str;
        this.isServer = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.message = readString(par1DataInput, 32767);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        writeString(this.message, par1DataOutput);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleChat(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.message.length() * 2;
    }

    /**
     * Get whether this is a server
     */
    public boolean getIsServer()
    {
        return this.isServer;
    }
}
