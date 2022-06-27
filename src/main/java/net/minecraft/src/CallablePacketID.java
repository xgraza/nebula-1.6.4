package net.minecraft.src;

import java.util.concurrent.Callable;

class CallablePacketID implements Callable
{
    final Packet thePacket;

    final NetServerHandler theNetServerHandler;

    CallablePacketID(NetServerHandler par1NetServerHandler, Packet par2Packet)
    {
        this.theNetServerHandler = par1NetServerHandler;
        this.thePacket = par2Packet;
    }

    public String callPacketID()
    {
        return String.valueOf(this.thePacket.getPacketId());
    }

    public Object call()
    {
        return this.callPacketID();
    }
}
