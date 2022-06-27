package net.minecraft.src;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer
{
    public AnimationMetadataSection func_110493_a(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        ArrayList var4 = Lists.newArrayList();
        JsonObject var5 = (JsonObject)par1JsonElement;
        int var6 = this.func_110485_a(var5.get("frametime"), "frametime", Integer.valueOf(1), 1, Integer.MAX_VALUE);
        int var8;

        if (var5.has("frames"))
        {
            try
            {
                JsonArray var7 = var5.getAsJsonArray("frames");

                for (var8 = 0; var8 < var7.size(); ++var8)
                {
                    JsonElement var9 = var7.get(var8);
                    AnimationFrame var10 = this.parseAnimationFrame(var8, var9);

                    if (var10 != null)
                    {
                        var4.add(var10);
                    }
                }
            }
            catch (ClassCastException var11)
            {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + var5.get("frames"), var11);
            }
        }

        int var12 = this.func_110485_a(var5.get("width"), "width", Integer.valueOf(-1), 1, Integer.MAX_VALUE);
        var8 = this.func_110485_a(var5.get("height"), "height", Integer.valueOf(-1), 1, Integer.MAX_VALUE);
        return new AnimationMetadataSection(var4, var12, var8, var6);
    }

    private AnimationFrame parseAnimationFrame(int par1, JsonElement par2JsonElement)
    {
        if (par2JsonElement.isJsonPrimitive())
        {
            try
            {
                return new AnimationFrame(par2JsonElement.getAsInt());
            }
            catch (NumberFormatException var6)
            {
                throw new JsonParseException("Invalid animation->frames->" + par1 + ": expected number, was " + par2JsonElement, var6);
            }
        }
        else if (par2JsonElement.isJsonObject())
        {
            JsonObject var3 = par2JsonElement.getAsJsonObject();
            int var4 = this.func_110485_a(var3.get("time"), "frames->" + par1 + "->time", Integer.valueOf(-1), 1, Integer.MAX_VALUE);
            int var5 = this.func_110485_a(var3.get("index"), "frames->" + par1 + "->index", (Integer)null, 0, Integer.MAX_VALUE);
            return new AnimationFrame(var5, var4);
        }
        else
        {
            return null;
        }
    }

    public JsonElement func_110491_a(AnimationMetadataSection par1AnimationMetadataSection, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        JsonObject var4 = new JsonObject();
        var4.addProperty("frametime", Integer.valueOf(par1AnimationMetadataSection.getFrameTime()));

        if (par1AnimationMetadataSection.getFrameWidth() != -1)
        {
            var4.addProperty("width", Integer.valueOf(par1AnimationMetadataSection.getFrameWidth()));
        }

        if (par1AnimationMetadataSection.getFrameHeight() != -1)
        {
            var4.addProperty("height", Integer.valueOf(par1AnimationMetadataSection.getFrameHeight()));
        }

        if (par1AnimationMetadataSection.getFrameCount() > 0)
        {
            JsonArray var5 = new JsonArray();

            for (int var6 = 0; var6 < par1AnimationMetadataSection.getFrameCount(); ++var6)
            {
                if (par1AnimationMetadataSection.frameHasTime(var6))
                {
                    JsonObject var7 = new JsonObject();
                    var7.addProperty("index", Integer.valueOf(par1AnimationMetadataSection.getFrameIndex(var6)));
                    var7.addProperty("time", Integer.valueOf(par1AnimationMetadataSection.getFrameTimeSingle(var6)));
                    var5.add(var7);
                }
                else
                {
                    var5.add(new JsonPrimitive(Integer.valueOf(par1AnimationMetadataSection.getFrameIndex(var6))));
                }
            }

            var4.add("frames", var5);
        }

        return var4;
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "animation";
    }

    public Object deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.func_110493_a(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }

    public JsonElement serialize(Object par1Obj, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        return this.func_110491_a((AnimationMetadataSection)par1Obj, par2Type, par3JsonSerializationContext);
    }
}
