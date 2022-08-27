package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWitch extends RenderLiving
{
    private static final ResourceLocation witchTextures = new ResourceLocation("textures/entity/witch.png");
    private final ModelWitch witchModel;
    private static final String __OBFID = "CL_00001033";

    public RenderWitch()
    {
        super(new ModelWitch(0.0F), 0.5F);
        this.witchModel = (ModelWitch)this.mainModel;
    }

    public void doRender(EntityWitch par1EntityWitch, double par2, double par4, double par6, float par8, float par9)
    {
        ItemStack var10 = par1EntityWitch.getHeldItem();
        this.witchModel.field_82900_g = var10 != null;
        super.doRender((EntityLiving)par1EntityWitch, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getEntityTexture(EntityWitch par1EntityWitch)
    {
        return witchTextures;
    }

    protected void renderEquippedItems(EntityWitch par1EntityWitch, float par2)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(par1EntityWitch, par2);
        ItemStack var3 = par1EntityWitch.getHeldItem();

        if (var3 != null)
        {
            GL11.glPushMatrix();
            float var4;

            if (this.mainModel.isChild)
            {
                var4 = 0.5F;
                GL11.glTranslatef(0.0F, 0.625F, 0.0F);
                GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                GL11.glScalef(var4, var4, var4);
            }

            this.witchModel.villagerNose.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.53125F, 0.21875F);

            if (var3.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(var3.getItem()).getRenderType()))
            {
                var4 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var4 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var4, -var4, var4);
            }
            else if (var3.getItem() == Items.bow)
            {
                var4 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var4, -var4, var4);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (var3.getItem().isFull3D())
            {
                var4 = 0.625F;

                if (var3.getItem().shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.func_82410_b();
                GL11.glScalef(var4, -var4, var4);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var4 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(var4, var4, var4);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
            this.renderManager.itemRenderer.renderItem(par1EntityWitch, var3, 0);

            if (var3.getItem().requiresMultipleRenderPasses())
            {
                this.renderManager.itemRenderer.renderItem(par1EntityWitch, var3, 1);
            }

            GL11.glPopMatrix();
        }
    }

    protected void func_82410_b()
    {
        GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
    }

    protected void preRenderCallback(EntityWitch par1EntityWitch, float par2)
    {
        float var3 = 0.9375F;
        GL11.glScalef(var3, var3, var3);
    }

    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityWitch)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.preRenderCallback((EntityWitch)par1EntityLivingBase, par2);
    }

    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.renderEquippedItems((EntityWitch)par1EntityLivingBase, par2);
    }

    public void doRender(EntityLivingBase par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityWitch)par1Entity, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityWitch)par1Entity);
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityWitch)par1Entity, par2, par4, par6, par8, par9);
    }
}