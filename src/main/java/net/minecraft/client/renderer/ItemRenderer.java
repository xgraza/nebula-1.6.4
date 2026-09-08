package net.minecraft.client.renderer;

import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.event.render.EventRenderWaterEffects;
import ez.nebula.client.impl.module.combat.KillAuraModule;
import ez.nebula.client.impl.module.render.GlintModule;
import ez.nebula.client.impl.module.render.NoRenderModule;
import ez.nebula.client.impl.module.render.ViewModelModule;
import ez.nebula.client.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static org.lwjgl.opengl.GL11.*;

public class ItemRenderer
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /**
     * A reference to the Minecraft object.
     */
    protected final Minecraft mc;
    protected ItemStack itemToRender;

    /**
     * How far the current item has been equipped (0 disequipped and 1 fully up)
     */
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderBlocks renderBlocksIr = new RenderBlocks();

    /**
     * The index of the currently held item (0-8, or -1 if not yet updated)
     */
    private int equippedItemSlot = -1;

    public ItemRenderer(Minecraft minecraft)
    {
        this.mc = minecraft;
    }

    /**
     * Renders the item stack for being in an entity's hand Args: itemStack
     */
    public void renderItem(EntityLivingBase entity, ItemStack itemStack, int par3)
    {
        glPushMatrix();
        TextureManager var4 = mc.getTextureManager();
        Item var5 = itemStack.getItem();
        Block var6 = Block.getBlockFromItem(var5);

        if (itemStack.getItemSpriteNumber() == 0 && var5 instanceof ItemBlock && RenderBlocks.renderItemIn3d(var6.getRenderType()))
        {
            var4.bindTexture(var4.getResourceLocation(0));

            if (itemStack != null && itemStack.getItem() instanceof ItemCloth)
            {
                glEnable(GL_BLEND);
                glDepthMask(false);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                renderBlocksIr.renderBlockAsItem(var6, itemStack.getItemDamage(), 1.0F);
                glDepthMask(true);
                glDisable(GL_BLEND);
            } else
            {
                renderBlocksIr.renderBlockAsItem(var6, itemStack.getItemDamage(), 1.0F);
            }
        } else
        {
            IIcon var7 = entity.getItemIcon(itemStack, par3);

            if (var7 == null)
            {
                glPopMatrix();
                return;
            }

            var4.bindTexture(var4.getResourceLocation(itemStack.getItemSpriteNumber()));
            TextureUtil.func_147950_a(false, false);
            Tessellator var8 = Tessellator.instance;
            float var9 = var7.getMinU();
            float var10 = var7.getMaxU();
            float var11 = var7.getMinV();
            float var12 = var7.getMaxV();
            float var13 = 0.0F;
            float var14 = 0.3F;
            glEnable(GL12.GL_RESCALE_NORMAL);
            glTranslatef(-var13, -var14, 0.0F);
            float var15 = 1.5F;
            glScalef(var15, var15, var15);
            glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            glTranslatef(-0.9375F, -0.0625F, 0.0F);
            renderItemIn2D(var8, var10, var11, var9, var12, var7.getIconWidth(), var7.getIconHeight(), 0.0625F);

            if (itemStack.hasEffect() && par3 == 0)
            {
                glDepthFunc(GL_EQUAL);
                glDisable(GL_LIGHTING);
                var4.bindTexture(RES_ITEM_GLINT);
                glEnable(GL_BLEND);
                OpenGlHelper.glBlendFunc(768, 1, 1, 0);
                float var16 = 0.76F;
                if (GlintModule.INSTANCE.isToggled())
                {
                    RenderUtil.setGLColorOpaque(GlintModule.INSTANCE.colorSetting.getValue().getRGB());
                } else
                {
                    glColor4f(0.5F * var16, 0.25F * var16, 0.8F * var16, 1.0F);
                }
                glMatrixMode(GL_TEXTURE);
                glPushMatrix();
                float var17 = 0.125F;
                glScalef(var17, var17, var17);
                float var18 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                glTranslatef(var18, 0.0F, 0.0F);
                glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                glPopMatrix();
                glPushMatrix();
                glScalef(var17, var17, var17);
                var18 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                glTranslatef(-var18, 0.0F, 0.0F);
                glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                glPopMatrix();
                glMatrixMode(GL_MODELVIEW);
                glDisable(GL_BLEND);
                glEnable(GL_LIGHTING);
                glDepthFunc(GL_LEQUAL);
            }

            glDisable(GL12.GL_RESCALE_NORMAL);
            var4.bindTexture(var4.getResourceLocation(itemStack.getItemSpriteNumber()));
            TextureUtil.func_147945_b();
        }

        glPopMatrix();
    }

    /**
     * Renders an item held in hand as a 2D texture with thickness
     */
    public static void renderItemIn2D(Tessellator tessellator, float par1, float par2, float par3, float par4, int par5, int par6, float par7)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, par1, par4);
        tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, par3, par4);
        tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, par3, par2);
        tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, par1, par2);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        tessellator.addVertexWithUV(0.0D, 1.0D, 0.0F - par7, par1, par2);
        tessellator.addVertexWithUV(1.0D, 1.0D, 0.0F - par7, par3, par2);
        tessellator.addVertexWithUV(1.0D, 0.0D, 0.0F - par7, par3, par4);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0F - par7, par1, par4);
        tessellator.draw();
        float var8 = 0.5F * (par1 - par3) / (float) par5;
        float var9 = 0.5F * (par4 - par2) / (float) par6;
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        int var10;
        float var11;
        float var12;

        for (var10 = 0; var10 < par5; ++var10)
        {
            var11 = (float) var10 / (float) par5;
            var12 = par1 + (par3 - par1) * var11 - var8;
            tessellator.addVertexWithUV(var11, 0.0D, 0.0F - par7, var12, par4);
            tessellator.addVertexWithUV(var11, 0.0D, 0.0D, var12, par4);
            tessellator.addVertexWithUV(var11, 1.0D, 0.0D, var12, par2);
            tessellator.addVertexWithUV(var11, 1.0D, 0.0F - par7, var12, par2);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        float var13;

        for (var10 = 0; var10 < par5; ++var10)
        {
            var11 = (float) var10 / (float) par5;
            var12 = par1 + (par3 - par1) * var11 - var8;
            var13 = var11 + 1.0F / (float) par5;
            tessellator.addVertexWithUV(var13, 1.0D, 0.0F - par7, var12, par2);
            tessellator.addVertexWithUV(var13, 1.0D, 0.0D, var12, par2);
            tessellator.addVertexWithUV(var13, 0.0D, 0.0D, var12, par4);
            tessellator.addVertexWithUV(var13, 0.0D, 0.0F - par7, var12, par4);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (var10 = 0; var10 < par6; ++var10)
        {
            var11 = (float) var10 / (float) par6;
            var12 = par4 + (par2 - par4) * var11 - var9;
            var13 = var11 + 1.0F / (float) par6;
            tessellator.addVertexWithUV(0.0D, var13, 0.0D, par1, var12);
            tessellator.addVertexWithUV(1.0D, var13, 0.0D, par3, var12);
            tessellator.addVertexWithUV(1.0D, var13, 0.0F - par7, par3, var12);
            tessellator.addVertexWithUV(0.0D, var13, 0.0F - par7, par1, var12);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (var10 = 0; var10 < par6; ++var10)
        {
            var11 = (float) var10 / (float) par6;
            var12 = par4 + (par2 - par4) * var11 - var9;
            tessellator.addVertexWithUV(1.0D, var11, 0.0D, par3, var12);
            tessellator.addVertexWithUV(0.0D, var11, 0.0D, par1, var12);
            tessellator.addVertexWithUV(0.0D, var11, 0.0F - par7, par1, var12);
            tessellator.addVertexWithUV(1.0D, var11, 0.0F - par7, par3, var12);
        }

        tessellator.draw();
    }

    /**
     * Renders the active item in the player's hand when in first person mode. Args: partialTickTime
     */
    public void renderItemInFirstPerson(float partialTickTime)
    {
        float equipProgress = prevEquippedProgress + (equippedProgress - prevEquippedProgress) * partialTickTime;
        EntityClientPlayerMP player = mc.thePlayer;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTickTime;
        glPushMatrix();

        glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        glRotatef(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTickTime, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        glPopMatrix();

        float armPitch = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * partialTickTime;
        float armYaw = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * partialTickTime;
        glRotatef((player.rotationPitch - armPitch) * 0.1F, 1.0F, 0.0F, 0.0F);
        glRotatef((player.rotationYaw - armYaw) * 0.1F, 0.0F, 1.0F, 0.0F);
        ItemStack renderItemStack = itemToRender;

        if (renderItemStack != null && renderItemStack.getItem() instanceof ItemCloth)
        {
            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        int var9 = mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ), 0);
        int var10 = var9 % 65536;
        int var11 = var9 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var10, (float) var11);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var13;
        float var14;
        float var15;

        if (renderItemStack != null)
        {
            int stackColor = renderItemStack.getItem().getColorFromItemStack(renderItemStack, 0);
            var13 = (float) (stackColor >> 16 & 255) / 255.0F;
            var14 = (float) (stackColor >> 8 & 255) / 255.0F;
            var15 = (float) (stackColor & 255) / 255.0F;
            glColor4f(var13, var14, var15, 1.0F);
        } else
        {
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        float var16;
        float var17;
        float var18;
        float var22;
        RenderPlayer renderPlayer;

        final ViewModelModule vm = ViewModelModule.INSTANCE;

        if (renderItemStack != null && vm.isToggled())
        {
            glTranslated(vm.translateXSetting.getValue(),
                    vm.translateYSetting.getValue(),
                    vm.translateZSetting.getValue());
            glScaled(vm.scaleXSetting.getValue(),
                    vm.scaleYSetting.getValue(),
                    vm.scaleZSetting.getValue());
        }

        if (renderItemStack != null && renderItemStack.getItem() == Items.filled_map)
        {
            glPushMatrix();
            var22 = 0.8F;
            var13 = player.getSwingProgress(partialTickTime);
            var14 = MathHelper.sin(var13 * (float) Math.PI);
            var15 = MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI);
            glTranslatef(-var15 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI * 2.0F) * 0.2F, -var14 * 0.2F);
            var13 = 1.0F - pitch / 45.0F + 0.1F;

            if (var13 < 0.0F)
            {
                var13 = 0.0F;
            }

            if (var13 > 1.0F)
            {
                var13 = 1.0F;
            }

            var13 = -MathHelper.cos(var13 * (float) Math.PI) * 0.5F + 0.5F;
            glTranslatef(0.0F, 0.0F * var22 - (1.0F - equipProgress) * 1.2F - var13 * 0.5F + 0.04F, -0.9F * var22);
            glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            glRotatef(var13 * -85.0F, 0.0F, 0.0F, 1.0F);
            glEnable(GL12.GL_RESCALE_NORMAL);
            mc.getTextureManager().bindTexture(player.getLocationSkin());

            for (int var24 = 0; var24 < 2; ++var24)
            {
                int var25 = var24 * 2 - 1;
                glPushMatrix();
                glTranslatef(-0.0F, -0.6F, 1.1F * (float) var25);
                glRotatef((float) (-45 * var25), 1.0F, 0.0F, 0.0F);
                glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
                glRotatef((float) (-65 * var25), 0.0F, 1.0F, 0.0F);
                renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(mc.thePlayer);
                var18 = 1.0F;
                glScalef(var18, var18, var18);
                renderPlayer.renderFirstPersonArm(mc.thePlayer);
                glPopMatrix();
            }

            var14 = player.getSwingProgress(partialTickTime);
            var15 = MathHelper.sin(var14 * var14 * (float) Math.PI);
            var16 = MathHelper.sin(MathHelper.sqrt_float(var14) * (float) Math.PI);
            glRotatef(-var15 * 20.0F, 0.0F, 1.0F, 0.0F);
            glRotatef(-var16 * 20.0F, 0.0F, 0.0F, 1.0F);
            glRotatef(-var16 * 80.0F, 1.0F, 0.0F, 0.0F);
            var17 = 0.38F;
            glScalef(var17, var17, var17);
            glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            glTranslatef(-1.0F, -1.0F, 0.0F);
            var18 = 0.015625F;
            glScalef(var18, var18, var18);
            mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
            Tessellator var30 = Tessellator.instance;
            glNormal3f(0.0F, 0.0F, -1.0F);
            var30.startDrawingQuads();
            byte var31 = 7;
            var30.addVertexWithUV(-var31, 128 + var31, 0.0D, 0.0D, 1.0D);
            var30.addVertexWithUV(128 + var31, 128 + var31, 0.0D, 1.0D, 1.0D);
            var30.addVertexWithUV(128 + var31, -var31, 0.0D, 1.0D, 0.0D);
            var30.addVertexWithUV(-var31, -var31, 0.0D, 0.0D, 0.0D);
            var30.draw();
            MapData var21 = Items.filled_map.getMapData(renderItemStack, mc.theWorld);

            if (var21 != null)
            {
                mc.entityRenderer.getMapItemRenderer().func_148250_a(var21, false);
            }

            glPopMatrix();
        } else if (renderItemStack != null)
        {
            glPushMatrix();
            var22 = 0.8F;

            if (player.getItemInUseCount() > 0)
            {
                EnumAction var23 = renderItemStack.getItemUseAction();

                if (var23 == EnumAction.eat || var23 == EnumAction.drink)
                {
                    var14 = (float) player.getItemInUseCount() - partialTickTime + 1.0F;
                    var15 = 1.0F - var14 / (float) renderItemStack.getMaxItemUseDuration();
                    var16 = 1.0F - var15;
                    var16 = var16 * var16 * var16;
                    var16 = var16 * var16 * var16;
                    var16 = var16 * var16 * var16;
                    var17 = 1.0F - var16;
                    glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(var14 / 4.0F * (float) Math.PI) * 0.1F) * (float) ((double) var15 > 0.2D ? 1 : 0), 0.0F);
                    glTranslatef(var17 * 0.6F, -var17 * 0.5F, 0.0F);
                    glRotatef(var17 * 90.0F, 0.0F, 1.0F, 0.0F);
                    glRotatef(var17 * 10.0F, 1.0F, 0.0F, 0.0F);
                    glRotatef(var17 * 30.0F, 0.0F, 0.0F, 1.0F);
                }
            } else
            {
                var13 = player.getSwingProgress(partialTickTime);
                var14 = MathHelper.sin(var13 * (float) Math.PI);
                var15 = MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI);
                glTranslatef(-var15 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI * 2.0F) * 0.2F, -var14 * 0.2F);
            }

            var13 = player.getSwingProgress(partialTickTime);
            var14 = MathHelper.sin(var13 * var13 * (float) Math.PI);
            var15 = MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI);
            var16 = 0.4F;

            if (!vm.isToggled())
            {
                glTranslatef(0.7F * var22, -0.65F * var22 - (1.0F - equipProgress) * 0.6F, -0.9F * var22);
                glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                glRotatef(-var14 * 20.0F, 0.0F, 1.0F, 0.0F);
                glRotatef(-var15 * 20.0F, 0.0F, 0.0F, 1.0F);
                glRotatef(-var15 * 80.0F, 1.0F, 0.0F, 0.0F);
                glEnable(GL12.GL_RESCALE_NORMAL);
                glScalef(var16, var16, var16);
            }

            float var19;
            float var20;

            if (player.getItemInUseCount() > 0 || KillAuraModule.INSTANCE.isBlocking())
            {
                EnumAction var26 = renderItemStack.getItemUseAction();
                if (KillAuraModule.INSTANCE.isBlocking())
                {
                    var26 = EnumAction.block;
                }

                if (var26 != EnumAction.block && vm.isToggled())
                {
                    glTranslatef(0.7F * var22, -0.65F * var22 - (1.0F - equipProgress) * 0.6F, -0.9F * var22);
                    glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                    glRotatef(-var14 * 20.0F, 0.0F, 1.0F, 0.0F);
                    glRotatef(-var15 * 20.0F, 0.0F, 0.0F, 1.0F);
                    glRotatef(-var15 * 80.0F, 1.0F, 0.0F, 0.0F);
                    glEnable(GL12.GL_RESCALE_NORMAL);
                    glScalef(var16, var16, var16);
                }

                if (var26 == EnumAction.block)
                {
                    if (vm.isToggled())
                    {
                        glTranslated(0.0, -0.07, 0.0);
                    }

                    float f = MathHelper.sin(var13 * var13 * (float) Math.PI);
                    float f1 = MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI);

                    if (vm.isToggled())
                    {
                        switch (vm.swordAnimationSetting.getValue())
                        {
                            case VANILLA:
                                transformFirstPersonItem(1.0f - equipProgress, var13);
                                doBlockTransformations();
                                break;

                            case _1_8:
                                transformFirstPersonItem(1.0f - equipProgress, 0.0f);
                                doBlockTransformations();
                                break;

                            case EXHIBITION:
                                glTranslatef(0.56F, -0.52F, -0.71999997F);
                                glTranslatef(0.0F, (1.0f - equipProgress) * -0.1F, 0.0F);
                                glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                                glRotatef(-f1 * 40.0F / 2.0F, f1 / 2.0F, -0.0F, 9.0F);
                                glRotatef(-f1 * 30.0F, 1.0F, f1 / 2.0F, -0.0F);
                                glScalef(0.4F, 0.4F, 0.4F);
                                doBlockTransformations();
                                break;

                            case AVATAR:
                                glTranslatef(0.56F, -0.45F, -0.71999997F);
                                glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
                                glRotatef(f * -20.0f, 0.0f, 1.0f, 0.0f);
                                glRotatef(f1 * -20.0f, 0.0f, 0.0f, 1.0f);
                                glRotatef(f1 * -40.0f, 1.0f, 0.0f, 0.0f);
                                glScalef(0.4F, 0.4F, 0.4F);
                                doBlockTransformations();
                                break;

                            case JIGSAW:
                                glTranslatef(0.56f, -0.42f, -0.71999997f);
                                glTranslatef(0.1f * f1, -0.0f, -0.21999997f * f1);
                                glTranslatef(0.0f, equipProgress * -0.15f, 0.0f);
                                glRotatef(equipProgress * 45.0f, 0.0f, 1.0f, 0.0f);
                                glScalef(0.4f, 0.4f, 0.4f);
                                doBlockTransformations();
                                break;

                            case TAP:
                                glTranslatef(0.56f, -0.42f, -0.71999997f);
                                glTranslatef(0.0f, equipProgress * -0.15f, 0.0f);
                                glRotatef(30.0f, 0.0f, 1.0f, 0.0f);
                                glRotatef(f1 * -30.0f, 0.0f, 1.0f, 0.0f);
                                glScalef(0.4f, 0.4f, 0.4f);
                                doBlockTransformations();
                                break;

                            case SIGMA:
                                transformFirstPersonItem(equipProgress * 0.5f, 0.0f);
                                glRotatef(-f * 55.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                glRotatef(-f * 45.0f, 1.0f, f / 2.0f, -0.0f);
                                doBlockTransformations();
                                glTranslated(1.2, 0.3, 0.5);
                                glTranslatef(-1.0f, player.isSneaking() ? -0.1f : -0.2f, 0.2f);
                                break;

                            case FATHUM:
                                glRotated(25, 0,0.2,0);
                                this.transformFirstPersonItem(0.0f, var13);
                                glScalef(0.9F, 0.9F, 0.9F);
                                doBlockTransformations();
                                break;

                            case PULL:
                                GL11.glTranslatef(0.56F, -0.52F, -0.71999997F);
                                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                                GL11.glRotatef(f1 * -20.0F, 1.0F, 0.0F, 0.0F);
                                GL11.glRotatef(f1 * 22.0f, 0.5f, f1 * 5, 0.0f);
                                GL11.glScalef(0.4F, 0.4F, 0.4F);
                                GL11.glTranslated(0.0, 0.1, 0.0);
                                doBlockTransformations();
                                break;

                            case BONK:
                                GL11.glTranslatef(0.56F, -0.34F, -0.71999997F);
                                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                                GL11.glRotatef(f1 * -10.0f, 20.0F, 1.0F, 0.0F);
                                GL11.glRotatef(-f1 * 20.0f, 1.5f, (f1 / 1.1f), 0.0f);
                                GL11.glScalef(0.4F, 0.4F, 0.4F);
                                doBlockTransformations();
                                break;

                            case NEBULA:
                                GL11.glTranslatef(0.56F, -0.52F, -0.71999997F);
                                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                                GL11.glRotatef(f1 * -20.0F, 1.0F, 0.0F, 0.0F);
                                GL11.glScalef(0.4F, 0.4F, 0.4F);
                                GL11.glTranslated(0.0, 0.1, 0.0);
                                doBlockTransformations();
                                break;
                        }

                    } else
                    {
                        doBlockTransformations();
                    }
                } else if (var26 == EnumAction.bow)
                {
                    glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                    glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                    glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                    glTranslatef(-0.9F, 0.2F, 0.0F);
                    var18 = (float) renderItemStack.getMaxItemUseDuration() - ((float) player.getItemInUseCount() - partialTickTime + 1.0F);
                    var19 = var18 / 20.0F;
                    var19 = (var19 * var19 + var19 * 2.0F) / 3.0F;

                    if (var19 > 1.0F)
                    {
                        var19 = 1.0F;
                    }

                    if (var19 > 0.1F)
                    {
                        glTranslatef(0.0F, MathHelper.sin((var18 - 0.1F) * 1.3F) * 0.01F * (var19 - 0.1F), 0.0F);
                    }

                    glTranslatef(0.0F, 0.0F, var19 * 0.1F);
                    glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                    glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    glTranslatef(0.0F, 0.5F, 0.0F);
                    var20 = 1.0F + var19 * 0.2F;
                    glScalef(1.0F, 1.0F, var20);
                    glTranslatef(0.0F, -0.5F, 0.0F);
                    glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                    glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                }
            } else
            {
                if (vm.isToggled())
                {
                    glTranslatef(0.7F * var22, -0.65F * var22 - (1.0F - equipProgress) * 0.6F, -0.9F * var22);
                    glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                    glRotatef(-var14 * 20.0F, 0.0F, 1.0F, 0.0F);
                    glRotatef(-var15 * 20.0F, 0.0F, 0.0F, 1.0F);
                    glRotatef(-var15 * 80.0F, 1.0F, 0.0F, 0.0F);
                    glEnable(GL12.GL_RESCALE_NORMAL);
                    glScalef(var16, var16, var16);
                }
            }

            if (renderItemStack.getItem().shouldRotateAroundWhenRendering())
            {
                glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (renderItemStack.getItem().requiresMultipleRenderPasses())
            {
                renderItem(player, renderItemStack, 0);
                int var28 = renderItemStack.getItem().getColorFromItemStack(renderItemStack, 1);
                var18 = (float) (var28 >> 16 & 255) / 255.0F;
                var19 = (float) (var28 >> 8 & 255) / 255.0F;
                var20 = (float) (var28 & 255) / 255.0F;
                glColor4f(var18, var19, var20, 1.0F);
                renderItem(player, renderItemStack, 1);
            } else
            {
                renderItem(player, renderItemStack, 0);
            }

            glPopMatrix();
        } else if (!player.isInvisible())
        {
            glPushMatrix();
            var22 = 0.8F;
            var13 = player.getSwingProgress(partialTickTime);
            var14 = MathHelper.sin(var13 * (float) Math.PI);
            var15 = MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI);
            glTranslatef(-var15 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI * 2.0F) * 0.4F, -var14 * 0.4F);
            glTranslatef(0.8F * var22, -0.75F * var22 - (1.0F - equipProgress) * 0.6F, -0.9F * var22);
            glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            glEnable(GL12.GL_RESCALE_NORMAL);
            var13 = player.getSwingProgress(partialTickTime);
            var14 = MathHelper.sin(var13 * var13 * (float) Math.PI);
            var15 = MathHelper.sin(MathHelper.sqrt_float(var13) * (float) Math.PI);
            glRotatef(var15 * 70.0F, 0.0F, 1.0F, 0.0F);
            glRotatef(-var14 * 20.0F, 0.0F, 0.0F, 1.0F);
            mc.getTextureManager().bindTexture(player.getLocationSkin());
            glTranslatef(-1.0F, 3.6F, 3.5F);
            glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
            glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
            glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            glScalef(1.0F, 1.0F, 1.0F);
            glTranslatef(5.6F, 0.0F, 0.0F);
            renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(mc.thePlayer);
            var18 = 1.0F;
            glScalef(var18, var18, var18);
            renderPlayer.renderFirstPersonArm(mc.thePlayer);
            glPopMatrix();
        }

        if (renderItemStack != null && renderItemStack.getItem() instanceof ItemCloth)
        {
            glDisable(GL_BLEND);
        }

        glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Translate and rotate the render for a sword block
     */
    private void doBlockTransformations()
    {
        glTranslatef(-0.5F, 0.2F, 0.0F);
        glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
        glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
        glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Performs transformations prior to the rendering of a held item in first person.
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress)
    {
        glTranslatef(0.56F, -0.52F, -0.71999997F);
        glTranslatef(0.0F, equipProgress * -0.6F, 0.0F);
        glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        glRotatef(f * -20.0F, 0.0F, 1.0F, 0.0F);
        glRotatef(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        glRotatef(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        glScalef(0.4F, 0.4F, 0.4F);
    }

    /**
     * Renders all the overlays that are in first person mode. Args: partialTickTime
     */
    public void renderOverlays(float par1)
    {
        glDisable(GL_ALPHA_TEST);

        if (mc.thePlayer.isBurning())
        {
            renderFireInFirstPerson(par1);
        }

        if (mc.thePlayer.isEntityInsideOpaqueBlock())
        {
            int var2 = MathHelper.floor_double(mc.thePlayer.posX);
            int var3 = MathHelper.floor_double(mc.thePlayer.posY);
            int var4 = MathHelper.floor_double(mc.thePlayer.posZ);
            Block var5 = mc.theWorld.getBlock(var2, var3, var4);

            if (mc.theWorld.getBlock(var2, var3, var4).isNormalCube())
            {
                renderInsideOfBlock(par1, var5.getBlockTextureFromSide(2));
            } else
            {
                for (int var6 = 0; var6 < 8; ++var6)
                {
                    float var7 = ((float) ((var6 >> 0) % 2) - 0.5F) * mc.thePlayer.width * 0.9F;
                    float var8 = ((float) ((var6 >> 1) % 2) - 0.5F) * mc.thePlayer.height * 0.2F;
                    float var9 = ((float) ((var6 >> 2) % 2) - 0.5F) * mc.thePlayer.width * 0.9F;
                    int var10 = MathHelper.floor_float((float) var2 + var7);
                    int var11 = MathHelper.floor_float((float) var3 + var8);
                    int var12 = MathHelper.floor_float((float) var4 + var9);

                    if (mc.theWorld.getBlock(var10, var11, var12).isNormalCube())
                    {
                        var5 = mc.theWorld.getBlock(var10, var11, var12);
                    }
                }
            }

            if (var5.getMaterial() != Material.air)
            {
                renderInsideOfBlock(par1, var5.getBlockTextureFromSide(2));
            }
        }

        if (mc.thePlayer.isInsideOfMaterial(Material.water))
        {
            renderWarpedTextureOverlay(par1);
        }

        glEnable(GL_ALPHA_TEST);
    }

    /**
     * Renders the texture of the block the player is inside as an overlay. Args: partialTickTime, blockTextureIndex
     */
    private void renderInsideOfBlock(float par1, IIcon par2Icon)
    {
        if (NoRenderModule.INSTANCE.isToggled()
                && NoRenderModule.INSTANCE.blockSetting.getValue())
        {
            return;
        }

        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator var3 = Tessellator.instance;
        float var4 = 0.1F;
        glColor4f(var4, var4, var4, 0.5F);
        glPushMatrix();
        float var5 = -1.0F;
        float var6 = 1.0F;
        float var7 = -1.0F;
        float var8 = 1.0F;
        float var9 = -0.5F;
        float var10 = par2Icon.getMinU();
        float var11 = par2Icon.getMaxU();
        float var12 = par2Icon.getMinV();
        float var13 = par2Icon.getMaxV();
        var3.startDrawingQuads();
        var3.addVertexWithUV(var5, var7, var9, var11, var13);
        var3.addVertexWithUV(var6, var7, var9, var10, var13);
        var3.addVertexWithUV(var6, var8, var9, var10, var12);
        var3.addVertexWithUV(var5, var8, var9, var11, var12);
        var3.draw();
        glPopMatrix();
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay. Args: parialTickTime
     */
    private void renderWarpedTextureOverlay(float par1)
    {
        if (EventBus.dispatch(new EventRenderWaterEffects()))
        {
            return;
        }
        mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
        Tessellator var2 = Tessellator.instance;
        float var3 = mc.thePlayer.getBrightness(par1);
        glColor4f(var3, var3, var3, 0.5F);
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glPushMatrix();
        float var4 = 4.0F;
        float var5 = -1.0F;
        float var6 = 1.0F;
        float var7 = -1.0F;
        float var8 = 1.0F;
        float var9 = -0.5F;
        float var10 = -mc.thePlayer.rotationYaw / 64.0F;
        float var11 = mc.thePlayer.rotationPitch / 64.0F;
        var2.startDrawingQuads();
        var2.addVertexWithUV(var5, var7, var9, var4 + var10, var4 + var11);
        var2.addVertexWithUV(var6, var7, var9, 0.0F + var10, var4 + var11);
        var2.addVertexWithUV(var6, var8, var9, 0.0F + var10, 0.0F + var11);
        var2.addVertexWithUV(var5, var8, var9, var4 + var10, 0.0F + var11);
        var2.draw();
        glPopMatrix();
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glDisable(GL_BLEND);
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     */
    private void renderFireInFirstPerson(float par1)
    {
        if (NoRenderModule.INSTANCE.isToggled()
                && NoRenderModule.INSTANCE.fireSetting.getValue())
        {
            return;
        }

        Tessellator var2 = Tessellator.instance;
        glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        float var3 = 1.0F;

        for (int var4 = 0; var4 < 2; ++var4)
        {
            glPushMatrix();
            IIcon var5 = Blocks.fire.getFireIcon(1);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float var6 = var5.getMinU();
            float var7 = var5.getMaxU();
            float var8 = var5.getMinV();
            float var9 = var5.getMaxV();
            float var10 = (0.0F - var3) / 2.0F;
            float var11 = var10 + var3;
            float var12 = 0.0F - var3 / 2.0F;
            float var13 = var12 + var3;
            float var14 = -0.5F;
            glTranslatef((float) (-(var4 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            glRotatef((float) (var4 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            var2.startDrawingQuads();
            var2.addVertexWithUV(var10, var12, var14, var7, var9);
            var2.addVertexWithUV(var11, var12, var14, var6, var9);
            var2.addVertexWithUV(var11, var13, var14, var6, var8);
            var2.addVertexWithUV(var10, var13, var14, var7, var8);
            var2.draw();
            glPopMatrix();
        }

        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glDisable(GL_BLEND);
    }

    public void updateEquippedItem()
    {
        prevEquippedProgress = equippedProgress;
        EntityClientPlayerMP var1 = mc.thePlayer;
        ItemStack var2 = var1.inventory.getCurrentItem();
        boolean var3 = equippedItemSlot == var1.inventory.currentItem && var2 == itemToRender;

        if (itemToRender == null && var2 == null)
        {
            var3 = true;
        }

        if (var2 != null && itemToRender != null && var2 != itemToRender && var2.getItem() == itemToRender.getItem() && var2.getItemDamage() == itemToRender.getItemDamage())
        {
            itemToRender = var2;
            var3 = true;
        }

        float var4 = 0.4F;
        float var5 = var3 ? 1.0F : 0.0F;
        float var6 = var5 - equippedProgress;

        if (var6 < -var4)
        {
            var6 = -var4;
        }

        if (var6 > var4)
        {
            var6 = var4;
        }

        equippedProgress += var6;

        if (equippedProgress < 0.1F)
        {
            itemToRender = var2;
            equippedItemSlot = var1.inventory.currentItem;
        }
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress()
    {
        equippedProgress = 0.0F;
    }
}
