package net.minecraft.src;

import argo.jdom.JsonNode;

public class PendingInvite extends ValueObject
{
    public String field_130094_a;
    public String field_130092_b;
    public String field_130093_c;

    public static PendingInvite func_130091_a(JsonNode par0JsonNode)
    {
        PendingInvite var1 = new PendingInvite();

        try
        {
            var1.field_130094_a = par0JsonNode.getStringValue(new Object[] {"invitationId"});
            var1.field_130092_b = par0JsonNode.getStringValue(new Object[] {"worldName"});
            var1.field_130093_c = par0JsonNode.getStringValue(new Object[] {"worldOwnerName"});
        }
        catch (Exception var3)
        {
            ;
        }

        return var1;
    }
}
