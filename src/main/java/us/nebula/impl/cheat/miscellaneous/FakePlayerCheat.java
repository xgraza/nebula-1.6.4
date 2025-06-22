package us.nebula.impl.cheat.miscellaneous;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.DamageSource;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.util.player.ChatUtil;

import java.util.UUID;

/**
 * @author xgraza
 * @since 06/06/25
 */
@CheatManifest(name = "FakePlayer",
        description = "Spawns a fake player to test things on",
        category = CheatCategory.MISCELLANEOUS)
public final class FakePlayerCheat extends Cheat
{
    private static final GameProfile FAKE_PROFILE = new GameProfile(
            UUID.randomUUID().toString(),
            "Aestheticall2");
    private static final int FAKE_ENTITY_ID = -1337420;

    private final Setting<Boolean> takeDamageSetting = new Setting<>(
            "Take Damage", false);
    private final Setting<Boolean> gapChugSetting = new Setting<>(
            "Gap Chug", false, takeDamageSetting::getValue);

    private EntityOtherPlayerMP fakePlayerEntity;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (MC.theWorld != null && fakePlayerEntity != null)
        {
            MC.theWorld.removeEntityFromWorld(FAKE_ENTITY_ID);
            MC.theWorld.removePlayerEntityDangerously(fakePlayerEntity);
        }
        fakePlayerEntity = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (isFakePlayerInvalidated())
        {
            if (fakePlayerEntity != null)
            {
                ChatUtil.send("Removing old entity...");
                MC.theWorld.removeEntityFromWorld(FAKE_ENTITY_ID);
                MC.theWorld.removePlayerEntityDangerously(fakePlayerEntity);
            }
            if (MC.thePlayer.ticksExisted < 20)
            {
                return;
            }
            fakePlayerEntity = new EntityOtherPlayerMP(MC.theWorld, MC.thePlayer.getGameProfile());
            fakePlayerEntity.setEntityId(FAKE_ENTITY_ID);
            fakePlayerEntity.dimension = MC.thePlayer.dimension;
            fakePlayerEntity.setLocationAndAngles(
                    MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ,
                    MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch);
            fakePlayerEntity.inventory.copyInventory(MC.thePlayer.inventory);
            MC.theWorld.addEntityToWorld(FAKE_ENTITY_ID, fakePlayerEntity);
            ChatUtil.send("spawning new entity");
            return;
        }
    };

    private boolean isFakePlayerInvalidated()
    {
        return fakePlayerEntity == null
                //|| MC.theWorld.getEntityByID(FAKE_ENTITY_ID) == null
                || fakePlayerEntity.isDead
                || fakePlayerEntity.dimension != MC.thePlayer.dimension;
    }
}
