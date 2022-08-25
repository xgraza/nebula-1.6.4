package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot
{
    private final Minecraft mc;
    protected int width;
    private int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected final int slotHeight;
    private int scrollUpButtonID;
    private int scrollDownButtonID;
    protected int mouseX;
    protected int mouseY;
    protected boolean field_148163_i = true;
    private float initialClickY = -2.0F;
    private float scrollMultiplier;
    private float amountScrolled;
    private int selectedElement = -1;
    private long lastClicked;
    private boolean showSelectionBox = true;
    private boolean field_148165_u;
    protected int field_148160_j;
    private boolean field_148164_v = true;
    private static final String __OBFID = "CL_00000679";

    public GuiSlot(Minecraft par1Minecraft, int par2, int par3, int par4, int par5, int par6)
    {
        this.mc = par1Minecraft;
        this.width = par2;
        this.height = par3;
        this.top = par4;
        this.bottom = par5;
        this.slotHeight = par6;
        this.left = 0;
        this.right = par2;
    }

    public void func_148122_a(int p_148122_1_, int p_148122_2_, int p_148122_3_, int p_148122_4_)
    {
        this.width = p_148122_1_;
        this.height = p_148122_2_;
        this.top = p_148122_3_;
        this.bottom = p_148122_4_;
        this.left = 0;
        this.right = p_148122_1_;
    }

    public void setShowSelectionBox(boolean p_148130_1_)
    {
        this.showSelectionBox = p_148130_1_;
    }

    protected void func_148133_a(boolean p_148133_1_, int p_148133_2_)
    {
        this.field_148165_u = p_148133_1_;
        this.field_148160_j = p_148133_2_;

        if (!p_148133_1_)
        {
            this.field_148160_j = 0;
        }
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int var1, boolean var2, int var3, int var4);

    protected abstract boolean isSelected(int var1);

    protected int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.field_148160_j;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7);

    protected void func_148129_a(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {}

    protected void func_148132_a(int p_148132_1_, int p_148132_2_) {}

    protected void func_148142_b(int p_148142_1_, int p_148142_2_) {}

    public int func_148124_c(int p_148124_1_, int p_148124_2_)
    {
        int var3 = this.left + this.width / 2 - this.func_148139_c() / 2;
        int var4 = this.left + this.width / 2 + this.func_148139_c() / 2;
        int var5 = p_148124_2_ - this.top - this.field_148160_j + (int)this.amountScrolled - 4;
        int var6 = var5 / this.slotHeight;
        return p_148124_1_ < this.getScrollBarX() && p_148124_1_ >= var3 && p_148124_1_ <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getSize() ? var6 : -1;
    }

    public void registerScrollButtons(int p_148134_1_, int p_148134_2_)
    {
        this.scrollUpButtonID = p_148134_1_;
        this.scrollDownButtonID = p_148134_2_;
    }

    private void bindAmountScrolled()
    {
        int var1 = this.func_148135_f();

        if (var1 < 0)
        {
            var1 /= 2;
        }

        if (!this.field_148163_i && var1 < 0)
        {
            var1 = 0;
        }

        if (this.amountScrolled < 0.0F)
        {
            this.amountScrolled = 0.0F;
        }

        if (this.amountScrolled > (float)var1)
        {
            this.amountScrolled = (float)var1;
        }
    }

    public int func_148135_f()
    {
        return this.getContentHeight() - (this.bottom - this.top - 4);
    }

    public int func_148148_g()
    {
        return (int)this.amountScrolled;
    }

    public boolean func_148141_e(int p_148141_1_)
    {
        return p_148141_1_ >= this.top && p_148141_1_ <= this.bottom;
    }

    public void func_148145_f(int p_148145_1_)
    {
        this.amountScrolled += (float)p_148145_1_;
        this.bindAmountScrolled();
        this.initialClickY = -2.0F;
    }

    public void actionPerformed(GuiButton p_148147_1_)
    {
        if (p_148147_1_.enabled)
        {
            if (p_148147_1_.id == this.scrollUpButtonID)
            {
                this.amountScrolled -= (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            }
            else if (p_148147_1_.id == this.scrollDownButtonID)
            {
                this.amountScrolled += (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            }
        }
    }

    public void drawScreen(int p_148128_1_, int p_148128_2_, float p_148128_3_)
    {
        this.mouseX = p_148128_1_;
        this.mouseY = p_148128_2_;
        this.drawBackground();
        int var4 = this.getSize();
        int var5 = this.getScrollBarX();
        int var6 = var5 + 6;
        int var9;
        int var10;
        int var13;
        int var19;

        if (p_148128_1_ > this.left && p_148128_1_ < this.right && p_148128_2_ > this.top && p_148128_2_ < this.bottom)
        {
            if (Mouse.isButtonDown(0) && this.func_148125_i())
            {
                if (this.initialClickY == -1.0F)
                {
                    boolean var16 = true;

                    if (p_148128_2_ >= this.top && p_148128_2_ <= this.bottom)
                    {
                        int var8 = this.width / 2 - this.func_148139_c() / 2;
                        var9 = this.width / 2 + this.func_148139_c() / 2;
                        var10 = p_148128_2_ - this.top - this.field_148160_j + (int)this.amountScrolled - 4;
                        int var11 = var10 / this.slotHeight;

                        if (p_148128_1_ >= var8 && p_148128_1_ <= var9 && var11 >= 0 && var10 >= 0 && var11 < var4)
                        {
                            boolean var12 = var11 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                            this.elementClicked(var11, var12, p_148128_1_, p_148128_2_);
                            this.selectedElement = var11;
                            this.lastClicked = Minecraft.getSystemTime();
                        }
                        else if (p_148128_1_ >= var8 && p_148128_1_ <= var9 && var10 < 0)
                        {
                            this.func_148132_a(p_148128_1_ - var8, p_148128_2_ - this.top + (int)this.amountScrolled - 4);
                            var16 = false;
                        }

                        if (p_148128_1_ >= var5 && p_148128_1_ <= var6)
                        {
                            this.scrollMultiplier = -1.0F;
                            var19 = this.func_148135_f();

                            if (var19 < 1)
                            {
                                var19 = 1;
                            }

                            var13 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());

                            if (var13 < 32)
                            {
                                var13 = 32;
                            }

                            if (var13 > this.bottom - this.top - 8)
                            {
                                var13 = this.bottom - this.top - 8;
                            }

                            this.scrollMultiplier /= (float)(this.bottom - this.top - var13) / (float)var19;
                        }
                        else
                        {
                            this.scrollMultiplier = 1.0F;
                        }

                        if (var16)
                        {
                            this.initialClickY = (float)p_148128_2_;
                        }
                        else
                        {
                            this.initialClickY = -2.0F;
                        }
                    }
                    else
                    {
                        this.initialClickY = -2.0F;
                    }
                }
                else if (this.initialClickY >= 0.0F)
                {
                    this.amountScrolled -= ((float)p_148128_2_ - this.initialClickY) * this.scrollMultiplier;
                    this.initialClickY = (float)p_148128_2_;
                }
            }
            else
            {
                for (; !this.mc.gameSettings.touchscreen && Mouse.next(); this.mc.currentScreen.handleMouseInput())
                {
                    int var7 = Mouse.getEventDWheel();

                    if (var7 != 0)
                    {
                        if (var7 > 0)
                        {
                            var7 = -1;
                        }
                        else if (var7 < 0)
                        {
                            var7 = 1;
                        }

                        this.amountScrolled += (float)(var7 * this.slotHeight / 2);
                    }
                }

                this.initialClickY = -1.0F;
            }
        }

        this.bindAmountScrolled();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var17 = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var15 = 32.0F;
        var17.startDrawingQuads();
        var17.setColorOpaque_I(2105376);
        var17.addVertexWithUV((double)this.left, (double)this.bottom, 0.0D, (double)((float)this.left / var15), (double)((float)(this.bottom + (int)this.amountScrolled) / var15));
        var17.addVertexWithUV((double)this.right, (double)this.bottom, 0.0D, (double)((float)this.right / var15), (double)((float)(this.bottom + (int)this.amountScrolled) / var15));
        var17.addVertexWithUV((double)this.right, (double)this.top, 0.0D, (double)((float)this.right / var15), (double)((float)(this.top + (int)this.amountScrolled) / var15));
        var17.addVertexWithUV((double)this.left, (double)this.top, 0.0D, (double)((float)this.left / var15), (double)((float)(this.top + (int)this.amountScrolled) / var15));
        var17.draw();
        var9 = this.left + this.width / 2 - this.func_148139_c() / 2 + 2;
        var10 = this.top + 4 - (int)this.amountScrolled;

        if (this.field_148165_u)
        {
            this.func_148129_a(var9, var10, var17);
        }

        this.func_148120_b(var9, var10, p_148128_1_, p_148128_2_);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte var18 = 4;
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.height, 255, 255);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        var17.startDrawingQuads();
        var17.setColorRGBA_I(0, 0);
        var17.addVertexWithUV((double)this.left, (double)(this.top + var18), 0.0D, 0.0D, 1.0D);
        var17.addVertexWithUV((double)this.right, (double)(this.top + var18), 0.0D, 1.0D, 1.0D);
        var17.setColorRGBA_I(0, 255);
        var17.addVertexWithUV((double)this.right, (double)this.top, 0.0D, 1.0D, 0.0D);
        var17.addVertexWithUV((double)this.left, (double)this.top, 0.0D, 0.0D, 0.0D);
        var17.draw();
        var17.startDrawingQuads();
        var17.setColorRGBA_I(0, 255);
        var17.addVertexWithUV((double)this.left, (double)this.bottom, 0.0D, 0.0D, 1.0D);
        var17.addVertexWithUV((double)this.right, (double)this.bottom, 0.0D, 1.0D, 1.0D);
        var17.setColorRGBA_I(0, 0);
        var17.addVertexWithUV((double)this.right, (double)(this.bottom - var18), 0.0D, 1.0D, 0.0D);
        var17.addVertexWithUV((double)this.left, (double)(this.bottom - var18), 0.0D, 0.0D, 0.0D);
        var17.draw();
        var19 = this.func_148135_f();

        if (var19 > 0)
        {
            var13 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();

            if (var13 < 32)
            {
                var13 = 32;
            }

            if (var13 > this.bottom - this.top - 8)
            {
                var13 = this.bottom - this.top - 8;
            }

            int var14 = (int)this.amountScrolled * (this.bottom - this.top - var13) / var19 + this.top;

            if (var14 < this.top)
            {
                var14 = this.top;
            }

            var17.startDrawingQuads();
            var17.setColorRGBA_I(0, 255);
            var17.addVertexWithUV((double)var5, (double)this.bottom, 0.0D, 0.0D, 1.0D);
            var17.addVertexWithUV((double)var6, (double)this.bottom, 0.0D, 1.0D, 1.0D);
            var17.addVertexWithUV((double)var6, (double)this.top, 0.0D, 1.0D, 0.0D);
            var17.addVertexWithUV((double)var5, (double)this.top, 0.0D, 0.0D, 0.0D);
            var17.draw();
            var17.startDrawingQuads();
            var17.setColorRGBA_I(8421504, 255);
            var17.addVertexWithUV((double)var5, (double)(var14 + var13), 0.0D, 0.0D, 1.0D);
            var17.addVertexWithUV((double)var6, (double)(var14 + var13), 0.0D, 1.0D, 1.0D);
            var17.addVertexWithUV((double)var6, (double)var14, 0.0D, 1.0D, 0.0D);
            var17.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var17.draw();
            var17.startDrawingQuads();
            var17.setColorRGBA_I(12632256, 255);
            var17.addVertexWithUV((double)var5, (double)(var14 + var13 - 1), 0.0D, 0.0D, 1.0D);
            var17.addVertexWithUV((double)(var6 - 1), (double)(var14 + var13 - 1), 0.0D, 1.0D, 1.0D);
            var17.addVertexWithUV((double)(var6 - 1), (double)var14, 0.0D, 1.0D, 0.0D);
            var17.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var17.draw();
        }

        this.func_148142_b(p_148128_1_, p_148128_2_);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void func_148143_b(boolean p_148143_1_)
    {
        this.field_148164_v = p_148143_1_;
    }

    public boolean func_148125_i()
    {
        return this.field_148164_v;
    }

    public int func_148139_c()
    {
        return 220;
    }

    protected void func_148120_b(int p_148120_1_, int p_148120_2_, int p_148120_3_, int p_148120_4_)
    {
        int var5 = this.getSize();
        Tessellator var6 = Tessellator.instance;

        for (int var7 = 0; var7 < var5; ++var7)
        {
            int var8 = p_148120_2_ + var7 * this.slotHeight + this.field_148160_j;
            int var9 = this.slotHeight - 4;

            if (var8 <= this.bottom && var8 + var9 >= this.top)
            {
                if (this.showSelectionBox && this.isSelected(var7))
                {
                    int var10 = this.left + (this.width / 2 - this.func_148139_c() / 2);
                    int var11 = this.left + this.width / 2 + this.func_148139_c() / 2;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var6.startDrawingQuads();
                    var6.setColorOpaque_I(8421504);
                    var6.addVertexWithUV((double)var10, (double)(var8 + var9 + 2), 0.0D, 0.0D, 1.0D);
                    var6.addVertexWithUV((double)var11, (double)(var8 + var9 + 2), 0.0D, 1.0D, 1.0D);
                    var6.addVertexWithUV((double)var11, (double)(var8 - 2), 0.0D, 1.0D, 0.0D);
                    var6.addVertexWithUV((double)var10, (double)(var8 - 2), 0.0D, 0.0D, 0.0D);
                    var6.setColorOpaque_I(0);
                    var6.addVertexWithUV((double)(var10 + 1), (double)(var8 + var9 + 1), 0.0D, 0.0D, 1.0D);
                    var6.addVertexWithUV((double)(var11 - 1), (double)(var8 + var9 + 1), 0.0D, 1.0D, 1.0D);
                    var6.addVertexWithUV((double)(var11 - 1), (double)(var8 - 1), 0.0D, 1.0D, 0.0D);
                    var6.addVertexWithUV((double)(var10 + 1), (double)(var8 - 1), 0.0D, 0.0D, 0.0D);
                    var6.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.drawSlot(var7, p_148120_1_, var8, var9, var6, p_148120_3_, p_148120_4_);
            }
        }
    }

    protected int getScrollBarX()
    {
        return this.width / 2 + 124;
    }

    private void overlayBackground(int p_148136_1_, int p_148136_2_, int p_148136_3_, int p_148136_4_)
    {
        Tessellator var5 = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var6 = 32.0F;
        var5.startDrawingQuads();
        var5.setColorRGBA_I(4210752, p_148136_4_);
        var5.addVertexWithUV((double)this.left, (double)p_148136_2_, 0.0D, 0.0D, (double)((float)p_148136_2_ / var6));
        var5.addVertexWithUV((double)(this.left + this.width), (double)p_148136_2_, 0.0D, (double)((float)this.width / var6), (double)((float)p_148136_2_ / var6));
        var5.setColorRGBA_I(4210752, p_148136_3_);
        var5.addVertexWithUV((double)(this.left + this.width), (double)p_148136_1_, 0.0D, (double)((float)this.width / var6), (double)((float)p_148136_1_ / var6));
        var5.addVertexWithUV((double)this.left, (double)p_148136_1_, 0.0D, 0.0D, (double)((float)p_148136_1_ / var6));
        var5.draw();
    }

    public void func_148140_g(int p_148140_1_)
    {
        this.left = p_148140_1_;
        this.right = p_148140_1_ + this.width;
    }

    public int func_148146_j()
    {
        return this.slotHeight;
    }
}
