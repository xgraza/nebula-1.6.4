package net.minecraft.util;

import java.util.Iterator;

public class ChatComponentText extends ChatComponentStyle
{
    public String text;
    private static final String __OBFID = "CL_00001269";

    public ChatComponentText(String p_i45159_1_)
    {
        this.text = p_i45159_1_;
    }

    public String getChatComponentText_TextValue()
    {
        return this.text;
    }

    public String getUnformattedTextForChat()
    {
        return this.text;
    }

    public ChatComponentText createCopy()
    {
        ChatComponentText var1 = new ChatComponentText(this.text);
        var1.setChatStyle(this.getChatStyle().createShallowCopy());
        Iterator var2 = this.getSiblings().iterator();

        while (var2.hasNext())
        {
            IChatComponent var3 = (IChatComponent)var2.next();
            var1.appendSibling(var3.createCopy());
        }

        return var1;
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ChatComponentText))
        {
            return false;
        }
        else
        {
            ChatComponentText var2 = (ChatComponentText)par1Obj;
            return this.text.equals(var2.getChatComponentText_TextValue()) && super.equals(par1Obj);
        }
    }

    public String toString()
    {
        return "TextComponent{text=\'" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }
}
