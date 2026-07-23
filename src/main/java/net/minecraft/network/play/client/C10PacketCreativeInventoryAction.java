package net.minecraft.network.play.client;

import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C10PacketCreativeInventoryAction extends Packet
{
    private int action;
    private ItemStack stack;

    public C10PacketCreativeInventoryAction()
    {
    }

    public C10PacketCreativeInventoryAction(int action, ItemStack stack)
    {
        this.action = action;
        this.stack = stack != null ? stack.copy() : null;
    }

    public void processPacket(INetHandlerPlayServer netHandler)
    {
        netHandler.processCreativeInventoryAction(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.action = buffer.readShort();
        this.stack = buffer.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeShort(this.action);
        buffer.writeItemStackToBuffer(this.stack);
    }

    public int getAction()
    {
        return this.action;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer) handler);
    }
}
