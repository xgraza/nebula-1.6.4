package us.nebula.impl.cheat.movement;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;

/**
 * @author xgraza
 * @since 03/13/25
 */
@CheatManifest(name = "IceSpeed",
        description = "Allows you to move super fast on ice",
        category = CheatCategory.MOVEMENT)
public final class IceSpeedCheat extends Cheat
{
    public static final float NCP_ICE_MAX = 0.391f;

    @CheatInstance
    public static IceSpeedCheat INSTANCE;
}
