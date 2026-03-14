package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicLightsMap
{
    private final Map<Integer, DynamicLight> map = new HashMap();
    private final List<DynamicLight> list = new ArrayList();
    private boolean dirty = false;

    public DynamicLight put(int id, DynamicLight dynamicLight)
    {
        DynamicLight old = this.map.put(Integer.valueOf(id), dynamicLight);
        this.setDirty();
        return old;
    }

    public DynamicLight get(int id)
    {
        return this.map.get(Integer.valueOf(id));
    }

    public int size()
    {
        return this.map.size();
    }

    public DynamicLight remove(int id)
    {
        DynamicLight old = this.map.remove(Integer.valueOf(id));

        if (old != null)
        {
            this.setDirty();
        }

        return old;
    }

    public void clear()
    {
        this.map.clear();
        this.setDirty();
    }

    private void setDirty()
    {
        this.dirty = true;
    }

    public List<DynamicLight> valueList()
    {
        if (this.dirty)
        {
            this.list.clear();
            this.list.addAll(this.map.values());
            this.dirty = false;
        }

        return this.list;
    }
}
