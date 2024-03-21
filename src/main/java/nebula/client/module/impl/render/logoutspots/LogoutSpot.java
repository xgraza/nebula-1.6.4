package nebula.client.module.impl.render.logoutspots;

import net.minecraft.util.Vec3;

/**
 * @author Gavin
 * @since 08/24/23
 * @param name the name of the entity that logged out
 * @param fakeId the id of the fake player entity
 * @param position the position the logged out player logged out at
 */
public record LogoutSpot(String name, int fakeId, Vec3 position) {
}
