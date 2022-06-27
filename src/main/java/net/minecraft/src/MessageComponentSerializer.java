package net.minecraft.src;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class MessageComponentSerializer implements JsonDeserializer, JsonSerializer
{
    public ChatMessageComponent deserializeComponent(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        ChatMessageComponent var4 = new ChatMessageComponent();
        JsonObject var5 = (JsonObject)par1JsonElement;
        JsonElement var6 = var5.get("text");
        JsonElement var7 = var5.get("translate");
        JsonElement var8 = var5.get("color");
        JsonElement var9 = var5.get("bold");
        JsonElement var10 = var5.get("italic");
        JsonElement var11 = var5.get("underlined");
        JsonElement var12 = var5.get("obfuscated");

        if (var8 != null && var8.isJsonPrimitive())
        {
            EnumChatFormatting var13 = EnumChatFormatting.func_96300_b(var8.getAsString());

            if (var13 == null || !var13.isColor())
            {
                throw new JsonParseException("Given color (" + var8.getAsString() + ") is not a valid selection");
            }

            var4.setColor(var13);
        }

        if (var9 != null && var9.isJsonPrimitive())
        {
            var4.setBold(Boolean.valueOf(var9.getAsBoolean()));
        }

        if (var10 != null && var10.isJsonPrimitive())
        {
            var4.setItalic(Boolean.valueOf(var10.getAsBoolean()));
        }

        if (var11 != null && var11.isJsonPrimitive())
        {
            var4.setUnderline(Boolean.valueOf(var11.getAsBoolean()));
        }

        if (var12 != null && var12.isJsonPrimitive())
        {
            var4.setObfuscated(Boolean.valueOf(var12.getAsBoolean()));
        }

        if (var6 != null)
        {
            if (var6.isJsonArray())
            {
                JsonArray var17 = var6.getAsJsonArray();
                Iterator var14 = var17.iterator();

                while (var14.hasNext())
                {
                    JsonElement var15 = (JsonElement)var14.next();

                    if (var15.isJsonPrimitive())
                    {
                        var4.addText(var15.getAsString());
                    }
                    else if (var15.isJsonObject())
                    {
                        var4.appendComponent(this.deserializeComponent(var15, par2Type, par3JsonDeserializationContext));
                    }
                }
            }
            else if (var6.isJsonPrimitive())
            {
                var4.addText(var6.getAsString());
            }
        }
        else if (var7 != null && var7.isJsonPrimitive())
        {
            JsonElement var18 = var5.get("using");

            if (var18 != null)
            {
                if (var18.isJsonArray())
                {
                    ArrayList var19 = Lists.newArrayList();
                    Iterator var20 = var18.getAsJsonArray().iterator();

                    while (var20.hasNext())
                    {
                        JsonElement var16 = (JsonElement)var20.next();

                        if (var16.isJsonPrimitive())
                        {
                            var19.add(var16.getAsString());
                        }
                        else if (var16.isJsonObject())
                        {
                            var19.add(this.deserializeComponent(var16, par2Type, par3JsonDeserializationContext));
                        }
                    }

                    var4.addFormatted(var7.getAsString(), var19.toArray());
                }
                else if (var18.isJsonPrimitive())
                {
                    var4.addFormatted(var7.getAsString(), new Object[] {var18.getAsString()});
                }
            }
            else
            {
                var4.addKey(var7.getAsString());
            }
        }

        return var4;
    }

    public JsonElement serializeComponent(ChatMessageComponent par1ChatMessageComponent, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        JsonObject var4 = new JsonObject();

        if (par1ChatMessageComponent.getColor() != null)
        {
            var4.addProperty("color", par1ChatMessageComponent.getColor().func_96297_d());
        }

        if (par1ChatMessageComponent.isBold() != null)
        {
            var4.addProperty("bold", par1ChatMessageComponent.isBold());
        }

        if (par1ChatMessageComponent.isItalic() != null)
        {
            var4.addProperty("italic", par1ChatMessageComponent.isItalic());
        }

        if (par1ChatMessageComponent.isUnderline() != null)
        {
            var4.addProperty("underlined", par1ChatMessageComponent.isUnderline());
        }

        if (par1ChatMessageComponent.isObfuscated() != null)
        {
            var4.addProperty("obfuscated", par1ChatMessageComponent.isObfuscated());
        }

        if (par1ChatMessageComponent.getText() != null)
        {
            var4.addProperty("text", par1ChatMessageComponent.getText());
        }
        else if (par1ChatMessageComponent.getTranslationKey() != null)
        {
            var4.addProperty("translate", par1ChatMessageComponent.getTranslationKey());

            if (par1ChatMessageComponent.getSubComponents() != null && !par1ChatMessageComponent.getSubComponents().isEmpty())
            {
                var4.add("using", this.serializeComponentChildren(par1ChatMessageComponent, par2Type, par3JsonSerializationContext));
            }
        }
        else if (par1ChatMessageComponent.getSubComponents() != null && !par1ChatMessageComponent.getSubComponents().isEmpty())
        {
            var4.add("text", this.serializeComponentChildren(par1ChatMessageComponent, par2Type, par3JsonSerializationContext));
        }

        return var4;
    }

    private JsonArray serializeComponentChildren(ChatMessageComponent par1ChatMessageComponent, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        JsonArray var4 = new JsonArray();
        Iterator var5 = par1ChatMessageComponent.getSubComponents().iterator();

        while (var5.hasNext())
        {
            ChatMessageComponent var6 = (ChatMessageComponent)var5.next();

            if (var6.getText() != null)
            {
                var4.add(new JsonPrimitive(var6.getText()));
            }
            else
            {
                var4.add(this.serializeComponent(var6, par2Type, par3JsonSerializationContext));
            }
        }

        return var4;
    }

    public Object deserialize(JsonElement par1JsonElement, Type par2Type, JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.deserializeComponent(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }

    public JsonElement serialize(Object par1Obj, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
    {
        return this.serializeComponent((ChatMessageComponent)par1Obj, par2Type, par3JsonSerializationContext);
    }
}
