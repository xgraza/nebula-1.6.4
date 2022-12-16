package wtf.nebula.client.impl.command.arg.impl;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import wtf.nebula.client.impl.command.arg.Argument;

public class BlockArgument extends Argument<Integer> {
    public BlockArgument(String name) {
        super(name, Integer.class);
    }

    @Override
    public boolean parse(String part) {
        Block block = Block.getBlockFromName(part);
        if (block == null || block.equals(Blocks.air)) {
            return false;
        }

        int id = Block.getIdFromBlock(block);
        if (id == Block.getIdFromBlock(Blocks.air)) {
            return false;
        }

        setValue(id);
        return true;
    }
}
