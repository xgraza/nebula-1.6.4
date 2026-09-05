package net.minecraft.client.multiplayer;

import ez.nebula.client.api.listener.event.world.EventPlace;
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
import ez.nebula.client.Nebula;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.impl.module.player.InteractModule;
import ez.nebula.client.api.listener.event.player.EventAttackBlock;

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
    public int currentPlayerItem;

    public PlayerControllerMP(Minecraft mc, NetHandlerPlayClient netHandler)
    {
        this.currentGameType = WorldSettings.GameType.SURVIVAL;
        this.mc = mc;
        this.netClientHandler = netHandler;
    }

    /**
     * Block dig operation in creative mode (instantly digs the block).
     */
    public static void clickBlockCreative(Minecraft par0Minecraft, PlayerControllerMP par1PlayerControllerMP, int x, int y, int z, int side)
    {
        if (!par0Minecraft.theWorld.extinguishFire(par0Minecraft.thePlayer, x, y, z, side))
        {
            par1PlayerControllerMP.onPlayerDestroyBlock(x, y, z, side);
        }
    }

    /**
     * Sets player capabilities depending on current gametype. params: player
     */
    public void setPlayerCapabilities(EntityPlayer player)
    {
        this.currentGameType.configurePlayerCapabilities(player.capabilities);
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
    public void setGameType(WorldSettings.GameType gameType)
    {
        this.currentGameType = gameType;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    /**
     * Flips the player around. Args: player
     */
    public void flipPlayer(EntityPlayer player)
    {
        player.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    /**
     * Called when a player completes the destruction of a block
     */
    public boolean onPlayerDestroyBlock(int x, int y, int z, int side)
    {
        if (this.currentGameType.isAdventure() && !this.mc.thePlayer.isCurrentToolAdventureModeExempt(x, y, z))
        {
            return false;
        } else if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
        {
            return false;
        } else
        {
            WorldClient world = this.mc.theWorld;
            Block block = world.getBlock(x, y, z);

            if (block.getMaterial() == Material.air)
            {
                return false;
            } else
            {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, side));
                world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
                int meta = world.getBlockMetadata(x, y, z);
                boolean setToAir = world.setBlockToAir(x, y, z);

                if (setToAir)
                {
                    block.onBlockDestroyedByPlayer(world, x, y, z, meta);
                }

                this.currentBlockY = -1;

                if (!this.currentGameType.isCreative())
                {
                    ItemStack stack = this.mc.thePlayer.getCurrentEquippedItem();

                    if (stack != null)
                    {
                        stack.func_150999_a(world, block, x, y, z, this.mc.thePlayer);

                        if (stack.stackSize == 0)
                        {
                            this.mc.thePlayer.destroyCurrentEquippedItem();
                        }
                    }
                }

                return setToAir;
            }
        }
    }

    /**
     * Called by Minecraft class when the player is hitting a block with an item. Args: x, y, z, side
     */
    public void clickBlock(int x, int y, int z, int side)
    {
        if (EventBus.dispatch(new EventAttackBlock(x, y, z, side)))
        {
            return;
        }
        if (!this.currentGameType.isAdventure() || this.mc.thePlayer.isCurrentToolAdventureModeExempt(x, y, z))
        {
            if (this.currentGameType.isCreative())
            {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, side));
                clickBlockCreative(this.mc, this, x, y, z, side);
                this.blockHitDelay = 5;
            } else if (!this.isHittingBlock || !this.sameToolAndBlock(x, y, z))
            {
                if (this.isHittingBlock)
                {
                    this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, side));
                }

                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, side));
                Block block = this.mc.theWorld.getBlock(x, y, z);
                boolean notAir = block.getMaterial() != Material.air;

                if (notAir && this.curBlockDamageMP == 0.0F)
                {
                    block.onBlockClicked(this.mc.theWorld, x, y, z, this.mc.thePlayer);
                }

                if (notAir && block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, x, y, z) >= 1.0F)
                {
                    this.onPlayerDestroyBlock(x, y, z, side);
                } else
                {
                    this.isHittingBlock = true;
                    this.currentBlockX = x;
                    this.currentBlockY = y;
                    this.currentblockZ = z;
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
    public void onPlayerDamageBlock(int x, int y, int z, int side)
    {
        this.syncCurrentPlayItem();

        if (this.blockHitDelay > 0)
        {
            --this.blockHitDelay;
        } else if (this.currentGameType.isCreative())
        {
            this.blockHitDelay = 5;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, side));
            clickBlockCreative(this.mc, this, x, y, z, side);
        } else
        {
            if (this.sameToolAndBlock(x, y, z))
            {
                Block block = this.mc.theWorld.getBlock(x, y, z);

                if (block.getMaterial() == Material.air)
                {
                    this.isHittingBlock = false;
                    return;
                }

                this.curBlockDamageMP += block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, x, y, z);

                if (this.stepSoundTickCounter % 4.0F == 0.0F)
                {
                    this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(block.stepSound.getStepSound()), (block.stepSound.func_150497_c() + 1.0F) / 8.0F, block.stepSound.func_150494_d() * 0.5F, (float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F));
                }

                ++this.stepSoundTickCounter;

                if (this.curBlockDamageMP >= 1.0F)
                {
                    this.isHittingBlock = false;
                    //this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(2, par1, par2, par3, par4));
                    this.onPlayerDestroyBlock(x, y, z, side);
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                }

                this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.getEntityId(), this.currentBlockX, this.currentBlockY, this.currentblockZ, (int) (this.curBlockDamageMP * 10.0F) - 1);
            } else
            {
                this.clickBlock(x, y, z, side);
            }
        }
    }

    /**
     * player reach distance = 4F
     */
    public float getBlockReachDistance()
    {
        if (InteractModule.INSTANCE.isToggled())
        {
            return InteractModule.INSTANCE.placeReachSetting.getValue().floatValue();
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

    public boolean sameToolAndBlock(int x, int y, int z)
    {
        ItemStack stack = Nebula.INSTANCE.getInventoryManager().getStack();
        boolean invalidStack = this.currentItemHittingBlock == null && stack == null;

        if (this.currentItemHittingBlock != null && stack != null)
        {
            invalidStack = stack.getItem() == this.currentItemHittingBlock.getItem()
                    && ItemStack.areItemStackTagsEqual(stack, this.currentItemHittingBlock)
                    && (stack.isItemStackDamageable()
                        || stack.getItemDamage() == this.currentItemHittingBlock.getItemDamage());
        }

        return x == this.currentBlockX && y == this.currentBlockY && z == this.currentblockZ && invalidStack;
    }

    /**
     * Syncs the current player item with the server
     */
    private void syncCurrentPlayItem()
    {
        int currentItem = this.mc.thePlayer.inventory.currentItem;
        if (currentItem != this.currentPlayerItem)
        {
            this.currentPlayerItem = currentItem;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
        }
    }

    /**
     * Handles a players right click. Args: player, world, x, y, z, side, hitVec
     */
    public boolean onPlayerRightClick(EntityPlayer player, World world, ItemStack stack, int x, int y, int z, int side, Vec3 hitVec)
    {
        if (EventBus.dispatch(new EventPlace(x, y, z, side, stack, hitVec)))
        {
            return false;
        }

        //ChatUtil.sendNebula("XYZ: (%s, %s, %s), Side: %s, Vec: %s", x, y, z, side, hitVec);
        syncCurrentPlayItem();
        final float facingX = (float) hitVec.xCoord - (float) x;
        final float facingY = (float) hitVec.yCoord - (float) y;
        final float facingZ = (float) hitVec.zCoord - (float) z;

        boolean sneaking = (!player.isSneaking() || stack == null)
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
            final int meta = stack.getItemDamage();
            final int size = stack.stackSize;
            boolean result = stack.tryPlaceItemIntoWorld(player, world, x, y, z, side, facingX, facingY, facingZ);
            stack.setItemDamage(meta);
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
    public boolean sendUseItem(EntityPlayer player, World world, ItemStack stack)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, stack, 0.0F, 0.0F, 0.0F));
        int size = stack.stackSize;
        ItemStack usedStack = stack.useItemRightClick(world, player);

        if (usedStack == stack && (usedStack == null || usedStack.stackSize == size))
        {
            return false;
        } else
        {
            final int slot = Nebula.INSTANCE.getInventoryManager().getSlot();
            player.inventory.mainInventory[slot] = usedStack;

            if (usedStack.stackSize == 0)
            {
                player.inventory.mainInventory[slot] = null;
            }

            return true;
        }
    }

    public EntityClientPlayerMP createMPPlayer(World world, StatFileWriter statFileWriter)
    {
        return new EntityClientPlayerMP(this.mc, world, this.mc.getSession(), this.netClientHandler, statFileWriter);
    }

    /**
     * Attacks an entity
     */
    public void attackEntity(EntityPlayer player, Entity entity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        player.attackTargetEntityWithCurrentItem(entity);
    }

    /**
     * Send packet to server - player is interacting with another entity (left click)
     */
    public boolean interactWithEntitySendPacket(EntityPlayer player, Entity entity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.INTERACT));
        return player.interactWith(entity);
    }

    public ItemStack windowClick(int windowId, int slot, int mouseButton, int action, EntityPlayer player)
    {
        //ChatUtil.sendNebula("Slot: %s, MB: %s, Action: %s", slot, mouseButton, action);
        short transactionId = player.openContainer.getNextTransactionID(player.inventory);
        ItemStack stack = player.openContainer.slotClick(slot, mouseButton, action, player);
        this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slot, mouseButton, action, stack, transactionId));
        return stack;
    }

    /**
     * GuiEnchantment uses this during multiplayer to tell PlayerControllerMP to send a packet indicating the
     * enchantment action the player has taken.
     */
    public void sendEnchantPacket(int id, int button)
    {
        this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(id, button));
    }

    /**
     * Used in PlayerControllerMP to update the server with an ItemStack in a slot.
     */
    public void sendSlotPacket(ItemStack stack, int action)
    {
        if (this.currentGameType.isCreative())
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(action, stack));
        }
    }

    /**
     * Sends a Packet107 to the server to drop the item on the ground
     */
    public void sendPacketDropItem(ItemStack stack)
    {
        if (this.currentGameType.isCreative() && stack != null)
        {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, stack));
        }
    }

    public void onStoppedUsingItem(EntityPlayer player)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
        player.stopUsingItem();
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
