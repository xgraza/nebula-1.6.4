package net.minecraft.src;

final class TextureUtils$1 implements ResourceManagerReloadListener
{
    public void onResourceManagerReload(ResourceManager var1)
    {
        TextureUtils.resourcesReloaded(var1);
    }
}
