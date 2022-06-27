package net.minecraft.src;

import argo.jdom.JsonNode;
import java.util.Date;

public class Backup extends ValueObject
{
    public String field_110727_a;
    public Date field_110725_b;
    public long field_110726_c;

    public static Backup func_110724_a(JsonNode par0JsonNode)
    {
        Backup var1 = new Backup();

        try
        {
            var1.field_110727_a = par0JsonNode.getStringValue(new Object[] {"backupId"});
            var1.field_110725_b = new Date(Long.parseLong(par0JsonNode.getNumberValue(new Object[] {"lastModifiedDate"})));
            var1.field_110726_c = Long.parseLong(par0JsonNode.getNumberValue(new Object[] {"size"}));
        }
        catch (IllegalArgumentException var3)
        {
            ;
        }

        return var1;
    }
}
