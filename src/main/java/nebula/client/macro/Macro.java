package nebula.client.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nebula.client.config.JSONSerializable;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class Macro implements JSONSerializable {

  private int key;
  private MacroType type;
  private final MacroListener listener;

  private boolean state;
  private final boolean instant;

  public Macro(int key, MacroType type, MacroListener listener) {
    this(key, type, listener, false);
  }

  public Macro(int key, MacroType type, MacroListener listener, boolean instant) {
    this.key = key;
    this.type = type;
    this.listener = listener;
    this.instant = instant;
  }

  public int key() {
    return key;
  }

  public void setKey(int key) {
    this.key = key;
  }

  public MacroType type() {
    return type;
  }

  public void setType(MacroType type) {
    this.type = type;
  }

  public MacroListener listener() {
    return listener;
  }

  public void setEnabled(boolean state) {
    this.state = state;

    if (state) {
      listener.enable();
      if (isInstant()) setEnabled(false);
    } else {
      listener.disable();
    }
  }

  public boolean toggled() {
    return state;
  }

  public boolean isInstant() {
    return instant;
  }

  @Override
  public JsonElement save() {
    JsonObject object = new JsonObject();

    object.addProperty("key", key);
    object.addProperty("type", type.name());
    object.addProperty("state", state);

    return object;
  }

  @Override
  public void load(JsonElement element) {
    if (!element.isJsonObject()) return;
    JsonObject object = element.getAsJsonObject();

    key = object.get("key").getAsInt();
    type = MacroType.valueOf(object.get("type").getAsString());

    setEnabled(object.get("state").getAsBoolean());
  }
}
