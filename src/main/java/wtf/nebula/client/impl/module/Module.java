package wtf.nebula.client.impl.module;

import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.utils.client.Labeled;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Module implements Labeled {
    private final String label;
    private final String[] aliases;
    protected ModuleCategory category;

    private final Map<String, Property> propertyMap = new LinkedHashMap<>();

    public Module(String label, String[] aliases) {
        this(label, aliases, ModuleCategory.ACTIVE);
        Launcher.BUS.subscribe(this);
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

    protected void offerProperties(Property... properties) {
        for (Property property : properties) {
            propertyMap.put(property.getLabel(), property);
        }
    }

    public Map<String, Property> getPropertyMap() {
        return propertyMap;
    }
}
