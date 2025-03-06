package us.nebula.api.manager.cheat;

import us.nebula.Nebula;
import us.nebula.api.manager.ITypedManager;
import us.nebula.impl.cheat.combat.KillAuraCheat;
import us.nebula.impl.cheat.combat.RegenCheat;
import us.nebula.impl.cheat.combat.VelocityCheat;
import us.nebula.impl.cheat.exploit.*;
import us.nebula.impl.cheat.movement.*;
import us.nebula.impl.cheat.player.AutoRespawnCheat;
import us.nebula.impl.cheat.player.InfiniteMoverCheat;
import us.nebula.impl.cheat.player.KeyPearlCheat;
import us.nebula.impl.cheat.player.ScaffoldCheat;
import us.nebula.impl.cheat.render.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class CheatManager implements ITypedManager<Cheat>
{
    private final Map<Class<? extends Cheat>, Cheat> cheatInstanceMap = new LinkedHashMap<>();
    private final List<Cheat> cheatInstanceList = new LinkedList<>();

    private final Map<String, CheatConfig> cachedConfigMap = new HashMap<>();
    private CheatConfig defaultConfig;

    @Override
    public void init()
    {
        addCheat(new KillAuraCheat());
        addCheat(new RegenCheat());
        addCheat(new VelocityCheat());
        addCheat(new FrankyCheat());
        addCheat(new NoHungerCheat());
        addCheat(new NoPortalGUICheat());
        addCheat(new TimerCheat());
        addCheat(new XCarryCheat());
        addCheat(new InvWalkCheat());
        addCheat(new JesusCheat());
        addCheat(new NoPushCheat());
        addCheat(new NoSlowCheat());
        addCheat(new SprintCheat());
        addCheat(new AutoRespawnCheat());
        addCheat(new InfiniteMoverCheat());
        addCheat(new KeyPearlCheat());
        addCheat(new ScaffoldCheat());
        addCheat(new CameraClipCheat());
        addCheat(new ClickGUICheat());
        addCheat(new FullbrightCheat());
        addCheat(new HUDCheat());
        addCheat(new InfiniteViewerCheat());
        addCheat(new TrajectoriesCheat());

        defaultConfig = new CheatConfig(this, "default");
        Nebula.INSTANCE.getConfigurationManager().addConfiguration(defaultConfig);

        Nebula.INSTANCE.getLogger().info("Registered {} cheats", cheatInstanceList.size());
    }

    private void addCheat(final Cheat cheat)
    {
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

    public CheatConfig getDefaultConfig()
    {
        return defaultConfig;
    }
}
