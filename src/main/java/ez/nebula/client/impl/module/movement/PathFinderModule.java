package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

@DebugFeature
@ModuleManifest(name = "PathFinder",
        description = "Allows you to pathfind to a specific location",
        category = ModuleCategory.MOVEMENT)
public final class PathFinderModule extends Module
{
    @ModuleInstance
    public static PathFinderModule INSTANCE;
}
