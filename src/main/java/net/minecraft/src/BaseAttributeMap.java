package net.minecraft.src;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BaseAttributeMap
{
    protected final Map attributes = new HashMap();
    protected final Map attributesByName = new LowerStringMap();

    public AttributeInstance getAttributeInstance(Attribute par1Attribute)
    {
        return (AttributeInstance)this.attributes.get(par1Attribute);
    }

    public AttributeInstance getAttributeInstanceByName(String par1Str)
    {
        return (AttributeInstance)this.attributesByName.get(par1Str);
    }

    public abstract AttributeInstance func_111150_b(Attribute var1);

    public Collection getAllAttributes()
    {
        return this.attributesByName.values();
    }

    public void func_111149_a(ModifiableAttributeInstance par1ModifiableAttributeInstance) {}

    public void removeAttributeModifiers(Multimap par1Multimap)
    {
        Iterator var2 = par1Multimap.entries().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();
            AttributeInstance var4 = this.getAttributeInstanceByName((String)var3.getKey());

            if (var4 != null)
            {
                var4.removeModifier((AttributeModifier)var3.getValue());
            }
        }
    }

    public void applyAttributeModifiers(Multimap par1Multimap)
    {
        Iterator var2 = par1Multimap.entries().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();
            AttributeInstance var4 = this.getAttributeInstanceByName((String)var3.getKey());

            if (var4 != null)
            {
                var4.removeModifier((AttributeModifier)var3.getValue());
                var4.applyModifier((AttributeModifier)var3.getValue());
            }
        }
    }
}
