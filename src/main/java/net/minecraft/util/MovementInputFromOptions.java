package net.minecraft.util;

import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.event.input.EventUpdateInput;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings par1GameSettings)
    {
        this.gameSettings = par1GameSettings;
    }

    public void updatePlayerMoveState()
    {
        if (EventBus.dispatch(new EventUpdateInput(this)))
        {
            moveForward = 0;
            moveStrafe = 0;
            jump = false;
            sneak = false;
            EventBus.dispatch(new EventUpdateInput.Post(this));
            return;
        }

        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.getIsKeyPressed())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.getIsKeyPressed())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.getIsKeyPressed())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.getIsKeyPressed())
        {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.getIsKeyPressed();
        this.sneak = this.gameSettings.keyBindSneak.getIsKeyPressed();

        final EventUpdateInput.Post event = new EventUpdateInput.Post(this);
        EventBus.dispatch(event);

        if (this.sneak && event.isModifySneaking())
        {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
