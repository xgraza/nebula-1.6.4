package net.minecraft.entity.monster;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals
{
    IEntitySelector mobSelector = new IEntitySelector()
    {
        private static final String __OBFID = "CL_00001688";
        public boolean isEntityApplicable(Entity par1Entity)
        {
            return par1Entity instanceof IMob;
        }
    };
}
