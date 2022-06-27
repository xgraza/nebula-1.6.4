package net.minecraft.src;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.lwjgl.input.Mouse;

abstract class GuiSlotStats extends GuiSlot
{
    protected int field_77262_g;
    protected List field_77266_h;
    protected Comparator field_77267_i;
    protected int field_77264_j;
    protected int field_77265_k;

    final GuiStats statsGui;

    protected GuiSlotStats(GuiStats par1GuiStats)
    {
        super(GuiStats.getMinecraft1(par1GuiStats), par1GuiStats.width, par1GuiStats.height, 32, par1GuiStats.height - 64, 20);
        this.statsGui = par1GuiStats;
        this.field_77262_g = -1;
        this.field_77264_j = -1;
        this.setShowSelectionBox(false);
        this.func_77223_a(true, 20);
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(int par1, boolean par2) {}

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int par1)
    {
        return false;
    }

    protected void drawBackground()
    {
        this.statsGui.drawDefaultBackground();
    }

    protected void func_77222_a(int par1, int par2, Tessellator par3Tessellator)
    {
        if (!Mouse.isButtonDown(0))
        {
            this.field_77262_g = -1;
        }

        if (this.field_77262_g == 0)
        {
            GuiStats.drawSprite(this.statsGui, par1 + 115 - 18, par2 + 1, 0, 0);
        }
        else
        {
            GuiStats.drawSprite(this.statsGui, par1 + 115 - 18, par2 + 1, 0, 18);
        }

        if (this.field_77262_g == 1)
        {
            GuiStats.drawSprite(this.statsGui, par1 + 165 - 18, par2 + 1, 0, 0);
        }
        else
        {
            GuiStats.drawSprite(this.statsGui, par1 + 165 - 18, par2 + 1, 0, 18);
        }

        if (this.field_77262_g == 2)
        {
            GuiStats.drawSprite(this.statsGui, par1 + 215 - 18, par2 + 1, 0, 0);
        }
        else
        {
            GuiStats.drawSprite(this.statsGui, par1 + 215 - 18, par2 + 1, 0, 18);
        }

        if (this.field_77264_j != -1)
        {
            short var4 = 79;
            byte var5 = 18;

            if (this.field_77264_j == 1)
            {
                var4 = 129;
            }
            else if (this.field_77264_j == 2)
            {
                var4 = 179;
            }

            if (this.field_77265_k == 1)
            {
                var5 = 36;
            }

            GuiStats.drawSprite(this.statsGui, par1 + var4, par2 + 1, var5, 0);
        }
    }

    protected void func_77224_a(int par1, int par2)
    {
        this.field_77262_g = -1;

        if (par1 >= 79 && par1 < 115)
        {
            this.field_77262_g = 0;
        }
        else if (par1 >= 129 && par1 < 165)
        {
            this.field_77262_g = 1;
        }
        else if (par1 >= 179 && par1 < 215)
        {
            this.field_77262_g = 2;
        }

        if (this.field_77262_g >= 0)
        {
            this.func_77261_e(this.field_77262_g);
            GuiStats.getMinecraft2(this.statsGui).sndManager.playSoundFX("random.click", 1.0F, 1.0F);
        }
    }

    /**
     * Gets the size of the current slot list.
     */
    protected final int getSize()
    {
        return this.field_77266_h.size();
    }

    protected final StatCrafting func_77257_d(int par1)
    {
        return (StatCrafting)this.field_77266_h.get(par1);
    }

    protected abstract String func_77258_c(int var1);

    protected void func_77260_a(StatCrafting par1StatCrafting, int par2, int par3, boolean par4)
    {
        String var5;

        if (par1StatCrafting != null)
        {
            var5 = par1StatCrafting.func_75968_a(GuiStats.getStatsFileWriter(this.statsGui).writeStat(par1StatCrafting));
            this.statsGui.drawString(GuiStats.getFontRenderer4(this.statsGui), var5, par2 - GuiStats.getFontRenderer5(this.statsGui).getStringWidth(var5), par3 + 5, par4 ? 16777215 : 9474192);
        }
        else
        {
            var5 = "-";
            this.statsGui.drawString(GuiStats.getFontRenderer6(this.statsGui), var5, par2 - GuiStats.getFontRenderer7(this.statsGui).getStringWidth(var5), par3 + 5, par4 ? 16777215 : 9474192);
        }
    }

    protected void func_77215_b(int par1, int par2)
    {
        if (par2 >= this.top && par2 <= this.bottom)
        {
            int var3 = this.func_77210_c(par1, par2);
            int var4 = this.statsGui.width / 2 - 92 - 16;

            if (var3 >= 0)
            {
                if (par1 < var4 + 40 || par1 > var4 + 40 + 20)
                {
                    return;
                }

                StatCrafting var5 = this.func_77257_d(var3);
                this.func_77259_a(var5, par1, par2);
            }
            else
            {
                String var9 = "";

                if (par1 >= var4 + 115 - 18 && par1 <= var4 + 115)
                {
                    var9 = this.func_77258_c(0);
                }
                else if (par1 >= var4 + 165 - 18 && par1 <= var4 + 165)
                {
                    var9 = this.func_77258_c(1);
                }
                else
                {
                    if (par1 < var4 + 215 - 18 || par1 > var4 + 215)
                    {
                        return;
                    }

                    var9 = this.func_77258_c(2);
                }

                var9 = ("" + I18n.getString(var9)).trim();

                if (var9.length() > 0)
                {
                    int var6 = par1 + 12;
                    int var7 = par2 - 12;
                    int var8 = GuiStats.getFontRenderer8(this.statsGui).getStringWidth(var9);
                    GuiStats.drawGradientRect(this.statsGui, var6 - 3, var7 - 3, var6 + var8 + 3, var7 + 8 + 3, -1073741824, -1073741824);
                    GuiStats.getFontRenderer9(this.statsGui).drawStringWithShadow(var9, var6, var7, -1);
                }
            }
        }
    }

    protected void func_77259_a(StatCrafting par1StatCrafting, int par2, int par3)
    {
        if (par1StatCrafting != null)
        {
            Item var4 = Item.itemsList[par1StatCrafting.getItemID()];
            String var5 = ("" + I18n.getString(var4.getUnlocalizedName() + ".name")).trim();

            if (var5.length() > 0)
            {
                int var6 = par2 + 12;
                int var7 = par3 - 12;
                int var8 = GuiStats.getFontRenderer10(this.statsGui).getStringWidth(var5);
                GuiStats.drawGradientRect1(this.statsGui, var6 - 3, var7 - 3, var6 + var8 + 3, var7 + 8 + 3, -1073741824, -1073741824);
                GuiStats.getFontRenderer11(this.statsGui).drawStringWithShadow(var5, var6, var7, -1);
            }
        }
    }

    protected void func_77261_e(int par1)
    {
        if (par1 != this.field_77264_j)
        {
            this.field_77264_j = par1;
            this.field_77265_k = -1;
        }
        else if (this.field_77265_k == -1)
        {
            this.field_77265_k = 1;
        }
        else
        {
            this.field_77264_j = -1;
            this.field_77265_k = 0;
        }

        Collections.sort(this.field_77266_h, this.field_77267_i);
    }
}
