package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess extends Packet
{
    private GameProfile field_149602_a;
    private static final String __OBFID = "CL_00001375";

    public S02PacketLoginSuccess() {}

    public S02PacketLoginSuccess(GameProfile p_i45267_1_)
    {
        this.field_149602_a = p_i45267_1_;
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        String var2 = p_148837_1_.readStringFromBuffer(36);
        String var3 = p_148837_1_.readStringFromBuffer(16);
        this.field_149602_a = new GameProfile(var2, var3);
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeStringToBuffer(this.field_149602_a.getId());
        p_148840_1_.writeStringToBuffer(this.field_149602_a.getName());
    }

    public void processPacket(INetHandlerLoginClient p_149601_1_)
    {
        p_149601_1_.handleLoginSuccess(this);
    }

    public boolean hasPriority()
    {
        return true;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerLoginClient)p_148833_1_);
    }
}
