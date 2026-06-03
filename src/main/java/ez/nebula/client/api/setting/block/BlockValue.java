package ez.nebula.client.api.setting.block;

import net.minecraft.block.Block;

/**
 * @author xgraza
 * @since 6/2/26
 */
public final class BlockValue
{
    private final Block block;
    private final int subType;

    public BlockValue(Block block, int subType)
    {
        this.block = block;
        this.subType = subType;
    }

    public Block getBlock()
    {
        return block;
    }

    public int getSubType()
    {
        return subType;
    }
}
