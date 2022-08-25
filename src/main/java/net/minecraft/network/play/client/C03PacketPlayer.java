package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer extends Packet
{
    public double x;
    public double y;
    public double z;
    public double stance;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public boolean moving;
    public boolean rotating;
    private static final String __OBFID = "CL_00001360";

    public C03PacketPlayer() {}

    public C03PacketPlayer(boolean p_i45256_1_)
    {
        this.onGround = p_i45256_1_;
    }

    public void processPacket(INetHandlerPlayServer p_149468_1_)
    {
        p_149468_1_.processPlayer(this);
    }

    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.onGround = p_148837_1_.readUnsignedByte() != 0;
    }

    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.onGround ? 1 : 0);
    }

    public double func_149464_c()
    {
        return this.x;
    }

    public double func_149467_d()
    {
        return this.y;
    }

    public double func_149472_e()
    {
        return this.z;
    }

    public double func_149471_f()
    {
        return this.stance;
    }

    public float func_149462_g()
    {
        return this.yaw;
    }

    public float func_149470_h()
    {
        return this.pitch;
    }

    public boolean func_149465_i()
    {
        return this.onGround;
    }

    public boolean func_149466_j()
    {
        return this.moving;
    }

    public boolean func_149463_k()
    {
        return this.rotating;
    }

    public void func_149469_a(boolean p_149469_1_)
    {
        this.moving = p_149469_1_;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }

    public static class C04PacketPlayerPosition extends C03PacketPlayer
    {
        private static final String __OBFID = "CL_00001361";

        public C04PacketPlayerPosition()
        {
            this.moving = true;
        }

        public C04PacketPlayerPosition(double p_i45253_1_, double p_i45253_3_, double p_i45253_5_, double p_i45253_7_, boolean p_i45253_9_)
        {
            this.x = p_i45253_1_;
            this.y = p_i45253_3_;
            this.stance = p_i45253_5_;
            this.z = p_i45253_7_;
            this.onGround = p_i45253_9_;
            this.moving = true;
        }

        public void readPacketData(PacketBuffer p_148837_1_) throws IOException
        {
            this.x = p_148837_1_.readDouble();
            this.y = p_148837_1_.readDouble();
            this.stance = p_148837_1_.readDouble();
            this.z = p_148837_1_.readDouble();
            super.readPacketData(p_148837_1_);
        }

        public void writePacketData(PacketBuffer p_148840_1_) throws IOException
        {
            p_148840_1_.writeDouble(this.x);
            p_148840_1_.writeDouble(this.y);
            p_148840_1_.writeDouble(this.stance);
            p_148840_1_.writeDouble(this.z);
            super.writePacketData(p_148840_1_);
        }

        public void processPacket(INetHandler p_148833_1_)
        {
            super.processPacket((INetHandlerPlayServer)p_148833_1_);
        }
    }

    public static class C06PacketPlayerPosLook extends C03PacketPlayer
    {
        private static final String __OBFID = "CL_00001362";

        public C06PacketPlayerPosLook()
        {
            this.moving = true;
            this.rotating = true;
        }

        public C06PacketPlayerPosLook(double p_i45254_1_, double p_i45254_3_, double p_i45254_5_, double p_i45254_7_, float p_i45254_9_, float p_i45254_10_, boolean p_i45254_11_)
        {
            this.x = p_i45254_1_;
            this.y = p_i45254_3_;
            this.stance = p_i45254_5_;
            this.z = p_i45254_7_;
            this.yaw = p_i45254_9_;
            this.pitch = p_i45254_10_;
            this.onGround = p_i45254_11_;
            this.rotating = true;
            this.moving = true;
        }

        public void readPacketData(PacketBuffer p_148837_1_) throws IOException
        {
            this.x = p_148837_1_.readDouble();
            this.y = p_148837_1_.readDouble();
            this.stance = p_148837_1_.readDouble();
            this.z = p_148837_1_.readDouble();
            this.yaw = p_148837_1_.readFloat();
            this.pitch = p_148837_1_.readFloat();
            super.readPacketData(p_148837_1_);
        }

        public void writePacketData(PacketBuffer p_148840_1_) throws IOException
        {
            p_148840_1_.writeDouble(this.x);
            p_148840_1_.writeDouble(this.y);
            p_148840_1_.writeDouble(this.stance);
            p_148840_1_.writeDouble(this.z);
            p_148840_1_.writeFloat(this.yaw);
            p_148840_1_.writeFloat(this.pitch);
            super.writePacketData(p_148840_1_);
        }

        public void processPacket(INetHandler p_148833_1_)
        {
            super.processPacket((INetHandlerPlayServer)p_148833_1_);
        }
    }

    public static class C05PacketPlayerLook extends C03PacketPlayer
    {
        private static final String __OBFID = "CL_00001363";

        public C05PacketPlayerLook()
        {
            this.rotating = true;
        }

        public C05PacketPlayerLook(float p_i45255_1_, float p_i45255_2_, boolean p_i45255_3_)
        {
            this.yaw = p_i45255_1_;
            this.pitch = p_i45255_2_;
            this.onGround = p_i45255_3_;
            this.rotating = true;
        }

        public void readPacketData(PacketBuffer p_148837_1_) throws IOException
        {
            this.yaw = p_148837_1_.readFloat();
            this.pitch = p_148837_1_.readFloat();
            super.readPacketData(p_148837_1_);
        }

        public void writePacketData(PacketBuffer p_148840_1_) throws IOException
        {
            p_148840_1_.writeFloat(this.yaw);
            p_148840_1_.writeFloat(this.pitch);
            super.writePacketData(p_148840_1_);
        }

        public void processPacket(INetHandler p_148833_1_)
        {
            super.processPacket((INetHandlerPlayServer)p_148833_1_);
        }
    }
}
