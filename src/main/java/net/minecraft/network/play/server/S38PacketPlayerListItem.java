package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S38PacketPlayerListItem extends Packet
{
    public String username;
    public boolean loggedIn;
    public int someInt;

    public S38PacketPlayerListItem() {}

    public S38PacketPlayerListItem(String p_i45209_1_, boolean p_i45209_2_, int p_i45209_3_)
    {
        this.username = p_i45209_1_;
        this.loggedIn = p_i45209_2_;
        this.someInt = p_i45209_3_;
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.username = p_148837_1_.readStringFromBuffer(16);
        this.loggedIn = p_148837_1_.readBoolean();
        this.someInt = p_148837_1_.readShort();
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeStringToBuffer(this.username);
        p_148840_1_.writeBoolean(this.loggedIn);
        p_148840_1_.writeShort(this.someInt);
    }

    public void processPacket(INetHandlerPlayClient p_149123_1_)
    {
        p_149123_1_.handlePlayerListItem(this);
    }

    public String func_149122_c()
    {
        return this.username;
    }

    public boolean func_149121_d()
    {
        return this.loggedIn;
    }

    public int func_149120_e()
    {
        return this.someInt;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
