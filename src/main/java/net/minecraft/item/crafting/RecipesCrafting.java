package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesCrafting
{
    private static final String __OBFID = "CL_00000095";

    /**
     * Adds the crafting recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        par1CraftingManager.addRecipe(new ItemStack(Blocks.chest), "###", "# #", "###", '#', Blocks.planks);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.trapped_chest), "#-", '#', Blocks.chest, '-', Blocks.tripwire_hook);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.ender_chest), "###", "#E#", "###", '#', Blocks.obsidian, 'E', Items.ender_eye);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.furnace), "###", "# #", "###", '#', Blocks.cobblestone);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.crafting_table), "##", "##", '#', Blocks.planks);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone), "##", "##", '#', new ItemStack(Blocks.sand, 1, 0));
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone, 4, 2), "##", "##", '#', Blocks.sandstone);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone, 1, 1), "#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 1));
        par1CraftingManager.addRecipe(new ItemStack(Blocks.quartz_block, 1, 1), "#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 7));
        par1CraftingManager.addRecipe(new ItemStack(Blocks.quartz_block, 2, 2), "#", "#", '#', new ItemStack(Blocks.quartz_block, 1, 0));
        par1CraftingManager.addRecipe(new ItemStack(Blocks.stonebrick, 4), "##", "##", '#', Blocks.stone);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.iron_bars, 16), "###", "###", '#', Items.iron_ingot);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.glass_pane, 16), "###", "###", '#', Blocks.glass);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.redstone_lamp, 1), " R ", "RGR", " R ", 'R', Items.redstone, 'G', Blocks.glowstone);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.beacon, 1), "GGG", "GSG", "OOO", 'G', Blocks.glass, 'S', Items.nether_star, 'O', Blocks.obsidian);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.nether_brick, 1), "NN", "NN", 'N', Items.netherbrick);
    }
}
