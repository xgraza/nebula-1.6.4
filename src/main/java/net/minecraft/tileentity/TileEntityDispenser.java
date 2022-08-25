package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntity implements IInventory
{
    private ItemStack[] field_146022_i = new ItemStack[9];
    private Random field_146021_j = new Random();
    protected String field_146020_a;
    private static final String __OBFID = "CL_00000352";

    public int getSizeInventory()
    {
        return 9;
    }

    public ItemStack getStackInSlot(int par1)
    {
        return this.field_146022_i[par1];
    }

    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.field_146022_i[par1] != null)
        {
            ItemStack var3;

            if (this.field_146022_i[par1].stackSize <= par2)
            {
                var3 = this.field_146022_i[par1];
                this.field_146022_i[par1] = null;
                this.onInventoryChanged();
                return var3;
            }
            else
            {
                var3 = this.field_146022_i[par1].splitStack(par2);

                if (this.field_146022_i[par1].stackSize == 0)
                {
                    this.field_146022_i[par1] = null;
                }

                this.onInventoryChanged();
                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.field_146022_i[par1] != null)
        {
            ItemStack var2 = this.field_146022_i[par1];
            this.field_146022_i[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    public int func_146017_i()
    {
        int var1 = -1;
        int var2 = 1;

        for (int var3 = 0; var3 < this.field_146022_i.length; ++var3)
        {
            if (this.field_146022_i[var3] != null && this.field_146021_j.nextInt(var2++) == 0)
            {
                var1 = var3;
            }
        }

        return var1;
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.field_146022_i[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    public int func_146019_a(ItemStack p_146019_1_)
    {
        for (int var2 = 0; var2 < this.field_146022_i.length; ++var2)
        {
            if (this.field_146022_i[var2] == null || this.field_146022_i[var2].getItem() == null)
            {
                this.setInventorySlotContents(var2, p_146019_1_);
                return var2;
            }
        }

        return -1;
    }

    public String getInventoryName()
    {
        return this.isInventoryNameLocalized() ? this.field_146020_a : "container.dispenser";
    }

    public void func_146018_a(String p_146018_1_)
    {
        this.field_146020_a = p_146018_1_;
    }

    public boolean isInventoryNameLocalized()
    {
        return this.field_146020_a != null;
    }

    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        super.readFromNBT(p_145839_1_);
        NBTTagList var2 = p_145839_1_.getTagList("Items", 10);
        this.field_146022_i = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < this.field_146022_i.length)
            {
                this.field_146022_i[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        if (p_145839_1_.hasKey("CustomName", 8))
        {
            this.field_146020_a = p_145839_1_.getString("CustomName");
        }
    }

    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        super.writeToNBT(p_145841_1_);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.field_146022_i.length; ++var3)
        {
            if (this.field_146022_i[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.field_146022_i[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        p_145841_1_.setTag("Items", var2);

        if (this.isInventoryNameLocalized())
        {
            p_145841_1_.setString("CustomName", this.field_146020_a);
        }
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openInventory() {}

    public void closeInventory() {}

    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }
}
