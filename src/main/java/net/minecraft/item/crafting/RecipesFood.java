package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesFood
{
    private static final String __OBFID = "CL_00000084";

    /**
     * Adds the food recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        par1CraftingManager.addShapelessRecipe(new ItemStack(Items.mushroom_stew), Blocks.brown_mushroom, Blocks.red_mushroom, Items.bowl);
        par1CraftingManager.addRecipe(new ItemStack(Items.cookie, 8), "#X#", 'X', new ItemStack(Items.dye, 1, 3), '#', Items.wheat);
        par1CraftingManager.addRecipe(new ItemStack(Blocks.melon_block), "MMM", "MMM", "MMM", 'M', Items.melon);
        par1CraftingManager.addRecipe(new ItemStack(Items.melon_seeds), "M", 'M', Items.melon);
        par1CraftingManager.addRecipe(new ItemStack(Items.pumpkin_seeds, 4), "M", 'M', Blocks.pumpkin);
        par1CraftingManager.addShapelessRecipe(new ItemStack(Items.pumpkin_pie), Blocks.pumpkin, Items.sugar, Items.egg);
        par1CraftingManager.addShapelessRecipe(new ItemStack(Items.fermented_spider_eye), Items.spider_eye, Blocks.brown_mushroom, Items.sugar);
        par1CraftingManager.addShapelessRecipe(new ItemStack(Items.blaze_powder, 2), Items.blaze_rod);
        par1CraftingManager.addShapelessRecipe(new ItemStack(Items.magma_cream), Items.blaze_powder, Items.slime_ball);
    }
}
