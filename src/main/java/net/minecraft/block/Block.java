package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.render.EventLightValue;
import wtf.nebula.client.impl.event.impl.render.EventRenderPass;
import wtf.nebula.client.impl.event.impl.render.EventBlockSide;
import wtf.nebula.client.impl.event.impl.world.EventAddBox;
import wtf.nebula.client.impl.event.impl.world.EventGetBox;

public class Block
{
    public static final RegistryNamespaced blockRegistry = new RegistryNamespacedDefaultedByKey("air");
    private CreativeTabs displayOnCreativeTab;
    protected String textureName;
    public static final Block.SoundType soundTypeStone = new Block.SoundType("stone", 1.0F, 1.0F);
    public static final Block.SoundType soundTypeWood = new Block.SoundType("wood", 1.0F, 1.0F);
    public static final Block.SoundType soundTypeGravel = new Block.SoundType("gravel", 1.0F, 1.0F);
    public static final Block.SoundType soundTypeGrass = new Block.SoundType("grass", 1.0F, 1.0F);
    public static final Block.SoundType soundStoneFootstep = new Block.SoundType("stone", 1.0F, 1.0F);
    public static final Block.SoundType soundTypeMetal = new Block.SoundType("stone", 1.0F, 1.5F);
    public static final Block.SoundType soundTypeGlass = new Block.SoundType("stone", 1.0F, 1.0F)
    {
        private static final String __OBFID = "CL_00000200";
        public String func_150495_a()
        {
            return "dig.glass";
        }
        public String func_150496_b()
        {
            return "step.stone";
        }
    };
    public static final Block.SoundType soundTypeCloth = new Block.SoundType("cloth", 1.0F, 1.0F);
    public static final Block.SoundType field_149776_m = new Block.SoundType("sand", 1.0F, 1.0F);
    public static final Block.SoundType soundTypeSnow = new Block.SoundType("snow", 1.0F, 1.0F);
    public static final Block.SoundType soundTypeLadder = new Block.SoundType("ladder", 1.0F, 1.0F)
    {
        private static final String __OBFID = "CL_00000201";
        public String func_150495_a()
        {
            return "dig.wood";
        }
    };
    public static final Block.SoundType soundTypeAnvil = new Block.SoundType("anvil", 0.3F, 1.0F)
    {
        private static final String __OBFID = "CL_00000202";
        public String func_150495_a()
        {
            return "dig.stone";
        }
        public String func_150496_b()
        {
            return "random.anvil_land";
        }
    };
    protected boolean opaque;
    protected int lightOpacity;
    protected boolean canBlockGrass;
    protected int lightValue;
    protected boolean field_149783_u;
    public float blockHardness;
    protected float blockResistance;
    protected boolean blockConstructorCalled = true;
    protected boolean enableStats = true;
    protected boolean needsRandomTick;
    protected boolean isBlockContainer;
    protected double minX;
    protected double minY;
    protected double minZ;
    protected double maxX;
    protected double maxY;
    protected double maxZ;
    public Block.SoundType stepSound;
    public float blockParticleGravity;
    protected final Material blockMaterial;
    public float slipperiness;
    private String unlocalizedName;
    protected IIcon blockIcon;
    private static final String __OBFID = "CL_00000199";

    public static int getIdFromBlock(Block p_149682_0_)
    {
        return blockRegistry.getIDForObject(p_149682_0_);
    }

    public static Block getBlockById(int p_149729_0_)
    {
        return (Block)blockRegistry.getObjectForID(p_149729_0_);
    }

    public static Block getBlockFromItem(Item p_149634_0_)
    {
        return getBlockById(Item.getIdFromItem(p_149634_0_));
    }

    public static Block getBlockFromName(String p_149684_0_)
    {
        if (blockRegistry.containsKey(p_149684_0_))
        {
            return (Block)blockRegistry.getObject(p_149684_0_);
        }
        else
        {
            try
            {
                return (Block)blockRegistry.getObjectForID(Integer.parseInt(p_149684_0_));
            }
            catch (NumberFormatException var2)
            {
                return null;
            }
        }
    }

    public AxisAlignedBB getBaseBoundingBox() {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean func_149730_j()
    {
        return this.opaque;
    }

    public int getLightOpacity()
    {
        return this.lightOpacity;
    }

    public boolean getCanBlockGrass()
    {
        return this.canBlockGrass;
    }

    public int getLightValue()
    {
        EventLightValue event = new EventLightValue(this, lightValue);
        Nebula.BUS.post(event);
        return event.isCancelled() ? event.getLightValue() : this.lightValue;
    }

    public boolean func_149710_n()
    {
        return this.field_149783_u;
    }

    public Material getMaterial()
    {
        return this.blockMaterial;
    }

    public MapColor getMapColor(int p_149728_1_)
    {
        return this.getMaterial().getMaterialMapColor();
    }

    public static void registerBlocks()
    {
        blockRegistry.addObject(0, "air", (new BlockAir()).setBlockName("air"));
        blockRegistry.addObject(1, "stone", (new BlockStone()).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stone").setTextureName("stone"));
        blockRegistry.addObject(2, "grass", (new BlockGrass()).setHardness(0.6F).setStepSound(soundTypeGrass).setBlockName("grass").setTextureName("grass"));
        blockRegistry.addObject(3, "dirt", (new BlockDirt()).setHardness(0.5F).setStepSound(soundTypeGravel).setBlockName("dirt").setTextureName("dirt"));
        Block var0 = (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stonebrick").setCreativeTab(CreativeTabs.tabBlock).setTextureName("cobblestone");
        blockRegistry.addObject(4, "cobblestone", var0);
        Block var1 = (new BlockWood()).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setBlockName("wood").setTextureName("planks");
        blockRegistry.addObject(5, "planks", var1);
        blockRegistry.addObject(6, "sapling", (new BlockSapling()).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("sapling").setTextureName("sapling"));
        blockRegistry.addObject(7, "bedrock", (new Block(Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundStoneFootstep).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setTextureName("bedrock"));
        blockRegistry.addObject(8, "flowing_water", (new BlockDynamicLiquid(Material.water)).setHardness(100.0F).setLightOpacity(3).setBlockName("water").disableStats().setTextureName("water_flow"));
        blockRegistry.addObject(9, "water", (new BlockStaticLiquid(Material.water)).setHardness(100.0F).setLightOpacity(3).setBlockName("water").disableStats().setTextureName("water_still"));
        blockRegistry.addObject(10, "flowing_lava", (new BlockDynamicLiquid(Material.lava)).setHardness(100.0F).setLightValue(1.0F).setBlockName("lava").disableStats().setTextureName("lava_flow"));
        blockRegistry.addObject(11, "lava", (new BlockStaticLiquid(Material.lava)).setHardness(100.0F).setLightValue(1.0F).setBlockName("lava").disableStats().setTextureName("lava_still"));
        blockRegistry.addObject(12, "sand", (new BlockSand()).setHardness(0.5F).setStepSound(field_149776_m).setBlockName("sand").setTextureName("sand"));
        blockRegistry.addObject(13, "gravel", (new BlockGravel()).setHardness(0.6F).setStepSound(soundTypeGravel).setBlockName("gravel").setTextureName("gravel"));
        blockRegistry.addObject(14, "gold_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreGold").setTextureName("gold_ore"));
        blockRegistry.addObject(15, "iron_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreIron").setTextureName("iron_ore"));
        blockRegistry.addObject(16, "coal_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreCoal").setTextureName("coal_ore"));
        blockRegistry.addObject(17, "log", (new BlockOldLog()).setBlockName("log").setTextureName("log"));
        blockRegistry.addObject(18, "leaves", (new BlockOldLeaf()).setBlockName("leaves").setTextureName("leaves"));
        blockRegistry.addObject(19, "sponge", (new BlockSponge()).setHardness(0.6F).setStepSound(soundTypeGrass).setBlockName("sponge").setTextureName("sponge"));
        blockRegistry.addObject(20, "glass", (new BlockGlass(Material.glass, false)).setHardness(0.3F).setStepSound(soundTypeGlass).setBlockName("glass").setTextureName("glass"));
        blockRegistry.addObject(21, "lapis_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreLapis").setTextureName("lapis_ore"));
        blockRegistry.addObject(22, "lapis_block", (new BlockCompressed(MapColor.field_151652_H)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("blockLapis").setCreativeTab(CreativeTabs.tabBlock).setTextureName("lapis_block"));
        blockRegistry.addObject(23, "dispenser", (new BlockDispenser()).setHardness(3.5F).setStepSound(soundStoneFootstep).setBlockName("dispenser").setTextureName("dispenser"));
        Block var2 = (new BlockSandStone()).setStepSound(soundStoneFootstep).setHardness(0.8F).setBlockName("sandStone").setTextureName("sandstone");
        blockRegistry.addObject(24, "sandstone", var2);
        blockRegistry.addObject(25, "noteblock", (new BlockNote()).setHardness(0.8F).setBlockName("musicBlock").setTextureName("noteblock"));
        blockRegistry.addObject(26, "bed", (new BlockBed()).setHardness(0.2F).setBlockName("bed").disableStats().setTextureName("bed"));
        blockRegistry.addObject(27, "golden_rail", (new BlockRailPowered()).setHardness(0.7F).setStepSound(soundTypeMetal).setBlockName("goldenRail").setTextureName("rail_golden"));
        blockRegistry.addObject(28, "detector_rail", (new BlockRailDetector()).setHardness(0.7F).setStepSound(soundTypeMetal).setBlockName("detectorRail").setTextureName("rail_detector"));
        blockRegistry.addObject(29, "sticky_piston", (new BlockPistonBase(true)).setBlockName("pistonStickyBase"));
        blockRegistry.addObject(30, "web", (new BlockWeb()).setLightOpacity(1).setHardness(4.0F).setBlockName("web").setTextureName("web"));
        blockRegistry.addObject(31, "tallgrass", (new BlockTallGrass()).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("tallgrass"));
        blockRegistry.addObject(32, "deadbush", (new BlockDeadBush()).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("deadbush").setTextureName("deadbush"));
        blockRegistry.addObject(33, "piston", (new BlockPistonBase(false)).setBlockName("pistonBase"));
        blockRegistry.addObject(34, "piston_head", new BlockPistonExtension());
        blockRegistry.addObject(35, "wool", (new BlockColored(Material.cloth)).setHardness(0.8F).setStepSound(soundTypeCloth).setBlockName("cloth").setTextureName("wool_colored"));
        blockRegistry.addObject(36, "piston_extension", new BlockPistonMoving());
        blockRegistry.addObject(37, "yellow_flower", (new BlockFlower(0)).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("flower1").setTextureName("flower_dandelion"));
        blockRegistry.addObject(38, "red_flower", (new BlockFlower(1)).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("flower2").setTextureName("flower_rose"));
        blockRegistry.addObject(39, "brown_mushroom", (new BlockMushroom()).setHardness(0.0F).setStepSound(soundTypeGrass).setLightValue(0.125F).setBlockName("mushroom").setTextureName("mushroom_brown"));
        blockRegistry.addObject(40, "red_mushroom", (new BlockMushroom()).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("mushroom").setTextureName("mushroom_red"));
        blockRegistry.addObject(41, "gold_block", (new BlockCompressed(MapColor.field_151647_F)).setHardness(3.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setBlockName("blockGold").setTextureName("gold_block"));
        blockRegistry.addObject(42, "iron_block", (new BlockCompressed(MapColor.field_151668_h)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setBlockName("blockIron").setTextureName("iron_block"));
        blockRegistry.addObject(43, "double_stone_slab", (new BlockStoneSlab(true)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneSlab"));
        blockRegistry.addObject(44, "stone_slab", (new BlockStoneSlab(false)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneSlab"));
        Block var3 = (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("brick").setCreativeTab(CreativeTabs.tabBlock).setTextureName("brick");
        blockRegistry.addObject(45, "brick_block", var3);
        blockRegistry.addObject(46, "tnt", (new BlockTNT()).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("tnt").setTextureName("tnt"));
        blockRegistry.addObject(47, "bookshelf", (new BlockBookshelf()).setHardness(1.5F).setStepSound(soundTypeWood).setBlockName("bookshelf").setTextureName("bookshelf"));
        blockRegistry.addObject(48, "mossy_cobblestone", (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneMoss").setCreativeTab(CreativeTabs.tabBlock).setTextureName("cobblestone_mossy"));
        blockRegistry.addObject(49, "obsidian", (new BlockObsidian()).setHardness(50.0F).setResistance(2000.0F).setStepSound(soundStoneFootstep).setBlockName("obsidian").setTextureName("obsidian"));
        blockRegistry.addObject(50, "torch", (new BlockTorch()).setHardness(0.0F).setLightValue(0.9375F).setStepSound(soundTypeWood).setBlockName("torch").setTextureName("torch_on"));
        blockRegistry.addObject(51, "fire", (new BlockFire()).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundTypeWood).setBlockName("fire").disableStats().setTextureName("fire"));
        blockRegistry.addObject(52, "mob_spawner", (new BlockMobSpawner()).setHardness(5.0F).setStepSound(soundTypeMetal).setBlockName("mobSpawner").disableStats().setTextureName("mob_spawner"));
        blockRegistry.addObject(53, "oak_stairs", (new BlockStairs(var1, 0)).setBlockName("stairsWood"));
        blockRegistry.addObject(54, "chest", (new BlockChest(0)).setHardness(2.5F).setStepSound(soundTypeWood).setBlockName("chest"));
        blockRegistry.addObject(55, "redstone_wire", (new BlockRedstoneWire()).setHardness(0.0F).setStepSound(soundTypeStone).setBlockName("redstoneDust").disableStats().setTextureName("redstone_dust"));
        blockRegistry.addObject(56, "diamond_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreDiamond").setTextureName("diamond_ore"));
        blockRegistry.addObject(57, "diamond_block", (new BlockCompressed(MapColor.field_151648_G)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setBlockName("blockDiamond").setTextureName("diamond_block"));
        blockRegistry.addObject(58, "crafting_table", (new BlockWorkbench()).setHardness(2.5F).setStepSound(soundTypeWood).setBlockName("workbench").setTextureName("crafting_table"));
        blockRegistry.addObject(59, "wheat", (new BlockCrops()).setBlockName("crops").setTextureName("wheat"));
        Block var4 = (new BlockFarmland()).setHardness(0.6F).setStepSound(soundTypeGravel).setBlockName("farmland").setTextureName("farmland");
        blockRegistry.addObject(60, "farmland", var4);
        blockRegistry.addObject(61, "furnace", (new BlockFurnace(false)).setHardness(3.5F).setStepSound(soundStoneFootstep).setBlockName("furnace").setCreativeTab(CreativeTabs.tabDecorations));
        blockRegistry.addObject(62, "lit_furnace", (new BlockFurnace(true)).setHardness(3.5F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setBlockName("furnace"));
        blockRegistry.addObject(63, "standing_sign", (new BlockSign(TileEntitySign.class, true)).setHardness(1.0F).setStepSound(soundTypeWood).setBlockName("sign").disableStats());
        blockRegistry.addObject(64, "wooden_door", (new BlockDoor(Material.wood)).setHardness(3.0F).setStepSound(soundTypeWood).setBlockName("doorWood").disableStats().setTextureName("door_wood"));
        blockRegistry.addObject(65, "ladder", (new BlockLadder()).setHardness(0.4F).setStepSound(soundTypeLadder).setBlockName("ladder").setTextureName("ladder"));
        blockRegistry.addObject(66, "rail", (new BlockRail()).setHardness(0.7F).setStepSound(soundTypeMetal).setBlockName("rail").setTextureName("rail_normal"));
        blockRegistry.addObject(67, "stone_stairs", (new BlockStairs(var0, 0)).setBlockName("stairsStone"));
        blockRegistry.addObject(68, "wall_sign", (new BlockSign(TileEntitySign.class, false)).setHardness(1.0F).setStepSound(soundTypeWood).setBlockName("sign").disableStats());
        blockRegistry.addObject(69, "lever", (new BlockLever()).setHardness(0.5F).setStepSound(soundTypeWood).setBlockName("lever").setTextureName("lever"));
        blockRegistry.addObject(70, "stone_pressure_plate", (new BlockPressurePlate("stone", Material.rock, BlockPressurePlate.Sensitivity.mobs)).setHardness(0.5F).setStepSound(soundStoneFootstep).setBlockName("pressurePlate"));
        blockRegistry.addObject(71, "iron_door", (new BlockDoor(Material.iron)).setHardness(5.0F).setStepSound(soundTypeMetal).setBlockName("doorIron").disableStats().setTextureName("door_iron"));
        blockRegistry.addObject(72, "wooden_pressure_plate", (new BlockPressurePlate("planks_oak", Material.wood, BlockPressurePlate.Sensitivity.everything)).setHardness(0.5F).setStepSound(soundTypeWood).setBlockName("pressurePlate"));
        blockRegistry.addObject(73, "redstone_ore", (new BlockRedstoneOre(false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setCreativeTab(CreativeTabs.tabBlock).setTextureName("redstone_ore"));
        blockRegistry.addObject(74, "lit_redstone_ore", (new BlockRedstoneOre(true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setTextureName("redstone_ore"));
        blockRegistry.addObject(75, "unlit_redstone_torch", (new BlockRedstoneTorch(false)).setHardness(0.0F).setStepSound(soundTypeWood).setBlockName("notGate").setTextureName("redstone_torch_off"));
        blockRegistry.addObject(76, "redstone_torch", (new BlockRedstoneTorch(true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundTypeWood).setBlockName("notGate").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("redstone_torch_on"));
        blockRegistry.addObject(77, "stone_button", (new BlockButtonStone()).setHardness(0.5F).setStepSound(soundStoneFootstep).setBlockName("button"));
        blockRegistry.addObject(78, "snow_layer", (new BlockSnow()).setHardness(0.1F).setStepSound(soundTypeSnow).setBlockName("snow").setLightOpacity(0).setTextureName("snow"));
        blockRegistry.addObject(79, "ice", (new BlockIce()).setHardness(0.5F).setLightOpacity(3).setStepSound(soundTypeGlass).setBlockName("ice").setTextureName("ice"));
        blockRegistry.addObject(80, "snow", (new BlockSnowBlock()).setHardness(0.2F).setStepSound(soundTypeSnow).setBlockName("snow").setTextureName("snow"));
        blockRegistry.addObject(81, "cactus", (new BlockCactus()).setHardness(0.4F).setStepSound(soundTypeCloth).setBlockName("cactus").setTextureName("cactus"));
        blockRegistry.addObject(82, "clay", (new BlockClay()).setHardness(0.6F).setStepSound(soundTypeGravel).setBlockName("clay").setTextureName("clay"));
        blockRegistry.addObject(83, "reeds", (new BlockReed()).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("reeds").disableStats().setTextureName("reeds"));
        blockRegistry.addObject(84, "jukebox", (new BlockJukebox()).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("jukebox").setTextureName("jukebox"));
        blockRegistry.addObject(85, "fence", (new BlockFence("planks_oak", Material.wood)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setBlockName("fence"));
        Block var5 = (new BlockPumpkin(false)).setHardness(1.0F).setStepSound(soundTypeWood).setBlockName("pumpkin").setTextureName("pumpkin");
        blockRegistry.addObject(86, "pumpkin", var5);
        blockRegistry.addObject(87, "netherrack", (new BlockNetherrack()).setHardness(0.4F).setStepSound(soundStoneFootstep).setBlockName("hellrock").setTextureName("netherrack"));
        blockRegistry.addObject(88, "soul_sand", (new BlockSoulSand()).setHardness(0.5F).setStepSound(field_149776_m).setBlockName("hellsand").setTextureName("soul_sand"));
        blockRegistry.addObject(89, "glowstone", (new BlockGlowstone(Material.glass)).setHardness(0.3F).setStepSound(soundTypeGlass).setLightValue(1.0F).setBlockName("lightgem").setTextureName("glowstone"));
        blockRegistry.addObject(90, "portal", (new BlockPortal()).setHardness(-1.0F).setStepSound(soundTypeGlass).setLightValue(0.75F).setBlockName("portal").setTextureName("portal"));
        blockRegistry.addObject(91, "lit_pumpkin", (new BlockPumpkin(true)).setHardness(1.0F).setStepSound(soundTypeWood).setLightValue(1.0F).setBlockName("litpumpkin").setTextureName("pumpkin"));
        blockRegistry.addObject(92, "cake", (new BlockCake()).setHardness(0.5F).setStepSound(soundTypeCloth).setBlockName("cake").disableStats().setTextureName("cake"));
        blockRegistry.addObject(93, "unpowered_repeater", (new BlockRedstoneRepeater(false)).setHardness(0.0F).setStepSound(soundTypeWood).setBlockName("diode").disableStats().setTextureName("repeater_off"));
        blockRegistry.addObject(94, "powered_repeater", (new BlockRedstoneRepeater(true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundTypeWood).setBlockName("diode").disableStats().setTextureName("repeater_on"));
        blockRegistry.addObject(95, "stained_glass", (new BlockStainedGlass(Material.glass)).setHardness(0.3F).setStepSound(soundTypeGlass).setBlockName("stainedGlass").setTextureName("glass"));
        blockRegistry.addObject(96, "trapdoor", (new BlockTrapDoor(Material.wood)).setHardness(3.0F).setStepSound(soundTypeWood).setBlockName("trapdoor").disableStats().setTextureName("trapdoor"));
        blockRegistry.addObject(97, "monster_egg", (new BlockSilverfish()).setHardness(0.75F).setBlockName("monsterStoneEgg"));
        Block var6 = (new BlockStoneBrick()).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stonebricksmooth").setTextureName("stonebrick");
        blockRegistry.addObject(98, "stonebrick", var6);
        blockRegistry.addObject(99, "brown_mushroom_block", (new BlockHugeMushroom(Material.wood, 0)).setHardness(0.2F).setStepSound(soundTypeWood).setBlockName("mushroom").setTextureName("mushroom_block"));
        blockRegistry.addObject(100, "red_mushroom_block", (new BlockHugeMushroom(Material.wood, 1)).setHardness(0.2F).setStepSound(soundTypeWood).setBlockName("mushroom").setTextureName("mushroom_block"));
        blockRegistry.addObject(101, "iron_bars", (new BlockPane("iron_bars", "iron_bars", Material.iron, true)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setBlockName("fenceIron"));
        blockRegistry.addObject(102, "glass_pane", (new BlockPane("glass", "glass_pane_top", Material.glass, false)).setHardness(0.3F).setStepSound(soundTypeGlass).setBlockName("thinGlass"));
        Block var7 = (new BlockMelon()).setHardness(1.0F).setStepSound(soundTypeWood).setBlockName("melon").setTextureName("melon");
        blockRegistry.addObject(103, "melon_block", var7);
        blockRegistry.addObject(104, "pumpkin_stem", (new BlockStem(var5)).setHardness(0.0F).setStepSound(soundTypeWood).setBlockName("pumpkinStem").setTextureName("pumpkin_stem"));
        blockRegistry.addObject(105, "melon_stem", (new BlockStem(var7)).setHardness(0.0F).setStepSound(soundTypeWood).setBlockName("pumpkinStem").setTextureName("melon_stem"));
        blockRegistry.addObject(106, "vine", (new BlockVine()).setHardness(0.2F).setStepSound(soundTypeGrass).setBlockName("vine").setTextureName("vine"));
        blockRegistry.addObject(107, "fence_gate", (new BlockFenceGate()).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setBlockName("fenceGate"));
        blockRegistry.addObject(108, "brick_stairs", (new BlockStairs(var3, 0)).setBlockName("stairsBrick"));
        blockRegistry.addObject(109, "stone_brick_stairs", (new BlockStairs(var6, 0)).setBlockName("stairsStoneBrickSmooth"));
        blockRegistry.addObject(110, "mycelium", (new BlockMycelium()).setHardness(0.6F).setStepSound(soundTypeGrass).setBlockName("mycel").setTextureName("mycelium"));
        blockRegistry.addObject(111, "waterlily", (new BlockLilyPad()).setHardness(0.0F).setStepSound(soundTypeGrass).setBlockName("waterlily").setTextureName("waterlily"));
        Block var8 = (new Block(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("netherBrick").setCreativeTab(CreativeTabs.tabBlock).setTextureName("nether_brick");
        blockRegistry.addObject(112, "nether_brick", var8);
        blockRegistry.addObject(113, "nether_brick_fence", (new BlockFence("nether_brick", Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("netherFence"));
        blockRegistry.addObject(114, "nether_brick_stairs", (new BlockStairs(var8, 0)).setBlockName("stairsNetherBrick"));
        blockRegistry.addObject(115, "nether_wart", (new BlockNetherWart()).setBlockName("netherStalk").setTextureName("nether_wart"));
        blockRegistry.addObject(116, "enchanting_table", (new BlockEnchantmentTable()).setHardness(5.0F).setResistance(2000.0F).setBlockName("enchantmentTable").setTextureName("enchanting_table"));
        blockRegistry.addObject(117, "brewing_stand", (new BlockBrewingStand()).setHardness(0.5F).setLightValue(0.125F).setBlockName("brewingStand").setTextureName("brewing_stand"));
        blockRegistry.addObject(118, "cauldron", (new BlockCauldron()).setHardness(2.0F).setBlockName("cauldron").setTextureName("cauldron"));
        blockRegistry.addObject(119, "end_portal", (new BlockEndPortal(Material.Portal)).setHardness(-1.0F).setResistance(6000000.0F));
        blockRegistry.addObject(120, "end_portal_frame", (new BlockEndPortalFrame()).setStepSound(soundTypeGlass).setLightValue(0.125F).setHardness(-1.0F).setBlockName("endPortalFrame").setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabDecorations).setTextureName("endframe"));
        blockRegistry.addObject(121, "end_stone", (new Block(Material.rock)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setBlockName("whiteStone").setCreativeTab(CreativeTabs.tabBlock).setTextureName("end_stone"));
        blockRegistry.addObject(122, "dragon_egg", (new BlockDragonEgg()).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setLightValue(0.125F).setBlockName("dragonEgg").setTextureName("dragon_egg"));
        blockRegistry.addObject(123, "redstone_lamp", (new BlockRedstoneLight(false)).setHardness(0.3F).setStepSound(soundTypeGlass).setBlockName("redstoneLight").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("redstone_lamp_off"));
        blockRegistry.addObject(124, "lit_redstone_lamp", (new BlockRedstoneLight(true)).setHardness(0.3F).setStepSound(soundTypeGlass).setBlockName("redstoneLight").setTextureName("redstone_lamp_on"));
        blockRegistry.addObject(125, "double_wooden_slab", (new BlockWoodSlab(true)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setBlockName("woodSlab"));
        blockRegistry.addObject(126, "wooden_slab", (new BlockWoodSlab(false)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setBlockName("woodSlab"));
        blockRegistry.addObject(127, "cocoa", (new BlockCocoa()).setHardness(0.2F).setResistance(5.0F).setStepSound(soundTypeWood).setBlockName("cocoa").setTextureName("cocoa"));
        blockRegistry.addObject(128, "sandstone_stairs", (new BlockStairs(var2, 0)).setBlockName("stairsSandStone"));
        blockRegistry.addObject(129, "emerald_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreEmerald").setTextureName("emerald_ore"));
        blockRegistry.addObject(130, "ender_chest", (new BlockEnderChest()).setHardness(22.5F).setResistance(1000.0F).setStepSound(soundStoneFootstep).setBlockName("enderChest").setLightValue(0.5F));
        blockRegistry.addObject(131, "tripwire_hook", (new BlockTripWireHook()).setBlockName("tripWireSource").setTextureName("trip_wire_source"));
        blockRegistry.addObject(132, "tripwire", (new BlockTripWire()).setBlockName("tripWire").setTextureName("trip_wire"));
        blockRegistry.addObject(133, "emerald_block", (new BlockCompressed(MapColor.field_151653_I)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setBlockName("blockEmerald").setTextureName("emerald_block"));
        blockRegistry.addObject(134, "spruce_stairs", (new BlockStairs(var1, 1)).setBlockName("stairsWoodSpruce"));
        blockRegistry.addObject(135, "birch_stairs", (new BlockStairs(var1, 2)).setBlockName("stairsWoodBirch"));
        blockRegistry.addObject(136, "jungle_stairs", (new BlockStairs(var1, 3)).setBlockName("stairsWoodJungle"));
        blockRegistry.addObject(137, "command_block", (new BlockCommandBlock()).setBlockUnbreakable().setResistance(6000000.0F).setBlockName("commandBlock").setTextureName("command_block"));
        blockRegistry.addObject(138, "beacon", (new BlockBeacon()).setBlockName("beacon").setLightValue(1.0F).setTextureName("beacon"));
        blockRegistry.addObject(139, "cobblestone_wall", (new BlockWall(var0)).setBlockName("cobbleWall"));
        blockRegistry.addObject(140, "flower_pot", (new BlockFlowerPot()).setHardness(0.0F).setStepSound(soundTypeStone).setBlockName("flowerPot").setTextureName("flower_pot"));
        blockRegistry.addObject(141, "carrots", (new BlockCarrot()).setBlockName("carrots").setTextureName("carrots"));
        blockRegistry.addObject(142, "potatoes", (new BlockPotato()).setBlockName("potatoes").setTextureName("potatoes"));
        blockRegistry.addObject(143, "wooden_button", (new BlockButtonWood()).setHardness(0.5F).setStepSound(soundTypeWood).setBlockName("button"));
        blockRegistry.addObject(144, "skull", (new BlockSkull()).setHardness(1.0F).setStepSound(soundStoneFootstep).setBlockName("skull").setTextureName("skull"));
        blockRegistry.addObject(145, "anvil", (new BlockAnvil()).setHardness(5.0F).setStepSound(soundTypeAnvil).setResistance(2000.0F).setBlockName("anvil"));
        blockRegistry.addObject(146, "trapped_chest", (new BlockChest(1)).setHardness(2.5F).setStepSound(soundTypeWood).setBlockName("chestTrap"));
        blockRegistry.addObject(147, "light_weighted_pressure_plate", (new BlockPressurePlateWeighted("gold_block", Material.iron, 15)).setHardness(0.5F).setStepSound(soundTypeWood).setBlockName("weightedPlate_light"));
        blockRegistry.addObject(148, "heavy_weighted_pressure_plate", (new BlockPressurePlateWeighted("iron_block", Material.iron, 150)).setHardness(0.5F).setStepSound(soundTypeWood).setBlockName("weightedPlate_heavy"));
        blockRegistry.addObject(149, "unpowered_comparator", (new BlockRedstoneComparator(false)).setHardness(0.0F).setStepSound(soundTypeWood).setBlockName("comparator").disableStats().setTextureName("comparator_off"));
        blockRegistry.addObject(150, "powered_comparator", (new BlockRedstoneComparator(true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundTypeWood).setBlockName("comparator").disableStats().setTextureName("comparator_on"));
        blockRegistry.addObject(151, "daylight_detector", (new BlockDaylightDetector()).setHardness(0.2F).setStepSound(soundTypeWood).setBlockName("daylightDetector").setTextureName("daylight_detector"));
        blockRegistry.addObject(152, "redstone_block", (new BlockCompressedPowered(MapColor.field_151656_f)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setBlockName("blockRedstone").setTextureName("redstone_block"));
        blockRegistry.addObject(153, "quartz_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("netherquartz").setTextureName("quartz_ore"));
        blockRegistry.addObject(154, "hopper", (new BlockHopper()).setHardness(3.0F).setResistance(8.0F).setStepSound(soundTypeWood).setBlockName("hopper").setTextureName("hopper"));
        Block var9 = (new BlockQuartz()).setStepSound(soundStoneFootstep).setHardness(0.8F).setBlockName("quartzBlock").setTextureName("quartz_block");
        blockRegistry.addObject(155, "quartz_block", var9);
        blockRegistry.addObject(156, "quartz_stairs", (new BlockStairs(var9, 0)).setBlockName("stairsQuartz"));
        blockRegistry.addObject(157, "activator_rail", (new BlockRailPowered()).setHardness(0.7F).setStepSound(soundTypeMetal).setBlockName("activatorRail").setTextureName("rail_activator"));
        blockRegistry.addObject(158, "dropper", (new BlockDropper()).setHardness(3.5F).setStepSound(soundStoneFootstep).setBlockName("dropper").setTextureName("dropper"));
        blockRegistry.addObject(159, "stained_hardened_clay", (new BlockColored(Material.rock)).setHardness(1.25F).setResistance(7.0F).setStepSound(soundStoneFootstep).setBlockName("clayHardenedStained").setTextureName("hardened_clay_stained"));
        blockRegistry.addObject(160, "stained_glass_pane", (new BlockStainedGlassPane()).setHardness(0.3F).setStepSound(soundTypeGlass).setBlockName("thinStainedGlass").setTextureName("glass"));
        blockRegistry.addObject(161, "leaves2", (new BlockNewLeaf()).setBlockName("leaves").setTextureName("leaves"));
        blockRegistry.addObject(162, "log2", (new BlockNewLog()).setBlockName("log").setTextureName("log"));
        blockRegistry.addObject(163, "acacia_stairs", (new BlockStairs(var1, 4)).setBlockName("stairsWoodAcacia"));
        blockRegistry.addObject(164, "dark_oak_stairs", (new BlockStairs(var1, 5)).setBlockName("stairsWoodDarkOak"));
        blockRegistry.addObject(170, "hay_block", (new BlockHay()).setHardness(0.5F).setStepSound(soundTypeGrass).setBlockName("hayBlock").setCreativeTab(CreativeTabs.tabBlock).setTextureName("hay_block"));
        blockRegistry.addObject(171, "carpet", (new BlockCarpet()).setHardness(0.1F).setStepSound(soundTypeCloth).setBlockName("woolCarpet").setLightOpacity(0));
        blockRegistry.addObject(172, "hardened_clay", (new BlockHardenedClay()).setHardness(1.25F).setResistance(7.0F).setStepSound(soundStoneFootstep).setBlockName("clayHardened").setTextureName("hardened_clay"));
        blockRegistry.addObject(173, "coal_block", (new Block(Material.rock)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("blockCoal").setCreativeTab(CreativeTabs.tabBlock).setTextureName("coal_block"));
        blockRegistry.addObject(174, "packed_ice", (new BlockPackedIce()).setHardness(0.5F).setStepSound(soundTypeGlass).setBlockName("icePacked").setTextureName("ice_packed"));
        blockRegistry.addObject(175, "double_plant", new BlockDoublePlant());
        Iterator var10 = blockRegistry.iterator();

        while (var10.hasNext())
        {
            Block var11 = (Block)var10.next();

            if (var11.blockMaterial == Material.air)
            {
                var11.field_149783_u = false;
            }
            else
            {
                boolean var12 = false;
                boolean var13 = var11.getRenderType() == 10;
                boolean var14 = var11 instanceof BlockSlab;
                boolean var15 = var11 == var4;
                boolean var16 = var11.canBlockGrass;
                boolean var17 = var11.lightOpacity == 0;

                if (var13 || var14 || var15 || var16 || var17)
                {
                    var12 = true;
                }

                var11.field_149783_u = var12;
            }
        }
    }

    protected Block(Material p_i45394_1_)
    {
        this.stepSound = soundTypeStone;
        this.blockParticleGravity = 1.0F;
        this.slipperiness = 0.6F;
        this.blockMaterial = p_i45394_1_;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.opaque = this.isOpaqueCube();
        this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
        this.canBlockGrass = !p_i45394_1_.getCanBlockGrass();
    }

    protected Block setStepSound(Block.SoundType p_149672_1_)
    {
        this.stepSound = p_149672_1_;
        return this;
    }

    public Block setLightOpacity(int p_149713_1_)
    {
        this.lightOpacity = p_149713_1_;
        return this;
    }

    protected Block setLightValue(float p_149715_1_)
    {
        this.lightValue = (int)(15.0F * p_149715_1_);
        return this;
    }

    protected Block setResistance(float p_149752_1_)
    {
        this.blockResistance = p_149752_1_ * 3.0F;
        return this;
    }

    public boolean isBlockNormalCube()
    {
        return this.blockMaterial.blocksMovement() && this.renderAsNormalBlock();
    }

    public boolean isNormalCube()
    {
        return this.blockMaterial.isOpaque() && this.renderAsNormalBlock() && !this.canProvidePower();
    }

    public boolean renderAsNormalBlock()
    {
        return true;
    }

    public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
    {
        return !this.blockMaterial.blocksMovement();
    }

    public int getRenderType()
    {
        return 0;
    }

    protected Block setHardness(float p_149711_1_)
    {
        this.blockHardness = p_149711_1_;

        if (this.blockResistance < p_149711_1_ * 5.0F)
        {
            this.blockResistance = p_149711_1_ * 5.0F;
        }

        return this;
    }

    protected Block setBlockUnbreakable()
    {
        this.setHardness(-1.0F);
        return this;
    }

    public float getBlockHardness(World p_149712_1_, int p_149712_2_, int p_149712_3_, int p_149712_4_)
    {
        return this.blockHardness;
    }

    protected Block setTickRandomly(boolean p_149675_1_)
    {
        this.needsRandomTick = p_149675_1_;
        return this;
    }

    public boolean getTickRandomly()
    {
        return this.needsRandomTick;
    }

    public boolean hasTileEntity()
    {
        return this.isBlockContainer;
    }

    protected final void setBlockBounds(float p_149676_1_, float p_149676_2_, float p_149676_3_, float p_149676_4_, float p_149676_5_, float p_149676_6_)
    {
        this.minX = (double)p_149676_1_;
        this.minY = (double)p_149676_2_;
        this.minZ = (double)p_149676_3_;
        this.maxX = (double)p_149676_4_;
        this.maxY = (double)p_149676_5_;
        this.maxZ = (double)p_149676_6_;
    }

    public int getBlockBrightness(IBlockAccess p_149677_1_, int p_149677_2_, int p_149677_3_, int p_149677_4_)
    {
        Block var5 = p_149677_1_.getBlock(p_149677_2_, p_149677_3_, p_149677_4_);
        int var6 = p_149677_1_.getLightBrightnessForSkyBlocks(p_149677_2_, p_149677_3_, p_149677_4_, var5.getLightValue());

        if (var6 == 0 && var5 instanceof BlockSlab)
        {
            --p_149677_3_;
            var5 = p_149677_1_.getBlock(p_149677_2_, p_149677_3_, p_149677_4_);
            return p_149677_1_.getLightBrightnessForSkyBlocks(p_149677_2_, p_149677_3_, p_149677_4_, var5.getLightValue());
        }
        else
        {
            return var6;
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        EventBlockSide event = new EventBlockSide(this);
        Nebula.BUS.post(event);
        if (event.isCancelled()) {
            return true;
        }

        return p_149646_5_ == 0 && this.minY > 0.0D ? true : (p_149646_5_ == 1 && this.maxY < 1.0D ? true : (p_149646_5_ == 2 && this.minZ > 0.0D ? true : (p_149646_5_ == 3 && this.maxZ < 1.0D ? true : (p_149646_5_ == 4 && this.minX > 0.0D ? true : (p_149646_5_ == 5 && this.maxX < 1.0D ? true : !p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_).isOpaqueCube())))));
    }

    public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_)
    {
        return p_149747_1_.getBlock(p_149747_2_, p_149747_3_, p_149747_4_).getMaterial().isSolid();
    }

    public IIcon getBlockTexture(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_)
    {
        return this.getIcon(p_149673_5_, p_149673_1_.getBlockMetadata(p_149673_2_, p_149673_3_, p_149673_4_));
    }

    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return this.blockIcon;
    }

    public final IIcon getBlockTextureFromSide(int p_149733_1_)
    {
        return this.getIcon(p_149733_1_, 0);
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
    {
        AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB((double)p_149633_2_ + this.minX, (double)p_149633_3_ + this.minY, (double)p_149633_4_ + this.minZ, (double)p_149633_2_ + this.maxX, (double)p_149633_3_ + this.maxY, (double)p_149633_4_ + this.maxZ);
        EventGetBox event = new EventGetBox(this, p_149633_2_, p_149633_3_, p_149633_4_, bb);
        return Nebula.BUS.post(event) ? event.getBox() : bb;
    }

    public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
    {
        AxisAlignedBB var8 = this.getCollisionBoundingBoxFromPool(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_);

        EventAddBox event = new EventAddBox(this, p_149743_7_,  p_149743_2_, p_149743_3_, p_149743_4_, var8);
        if (Nebula.BUS.post(event)) {
            var8 = event.getBox();
        }

        if (var8 != null && p_149743_5_.intersectsWith(var8))
        {
            p_149743_6_.add(var8);
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB((double)p_149668_2_ + this.minX, (double)p_149668_3_ + this.minY, (double)p_149668_4_ + this.minZ, (double)p_149668_2_ + this.maxX, (double)p_149668_3_ + this.maxY, (double)p_149668_4_ + this.maxZ);
        EventGetBox event = new EventGetBox(this, p_149668_2_, p_149668_3_, p_149668_4_, bb);
        return Nebula.BUS.post(event) ? event.getBox() : bb;
    }

    public boolean isOpaqueCube()
    {
        return true;
    }

    public boolean canCollideCheck(int p_149678_1_, boolean p_149678_2_)
    {
        return this.isCollidable();
    }

    public boolean isCollidable()
    {
        return true;
    }

    public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {}

    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {}

    public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_) {}

    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {}

    public int tickRate(World p_149738_1_)
    {
        return 10;
    }

    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_) {}

    public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_) {}

    public int quantityDropped(Random p_149745_1_)
    {
        return 1;
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Item.getItemFromBlock(this);
    }

    public float getPlayerRelativeBlockHardness(EntityPlayer p_149737_1_, World p_149737_2_, int p_149737_3_, int p_149737_4_, int p_149737_5_)
    {
        float var6 = this.getBlockHardness(p_149737_2_, p_149737_3_, p_149737_4_, p_149737_5_);
        return var6 < 0.0F ? 0.0F : (!p_149737_1_.canHarvestBlock(this) ? p_149737_1_.getCurrentPlayerStrVsBlock(this, false) / var6 / 100.0F : p_149737_1_.getCurrentPlayerStrVsBlock(this, true) / var6 / 30.0F);
    }

    public final void dropBlockAsItem(World p_149697_1_, int p_149697_2_, int p_149697_3_, int p_149697_4_, int p_149697_5_, int p_149697_6_)
    {
        this.dropBlockAsItemWithChance(p_149697_1_, p_149697_2_, p_149697_3_, p_149697_4_, p_149697_5_, 1.0F, p_149697_6_);
    }

    public void dropBlockAsItemWithChance(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_)
    {
        if (!p_149690_1_.isClient)
        {
            int var8 = this.quantityDroppedWithBonus(p_149690_7_, p_149690_1_.rand);

            for (int var9 = 0; var9 < var8; ++var9)
            {
                if (p_149690_1_.rand.nextFloat() <= p_149690_6_)
                {
                    Item var10 = this.getItemDropped(p_149690_5_, p_149690_1_.rand, p_149690_7_);

                    if (var10 != null)
                    {
                        this.dropBlockAsItem_do(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, new ItemStack(var10, 1, this.damageDropped(p_149690_5_)));
                    }
                }
            }
        }
    }

    protected void dropBlockAsItem_do(World p_149642_1_, int p_149642_2_, int p_149642_3_, int p_149642_4_, ItemStack p_149642_5_)
    {
        if (!p_149642_1_.isClient && p_149642_1_.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float var6 = 0.7F;
            double var7 = (double)(p_149642_1_.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            double var9 = (double)(p_149642_1_.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            double var11 = (double)(p_149642_1_.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
            EntityItem var13 = new EntityItem(p_149642_1_, (double)p_149642_2_ + var7, (double)p_149642_3_ + var9, (double)p_149642_4_ + var11, p_149642_5_);
            var13.delayBeforeCanPickup = 10;
            p_149642_1_.spawnEntityInWorld(var13);
        }
    }

    protected void dropXpOnBlockBreak(World p_149657_1_, int p_149657_2_, int p_149657_3_, int p_149657_4_, int p_149657_5_)
    {
        if (!p_149657_1_.isClient)
        {
            while (p_149657_5_ > 0)
            {
                int var6 = EntityXPOrb.getXPSplit(p_149657_5_);
                p_149657_5_ -= var6;
                p_149657_1_.spawnEntityInWorld(new EntityXPOrb(p_149657_1_, (double)p_149657_2_ + 0.5D, (double)p_149657_3_ + 0.5D, (double)p_149657_4_ + 0.5D, var6));
            }
        }
    }

    public int damageDropped(int p_149692_1_)
    {
        return 0;
    }

    public float getExplosionResistance(Entity p_149638_1_)
    {
        return this.blockResistance / 5.0F;
    }

    public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_, Vec3 p_149731_5_, Vec3 p_149731_6_)
    {
        this.setBlockBoundsBasedOnState(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_);
        p_149731_5_ = p_149731_5_.addVector((double)(-p_149731_2_), (double)(-p_149731_3_), (double)(-p_149731_4_));
        p_149731_6_ = p_149731_6_.addVector((double)(-p_149731_2_), (double)(-p_149731_3_), (double)(-p_149731_4_));
        Vec3 var7 = p_149731_5_.getIntermediateWithXValue(p_149731_6_, this.minX);
        Vec3 var8 = p_149731_5_.getIntermediateWithXValue(p_149731_6_, this.maxX);
        Vec3 var9 = p_149731_5_.getIntermediateWithYValue(p_149731_6_, this.minY);
        Vec3 var10 = p_149731_5_.getIntermediateWithYValue(p_149731_6_, this.maxY);
        Vec3 var11 = p_149731_5_.getIntermediateWithZValue(p_149731_6_, this.minZ);
        Vec3 var12 = p_149731_5_.getIntermediateWithZValue(p_149731_6_, this.maxZ);

        if (!this.isVecInsideYZBounds(var7))
        {
            var7 = null;
        }

        if (!this.isVecInsideYZBounds(var8))
        {
            var8 = null;
        }

        if (!this.isVecInsideXZBounds(var9))
        {
            var9 = null;
        }

        if (!this.isVecInsideXZBounds(var10))
        {
            var10 = null;
        }

        if (!this.isVecInsideXYBounds(var11))
        {
            var11 = null;
        }

        if (!this.isVecInsideXYBounds(var12))
        {
            var12 = null;
        }

        Vec3 var13 = null;

        if (var7 != null && (var13 == null || p_149731_5_.squareDistanceTo(var7) < p_149731_5_.squareDistanceTo(var13)))
        {
            var13 = var7;
        }

        if (var8 != null && (var13 == null || p_149731_5_.squareDistanceTo(var8) < p_149731_5_.squareDistanceTo(var13)))
        {
            var13 = var8;
        }

        if (var9 != null && (var13 == null || p_149731_5_.squareDistanceTo(var9) < p_149731_5_.squareDistanceTo(var13)))
        {
            var13 = var9;
        }

        if (var10 != null && (var13 == null || p_149731_5_.squareDistanceTo(var10) < p_149731_5_.squareDistanceTo(var13)))
        {
            var13 = var10;
        }

        if (var11 != null && (var13 == null || p_149731_5_.squareDistanceTo(var11) < p_149731_5_.squareDistanceTo(var13)))
        {
            var13 = var11;
        }

        if (var12 != null && (var13 == null || p_149731_5_.squareDistanceTo(var12) < p_149731_5_.squareDistanceTo(var13)))
        {
            var13 = var12;
        }

        if (var13 == null)
        {
            return null;
        }
        else
        {
            byte var14 = -1;

            if (var13 == var7)
            {
                var14 = 4;
            }

            if (var13 == var8)
            {
                var14 = 5;
            }

            if (var13 == var9)
            {
                var14 = 0;
            }

            if (var13 == var10)
            {
                var14 = 1;
            }

            if (var13 == var11)
            {
                var14 = 2;
            }

            if (var13 == var12)
            {
                var14 = 3;
            }

            return new MovingObjectPosition(p_149731_2_, p_149731_3_, p_149731_4_, var14, var13.addVector((double)p_149731_2_, (double)p_149731_3_, (double)p_149731_4_));
        }
    }

    private boolean isVecInsideYZBounds(Vec3 p_149654_1_)
    {
        return p_149654_1_ == null ? false : p_149654_1_.yCoord >= this.minY && p_149654_1_.yCoord <= this.maxY && p_149654_1_.zCoord >= this.minZ && p_149654_1_.zCoord <= this.maxZ;
    }

    private boolean isVecInsideXZBounds(Vec3 p_149687_1_)
    {
        return p_149687_1_ == null ? false : p_149687_1_.xCoord >= this.minX && p_149687_1_.xCoord <= this.maxX && p_149687_1_.zCoord >= this.minZ && p_149687_1_.zCoord <= this.maxZ;
    }

    private boolean isVecInsideXYBounds(Vec3 p_149661_1_)
    {
        return p_149661_1_ == null ? false : p_149661_1_.xCoord >= this.minX && p_149661_1_.xCoord <= this.maxX && p_149661_1_.yCoord >= this.minY && p_149661_1_.yCoord <= this.maxY;
    }

    public void onBlockDestroyedByExplosion(World p_149723_1_, int p_149723_2_, int p_149723_3_, int p_149723_4_, Explosion p_149723_5_) {}

    public int getRenderBlockPass()
    {
        EventRenderPass event = new EventRenderPass(this, 0);
        Nebula.BUS.post(event);
        if (event.isCancelled()) {
            return event.getRenderPass();
        }

        return 0;
    }

    public boolean canReplace(World p_149705_1_, int p_149705_2_, int p_149705_3_, int p_149705_4_, int p_149705_5_, ItemStack p_149705_6_)
    {
        return this.canPlaceBlockOnSide(p_149705_1_, p_149705_2_, p_149705_3_, p_149705_4_, p_149705_5_);
    }

    public boolean canPlaceBlockOnSide(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_)
    {
        return this.canPlaceBlockAt(p_149707_1_, p_149707_2_, p_149707_3_, p_149707_4_);
    }

    public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return p_149742_1_.getBlock(p_149742_2_, p_149742_3_, p_149742_4_).blockMaterial.isReplaceable();
    }

    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        return false;
    }

    public void onEntityWalking(World p_149724_1_, int p_149724_2_, int p_149724_3_, int p_149724_4_, Entity p_149724_5_) {}

    public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        return p_149660_9_;
    }

    public void onBlockClicked(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_) {}

    public void velocityToAddToEntity(World p_149640_1_, int p_149640_2_, int p_149640_3_, int p_149640_4_, Entity p_149640_5_, Vec3 p_149640_6_) {}

    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_) {}

    public final double getBlockBoundsMinX()
    {
        return this.minX;
    }

    public final double getBlockBoundsMaxX()
    {
        return this.maxX;
    }

    public final double getBlockBoundsMinY()
    {
        return this.minY;
    }

    public final double getBlockBoundsMaxY()
    {
        return this.maxY;
    }

    public final double getBlockBoundsMinZ()
    {
        return this.minZ;
    }

    public final double getBlockBoundsMaxZ()
    {
        return this.maxZ;
    }

    public int getBlockColor()
    {
        return 16777215;
    }

    public int getRenderColor(int p_149741_1_)
    {
        return 16777215;
    }

    public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
    {
        return 16777215;
    }

    public int isProvidingWeakPower(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        return 0;
    }

    public boolean canProvidePower()
    {
        return false;
    }

    public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_) {}

    public int isProvidingStrongPower(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        return 0;
    }

    public void setBlockBoundsForItemRender() {}

    public void harvestBlock(World p_149636_1_, EntityPlayer p_149636_2_, int p_149636_3_, int p_149636_4_, int p_149636_5_, int p_149636_6_)
    {
        p_149636_2_.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        p_149636_2_.addExhaustion(0.025F);

        if (this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(p_149636_2_))
        {
            ItemStack var8 = this.createStackedBlock(p_149636_6_);

            if (var8 != null)
            {
                this.dropBlockAsItem_do(p_149636_1_, p_149636_3_, p_149636_4_, p_149636_5_, var8);
            }
        }
        else
        {
            int var7 = EnchantmentHelper.getFortuneModifier(p_149636_2_);
            this.dropBlockAsItem(p_149636_1_, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_, var7);
        }
    }

    protected boolean canSilkHarvest()
    {
        return this.renderAsNormalBlock() && !this.isBlockContainer;
    }

    protected ItemStack createStackedBlock(int p_149644_1_)
    {
        int var2 = 0;
        Item var3 = Item.getItemFromBlock(this);

        if (var3 != null && var3.getHasSubtypes())
        {
            var2 = p_149644_1_;
        }

        return new ItemStack(var3, 1, var2);
    }

    public int quantityDroppedWithBonus(int p_149679_1_, Random p_149679_2_)
    {
        return this.quantityDropped(p_149679_2_);
    }

    public boolean canBlockStay(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
    {
        return true;
    }

    public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {}

    public void onPostBlockPlaced(World p_149714_1_, int p_149714_2_, int p_149714_3_, int p_149714_4_, int p_149714_5_) {}

    public Block setBlockName(String p_149663_1_)
    {
        this.unlocalizedName = p_149663_1_;
        return this;
    }

    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
    }

    public String getUnlocalizedName()
    {
        return "tile." + this.unlocalizedName;
    }

    public boolean onBlockEventReceived(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_)
    {
        return false;
    }

    public boolean getEnableStats()
    {
        return this.enableStats;
    }

    protected Block disableStats()
    {
        this.enableStats = false;
        return this;
    }

    public int getMobilityFlag()
    {
        return this.blockMaterial.getMaterialMobility();
    }

    public float getAmbientOcclusionLightValue()
    {
        return this.isBlockNormalCube() ? 0.2F : 1.0F;
    }

    public void onFallenUpon(World p_149746_1_, int p_149746_2_, int p_149746_3_, int p_149746_4_, Entity p_149746_5_, float p_149746_6_) {}

    public Item getItemPicked(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.getItemFromBlock(this);
    }

    public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
        return this.damageDropped(p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_));
    }

    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
    }

    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return this.displayOnCreativeTab;
    }

    public Block setCreativeTab(CreativeTabs p_149647_1_)
    {
        this.displayOnCreativeTab = p_149647_1_;
        return this;
    }

    public void onBlockHarvested(World p_149681_1_, int p_149681_2_, int p_149681_3_, int p_149681_4_, int p_149681_5_, EntityPlayer p_149681_6_) {}

    public void onBlockPreDestroy(World p_149725_1_, int p_149725_2_, int p_149725_3_, int p_149725_4_, int p_149725_5_) {}

    public void fillWithRain(World p_149639_1_, int p_149639_2_, int p_149639_3_, int p_149639_4_) {}

    public boolean isFlowerPot()
    {
        return false;
    }

    public boolean func_149698_L()
    {
        return true;
    }

    public boolean canDropFromExplosion(Explosion p_149659_1_)
    {
        return true;
    }

    public boolean isAssociatedBlock(Block p_149667_1_)
    {
        return this == p_149667_1_;
    }

    public static boolean isEqualTo(Block p_149680_0_, Block p_149680_1_)
    {
        return p_149680_0_ != null && p_149680_1_ != null ? (p_149680_0_ == p_149680_1_ ? true : p_149680_0_.isAssociatedBlock(p_149680_1_)) : false;
    }

    public boolean hasComparatorInputOverride()
    {
        return false;
    }

    public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
    {
        return 0;
    }

    protected Block setTextureName(String p_149658_1_)
    {
        this.textureName = p_149658_1_;
        return this;
    }

    protected String getTextureName()
    {
        return this.textureName == null ? "MISSING_ICON_BLOCK_" + getIdFromBlock(this) + "_" + this.unlocalizedName : this.textureName;
    }

    public IIcon func_149735_b(int p_149735_1_, int p_149735_2_)
    {
        return this.getIcon(p_149735_1_, p_149735_2_);
    }

    public void registerIcons(IIconRegister p_149651_1_)
    {
        this.blockIcon = p_149651_1_.registerIcon(this.getTextureName());
    }

    public String getItemIconName()
    {
        return null;
    }

    public static class SoundType
    {
        public final String field_150501_a;
        public final float field_150499_b;
        public final float field_150500_c;
        private static final String __OBFID = "CL_00000203";

        public SoundType(String p_i45393_1_, float p_i45393_2_, float p_i45393_3_)
        {
            this.field_150501_a = p_i45393_1_;
            this.field_150499_b = p_i45393_2_;
            this.field_150500_c = p_i45393_3_;
        }

        public float func_150497_c()
        {
            return this.field_150499_b;
        }

        public float func_150494_d()
        {
            return this.field_150500_c;
        }

        public String func_150495_a()
        {
            return "dig." + this.field_150501_a;
        }

        public String func_150498_e()
        {
            return "step." + this.field_150501_a;
        }

        public String func_150496_b()
        {
            return this.func_150495_a();
        }
    }
}
