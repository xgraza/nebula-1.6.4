package net.minecraft.src;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer
{
    public LanguageMetadataSection func_135020_a(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject var4 = par1JsonElement.getAsJsonObject();
        HashSet var5 = Sets.newHashSet();
        Iterator var6 = var4.entrySet().iterator();
        String var8;
        String var11;
        String var12;
        boolean var13;

        do
        {
            if (!var6.hasNext())
            {
                return new LanguageMetadataSection(var5);
            }

            Entry var7 = (Entry)var6.next();
            var8 = (String)var7.getKey();
            JsonElement var9 = (JsonElement)var7.getValue();

            if (!var9.isJsonObject())
            {
                throw new JsonParseException("Invalid language->\'" + var8 + "\': expected object, was " + var9);
            }

            JsonObject var10 = var9.getAsJsonObject();
            var11 = this.func_110486_a(var10.get("region"), "region", "", 0, Integer.MAX_VALUE);
            var12 = this.func_110486_a(var10.get("name"), "name", "", 0, Integer.MAX_VALUE);
            var13 = this.func_110484_a(var10.get("bidirectional"), "bidirectional", Boolean.valueOf(false));

            if (var11.isEmpty())
            {
                throw new JsonParseException("Invalid language->\'" + var8 + "\'->region: empty value");
            }

            if (var12.isEmpty())
            {
                throw new JsonParseException("Invalid language->\'" + var8 + "\'->name: empty value");
            }
        }
        while (var5.add(new Language(var8, var11, var12, var13)));

        throw new JsonParseException("Duplicate language->\'" + var8 + "\' defined");
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "language";
    }

    public Object deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.func_135020_a(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }
}
