package net.minecraft.src;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BackupList
{
    public List field_111223_a;

    public static BackupList func_111222_a(String par0Str)
    {
        BackupList var1 = new BackupList();
        var1.field_111223_a = new ArrayList();

        try
        {
            JsonRootNode var2 = (new JdomParser()).parse(par0Str);

            if (var2.isArrayNode(new Object[] {"backups"}))
            {
                Iterator var3 = var2.getArrayNode(new Object[] {"backups"}).iterator();

                while (var3.hasNext())
                {
                    JsonNode var4 = (JsonNode)var3.next();
                    var1.field_111223_a.add(Backup.func_110724_a(var4));
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
