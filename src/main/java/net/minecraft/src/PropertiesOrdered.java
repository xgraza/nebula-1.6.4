package net.minecraft.src;

import java.util.*;

public class PropertiesOrdered extends Properties
{
    private final Set<Object> keysOrdered = new LinkedHashSet();

    public synchronized Object put(Object key, Object value)
    {
        this.keysOrdered.add(key);
        return super.put(key, value);
    }

    public Set<Object> keySet()
    {
        Set keysParent = super.keySet();
        this.keysOrdered.retainAll(keysParent);
        return Collections.unmodifiableSet(this.keysOrdered);
    }

    public synchronized Enumeration<Object> keys()
    {
        return Collections.enumeration(this.keySet());
    }
}
