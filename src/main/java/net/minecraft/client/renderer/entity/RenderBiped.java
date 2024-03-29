package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBiped extends RenderLiving
{
    protected ModelBiped modelBipedMain;
    protected float field_77070_b;
    protected ModelBiped field_82423_g;
    protected ModelBiped field_82425_h;
    private static final Map field_110859_k = Maps.newHashMap();

    /** List of armor texture filenames. */
    private static final String[] bipedArmorFilenamePrefix = new String[] {"leather", "chainmail", "iron", "diamond", "gold"};
    private static final String __OBFID = "CL_00001001";

    public RenderBiped(ModelBiped par1ModelBiped, float par2)
    {
        this(par1ModelBiped, par2, 1.0F);
    }

    public RenderBiped(ModelBiped par1ModelBiped, float par2, float par3)
    {
        super(par1ModelBiped, par2);
        this.modelBipedMain = par1ModelBiped;
        this.field_77070_b = par3;
        this.func_82421_b();
    }

    protected void func_82421_b()
    {
        this.field_82423_g = new ModelBiped(1.0F);
        this.field_82425_h = new ModelBiped(0.5F);
    }

    public static ResourceLocation func_110857_a(ItemArmor par0ItemArmor, int par1)
    {
        return func_110858_a(par0ItemArmor, par1, (String)null);
    }

    public static ResourceLocation func_110858_a(ItemArmor par0ItemArmor, int par1, String par2Str)
    {
        String var3 = String.format("textures/models/armor/%s_layer_%d%s.png", new Object[] {bipedArmorFilenamePrefix[par0ItemArmor.renderIndex], Integer.valueOf(par1 == 2 ? 2 : 1), par2Str == null ? "" : String.format("_%s", new Object[]{par2Str})});
        ResourceLocation var4 = (ResourceLocation)field_110859_k.get(var3);

        if (var4 == null)
        {
            var4 = new ResourceLocation(var3);
            field_110859_k.put(var3, var4);
        }

        return var4;
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        ItemStack var4 = par1EntityLiving.func_130225_q(3 - par2);

        if (var4 != null)
        {
            Item var5 = var4.getItem();

            if (var5 instanceof ItemArmor)
            {
                ItemArmor var6 = (ItemArmor)var5;
                this.bindTexture(func_110857_a(var6, par2));
                ModelBiped var7 = par2 == 2 ? this.field_82425_h : this.field_82423_g;
                var7.bipedHead.showModel = par2 == 0;
                var7.bipedHeadwear.showModel = par2 == 0;
                var7.bipedBody.showModel = par2 == 1 || par2 == 2;
                var7.bipedRightArm.showModel = par2 == 1;
                var7.bipedLeftArm.showModel = par2 == 1;
                var7.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                var7.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                this.setRenderPassModel(var7);
                var7.onGround = this.mainModel.onGround;
                var7.isRiding = this.mainModel.isRiding;
                var7.isChild = this.mainModel.isChild;

                if (var6.getArmorMaterial() == ItemArmor.ArmorMaterial.CLOTH)
                {
                    int var8 = var6.getColor(var4);
                    float var9 = (float)(var8 >> 16 & 255) / 255.0F;
                    float var10 = (float)(var8 >> 8 & 255) / 255.0F;
                    float var11 = (float)(var8 & 255) / 255.0F;
                    GL11.glColor3f(var9, var10, var11);

                    if (var4.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(1.0F, 1.0F, 1.0F);

                if (var4.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    protected void func_82408_c(EntityLiving par1EntityLivingBase, int par2, float par3)
    {
        ItemStack var4 = par1EntityLivingBase.func_130225_q(3 - par2);

        if (var4 != null)
        {
            Item var5 = var4.getItem();

            if (var5 instanceof ItemArmor)
            {
                this.bindTexture(func_110858_a((ItemArmor)var5, par2, "overlay"));
                float var6 = 1.0F;
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        ItemStack var10 = par1EntityLiving.getHeldItem();
        this.func_82420_a(par1EntityLiving, var10);
        double var11 = par4 - (double)par1EntityLiving.yOffset;

        if (par1EntityLiving.isSneaking())
        {
            var11 -= 0.125D;
        }

        super.doRender(par1EntityLiving, par2, var11, par6, par8, par9);
        this.field_82423_g.aimedBow = this.field_82425_h.aimedBow = this.modelBipedMain.aimedBow = false;
        this.field_82423_g.isSneak = this.field_82425_h.isSneak = this.modelBipedMain.isSneak = false;
        this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = 0;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityLiving par1EntityLiving)
    {
        return null;
    }

    protected void func_82420_a(EntityLiving par1EntityLiving, ItemStack par2ItemStack)
    {
        this.field_82423_g.heldItemRight = this.field_82425_h.heldItemRight = this.modelBipedMain.heldItemRight = par2ItemStack != null ? 1 : 0;
        this.field_82423_g.isSneak = this.field_82425_h.isSneak = this.modelBipedMain.isSneak = par1EntityLiving.isSneaking();
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(par1EntityLiving, par2);
        ItemStack var3 = par1EntityLiving.getHeldItem();
        ItemStack var4 = par1EntityLiving.func_130225_q(3);
        Item var5;
        float var6;

        if (var4 != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);
            var5 = var4.getItem();

            if (var5 instanceof ItemBlock)
            {
                if (RenderBlocks.renderItemIn3d(Block.getBlockFromItem(var5).getRenderType()))
                {
                    var6 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var6, -var6, -var6);
                }

                this.renderManager.itemRenderer.renderItem(par1EntityLiving, var4, 0);
            }
            else if (var5 == Items.skull)
            {
                var6 = 1.0625F;
                GL11.glScalef(var6, -var6, -var6);
                String var7 = "";

                if (var4.hasTagCompound() && var4.getTagCompound().func_150297_b("SkullOwner", 8))
                {
                    var7 = var4.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.field_147536_b.func_147530_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var4.getItemDamage(), var7);
            }

            GL11.glPopMatrix();
        }

        if (var3 != null && var3.getItem() != null)
        {
            var5 = var3.getItem();
            GL11.glPushMatrix();

            if (this.mainModel.isChild)
            {
                var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.625F, 0.0F);
                GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                GL11.glScalef(var6, var6, var6);
            }

            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

            if (var5 instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(var5).getRenderType()))
            {
                var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var6 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-var6, -var6, var6);
            }
            else if (var5 == Items.bow)
            {
                var6 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (var5.isFull3D())
            {
                var6 = 0.625F;

                if (var5.shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.func_82422_c();
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var6 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(var6, var6, var6);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            float var8;
            float var9;
            int var13;

            if (var3.getItem().requiresMultipleRenderPasses())
            {
                for (var13 = 0; var13 <= 1; ++var13)
                {
                    int var11 = var3.getItem().getColorFromItemStack(var3, var13);
                    var8 = (float)(var11 >> 16 & 255) / 255.0F;
                    var9 = (float)(var11 >> 8 & 255) / 255.0F;
                    float var10 = (float)(var11 & 255) / 255.0F;
                    GL11.glColor4f(var8, var9, var10, 1.0F);
                    this.renderManager.itemRenderer.renderItem(par1EntityLiving, var3, var13);
                }
            }
            else
            {
                var13 = var3.getItem().getColorFromItemStack(var3, 0);
                float var12 = (float)(var13 >> 16 & 255) / 255.0F;
                var8 = (float)(var13 >> 8 & 255) / 255.0F;
                var9 = (float)(var13 & 255) / 255.0F;
                GL11.glColor4f(var12, var8, var9, 1.0F);
                this.renderManager.itemRenderer.renderItem(par1EntityLiving, var3, 0);
            }

            GL11.glPopMatrix();
        }
    }

    protected void func_82422_c()
    {
        GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
    }

    protected void func_82408_c(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    {
        this.func_82408_c((EntityLiving)par1EntityLivingBase, par2, par3);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
    {
        return this.shouldRenderPass((EntityLiving)par1EntityLivingBase, par2, par3);
    }

    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.renderEquippedItems((EntityLiving)par1EntityLivingBase, par2);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityLivingBase par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityLiving)par1Entity, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityLiving)par1Entity);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityLiving)par1Entity, par2, par4, par6, par8, par9);
    }
}
