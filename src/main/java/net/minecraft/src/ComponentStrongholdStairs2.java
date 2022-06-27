package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComponentStrongholdStairs2 extends ComponentStrongholdStairs
{
    public StructureStrongholdPieceWeight strongholdPieceWeight;
    public ComponentStrongholdPortalRoom strongholdPortalRoom;
    public List field_75026_c = new ArrayList();

    public ComponentStrongholdStairs2() {}

    public ComponentStrongholdStairs2(int par1, Random par2Random, int par3, int par4)
    {
        super(0, par2Random, par3, par4);
    }

    public ChunkPosition getCenter()
    {
        return this.strongholdPortalRoom != null ? this.strongholdPortalRoom.getCenter() : super.getCenter();
    }
}
