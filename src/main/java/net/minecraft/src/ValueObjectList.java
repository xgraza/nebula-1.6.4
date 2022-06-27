package net.minecraft.src;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValueObjectList extends ValueObject
{
    public List field_96430_d;

    public static ValueObjectList func_98161_a(String par0Str)
    {
        ValueObjectList var1 = new ValueObjectList();
        var1.field_96430_d = new ArrayList();

        try
        {
            JsonRootNode var2 = (new JdomParser()).parse(par0Str);

            if (var2.isArrayNode(new Object[] {"servers"}))
            {
                Iterator var3 = var2.getArrayNode(new Object[] {"servers"}).iterator();

                while (var3.hasNext())
                {
                    JsonNode var4 = (JsonNode)var3.next();
                    var1.field_96430_d.add(McoServer.func_98163_a(var4));
                }
            }
        }
        catch (InvalidSyntaxException var5)
        {
            ;
        }
        catch (IllegalArgumentException var6)
        {
            ;
        }

        return var1;
    }
}
