package net.minecraft.src;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServersideAttributeMap extends BaseAttributeMap
{
    private final Set field_111162_d = Sets.newHashSet();
    protected final Map field_111163_c = new LowerStringMap();

    public ModifiableAttributeInstance func_111159_c(Attribute par1Attribute)
    {
        return (ModifiableAttributeInstance)super.getAttributeInstance(par1Attribute);
    }

    public ModifiableAttributeInstance func_111158_b(String par1Str)
    {
        AttributeInstance var2 = super.getAttributeInstanceByName(par1Str);

        if (var2 == null)
        {
            var2 = (AttributeInstance)this.field_111163_c.get(par1Str);
        }

        return (ModifiableAttributeInstance)var2;
    }

    public AttributeInstance func_111150_b(Attribute par1Attribute)
    {
        if (this.attributesByName.containsKey(par1Attribute.getAttributeUnlocalizedName()))
        {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        else
        {
            ModifiableAttributeInstance var2 = new ModifiableAttributeInstance(this, par1Attribute);
            this.attributesByName.put(par1Attribute.getAttributeUnlocalizedName(), var2);

            if (par1Attribute instanceof RangedAttribute && ((RangedAttribute)par1Attribute).func_111116_f() != null)
            {
                this.field_111163_c.put(((RangedAttribute)par1Attribute).func_111116_f(), var2);
            }

            this.attributes.put(par1Attribute, var2);
            return var2;
        }
    }

    public void func_111149_a(ModifiableAttributeInstance par1ModifiableAttributeInstance)
    {
        if (par1ModifiableAttributeInstance.func_111123_a().getShouldWatch())
        {
            this.field_111162_d.add(par1ModifiableAttributeInstance);
        }
    }

    public Set func_111161_b()
    {
        return this.field_111162_d;
    }

    public Collection func_111160_c()
    {
        HashSet var1 = Sets.newHashSet();
        Iterator var2 = this.getAllAttributes().iterator();

        while (var2.hasNext())
        {
            AttributeInstance var3 = (AttributeInstance)var2.next();

            if (var3.func_111123_a().getShouldWatch())
            {
                var1.add(var3);
            }
        }

        return var1;
    }

    public AttributeInstance getAttributeInstanceByName(String par1Str)
    {
        return this.func_111158_b(par1Str);
    }

    public AttributeInstance getAttributeInstance(Attribute par1Attribute)
    {
        return this.func_111159_c(par1Attribute);
    }
}
