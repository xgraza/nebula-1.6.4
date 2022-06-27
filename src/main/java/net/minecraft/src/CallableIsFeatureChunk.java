package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableIsFeatureChunk implements Callable
{
    final int field_85169_a;

    final int field_85167_b;

    final MapGenStructure theMapStructureGenerator;

    CallableIsFeatureChunk(MapGenStructure par1MapGenStructure, int par2, int par3)
    {
        this.theMapStructureGenerator = par1MapGenStructure;
        this.field_85169_a = par2;
        this.field_85167_b = par3;
    }

    public String func_85166_a()
    {
        return this.theMapStructureGenerator.canSpawnStructureAtCoords(this.field_85169_a, this.field_85167_b) ? "True" : "False";
    }

    public Object call()
    {
        return this.func_85166_a();
    }
}
