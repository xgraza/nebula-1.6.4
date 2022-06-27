package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet105UpdateProgressbar extends Packet
{
    /** The id of the window that the progress bar is in. */
    public int windowId;

    /**
     * Which of the progress bars that should be updated. (For furnaces, 0 = progress arrow, 1 = fire icon)
     */
    public int progressBar;

    /**
     * The value of the progress bar. The maximum values vary depending on the progress bar. Presumably the values are
     * specified as in-game ticks. Some progress bar values increase, while others decrease. For furnaces, 0 is empty,
     * full progress arrow = about 180, full fire icon = about 250)
     */
    public int progressBarValue;

    public Packet105UpdateProgressbar() {}

    public Packet105UpdateProgressbar(int par1, int par2, int par3)
    {
        this.windowId = par1;
        this.progressBar = par2;
        this.progressBarValue = par3;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleUpdateProgressbar(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.windowId = par1DataInput.readByte();
        this.progressBar = par1DataInput.readShort();
        this.progressBarValue = par1DataInput.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.windowId);
        par1DataOutput.writeShort(this.progressBar);
        par1DataOutput.writeShort(this.progressBarValue);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 5;
    }
}
