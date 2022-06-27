package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer
{
    public void renderTileEntityMobSpawner(TileEntityMobSpawner par1TileEntityMobSpawner, double par2, double par4, double par6, float par8)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2 + 0.5F, (float)par4, (float)par6 + 0.5F);
        func_98144_a(par1TileEntityMobSpawner.getSpawnerLogic(), par2, par4, par6, par8);
        GL11.glPopMatrix();
    }

    public static void func_98144_a(MobSpawnerBaseLogic par0MobSpawnerBaseLogic, double par1, double par3, double par5, float par7)
    {
        Entity var8 = par0MobSpawnerBaseLogic.func_98281_h();

        if (var8 != null)
        {
            var8.setWorld(par0MobSpawnerBaseLogic.getSpawnerWorld());
            float var9 = 0.4375F;
            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
            GL11.glRotatef((float)(par0MobSpawnerBaseLogic.field_98284_d + (par0MobSpawnerBaseLogic.field_98287_c - par0MobSpawnerBaseLogic.field_98284_d) * (double)par7) * 10.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(var9, var9, var9);
            var8.setLocationAndAngles(par1, par3, par5, 0.0F, 0.0F);
            RenderManager.instance.renderEntityWithPosYaw(var8, 0.0D, 0.0D, 0.0D, 0.0F, par7);
        }
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderTileEntityMobSpawner((TileEntityMobSpawner)par1TileEntity, par2, par4, par6, par8);
    }
}
