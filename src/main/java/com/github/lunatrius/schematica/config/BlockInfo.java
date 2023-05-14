//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.config;

import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import java.util.*;

public class BlockInfo
{
    public static final List<Block> BLOCK_LIST_IGNORE_BLOCK;
    public static final List<Block> BLOCK_LIST_IGNORE_METADATA;
    public static final Map<Block, Item> BLOCK_ITEM_MAP;
    public static final Map<Class, PlacementData> CLASS_PLACEMENT_MAP;
    public static final Map<Item, PlacementData> ITEM_PLACEMENT_MAP;
    private static String modId;
    
    public static void setModId(final String modId) {
        BlockInfo.modId = modId;
    }
    
    public static void populateIgnoredBlocks() {
        BlockInfo.BLOCK_LIST_IGNORE_BLOCK.clear();
        addIgnoredBlock((Block)Blocks.piston_head);
        addIgnoredBlock((Block)Blocks.piston_extension);
        addIgnoredBlock((Block)Blocks.portal);
        addIgnoredBlock(Blocks.end_portal);
    }
    
    private static boolean addIgnoredBlock(final Block block) {
        return block != null && BlockInfo.BLOCK_LIST_IGNORE_BLOCK.add(block);
    }

    public static void populateIgnoredBlockMetadata() {
        BlockInfo.BLOCK_LIST_IGNORE_METADATA.clear();
        addIgnoredBlockMetadata((Block)Blocks.flowing_water);
        addIgnoredBlockMetadata(Blocks.water);
        addIgnoredBlockMetadata((Block)Blocks.flowing_lava);
        addIgnoredBlockMetadata(Blocks.lava);
        addIgnoredBlockMetadata(Blocks.dispenser);
        addIgnoredBlockMetadata(Blocks.bed);
        addIgnoredBlockMetadata(Blocks.golden_rail);
        addIgnoredBlockMetadata(Blocks.detector_rail);
        addIgnoredBlockMetadata((Block)Blocks.sticky_piston);
        addIgnoredBlockMetadata((Block)Blocks.piston);
        addIgnoredBlockMetadata(Blocks.torch);
        addIgnoredBlockMetadata(Blocks.oak_stairs);
        addIgnoredBlockMetadata((Block)Blocks.chest);
        addIgnoredBlockMetadata((Block)Blocks.redstone_wire);
        addIgnoredBlockMetadata(Blocks.wheat);
        addIgnoredBlockMetadata(Blocks.farmland);
        addIgnoredBlockMetadata(Blocks.furnace);
        addIgnoredBlockMetadata(Blocks.lit_furnace);
        addIgnoredBlockMetadata(Blocks.standing_sign);
        addIgnoredBlockMetadata(Blocks.wooden_door);
        addIgnoredBlockMetadata(Blocks.ladder);
        addIgnoredBlockMetadata(Blocks.rail);
        addIgnoredBlockMetadata(Blocks.stone_stairs);
        addIgnoredBlockMetadata(Blocks.wall_sign);
        addIgnoredBlockMetadata(Blocks.lever);
        addIgnoredBlockMetadata(Blocks.stone_pressure_plate);
        addIgnoredBlockMetadata(Blocks.iron_door);
        addIgnoredBlockMetadata(Blocks.wooden_pressure_plate);
        addIgnoredBlockMetadata(Blocks.unlit_redstone_torch);
        addIgnoredBlockMetadata(Blocks.redstone_torch);
        addIgnoredBlockMetadata(Blocks.stone_button);
        addIgnoredBlockMetadata(Blocks.cactus);
        addIgnoredBlockMetadata(Blocks.reeds);
        addIgnoredBlockMetadata(Blocks.pumpkin);
        addIgnoredBlockMetadata((Block)Blocks.portal);
        addIgnoredBlockMetadata(Blocks.lit_pumpkin);
        addIgnoredBlockMetadata(Blocks.cake);
        addIgnoredBlockMetadata((Block)Blocks.unpowered_repeater);
        addIgnoredBlockMetadata((Block)Blocks.powered_repeater);
        addIgnoredBlockMetadata(Blocks.trapdoor);
        addIgnoredBlockMetadata(Blocks.vine);
        addIgnoredBlockMetadata(Blocks.fence_gate);
        addIgnoredBlockMetadata(Blocks.brick_stairs);
        addIgnoredBlockMetadata(Blocks.stone_brick_stairs);
        addIgnoredBlockMetadata(Blocks.waterlily);
        addIgnoredBlockMetadata(Blocks.nether_brick_stairs);
        addIgnoredBlockMetadata(Blocks.nether_wart);
        addIgnoredBlockMetadata(Blocks.end_portal_frame);
        addIgnoredBlockMetadata(Blocks.redstone_lamp);
        addIgnoredBlockMetadata(Blocks.lit_redstone_lamp);
        addIgnoredBlockMetadata(Blocks.sandstone_stairs);
        addIgnoredBlockMetadata(Blocks.ender_chest);
        addIgnoredBlockMetadata((Block)Blocks.tripwire_hook);
        addIgnoredBlockMetadata(Blocks.tripwire);
        addIgnoredBlockMetadata(Blocks.spruce_stairs);
        addIgnoredBlockMetadata(Blocks.birch_stairs);
        addIgnoredBlockMetadata(Blocks.jungle_stairs);
        addIgnoredBlockMetadata(Blocks.command_block);
        addIgnoredBlockMetadata(Blocks.flower_pot);
        addIgnoredBlockMetadata(Blocks.carrots);
        addIgnoredBlockMetadata(Blocks.potatoes);
        addIgnoredBlockMetadata(Blocks.wooden_button);
        addIgnoredBlockMetadata(Blocks.anvil);
        addIgnoredBlockMetadata(Blocks.trapped_chest);
        addIgnoredBlockMetadata((Block)Blocks.hopper);
        addIgnoredBlockMetadata(Blocks.quartz_stairs);
        addIgnoredBlockMetadata(Blocks.dropper);
    }
    
    private static boolean addIgnoredBlockMetadata(final Block block) {
        return block != null && BlockInfo.BLOCK_LIST_IGNORE_METADATA.add(block);
    }

    public static void populateBlockItemMap() {
        BlockInfo.BLOCK_ITEM_MAP.clear();
        addBlockItemMapping((Block)Blocks.flowing_water, Items.water_bucket);
        addBlockItemMapping(Blocks.water, Items.water_bucket);
        addBlockItemMapping((Block)Blocks.flowing_lava, Items.lava_bucket);
        addBlockItemMapping(Blocks.lava, Items.lava_bucket);
        addBlockItemMapping(Blocks.bed, Items.bed);
        addBlockItemMapping((Block)Blocks.redstone_wire, Items.redstone);
        addBlockItemMapping(Blocks.wheat, Items.wheat_seeds);
        addBlockItemMapping(Blocks.lit_furnace, Blocks.furnace);
        addBlockItemMapping(Blocks.standing_sign, Items.sign);
        addBlockItemMapping(Blocks.wooden_door, Items.wooden_door);
        addBlockItemMapping(Blocks.iron_door, Items.iron_door);
        addBlockItemMapping(Blocks.wall_sign, Items.sign);
        addBlockItemMapping(Blocks.unlit_redstone_torch, Blocks.redstone_torch);
        addBlockItemMapping(Blocks.reeds, Items.reeds);
        addBlockItemMapping((Block)Blocks.unpowered_repeater, Items.repeater);
        addBlockItemMapping((Block)Blocks.powered_repeater, Items.repeater);
        addBlockItemMapping(Blocks.pumpkin_stem, Items.pumpkin_seeds);
        addBlockItemMapping(Blocks.melon_stem, Items.melon_seeds);
        addBlockItemMapping(Blocks.nether_wart, Items.nether_wart);
        addBlockItemMapping(Blocks.brewing_stand, Items.brewing_stand);
        addBlockItemMapping((Block)Blocks.cauldron, Items.cauldron);
        addBlockItemMapping(Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
        addBlockItemMapping(Blocks.cocoa, Items.dye);
        addBlockItemMapping(Blocks.tripwire, Items.string);
        addBlockItemMapping(Blocks.flower_pot, Items.flower_pot);
        addBlockItemMapping(Blocks.carrots, Items.carrot);
        addBlockItemMapping(Blocks.potatoes, Items.potato);
        addBlockItemMapping(Blocks.skull, Items.skull);
        addBlockItemMapping((Block)Blocks.unpowered_comparator, Items.comparator);
        addBlockItemMapping((Block)Blocks.powered_comparator, Items.comparator);
    }
    
    private static Item addBlockItemMapping(final Block block, final Item item) {
        if (block == null || item == null) {
            return null;
        }
        return BlockInfo.BLOCK_ITEM_MAP.put(block, item);
    }
    
    private static Item addBlockItemMapping(final Block block, final Block item) {
        return addBlockItemMapping(block, Item.getItemFromBlock(item));
    }
    
    private static Item addBlockItemMapping(final Object blockObj, final Object itemObj) {
        if (!"minecraft".equals(BlockInfo.modId)) {
            return null;
        }
        Block block = null;
        Item item = null;
        if (blockObj instanceof Block) {
            block = (Block)blockObj;
        }
        else if (blockObj instanceof String) {
            //block = (Block)GameData.getBlockRegistry().getObject(String.format("%s:%s", BlockInfo.modId, blockObj));
            block = Block.getBlockFromName(String.format("%s:%s", BlockInfo.modId, blockObj)); //(Block)GameData.getBlockRegistry().getObject(String.format("%s:%s", BlockInfo.modId, blockObj));
        }
        if (itemObj instanceof Item) {
            item = (Item)itemObj;
        }
        else if (itemObj instanceof Block) {
            item = Item.getItemFromBlock((Block)itemObj);
        }
        else if (itemObj instanceof String) {
            final String formattedName = String.format("%s:%s", BlockInfo.modId, itemObj);
            //item = (Item)GameData.getItemRegistry().getObject(formattedName);
            item = (Item) Item.itemRegistry.getObject(formattedName);//(formattedName);
            if (item == null) {
                //item = Item.getItemFromBlock((Block)GameData.getBlockRegistry().getObject(formattedName));
                item = Item.getItemFromBlock((Block) Block.blockRegistry.getObject(formattedName));
            }
        }
        return addBlockItemMapping(block, item);
    }
    
    public static Item getItemFromBlock(final Block block) {
        if (BlockInfo.BLOCK_ITEM_MAP.containsKey(block)) {
            return BlockInfo.BLOCK_ITEM_MAP.get(block);
        }
        return Item.getItemFromBlock(block);
    }
    
    public static void populatePlacementMaps() {
        BlockInfo.ITEM_PLACEMENT_MAP.clear();
        addPlacementMapping(BlockButton.class, new PlacementData(PlacementData.PlacementType.BLOCK, new int[] { -1, -1, 3, 4, 1, 2 }).setMaskMeta(7));
        addPlacementMapping(BlockChest.class, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 3, 2, 5, 4 }));
        addPlacementMapping(BlockDispenser.class, new PlacementData(PlacementData.PlacementType.PISTON, new int[] { 0, 1, 2, 3, 4, 5 }).setMaskMeta(7));
        addPlacementMapping(BlockEnderChest.class, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 3, 2, 5, 4 }));
        addPlacementMapping(BlockFurnace.class, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 3, 2, 5, 4 }));
        addPlacementMapping(BlockHopper.class, new PlacementData(PlacementData.PlacementType.BLOCK, new int[] { 0, 1, 2, 3, 4, 5 }).setMaskMeta(7));
        addPlacementMapping(BlockLog.class, new PlacementData(PlacementData.PlacementType.BLOCK, new int[] { 0, 0, 8, 8, 4, 4 }).setMaskMeta(12));
        addPlacementMapping(BlockPistonBase.class, new PlacementData(PlacementData.PlacementType.PISTON, new int[] { 0, 1, 2, 3, 4, 5 }).setMaskMeta(7));
        addPlacementMapping(BlockPumpkin.class, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 0, 2, 3, 1 }).setMaskMeta(15));
        addPlacementMapping(BlockStairs.class, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 3, 2, 1, 0 }).setOffset(4, 0.0f, 1.0f).setMaskMeta(3));
        addPlacementMapping(BlockTorch.class, new PlacementData(PlacementData.PlacementType.BLOCK, new int[] { 5, -1, 3, 4, 1, 2 }).setMaskMeta(15));
        addPlacementMapping(Blocks.dirt, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Blocks.planks, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Blocks.sandstone, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Blocks.wool, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping((Block)Blocks.stone_slab, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setOffset(8, 0.0f, 1.0f).setMaskMeta(7).setMaskMetaInHand(7));
        addPlacementMapping((Block)Blocks.stained_glass, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Blocks.trapdoor, new PlacementData(PlacementData.PlacementType.BLOCK, new int[] { -1, -1, 1, 0, 3, 2 }).setOffset(8, 0.0f, 1.0f).setMaskMeta(3));
        addPlacementMapping(Blocks.monster_egg, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Blocks.stonebrick, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Blocks.quartz_block, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Blocks.fence_gate, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 2, 0, 1, 3 }).setMaskMeta(3));
        addPlacementMapping((Block)Blocks.wooden_slab, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setOffset(8, 0.0f, 1.0f).setMaskMeta(7).setMaskMetaInHand(7));
        addPlacementMapping(Blocks.anvil, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 1, 3, 0, 2 }).setMaskMeta(3).setMaskMetaInHand(12).setBitShiftMetaInHand(2));
        addPlacementMapping(Blocks.stained_hardened_clay, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping((Block)Blocks.stained_glass_pane, new PlacementData(PlacementData.PlacementType.BLOCK, new int[0]).setMaskMetaInHand(15));
        addPlacementMapping(Items.repeater, new PlacementData(PlacementData.PlacementType.PLAYER, new int[] { -1, -1, 0, 2, 3, 1 }).setMaskMeta(3));
    }
    
    public static PlacementData addPlacementMapping(final Class clazz, final PlacementData data) {
        if (clazz == null || data == null) {
            return null;
        }
        return BlockInfo.CLASS_PLACEMENT_MAP.put(clazz, data);
    }
    
    public static PlacementData addPlacementMapping(final Item item, final PlacementData data) {
        if (item == null || data == null) {
            return null;
        }
        return BlockInfo.ITEM_PLACEMENT_MAP.put(item, data);
    }
    
    public static PlacementData addPlacementMapping(final Block block, final PlacementData data) {
        return addPlacementMapping(Item.getItemFromBlock(block), data);
    }
    
    public static PlacementData addPlacementMapping(final Object itemObj, final PlacementData data) {
        if (itemObj == null || data == null) {
            return null;
        }
        Item item = null;
        if (itemObj instanceof Item) {
            item = (Item)itemObj;
        }
        else if (itemObj instanceof Block) {
            item = Item.getItemFromBlock((Block)itemObj);
        }
        else if (itemObj instanceof String) {
            final String formattedName = String.format("%s:%s", BlockInfo.modId, itemObj);
            //item = (Item)GameData.getItemRegistry().getObject(formattedName);
            item = (Item) Item.itemRegistry.getObject(formattedName);//(formattedName);
            if (item == null) {
                //item = Item.getItemFromBlock((Block)GameData.getBlockRegistry().getObject(formattedName));
                item = Item.getItemFromBlock((Block) Block.blockRegistry.getObject(formattedName));
            }
        }
        return addPlacementMapping(item, data);
    }
    
    public static PlacementData getPlacementDataFromItem(final Item item) {
        final Block block = Block.getBlockFromItem(item);
        PlacementData data = null;
        for (final Class clazz : BlockInfo.CLASS_PLACEMENT_MAP.keySet()) {
            if (clazz.isInstance(block)) {
                data = BlockInfo.CLASS_PLACEMENT_MAP.get(clazz);
                break;
            }
        }
        for (final Item i : BlockInfo.ITEM_PLACEMENT_MAP.keySet()) {
            if (i == item) {
                data = BlockInfo.ITEM_PLACEMENT_MAP.get(i);
                break;
            }
        }
        return data;
    }
    
    static {
        BLOCK_LIST_IGNORE_BLOCK = new ArrayList<Block>();
        BLOCK_LIST_IGNORE_METADATA = new ArrayList<Block>();
        BLOCK_ITEM_MAP = new HashMap<Block, Item>();
        CLASS_PLACEMENT_MAP = new HashMap<Class, PlacementData>();
        ITEM_PLACEMENT_MAP = new HashMap<Item, PlacementData>();
        BlockInfo.modId = "minecraft";
        populateIgnoredBlocks();
        populateIgnoredBlockMetadata();
        populateBlockItemMap();
        populatePlacementMaps();
    }
}
