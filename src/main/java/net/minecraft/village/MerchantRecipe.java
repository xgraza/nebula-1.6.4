package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MerchantRecipe
{
    private ItemStack itemToBuy;
    private ItemStack secondItemToBuy;
    private ItemStack itemToSell;
    private int toolUses;
    private int maxTradeUses;
    private static final String __OBFID = "CL_00000126";

    public MerchantRecipe(NBTTagCompound par1NBTTagCompound)
    {
        this.readFromTags(par1NBTTagCompound);
    }

    public MerchantRecipe(ItemStack par1ItemStack, ItemStack par2ItemStack, ItemStack par3ItemStack)
    {
        this.itemToBuy = par1ItemStack;
        this.secondItemToBuy = par2ItemStack;
        this.itemToSell = par3ItemStack;
        this.maxTradeUses = 7;
    }

    public MerchantRecipe(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        this(par1ItemStack, (ItemStack)null, par2ItemStack);
    }

    public MerchantRecipe(ItemStack par1ItemStack, Item par2Item)
    {
        this(par1ItemStack, new ItemStack(par2Item));
    }

    public ItemStack getItemToBuy()
    {
        return this.itemToBuy;
    }

    public ItemStack getSecondItemToBuy()
    {
        return this.secondItemToBuy;
    }

    public boolean hasSecondItemToBuy()
    {
        return this.secondItemToBuy != null;
    }

    public ItemStack getItemToSell()
    {
        return this.itemToSell;
    }

    public boolean hasSameIDsAs(MerchantRecipe par1MerchantRecipe)
    {
        return this.itemToBuy.getItem() == par1MerchantRecipe.itemToBuy.getItem() && this.itemToSell.getItem() == par1MerchantRecipe.itemToSell.getItem() ? this.secondItemToBuy == null && par1MerchantRecipe.secondItemToBuy == null || this.secondItemToBuy != null && par1MerchantRecipe.secondItemToBuy != null && this.secondItemToBuy.getItem() == par1MerchantRecipe.secondItemToBuy.getItem() : false;
    }

    public boolean hasSameItemsAs(MerchantRecipe par1MerchantRecipe)
    {
        return this.hasSameIDsAs(par1MerchantRecipe) && (this.itemToBuy.stackSize < par1MerchantRecipe.itemToBuy.stackSize || this.secondItemToBuy != null && this.secondItemToBuy.stackSize < par1MerchantRecipe.secondItemToBuy.stackSize);
    }

    public void incrementToolUses()
    {
        ++this.toolUses;
    }

    public void func_82783_a(int par1)
    {
        this.maxTradeUses += par1;
    }

    public boolean isRecipeDisabled()
    {
        return this.toolUses >= this.maxTradeUses;
    }

    public void func_82785_h()
    {
        this.toolUses = this.maxTradeUses;
    }

    public void readFromTags(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("buy");
        this.itemToBuy = ItemStack.loadItemStackFromNBT(var2);
        NBTTagCompound var3 = par1NBTTagCompound.getCompoundTag("sell");
        this.itemToSell = ItemStack.loadItemStackFromNBT(var3);

        if (par1NBTTagCompound.hasKey("buyB", 10))
        {
            this.secondItemToBuy = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("buyB"));
        }

        if (par1NBTTagCompound.hasKey("uses", 99))
        {
            this.toolUses = par1NBTTagCompound.getInteger("uses");
        }

        if (par1NBTTagCompound.hasKey("maxUses", 99))
        {
            this.maxTradeUses = par1NBTTagCompound.getInteger("maxUses");
        }
        else
        {
            this.maxTradeUses = 7;
        }
    }

    public NBTTagCompound writeToTags()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        var1.setTag("buy", this.itemToBuy.writeToNBT(new NBTTagCompound()));
        var1.setTag("sell", this.itemToSell.writeToNBT(new NBTTagCompound()));

        if (this.secondItemToBuy != null)
        {
            var1.setTag("buyB", this.secondItemToBuy.writeToNBT(new NBTTagCompound()));
        }

        var1.setInteger("uses", this.toolUses);
        var1.setInteger("maxUses", this.maxTradeUses);
        return var1;
    }
}
