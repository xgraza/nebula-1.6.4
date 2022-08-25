package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced extends RegistrySimple implements IObjectIntIterable
{
    protected final ObjectIntIdentityMap underlyingIntegerMap = new ObjectIntIdentityMap();
    protected final Map field_148758_b;
    private static final String __OBFID = "CL_00001206";

    public RegistryNamespaced()
    {
        this.field_148758_b = ((BiMap)this.registryObjects).inverse();
    }

    public void addObject(int p_148756_1_, String p_148756_2_, Object p_148756_3_)
    {
        this.underlyingIntegerMap.func_148746_a(p_148756_3_, p_148756_1_);
        this.putObject(ensureNamespaced(p_148756_2_), p_148756_3_);
    }

    protected Map createUnderlyingMap()
    {
        return HashBiMap.create();
    }

    public Object getObject(String p_148751_1_)
    {
        return super.getObject(ensureNamespaced(p_148751_1_));
    }

    public String getNameForObject(Object p_148750_1_)
    {
        return (String)this.field_148758_b.get(p_148750_1_);
    }

    public boolean containsKey(String p_148752_1_)
    {
        return super.containsKey(ensureNamespaced(p_148752_1_));
    }

    public int getIDForObject(Object p_148757_1_)
    {
        return this.underlyingIntegerMap.func_148747_b(p_148757_1_);
    }

    public Object getObjectForID(int p_148754_1_)
    {
        return this.underlyingIntegerMap.func_148745_a(p_148754_1_);
    }

    public Iterator iterator()
    {
        return this.underlyingIntegerMap.iterator();
    }

    public boolean containsID(int p_148753_1_)
    {
        return this.underlyingIntegerMap.func_148744_b(p_148753_1_);
    }

    private static String ensureNamespaced(String p_148755_0_)
    {
        return p_148755_0_.indexOf(58) == -1 ? "minecraft:" + p_148755_0_ : p_148755_0_;
    }

    public boolean containsKey(Object p_148741_1_)
    {
        return this.containsKey((String)p_148741_1_);
    }

    public Object getObject(Object par1Obj)
    {
        return this.getObject((String)par1Obj);
    }
}
