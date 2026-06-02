package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

/**
 * @author xgraza
 * @since 5/23/26
 */
@ModuleManifest(name = "NoFriends",
        description = "Fuck them! Attack all of your friends!",
        category = ModuleCategory.COMBAT)
public final class NoFriendsModule extends Module
{
    @ModuleInstance
    public static NoFriendsModule INSTANCE;
}
