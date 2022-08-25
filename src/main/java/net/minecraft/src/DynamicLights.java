package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class DynamicLights
{
    private static DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
    private static long timeUpdateMs = 0L;
    private static final double MAX_DIST = 7.5D;
    private static final double MAX_DIST_SQ = 56.25D;
    private static final int LIGHT_LEVEL_MAX = 15;
    private static final int LIGHT_LEVEL_FIRE = 15;
    private static final int LIGHT_LEVEL_BLAZE = 10;
    private static final int LIGHT_LEVEL_MAGMA_CUBE = 8;
    private static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
    private static final int LIGHT_LEVEL_GLOWSTONE_DUST = 8;
    private static final int LIGHT_LEVEL_PRISMARINE_CRYSTALS = 8;
    private static ReflectorField ItemBlock_block = Reflector.getReflectorField(ItemBlock.class, Block.class);

    public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {}

    public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal)
    {
        DynamicLightsMap var2 = mapDynamicLights;

        synchronized (mapDynamicLights)
        {
            DynamicLight dynamicLight = mapDynamicLights.remove(entityIn.getEntityId());

            if (dynamicLight != null)
            {
                dynamicLight.updateLitChunks(renderGlobal);
            }
        }
    }

    public static void update(RenderGlobal renderGlobal)
    {
        long timeNowMs = System.currentTimeMillis();

        if (timeNowMs >= timeUpdateMs + 50L)
        {
            timeUpdateMs = timeNowMs;
            DynamicLightsMap var3 = mapDynamicLights;

            synchronized (mapDynamicLights)
            {
                updateMapDynamicLights(renderGlobal);

                if (mapDynamicLights.size() > 0)
                {
                    List dynamicLights = mapDynamicLights.valueList();

                    for (int i = 0; i < dynamicLights.size(); ++i)
                    {
                        DynamicLight dynamicLight = (DynamicLight)dynamicLights.get(i);
                        dynamicLight.update(renderGlobal);
                    }
                }
            }
        }
    }

    private static void updateMapDynamicLights(RenderGlobal renderGlobal)
    {
        WorldClient world = renderGlobal.theWorld;

        if (world != null)
        {
            List entities = world.getLoadedEntityList();
            Iterator it = entities.iterator();

            while (it.hasNext())
            {
                Entity entity = (Entity)it.next();
                int lightLevel = getLightLevel(entity);
                int key;
                DynamicLight dynamicLight;

                if (lightLevel > 0)
                {
                    key = entity.getEntityId();
                    dynamicLight = mapDynamicLights.get(key);

                    if (dynamicLight == null)
                    {
                        dynamicLight = new DynamicLight(entity);
                        mapDynamicLights.put(key, dynamicLight);
                    }
                }
                else
                {
                    key = entity.getEntityId();
                    dynamicLight = mapDynamicLights.remove(key);

                    if (dynamicLight != null)
                    {
                        dynamicLight.updateLitChunks(renderGlobal);
                    }
                }
            }
        }
    }

    public static int getCombinedLight(int x, int y, int z, int combinedLight)
    {
        double lightPlayer = getLightLevel(x, y, z);
        combinedLight = getCombinedLight(lightPlayer, combinedLight);
        return combinedLight;
    }

    public static int getCombinedLight(Entity entity, int combinedLight)
    {
        double lightPlayer = (double)getLightLevel(entity);
        combinedLight = getCombinedLight(lightPlayer, combinedLight);
        return combinedLight;
    }

    public static int getCombinedLight(double lightPlayer, int combinedLight)
    {
        if (lightPlayer > 0.0D)
        {
            int lightPlayerFF = (int)(lightPlayer * 16.0D);
            int lightBlockFF = combinedLight & 255;

            if (lightPlayerFF > lightBlockFF)
            {
                combinedLight &= -256;
                combinedLight |= lightPlayerFF;
            }
        }

        return combinedLight;
    }

    public static double getLightLevel(int x, int y, int z)
    {
        double lightLevelMax = 0.0D;
        DynamicLightsMap lightPlayer = mapDynamicLights;

        synchronized (mapDynamicLights)
        {
            List dynamicLights = mapDynamicLights.valueList();

            for (int i = 0; i < dynamicLights.size(); ++i)
            {
                DynamicLight dynamicLight = (DynamicLight)dynamicLights.get(i);
                int dynamicLightLevel = dynamicLight.getLastLightLevel();

                if (dynamicLightLevel > 0)
                {
                    double px = dynamicLight.getLastPosX();
                    double py = dynamicLight.getLastPosY();
                    double pz = dynamicLight.getLastPosZ();
                    double dx = (double)x - px;
                    double dy = (double)y - py;
                    double dz = (double)z - pz;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    if (dynamicLight.isUnderwater() && !Config.isClearWater())
                    {
                        dynamicLightLevel = Config.limit(dynamicLightLevel - 2, 0, 15);
                        distSq *= 2.0D;
                    }

                    if (distSq <= 56.25D)
                    {
                        double dist = Math.sqrt(distSq);
                        double light = 1.0D - dist / 7.5D;
                        double lightLevel = light * (double)dynamicLightLevel;

                        if (lightLevel > lightLevelMax)
                        {
                            lightLevelMax = lightLevel;
                        }
                    }
                }
            }
        }

        double var32 = Config.limit(lightLevelMax, 0.0D, 15.0D);
        return var32;
    }

    public static int getLightLevel(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return 0;
        }
        else
        {
            Item item = itemStack.getItem();

            if (item instanceof ItemBlock)
            {
                ItemBlock itemBlock = (ItemBlock)item;
                Block block = (Block)Reflector.getFieldValue(itemBlock, ItemBlock_block);

                if (block != null)
                {
                    return block.getLightValue();
                }
            }

            return item == Items.lava_bucket ? Blocks.lava.getLightValue() : (item != Items.blaze_rod && item != Items.blaze_powder ? (item == Items.glowstone_dust ? 8 : (item == Items.magma_cream ? 8 : (item == Items.nether_star ? Blocks.beacon.getLightValue() / 2 : 0))) : 10);
        }
    }

    public static int getLightLevel(Entity entity)
    {
        if (entity == Config.getMinecraft().renderViewEntity && !Config.isDynamicHandLight())
        {
            return 0;
        }
        else if (entity.isBurning())
        {
            return 15;
        }
        else if (entity instanceof EntityFireball)
        {
            return 15;
        }
        else if (entity instanceof EntityTNTPrimed)
        {
            return 15;
        }
        else if (entity instanceof EntityBlaze)
        {
            EntityBlaze entityItem4 = (EntityBlaze)entity;
            return entityItem4.func_70845_n() ? 15 : 10;
        }
        else if (entity instanceof EntityMagmaCube)
        {
            EntityMagmaCube entityItem3 = (EntityMagmaCube)entity;
            return (double)entityItem3.squishFactor > 0.6D ? 13 : 8;
        }
        else
        {
            if (entity instanceof EntityCreeper)
            {
                EntityCreeper entityItem = (EntityCreeper)entity;

                if (entityItem.getCreeperState() > 0)
                {
                    return 15;
                }
            }

            ItemStack itemStack;

            if (entity instanceof EntityLivingBase)
            {
                EntityLivingBase entityItem2 = (EntityLivingBase)entity;
                itemStack = entityItem2.getHeldItem();
                int levelMain = getLightLevel(itemStack);
                ItemStack stackHead = entityItem2.getEquipmentInSlot(4);
                int levelHead = getLightLevel(stackHead);
                return Math.max(levelMain, levelHead);
            }
            else if (entity instanceof EntityItem)
            {
                EntityItem entityItem1 = (EntityItem)entity;
                itemStack = getItemStack(entityItem1);
                return getLightLevel(itemStack);
            }
            else
            {
                return 0;
            }
        }
    }

    public static void removeLights(RenderGlobal renderGlobal)
    {
        DynamicLightsMap var1 = mapDynamicLights;

        synchronized (mapDynamicLights)
        {
            List dynamicLights = mapDynamicLights.valueList();

            for (int i = 0; i < dynamicLights.size(); ++i)
            {
                DynamicLight dynamicLight = (DynamicLight)dynamicLights.get(i);
                dynamicLight.updateLitChunks(renderGlobal);
            }

            dynamicLights.clear();
        }
    }

    public static void clear()
    {
        DynamicLightsMap var0 = mapDynamicLights;

        synchronized (mapDynamicLights)
        {
            mapDynamicLights.clear();
        }
    }

    public static int getCount()
    {
        DynamicLightsMap var0 = mapDynamicLights;

        synchronized (mapDynamicLights)
        {
            return mapDynamicLights.size();
        }
    }

    public static ItemStack getItemStack(EntityItem entityItem)
    {
        ItemStack itemstack = entityItem.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack;
    }
}
