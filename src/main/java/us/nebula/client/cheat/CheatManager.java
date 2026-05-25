package us.nebula.client.cheat;

import us.nebula.client.ClientSettings;
import us.nebula.client.Nebula;
import us.nebula.client.util.ITypedManager;
import us.nebula.client.util.trait.DebugFeature;
import us.nebula.client.cheat.impl.combat.*;
import us.nebula.client.cheat.impl.exploit.*;
import us.nebula.client.cheat.impl.movement.*;
import us.nebula.client.cheat.impl.player.*;
import us.nebula.client.cheat.impl.render.*;
import us.nebula.client.cheat.impl.world.*;
import us.nebula.client.cheat.trait.CheatInstance;

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
public final class CheatManager implements ITypedManager<Cheat>
{
    private final Map<Class<? extends Cheat>, Cheat> cheatInstanceMap = new LinkedHashMap<>();
    private final List<Cheat> cheatInstanceList = new LinkedList<>();

    @Override
    public void init()
    {
        addCheat(new AutoArmorCheat());
        addCheat(new AutoBedCheat());
        addCheat(new AutoLogCheat());
        addCheat(new AutoPotCheat());
        addCheat(new BurrowCheat());
        addCheat(new CriticalsCheat());
        addCheat(new KillAuraCheat());
        addCheat(new NoFriendsCheat());
        addCheat(new RegenCheat());
        addCheat(new VehicleBreakerCheat());
        addCheat(new VelocityCheat());
        addCheat(new AntiRevertCheat());
        addCheat(new EnderchestBPCheat());
        addCheat(new FastLatencyCheat());
        addCheat(new FastPortalCheat());
        addCheat(new FastUseCheat());
        addCheat(new FrankyCheat());
        addCheat(new GhostHandCheat());
        addCheat(new NewChunksCheat());
        addCheat(new NoC03Cheat());
        addCheat(new NoHungerCheat());
        addCheat(new NoMoveDelayCheat());
        addCheat(new NoPacketKickCheat());
        addCheat(new NoPortalGUICheat());
        addCheat(new NoRotateSetCheat());
        addCheat(new PotionSaverCheat());
        addCheat(new TimerCheat());
        addCheat(new XCarryCheat());
        addCheat(new ZootCheat());
        addCheat(new AutoWalkCheat());
        addCheat(new BlinkCheat());
        addCheat(new FlyCheat());
        addCheat(new InvWalkCheat());
        addCheat(new JesusCheat());
        addCheat(new LongJumpCheat());
        addCheat(new NoJumpDelayCheat());
        addCheat(new NoSlowCheat());
        // addCheat(new PathFinderCheat());
        addCheat(new SpeedCheat());
        addCheat(new SprintCheat());
        addCheat(new StaticCheat());
        addCheat(new StepCheat());
        addCheat(new TargetStrafeCheat());
        addCheat(new TerrainCheat());
        addCheat(new AntiBlockCheat());
        addCheat(new AntiDisconnectCheat());
        addCheat(new AntiLagCheat());
        addCheat(new AutoReconnectCheat());
        addCheat(new AutoRespawnCheat());
        addCheat(new FreecamCheat());
        addCheat(new InfiniteMoverCheat());
        addCheat(new InteractCheat());
        addCheat(new InventorySyncCheat());
        addCheat(new KeyPearlCheat());
        addCheat(new MCFCheat());
        addCheat(new NoFallCheat());
        addCheat(new NotifierCheat());
        addCheat(new PearlPhaseCheat());
        addCheat(new SpammerCheat());
        // addCheat(new TestCheat());
        addCheat(new TranslateCheat());
        addCheat(new YawCheat());
        addCheat(new AmbienceCheat());
        addCheat(new BetterF3Cheat());
        addCheat(new CameraClipCheat());
        addCheat(new ChamsCheat());
        addCheat(new ChatModifierCheat());
        addCheat(new ChunkBoundariesCheat());
        addCheat(new ClickGUICheat());
        addCheat(new EntityCullingCheat());
        addCheat(new ESPCheat());
        addCheat(new ExtraTabCheat());
        addCheat(new FullbrightCheat());
        addCheat(new HUDCheat());
        addCheat(new ItemPhysicsCheat());
        addCheat(new ItemTweaksCheat());
        addCheat(new NametagsCheat());
        addCheat(new NoRenderCheat());
        addCheat(new TimeChangerCheat());
        addCheat(new TrajectoriesCheat());
        addCheat(new UnfocusedCPUCheat());
        addCheat(new ViewModelCheat());
        addCheat(new XRayCheat());
        addCheat(new AntiGhostBlockCheat());
        addCheat(new AutoFarmCheat());
        addCheat(new AutoFishCheat());
        addCheat(new AutoHighwayCheat());
        addCheat(new AutoInfiniteCheat());
        addCheat(new AutoToolCheat());
        addCheat(new AutoTorchCheat());
        addCheat(new AutoTunnelCheat());
        addCheat(new FakePlayerCheat());
        addCheat(new FlattenCheat());
        addCheat(new LandscaperCheat());
        addCheat(new NukerCheat());
        addCheat(new PacketMineCheat());
        // addCheat(new PortalBreakerCheat());
        addCheat(new ScaffoldCheat());
        addCheat(new StashHunterCheat());

        try
        {
            CheatConfig.loadConfig("default");
        } catch (final IOException e)
        {
            Nebula.INSTANCE.getLogger().error(e);
        }
        Nebula.INSTANCE.getLogger().info("Registered {} cheats", cheatInstanceList.size());
    }

    private void addCheat(final Cheat cheat)
    {
        if (cheat.getClass().isAnnotationPresent(DebugFeature.class) && !ClientSettings.DEBUG)
        {
            return;
        }

        cheat.reflectSettings();

        cheatInstanceMap.put(cheat.getClass(), cheat);
        cheatInstanceList.add(cheat);

        // Find instance (if present)
        for (final Field field : cheat.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(CheatInstance.class)
                    && field.getType().isAssignableFrom(cheat.getClass()))
            {
                try
                {
                    field.set(null, cheat);
                } catch (final IllegalAccessException e)
                {
                    Nebula.INSTANCE.getLogger().error("Failed to set {}$INSTANCE", cheat);
                    Nebula.INSTANCE.getLogger().error(e);
                }
                return;
            }
        }
    }

    @Override
    public Cheat getReference(final Class<Cheat> type)
    {
        return cheatInstanceMap.get(type);
    }

    @Override
    public List<Cheat> getAll()
    {
        return cheatInstanceList;
    }
}
