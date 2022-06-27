package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableEntityName implements Callable
{
    final Entity theEntity;

    CallableEntityName(Entity par1Entity)
    {
        this.theEntity = par1Entity;
    }

    public String callEntityName()
    {
        return this.theEntity.getEntityName();
    }

    public Object call()
    {
        return this.callEntityName();
    }
}
