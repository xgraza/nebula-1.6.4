package nebula.client.util.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gavin
 * @since 08/24/23
 */
public class FakePlayerUtils {

  /**
   * A map containing the fake player id and its instance
   */
  private static final Map<Integer, EntityOtherPlayerMP> fakes = new ConcurrentHashMap<>();

  /**
   * The minecraft game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * Spawns a fake player
   * @param id the entity id of the fake player
   * @param copy the entity to copy (nullable)
   */
  public static void spawn(int id, EntityPlayer copy) {
    if (!fakes.containsKey(id) && mc.theWorld != null) {

      EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.theWorld,
        copy == null
          ? new GameProfile(UUID.randomUUID().toString(), "FakePlayer" + id)
          : copy.getGameProfile());

      if (copy != null) {
        fake.setHealth(copy.getHealth());
        fake.setAbsorptionAmount(copy.getAbsorptionAmount());

        fake.copyLocationAndAnglesFrom(copy);
        fake.inventory.copyInventory(copy.inventory);

        if (copy.equals(mc.thePlayer)) {
          fake.setPosition(mc.thePlayer.posX,
            mc.thePlayer.boundingBox.minY,
            mc.thePlayer.posZ);
        }
      }

      fake.setEntityId(id);

      mc.theWorld.spawnEntityInWorld(fake);
      mc.theWorld.addEntityToWorld(id, fake);

      fakes.put(id, fake);
    }
  }

  /**
   * Despawns a fake player
   * @param id the entity id of the fake player
   */
  public static void despawn(int id) {
    if (fakes.containsKey(id) && mc.theWorld != null) {
      if (mc.theWorld.getEntityByID(id) == null) {
        fakes.remove(id);
        return;
      }

      EntityOtherPlayerMP fake = fakes.remove(id);

      mc.theWorld.removeEntity(fake);
      mc.theWorld.removePlayerEntityDangerously(fake);
      mc.theWorld.removeEntityFromWorld(fake.getEntityId());
    }
  }

  public static boolean isFake(int id) {
    return fakes.containsKey(id);
  }

  public static void clear() {
    fakes.clear();
  }
}
