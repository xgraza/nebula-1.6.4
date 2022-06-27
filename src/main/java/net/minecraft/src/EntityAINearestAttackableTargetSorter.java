package net.minecraft.src;

import java.util.Comparator;

public class EntityAINearestAttackableTargetSorter implements Comparator
{
    private final Entity theEntity;

    public EntityAINearestAttackableTargetSorter(Entity par1Entity)
    {
        this.theEntity = par1Entity;
    }

    public int compareDistanceSq(Entity par1Entity, Entity par2Entity)
    {
        double var3 = this.theEntity.getDistanceSqToEntity(par1Entity);
        double var5 = this.theEntity.getDistanceSqToEntity(par2Entity);
        return var3 < var5 ? -1 : (var3 > var5 ? 1 : 0);
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.compareDistanceSq((Entity)par1Obj, (Entity)par2Obj);
    }
}
