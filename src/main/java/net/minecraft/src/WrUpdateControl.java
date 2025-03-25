package net.minecraft.src;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.AxisAlignedBB;

public class WrUpdateControl implements IWrUpdateControl
{
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
}
