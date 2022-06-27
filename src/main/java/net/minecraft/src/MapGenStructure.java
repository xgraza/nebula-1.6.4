package net.minecraft.src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class MapGenStructure extends MapGenBase
{
    private MapGenStructureData field_143029_e;

    /**
     * Used to store a list of all structures that have been recursively generated. Used so that during recursive
     * generation, the structure generator can avoid generating structures that intersect ones that have already been
     * placed.
     */
    protected Map structureMap = new HashMap();

    public abstract String func_143025_a();

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected final void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte)
    {
        this.func_143027_a(par1World);

        if (!this.structureMap.containsKey(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par2, par3))))
        {
            this.rand.nextInt();

            try
            {
                if (this.canSpawnStructureAtCoords(par2, par3))
                {
                    StructureStart var7 = this.getStructureStart(par2, par3);
                    this.structureMap.put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par2, par3)), var7);
                    this.func_143026_a(par2, par3, var7);
                }
            }
            catch (Throwable var10)
            {
                CrashReport var8 = CrashReport.makeCrashReport(var10, "Exception preparing structure feature");
                CrashReportCategory var9 = var8.makeCategory("Feature being prepared");
                var9.addCrashSectionCallable("Is feature chunk", new CallableIsFeatureChunk(this, par2, par3));
                var9.addCrashSection("Chunk location", String.format("%d,%d", new Object[] {Integer.valueOf(par2), Integer.valueOf(par3)}));
                var9.addCrashSectionCallable("Chunk pos hash", new CallableChunkPosHash(this, par2, par3));
                var9.addCrashSectionCallable("Structure type", new CallableStructureType(this));
                throw new ReportedException(var8);
            }
        }
    }

    /**
     * Generates structures in specified chunk next to existing structures. Does *not* generate StructureStarts.
     */
    public boolean generateStructuresInChunk(World par1World, Random par2Random, int par3, int par4)
    {
        this.func_143027_a(par1World);
        int var5 = (par3 << 4) + 8;
        int var6 = (par4 << 4) + 8;
        boolean var7 = false;
        Iterator var8 = this.structureMap.values().iterator();

        while (var8.hasNext())
        {
            StructureStart var9 = (StructureStart)var8.next();

            if (var9.isSizeableStructure() && var9.getBoundingBox().intersectsWith(var5, var6, var5 + 15, var6 + 15))
            {
                var9.generateStructure(par1World, par2Random, new StructureBoundingBox(var5, var6, var5 + 15, var6 + 15));
                var7 = true;
                this.func_143026_a(var9.func_143019_e(), var9.func_143018_f(), var9);
            }
        }

        return var7;
    }

    /**
     * Returns true if the structure generator has generated a structure located at the given position tuple.
     */
    public boolean hasStructureAt(int par1, int par2, int par3)
    {
        this.func_143027_a(this.worldObj);
        return this.func_143028_c(par1, par2, par3) != null;
    }

    protected StructureStart func_143028_c(int par1, int par2, int par3)
    {
        Iterator var4 = this.structureMap.values().iterator();

        while (var4.hasNext())
        {
            StructureStart var5 = (StructureStart)var4.next();

            if (var5.isSizeableStructure() && var5.getBoundingBox().intersectsWith(par1, par3, par1, par3))
            {
                Iterator var6 = var5.getComponents().iterator();

                while (var6.hasNext())
                {
                    StructureComponent var7 = (StructureComponent)var6.next();

                    if (var7.getBoundingBox().isVecInside(par1, par2, par3))
                    {
                        return var5;
                    }
                }
            }
        }

        return null;
    }

    public boolean func_142038_b(int par1, int par2, int par3)
    {
        this.func_143027_a(this.worldObj);
        Iterator var4 = this.structureMap.values().iterator();
        StructureStart var5;

        do
        {
            if (!var4.hasNext())
            {
                return false;
            }

            var5 = (StructureStart)var4.next();
        }
        while (!var5.isSizeableStructure());

        return var5.getBoundingBox().intersectsWith(par1, par3, par1, par3);
    }

    public ChunkPosition getNearestInstance(World par1World, int par2, int par3, int par4)
    {
        this.worldObj = par1World;
        this.func_143027_a(par1World);
        this.rand.setSeed(par1World.getSeed());
        long var5 = this.rand.nextLong();
        long var7 = this.rand.nextLong();
        long var9 = (long)(par2 >> 4) * var5;
        long var11 = (long)(par4 >> 4) * var7;
        this.rand.setSeed(var9 ^ var11 ^ par1World.getSeed());
        this.recursiveGenerate(par1World, par2 >> 4, par4 >> 4, 0, 0, (byte[])null);
        double var13 = Double.MAX_VALUE;
        ChunkPosition var15 = null;
        Iterator var16 = this.structureMap.values().iterator();
        ChunkPosition var19;
        int var20;
        int var21;
        int var22;
        double var23;

        while (var16.hasNext())
        {
            StructureStart var17 = (StructureStart)var16.next();

            if (var17.isSizeableStructure())
            {
                StructureComponent var18 = (StructureComponent)var17.getComponents().get(0);
                var19 = var18.getCenter();
                var20 = var19.x - par2;
                var21 = var19.y - par3;
                var22 = var19.z - par4;
                var23 = (double)(var20 * var20 + var21 * var21 + var22 * var22);

                if (var23 < var13)
                {
                    var13 = var23;
                    var15 = var19;
                }
            }
        }

        if (var15 != null)
        {
            return var15;
        }
        else
        {
            List var25 = this.getCoordList();

            if (var25 != null)
            {
                ChunkPosition var26 = null;
                Iterator var27 = var25.iterator();

                while (var27.hasNext())
                {
                    var19 = (ChunkPosition)var27.next();
                    var20 = var19.x - par2;
                    var21 = var19.y - par3;
                    var22 = var19.z - par4;
                    var23 = (double)(var20 * var20 + var21 * var21 + var22 * var22);

                    if (var23 < var13)
                    {
                        var13 = var23;
                        var26 = var19;
                    }
                }

                return var26;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Returns a list of other locations at which the structure generation has been run, or null if not relevant to this
     * structure generator.
     */
    protected List getCoordList()
    {
        return null;
    }

    private void func_143027_a(World par1World)
    {
        if (this.field_143029_e == null)
        {
            this.field_143029_e = (MapGenStructureData)par1World.loadItemData(MapGenStructureData.class, this.func_143025_a());

            if (this.field_143029_e == null)
            {
                this.field_143029_e = new MapGenStructureData(this.func_143025_a());
                par1World.setItemData(this.func_143025_a(), this.field_143029_e);
            }
            else
            {
                NBTTagCompound var2 = this.field_143029_e.func_143041_a();
                Iterator var3 = var2.getTags().iterator();

                while (var3.hasNext())
                {
                    NBTBase var4 = (NBTBase)var3.next();

                    if (var4.getId() == 10)
                    {
                        NBTTagCompound var5 = (NBTTagCompound)var4;

                        if (var5.hasKey("ChunkX") && var5.hasKey("ChunkZ"))
                        {
                            int var6 = var5.getInteger("ChunkX");
                            int var7 = var5.getInteger("ChunkZ");
                            StructureStart var8 = MapGenStructureIO.func_143035_a(var5, par1World);
                            this.structureMap.put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(var6, var7)), var8);
                        }
                    }
                }
            }
        }
    }

    private void func_143026_a(int par1, int par2, StructureStart par3StructureStart)
    {
        this.field_143029_e.func_143043_a(par3StructureStart.func_143021_a(par1, par2), par1, par2);
        this.field_143029_e.markDirty();
    }

    protected abstract boolean canSpawnStructureAtCoords(int var1, int var2);

    protected abstract StructureStart getStructureStart(int var1, int var2);
}
