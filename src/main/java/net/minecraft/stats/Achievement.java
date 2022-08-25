package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class Achievement extends StatBase
{
    public final int displayColumn;
    public final int displayRow;
    public final Achievement parentAchievement;
    private final String achievementDescription;
    private IStatStringFormat statStringFormatter;
    public final ItemStack theItemStack;
    private boolean isSpecial;
    private static final String __OBFID = "CL_00001466";

    public Achievement(String p_i45300_1_, String p_i45300_2_, int p_i45300_3_, int p_i45300_4_, Item p_i45300_5_, Achievement p_i45300_6_)
    {
        this(p_i45300_1_, p_i45300_2_, p_i45300_3_, p_i45300_4_, new ItemStack(p_i45300_5_), p_i45300_6_);
    }

    public Achievement(String p_i45301_1_, String p_i45301_2_, int p_i45301_3_, int p_i45301_4_, Block p_i45301_5_, Achievement p_i45301_6_)
    {
        this(p_i45301_1_, p_i45301_2_, p_i45301_3_, p_i45301_4_, new ItemStack(p_i45301_5_), p_i45301_6_);
    }

    public Achievement(String p_i45302_1_, String p_i45302_2_, int p_i45302_3_, int p_i45302_4_, ItemStack p_i45302_5_, Achievement p_i45302_6_)
    {
        super(p_i45302_1_, new ChatComponentTranslation("achievement." + p_i45302_2_, new Object[0]));
        this.theItemStack = p_i45302_5_;
        this.achievementDescription = "achievement." + p_i45302_2_ + ".desc";
        this.displayColumn = p_i45302_3_;
        this.displayRow = p_i45302_4_;

        if (p_i45302_3_ < AchievementList.minDisplayColumn)
        {
            AchievementList.minDisplayColumn = p_i45302_3_;
        }

        if (p_i45302_4_ < AchievementList.minDisplayRow)
        {
            AchievementList.minDisplayRow = p_i45302_4_;
        }

        if (p_i45302_3_ > AchievementList.maxDisplayColumn)
        {
            AchievementList.maxDisplayColumn = p_i45302_3_;
        }

        if (p_i45302_4_ > AchievementList.maxDisplayRow)
        {
            AchievementList.maxDisplayRow = p_i45302_4_;
        }

        this.parentAchievement = p_i45302_6_;
    }

    public Achievement initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    public Achievement setSpecial()
    {
        this.isSpecial = true;
        return this;
    }

    public Achievement registerStat()
    {
        super.registerStat();
        AchievementList.achievementList.add(this);
        return this;
    }

    public boolean isAchievement()
    {
        return true;
    }

    public IChatComponent func_150951_e()
    {
        IChatComponent var1 = super.func_150951_e();
        var1.getChatStyle().setColor(this.getSpecial() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
        return var1;
    }

    public Achievement func_150953_b(Class p_150958_1_)
    {
        return (Achievement)super.func_150953_b(p_150958_1_);
    }

    public String getDescription()
    {
        return this.statStringFormatter != null ? this.statStringFormatter.formatString(StatCollector.translateToLocal(this.achievementDescription)) : StatCollector.translateToLocal(this.achievementDescription);
    }

    public Achievement setStatStringFormatter(IStatStringFormat par1IStatStringFormat)
    {
        this.statStringFormatter = par1IStatStringFormat;
        return this;
    }

    public boolean getSpecial()
    {
        return this.isSpecial;
    }
}
