package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class ValueObject
{
    public String toString()
    {
        StringBuilder var1 = new StringBuilder("{");
        Field[] var2 = this.getClass().getFields();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            Field var5 = var2[var4];

            if (!func_96394_a(var5))
            {
                try
                {
                    var1.append(var5.getName()).append("=").append(var5.get(this)).append(" ");
                }
                catch (IllegalAccessException var7)
                {
                    ;
                }
            }
        }

        var1.deleteCharAt(var1.length() - 1);
        var1.append('}');
        return var1.toString();
    }

    private static boolean func_96394_a(Field par0Field)
    {
        return Modifier.isStatic(par0Field.getModifiers());
    }
}
