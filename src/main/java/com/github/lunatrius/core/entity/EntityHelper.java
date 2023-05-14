//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.entity;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import com.github.lunatrius.core.util.vector.*;

public class EntityHelper
{
    public static int getItemCountInInventory(final IInventory inventory, final Item item) {
        return getItemCountInInventory(inventory, item, -1);
    }
    
    public static int getItemCountInInventory(final IInventory inventory, final Item item, final int itemDamage) {
        final int inventorySize = inventory.getSizeInventory();
        int count = 0;
        for (int slot = 0; slot < inventorySize; ++slot) {
            final ItemStack itemStack = inventory.getStackInSlot(slot);
            if (itemStack != null && itemStack.getItem() == item && (itemDamage == -1 || itemDamage == itemStack.getItemDamage())) {
                count += itemStack.stackSize;
            }
        }
        return count;
    }
    
    public static Vector3f getVector3fFromEntity(final Entity entity) {
        return new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
    }
    
    public static Vector3i getVector3iFromEntity(final Entity entity) {
        return new Vector3i((int)Math.floor(entity.posX), (int)Math.floor(entity.posY), (int)Math.floor(entity.posZ));
    }
}
