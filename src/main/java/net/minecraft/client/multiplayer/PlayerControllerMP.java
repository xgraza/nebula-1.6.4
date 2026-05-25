package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import us.nebula.client.Nebula;
import us.nebula.client.listener.EventBus;
import us.nebula.client.cheat.impl.player.InteractCheat;
import us.nebula.client.listener.event.player.EventAttackBlock;
import us.nebula.client.util.player.ChatUtil;

public class PlayerControllerMP
{
    /**
     * If features should be able to override the vanilla minecraft checks for block breaking
     */
    public static boolean ALLOW_BREAK_OVERRIDE = false;

    /**
     * The Minecraft instance.
     */
    private final Minecraft mc;
    private final NetHandlerPlayClient netClientHandler;

    /**
     * PosX of the current block being destroyed
     */
    public int currentBlockX = -1;

    /**
     * PosY of the current block being destroyed
     */
    public int currentBlockY = -1;

    /**
     * PosZ of the current block being destroyed
     */
    public int currentblockZ = -1;

    /**
     * The Item currently being used to destroy a block
     */
    private ItemStack currentItemHittingBlock;

    /**
     * Current block damage (MP)
     */
    public float curBlockDamageMP;

    /**
     * Tick counter, when it hits 4 it resets back to 0 and plays the step sound
     */
    private float stepSoundTickCounter;

    /**
     * Delays the first damage on the block after the first click on the block
     */
    public int blockHitDelay;

    /**
     * Tells if the player is hitting a block
     */
    public boolean isHittingBlock;

    /**
     * Current game type for the player
     */
    public WorldSettings.GameType currentGameType;

    /**
     * Index of the current item held by the player in the inventory hotbar
     */
    private int currentPlayerItem;
    private static final String __OBFID = "CL_00000881";

    public PlayerControllerMP(Minecraft p_i45062_1_, NetHandlerPlayClient p_i45062_2_)
    {
        this.currentGameType = WorldSettings.GameType.SURVIVAL;
        this.mc = p_i45062_1_;
        this.netClientHandler = p_i45062_2_;
    }

    /**
     * Block dig operation in creative mode (instantly digs the block).
     */
    public static void clickBlockCreative(Minecraft par0Minecraft, PlayerControllerMP par1PlayerControllerMP, int par2, int par3, int par4, int par5)
    {
        if (!par0Minecraft.theWorld.extinguishFire(par0Minecraft.thePlayer, par2, par3, par4, par5))
        {
            par1PlayerControllerMP.onPlayerDestroyBlock(par2, par3, par4, par5);
        }
    }

    /**
     * Sets player capabilities depending on current gametype. params: player
     */
    public void setPlayerCapabilities(EntityPlayer par1EntityPlayer)
    {
        this.currentGameType.configurePlayerCapabilities(par1EntityPlayer.capabilities);
    }

    /**
     * If modified to return true, the player spins around slowly around (0, 68.5, 0). The GUI is disabled, the view is
     * set to first person, and both chat and menu are disabled. Unless the server is modified to ignore illegal
     * stances, attempting to enter a world at all will result in an immediate kick due to an illegal stance. Appears to
     * be left-over debug, or demo code.
     */
    public boolean enableEverythingIsScrewedUpMode()
    {
        return false;
    }

    /**
     * Sets the game type for the player.
     */
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        this.currentGameType = par1EnumGameType;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    /**
     * Flips the player around. Args: player
     */
    public void flipPlayer(EntityPlayer par1EntityPlayer)
    {
        par1EntityPlayer.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    /**
     * Called when a player completes the destruction of a block
     */
    public boolean onPlayerDestroyBlock(int par1, int par2, int par3, int par4)
    {
        if (this.currentGameType.isAdventure() && !this.mc.thePlayer.isCurrentToolAdventureModeExempt(par1, par2, par3))
        {
            return false;
        } else if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
        {
            return false;
        } else
        {
            WorldClient var5 = this.mc.theWorld;
            Block var6 = var5.getBlock(par1, par2, par3);

            if (var6.getMaterial() == Material.air)
            {
                return false;
            } else
            {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(2, par1, par2, par3, par4));
                var5.playAuxSFX(2001, par1, par2, par3, Block.getIdFromBlock(var6) + (var5.getBlockMetadata(par1, par2, par3) << 12));
                int var7 = var5.getBlockMetadata(par1, par2, par3);
                boolean var8 = var5.setBlockToAir(par1, par2, par3);

                if (var8)
                {
                    var6.onBlockDestroyedByPlayer(var5, par1, par2, par3, var7);
                }

                this.currentBlockY = -1;

                if (!this.currentGameType.isCreative())
                {
                    ItemStack var9 = this.mc.thePlayer.getCurrentEquippedItem();

                    if (var9 != null)
                    {
                        var9.func_150999_a(var5, var6, par1, par2, par3, this.mc.thePlayer);

                        if (var9.stackSize == 0)
                        {
                            this.mc.thePlayer.destroyCurrentEquippedItem();
                        }
                    }
                }

                return var8;
            }
        }
    }

    /**
     * Called by Minecraft class when the player is hitting a block with an item. Args: x, y, z, side
     */
    public void clickBlock(int par1, int par2, int par3, int par4)
    {
        if (EventBus.dispatch(new EventAttackBlock(par1, par2, par3, par4)))
        {
            return;
        }
        if (!this.currentGameType.isAdventure() || this.mc.thePlayer.isCurrentToolAdventureModeExempt(par1, par2, par3))
        {
            if (this.currentGameType.isCreative())
            {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, par1, par2, par3, par4));
                clickBlockCreative(this.mc, this, par1, par2, par3, par4);
                this.blockHitDelay = 5;
            } else if (!this.isHittingBlock || !this.sameToolAndBlock(par1, par2, par3))
            {
                if (this.isHittingBlock)
                {
                    this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, par4));
                }

                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, par1, par2, par3, par4));
                Block var5 = this.mc.theWorld.getBlock(par1, par2, par3);
                boolean var6 = var5.getMaterial() != Material.air;

                if (var6 && this.curBlockDamageMP == 0.0F)
                {
                    var5.onBlockClicked(this.mc.theWorld, par1, par2, par3, this.mc.thePlayer);
                }

                if (var6 && var5.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, par1, par2, par3) >= 1.0F)
                {
                    this.onPlayerDestroyBlock(par1, par2, par3, par4);
                } else
                {
                    this.isHittingBlock = true;
                    this.currentBlockX = par1;
                    this.currentBlockY = par2;
                    this.currentblockZ = par3;
                    this.currentItemHittingBlock = Nebula.INSTANCE.getInventoryManager().getStack();
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.getEntityId(), this.currentBlockX, this.currentBlockY, this.currentblockZ, (int) (this.curBlockDamageMP * 10.0F) - 1);
                }
            }
        }
    }

    /**
     * Resets current block damage and isHittingBlock
     */
    public void resetBlockRemoving()
    {
        if (this.isHittingBlock)
        {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, -1));
        }

        this.isHittingBlock = false;
        this.curBlockDamageMP = 0.0F;
        this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.getEntityId(), this.currentBlockX, this.currentBlockY, this.currentblockZ, -1);
    }

    /**
     * Called when a player damages a block and updates damage counters
     */
    public void onPlayerDamageBlock(int par1, int par2, int par3, int par4)
    {
        this.syncCurrentPlayItem();

        if (this.blockHitDelay > 0)
        {
            --this.blockHitDelay;
        } else if (this.currentGameType.isCreative())
        {
            this.blockHitDelay = 5;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, par1, par2, par3, par4));
            clickBlockCreative(this.mc, this, par1, par2, par3, par4);
        } else
        {
            if (this.sameToolAndBlock(par1, par2, par3))
            {
                Block var5 = this.mc.theWorld.getBlock(par1, par2, par3);

                if (var5.getMaterial() == Material.air)
                {
                    this.isHittingBlock = false;
                    return;
                }

                this.curBlockDamageMP += var5.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, par1, par2, par3);

                if (this.stepSoundTickCounter % 4.0F == 0.0F)
                {
                    this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(var5.stepSound.func_150498_e()), (var5.stepSound.func_150497_c() + 1.0F) / 8.0F, var5.stepSound.func_150494_d() * 0.5F, (float) par1 + 0.5F, (float) par2 + 0.5F, (float) par3 + 0.5F));
                }

                ++this.stepSoundTickCounter;

                if (this.curBlockDamageMP >= 1.0F)
                {
                    this.isHittingBlock = false;
                    //this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(2, par1, par2, par3, par4));
                    this.onPlayerDestroyBlock(par1, par2, par3, par4);
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                }

                this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.getEntityId(), this.currentBlockX, this.currentBlockY, this.currentblockZ, (int) (this.curBlockDamageMP * 10.0F) - 1);
            } else
            {
                this.clickBlock(par1, par2, par3, par4);
            }
        }
    }

    /**
     * player reach distance = 4F
     */
    public float getBlockReachDistance()
    {
        if (InteractCheat.INSTANCE.isToggled())
        {
            return InteractCheat.INSTANCE.placeReachSetting.getValue();
        }
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

    public void updateController()
    {
        this.syncCurrentPlayItem();

        if (this.netClientHandler.getNetworkManager().isChannelOpen())
        {
            this.netClientHandler.getNetworkManager().processReceivedPackets();
        } else if (this.netClientHandler.getNetworkManager().getExitMessage() != null)
        {
            this.netClientHandler.getNetworkManager().getNetHandler().onDisconnect(this.netClientHandler.getNetworkManager().getExitMessage());
        } else
        {
            this.netClientHandler.getNetworkManager().getNetHandler().onDisconnect(new ChatComponentText("Disconnected from server"));
        }
    }

    public boolean sameToolAndBlock(int par1, int par2, int par3)
    {
        ItemStack var4 = Nebula.INSTANCE.getInventoryManager().getStack();
        boolean var5 = this.currentItemHittingBlock == null && var4 == null;

        if (this.currentItemHittingBlock != null && var4 != null)
        {
            var5 = var4.getItem() == this.currentItemHittingBlock.getItem() && ItemStack.areItemStackTagsEqual(var4, this.currentItemHittingBlock) && (var4.isItemStackDamageable() || var4.getItemDamage() == this.currentItemHittingBlock.getItemDamage());
        }

        return par1 == this.currentBlockX && par2 == this.currentBlockY && par3 == this.currentblockZ && var5;
    }

    /**
     * Syncs the current player item with the server
     */
    private void syncCurrentPlayItem()
    {
        int var1 = this.mc.thePlayer.inventory.currentItem;

        if (var1 != this.currentPlayerItem)
        {
            this.currentPlayerItem = var1;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
        }
    }

    /**
     * Handles a players right click. Args: player, world, x, y, z, side, hitVec
     */
    public boolean onPlayerRightClick(EntityPlayer player, World world, ItemStack stack, int x, int y, int z, int side, Vec3 hitVec)
    {
        //ChatUtil.sendNebula("XYZ: (%s, %s, %s), Side: %s, Vec: %s", x, y, z, side, hitVec);
        syncCurrentPlayItem();
        final float facingX = (float) hitVec.xCoord - (float) x;
        final float facingY = (float) hitVec.yCoord - (float) y;
        final float facingZ = (float) hitVec.zCoord - (float) z;

        boolean sneaking = (!player.isSneaking() || player.getHeldItem() == null)
                && world.getBlock(x, y, z).onBlockActivated(world, x, y, z, player, side, facingX, facingY, facingZ);

        if (!sneaking && stack != null && stack.getItem() instanceof ItemBlock)
        {
            final ItemBlock blockItem = (ItemBlock) stack.getItem();
            if (!blockItem.canPlaceBlock(world, x, y, z, side, player, stack))
            {
                return false;
            }
        }

        netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(
                x, y, z, side, stack, facingX, facingY, facingZ));

        if (sneaking)
        {
            return true;
        } else if (stack == null)
        {
            return false;
        } else if (this.currentGameType.isCreative())
        {
            final int damage = stack.getItemDamage();
            final int size = stack.stackSize;
            boolean result = stack.tryPlaceItemIntoWorld(player, world, x, y, z, side, facingX, facingY, facingZ);
            stack.setItemDamage(damage);
            stack.stackSize = size;
            return result;
        } else
        {
            return stack.tryPlaceItemIntoWorld(player, world, x, y, z, side, facingX, facingY, facingZ);
        }
    }

    /**
     * Notifies the server of things like consuming food, etc...
     */
    public boolean sendUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, par1EntityPlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
        int var4 = par3ItemStack.stackSize;
        ItemStack var5 = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);

        if (var5 == par3ItemStack && (var5 == null || var5.stackSize == var4))
        {
            return false;
        } else
        {
            par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = var5;

            if (var5.stackSize == 0)
            {
                par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
            }

            return true;
        }
    }

    public EntityClientPlayerMP func_147493_a(World p_147493_1_, StatFileWriter p_147493_2_)
    {
        return new EntityClientPlayerMP(this.mc, p_147493_1_, this.mc.getSession(), this.netClientHandler, p_147493_2_);
    }

    /**
     * Attacks an entity
     */
    public void attackEntity(EntityPlayer par1EntityPlayer, Entity par2Entity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(par2Entity, C02PacketUseEntity.Action.ATTACK));
        par1EntityPlayer.attackTargetEntityWithCurrentItem(par2Entity);
    }

    /**
     * Send packet to server - player is interacting with another entity (left click)
     */
    public boolean interactWithEntitySendPacket(EntityPlayer par1EntityPlayer, Entity par2Entity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(par2Entity, C02PacketUseEntity.Action.INTERACT));
        return par1EntityPlayer.interactWith(par2Entity);
    }

    public ItemStack windowClick(int windowId, int slot, int mouseButton, int action, EntityPlayer par5EntityPlayer)
    {
        short transactionId = par5EntityPlayer.openContainer.getNextTransactionID(par5EntityPlayer.inventory);
        ItemStack stack = par5EntityPlayer.openContainer.slotClick(slot, mouseButton, action, par5EntityPlayer);
        this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slot, mouseButton, action, stack, transactionId));
        return stack;
    }

    /**
     * GuiEnchantment uses this during multiplayer to tell PlayerControllerMP to send a packet indicating the
     * enchantment action the player has taken.
     */
    public void sendEnchantPacket(int par1, int par2)
    {
        this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(par1, par2));
    }

    /**
     * Used in PlayerControllerMP to update the server with an ItemStack in a slot.
     */
    public void sendSlotPacket(ItemStack par1ItemStack, int par2)
    {
        if (this.currentGameType.isCreative())
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(par2, par1ItemStack));
        }
    }

    /**
     * Sends a Packet107 to the server to drop the item on the ground
     */
    public void sendPacketDropItem(ItemStack par1ItemStack)
    {
        if (this.currentGameType.isCreative() && par1ItemStack != null)
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, par1ItemStack));
        }
    }

    public void onStoppedUsingItem(EntityPlayer par1EntityPlayer)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
        par1EntityPlayer.stopUsingItem();
    }

    public boolean gameIsSurvivalOrAdventure()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    /**
     * Checks if the player is not creative, used for checking if it should break a block instantly
     */
    public boolean isNotCreative()
    {
        return !this.currentGameType.isCreative();
    }

    /**
     * returns true if player is in creative mode
     */
    public boolean isInCreativeMode()
    {
        return this.currentGameType.isCreative();
    }

    /**
     * true for hitting entities far away.
     */
    public boolean extendedReach()
    {
        return this.currentGameType.isCreative();
    }

    public boolean isRidingHorse()
    {
        return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
    }
}
