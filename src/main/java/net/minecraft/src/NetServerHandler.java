package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import wtf.nebula.Nebula;
import wtf.nebula.event.PacketEvent;

public class NetServerHandler extends NetHandler
{
    /** The underlying network manager for this server handler. */
    public final INetworkManager netManager;

    /** Reference to the MinecraftServer object. */
    private final MinecraftServer mcServer;

    /** This is set to true whenever a player disconnects from the server. */
    public boolean connectionClosed;

    /** Reference to the EntityPlayerMP object. */
    public EntityPlayerMP playerEntity;

    /** incremented each tick */
    private int currentTicks;

    /**
     * player is kicked if they float for over 80 ticks without flying enabled
     */
    private int ticksForFloatKick;
    private boolean field_72584_h;
    private int keepAliveRandomID;
    private long keepAliveTimeSent;
    private static Random randomGenerator = new Random();
    private long ticksOfLastKeepAlive;
    private int chatSpamThresholdCount;
    private int creativeItemCreationSpamThresholdTally;

    /** The last known x position for this connection. */
    private double lastPosX;

    /** The last known y position for this connection. */
    private double lastPosY;

    /** The last known z position for this connection. */
    private double lastPosZ;

    /** is true when the player has moved since his last movement packet */
    private boolean hasMoved = true;
    private IntHashMap field_72586_s = new IntHashMap();

    public NetServerHandler(MinecraftServer par1MinecraftServer, INetworkManager par2INetworkManager, EntityPlayerMP par3EntityPlayerMP)
    {
        this.mcServer = par1MinecraftServer;
        this.netManager = par2INetworkManager;
        par2INetworkManager.setNetHandler(this);
        this.playerEntity = par3EntityPlayerMP;
        par3EntityPlayerMP.playerNetServerHandler = this;
    }

    /**
     * run once each game tick
     */
    public void networkTick()
    {
        this.field_72584_h = false;
        ++this.currentTicks;
        this.mcServer.theProfiler.startSection("packetflow");
        this.netManager.processReadPackets();
        this.mcServer.theProfiler.endStartSection("keepAlive");

        if ((long)this.currentTicks - this.ticksOfLastKeepAlive > 20L)
        {
            this.ticksOfLastKeepAlive = (long)this.currentTicks;
            this.keepAliveTimeSent = System.nanoTime() / 1000000L;
            this.keepAliveRandomID = randomGenerator.nextInt();
            this.sendPacketToPlayer(new Packet0KeepAlive(this.keepAliveRandomID));
        }

        if (this.chatSpamThresholdCount > 0)
        {
            --this.chatSpamThresholdCount;
        }

        if (this.creativeItemCreationSpamThresholdTally > 0)
        {
            --this.creativeItemCreationSpamThresholdTally;
        }

        this.mcServer.theProfiler.endStartSection("playerTick");
        this.mcServer.theProfiler.endSection();
    }

    public void kickPlayerFromServer(String par1Str)
    {
        if (!this.connectionClosed)
        {
            this.playerEntity.mountEntityAndWakeUp();
            this.sendPacketToPlayer(new Packet255KickDisconnect(par1Str));
            this.netManager.serverShutdown();
            this.mcServer.getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("multiplayer.player.left", new Object[] {this.playerEntity.getTranslatedEntityName()}).setColor(EnumChatFormatting.YELLOW));
            this.mcServer.getConfigurationManager().playerLoggedOut(this.playerEntity);
            this.connectionClosed = true;
        }
    }

    public void func_110774_a(Packet27PlayerInput par1Packet27PlayerInput)
    {
        this.playerEntity.setEntityActionState(par1Packet27PlayerInput.func_111010_d(), par1Packet27PlayerInput.func_111012_f(), par1Packet27PlayerInput.func_111013_g(), par1Packet27PlayerInput.func_111011_h());
    }

    public void handleFlying(Packet10Flying par1Packet10Flying)
    {
        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
        this.field_72584_h = true;

        if (!this.playerEntity.playerConqueredTheEnd)
        {
            double var3;

            if (!this.hasMoved)
            {
                var3 = par1Packet10Flying.yPosition - this.lastPosY;

                if (par1Packet10Flying.xPosition == this.lastPosX && var3 * var3 < 0.01D && par1Packet10Flying.zPosition == this.lastPosZ)
                {
                    this.hasMoved = true;
                }
            }

            if (this.hasMoved)
            {
                double var5;
                double var7;
                double var9;

                if (this.playerEntity.ridingEntity != null)
                {
                    float var34 = this.playerEntity.rotationYaw;
                    float var4 = this.playerEntity.rotationPitch;
                    this.playerEntity.ridingEntity.updateRiderPosition();
                    var5 = this.playerEntity.posX;
                    var7 = this.playerEntity.posY;
                    var9 = this.playerEntity.posZ;

                    if (par1Packet10Flying.rotating)
                    {
                        var34 = par1Packet10Flying.yaw;
                        var4 = par1Packet10Flying.pitch;
                    }

                    this.playerEntity.onGround = par1Packet10Flying.onGround;
                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.ySize = 0.0F;
                    this.playerEntity.setPositionAndRotation(var5, var7, var9, var34, var4);

                    if (this.playerEntity.ridingEntity != null)
                    {
                        this.playerEntity.ridingEntity.updateRiderPosition();
                    }

                    this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);

                    if (this.hasMoved)
                    {
                        this.lastPosX = this.playerEntity.posX;
                        this.lastPosY = this.playerEntity.posY;
                        this.lastPosZ = this.playerEntity.posZ;
                    }

                    var2.updateEntity(this.playerEntity);
                    return;
                }

                if (this.playerEntity.isPlayerSleeping())
                {
                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    var2.updateEntity(this.playerEntity);
                    return;
                }

                var3 = this.playerEntity.posY;
                this.lastPosX = this.playerEntity.posX;
                this.lastPosY = this.playerEntity.posY;
                this.lastPosZ = this.playerEntity.posZ;
                var5 = this.playerEntity.posX;
                var7 = this.playerEntity.posY;
                var9 = this.playerEntity.posZ;
                float var11 = this.playerEntity.rotationYaw;
                float var12 = this.playerEntity.rotationPitch;

                if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D)
                {
                    par1Packet10Flying.moving = false;
                }

                double var13;

                if (par1Packet10Flying.moving)
                {
                    var5 = par1Packet10Flying.xPosition;
                    var7 = par1Packet10Flying.yPosition;
                    var9 = par1Packet10Flying.zPosition;
                    var13 = par1Packet10Flying.stance - par1Packet10Flying.yPosition;

                    if (!this.playerEntity.isPlayerSleeping() && (var13 > 1.65D || var13 < 0.1D))
                    {
                        this.kickPlayerFromServer("Illegal stance");
                        this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " had an illegal stance: " + var13);
                        return;
                    }

                    if (Math.abs(par1Packet10Flying.xPosition) > 3.2E7D || Math.abs(par1Packet10Flying.zPosition) > 3.2E7D)
                    {
                        this.kickPlayerFromServer("Illegal position");
                        return;
                    }
                }

                if (par1Packet10Flying.rotating)
                {
                    var11 = par1Packet10Flying.yaw;
                    var12 = par1Packet10Flying.pitch;
                }

                this.playerEntity.onUpdateEntity();
                this.playerEntity.ySize = 0.0F;
                this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);

                if (!this.hasMoved)
                {
                    return;
                }

                var13 = var5 - this.playerEntity.posX;
                double var15 = var7 - this.playerEntity.posY;
                double var17 = var9 - this.playerEntity.posZ;
                double var19 = Math.min(Math.abs(var13), Math.abs(this.playerEntity.motionX));
                double var21 = Math.min(Math.abs(var15), Math.abs(this.playerEntity.motionY));
                double var23 = Math.min(Math.abs(var17), Math.abs(this.playerEntity.motionZ));
                double var25 = var19 * var19 + var21 * var21 + var23 * var23;

                if (var25 > 100.0D && (!this.mcServer.isSinglePlayer() || !this.mcServer.getServerOwner().equals(this.playerEntity.getCommandSenderName())))
                {
                    this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " moved too quickly! " + var13 + "," + var15 + "," + var17 + " (" + var19 + ", " + var21 + ", " + var23 + ")");
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    return;
                }

                float var27 = 0.0625F;
                boolean var28 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();

                if (this.playerEntity.onGround && !par1Packet10Flying.onGround && var15 > 0.0D)
                {
                    this.playerEntity.addExhaustion(0.2F);
                }

                this.playerEntity.moveEntity(var13, var15, var17);
                this.playerEntity.onGround = par1Packet10Flying.onGround;
                this.playerEntity.addMovementStat(var13, var15, var17);
                double var29 = var15;
                var13 = var5 - this.playerEntity.posX;
                var15 = var7 - this.playerEntity.posY;

                if (var15 > -0.5D || var15 < 0.5D)
                {
                    var15 = 0.0D;
                }

                var17 = var9 - this.playerEntity.posZ;
                var25 = var13 * var13 + var15 * var15 + var17 * var17;
                boolean var31 = false;

                if (var25 > 0.0625D && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative())
                {
                    var31 = true;
                    this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " moved wrongly!");
                }

                this.playerEntity.setPositionAndRotation(var5, var7, var9, var11, var12);
                boolean var32 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var27, (double)var27, (double)var27)).isEmpty();

                if (var28 && (var31 || !var32) && !this.playerEntity.isPlayerSleeping())
                {
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);
                    return;
                }

                AxisAlignedBB var33 = this.playerEntity.boundingBox.copy().expand((double)var27, (double)var27, (double)var27).addCoord(0.0D, -0.55D, 0.0D);

                if (!this.mcServer.isFlightAllowed() && !this.playerEntity.theItemInWorldManager.isCreative() && !var2.checkBlockCollision(var33))
                {
                    if (var29 >= -0.03125D)
                    {
                        ++this.ticksForFloatKick;

                        if (this.ticksForFloatKick > 80)
                        {
                            this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " was kicked for floating too long!");
                            this.kickPlayerFromServer("Flying is not enabled on this server");
                            return;
                        }
                    }
                }
                else
                {
                    this.ticksForFloatKick = 0;
                }

                this.playerEntity.onGround = par1Packet10Flying.onGround;
                this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                this.playerEntity.updateFlyingState(this.playerEntity.posY - var3, par1Packet10Flying.onGround);
            }
            else if (this.currentTicks % 20 == 0)
            {
                this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
            }
        }
    }

    /**
     * Moves the player to the specified destination and rotation
     */
    public void setPlayerLocation(double par1, double par3, double par5, float par7, float par8)
    {
        this.hasMoved = false;
        this.lastPosX = par1;
        this.lastPosY = par3;
        this.lastPosZ = par5;
        this.playerEntity.setPositionAndRotation(par1, par3, par5, par7, par8);
        this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet13PlayerLookMove(par1, par3 + 1.6200000047683716D, par3, par5, par7, par8, false));
    }

    public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig)
    {
        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
        this.playerEntity.func_143004_u();

        if (par1Packet14BlockDig.status == 4)
        {
            this.playerEntity.dropOneItem(false);
        }
        else if (par1Packet14BlockDig.status == 3)
        {
            this.playerEntity.dropOneItem(true);
        }
        else if (par1Packet14BlockDig.status == 5)
        {
            this.playerEntity.stopUsingItem();
        }
        else
        {
            boolean var3 = false;

            if (par1Packet14BlockDig.status == 0)
            {
                var3 = true;
            }

            if (par1Packet14BlockDig.status == 1)
            {
                var3 = true;
            }

            if (par1Packet14BlockDig.status == 2)
            {
                var3 = true;
            }

            int var4 = par1Packet14BlockDig.xPosition;
            int var5 = par1Packet14BlockDig.yPosition;
            int var6 = par1Packet14BlockDig.zPosition;

            if (var3)
            {
                double var7 = this.playerEntity.posX - ((double)var4 + 0.5D);
                double var9 = this.playerEntity.posY - ((double)var5 + 0.5D) + 1.5D;
                double var11 = this.playerEntity.posZ - ((double)var6 + 0.5D);
                double var13 = var7 * var7 + var9 * var9 + var11 * var11;

                if (var13 > 36.0D)
                {
                    return;
                }

                if (var5 >= this.mcServer.getBuildLimit())
                {
                    return;
                }
            }

            if (par1Packet14BlockDig.status == 0)
            {
                if (!this.mcServer.isBlockProtected(var2, var4, var5, var6, this.playerEntity))
                {
                    this.playerEntity.theItemInWorldManager.onBlockClicked(var4, var5, var6, par1Packet14BlockDig.face);
                }
                else
                {
                    this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var4, var5, var6, var2));
                }
            }
            else if (par1Packet14BlockDig.status == 2)
            {
                this.playerEntity.theItemInWorldManager.uncheckedTryHarvestBlock(var4, var5, var6);

                if (var2.getBlockId(var4, var5, var6) != 0)
                {
                    this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var4, var5, var6, var2));
                }
            }
            else if (par1Packet14BlockDig.status == 1)
            {
                this.playerEntity.theItemInWorldManager.cancelDestroyingBlock(var4, var5, var6);

                if (var2.getBlockId(var4, var5, var6) != 0)
                {
                    this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var4, var5, var6, var2));
                }
            }
        }
    }

    public void handlePlace(Packet15Place par1Packet15Place)
    {
        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
        ItemStack var3 = this.playerEntity.inventory.getCurrentItem();
        boolean var4 = false;
        int var5 = par1Packet15Place.getXPosition();
        int var6 = par1Packet15Place.getYPosition();
        int var7 = par1Packet15Place.getZPosition();
        int var8 = par1Packet15Place.getDirection();
        this.playerEntity.func_143004_u();

        if (par1Packet15Place.getDirection() == 255)
        {
            if (var3 == null)
            {
                return;
            }

            this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, var2, var3);
        }
        else if (par1Packet15Place.getYPosition() >= this.mcServer.getBuildLimit() - 1 && (par1Packet15Place.getDirection() == 1 || par1Packet15Place.getYPosition() >= this.mcServer.getBuildLimit()))
        {
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromTranslationWithSubstitutions("build.tooHigh", new Object[] {Integer.valueOf(this.mcServer.getBuildLimit())}).setColor(EnumChatFormatting.RED)));
            var4 = true;
        }
        else
        {
            if (this.hasMoved && this.playerEntity.getDistanceSq((double)var5 + 0.5D, (double)var6 + 0.5D, (double)var7 + 0.5D) < 64.0D && !this.mcServer.isBlockProtected(var2, var5, var6, var7, this.playerEntity))
            {
                this.playerEntity.theItemInWorldManager.activateBlockOrUseItem(this.playerEntity, var2, var3, var5, var6, var7, var8, par1Packet15Place.getXOffset(), par1Packet15Place.getYOffset(), par1Packet15Place.getZOffset());
            }

            var4 = true;
        }

        if (var4)
        {
            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));

            if (var8 == 0)
            {
                --var6;
            }

            if (var8 == 1)
            {
                ++var6;
            }

            if (var8 == 2)
            {
                --var7;
            }

            if (var8 == 3)
            {
                ++var7;
            }

            if (var8 == 4)
            {
                --var5;
            }

            if (var8 == 5)
            {
                ++var5;
            }

            this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet53BlockChange(var5, var6, var7, var2));
        }

        var3 = this.playerEntity.inventory.getCurrentItem();

        if (var3 != null && var3.stackSize == 0)
        {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
            var3 = null;
        }

        if (var3 == null || var3.getMaxItemUseDuration() == 0)
        {
            this.playerEntity.playerInventoryBeingManipulated = true;
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
            Slot var9 = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
            this.playerEntity.openContainer.detectAndSendChanges();
            this.playerEntity.playerInventoryBeingManipulated = false;

            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), par1Packet15Place.getItemStack()))
            {
                this.sendPacketToPlayer(new Packet103SetSlot(this.playerEntity.openContainer.windowId, var9.slotNumber, this.playerEntity.inventory.getCurrentItem()));
            }
        }
    }

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        this.mcServer.getLogAgent().logInfo(this.playerEntity.getCommandSenderName() + " lost connection: " + par1Str);
        this.mcServer.getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("multiplayer.player.left", new Object[] {this.playerEntity.getTranslatedEntityName()}).setColor(EnumChatFormatting.YELLOW));
        this.mcServer.getConfigurationManager().playerLoggedOut(this.playerEntity);
        this.connectionClosed = true;

        if (this.mcServer.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.mcServer.getServerOwner()))
        {
            this.mcServer.getLogAgent().logInfo("Stopping singleplayer server as player logged out");
            this.mcServer.initiateShutdown();
        }
    }

    /**
     * Default handler called for packets that don't have their own handlers in NetClientHandler; currentlly does
     * nothing.
     */
    public void unexpectedPacket(Packet par1Packet)
    {
        this.mcServer.getLogAgent().logWarning(this.getClass() + " wasn\'t prepared to deal with a " + par1Packet.getClass());
        this.kickPlayerFromServer("Protocol error, unexpected packet");
    }

    /**
     * addToSendQueue. if it is a chat packet, check before sending it
     */
    public void sendPacketToPlayer(Packet par1Packet)
    {

        if (Minecraft.getMinecraft().isSingleplayer()) {
            if (Nebula.BUS.post(new PacketEvent.Receive(par1Packet))) {
                return;
            }
        }

        if (par1Packet instanceof Packet3Chat)
        {
            Packet3Chat var2 = (Packet3Chat)par1Packet;
            int var3 = this.playerEntity.getChatVisibility();

            if (var3 == 2)
            {
                return;
            }

            if (var3 == 1 && !var2.getIsServer())
            {
                return;
            }
        }

        try
        {
            this.netManager.addToSendQueue(par1Packet);
        }
        catch (Throwable var5)
        {
            CrashReport var6 = CrashReport.makeCrashReport(var5, "Sending packet");
            CrashReportCategory var4 = var6.makeCategory("Packet being sent");
            var4.addCrashSectionCallable("Packet ID", new CallablePacketID(this, par1Packet));
            var4.addCrashSectionCallable("Packet class", new CallablePacketClass(this, par1Packet));
            throw new ReportedException(var6);
        }
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.getHotbarSize())
        {
            this.playerEntity.inventory.currentItem = par1Packet16BlockItemSwitch.id;
            this.playerEntity.func_143004_u();
        }
        else
        {
            this.mcServer.getLogAgent().logWarning(this.playerEntity.getCommandSenderName() + " tried to set an invalid carried item");
        }
    }

    public void handleChat(Packet3Chat par1Packet3Chat)
    {
        if (this.playerEntity.getChatVisibility() == 2)
        {
            this.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromTranslationKey("chat.cannotSend").setColor(EnumChatFormatting.RED)));
        }
        else
        {
            this.playerEntity.func_143004_u();
            String var2 = par1Packet3Chat.message;

            if (var2.length() > 100)
            {
                this.kickPlayerFromServer("Chat message too long");
            }
            else
            {
                var2 = org.apache.commons.lang3.StringUtils.normalizeSpace(var2);

                for (int var3 = 0; var3 < var2.length(); ++var3)
                {
                    if (!ChatAllowedCharacters.isAllowedCharacter(var2.charAt(var3)))
                    {
                        this.kickPlayerFromServer("Illegal characters in chat");
                        return;
                    }
                }

                if (var2.startsWith("/"))
                {
                    this.handleSlashCommand(var2);
                }
                else
                {
                    if (this.playerEntity.getChatVisibility() == 1)
                    {
                        this.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromTranslationKey("chat.cannotSend").setColor(EnumChatFormatting.RED)));
                        return;
                    }

                    ChatMessageComponent var4 = ChatMessageComponent.createFromTranslationWithSubstitutions("chat.type.text", new Object[] {this.playerEntity.getTranslatedEntityName(), var2});
                    this.mcServer.getConfigurationManager().func_110459_a(var4, false);
                }

                this.chatSpamThresholdCount += 20;

                if (this.chatSpamThresholdCount > 200 && !this.mcServer.getConfigurationManager().isPlayerOpped(this.playerEntity.getCommandSenderName()))
                {
                    this.kickPlayerFromServer("disconnect.spam");
                }
            }
        }
    }

    /**
     * Processes a / command
     */
    private void handleSlashCommand(String par1Str)
    {
        this.mcServer.getCommandManager().executeCommand(this.playerEntity, par1Str);
    }

    public void handleAnimation(Packet18Animation par1Packet18Animation)
    {
        this.playerEntity.func_143004_u();

        if (par1Packet18Animation.animate == 1)
        {
            this.playerEntity.swingItem();
        }
    }

    /**
     * runs registerPacket on the given Packet19EntityAction
     */
    public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction)
    {
        this.playerEntity.func_143004_u();

        if (par1Packet19EntityAction.action == 1)
        {
            this.playerEntity.setSneaking(true);
        }
        else if (par1Packet19EntityAction.action == 2)
        {
            this.playerEntity.setSneaking(false);
        }
        else if (par1Packet19EntityAction.action == 4)
        {
            this.playerEntity.setSprinting(true);
        }
        else if (par1Packet19EntityAction.action == 5)
        {
            this.playerEntity.setSprinting(false);
        }
        else if (par1Packet19EntityAction.action == 3)
        {
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
        }
        else if (par1Packet19EntityAction.action == 6)
        {
            if (this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse)
            {
                ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(par1Packet19EntityAction.auxData);
            }
        }
        else if (par1Packet19EntityAction.action == 7 && this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse)
        {
            ((EntityHorse)this.playerEntity.ridingEntity).openGUI(this.playerEntity);
        }
    }

    public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        this.netManager.networkShutdown("disconnect.quitting", new Object[0]);
    }

    /**
     * returns 0 for memoryMapped connections
     */
    public int packetSize()
    {
        return this.netManager.packetSize();
    }

    public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity)
    {
        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);
        Entity var3 = var2.getEntityByID(par1Packet7UseEntity.targetEntity);
        this.playerEntity.func_143004_u();

        if (var3 != null)
        {
            boolean var4 = this.playerEntity.canEntityBeSeen(var3);
            double var5 = 36.0D;

            if (!var4)
            {
                var5 = 9.0D;
            }

            if (this.playerEntity.getDistanceSqToEntity(var3) < var5)
            {
                if (par1Packet7UseEntity.isLeftClick == 0)
                {
                    this.playerEntity.interactWith(var3);
                }
                else if (par1Packet7UseEntity.isLeftClick == 1)
                {
                    if (var3 instanceof EntityItem || var3 instanceof EntityXPOrb || var3 instanceof EntityArrow || var3 == this.playerEntity)
                    {
                        this.kickPlayerFromServer("Attempting to attack an invalid entity");
                        this.mcServer.logWarning("Player " + this.playerEntity.getCommandSenderName() + " tried to attack an invalid entity");
                        return;
                    }

                    this.playerEntity.attackTargetEntityWithCurrentItem(var3);
                }
            }
        }
    }

    public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand)
    {
        this.playerEntity.func_143004_u();

        if (par1Packet205ClientCommand.forceRespawn == 1)
        {
            if (this.playerEntity.playerConqueredTheEnd)
            {
                this.playerEntity = this.mcServer.getConfigurationManager().respawnPlayer(this.playerEntity, 0, true);
            }
            else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled())
            {
                if (this.mcServer.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.mcServer.getServerOwner()))
                {
                    this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                    this.mcServer.deleteWorldAndStopServer();
                }
                else
                {
                    BanEntry var2 = new BanEntry(this.playerEntity.getCommandSenderName());
                    var2.setBanReason("Death in Hardcore");
                    this.mcServer.getConfigurationManager().getBannedPlayers().put(var2);
                    this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                }
            }
            else
            {
                if (this.playerEntity.getHealth() > 0.0F)
                {
                    return;
                }

                this.playerEntity = this.mcServer.getConfigurationManager().respawnPlayer(this.playerEntity, 0, false);
            }
        }
    }

    /**
     * If this returns false, all packets will be queued for the main thread to handle, even if they would otherwise be
     * processed asynchronously. Used to avoid processing packets on the client before the world has been downloaded
     * (which happens on the main thread)
     */
    public boolean canProcessPacketsAsync()
    {
        return true;
    }

    /**
     * respawns the player
     */
    public void handleRespawn(Packet9Respawn par1Packet9Respawn) {}

    public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow)
    {
        this.playerEntity.closeContainer();
    }

    public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick)
    {
        this.playerEntity.func_143004_u();

        if (this.playerEntity.openContainer.windowId == par1Packet102WindowClick.window_Id && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity))
        {
            ItemStack var2 = this.playerEntity.openContainer.slotClick(par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick, par1Packet102WindowClick.holdingShift, this.playerEntity);

            if (ItemStack.areItemStacksEqual(par1Packet102WindowClick.itemStack, var2))
            {
                this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, true));
                this.playerEntity.playerInventoryBeingManipulated = true;
                this.playerEntity.openContainer.detectAndSendChanges();
                this.playerEntity.updateHeldItem();
                this.playerEntity.playerInventoryBeingManipulated = false;
            }
            else
            {
                this.field_72586_s.addKey(this.playerEntity.openContainer.windowId, Short.valueOf(par1Packet102WindowClick.action));
                this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, false));
                this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, false);
                ArrayList var3 = new ArrayList();

                for (int var4 = 0; var4 < this.playerEntity.openContainer.inventorySlots.size(); ++var4)
                {
                    var3.add(((Slot)this.playerEntity.openContainer.inventorySlots.get(var4)).getStack());
                }

                this.playerEntity.sendContainerAndContentsToPlayer(this.playerEntity.openContainer, var3);
            }
        }
    }

    public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem)
    {
        this.playerEntity.func_143004_u();

        if (this.playerEntity.openContainer.windowId == par1Packet108EnchantItem.windowId && this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity))
        {
            this.playerEntity.openContainer.enchantItem(this.playerEntity, par1Packet108EnchantItem.enchantment);
            this.playerEntity.openContainer.detectAndSendChanges();
        }
    }

    /**
     * Handle a creative slot packet.
     */
    public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot)
    {
        if (this.playerEntity.theItemInWorldManager.isCreative())
        {
            boolean var2 = par1Packet107CreativeSetSlot.slot < 0;
            ItemStack var3 = par1Packet107CreativeSetSlot.itemStack;
            boolean var4 = par1Packet107CreativeSetSlot.slot >= 1 && par1Packet107CreativeSetSlot.slot < 36 + InventoryPlayer.getHotbarSize();
            boolean var5 = var3 == null || var3.itemID < Item.itemsList.length && var3.itemID >= 0 && Item.itemsList[var3.itemID] != null;
            boolean var6 = var3 == null || var3.getItemDamage() >= 0 && var3.getItemDamage() >= 0 && var3.stackSize <= 64 && var3.stackSize > 0;

            if (var4 && var5 && var6)
            {
                if (var3 == null)
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, (ItemStack)null);
                }
                else
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(par1Packet107CreativeSetSlot.slot, var3);
                }

                this.playerEntity.inventoryContainer.setPlayerIsPresent(this.playerEntity, true);
            }
            else if (var2 && var5 && var6 && this.creativeItemCreationSpamThresholdTally < 200)
            {
                this.creativeItemCreationSpamThresholdTally += 20;
                EntityItem var7 = this.playerEntity.dropPlayerItem(var3);

                if (var7 != null)
                {
                    var7.setAgeToCreativeDespawnTime();
                }
            }
        }
    }

    public void handleTransaction(Packet106Transaction par1Packet106Transaction)
    {
        Short var2 = (Short)this.field_72586_s.lookup(this.playerEntity.openContainer.windowId);

        if (var2 != null && par1Packet106Transaction.shortWindowId == var2.shortValue() && this.playerEntity.openContainer.windowId == par1Packet106Transaction.windowId && !this.playerEntity.openContainer.isPlayerNotUsingContainer(this.playerEntity))
        {
            this.playerEntity.openContainer.setPlayerIsPresent(this.playerEntity, true);
        }
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
    {
        this.playerEntity.func_143004_u();
        WorldServer var2 = this.mcServer.worldServerForDimension(this.playerEntity.dimension);

        if (var2.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition))
        {
            TileEntity var3 = var2.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (!var4.isEditable() || var4.func_142009_b() != this.playerEntity)
                {
                    this.mcServer.logWarning("Player " + this.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
                    return;
                }
            }

            int var6;
            int var8;

            for (var8 = 0; var8 < 4; ++var8)
            {
                boolean var5 = true;

                if (par1Packet130UpdateSign.signLines[var8].length() > 15)
                {
                    var5 = false;
                }
                else
                {
                    for (var6 = 0; var6 < par1Packet130UpdateSign.signLines[var8].length(); ++var6)
                    {
                        if (ChatAllowedCharacters.allowedCharacters.indexOf(par1Packet130UpdateSign.signLines[var8].charAt(var6)) < 0)
                        {
                            var5 = false;
                        }
                    }
                }

                if (!var5)
                {
                    par1Packet130UpdateSign.signLines[var8] = "!?";
                }
            }

            if (var3 instanceof TileEntitySign)
            {
                var8 = par1Packet130UpdateSign.xPosition;
                int var9 = par1Packet130UpdateSign.yPosition;
                var6 = par1Packet130UpdateSign.zPosition;
                TileEntitySign var7 = (TileEntitySign)var3;
                System.arraycopy(par1Packet130UpdateSign.signLines, 0, var7.signText, 0, 4);
                var7.onInventoryChanged();
                var2.markBlockForUpdate(var8, var9, var6);
            }
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive)
    {
        if (par1Packet0KeepAlive.randomId == this.keepAliveRandomID)
        {
            int var2 = (int)(System.nanoTime() / 1000000L - this.keepAliveTimeSent);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + var2) / 4;
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return true;
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities)
    {
        this.playerEntity.capabilities.isFlying = par1Packet202PlayerAbilities.getFlying() && this.playerEntity.capabilities.allowFlying;
    }

    public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete)
    {
        StringBuilder var2 = new StringBuilder();
        String var4;

        for (Iterator var3 = this.mcServer.getPossibleCompletions(this.playerEntity, par1Packet203AutoComplete.getText()).iterator(); var3.hasNext(); var2.append(var4))
        {
            var4 = (String)var3.next();

            if (var2.length() > 0)
            {
                var2.append("\u0000");
            }
        }

        this.playerEntity.playerNetServerHandler.sendPacketToPlayer(new Packet203AutoComplete(var2.toString()));
    }

    public void handleClientInfo(Packet204ClientInfo par1Packet204ClientInfo)
    {
        this.playerEntity.updateClientInfo(par1Packet204ClientInfo);
    }

    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload)
    {
        DataInputStream var2;
        ItemStack var3;
        ItemStack var4;

        if ("MC|BEdit".equals(par1Packet250CustomPayload.channel))
        {
            try
            {
                var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                var3 = Packet.readItemStack(var2);

                if (!ItemWritableBook.validBookTagPages(var3.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                var4 = this.playerEntity.inventory.getCurrentItem();

                if (var3 != null && var3.itemID == Item.writableBook.itemID && var3.itemID == var4.itemID)
                {
                    var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
                }
            }
            catch (Exception var12)
            {
                var12.printStackTrace();
            }
        }
        else if ("MC|BSign".equals(par1Packet250CustomPayload.channel))
        {
            try
            {
                var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                var3 = Packet.readItemStack(var2);

                if (!ItemEditableBook.validBookTagContents(var3.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                var4 = this.playerEntity.inventory.getCurrentItem();

                if (var3 != null && var3.itemID == Item.writtenBook.itemID && var4.itemID == Item.writableBook.itemID)
                {
                    var4.setTagInfo("author", new NBTTagString("author", this.playerEntity.getCommandSenderName()));
                    var4.setTagInfo("title", new NBTTagString("title", var3.getTagCompound().getString("title")));
                    var4.setTagInfo("pages", var3.getTagCompound().getTagList("pages"));
                    var4.itemID = Item.writtenBook.itemID;
                }
            }
            catch (Exception var11)
            {
                var11.printStackTrace();
            }
        }
        else
        {
            int var13;

            if ("MC|TrSel".equals(par1Packet250CustomPayload.channel))
            {
                try
                {
                    var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                    var13 = var2.readInt();
                    Container var15 = this.playerEntity.openContainer;

                    if (var15 instanceof ContainerMerchant)
                    {
                        ((ContainerMerchant)var15).setCurrentRecipeIndex(var13);
                    }
                }
                catch (Exception var10)
                {
                    var10.printStackTrace();
                }
            }
            else
            {
                int var17;

                if ("MC|AdvCdm".equals(par1Packet250CustomPayload.channel))
                {
                    if (!this.mcServer.isCommandBlockEnabled())
                    {
                        this.playerEntity.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.notEnabled"));
                    }
                    else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode)
                    {
                        try
                        {
                            var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                            var13 = var2.readInt();
                            var17 = var2.readInt();
                            int var5 = var2.readInt();
                            String var6 = Packet.readString(var2, 256);
                            TileEntity var7 = this.playerEntity.worldObj.getBlockTileEntity(var13, var17, var5);

                            if (var7 != null && var7 instanceof TileEntityCommandBlock)
                            {
                                ((TileEntityCommandBlock)var7).setCommand(var6);
                                this.playerEntity.worldObj.markBlockForUpdate(var13, var17, var5);
                                this.playerEntity.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("advMode.setCommand.success", new Object[] {var6}));
                            }
                        }
                        catch (Exception var9)
                        {
                            var9.printStackTrace();
                        }
                    }
                    else
                    {
                        this.playerEntity.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.notAllowed"));
                    }
                }
                else if ("MC|Beacon".equals(par1Packet250CustomPayload.channel))
                {
                    if (this.playerEntity.openContainer instanceof ContainerBeacon)
                    {
                        try
                        {
                            var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));
                            var13 = var2.readInt();
                            var17 = var2.readInt();
                            ContainerBeacon var18 = (ContainerBeacon)this.playerEntity.openContainer;
                            Slot var19 = var18.getSlot(0);

                            if (var19.getHasStack())
                            {
                                var19.decrStackSize(1);
                                TileEntityBeacon var20 = var18.getBeacon();
                                var20.setPrimaryEffect(var13);
                                var20.setSecondaryEffect(var17);
                                var20.onInventoryChanged();
                            }
                        }
                        catch (Exception var8)
                        {
                            var8.printStackTrace();
                        }
                    }
                }
                else if ("MC|ItemName".equals(par1Packet250CustomPayload.channel) && this.playerEntity.openContainer instanceof ContainerRepair)
                {
                    ContainerRepair var14 = (ContainerRepair)this.playerEntity.openContainer;

                    if (par1Packet250CustomPayload.data != null && par1Packet250CustomPayload.data.length >= 1)
                    {
                        String var16 = ChatAllowedCharacters.filerAllowedCharacters(new String(par1Packet250CustomPayload.data));

                        if (var16.length() <= 30)
                        {
                            var14.updateItemName(var16);
                        }
                    }
                    else
                    {
                        var14.updateItemName("");
                    }
                }
            }
        }
    }

    public boolean isConnectionClosed()
    {
        return this.connectionClosed;
    }
}
