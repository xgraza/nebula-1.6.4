package ez.nebula.client.api.manager.module;

import ez.nebula.client.core.ClientConfig;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.config.ModuleConfig;
import ez.nebula.client.impl.module.combat.*;
import ez.nebula.client.impl.module.exploit.*;
import ez.nebula.client.impl.module.movement.*;
import ez.nebula.client.impl.module.player.*;
import ez.nebula.client.impl.module.render.*;
import ez.nebula.client.impl.module.world.*;
import ez.nebula.client.api.manager.ITypedManager;
import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class ModuleManager implements ITypedManager<Module>
{
    private final Map<Class<? extends Module>, Module> moduleInstanceMap = new LinkedHashMap<>();
    private final List<Module> moduleInstanceList = new LinkedList<>();

    @Override
    public void init()
    {
        registerModule(new AutoArmorModule());
        registerModule(new AutoBedModule());
        registerModule(new AutoLogModule());
        registerModule(new AutoPotModule());
        registerModule(new BurrowModule());
        registerModule(new CriticalsModule());
        registerModule(new KillAuraModule());
        registerModule(new NoFriendsModule());
        registerModule(new RegenModule());
        registerModule(new VehicleBreakerModule());
        registerModule(new VelocityModule());
        registerModule(new AntiAFKModule());
        registerModule(new AntiRevertModule());
        registerModule(new ColorSignsModule());
        registerModule(new EnderchestBPModule());
        registerModule(new FastLatencyModule());
        registerModule(new FastPortalModule());
        registerModule(new FastUseModule());
        registerModule(new FrankyModule());
        registerModule(new GhostHandModule());
        registerModule(new LongChatModule());
        registerModule(new NewChunksModule());
        registerModule(new NoC03Module());
        registerModule(new NoHungerModule());
        registerModule(new NoMoveDelayModule());
        registerModule(new NoPacketKickModule());
        registerModule(new NoPortalGUIModule());
        registerModule(new NoRotateSetModule());
        registerModule(new PotionSaverModule());
        registerModule(new TimerModule());
        registerModule(new XCarryModule());
        registerModule(new ZootModule());
        registerModule(new AutoWalkModule());
        registerModule(new BlinkModule());
        registerModule(new EntityControlModule());
        registerModule(new EntitySpeedModule());
        registerModule(new FastSwimModule());
        registerModule(new FlyModule());
        registerModule(new InvWalkModule());
        registerModule(new JesusModule());
        registerModule(new LongJumpModule());
        registerModule(new NoAccelModule());
        registerModule(new NoJumpDelayModule());
        registerModule(new NoSlowModule());
        registerModule(new PathFinderModule());
        registerModule(new SafeWalkModule());
        registerModule(new SpeedModule());
        registerModule(new SprintModule());
        registerModule(new StepModule());
        registerModule(new TargetStrafeModule());
        registerModule(new TerrainModule());
        registerModule(new AntiBlockModule());
        registerModule(new AntiDisconnectModule());
        registerModule(new AntiLagModule());
        registerModule(new AutoReconnectModule());
        registerModule(new AutoRespawnModule());
        registerModule(new FreecamModule());
        registerModule(new InfiniteMoverModule());
        registerModule(new InteractModule());
        registerModule(new InventorySyncModule());
        registerModule(new KeyPearlModule());
        registerModule(new MCFModule());
        registerModule(new NoFallModule());
        registerModule(new NotifierModule());
        registerModule(new PearlPhaseModule());
        registerModule(new SpammerModule());
        registerModule(new TestModule());
        registerModule(new TranslateModule());
        registerModule(new YawModule());
        registerModule(new AmbienceModule());
        registerModule(new BetterF3Module());
        registerModule(new CameraClipModule());
        registerModule(new ChamsModule());
        registerModule(new ChatModifierModule());
        registerModule(new ChunkBoundariesModule());
        registerModule(new ClickGUIModule());
        registerModule(new EntityCullingModule());
        registerModule(new ESPModule());
        registerModule(new ExtraTabModule());
        registerModule(new FinderModule());
        registerModule(new FullbrightModule());
        registerModule(new GlintModule());
        registerModule(new HUDModule());
        registerModule(new ItemPhysicsModule());
        registerModule(new ItemTweaksModule());
        registerModule(new NametagsModule());
        registerModule(new NoRenderModule());
        registerModule(new TimeChangerModule());
        registerModule(new TracersModule());
        registerModule(new TrajectoriesModule());
        registerModule(new TunnelESPModule());
        registerModule(new UnfocusedCPUModule());
        registerModule(new ViewModelModule());
        registerModule(new WaypointsModule());
        registerModule(new XRayModule());
        registerModule(new AirPlaceModule());
        registerModule(new AntiGhostBlockModule());
        registerModule(new AutoFarmModule());
        registerModule(new AutoFishModule());
        registerModule(new AutoHighwayModule());
        registerModule(new AutoToolModule());
        registerModule(new AutoTorchModule());
        registerModule(new AutoTreeModule());
        registerModule(new AutoTunnelModule());
        registerModule(new ChestStealerModule());
        registerModule(new FakePlayerModule());
        registerModule(new FlattenModule());
        registerModule(new LandscaperModule());
        registerModule(new NukerModule());
        registerModule(new PacketMineModule());
        registerModule(new PortalBreakerModule());
        registerModule(new ScaffoldModule());
        registerModule(new StashHunterModule());

        try
        {
            ModuleConfig.loadConfig("default");
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
        }
        Nebula.INSTANCE.getLogger().info("Registered {} modules", moduleInstanceList.size());
    }

    private void registerModule(final Module module)
    {
        if (module.getClass().isAnnotationPresent(DebugFeature.class) && !ClientConfig.DEBUG)
        {
            return;
        }

        module.discoverSettings();

        moduleInstanceMap.put(module.getClass(), module);
        moduleInstanceList.add(module);

        // Find instance (if present)
        for (final Field field : module.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(ModuleInstance.class)
                    && field.getType().isAssignableFrom(module.getClass()))
            {
                try
                {
                    field.set(null, module);
                } catch (final IllegalAccessException e)
                {
                    Nebula.INSTANCE.getLogger().error("Failed to set {}$INSTANCE", module);
                    Nebula.INSTANCE.getLogger().error(e);
                }
                return;
            }
        }
    }

    @Override
    public Module getReference(final Class<Module> type)
    {
        return moduleInstanceMap.get(type);
    }

    @Override
    public List<Module> getAll()
    {
        return moduleInstanceList;
    }
}
