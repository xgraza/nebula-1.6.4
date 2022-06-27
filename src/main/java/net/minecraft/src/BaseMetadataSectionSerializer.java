package net.minecraft.src;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public abstract class BaseMetadataSectionSerializer implements MetadataSectionSerializer
{
    protected float func_110487_a(JsonElement par1JsonElement, String par2Str, Float par3, float par4, float par5)
    {
        par2Str = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3 == null)
            {
                throw new JsonParseException("Missing " + par2Str + ": expected float");
            }
            else
            {
                return par3.floatValue();
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str + ": expected float, was " + par1JsonElement);
        }
        else
        {
            try
            {
                float var6 = par1JsonElement.getAsFloat();

                if (var6 < par4)
                {
                    throw new JsonParseException("Invalid " + par2Str + ": expected float >= " + par4 + ", was " + var6);
                }
                else if (var6 > par5)
                {
                    throw new JsonParseException("Invalid " + par2Str + ": expected float <= " + par5 + ", was " + var6);
                }
                else
                {
                    return var6;
                }
            }
            catch (NumberFormatException var7)
            {
                throw new JsonParseException("Invalid " + par2Str + ": expected float, was " + par1JsonElement, var7);
            }
        }
    }

    protected int func_110485_a(JsonElement par1JsonElement, String par2Str, Integer par3, int par4, int par5)
    {
        par2Str = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3 == null)
            {
                throw new JsonParseException("Missing " + par2Str + ": expected int");
            }
            else
            {
                return par3.intValue();
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str + ": expected int, was " + par1JsonElement);
        }
        else
        {
            try
            {
                int var6 = par1JsonElement.getAsInt();

                if (var6 < par4)
                {
                    throw new JsonParseException("Invalid " + par2Str + ": expected int >= " + par4 + ", was " + var6);
                }
                else if (var6 > par5)
                {
                    throw new JsonParseException("Invalid " + par2Str + ": expected int <= " + par5 + ", was " + var6);
                }
                else
                {
                    return var6;
                }
            }
            catch (NumberFormatException var7)
            {
                throw new JsonParseException("Invalid " + par2Str + ": expected int, was " + par1JsonElement, var7);
            }
        }
    }

    protected String func_110486_a(JsonElement par1JsonElement, String par2Str, String par3Str, int par4, int par5)
    {
        par2Str = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3Str == null)
            {
                throw new JsonParseException("Missing " + par2Str + ": expected string");
            }
            else
            {
                return par3Str;
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str + ": expected string, was " + par1JsonElement);
        }
        else
        {
            String var6 = par1JsonElement.getAsString();

            if (var6.length() < par4)
            {
                throw new JsonParseException("Invalid " + par2Str + ": expected string length >= " + par4 + ", was " + var6);
            }
            else if (var6.length() > par5)
            {
                throw new JsonParseException("Invalid " + par2Str + ": expected string length <= " + par5 + ", was " + var6);
            }
            else
            {
                return var6;
            }
        }
    }

    protected boolean func_110484_a(JsonElement par1JsonElement, String par2Str, Boolean par3)
    {
        par2Str = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3 == null)
            {
                throw new JsonParseException("Missing " + par2Str + ": expected boolean");
            }
            else
            {
                return par3.booleanValue();
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str + ": expected boolean, was " + par1JsonElement);
        }
        else
        {
            boolean var4 = par1JsonElement.getAsBoolean();
            return var4;
        }
    }
}
