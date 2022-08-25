package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCaveSpider extends RenderSpider
{
    private static final ResourceLocation caveSpiderTextures = new ResourceLocation("textures/entity/spider/cave_spider.png");
    private static final String __OBFID = "CL_00000982";

    public RenderCaveSpider()
    {
        this.shadowSize *= 0.7F;
    }

    protected void preRenderCallback(EntityCaveSpider par1EntityCaveSpider, float par2)
    {
        GL11.glScalef(0.7F, 0.7F, 0.7F);
    }

    protected ResourceLocation getEntityTexture(EntityCaveSpider par1EntityCaveSpider)
    {
        return caveSpiderTextures;
    }

    protected ResourceLocation getEntityTexture(EntitySpider par1EntitySpider)
    {
        return this.getEntityTexture((EntityCaveSpider)par1EntitySpider);
    }

    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.preRenderCallback((EntityCaveSpider)par1EntityLivingBase, par2);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityCaveSpider)par1Entity);
    }
}
