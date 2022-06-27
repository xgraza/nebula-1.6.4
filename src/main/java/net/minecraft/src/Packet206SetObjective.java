package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet206SetObjective extends Packet
{
    public String objectiveName;
    public String objectiveDisplayName;

    /**
     * 0 to create scoreboard, 1 to remove scoreboard, 2 to update display text.
     */
    public int change;

    public Packet206SetObjective() {}

    public Packet206SetObjective(ScoreObjective par1ScoreObjective, int par2)
    {
        this.objectiveName = par1ScoreObjective.getName();
        this.objectiveDisplayName = par1ScoreObjective.getDisplayName();
        this.change = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.objectiveName = readString(par1DataInput, 16);
        this.objectiveDisplayName = readString(par1DataInput, 32);
        this.change = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        writeString(this.objectiveName, par1DataOutput);
        writeString(this.objectiveDisplayName, par1DataOutput);
        par1DataOutput.writeByte(this.change);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleSetObjective(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.objectiveName.length() + 2 + this.objectiveDisplayName.length() + 1;
    }
}
