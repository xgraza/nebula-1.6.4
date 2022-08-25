package net.minecraft.util;

public class RegistryDefaulted extends RegistrySimple
{
    private final Object defaultObject;
    private static final String __OBFID = "CL_00001198";

    public RegistryDefaulted(Object par1Obj)
    {
        this.defaultObject = par1Obj;
    }

    public Object getObject(Object par1Obj)
    {
        Object var2 = super.getObject(par1Obj);
        return var2 == null ? this.defaultObject : var2;
    }
}
