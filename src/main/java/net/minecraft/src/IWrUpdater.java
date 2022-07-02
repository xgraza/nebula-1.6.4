package net.minecraft.src;

import java.util.List;

public interface IWrUpdater
{
    void initialize();

    WorldRenderer makeWorldRenderer(World var1, List var2, int var3, int var4, int var5, int var6);

    void preRender(RenderGlobal var1, EntityLivingBase var2);

    void postRender();

    boolean updateRenderers(RenderGlobal var1, EntityLivingBase var2, boolean var3);

    void resumeBackgroundUpdates();

    void pauseBackgroundUpdates();

    void finishCurrentUpdate();

    void terminate();
}
