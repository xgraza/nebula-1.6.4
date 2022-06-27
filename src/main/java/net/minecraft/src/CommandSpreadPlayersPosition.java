package net.minecraft.src;

import java.util.Random;

class CommandSpreadPlayersPosition
{
    double field_111101_a;
    double field_111100_b;

    CommandSpreadPlayersPosition() {}

    CommandSpreadPlayersPosition(double par1, double par3)
    {
        this.field_111101_a = par1;
        this.field_111100_b = par3;
    }

    double func_111099_a(CommandSpreadPlayersPosition par1CommandSpreadPlayersPosition)
    {
        double var2 = this.field_111101_a - par1CommandSpreadPlayersPosition.field_111101_a;
        double var4 = this.field_111100_b - par1CommandSpreadPlayersPosition.field_111100_b;
        return Math.sqrt(var2 * var2 + var4 * var4);
    }

    void func_111095_a()
    {
        double var1 = (double)this.func_111096_b();
        this.field_111101_a /= var1;
        this.field_111100_b /= var1;
    }

    float func_111096_b()
    {
        return MathHelper.sqrt_double(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
    }

    public void func_111094_b(CommandSpreadPlayersPosition par1CommandSpreadPlayersPosition)
    {
        this.field_111101_a -= par1CommandSpreadPlayersPosition.field_111101_a;
        this.field_111100_b -= par1CommandSpreadPlayersPosition.field_111100_b;
    }

    public boolean func_111093_a(double par1, double par3, double par5, double par7)
    {
        boolean var9 = false;

        if (this.field_111101_a < par1)
        {
            this.field_111101_a = par1;
            var9 = true;
        }
        else if (this.field_111101_a > par5)
        {
            this.field_111101_a = par5;
            var9 = true;
        }

        if (this.field_111100_b < par3)
        {
            this.field_111100_b = par3;
            var9 = true;
        }
        else if (this.field_111100_b > par7)
        {
            this.field_111100_b = par7;
            var9 = true;
        }

        return var9;
    }

    public int func_111092_a(World par1World)
    {
        int var2 = MathHelper.floor_double(this.field_111101_a);
        int var3 = MathHelper.floor_double(this.field_111100_b);

        for (int var4 = 256; var4 > 0; --var4)
        {
            int var5 = par1World.getBlockId(var2, var4, var3);

            if (var5 != 0)
            {
                return var4 + 1;
            }
        }

        return 257;
    }

    public boolean func_111098_b(World par1World)
    {
        int var2 = MathHelper.floor_double(this.field_111101_a);
        int var3 = MathHelper.floor_double(this.field_111100_b);

        for (int var4 = 256; var4 > 0; --var4)
        {
            int var5 = par1World.getBlockId(var2, var4, var3);

            if (var5 != 0)
            {
                Material var6 = Block.blocksList[var5].blockMaterial;
                return !var6.isLiquid() && var6 != Material.fire;
            }
        }

        return false;
    }

    public void func_111097_a(Random par1Random, double par2, double par4, double par6, double par8)
    {
        this.field_111101_a = MathHelper.getRandomDoubleInRange(par1Random, par2, par6);
        this.field_111100_b = MathHelper.getRandomDoubleInRange(par1Random, par4, par8);
    }
}
