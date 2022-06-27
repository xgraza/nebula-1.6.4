package net.minecraft.src;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScreenSelectLocation
{
    private final Minecraft field_104092_f;
    private int field_104093_g;
    private int field_104105_h;
    protected int field_104098_a;
    protected int field_104096_b;
    private int field_104106_i;
    private int field_104103_j;
    protected final int field_104097_c;
    private int field_104104_k;
    private int field_104101_l;
    protected int field_104094_d;
    protected int field_104095_e;
    private float field_104102_m = -2.0F;
    private float field_104099_n;
    private float field_104100_o;
    private int field_104111_p = -1;
    private long field_104110_q;
    private boolean field_104109_r = true;
    private boolean field_104108_s;
    private int field_104107_t;

    public GuiScreenSelectLocation(Minecraft par1Minecraft, int par2, int par3, int par4, int par5, int par6)
    {
        this.field_104092_f = par1Minecraft;
        this.field_104093_g = par2;
        this.field_104105_h = par3;
        this.field_104098_a = par4;
        this.field_104096_b = par5;
        this.field_104097_c = par6;
        this.field_104103_j = 0;
        this.field_104106_i = par2;
    }

    public void func_104084_a(int par1, int par2, int par3, int par4)
    {
        this.field_104093_g = par1;
        this.field_104105_h = par2;
        this.field_104098_a = par3;
        this.field_104096_b = par4;
        this.field_104103_j = 0;
        this.field_104106_i = par1;
    }

    /**
     * Gets the size of the current slot list.
     */
    protected abstract int getSize();

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected abstract void elementClicked(int var1, boolean var2);

    /**
     * returns true if the element passed in is currently selected
     */
    protected abstract boolean isSelected(int var1);

    protected abstract boolean func_104086_b(int var1);

    protected int func_130003_b()
    {
        return this.getSize() * this.field_104097_c + this.field_104107_t;
    }

    protected abstract void func_130004_c();

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5);

    protected void func_104088_a(int par1, int par2, Tessellator par3Tessellator) {}

    protected void func_104089_a(int par1, int par2) {}

    protected void func_104087_b(int par1, int par2) {}

    private void func_104091_h()
    {
        int var1 = this.func_104085_d();

        if (var1 < 0)
        {
            var1 /= 2;
        }

        if (this.field_104100_o < 0.0F)
        {
            this.field_104100_o = 0.0F;
        }

        if (this.field_104100_o > (float)var1)
        {
            this.field_104100_o = (float)var1;
        }
    }

    public int func_104085_d()
    {
        return this.func_130003_b() - (this.field_104096_b - this.field_104098_a - 4);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    public void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == this.field_104104_k)
            {
                this.field_104100_o -= (float)(this.field_104097_c * 2 / 3);
                this.field_104102_m = -2.0F;
                this.func_104091_h();
            }
            else if (par1GuiButton.id == this.field_104101_l)
            {
                this.field_104100_o += (float)(this.field_104097_c * 2 / 3);
                this.field_104102_m = -2.0F;
                this.func_104091_h();
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_104094_d = par1;
        this.field_104095_e = par2;
        this.func_130004_c();
        int var4 = this.getSize();
        int var5 = this.func_104090_g();
        int var6 = var5 + 6;
        int var9;
        int var10;
        int var11;
        int var13;
        int var20;

        if (Mouse.isButtonDown(0))
        {
            if (this.field_104102_m == -1.0F)
            {
                boolean var7 = true;

                if (par2 >= this.field_104098_a && par2 <= this.field_104096_b)
                {
                    int var8 = this.field_104093_g / 2 - 110;
                    var9 = this.field_104093_g / 2 + 110;
                    var10 = par2 - this.field_104098_a - this.field_104107_t + (int)this.field_104100_o - 4;
                    var11 = var10 / this.field_104097_c;

                    if (par1 >= var8 && par1 <= var9 && var11 >= 0 && var10 >= 0 && var11 < var4)
                    {
                        boolean var12 = var11 == this.field_104111_p && Minecraft.getSystemTime() - this.field_104110_q < 250L;
                        this.elementClicked(var11, var12);
                        this.field_104111_p = var11;
                        this.field_104110_q = Minecraft.getSystemTime();
                    }
                    else if (par1 >= var8 && par1 <= var9 && var10 < 0)
                    {
                        this.func_104089_a(par1 - var8, par2 - this.field_104098_a + (int)this.field_104100_o - 4);
                        var7 = false;
                    }

                    if (par1 >= var5 && par1 <= var6)
                    {
                        this.field_104099_n = -1.0F;
                        var20 = this.func_104085_d();

                        if (var20 < 1)
                        {
                            var20 = 1;
                        }

                        var13 = (int)((float)((this.field_104096_b - this.field_104098_a) * (this.field_104096_b - this.field_104098_a)) / (float)this.func_130003_b());

                        if (var13 < 32)
                        {
                            var13 = 32;
                        }

                        if (var13 > this.field_104096_b - this.field_104098_a - 8)
                        {
                            var13 = this.field_104096_b - this.field_104098_a - 8;
                        }

                        this.field_104099_n /= (float)(this.field_104096_b - this.field_104098_a - var13) / (float)var20;
                    }
                    else
                    {
                        this.field_104099_n = 1.0F;
                    }

                    if (var7)
                    {
                        this.field_104102_m = (float)par2;
                    }
                    else
                    {
                        this.field_104102_m = -2.0F;
                    }
                }
                else
                {
                    this.field_104102_m = -2.0F;
                }
            }
            else if (this.field_104102_m >= 0.0F)
            {
                this.field_104100_o -= ((float)par2 - this.field_104102_m) * this.field_104099_n;
                this.field_104102_m = (float)par2;
            }
        }
        else
        {
            while (!this.field_104092_f.gameSettings.touchscreen && Mouse.next())
            {
                int var16 = Mouse.getEventDWheel();

                if (var16 != 0)
                {
                    if (var16 > 0)
                    {
                        var16 = -1;
                    }
                    else if (var16 < 0)
                    {
                        var16 = 1;
                    }

                    this.field_104100_o += (float)(var16 * this.field_104097_c / 2);
                }
            }

            this.field_104102_m = -1.0F;
        }

        this.func_104091_h();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var18 = Tessellator.instance;
        this.field_104092_f.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var17 = 32.0F;
        var18.startDrawingQuads();
        var18.setColorOpaque_I(2105376);
        var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104096_b, 0.0D, (double)((float)this.field_104103_j / var17), (double)((float)(this.field_104096_b + (int)this.field_104100_o) / var17));
        var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104096_b, 0.0D, (double)((float)this.field_104106_i / var17), (double)((float)(this.field_104096_b + (int)this.field_104100_o) / var17));
        var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104098_a, 0.0D, (double)((float)this.field_104106_i / var17), (double)((float)(this.field_104098_a + (int)this.field_104100_o) / var17));
        var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104098_a, 0.0D, (double)((float)this.field_104103_j / var17), (double)((float)(this.field_104098_a + (int)this.field_104100_o) / var17));
        var18.draw();
        var9 = this.field_104093_g / 2 - 92 - 16;
        var10 = this.field_104098_a + 4 - (int)this.field_104100_o;

        if (this.field_104108_s)
        {
            this.func_104088_a(var9, var10, var18);
        }

        int var14;

        for (var11 = 0; var11 < var4; ++var11)
        {
            var20 = var10 + var11 * this.field_104097_c + this.field_104107_t;
            var13 = this.field_104097_c - 4;

            if (var20 <= this.field_104096_b && var20 + var13 >= this.field_104098_a)
            {
                int var15;

                if (this.field_104109_r && this.func_104086_b(var11))
                {
                    var14 = this.field_104093_g / 2 - 110;
                    var15 = this.field_104093_g / 2 + 110;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var18.startDrawingQuads();
                    var18.setColorOpaque_I(0);
                    var18.addVertexWithUV((double)var14, (double)(var20 + var13 + 2), 0.0D, 0.0D, 1.0D);
                    var18.addVertexWithUV((double)var15, (double)(var20 + var13 + 2), 0.0D, 1.0D, 1.0D);
                    var18.addVertexWithUV((double)var15, (double)(var20 - 2), 0.0D, 1.0D, 0.0D);
                    var18.addVertexWithUV((double)var14, (double)(var20 - 2), 0.0D, 0.0D, 0.0D);
                    var18.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                if (this.field_104109_r && this.isSelected(var11))
                {
                    var14 = this.field_104093_g / 2 - 110;
                    var15 = this.field_104093_g / 2 + 110;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var18.startDrawingQuads();
                    var18.setColorOpaque_I(8421504);
                    var18.addVertexWithUV((double)var14, (double)(var20 + var13 + 2), 0.0D, 0.0D, 1.0D);
                    var18.addVertexWithUV((double)var15, (double)(var20 + var13 + 2), 0.0D, 1.0D, 1.0D);
                    var18.addVertexWithUV((double)var15, (double)(var20 - 2), 0.0D, 1.0D, 0.0D);
                    var18.addVertexWithUV((double)var14, (double)(var20 - 2), 0.0D, 0.0D, 0.0D);
                    var18.setColorOpaque_I(0);
                    var18.addVertexWithUV((double)(var14 + 1), (double)(var20 + var13 + 1), 0.0D, 0.0D, 1.0D);
                    var18.addVertexWithUV((double)(var15 - 1), (double)(var20 + var13 + 1), 0.0D, 1.0D, 1.0D);
                    var18.addVertexWithUV((double)(var15 - 1), (double)(var20 - 1), 0.0D, 1.0D, 0.0D);
                    var18.addVertexWithUV((double)(var14 + 1), (double)(var20 - 1), 0.0D, 0.0D, 0.0D);
                    var18.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.drawSlot(var11, var9, var20, var13, var18);
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte var19 = 4;
        this.func_104083_b(0, this.field_104098_a, 255, 255);
        this.func_104083_b(this.field_104096_b, this.field_104105_h, 255, 255);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        var18.startDrawingQuads();
        var18.setColorRGBA_I(0, 0);
        var18.addVertexWithUV((double)this.field_104103_j, (double)(this.field_104098_a + var19), 0.0D, 0.0D, 1.0D);
        var18.addVertexWithUV((double)this.field_104106_i, (double)(this.field_104098_a + var19), 0.0D, 1.0D, 1.0D);
        var18.setColorRGBA_I(0, 255);
        var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104098_a, 0.0D, 1.0D, 0.0D);
        var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104098_a, 0.0D, 0.0D, 0.0D);
        var18.draw();
        var18.startDrawingQuads();
        var18.setColorRGBA_I(0, 255);
        var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104096_b, 0.0D, 0.0D, 1.0D);
        var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104096_b, 0.0D, 1.0D, 1.0D);
        var18.setColorRGBA_I(0, 0);
        var18.addVertexWithUV((double)this.field_104106_i, (double)(this.field_104096_b - var19), 0.0D, 1.0D, 0.0D);
        var18.addVertexWithUV((double)this.field_104103_j, (double)(this.field_104096_b - var19), 0.0D, 0.0D, 0.0D);
        var18.draw();
        var20 = this.func_104085_d();

        if (var20 > 0)
        {
            var13 = (this.field_104096_b - this.field_104098_a) * (this.field_104096_b - this.field_104098_a) / this.func_130003_b();

            if (var13 < 32)
            {
                var13 = 32;
            }

            if (var13 > this.field_104096_b - this.field_104098_a - 8)
            {
                var13 = this.field_104096_b - this.field_104098_a - 8;
            }

            var14 = (int)this.field_104100_o * (this.field_104096_b - this.field_104098_a - var13) / var20 + this.field_104098_a;

            if (var14 < this.field_104098_a)
            {
                var14 = this.field_104098_a;
            }

            var18.startDrawingQuads();
            var18.setColorRGBA_I(0, 255);
            var18.addVertexWithUV((double)var5, (double)this.field_104096_b, 0.0D, 0.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)this.field_104096_b, 0.0D, 1.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)this.field_104098_a, 0.0D, 1.0D, 0.0D);
            var18.addVertexWithUV((double)var5, (double)this.field_104098_a, 0.0D, 0.0D, 0.0D);
            var18.draw();
            var18.startDrawingQuads();
            var18.setColorRGBA_I(8421504, 255);
            var18.addVertexWithUV((double)var5, (double)(var14 + var13), 0.0D, 0.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)(var14 + var13), 0.0D, 1.0D, 1.0D);
            var18.addVertexWithUV((double)var6, (double)var14, 0.0D, 1.0D, 0.0D);
            var18.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var18.draw();
            var18.startDrawingQuads();
            var18.setColorRGBA_I(12632256, 255);
            var18.addVertexWithUV((double)var5, (double)(var14 + var13 - 1), 0.0D, 0.0D, 1.0D);
            var18.addVertexWithUV((double)(var6 - 1), (double)(var14 + var13 - 1), 0.0D, 1.0D, 1.0D);
            var18.addVertexWithUV((double)(var6 - 1), (double)var14, 0.0D, 1.0D, 0.0D);
            var18.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var18.draw();
        }

        this.func_104087_b(par1, par2);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected int func_104090_g()
    {
        return this.field_104093_g / 2 + 124;
    }

    private void func_104083_b(int par1, int par2, int par3, int par4)
    {
        Tessellator var5 = Tessellator.instance;
        this.field_104092_f.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var6 = 32.0F;
        var5.startDrawingQuads();
        var5.setColorRGBA_I(4210752, par4);
        var5.addVertexWithUV(0.0D, (double)par2, 0.0D, 0.0D, (double)((float)par2 / var6));
        var5.addVertexWithUV((double)this.field_104093_g, (double)par2, 0.0D, (double)((float)this.field_104093_g / var6), (double)((float)par2 / var6));
        var5.setColorRGBA_I(4210752, par3);
        var5.addVertexWithUV((double)this.field_104093_g, (double)par1, 0.0D, (double)((float)this.field_104093_g / var6), (double)((float)par1 / var6));
        var5.addVertexWithUV(0.0D, (double)par1, 0.0D, 0.0D, (double)((float)par1 / var6));
        var5.draw();
    }
}
