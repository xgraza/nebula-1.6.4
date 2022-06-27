package net.minecraft.src;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer
{
    public TextureMetadataSection func_110494_a(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject var4 = par1JsonElement.getAsJsonObject();
        boolean var5 = this.func_110484_a(var4.get("blur"), "blur", Boolean.valueOf(false));
        boolean var6 = this.func_110484_a(var4.get("clamp"), "clamp", Boolean.valueOf(false));
        return new TextureMetadataSection(var5, var6);
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "texture";
    }

    public Object deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.func_110494_a(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }
}
