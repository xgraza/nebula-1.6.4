package net.minecraft.src;

import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

public class ValueObjectSubscription extends ValueObject
{
    public long field_98171_a;
    public int field_98170_b;

    public static ValueObjectSubscription func_98169_a(String par0Str)
    {
        ValueObjectSubscription var1 = new ValueObjectSubscription();

        try
        {
            JsonRootNode var2 = (new JdomParser()).parse(par0Str);
            var1.field_98171_a = Long.parseLong(var2.getNumberValue(new Object[] {"startDate"}));
            var1.field_98170_b = Integer.parseInt(var2.getNumberValue(new Object[] {"daysLeft"}));
        }
        catch (InvalidSyntaxException var3)
        {
            ;
        }
        catch (IllegalArgumentException var4)
        {
            ;
        }

        return var1;
    }
}
