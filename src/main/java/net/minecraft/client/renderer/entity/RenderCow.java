package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving
{
    private static final ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");
    private static final String __OBFID = "CL_00000984";

    public RenderCow(ModelBase par1ModelBase, float par2)
    {
        super(par1ModelBase, par2);
    }

    protected ResourceLocation getEntityTexture(EntityCow par1EntityCow)
    {
        return cowTextures;
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityCow)par1Entity);
    }
}
