package net.minecraft.block.material;

public class MaterialPortal extends Material
{
    private static final String __OBFID = "CL_00000545";

    public MaterialPortal(MapColor par1MapColor)
    {
        super(par1MapColor);
    }

    public boolean isSolid()
    {
        return false;
    }

    public boolean getCanBlockGrass()
    {
        return false;
    }

    public boolean blocksMovement()
    {
        return false;
    }
}
