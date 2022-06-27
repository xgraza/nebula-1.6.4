package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEntityType implements Callable
{
    final Entity theEntity;

    CallableEntityType(Entity par1Entity)
    {
        this.theEntity = par1Entity;
    }

    public String callEntityType()
    {
        return EntityList.getEntityString(this.theEntity) + " (" + this.theEntity.getClass().getCanonicalName() + ")";
    }

    public Object call()
    {
        return this.callEntityType();
    }
}
