package us.nebula.client.cheat.impl.combat;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;

/**
 * @author xgraza
 * @since 5/23/26
 */
@CheatManifest(name = "NoFriends",
        description = "Fuck them! Attack all of your friends!",
        category = CheatCategory.COMBAT)
public final class NoFriendsCheat extends Cheat
{
    @CheatInstance
    public static NoFriendsCheat INSTANCE;
}
