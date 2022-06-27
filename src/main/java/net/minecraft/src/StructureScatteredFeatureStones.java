package net.minecraft.src;

import java.util.Random;

class StructureScatteredFeatureStones extends StructurePieceBlockSelector
{
    private StructureScatteredFeatureStones() {}

    /**
     * picks Block Ids and Metadata (Silverfish)
     */
    public void selectBlocks(Random par1Random, int par2, int par3, int par4, boolean par5)
    {
        if (par1Random.nextFloat() < 0.4F)
        {
            this.selectedBlockId = Block.cobblestone.blockID;
        }
        else
        {
            this.selectedBlockId = Block.cobblestoneMossy.blockID;
        }
    }

    StructureScatteredFeatureStones(ComponentScatteredFeaturePieces2 par1ComponentScatteredFeaturePieces2)
    {
        this();
    }
}
