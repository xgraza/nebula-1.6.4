package net.minecraft.src;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.StringEscapeUtils;

public class NbtTagValue
{
    private String[] parents = null;
    private String name = null;
    private boolean negative = false;
    private int type = 0;
    private String value = null;
    private int valueFormat = 0;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_PATTERN = 1;
    private static final int TYPE_IPATTERN = 2;
    private static final int TYPE_REGEX = 3;
    private static final int TYPE_IREGEX = 4;
    private static final String PREFIX_PATTERN = "pattern:";
    private static final String PREFIX_IPATTERN = "ipattern:";
    private static final String PREFIX_REGEX = "regex:";
    private static final String PREFIX_IREGEX = "iregex:";
    private static final int FORMAT_DEFAULT = 0;
    private static final int FORMAT_HEX_COLOR = 1;
    private static final String PREFIX_HEX_COLOR = "#";
    private static final Pattern PATTERN_HEX_COLOR = Pattern.compile("^#[0-9a-f]{6}+$");

    public NbtTagValue(String tag, String value)
    {
        String[] names = Config.tokenize(tag, ".");
        this.parents = (String[])Arrays.copyOfRange(names, 0, names.length - 1);
        this.name = names[names.length - 1];

        if (value.startsWith("!"))
        {
            this.negative = true;
            value = value.substring(1);
        }

        if (value.startsWith("pattern:"))
        {
            this.type = 1;
            value = value.substring("pattern:".length());
        }
        else if (value.startsWith("ipattern:"))
        {
            this.type = 2;
            value = value.substring("ipattern:".length()).toLowerCase();
        }
        else if (value.startsWith("regex:"))
        {
            this.type = 3;
            value = value.substring("regex:".length());
        }
        else if (value.startsWith("iregex:"))
        {
            this.type = 4;
            value = value.substring("iregex:".length()).toLowerCase();
        }
        else
        {
            this.type = 0;
        }

        value = StringEscapeUtils.unescapeJava(value);

        if (this.type == 0 && PATTERN_HEX_COLOR.matcher(value).matches())
        {
            this.valueFormat = 1;
        }

        this.value = value;
    }

    public boolean matches(NBTTagCompound nbt)
    {
        return this.negative ? !this.matchesCompound(nbt) : this.matchesCompound(nbt);
    }

    public boolean matchesCompound(NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return false;
        }
        else
        {
            Object tagBase = nbt;

            for (int i = 0; i < this.parents.length; ++i)
            {
                String tag = this.parents[i];
                tagBase = getChildTag((NBTBase)tagBase, tag);

                if (tagBase == null)
                {
                    return false;
                }
            }

            if (this.name.equals("*"))
            {
                return this.matchesAnyChild((NBTBase)tagBase);
            }
            else
            {
                NBTBase var5 = getChildTag((NBTBase)tagBase, this.name);

                if (var5 == null)
                {
                    return false;
                }
                else if (this.matchesBase(var5))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
    }

    private boolean matchesAnyChild(NBTBase tagBase)
    {
        if (tagBase instanceof NBTTagCompound)
        {
            NBTTagCompound tagList = (NBTTagCompound)tagBase;
            Set count = tagList.func_150296_c();
            Iterator i = count.iterator();

            while (i.hasNext())
            {
                String nbtBase = (String)i.next();
                NBTBase nbtBase1 = tagList.getTag(nbtBase);

                if (this.matchesBase(nbtBase1))
                {
                    return true;
                }
            }
        }

        if (tagBase instanceof NBTTagList)
        {
            NBTTagList var7 = (NBTTagList)tagBase;
            int var8 = var7.tagCount();

            for (int var9 = 0; var9 < var8; ++var9)
            {
                NBTTagCompound var10 = var7.getCompoundTagAt(var9);

                if (this.matchesBase(var10))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private static NBTBase getChildTag(NBTBase tagBase, String tag)
    {
        if (tagBase instanceof NBTTagCompound)
        {
            NBTTagCompound tagList1 = (NBTTagCompound)tagBase;
            return tagList1.getTag(tag);
        }
        else if (tagBase instanceof NBTTagList)
        {
            NBTTagList tagList = (NBTTagList)tagBase;

            if (tag.equals("count"))
            {
                return new NBTTagInt(tagList.tagCount());
            }
            else
            {
                int index = Config.parseInt(tag, -1);
                return index < 0 ? null : tagList.getCompoundTagAt(index);
            }
        }
        else
        {
            return null;
        }
    }

    public boolean matchesBase(NBTBase nbtBase)
    {
        if (nbtBase == null)
        {
            return false;
        }
        else
        {
            String nbtValue = getNbtString(nbtBase, this.valueFormat);
            return this.matchesValue(nbtValue);
        }
    }

    public boolean matchesValue(String nbtValue)
    {
        if (nbtValue == null)
        {
            return false;
        }
        else
        {
            switch (this.type)
            {
                case 0:
                    return nbtValue.equals(this.value);

                case 1:
                    return this.matchesPattern(nbtValue, this.value);

                case 2:
                    return this.matchesPattern(nbtValue.toLowerCase(), this.value);

                case 3:
                    return this.matchesRegex(nbtValue, this.value);

                case 4:
                    return this.matchesRegex(nbtValue.toLowerCase(), this.value);

                default:
                    throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.type);
            }
        }
    }

    private boolean matchesPattern(String str, String pattern)
    {
        return StrUtils.equalsMask(str, pattern, '*', '?');
    }

    private boolean matchesRegex(String str, String regex)
    {
        return str.matches(regex);
    }

    private static String getNbtString(NBTBase nbtBase, int format)
    {
        if (nbtBase == null)
        {
            return null;
        }
        else if (nbtBase instanceof NBTTagString)
        {
            NBTTagString d6 = (NBTTagString)nbtBase;
            return d6.func_150285_a_();
        }
        else if (nbtBase instanceof NBTTagInt)
        {
            NBTTagInt d5 = (NBTTagInt)nbtBase;
            return format == 1 ? "#" + StrUtils.fillLeft(Integer.toHexString(d5.func_150287_d()), 6, '0') : Integer.toString(d5.func_150287_d());
        }
        else if (nbtBase instanceof NBTTagByte)
        {
            NBTTagByte d4 = (NBTTagByte)nbtBase;
            return Byte.toString(d4.func_150290_f());
        }
        else if (nbtBase instanceof NBTTagShort)
        {
            NBTTagShort d3 = (NBTTagShort)nbtBase;
            return Short.toString(d3.func_150289_e());
        }
        else if (nbtBase instanceof NBTTagLong)
        {
            NBTTagLong d2 = (NBTTagLong)nbtBase;
            return Long.toString(d2.func_150291_c());
        }
        else if (nbtBase instanceof NBTTagFloat)
        {
            NBTTagFloat d1 = (NBTTagFloat)nbtBase;
            return Float.toString(d1.func_150288_h());
        }
        else if (nbtBase instanceof NBTTagDouble)
        {
            NBTTagDouble d = (NBTTagDouble)nbtBase;
            return Double.toString(d.func_150286_g());
        }
        else
        {
            return nbtBase.toString();
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < this.parents.length; ++i)
        {
            String parent = this.parents[i];

            if (i > 0)
            {
                sb.append(".");
            }

            sb.append(parent);
        }

        if (sb.length() > 0)
        {
            sb.append(".");
        }

        sb.append(this.name);
        sb.append(" = ");
        sb.append(this.value);
        return sb.toString();
    }
}
