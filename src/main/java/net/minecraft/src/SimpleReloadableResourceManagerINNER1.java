package net.minecraft.src;

import com.google.common.base.Function;

class SimpleReloadableResourceManagerINNER1 implements Function
{
    final SimpleReloadableResourceManager theSimpleReloadableResourceManager;

    SimpleReloadableResourceManagerINNER1(SimpleReloadableResourceManager par1SimpleReloadableResourceManager)
    {
        this.theSimpleReloadableResourceManager = par1SimpleReloadableResourceManager;
    }

    public String apply(ResourcePack par1ResourcePack)
    {
        return par1ResourcePack.getPackName();
    }

    public Object apply(Object par1Obj)
    {
        return this.apply((ResourcePack)par1Obj);
    }
}
