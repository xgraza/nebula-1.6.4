package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderTntMinecart extends RenderMinecart
{
    protected void func_94146_a(EntityMinecartTNT par1EntityMinecartTNT, float par2, Block par3Block, int par4)
    {
        int var5 = par1EntityMinecartTNT.func_94104_d();

        if (var5 > -1 && (float)var5 - par2 + 1.0F < 10.0F)
        {
            float var6 = 1.0F - ((float)var5 - par2 + 1.0F) / 10.0F;

            if (var6 < 0.0F)
            {
                var6 = 0.0F;
            }

            if (var6 > 1.0F)
            {
                var6 = 1.0F;
            }

            var6 *= var6;
            var6 *= var6;
            float var7 = 1.0F + var6 * 0.3F;
            GL11.glScalef(var7, var7, var7);
        }

        super.renderBlockInMinecart(par1EntityMinecartTNT, par2, par3Block, par4);

        if (var5 > -1 && var5 / 5 % 2 == 0)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - ((float)var5 - par2 + 1.0F) / 100.0F) * 0.8F);
            GL11.glPushMatrix();
            this.field_94145_f.renderBlockAsItem(Block.tnt, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    /**
     * Renders the block that is inside the minecart.
     */
    protected void renderBlockInMinecart(EntityMinecart par1EntityMinecart, float par2, Block par3Block, int par4)
    {
        this.func_94146_a((EntityMinecartTNT)par1EntityMinecart, par2, par3Block, par4);
    }
}
