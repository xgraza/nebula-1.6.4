package net.minecraft.src;

import argo.jdom.JsonNode;

public class WorldTemplate extends ValueObject
{
    public String field_110734_a;
    public String field_110732_b;
    public String field_110733_c;
    public String field_110731_d;

    public static WorldTemplate func_110730_a(JsonNode par0JsonNode)
    {
        WorldTemplate var1 = new WorldTemplate();

        try
        {
            var1.field_110734_a = par0JsonNode.getNumberValue(new Object[] {"id"});
            var1.field_110732_b = par0JsonNode.getStringValue(new Object[] {"name"});
            var1.field_110733_c = par0JsonNode.getStringValue(new Object[] {"version"});
            var1.field_110731_d = par0JsonNode.getStringValue(new Object[] {"author"});
        }
        catch (IllegalArgumentException var3)
        {
            ;
        }

        return var1;
    }
}
