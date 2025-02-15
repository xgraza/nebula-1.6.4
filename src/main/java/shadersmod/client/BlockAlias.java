package shadersmod.client;

import java.util.ArrayList;
import java.util.HashSet;
import net.minecraft.src.Config;
import net.minecraft.src.MatchBlock;

public class BlockAlias
{
    private int blockId;
    private MatchBlock[] matchBlocks;

    public BlockAlias(int blockId, MatchBlock[] matchBlocks)
    {
        this.blockId = blockId;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockId()
    {
        return this.blockId;
    }

    public boolean matches(int id, int metadata)
    {
        for (int i = 0; i < this.matchBlocks.length; ++i)
        {
            MatchBlock matchBlock = this.matchBlocks[i];

            if (matchBlock.matches(id, metadata))
            {
                return true;
            }
        }

        return false;
    }

    public int[] getMatchBlockIds()
    {
        HashSet blockIdSet = new HashSet();

        for (int blockIdsArr = 0; blockIdsArr < this.matchBlocks.length; ++blockIdsArr)
        {
            MatchBlock blockIds = this.matchBlocks[blockIdsArr];
            int blockId = blockIds.getBlockId();
            blockIdSet.add(Integer.valueOf(blockId));
        }

        Integer[] var5 = (Integer[])blockIdSet.toArray(new Integer[blockIdSet.size()]);
        int[] var6 = Config.toPrimitive(var5);
        return var6;
    }

    public MatchBlock[] getMatchBlocks(int matchBlockId)
    {
        ArrayList listMatchBlock = new ArrayList();

        for (int mbs = 0; mbs < this.matchBlocks.length; ++mbs)
        {
            MatchBlock mb = this.matchBlocks[mbs];

            if (mb.getBlockId() == matchBlockId)
            {
                listMatchBlock.add(mb);
            }
        }

        MatchBlock[] var5 = (MatchBlock[])((MatchBlock[])listMatchBlock.toArray(new MatchBlock[listMatchBlock.size()]));
        return var5;
    }

    public String toString()
    {
        return "block." + this.blockId + "=" + Config.arrayToString((Object[])this.matchBlocks);
    }
}
