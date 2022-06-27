package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderMagmaCube extends RenderLiving
{
    private static final ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

    public RenderMagmaCube()
    {
        super(new ModelMagmaCube(), 0.25F);
    }

    protected ResourceLocation getMagmaCubeTextures(EntityMagmaCube par1EntityMagmaCube)
    {
        return magmaCubeTextures;
    }

    protected void scaleMagmaCube(EntityMagmaCube par1EntityMagmaCube, float par2)
    {
        int var3 = par1EntityMagmaCube.getSlimeSize();
        float var4 = (par1EntityMagmaCube.prevSquishFactor + (par1EntityMagmaCube.squishFactor - par1EntityMagmaCube.prevSquishFactor) * par2) / ((float)var3 * 0.5F + 1.0F);
        float var5 = 1.0F / (var4 + 1.0F);
        float var6 = (float)var3;
        GL11.glScalef(var5 * var6, 1.0F / var5 * var6, var5 * var6);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.scaleMagmaCube((EntityMagmaCube)par1EntityLivingBase, par2);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getMagmaCubeTextures((EntityMagmaCube)par1Entity);
    }
}
