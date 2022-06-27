package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class StructureVillageStart extends StructureStart
{
    /** well ... thats what it does */
    private boolean hasMoreThanTwoComponents;

    public StructureVillageStart() {}

    public StructureVillageStart(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        super(par3, par4);
        List var6 = StructureVillagePieces.getStructureVillageWeightedPieceList(par2Random, par5);
        ComponentVillageStartPiece var7 = new ComponentVillageStartPiece(par1World.getWorldChunkManager(), 0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2, var6, par5);
        this.components.add(var7);
        var7.buildComponent(var7, this.components, par2Random);
        List var8 = var7.field_74930_j;
        List var9 = var7.field_74932_i;
        int var10;

        while (!var8.isEmpty() || !var9.isEmpty())
        {
            StructureComponent var11;

            if (var8.isEmpty())
            {
                var10 = par2Random.nextInt(var9.size());
                var11 = (StructureComponent)var9.remove(var10);
                var11.buildComponent(var7, this.components, par2Random);
            }
            else
            {
                var10 = par2Random.nextInt(var8.size());
                var11 = (StructureComponent)var8.remove(var10);
                var11.buildComponent(var7, this.components, par2Random);
            }
        }

        this.updateBoundingBox();
        var10 = 0;
        Iterator var13 = this.components.iterator();

        while (var13.hasNext())
        {
            StructureComponent var12 = (StructureComponent)var13.next();

            if (!(var12 instanceof ComponentVillageRoadPiece))
            {
                ++var10;
            }
        }

        this.hasMoreThanTwoComponents = var10 > 2;
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return this.hasMoreThanTwoComponents;
    }

    public void func_143022_a(NBTTagCompound par1NBTTagCompound)
    {
        super.func_143022_a(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
    }

    public void func_143017_b(NBTTagCompound var1)
    {
        super.func_143017_b(var1);
        this.hasMoreThanTwoComponents = var1.getBoolean("Valid");
    }
}
