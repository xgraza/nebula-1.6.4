package net.minecraft.src;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Iterator;
import java.util.List;

public class ChatMessageComponent
{
    private static final Gson field_111089_a = (new GsonBuilder()).registerTypeAdapter(ChatMessageComponent.class, new MessageComponentSerializer()).create();
    private EnumChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
    private Boolean obfuscated;
    private String text;
    private String translationKey;
    private List field_111091_i;

    public ChatMessageComponent() {}

    public ChatMessageComponent(ChatMessageComponent par1ChatMessageComponent)
    {
        this.color = par1ChatMessageComponent.color;
        this.bold = par1ChatMessageComponent.bold;
        this.italic = par1ChatMessageComponent.italic;
        this.underline = par1ChatMessageComponent.underline;
        this.obfuscated = par1ChatMessageComponent.obfuscated;
        this.text = par1ChatMessageComponent.text;
        this.translationKey = par1ChatMessageComponent.translationKey;
        this.field_111091_i = par1ChatMessageComponent.field_111091_i == null ? null : Lists.newArrayList(par1ChatMessageComponent.field_111091_i);
    }

    public ChatMessageComponent setColor(EnumChatFormatting par1EnumChatFormatting)
    {
        if (par1EnumChatFormatting != null && !par1EnumChatFormatting.isColor())
        {
            throw new IllegalArgumentException("Argument is not a valid color!");
        }
        else
        {
            this.color = par1EnumChatFormatting;
            return this;
        }
    }

    public EnumChatFormatting getColor()
    {
        return this.color;
    }

    public ChatMessageComponent setBold(Boolean par1)
    {
        this.bold = par1;
        return this;
    }

    public Boolean isBold()
    {
        return this.bold;
    }

    public ChatMessageComponent setItalic(Boolean par1)
    {
        this.italic = par1;
        return this;
    }

    public Boolean isItalic()
    {
        return this.italic;
    }

    public ChatMessageComponent setUnderline(Boolean par1)
    {
        this.underline = par1;
        return this;
    }

    public Boolean isUnderline()
    {
        return this.underline;
    }

    public ChatMessageComponent setObfuscated(Boolean par1)
    {
        this.obfuscated = par1;
        return this;
    }

    public Boolean isObfuscated()
    {
        return this.obfuscated;
    }

    protected String getText()
    {
        return this.text;
    }

    protected String getTranslationKey()
    {
        return this.translationKey;
    }

    protected List getSubComponents()
    {
        return this.field_111091_i;
    }

    public ChatMessageComponent appendComponent(ChatMessageComponent par1ChatMessageComponent)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(par1ChatMessageComponent);
            }
            else
            {
                this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {par1ChatMessageComponent});
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), par1ChatMessageComponent});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    public ChatMessageComponent addText(String par1Str)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(createFromText(par1Str));
            }
            else
            {
                this.text = par1Str;
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), createFromText(par1Str)});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    /**
     * Appends a translated string.
     */
    public ChatMessageComponent addKey(String par1Str)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(createFromTranslationKey(par1Str));
            }
            else
            {
                this.translationKey = par1Str;
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), createFromTranslationKey(par1Str)});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    /**
     * Appends a formatted translation key. Args: key, params. The text ultimately displayed is
     * String.format(translate(key), params)
     */
    public ChatMessageComponent addFormatted(String par1Str, Object ... par2ArrayOfObj)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(createFromTranslationWithSubstitutions(par1Str, par2ArrayOfObj));
            }
            else
            {
                this.translationKey = par1Str;
                this.field_111091_i = Lists.newArrayList();
                Object[] var3 = par2ArrayOfObj;
                int var4 = par2ArrayOfObj.length;

                for (int var5 = 0; var5 < var4; ++var5)
                {
                    Object var6 = var3[var5];

                    if (var6 instanceof ChatMessageComponent)
                    {
                        this.field_111091_i.add((ChatMessageComponent)var6);
                    }
                    else
                    {
                        this.field_111091_i.add(createFromText(var6.toString()));
                    }
                }
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), createFromTranslationWithSubstitutions(par1Str, par2ArrayOfObj)});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    public String toString()
    {
        return this.toStringWithFormatting(false);
    }

    public String toStringWithFormatting(boolean par1)
    {
        return this.toStringWithDefaultFormatting(par1, (EnumChatFormatting)null, false, false, false, false);
    }

    /**
     * args: enableFormat, defaultColor, defaultBold, defaultItalic, defaultUnderline, defaultObfuscated
     */
    public String toStringWithDefaultFormatting(boolean par1, EnumChatFormatting par2EnumChatFormatting, boolean par3, boolean par4, boolean par5, boolean par6)
    {
        StringBuilder var7 = new StringBuilder();
        EnumChatFormatting var8 = this.color == null ? par2EnumChatFormatting : this.color;
        boolean var9 = this.bold == null ? par3 : this.bold.booleanValue();
        boolean var10 = this.italic == null ? par4 : this.italic.booleanValue();
        boolean var11 = this.underline == null ? par5 : this.underline.booleanValue();
        boolean var12 = this.obfuscated == null ? par6 : this.obfuscated.booleanValue();

        if (this.translationKey != null)
        {
            if (par1)
            {
                appendFormattingToString(var7, var8, var9, var10, var11, var12);
            }

            if (this.field_111091_i != null)
            {
                String[] var13 = new String[this.field_111091_i.size()];

                for (int var14 = 0; var14 < this.field_111091_i.size(); ++var14)
                {
                    var13[var14] = ((ChatMessageComponent)this.field_111091_i.get(var14)).toStringWithDefaultFormatting(par1, var8, var9, var10, var11, var12);
                }

                var7.append(StatCollector.translateToLocalFormatted(this.translationKey, var13));
            }
            else
            {
                var7.append(StatCollector.translateToLocal(this.translationKey));
            }
        }
        else if (this.text != null)
        {
            if (par1)
            {
                appendFormattingToString(var7, var8, var9, var10, var11, var12);
            }

            var7.append(this.text);
        }
        else
        {
            ChatMessageComponent var16;

            if (this.field_111091_i != null)
            {
                for (Iterator var15 = this.field_111091_i.iterator(); var15.hasNext(); var7.append(var16.toStringWithDefaultFormatting(par1, var8, var9, var10, var11, var12)))
                {
                    var16 = (ChatMessageComponent)var15.next();

                    if (par1)
                    {
                        appendFormattingToString(var7, var8, var9, var10, var11, var12);
                    }
                }
            }
        }

        return var7.toString();
    }

    private static void appendFormattingToString(StringBuilder par0StringBuilder, EnumChatFormatting par1EnumChatFormatting, boolean par2, boolean par3, boolean par4, boolean par5)
    {
        if (par1EnumChatFormatting != null)
        {
            par0StringBuilder.append(par1EnumChatFormatting);
        }
        else if (par2 || par3 || par4 || par5)
        {
            par0StringBuilder.append(EnumChatFormatting.RESET);
        }

        if (par2)
        {
            par0StringBuilder.append(EnumChatFormatting.BOLD);
        }

        if (par3)
        {
            par0StringBuilder.append(EnumChatFormatting.ITALIC);
        }

        if (par4)
        {
            par0StringBuilder.append(EnumChatFormatting.UNDERLINE);
        }

        if (par5)
        {
            par0StringBuilder.append(EnumChatFormatting.OBFUSCATED);
        }
    }

    public static ChatMessageComponent createFromJson(String par0Str)
    {
        try
        {
            return (ChatMessageComponent)field_111089_a.fromJson(par0Str, ChatMessageComponent.class);
        }
        catch (Throwable var4)
        {
            CrashReport var2 = CrashReport.makeCrashReport(var4, "Deserializing Message");
            CrashReportCategory var3 = var2.makeCategory("Serialized Message");
            var3.addCrashSection("JSON string", par0Str);
            throw new ReportedException(var2);
        }
    }

    public static ChatMessageComponent createFromText(String par0Str)
    {
        ChatMessageComponent var1 = new ChatMessageComponent();
        var1.addText(par0Str);
        return var1;
    }

    public static ChatMessageComponent createFromTranslationKey(String par0Str)
    {
        ChatMessageComponent var1 = new ChatMessageComponent();
        var1.addKey(par0Str);
        return var1;
    }

    public static ChatMessageComponent createFromTranslationWithSubstitutions(String par0Str, Object ... par1ArrayOfObj)
    {
        ChatMessageComponent var2 = new ChatMessageComponent();
        var2.addFormatted(par0Str, par1ArrayOfObj);
        return var2;
    }

    public String toJson()
    {
        return field_111089_a.toJson(this);
    }
}
