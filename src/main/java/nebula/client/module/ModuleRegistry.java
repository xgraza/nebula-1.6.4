package nebula.client.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import nebula.client.module.impl.combat.aura.AuraModule;
import nebula.client.module.impl.combat.autologout.AutoLogoutModule;
import nebula.client.module.impl.combat.criticals.CriticalsModule;
import nebula.client.module.impl.combat.regen.RegenModule;
import nebula.client.module.impl.combat.velocity.VelocityModule;
import nebula.client.module.impl.exploit.fastuse.FastUseModule;
import nebula.client.module.impl.exploit.freeze.FreezeModule;
import nebula.client.module.impl.exploit.noc03.NoC03Module;
import nebula.client.module.impl.exploit.nohunger.NoHungerModule;
import nebula.client.module.impl.exploit.noswing.NoSwingModule;
import nebula.client.module.impl.exploit.phase.PhaseModule;
import nebula.client.module.impl.exploit.portalchat.PortalChatModule;
import nebula.client.module.impl.exploit.thunderlocator.ThunderLocatorModule;
import nebula.client.module.impl.movement.autowalk.AutoWalkModule;
import nebula.client.module.impl.movement.guimove.GuiMoveModule;
import nebula.client.module.impl.movement.icespeed.IceSpeedModule;
import nebula.client.module.impl.movement.jesus.JesusModule;
import nebula.client.module.impl.movement.longjump.LongJumpModule;
import nebula.client.module.impl.movement.nopush.NoPushModule;
import nebula.client.module.impl.movement.noslow.NoSlowModule;
import nebula.client.module.impl.movement.speed.SpeedModule;
import nebula.client.module.impl.movement.sprint.SprintModule;
import nebula.client.module.impl.movement.tickboost.TickBoostModule;
import nebula.client.module.impl.player.antidisconnect.AntiDisconnectModule;
import nebula.client.module.impl.player.autoreconnect.AutoReconnectModule;
import nebula.client.module.impl.player.autorespawn.AutoRespawnModule;
import nebula.client.module.impl.player.autotool.AutoToolModule;
import nebula.client.module.impl.player.blink.BlinkModule;
import nebula.client.module.impl.player.fastplace.FastPlaceModule;
import nebula.client.module.impl.player.infmover.InfMoverModule;
import nebula.client.module.impl.player.keypearl.KeyPearlModule;
import nebula.client.module.impl.player.nofall.NoFallModule;
import nebula.client.module.impl.player.novoid.NoVoidModule;
import nebula.client.module.impl.player.packetmine.PacketMineModule;
import nebula.client.module.impl.player.scaffold.ScaffoldModule;
import nebula.client.module.impl.render.appleskin.AppleSkinModule;
import nebula.client.module.impl.render.chams.ChamsModule;
import nebula.client.module.impl.render.chat.ChatModule;
import nebula.client.module.impl.render.chunkborders.ChunkBordersModule;
import nebula.client.module.impl.render.clickgui.ClickGUIModule;
import nebula.client.module.impl.render.esp.ESPModule;
import nebula.client.module.impl.render.fullbright.FullBrightModule;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.module.impl.render.infviewer.InfViewerModule;
import nebula.client.module.impl.render.logoutspots.LogoutSpotsModule;
import nebula.client.module.impl.render.norender.NoRenderModule;
import nebula.client.module.impl.render.noweather.NoWeatherModule;
import nebula.client.module.impl.render.shader.ShaderModule;
import nebula.client.util.fs.FileUtils;
import nebula.client.util.fs.JSONUtils;
import nebula.client.util.registry.Registry;
import nebula.client.util.system.Reflections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static nebula.client.util.fs.JSONUtils.GSON;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unchecked")
public class ModuleRegistry implements Registry<Module> {

  /**
   * The package containing the module implementations
   */
  public static final String MODULE_IMPL = "nebula.client.module.impl";

  private static final File CONFIGS_FOLDER = new File(
    FileUtils.ROOT, "configs");

  /**
   * The logger for the registry
   */
  private static final Logger LOGGER = LogManager.getLogger("Modules");

  private final Map<Class<? extends Module>, Module> moduleInstanceMap = new LinkedHashMap<>();
  private final Set<Module> moduleSet = new LinkedHashSet<>();

  @Override
  public void init() {
//    try {
//      Reflections.classesInPackage(MODULE_IMPL, Module.class)
//        .forEach((clazz) -> {
//          try {
//            add((Module) clazz.getConstructors()[0].newInstance());
//          } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.error("Failed to invoke constructor on {}", clazz);
//            e.printStackTrace();
//            Sentry.captureException(e);
//          }
//        });
//    } catch (Exception e) {
//      e.printStackTrace();
//      Sentry.captureException(e);
//    }

    {
      add(new AuraModule());
      add(new AutoLogoutModule());
      add(new CriticalsModule());
      add(new RegenModule());
      add(new VelocityModule());

      add(new FastUseModule());
      add(new FreezeModule());
      add(new NoC03Module());
      add(new NoHungerModule());
      add(new NoSwingModule());
      add(new PhaseModule());
      add(new PortalChatModule());
      add(new ThunderLocatorModule());

      add(new AutoWalkModule());
      add(new GuiMoveModule());
      add(new IceSpeedModule());
      add(new JesusModule());
      add(new LongJumpModule());
      add(new NoPushModule());
      add(new NoSlowModule());
      add(new SpeedModule());
      add(new SprintModule());
      add(new TickBoostModule());

      add(new AntiDisconnectModule());
      add(new AutoReconnectModule());
      add(new AutoRespawnModule());
      add(new AutoToolModule());
      add(new BlinkModule());
      add(new FastPlaceModule());
      add(new InfMoverModule());
      add(new KeyPearlModule());
      add(new NoFallModule());
      add(new NoVoidModule());
      add(new PacketMineModule());
      add(new ScaffoldModule());

      add(new AppleSkinModule());
      add(new ChamsModule());
      add(new ChatModule());
      add(new ChunkBordersModule());
      add(new ClickGUIModule());
      add(new ESPModule());
      add(new FullBrightModule());
      add(new HUDModule());
      add(new InfViewerModule());
      add(new LogoutSpotsModule());
      add(new NoRenderModule());
      add(new NoWeatherModule());
      add(new ShaderModule());
    }

    LOGGER.info("Discovered {} modules from {}", moduleInstanceMap.size(), MODULE_IMPL);

    moduleSet.forEach(Module::init);

    if (!CONFIGS_FOLDER.exists()) {
      boolean result = CONFIGS_FOLDER.mkdir();
      LOGGER.info("Created {} {}", CONFIGS_FOLDER,
        result ? "successfully" : "unsuccessfully");
    }

    LOGGER.info(load("default"));
  }

  public String load(String configName) {
    File file = new File(CONFIGS_FOLDER, configName + ".cfg");
    if (!file.exists()) return "No config found with name \"" + configName + "\"";

    String content;
    try {
      content = FileUtils.readFile(file);
    } catch (IOException e) {
      LOGGER.error("Failed to read config file {}", file);
      e.printStackTrace();
      Sentry.captureException(e);

      return "Failed to read config \"" + configName + "\"";
    }

    JsonObject object = JSONUtils.parse(content, JsonObject.class);

    for (Module module : values()) {
      if (!object.has(module.meta().name())) {
        LOGGER.info("Missing element for {}", module.meta().name());
        continue;
      }

      JsonElement element = object.get(module.meta().name());
      module.load(element);
    }

    return "Loaded config \"" + configName + "\"";
  }

  public String save(String configName) throws IOException {
    File file = new File(CONFIGS_FOLDER, configName + ".cfg");
    if (!file.exists()) {
      boolean result = file.createNewFile();
      if (!result) return "Failed to create file";
    }

    JsonObject object = new JsonObject();

    for (Module module : values()) {
      JsonElement element = module.save();
      if (element == null) {
        LOGGER.info("Missing element for {}", module.meta().name());
        continue;
      }

      object.add(module.meta().name(), element);
    }

    FileUtils.writeFile(file, GSON.toJson(object));
    return "Saved config \"" + configName + "\"";
  }

  @Override
  public void add(Module... elements) {
    for (Module element : elements) {
      moduleInstanceMap.put(element.getClass(), element);
      moduleSet.add(element);
    }
  }

  @Override
  public void remove(Module... elements) {

  }

  @Override
  public Collection<Module> values() {
    return moduleSet;
  }

  public <T extends Module> T get(Class<T> clazz) {
    return (T) moduleInstanceMap.get(clazz);
  }
}
