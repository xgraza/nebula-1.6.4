package net.minecraft.src;

import java.lang.reflect.Field;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererOF extends ItemRenderer
{
    /** A reference to the Minecraft object. */
    private Minecraft mc = null;

    /** Instance of RenderBlocks. */
    private RenderBlocks renderBlocksInstance = null;
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static Field ItemRenderer_renderBlockInstance = Reflector.getFieldByType(ItemRenderer.class, RenderBlocks.class);

    public ItemRendererOF(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
        this.mc = par1Minecraft;

        if (ItemRenderer_renderBlockInstance == null)
        {
            Config.error("ItemRenderOF not initialized");
        }

        try
        {
            this.renderBlocksInstance = (RenderBlocks)ItemRenderer_renderBlockInstance.get(this);
        }
        catch (IllegalAccessException var3)
        {
            throw new RuntimeException(var3);
        }
    }

    /**
     * Renders the item stack for being in an entity's hand Args: itemStack
     */
    public void renderItem(EntityLivingBase par1EntityLivingBase, ItemStack par2ItemStack, int par3)
    {
        GL11.glPushMatrix();
        TextureManager var4 = this.mc.getTextureManager();
        boolean hasForge = Reflector.MinecraftForgeClient.exists();
        Block block = null;

        if (par2ItemStack.getItem() instanceof ItemBlock && par2ItemStack.itemID < Block.blocksList.length)
        {
            block = Block.blocksList[par2ItemStack.itemID];
        }

        Object type = null;
        Object customRenderer = null;

        if (hasForge)
        {
            type = Reflector.getFieldValue(Reflector.ItemRenderType_EQUIPPED);
            customRenderer = Reflector.call(Reflector.MinecraftForgeClient_getItemRenderer, new Object[] {par2ItemStack, type});
        }

        if (customRenderer != null)
        {
            var4.bindTexture(var4.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
            Reflector.callVoid(Reflector.ForgeHooksClient_renderEquippedItem, new Object[] {type, customRenderer, this.renderBlocksInstance, par1EntityLivingBase, par2ItemStack});
        }
        else if (block != null && par2ItemStack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(block.getRenderType()))
        {
            var4.bindTexture(var4.getResourceLocation(0));
            this.renderBlocksInstance.renderBlockAsItem(Block.blocksList[par2ItemStack.itemID], par2ItemStack.getItemDamage(), 1.0F);
        }
        else
        {
            Icon var5 = par1EntityLivingBase.getItemIcon(par2ItemStack, par3);

            if (var5 == null)
            {
                GL11.glPopMatrix();
                return;
            }

            var4.bindTexture(var4.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
            Tessellator var6 = Tessellator.instance;
            float var7 = var5.getMinU();
            float var8 = var5.getMaxU();
            float var9 = var5.getMinV();
            float var10 = var5.getMaxV();
            float var11 = 0.0F;
            float var12 = 0.3F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(-var11, -var12, 0.0F);
            float var13 = 1.5F;
            GL11.glScalef(var13, var13, var13);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            renderItemIn2D(var6, var8, var9, var7, var10, var5.getIconWidth(), var5.getIconHeight(), 0.0625F);
            boolean renderEffect = false;

            if (Reflector.ForgeItemStack_hasEffect.exists())
            {
                renderEffect = Reflector.callBoolean(par2ItemStack, Reflector.ForgeItemStack_hasEffect, new Object[] {Integer.valueOf(par3)});
            }
            else
            {
                renderEffect = par2ItemStack.hasEffect() && par3 == 0;
            }

            if (renderEffect)
            {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                var4.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float var14 = 0.76F;
                GL11.glColor4f(0.5F * var14, 0.25F * var14, 0.8F * var14, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float var15 = 0.125F;
                GL11.glScalef(var15, var15, var15);
                float var16 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(var16, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(var6, 0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(var15, var15, var15);
                var16 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-var16, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(var6, 0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }

        GL11.glPopMatrix();
    }
}
