package net.minecraft.block.material;

public class MaterialLiquid extends Material
{
    private static final String __OBFID = "CL_00000541";

    public MaterialLiquid(MapColor par1MapColor)
    {
        super(par1MapColor);
        this.setReplaceable();
        this.setNoPushMobility();
    }

    public boolean isLiquid()
    {
        return true;
    }

    public boolean blocksMovement()
    {
        return false;
    }

    public boolean isSolid()
    {
        return false;
    }
}
