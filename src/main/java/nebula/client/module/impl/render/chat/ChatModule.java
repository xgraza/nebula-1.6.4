package nebula.client.module.impl.render.chat;

import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

/**
 * @author Gavin
 * @since 08/25/23
 */
@ModuleMeta(name = "Chat",
  description = "Changes how the chat looks")
public class ChatModule extends Module {

  @SettingMeta("Timestamps")
  public final Setting<Boolean> timestamps = new Setting<>(
    true);
  @SettingMeta("Clear Chat")
  public final Setting<Boolean> clearChat = new Setting<>(
    true);

  @SettingMeta("Infinite")
  public final Setting<Boolean> infinite = new Setting<>(
    true);

  @SettingMeta("Animate")
  public final Setting<Boolean> animate = new Setting<>(
    false);
  @SettingMeta("Speed")
  public final Setting<Double> speed = new Setting<>(
    animate::value,
    0.8, 0.1, 1.0, 0.01);
}
