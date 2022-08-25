package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderFallingBlock extends Render
{
    private final RenderBlocks field_147920_a = new RenderBlocks();
    private static final String __OBFID = "CL_00000994";

    public RenderFallingBlock()
    {
        this.shadowSize = 0.5F;
    }

    public void doRender(EntityFallingBlock p_147918_1_, double p_147918_2_, double p_147918_4_, double p_147918_6_, float p_147918_8_, float p_147918_9_)
    {
        World var10 = p_147918_1_.func_145807_e();
        Block var11 = p_147918_1_.func_145805_f();
        int var12 = MathHelper.floor_double(p_147918_1_.posX);
        int var13 = MathHelper.floor_double(p_147918_1_.posY);
        int var14 = MathHelper.floor_double(p_147918_1_.posZ);

        if (var11 != null && var11 != var10.getBlock(var12, var13, var14))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)p_147918_2_, (float)p_147918_4_, (float)p_147918_6_);
            this.bindEntityTexture(p_147918_1_);
            GL11.glDisable(GL11.GL_LIGHTING);
            Tessellator var15;

            if (var11 instanceof BlockAnvil)
            {
                this.field_147920_a.blockAccess = var10;
                var15 = Tessellator.instance;
                var15.startDrawingQuads();
                var15.setTranslation((double)((float)(-var12) - 0.5F), (double)((float)(-var13) - 0.5F), (double)((float)(-var14) - 0.5F));
                this.field_147920_a.renderBlockAnvilMetadata((BlockAnvil)var11, var12, var13, var14, p_147918_1_.field_145814_a);
                var15.setTranslation(0.0D, 0.0D, 0.0D);
                var15.draw();
            }
            else if (var11 instanceof BlockDragonEgg)
            {
                this.field_147920_a.blockAccess = var10;
                var15 = Tessellator.instance;
                var15.startDrawingQuads();
                var15.setTranslation((double)((float)(-var12) - 0.5F), (double)((float)(-var13) - 0.5F), (double)((float)(-var14) - 0.5F));
                this.field_147920_a.renderBlockDragonEgg((BlockDragonEgg)var11, var12, var13, var14);
                var15.setTranslation(0.0D, 0.0D, 0.0D);
                var15.draw();
            }
            else
            {
                this.field_147920_a.setRenderBoundsFromBlock(var11);
                this.field_147920_a.renderBlockSandFalling(var11, var10, var12, var13, var14, p_147918_1_.field_145814_a);
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

    protected ResourceLocation getEntityTexture(EntityFallingBlock p_147919_1_)
    {
        return TextureMap.locationBlocksTexture;
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityFallingBlock)par1Entity);
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityFallingBlock)par1Entity, par2, par4, par6, par8, par9);
    }
}
