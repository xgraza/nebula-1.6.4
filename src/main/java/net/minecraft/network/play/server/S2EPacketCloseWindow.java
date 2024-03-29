package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2EPacketCloseWindow extends Packet
{
    private int windowId;
    private static final String __OBFID = "CL_00001292";

    public S2EPacketCloseWindow() {}

    public S2EPacketCloseWindow(int p_i45183_1_)
    {
        this.windowId = p_i45183_1_;
    }

    public void processPacket(INetHandlerPlayClient p_148895_1_)
    {
        p_148895_1_.handleCloseWindow(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.windowId = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.windowId);
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }

    public int getWindowId() {
        return windowId;
    }
}
