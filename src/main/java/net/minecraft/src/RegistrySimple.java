package net.minecraft.src;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class RegistrySimple implements IRegistry
{
    /** Objects registered on this registry. */
    protected final Map registryObjects = this.func_111054_a();

    protected HashMap func_111054_a()
    {
        return Maps.newHashMap();
    }

    public Object getObject(Object par1Obj)
    {
        return this.registryObjects.get(par1Obj);
    }

    /**
     * Register an object on this registry.
     */
    public void putObject(Object par1Obj, Object par2Obj)
    {
        this.registryObjects.put(par1Obj, par2Obj);
    }
}
