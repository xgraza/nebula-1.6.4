package us.nebula.client.cheat.impl.movement;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.IEventPriorities;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.cheat.impl.combat.KillAuraCheat;
import us.nebula.client.cheat.impl.render.HUDCheat;
import us.nebula.client.listener.event.player.EventMove;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.player.MoveUtil;
import us.nebula.client.util.render.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 3/16/26
 */
@CheatManifest(name = "TargetStrafe",
        description = "Moves in a circle around your KillAura target to make it harder for them to attack you back",
        category = CheatCategory.MOVEMENT)
public final class TargetStrafeCheat extends Cheat
{
    private final Setting<Float> rangeSetting = new Setting<>(
            "Range", 4.2f, 1.0f, 6.0f, 0.1f);
    private final Setting<Double> reductionSetting = new Setting<>(
            "Speed Reduction", 0.0, 0.0, 1.0, 0.01);
    private final Setting<Boolean> jumpBackoutSetting = new Setting<>(
            "Jump to Backout", true);
    private final Setting<Boolean> renderSetting = new Setting<>(
            "Render", true);

    private boolean directional = true;

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (!renderSetting.getValue() || isBlocked())
        {
            return;
        }
        final EntityLivingBase target = KillAuraCheat.INSTANCE.getTarget();

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(2.5f);

        RenderUtil.setGLColor(HUDCheat.INSTANCE.getBaseColor(10));
        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        final double x = target.prevPosX + (target.posX - target.prevPosX) * event.getPartialTicks();
        final double y = target.prevPosY + (target.posY - target.prevPosY) * event.getPartialTicks();
        final double z = target.prevPosZ + (target.posZ - target.prevPosZ) * event.getPartialTicks();

        glBegin(GL_LINE_LOOP);
        {
            final double radius = rangeSetting.getValue();
            for (double angle = 0.0; angle <= 360.0; angle += 1.0)
            {
                final double rad = Math.toRadians(angle);
                glVertex3d(x + (Math.sin(rad) * radius), y, z - (Math.cos(rad) * radius));
            }
        }
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    };

    @Subscribe(priority = IEventPriorities.LOW)
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (isBlocked())
        {
            return;
        }

        if (MC.thePlayer.isCollidedHorizontally)
        {
            directional = !directional;
        } else
        {
            if (MC.gameSettings.keyBindLeft.pressed)
            {
                directional = true;
            } else if (MC.gameSettings.keyBindRight.pressed)
            {
                directional = false;
            }
        }

        final EntityLivingBase target = KillAuraCheat.INSTANCE.getTarget();
        final double moveSpeed = Math.sqrt(event.getX() * event.getX() + event.getZ() * event.getZ())
                * (1.0 - reductionSetting.getValue());

        double degree = Math.atan2(MC.thePlayer.posZ - target.posZ, MC.thePlayer.posX - target.posX);
        degree += (moveSpeed / MC.thePlayer.getDistanceToEntity(target)) * (directional ? 1 : -1);

        // target strafe will tweak if strafe range is > than ka range
        double dist = Math.min(rangeSetting.getValue(), KillAuraCheat.INSTANCE.rangeSetting.getValue())
                - Math.max(MC.thePlayer.movementInput.moveForward, 0.0) - 0.1;

        double x = target.posX + dist * Math.cos(degree);
        double z = target.posZ + dist * Math.sin(degree);

        float yaw = (float) (Math.toDegrees(Math.atan2(z - MC.thePlayer.posZ, x - MC.thePlayer.posX)) - 90.0);

        double rad = Math.toRadians(yaw);
        event.setX(moveSpeed * -Math.sin(rad));
        event.setZ(moveSpeed * Math.cos(rad));
    };

    private boolean isBlocked()
    {
        return !KillAuraCheat.INSTANCE.isAttacking()
                || !SpeedCheat.INSTANCE.isToggled()
                || !MoveUtil.isMoving()
                || MC.gameSettings.keyBindBack.pressed // allow to backout of the target strafe
                || (jumpBackoutSetting.getValue() && MC.gameSettings.keyBindJump.pressed); // additionally, holding jump backs out
    }
}
