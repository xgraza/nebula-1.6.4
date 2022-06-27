package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet207SetScore extends Packet
{
    /** An unique name to be displayed in the list. */
    public String itemName = "";

    /**
     * The unique name for the scoreboard to be updated. Only sent when updateOrRemove does not equal 1.
     */
    public String scoreName = "";

    /**
     * The score to be displayed next to the entry. Only sent when Update/Remove does not equal 1.
     */
    public int value;

    /** 0 to create/update an item. 1 to remove an item. */
    public int updateOrRemove;

    public Packet207SetScore() {}

    public Packet207SetScore(Score par1Score, int par2)
    {
        this.itemName = par1Score.getPlayerName();
        this.scoreName = par1Score.func_96645_d().getName();
        this.value = par1Score.getScorePoints();
        this.updateOrRemove = par2;
    }

    public Packet207SetScore(String par1Str)
    {
        this.itemName = par1Str;
        this.scoreName = "";
        this.value = 0;
        this.updateOrRemove = 1;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.itemName = readString(par1DataInput, 16);
        this.updateOrRemove = par1DataInput.readByte();

        if (this.updateOrRemove != 1)
        {
            this.scoreName = readString(par1DataInput, 16);
            this.value = par1DataInput.readInt();
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        writeString(this.itemName, par1DataOutput);
        par1DataOutput.writeByte(this.updateOrRemove);

        if (this.updateOrRemove != 1)
        {
            writeString(this.scoreName, par1DataOutput);
            par1DataOutput.writeInt(this.value);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleSetScore(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + (this.itemName == null ? 0 : this.itemName.length()) + 2 + (this.scoreName == null ? 0 : this.scoreName.length()) + 4 + 1;
    }
}
