package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import shadersmod.client.ShadersTex;

public class DynamicTexture extends AbstractTexture
{
    private final int[] dynamicTextureData;
    private final int width;
    private final int height;
    private boolean shadersInitialized;

    public DynamicTexture(BufferedImage par1BufferedImage)
    {
        this(par1BufferedImage.getWidth(), par1BufferedImage.getHeight());
        par1BufferedImage.getRGB(0, 0, par1BufferedImage.getWidth(), par1BufferedImage.getHeight(), this.dynamicTextureData, 0, par1BufferedImage.getWidth());
        this.updateDynamicTexture();
    }

    public DynamicTexture(int par1, int par2)
    {
        this.shadersInitialized = false;
        this.width = par1;
        this.height = par2;
        this.dynamicTextureData = new int[par1 * par2 * 3];

        if (Config.isShaders())
        {
            ShadersTex.initDynamicTexture(this.getGlTextureId(), par1, par2, this);
            this.shadersInitialized = true;
        }
        else
        {
            TextureUtil.allocateTexture(this.getGlTextureId(), par1, par2);
        }
    }

    public void loadTexture(IResourceManager par1ResourceManager) throws IOException {}

    public void updateDynamicTexture()
    {
        if (Config.isShaders())
        {
            if (!this.shadersInitialized)
            {
                ShadersTex.initDynamicTexture(this.getGlTextureId(), this.width, this.height, this);
                this.shadersInitialized = true;
            }

            ShadersTex.updateDynamicTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height, this);
        }
        else
        {
            TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
        }
    }

    public int[] getTextureData()
    {
        return this.dynamicTextureData;
    }
}
