package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableParticlePositionInfo implements Callable
{
    final double posX;

    final double posY;

    final double posZ;

    final RenderGlobal globalRenderer;

    CallableParticlePositionInfo(RenderGlobal par1RenderGlobal, double par2, double par4, double par6)
    {
        this.globalRenderer = par1RenderGlobal;
        this.posX = par2;
        this.posY = par4;
        this.posZ = par6;
    }

    public String callParticlePositionInfo()
    {
        return CrashReportCategory.func_85074_a(this.posX, this.posY, this.posZ);
    }

    public Object call()
    {
        return this.callParticlePositionInfo();
    }
}
