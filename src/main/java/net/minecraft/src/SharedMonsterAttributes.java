package net.minecraft.src;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class SharedMonsterAttributes
{
    public static final Attribute maxHealth = (new RangedAttribute("generic.maxHealth", 20.0D, 0.0D, Double.MAX_VALUE)).func_111117_a("Max Health").setShouldWatch(true);
    public static final Attribute followRange = (new RangedAttribute("generic.followRange", 32.0D, 0.0D, 2048.0D)).func_111117_a("Follow Range");
    public static final Attribute knockbackResistance = (new RangedAttribute("generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).func_111117_a("Knockback Resistance");
    public static final Attribute movementSpeed = (new RangedAttribute("generic.movementSpeed", 0.699999988079071D, 0.0D, Double.MAX_VALUE)).func_111117_a("Movement Speed").setShouldWatch(true);
    public static final Attribute attackDamage = new RangedAttribute("generic.attackDamage", 2.0D, 0.0D, Double.MAX_VALUE);

    public static NBTTagList func_111257_a(BaseAttributeMap par0BaseAttributeMap)
    {
        NBTTagList var1 = new NBTTagList();
        Iterator var2 = par0BaseAttributeMap.getAllAttributes().iterator();

        while (var2.hasNext())
        {
            AttributeInstance var3 = (AttributeInstance)var2.next();
            var1.appendTag(func_111261_a(var3));
        }

        return var1;
    }

    private static NBTTagCompound func_111261_a(AttributeInstance par0AttributeInstance)
    {
        NBTTagCompound var1 = new NBTTagCompound();
        Attribute var2 = par0AttributeInstance.func_111123_a();
        var1.setString("Name", var2.getAttributeUnlocalizedName());
        var1.setDouble("Base", par0AttributeInstance.getBaseValue());
        Collection var3 = par0AttributeInstance.func_111122_c();

        if (var3 != null && !var3.isEmpty())
        {
            NBTTagList var4 = new NBTTagList();
            Iterator var5 = var3.iterator();

            while (var5.hasNext())
            {
                AttributeModifier var6 = (AttributeModifier)var5.next();

                if (var6.isSaved())
                {
                    var4.appendTag(func_111262_a(var6));
                }
            }

            var1.setTag("Modifiers", var4);
        }

        return var1;
    }

    private static NBTTagCompound func_111262_a(AttributeModifier par0AttributeModifier)
    {
        NBTTagCompound var1 = new NBTTagCompound();
        var1.setString("Name", par0AttributeModifier.getName());
        var1.setDouble("Amount", par0AttributeModifier.getAmount());
        var1.setInteger("Operation", par0AttributeModifier.getOperation());
        var1.setLong("UUIDMost", par0AttributeModifier.getID().getMostSignificantBits());
        var1.setLong("UUIDLeast", par0AttributeModifier.getID().getLeastSignificantBits());
        return var1;
    }

    public static void func_111260_a(BaseAttributeMap par0BaseAttributeMap, NBTTagList par1NBTTagList, ILogAgent par2ILogAgent)
    {
        for (int var3 = 0; var3 < par1NBTTagList.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)par1NBTTagList.tagAt(var3);
            AttributeInstance var5 = par0BaseAttributeMap.getAttributeInstanceByName(var4.getString("Name"));

            if (var5 != null)
            {
                func_111258_a(var5, var4);
            }
            else if (par2ILogAgent != null)
            {
                par2ILogAgent.logWarning("Ignoring unknown attribute \'" + var4.getString("Name") + "\'");
            }
        }
    }

    private static void func_111258_a(AttributeInstance par0AttributeInstance, NBTTagCompound par1NBTTagCompound)
    {
        par0AttributeInstance.setAttribute(par1NBTTagCompound.getDouble("Base"));

        if (par1NBTTagCompound.hasKey("Modifiers"))
        {
            NBTTagList var2 = par1NBTTagCompound.getTagList("Modifiers");

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                AttributeModifier var4 = func_111259_a((NBTTagCompound)var2.tagAt(var3));
                AttributeModifier var5 = par0AttributeInstance.getModifier(var4.getID());

                if (var5 != null)
                {
                    par0AttributeInstance.removeModifier(var5);
                }

                par0AttributeInstance.applyModifier(var4);
            }
        }
    }

    public static AttributeModifier func_111259_a(NBTTagCompound par0NBTTagCompound)
    {
        UUID var1 = new UUID(par0NBTTagCompound.getLong("UUIDMost"), par0NBTTagCompound.getLong("UUIDLeast"));
        return new AttributeModifier(var1, par0NBTTagCompound.getString("Name"), par0NBTTagCompound.getDouble("Amount"), par0NBTTagCompound.getInteger("Operation"));
    }
}
