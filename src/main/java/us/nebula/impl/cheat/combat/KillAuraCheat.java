package us.nebula.impl.cheat.combat;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.render.EventRender3D;
import us.nebula.util.Timer;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/02/25
 */
@SuppressWarnings("unchecked")
@CheatManifest(name = "KillAura",
        description = "Attacks entities in your range",
        category = CheatCategory.COMBAT)
public final class KillAuraCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.SINGLE);
    private final Setting<Priority> prioritySetting = new Setting<>(
            "Priority", Priority.DISTANCE);
    private final Setting<Float> rangeSetting = new Setting<>(
            "Range", 4.2f, 1.0f, 6.0f, 0.1f);
    private final Setting<Boolean> wallsSetting = new Setting<>(
            "Walls", true);
    private final Setting<Weapon> weaponSetting = new Setting<>(
            "Weapon", Weapon.NONE);
    private final Setting<Boolean> autoBlockSetting = new Setting<>(
            "Auto Block", true);
    private final Setting<Boolean> tickSetting = new Setting<>(
            "Tick", false);
    private final Setting<Boolean> armorBreaker = new Setting<>(
            "Armor Breaker", false);
    private final Setting<Boolean> keepSprint = new Setting<>(
            "Keep Sprint", false);
    private final Setting<Boolean> attackPlayersSetting = new Setting<>(
            "Attack Players", true);
    private final Setting<Boolean> attackHostileSetting = new Setting<>(
            "Attack Hostile", true);
    private final Setting<Boolean> attackPassiveSetting = new Setting<>(
            "Attack Passive", true);
    private final Setting<Boolean> renderSetting = new Setting<>(
            "Render", false);

    private final Animation renderAnimation = new Animation(
            AnimationEasing.CUBIC_IN_OUT, 750.0);

    private final Timer timer = new Timer();
    private EntityLivingBase target;
    private boolean blocking;

    @Override
    protected void onDisable()
    {
        super.onDisable();

        if (blocking && autoBlockSetting.getValue())
        {
            blockSword(false);
        }
        blocking = false;
        target = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (!handleWeapon())
        {
            return;
        }
        if (modeSetting.getValue() == Mode.SWITCH || !isValidEntity(target))
        {
            target = getNextTarget();
        }
        if (target == null)
        {
            if (blocking)
            {
                blockSword(false);
            }
            return;
        }
        if (canAttack())
        {
            timer.resetTime();

            if (autoBlockSetting.getValue())
            {
                blockSword(false);
            }

            attackTarget();

            if (armorBreaker.getValue())
            {
                attackTarget();
                MC.thePlayer.sendQueue.addToSendQueue(
                        new C09PacketHeldItemChange(8));
                attackTarget();
                attackTarget();
                MC.thePlayer.sendQueue.addToSendQueue(
                        new C09PacketHeldItemChange(MC.thePlayer.inventory.currentItem));
            }
        }
        if (autoBlockSetting.getValue())
        {
            blockSword(true);
        }
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (!renderSetting.getValue() || target == null)
        {
            return;
        }

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(2.5f);

        if (renderAnimation.getFactor() >= 1.0 || renderAnimation.getFactor() <= 0.0)
        {
            renderAnimation.setState(!renderAnimation.getState());
        }

        final float hurtTime = target.hurtTime / (float) target.maxHurtTime;
        if (hurtTime > 0.0f) {
            glColor4f(0.3f + hurtTime, 0.0f, 0.0f, 1.0f);
        } else {
            glColor4f(1.0f - (target.hurtResistantTime / (float) target.maxHurtResistantTime),
                    1.0f, 1.0f, 1.0f);
        }
        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        final double x = target.prevPosX + (target.posX - target.prevPosX) * event.getPartialTicks();
        final double y = target.prevPosY + (target.posY - target.prevPosY) * event.getPartialTicks();
        final double z = target.prevPosZ + (target.posZ - target.prevPosZ) * event.getPartialTicks();

        glBegin(GL_LINE_LOOP);
        {
            final double radius = target.width + 0.2;
            for (double angle = 0.0; angle <= 360.0; angle += 1.0)
            {
                final double rad = Math.toRadians(angle);
                glVertex3d(x + (Math.sin(rad) * radius),
                        y + target.height - (target.height * renderAnimation.getEasedFactor()),
                        z - (Math.cos(rad) * radius));
            }
        }
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    };

    private void attackTarget()
    {
        MC.thePlayer.swingItem();
        if (keepSprint.getValue())
        {
            MC.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(
                    target, C02PacketUseEntity.Action.ATTACK));
        } else
        {
            MC.playerController.attackEntity(MC.thePlayer, target);
        }
    }

    private boolean canAttack()
    {
        if (tickSetting.getValue())
        {
            return true;
        }
        return timer.hasElapsed((long) (50L + (Math.random() * 150)))
                || target.hurtResistantTime <= 0;
    }

    private void blockSword(final boolean block)
    {
        final ItemStack itemStack = MC.thePlayer.getHeldItem();
        if (itemStack == null || !(itemStack.getItem() instanceof ItemSword))
        {
            blocking = false;
            return;
        }

        if (block)
        {
            blocking = true;
            MC.playerController.sendUseItem(MC.thePlayer, MC.theWorld, itemStack);
        } else
        {
            blocking = false;
            MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    5, 0, 0, 0, 255));
        }
    }

    private boolean handleWeapon()
    {
        final ItemStack itemStack = MC.thePlayer.getHeldItem();
        if (blocking && (itemStack == null || !(itemStack.getItem() instanceof ItemSword)))
        {
            blocking = false;
        }

        switch (weaponSetting.getValue())
        {
            case SWAP:
            {
                float maxDamage = 0.0f;
                int slot = -1;
                for (int i = 0; i < 9; ++i)
                {
                    final ItemStack stack = MC.thePlayer.inventory.getStackInSlot(i);
                    if (stack == null || !(stack.getItem() instanceof ItemSword))
                    {
                        continue;
                    }
                    final float itemDamage = ((ItemSword)stack.getItem()).itemDamage;
                    // TODO: calc w/ enchantments
                    if (itemDamage > maxDamage)
                    {
                        maxDamage = itemDamage;
                        slot = i;
                    }
                }
                if (slot != -1)
                {
                    MC.thePlayer.inventory.currentItem = slot;
                    return true;
                }
                return false;
            }
            case REQUIRE:
            {
                if (itemStack == null)
                {
                    return false;
                }
                return itemStack.getItem() instanceof ItemSword;
            }
            default:
                return true;
        }
    }

    private EntityLivingBase getNextTarget()
    {
        return (EntityLivingBase) ((List<Entity>) MC.theWorld.loadedEntityList)
                .stream()
                .filter((entity) -> entity instanceof EntityLivingBase
                        && isValidEntity((EntityLivingBase) entity))
                .min(Comparator.comparingDouble((entity)
                        -> prioritySetting.getValue().test((EntityLivingBase) entity)))
                .orElse(null);
    }

    private boolean isValidEntity(final EntityLivingBase entity)
    {
        if (entity == null || entity.getHealth() <= 0.0f || entity.isDead || entity.equals(MC.thePlayer))
        {
            return false;
        }
        final double distanceSq = MC.thePlayer.getDistanceSqToEntity(entity);
        if (distanceSq > rangeSetting.getValue() * rangeSetting.getValue())
        {
            return false;
        }
        if (!wallsSetting.getValue() && !MC.thePlayer.canEntityBeSeen(entity))
        {
            return false;
        }
        if (!attackPlayersSetting.getValue() && entity instanceof EntityPlayer)
        {
            return false;
        }
        // TODO: hostile & passive entities
        return true;
    }

    private enum Mode
    {
        SINGLE, SWITCH
    }

    private enum Priority
    {
        DISTANCE((entity) -> MC.thePlayer.getDistanceSqToEntity(entity)),
        HEALTH((entity) -> (double) (entity.getHealth() + entity.getAbsorptionAmount())),
        ARMOR((entity) -> (double) entity.getTotalArmorValue());

        private final Function<EntityLivingBase, Double> sortFunction;

        Priority(final Function<EntityLivingBase, Double> sortFunction)
        {
            this.sortFunction = sortFunction;
        }

        public double test(final EntityLivingBase entity)
        {
            return sortFunction.apply(entity);
        }
    }

    private enum Weapon
    {
        NONE, SWAP, REQUIRE
    }
}
