package net.minecraft.command;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public interface ICommandSender
{
    String getCommandSenderName();

    IChatComponent func_145748_c_();

    void addChatMessage(IChatComponent var1);

    boolean canCommandSenderUseCommand(int var1, String var2);

    ChunkCoordinates getPlayerCoordinates();

    World getEntityWorld();
}
