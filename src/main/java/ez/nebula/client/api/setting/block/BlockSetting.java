package ez.nebula.client.api.setting.block;

import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.block.Block;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author xgraza
 * @since 6/2/26
 */
public final class BlockSetting extends Setting<BlockValue>
{
    public BlockSetting(String name, String description, Predicate<BlockValue> visibility, Consumer<BlockValue> valueChanged, BlockValue value)
    {
        super(name, description, visibility, valueChanged, value);
    }

    public void setBlock(final Block block)
    {
        setValue(new BlockValue(block, getSubType()));
    }

    public void setSubType(final int subType)
    {
        setValue(new BlockValue(getBlock(), subType));
    }

    public Block getBlock()
    {
        return getValue().getBlock();
    }

    public int getSubType()
    {
        return getValue().getSubType();
    }

    public static final class Builder extends Setting.Builder<BlockValue>
    {
        private Block block;
        private int subType;

        public Builder(String name, Block block)
        {
            super(name, null);
            this.block = block;
        }

        public Builder setBlock(Block block)
        {
            this.block = block;
            return this;
        }

        public Builder setSubType(int subType)
        {
            if (!BlockUtil.blockHasSubType(block))
            {
                return this;
            }
            this.subType = subType;
            return this;
        }

        @Override
        public BlockSetting build()
        {
            return new BlockSetting(name, description, visibility, valueChanged, new BlockValue(block, subType));
        }
    }
}
