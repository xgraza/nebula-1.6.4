package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class StructureStrongholdStart extends StructureStart
{
    public StructureStrongholdStart() {}

    public StructureStrongholdStart(World par1World, Random par2Random, int par3, int par4)
    {
        super(par3, par4);
        StructureStrongholdPieces.prepareStructurePieces();
        ComponentStrongholdStairs2 var5 = new ComponentStrongholdStairs2(0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
        this.components.add(var5);
        var5.buildComponent(var5, this.components, par2Random);
        List var6 = var5.field_75026_c;

        while (!var6.isEmpty())
        {
            int var7 = par2Random.nextInt(var6.size());
            StructureComponent var8 = (StructureComponent)var6.remove(var7);
            var8.buildComponent(var5, this.components, par2Random);
        }

        this.updateBoundingBox();
        this.markAvailableHeight(par1World, par2Random, 10);
    }
}
