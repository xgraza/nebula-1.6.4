package net.minecraft.src;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.saj.InvalidSyntaxException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class McoServer
{
    public long field_96408_a;
    public String field_96406_b;
    public String field_96407_c;
    public String field_96404_d;
    public String field_96405_e;
    public List field_96402_f;
    public String field_96403_g;
    public boolean field_98166_h;
    public int field_110729_i;
    public int field_110728_j;
    public int field_104063_i;
    public int field_96415_h;
    public String field_96414_k = "";
    public boolean field_96411_l;
    public boolean field_102022_m;
    public long field_96412_m;
    private String field_96409_n;
    private String field_96410_o;

    public String func_96397_a()
    {
        if (this.field_96409_n == null)
        {
            try
            {
                this.field_96409_n = URLDecoder.decode(this.field_96407_c, "UTF-8");
            }
            catch (UnsupportedEncodingException var2)
            {
                this.field_96409_n = this.field_96407_c;
            }
        }

        return this.field_96409_n;
    }

    public String func_96398_b()
    {
        if (this.field_96410_o == null)
        {
            try
            {
                this.field_96410_o = URLDecoder.decode(this.field_96406_b, "UTF-8");
            }
            catch (UnsupportedEncodingException var2)
            {
                this.field_96410_o = this.field_96406_b;
            }
        }

        return this.field_96410_o;
    }

    public void func_96399_a(String par1Str)
    {
        this.field_96406_b = par1Str;
        this.field_96410_o = null;
    }

    public void func_96400_b(String par1Str)
    {
        this.field_96407_c = par1Str;
        this.field_96409_n = null;
    }

    public void func_96401_a(McoServer par1McoServer)
    {
        this.field_96414_k = par1McoServer.field_96414_k;
        this.field_96412_m = par1McoServer.field_96412_m;
        this.field_96411_l = par1McoServer.field_96411_l;
        this.field_96415_h = par1McoServer.field_96415_h;
        this.field_102022_m = true;
    }

    public static McoServer func_98163_a(JsonNode par0JsonNode)
    {
        McoServer var1 = new McoServer();

        try
        {
            var1.field_96408_a = Long.parseLong(par0JsonNode.getNumberValue(new Object[] {"id"}));
            var1.field_96406_b = par0JsonNode.getStringValue(new Object[] {"name"});
            var1.field_96407_c = par0JsonNode.getStringValue(new Object[] {"motd"});
            var1.field_96404_d = par0JsonNode.getStringValue(new Object[] {"state"});
            var1.field_96405_e = par0JsonNode.getStringValue(new Object[] {"owner"});

            if (par0JsonNode.isArrayNode(new Object[] {"invited"}))
            {
                var1.field_96402_f = func_98164_a(par0JsonNode.getArrayNode(new Object[] {"invited"}));
            }
            else
            {
                var1.field_96402_f = new ArrayList();
            }

            var1.field_104063_i = Integer.parseInt(par0JsonNode.getNumberValue(new Object[] {"daysLeft"}));
            var1.field_96403_g = par0JsonNode.getStringValue(new Object[] {"ip"});
            var1.field_98166_h = par0JsonNode.getBooleanValue(new Object[] {"expired"}).booleanValue();
            var1.field_110729_i = Integer.parseInt(par0JsonNode.getNumberValue(new Object[] {"difficulty"}));
            var1.field_110728_j = Integer.parseInt(par0JsonNode.getNumberValue(new Object[] {"gameMode"}));
        }
        catch (IllegalArgumentException var3)
        {
            ;
        }

        return var1;
    }

    private static List func_98164_a(List par0List)
    {
        ArrayList var1 = new ArrayList();
        Iterator var2 = par0List.iterator();

        while (var2.hasNext())
        {
            JsonNode var3 = (JsonNode)var2.next();
            var1.add(var3.getStringValue(new Object[0]));
        }

        return var1;
    }

    public static McoServer func_98165_c(String par0Str)
    {
        McoServer var1 = new McoServer();

        try
        {
            var1 = func_98163_a((new JdomParser()).parse(par0Str));
        }
        catch (InvalidSyntaxException var3)
        {
            ;
        }

        return var1;
    }

    public int hashCode()
    {
        return (new HashCodeBuilder(17, 37)).append(this.field_96408_a).append(this.field_96406_b).append(this.field_96407_c).append(this.field_96404_d).append(this.field_96405_e).append(this.field_98166_h).toHashCode();
    }

    public boolean equals(Object par1Obj)
    {
        if (par1Obj == null)
        {
            return false;
        }
        else if (par1Obj == this)
        {
            return true;
        }
        else if (par1Obj.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            McoServer var2 = (McoServer)par1Obj;
            return (new EqualsBuilder()).append(this.field_96408_a, var2.field_96408_a).append(this.field_96406_b, var2.field_96406_b).append(this.field_96407_c, var2.field_96407_c).append(this.field_96404_d, var2.field_96404_d).append(this.field_96405_e, var2.field_96405_e).append(this.field_98166_h, var2.field_98166_h).isEquals();
        }
    }
}
