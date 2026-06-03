package ez.nebula.client.api.setting;

import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author xgraza
 * @since 6/2/26
 */
public final class BlockSetting extends Setting<Block>
{
    private int subType;

    public BlockSetting(String name, String description, Predicate<Block> visibility, Consumer<Block> valueChanged, Block value, final int subType)
    {
        super(name, description, visibility, valueChanged, value);
        this.subType = subType;
    }

    public void setSubType(int subType)
    {
        if (!BlockUtil.blockHasSubType(getValue()))
        {
            this.subType = 0;
            return;
        }
        this.subType = subType;
    }

    public int getSubType()
    {
        return subType;
    }

    public static final class Builder extends Setting.Builder<Block>
    {
        private int subType;

        public Builder(String name, Block value)
        {
            super(name, value);
        }

        public Builder setSubType(int subType)
        {
            if (!BlockUtil.blockHasSubType(value))
            {
                return this;
            }
            this.subType = subType;
            return this;
        }

        @Override
        public BlockSetting build()
        {
            return new BlockSetting(name, description, visibility, valueChanged, value, subType);
        }
    }
}
