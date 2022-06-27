package net.minecraft.src;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer
{
    public PackMetadataSection func_110489_a(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject var4 = par1JsonElement.getAsJsonObject();
        String var5 = this.func_110486_a(var4.get("description"), "description", (String)null, 1, Integer.MAX_VALUE);
        int var6 = this.func_110485_a(var4.get("pack_format"), "pack_format", (Integer)null, 1, Integer.MAX_VALUE);
        return new PackMetadataSection(var5, var6);
    }

    public JsonElement func_110488_a(PackMetadataSection par1PackMetadataSection, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        JsonObject var4 = new JsonObject();
        var4.addProperty("pack_format", Integer.valueOf(par1PackMetadataSection.getPackFormat()));
        var4.addProperty("description", par1PackMetadataSection.getPackDescription());
        return var4;
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "pack";
    }

    public Object deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.func_110489_a(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }

    public JsonElement serialize(Object par1Obj, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        return this.func_110488_a((PackMetadataSection)par1Obj, par2Type, par3JsonSerializationContext);
    }
}
