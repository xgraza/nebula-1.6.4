package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;

import java.io.IOException;

public class GrassColorReloadListener implements IResourceManagerReloadListener
{
    private static final ResourceLocation field_130078_a = new ResourceLocation("textures/colormap/grass.png");
    private static final String __OBFID = "CL_00001078";

    public void onResourceManagerReload(IResourceManager par1ResourceManager)
    {
        try
        {
            ColorizerGrass.setGrassBiomeColorizer(TextureUtil.readImageData(par1ResourceManager, field_130078_a));
        } catch (IOException var3)
        {
        }
    }
}
