package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet103SetSlot extends Packet
{
    /** The window which is being updated. 0 for player inventory */
    public int windowId;

    /** Slot that should be updated */
    public int itemSlot;

    /** Item stack */
    public ItemStack myItemStack;

    public Packet103SetSlot() {}

    public Packet103SetSlot(int par1, int par2, ItemStack par3ItemStack)
    {
        this.windowId = par1;
        this.itemSlot = par2;
        this.myItemStack = par3ItemStack == null ? par3ItemStack : par3ItemStack.copy();
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleSetSlot(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.windowId = par1DataInput.readByte();
        this.itemSlot = par1DataInput.readShort();
        this.myItemStack = readItemStack(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.windowId);
        par1DataOutput.writeShort(this.itemSlot);
        writeItemStack(this.myItemStack, par1DataOutput);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }
}
