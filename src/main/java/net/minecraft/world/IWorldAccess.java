package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IWorldAccess
{
    void markBlockForUpdate(int var1, int var2, int var3);

    void markBlockForRenderUpdate(int var1, int var2, int var3);

    void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6);

    void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

    void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10);

    void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12);

    void onEntityCreate(Entity var1);

    void onEntityDestroy(Entity var1);

    void playRecord(String var1, int var2, int var3, int var4);

    void broadcastSound(int var1, int var2, int var3, int var4, int var5);

    void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6);

    void destroyBlockPartially(int var1, int var2, int var3, int var4, int var5);

    void onStaticEntitiesChanged();
}
