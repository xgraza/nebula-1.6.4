package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C00PacketServerQuery extends Packet
{
    private static final String __OBFID = "CL_00001393";

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException {}

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {}

    public void processPacket(INetHandlerStatusServer p_149287_1_)
    {
        p_149287_1_.processServerQuery(this);
    }

    public boolean hasPriority()
    {
        return true;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerStatusServer)p_148833_1_);
    }
}
