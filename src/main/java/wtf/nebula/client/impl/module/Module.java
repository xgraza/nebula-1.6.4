package wtf.nebula.client.impl.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.utils.client.Labeled;

import java.util.LinkedHashMap;
import java.util.Map;

public class Module implements Labeled {
    private final String label;
    private final String[] aliases;
    protected ModuleCategory category;

    private final Map<String, Property> propertyMap = new LinkedHashMap<>();

    private String tag = null;

    public Module(String label, String[] aliases) {
        this(label, aliases, ModuleCategory.ACTIVE);
        Nebula.BUS.subscribe(this);
    }

    public Module(String label, String[] aliases, ModuleCategory category) {
        this.label = label;
        this.aliases = aliases;
        this.category = category;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getInfo() {
        String l = getLabel();
        if (getTag() != null) {
            l += " " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + getTag() + EnumChatFormatting.GRAY + "]";
        }

        return l;
    }

    protected void offerProperties(Property... properties) {
        for (Property property : properties) {
            propertyMap.put(property.getLabel(), property);
        }
    }

    public Map<String, Property> getPropertyMap() {
        return propertyMap;
    }

    public JsonObject toConfig() {
        JsonObject settings = new JsonObject();

        propertyMap.forEach((name, prop) -> {
            if (prop.getValue() instanceof Boolean) {
                settings.addProperty(name, (boolean) prop.getValue());
            } else if (prop.getValue() instanceof Number) {
                if (prop.getValue() instanceof Integer) {
                    settings.addProperty(name, (int) prop.getValue());
                } else if (prop.getValue() instanceof Double) {
                    settings.addProperty(name, (double) prop.getValue());
                } else if (prop.getValue() instanceof Float) {
                    settings.addProperty(name, (float) prop.getValue());
                }
            } else if (prop.getValue() instanceof Enum) {
                settings.addProperty(name, (String) ((Enum) prop.getValue()).name());
            } else if (prop.getValue() instanceof String) {
                settings.addProperty(name, (String) prop.getValue());
            }
        });

        JsonObject obj = new JsonObject();
        obj.add("properties", settings);
        return obj;
    }

    public void fromConfig(JsonObject object) {
        if (object.has("properties")) {
            JsonObject obj = object.get("properties").getAsJsonObject();

            propertyMap.forEach((name, prop) -> {
                JsonElement element = obj.get(name);
                if (element != null) {
                    if (prop.getValue() instanceof Boolean) {
                        prop.setValue((boolean) element.getAsBoolean());
                    } else if (prop.getValue() instanceof Number) {
                        if (prop.getValue() instanceof Integer) {
                            prop.setValue((int) element.getAsInt());
                        } else if (prop.getValue() instanceof Double) {
                            prop.setValue((double) element.getAsDouble());
                        } else if (prop.getValue() instanceof Float) {
                            prop.setValue((float) element.getAsFloat());
                        }
                    } else if (prop.getValue() instanceof Enum) {
                        try {
                            Enum val = Enum.valueOf(((Enum) prop.getValue()).getClass(), element.getAsString());
                            prop.setValue(val);
                        } catch (Exception e) {
                            if (Nebula.VERSION.isDev()) {
                                e.printStackTrace();
                            }
                        }
                    } else if (prop.getValue() instanceof String) {
                        prop.setValue((String) element.getAsString());
                    }
                }
            });
        }
    }
}
