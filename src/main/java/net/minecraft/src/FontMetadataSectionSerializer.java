package net.minecraft.src;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer
{
    public FontMetadataSection func_110490_a(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        JsonObject var4 = par1JsonElement.getAsJsonObject();
        float[] var5 = new float[256];
        float[] var6 = new float[256];
        float[] var7 = new float[256];
        float var8 = 1.0F;
        float var9 = 0.0F;
        float var10 = 0.0F;

        if (var4.has("characters"))
        {
            if (!var4.get("characters").isJsonObject())
            {
                throw new JsonParseException("Invalid font->characters: expected object, was " + var4.get("characters"));
            }

            JsonObject var11 = var4.getAsJsonObject("characters");

            if (var11.has("default"))
            {
                if (!var11.get("default").isJsonObject())
                {
                    throw new JsonParseException("Invalid font->characters->default: expected object, was " + var11.get("default"));
                }

                JsonObject var12 = var11.getAsJsonObject("default");
                var8 = this.func_110487_a(var12.get("width"), "characters->default->width", Float.valueOf(var8), 0.0F, 2.14748365E9F);
                var9 = this.func_110487_a(var12.get("spacing"), "characters->default->spacing", Float.valueOf(var9), 0.0F, 2.14748365E9F);
                var10 = this.func_110487_a(var12.get("left"), "characters->default->left", Float.valueOf(var10), 0.0F, 2.14748365E9F);
            }

            for (int var18 = 0; var18 < 256; ++var18)
            {
                JsonElement var13 = var11.get(Integer.toString(var18));
                float var14 = var8;
                float var15 = var9;
                float var16 = var10;

                if (var13 != null)
                {
                    if (!var13.isJsonObject())
                    {
                        throw new JsonParseException("Invalid font->characters->" + var18 + ": expected object, was " + var13);
                    }

                    JsonObject var17 = var13.getAsJsonObject();
                    var14 = this.func_110487_a(var17.get("width"), "characters->" + var18 + "->width", Float.valueOf(var8), 0.0F, 2.14748365E9F);
                    var15 = this.func_110487_a(var17.get("spacing"), "characters->" + var18 + "->spacing", Float.valueOf(var9), 0.0F, 2.14748365E9F);
                    var16 = this.func_110487_a(var17.get("left"), "characters->" + var18 + "->left", Float.valueOf(var10), 0.0F, 2.14748365E9F);
                }

                var5[var18] = var14;
                var6[var18] = var15;
                var7[var18] = var16;
            }
        }

        return new FontMetadataSection(var5, var7, var6);
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "font";
    }

    public Object deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.func_110490_a(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }
}
