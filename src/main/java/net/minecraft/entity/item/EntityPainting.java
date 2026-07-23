package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityPainting extends EntityHanging
{
    public EntityPainting.EnumArt art;

    public EntityPainting(World world)
    {
        super(world);
    }

    public EntityPainting(World world, int x, int y, int z, int direction)
    {
        super(world, x, y, z, direction);
        ArrayList<EnumArt> artList = new ArrayList<>();
        for (EnumArt art : EntityPainting.EnumArt.values())
        {
            this.art = art;
            this.setDirection(direction);
            if (this.onValidSurface())
            {
                artList.add(art);
            }
        }

        if (!artList.isEmpty())
        {
            this.art = artList.get(this.rand.nextInt(artList.size()));
        }

        this.setDirection(direction);
    }

    public EntityPainting(World world, int x, int y, int z, int direction, String title)
    {
        this(world, x, y, z, direction);
        for (EnumArt art : EntityPainting.EnumArt.values())
        {
            if (art.title.equals(title))
            {
                this.art = art;
                break;
            }
        }

        this.setDirection(direction);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setString("Motive", this.art.title);
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        String title = par1NBTTagCompound.getString("Motive");
        EntityPainting.EnumArt[] var3 = EntityPainting.EnumArt.values();
        for (EnumArt art : var3)
        {
            if (art.title.equals(title))
            {
                this.art = art;
            }
        }

        if (this.art == null)
        {
            this.art = EntityPainting.EnumArt.Kebab;
        }

        super.readEntityFromNBT(par1NBTTagCompound);
    }

    public int getWidthPixels()
    {
        return this.art.sizeX;
    }

    public int getHeightPixels()
    {
        return this.art.sizeY;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isCreativeMode)
            {
                return;
            }
        }

        this.entityDropItem(new ItemStack(Items.painting), 0.0F);
    }

    public enum EnumArt
    {
        Kebab("Kebab", 0, "Kebab", 16, 16, 0, 0),
        Aztec("Aztec", 1, "Aztec", 16, 16, 16, 0),
        Alban("Alban", 2, "Alban", 16, 16, 32, 0),
        Aztec2("Aztec2", 3, "Aztec2", 16, 16, 48, 0),
        Bomb("Bomb", 4, "Bomb", 16, 16, 64, 0),
        Plant("Plant", 5, "Plant", 16, 16, 80, 0),
        Wasteland("Wasteland", 6, "Wasteland", 16, 16, 96, 0),
        Pool("Pool", 7, "Pool", 32, 16, 0, 32),
        Courbet("Courbet", 8, "Courbet", 32, 16, 32, 32),
        Sea("Sea", 9, "Sea", 32, 16, 64, 32),
        Sunset("Sunset", 10, "Sunset", 32, 16, 96, 32),
        Creebet("Creebet", 11, "Creebet", 32, 16, 128, 32),
        Wanderer("Wanderer", 12, "Wanderer", 16, 32, 0, 64),
        Graham("Graham", 13, "Graham", 16, 32, 16, 64),
        Match("Match", 14, "Match", 32, 32, 0, 128),
        Bust("Bust", 15, "Bust", 32, 32, 32, 128),
        Stage("Stage", 16, "Stage", 32, 32, 64, 128),
        Void("Void", 17, "Void", 32, 32, 96, 128),
        SkullAndRoses("SkullAndRoses", 18, "SkullAndRoses", 32, 32, 128, 128),
        Wither("Wither", 19, "Wither", 32, 32, 160, 128),
        Fighters("Fighters", 20, "Fighters", 64, 32, 0, 96),
        Pointer("Pointer", 21, "Pointer", 64, 64, 0, 192),
        Pigscene("Pigscene", 22, "Pigscene", 64, 64, 64, 192),
        BurningSkull("BurningSkull", 23, "BurningSkull", 64, 64, 128, 192),
        Skeleton("Skeleton", 24, "Skeleton", 64, 48, 192, 64),
        DonkeyKong("DonkeyKong", 25, "DonkeyKong", 64, 48, 192, 112);
        public static final int maxArtTitleLength = "SkullAndRoses".length();
        public final String title;
        public final int sizeX;
        public final int sizeY;
        public final int offsetX;
        public final int offsetY;

        EnumArt(String par1Str, int par2, String par3Str, int par4, int par5, int par6, int par7)
        {
            this.title = par3Str;
            this.sizeX = par4;
            this.sizeY = par5;
            this.offsetX = par6;
            this.offsetY = par7;
        }
    }
}
