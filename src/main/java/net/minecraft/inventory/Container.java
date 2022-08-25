package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public abstract class Container
{
    public List inventoryItemStacks = new ArrayList();
    public List inventorySlots = new ArrayList();
    public int windowId;
    private short transactionID;
    private int field_94535_f = -1;
    private int field_94536_g;
    private final Set field_94537_h = new HashSet();
    protected List crafters = new ArrayList();
    private Set playerList = new HashSet();
    private static final String __OBFID = "CL_00001730";

    protected Slot addSlotToContainer(Slot par1Slot)
    {
        par1Slot.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(par1Slot);
        this.inventoryItemStacks.add((Object)null);
        return par1Slot;
    }

    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        if (this.crafters.contains(par1ICrafting))
        {
            throw new IllegalArgumentException("Listener already listening");
        }
        else
        {
            this.crafters.add(par1ICrafting);
            par1ICrafting.sendContainerAndContentsToPlayer(this, this.getInventory());
            this.detectAndSendChanges();
        }
    }

    public void removeCraftingFromCrafters(ICrafting par1ICrafting)
    {
        this.crafters.remove(par1ICrafting);
    }

    public List getInventory()
    {
        ArrayList var1 = new ArrayList();

        for (int var2 = 0; var2 < this.inventorySlots.size(); ++var2)
        {
            var1.add(((Slot)this.inventorySlots.get(var2)).getStack());
        }

        return var1;
    }

    public void detectAndSendChanges()
    {
        for (int var1 = 0; var1 < this.inventorySlots.size(); ++var1)
        {
            ItemStack var2 = ((Slot)this.inventorySlots.get(var1)).getStack();
            ItemStack var3 = (ItemStack)this.inventoryItemStacks.get(var1);

            if (!ItemStack.areItemStacksEqual(var3, var2))
            {
                var3 = var2 == null ? null : var2.copy();
                this.inventoryItemStacks.set(var1, var3);

                for (int var4 = 0; var4 < this.crafters.size(); ++var4)
                {
                    ((ICrafting)this.crafters.get(var4)).sendSlotContents(this, var1, var3);
                }
            }
        }
    }

    public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2)
    {
        return false;
    }

    public Slot getSlotFromInventory(IInventory par1IInventory, int par2)
    {
        for (int var3 = 0; var3 < this.inventorySlots.size(); ++var3)
        {
            Slot var4 = (Slot)this.inventorySlots.get(var3);

            if (var4.isSlotInInventory(par1IInventory, par2))
            {
                return var4;
            }
        }

        return null;
    }

    public Slot getSlot(int par1)
    {
        return (Slot)this.inventorySlots.get(par1);
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        Slot var3 = (Slot)this.inventorySlots.get(par2);
        return var3 != null ? var3.getStack() : null;
    }

    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
    {
        ItemStack var5 = null;
        InventoryPlayer var6 = par4EntityPlayer.inventory;
        int var9;
        ItemStack var17;

        if (par3 == 5)
        {
            int var7 = this.field_94536_g;
            this.field_94536_g = func_94532_c(par2);

            if ((var7 != 1 || this.field_94536_g != 2) && var7 != this.field_94536_g)
            {
                this.func_94533_d();
            }
            else if (var6.getItemStack() == null)
            {
                this.func_94533_d();
            }
            else if (this.field_94536_g == 0)
            {
                this.field_94535_f = func_94529_b(par2);

                if (func_94528_d(this.field_94535_f))
                {
                    this.field_94536_g = 1;
                    this.field_94537_h.clear();
                }
                else
                {
                    this.func_94533_d();
                }
            }
            else if (this.field_94536_g == 1)
            {
                Slot var8 = (Slot)this.inventorySlots.get(par1);

                if (var8 != null && func_94527_a(var8, var6.getItemStack(), true) && var8.isItemValid(var6.getItemStack()) && var6.getItemStack().stackSize > this.field_94537_h.size() && this.canDragIntoSlot(var8))
                {
                    this.field_94537_h.add(var8);
                }
            }
            else if (this.field_94536_g == 2)
            {
                if (!this.field_94537_h.isEmpty())
                {
                    var17 = var6.getItemStack().copy();
                    var9 = var6.getItemStack().stackSize;
                    Iterator var10 = this.field_94537_h.iterator();

                    while (var10.hasNext())
                    {
                        Slot var11 = (Slot)var10.next();

                        if (var11 != null && func_94527_a(var11, var6.getItemStack(), true) && var11.isItemValid(var6.getItemStack()) && var6.getItemStack().stackSize >= this.field_94537_h.size() && this.canDragIntoSlot(var11))
                        {
                            ItemStack var12 = var17.copy();
                            int var13 = var11.getHasStack() ? var11.getStack().stackSize : 0;
                            func_94525_a(this.field_94537_h, this.field_94535_f, var12, var13);

                            if (var12.stackSize > var12.getMaxStackSize())
                            {
                                var12.stackSize = var12.getMaxStackSize();
                            }

                            if (var12.stackSize > var11.getSlotStackLimit())
                            {
                                var12.stackSize = var11.getSlotStackLimit();
                            }

                            var9 -= var12.stackSize - var13;
                            var11.putStack(var12);
                        }
                    }

                    var17.stackSize = var9;

                    if (var17.stackSize <= 0)
                    {
                        var17 = null;
                    }

                    var6.setItemStack(var17);
                }

                this.func_94533_d();
            }
            else
            {
                this.func_94533_d();
            }
        }
        else if (this.field_94536_g != 0)
        {
            this.func_94533_d();
        }
        else
        {
            Slot var16;
            int var19;
            ItemStack var23;

            if ((par3 == 0 || par3 == 1) && (par2 == 0 || par2 == 1))
            {
                if (par1 == -999)
                {
                    if (var6.getItemStack() != null && par1 == -999)
                    {
                        if (par2 == 0)
                        {
                            par4EntityPlayer.dropPlayerItemWithRandomChoice(var6.getItemStack(), true);
                            var6.setItemStack((ItemStack)null);
                        }

                        if (par2 == 1)
                        {
                            par4EntityPlayer.dropPlayerItemWithRandomChoice(var6.getItemStack().splitStack(1), true);

                            if (var6.getItemStack().stackSize == 0)
                            {
                                var6.setItemStack((ItemStack)null);
                            }
                        }
                    }
                }
                else if (par3 == 1)
                {
                    if (par1 < 0)
                    {
                        return null;
                    }

                    var16 = (Slot)this.inventorySlots.get(par1);

                    if (var16 != null && var16.canTakeStack(par4EntityPlayer))
                    {
                        var17 = this.transferStackInSlot(par4EntityPlayer, par1);

                        if (var17 != null)
                        {
                            Item var20 = var17.getItem();
                            var5 = var17.copy();

                            if (var16.getStack() != null && var16.getStack().getItem() == var20)
                            {
                                this.retrySlotClick(par1, par2, true, par4EntityPlayer);
                            }
                        }
                    }
                }
                else
                {
                    if (par1 < 0)
                    {
                        return null;
                    }

                    var16 = (Slot)this.inventorySlots.get(par1);

                    if (var16 != null)
                    {
                        var17 = var16.getStack();
                        ItemStack var21 = var6.getItemStack();

                        if (var17 != null)
                        {
                            var5 = var17.copy();
                        }

                        if (var17 == null)
                        {
                            if (var21 != null && var16.isItemValid(var21))
                            {
                                var19 = par2 == 0 ? var21.stackSize : 1;

                                if (var19 > var16.getSlotStackLimit())
                                {
                                    var19 = var16.getSlotStackLimit();
                                }

                                if (var21.stackSize >= var19)
                                {
                                    var16.putStack(var21.splitStack(var19));
                                }

                                if (var21.stackSize == 0)
                                {
                                    var6.setItemStack((ItemStack)null);
                                }
                            }
                        }
                        else if (var16.canTakeStack(par4EntityPlayer))
                        {
                            if (var21 == null)
                            {
                                var19 = par2 == 0 ? var17.stackSize : (var17.stackSize + 1) / 2;
                                var23 = var16.decrStackSize(var19);
                                var6.setItemStack(var23);

                                if (var17.stackSize == 0)
                                {
                                    var16.putStack((ItemStack)null);
                                }

                                var16.onPickupFromSlot(par4EntityPlayer, var6.getItemStack());
                            }
                            else if (var16.isItemValid(var21))
                            {
                                if (var17.getItem() == var21.getItem() && var17.getItemDamage() == var21.getItemDamage() && ItemStack.areItemStackTagsEqual(var17, var21))
                                {
                                    var19 = par2 == 0 ? var21.stackSize : 1;

                                    if (var19 > var16.getSlotStackLimit() - var17.stackSize)
                                    {
                                        var19 = var16.getSlotStackLimit() - var17.stackSize;
                                    }

                                    if (var19 > var21.getMaxStackSize() - var17.stackSize)
                                    {
                                        var19 = var21.getMaxStackSize() - var17.stackSize;
                                    }

                                    var21.splitStack(var19);

                                    if (var21.stackSize == 0)
                                    {
                                        var6.setItemStack((ItemStack)null);
                                    }

                                    var17.stackSize += var19;
                                }
                                else if (var21.stackSize <= var16.getSlotStackLimit())
                                {
                                    var16.putStack(var21);
                                    var6.setItemStack(var17);
                                }
                            }
                            else if (var17.getItem() == var21.getItem() && var21.getMaxStackSize() > 1 && (!var17.getHasSubtypes() || var17.getItemDamage() == var21.getItemDamage()) && ItemStack.areItemStackTagsEqual(var17, var21))
                            {
                                var19 = var17.stackSize;

                                if (var19 > 0 && var19 + var21.stackSize <= var21.getMaxStackSize())
                                {
                                    var21.stackSize += var19;
                                    var17 = var16.decrStackSize(var19);

                                    if (var17.stackSize == 0)
                                    {
                                        var16.putStack((ItemStack)null);
                                    }

                                    var16.onPickupFromSlot(par4EntityPlayer, var6.getItemStack());
                                }
                            }
                        }

                        var16.onSlotChanged();
                    }
                }
            }
            else if (par3 == 2 && par2 >= 0 && par2 < 9)
            {
                var16 = (Slot)this.inventorySlots.get(par1);

                if (var16.canTakeStack(par4EntityPlayer))
                {
                    var17 = var6.getStackInSlot(par2);
                    boolean var18 = var17 == null || var16.inventory == var6 && var16.isItemValid(var17);
                    var19 = -1;

                    if (!var18)
                    {
                        var19 = var6.getFirstEmptyStack();
                        var18 |= var19 > -1;
                    }

                    if (var16.getHasStack() && var18)
                    {
                        var23 = var16.getStack();
                        var6.setInventorySlotContents(par2, var23.copy());

                        if ((var16.inventory != var6 || !var16.isItemValid(var17)) && var17 != null)
                        {
                            if (var19 > -1)
                            {
                                var6.addItemStackToInventory(var17);
                                var16.decrStackSize(var23.stackSize);
                                var16.putStack((ItemStack)null);
                                var16.onPickupFromSlot(par4EntityPlayer, var23);
                            }
                        }
                        else
                        {
                            var16.decrStackSize(var23.stackSize);
                            var16.putStack(var17);
                            var16.onPickupFromSlot(par4EntityPlayer, var23);
                        }
                    }
                    else if (!var16.getHasStack() && var17 != null && var16.isItemValid(var17))
                    {
                        var6.setInventorySlotContents(par2, (ItemStack)null);
                        var16.putStack(var17);
                    }
                }
            }
            else if (par3 == 3 && par4EntityPlayer.capabilities.isCreativeMode && var6.getItemStack() == null && par1 >= 0)
            {
                var16 = (Slot)this.inventorySlots.get(par1);

                if (var16 != null && var16.getHasStack())
                {
                    var17 = var16.getStack().copy();
                    var17.stackSize = var17.getMaxStackSize();
                    var6.setItemStack(var17);
                }
            }
            else if (par3 == 4 && var6.getItemStack() == null && par1 >= 0)
            {
                var16 = (Slot)this.inventorySlots.get(par1);

                if (var16 != null && var16.getHasStack() && var16.canTakeStack(par4EntityPlayer))
                {
                    var17 = var16.decrStackSize(par2 == 0 ? 1 : var16.getStack().stackSize);
                    var16.onPickupFromSlot(par4EntityPlayer, var17);
                    par4EntityPlayer.dropPlayerItemWithRandomChoice(var17, true);
                }
            }
            else if (par3 == 6 && par1 >= 0)
            {
                var16 = (Slot)this.inventorySlots.get(par1);
                var17 = var6.getItemStack();

                if (var17 != null && (var16 == null || !var16.getHasStack() || !var16.canTakeStack(par4EntityPlayer)))
                {
                    var9 = par2 == 0 ? 0 : this.inventorySlots.size() - 1;
                    var19 = par2 == 0 ? 1 : -1;

                    for (int var22 = 0; var22 < 2; ++var22)
                    {
                        for (int var24 = var9; var24 >= 0 && var24 < this.inventorySlots.size() && var17.stackSize < var17.getMaxStackSize(); var24 += var19)
                        {
                            Slot var25 = (Slot)this.inventorySlots.get(var24);

                            if (var25.getHasStack() && func_94527_a(var25, var17, true) && var25.canTakeStack(par4EntityPlayer) && this.func_94530_a(var17, var25) && (var22 != 0 || var25.getStack().stackSize != var25.getStack().getMaxStackSize()))
                            {
                                int var14 = Math.min(var17.getMaxStackSize() - var17.stackSize, var25.getStack().stackSize);
                                ItemStack var15 = var25.decrStackSize(var14);
                                var17.stackSize += var14;

                                if (var15.stackSize <= 0)
                                {
                                    var25.putStack((ItemStack)null);
                                }

                                var25.onPickupFromSlot(par4EntityPlayer, var15);
                            }
                        }
                    }
                }

                this.detectAndSendChanges();
            }
        }

        return var5;
    }

    public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
    {
        return true;
    }

    protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer)
    {
        this.slotClick(par1, par2, 1, par4EntityPlayer);
    }

    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        InventoryPlayer var2 = par1EntityPlayer.inventory;

        if (var2.getItemStack() != null)
        {
            par1EntityPlayer.dropPlayerItemWithRandomChoice(var2.getItemStack(), false);
            var2.setItemStack((ItemStack)null);
        }
    }

    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        this.detectAndSendChanges();
    }

    public void putStackInSlot(int par1, ItemStack par2ItemStack)
    {
        this.getSlot(par1).putStack(par2ItemStack);
    }

    public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack)
    {
        for (int var2 = 0; var2 < par1ArrayOfItemStack.length; ++var2)
        {
            this.getSlot(var2).putStack(par1ArrayOfItemStack[var2]);
        }
    }

    public void updateProgressBar(int par1, int par2) {}

    public short getNextTransactionID(InventoryPlayer par1InventoryPlayer)
    {
        ++this.transactionID;
        return this.transactionID;
    }

    public boolean isPlayerNotUsingContainer(EntityPlayer par1EntityPlayer)
    {
        return !this.playerList.contains(par1EntityPlayer);
    }

    public void setPlayerIsPresent(EntityPlayer par1EntityPlayer, boolean par2)
    {
        if (par2)
        {
            this.playerList.remove(par1EntityPlayer);
        }
        else
        {
            this.playerList.add(par1EntityPlayer);
        }
    }

    public abstract boolean canInteractWith(EntityPlayer var1);

    protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4)
    {
        boolean var5 = false;
        int var6 = par2;

        if (par4)
        {
            var6 = par3 - 1;
        }

        Slot var7;
        ItemStack var8;

        if (par1ItemStack.isStackable())
        {
            while (par1ItemStack.stackSize > 0 && (!par4 && var6 < par3 || par4 && var6 >= par2))
            {
                var7 = (Slot)this.inventorySlots.get(var6);
                var8 = var7.getStack();

                if (var8 != null && var8.getItem() == par1ItemStack.getItem() && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == var8.getItemDamage()) && ItemStack.areItemStackTagsEqual(par1ItemStack, var8))
                {
                    int var9 = var8.stackSize + par1ItemStack.stackSize;

                    if (var9 <= par1ItemStack.getMaxStackSize())
                    {
                        par1ItemStack.stackSize = 0;
                        var8.stackSize = var9;
                        var7.onSlotChanged();
                        var5 = true;
                    }
                    else if (var8.stackSize < par1ItemStack.getMaxStackSize())
                    {
                        par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - var8.stackSize;
                        var8.stackSize = par1ItemStack.getMaxStackSize();
                        var7.onSlotChanged();
                        var5 = true;
                    }
                }

                if (par4)
                {
                    --var6;
                }
                else
                {
                    ++var6;
                }
            }
        }

        if (par1ItemStack.stackSize > 0)
        {
            if (par4)
            {
                var6 = par3 - 1;
            }
            else
            {
                var6 = par2;
            }

            while (!par4 && var6 < par3 || par4 && var6 >= par2)
            {
                var7 = (Slot)this.inventorySlots.get(var6);
                var8 = var7.getStack();

                if (var8 == null)
                {
                    var7.putStack(par1ItemStack.copy());
                    var7.onSlotChanged();
                    par1ItemStack.stackSize = 0;
                    var5 = true;
                    break;
                }

                if (par4)
                {
                    --var6;
                }
                else
                {
                    ++var6;
                }
            }
        }

        return var5;
    }

    public static int func_94529_b(int par0)
    {
        return par0 >> 2 & 3;
    }

    public static int func_94532_c(int par0)
    {
        return par0 & 3;
    }

    public static int func_94534_d(int par0, int par1)
    {
        return par0 & 3 | (par1 & 3) << 2;
    }

    public static boolean func_94528_d(int par0)
    {
        return par0 == 0 || par0 == 1;
    }

    protected void func_94533_d()
    {
        this.field_94536_g = 0;
        this.field_94537_h.clear();
    }

    public static boolean func_94527_a(Slot par0Slot, ItemStack par1ItemStack, boolean par2)
    {
        boolean var3 = par0Slot == null || !par0Slot.getHasStack();

        if (par0Slot != null && par0Slot.getHasStack() && par1ItemStack != null && par1ItemStack.isItemEqual(par0Slot.getStack()) && ItemStack.areItemStackTagsEqual(par0Slot.getStack(), par1ItemStack))
        {
            int var10002 = par2 ? 0 : par1ItemStack.stackSize;
            var3 |= par0Slot.getStack().stackSize + var10002 <= par1ItemStack.getMaxStackSize();
        }

        return var3;
    }

    public static void func_94525_a(Set par0Set, int par1, ItemStack par2ItemStack, int par3)
    {
        switch (par1)
        {
            case 0:
                par2ItemStack.stackSize = MathHelper.floor_float((float)par2ItemStack.stackSize / (float)par0Set.size());
                break;

            case 1:
                par2ItemStack.stackSize = 1;
        }

        par2ItemStack.stackSize += par3;
    }

    public boolean canDragIntoSlot(Slot par1Slot)
    {
        return true;
    }

    public static int calcRedstoneFromInventory(IInventory par0IInventory)
    {
        if (par0IInventory == null)
        {
            return 0;
        }
        else
        {
            int var1 = 0;
            float var2 = 0.0F;

            for (int var3 = 0; var3 < par0IInventory.getSizeInventory(); ++var3)
            {
                ItemStack var4 = par0IInventory.getStackInSlot(var3);

                if (var4 != null)
                {
                    var2 += (float)var4.stackSize / (float)Math.min(par0IInventory.getInventoryStackLimit(), var4.getMaxStackSize());
                    ++var1;
                }
            }

            var2 /= (float)par0IInventory.getSizeInventory();
            return MathHelper.floor_float(var2 * 14.0F) + (var1 > 0 ? 1 : 0);
        }
    }
}
