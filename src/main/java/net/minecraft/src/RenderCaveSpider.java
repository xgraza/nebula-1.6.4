package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderCaveSpider extends RenderSpider
{
    private static final ResourceLocation caveSpiderTextures = new ResourceLocation("textures/entity/spider/cave_spider.png");

    public RenderCaveSpider()
    {
        this.shadowSize *= 0.7F;
    }

    protected void scaleSpider(EntityCaveSpider par1EntityCaveSpider, float par2)
    {
        GL11.glScalef(0.7F, 0.7F, 0.7F);
    }

    protected ResourceLocation getCaveSpiderTextures(EntityCaveSpider par1EntityCaveSpider)
    {
        return caveSpiderTextures;
    }

    protected ResourceLocation getSpiderTextures(EntitySpider par1EntitySpider)
    {
        return this.getCaveSpiderTextures((EntityCaveSpider)par1EntitySpider);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.scaleSpider((EntityCaveSpider)par1EntityLivingBase, par2);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getCaveSpiderTextures((EntityCaveSpider)par1Entity);
    }
}
