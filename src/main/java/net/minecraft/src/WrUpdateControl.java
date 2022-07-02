package net.minecraft.src;

public class WrUpdateControl implements IWrUpdateControl
{
    private boolean hasForge;
    private int renderPass;

    public WrUpdateControl()
    {
        this.hasForge = Reflector.ForgeHooksClient.exists();
        this.renderPass = 0;
    }

    public void resume() {}

    public void pause()
    {
        AxisAlignedBB.getAABBPool().cleanPool();
        WorldClient theWorld = Config.getMinecraft().theWorld;

        if (theWorld != null)
        {
            theWorld.getWorldVec3Pool().clear();
        }
    }

    public void setRenderPass(int renderPass)
    {
        this.renderPass = renderPass;
    }
}
