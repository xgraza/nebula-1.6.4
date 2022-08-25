package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap
{
    private transient IntHashMap.Entry[] slots = new IntHashMap.Entry[16];
    private transient int count;
    private int threshold = 12;
    private final float growFactor = 0.75F;
    private transient volatile int versionStamp;
    private Set keySet = new HashSet();
    private static final String __OBFID = "CL_00001490";

    private static int computeHash(int par0)
    {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    private static int getSlotIndex(int par0, int par1)
    {
        return par0 & par1 - 1;
    }

    public Object lookup(int par1)
    {
        int var2 = computeHash(par1);

        for (IntHashMap.Entry var3 = this.slots[getSlotIndex(var2, this.slots.length)]; var3 != null; var3 = var3.nextEntry)
        {
            if (var3.hashEntry == par1)
            {
                return var3.valueEntry;
            }
        }

        return null;
    }

    public boolean containsItem(int par1)
    {
        return this.lookupEntry(par1) != null;
    }

    final IntHashMap.Entry lookupEntry(int par1)
    {
        int var2 = computeHash(par1);

        for (IntHashMap.Entry var3 = this.slots[getSlotIndex(var2, this.slots.length)]; var3 != null; var3 = var3.nextEntry)
        {
            if (var3.hashEntry == par1)
            {
                return var3;
            }
        }

        return null;
    }

    public void addKey(int par1, Object par2Obj)
    {
        this.keySet.add(Integer.valueOf(par1));
        int var3 = computeHash(par1);
        int var4 = getSlotIndex(var3, this.slots.length);

        for (IntHashMap.Entry var5 = this.slots[var4]; var5 != null; var5 = var5.nextEntry)
        {
            if (var5.hashEntry == par1)
            {
                var5.valueEntry = par2Obj;
                return;
            }
        }

        ++this.versionStamp;
        this.insert(var3, par1, par2Obj, var4);
    }

    private void grow(int par1)
    {
        IntHashMap.Entry[] var2 = this.slots;
        int var3 = var2.length;

        if (var3 == 1073741824)
        {
            this.threshold = Integer.MAX_VALUE;
        }
        else
        {
            IntHashMap.Entry[] var4 = new IntHashMap.Entry[par1];
            this.copyTo(var4);
            this.slots = var4;
            this.threshold = (int)((float)par1 * this.growFactor);
        }
    }

    private void copyTo(IntHashMap.Entry[] par1ArrayOfIntHashMapEntry)
    {
        IntHashMap.Entry[] var2 = this.slots;
        int var3 = par1ArrayOfIntHashMapEntry.length;

        for (int var4 = 0; var4 < var2.length; ++var4)
        {
            IntHashMap.Entry var5 = var2[var4];

            if (var5 != null)
            {
                var2[var4] = null;
                IntHashMap.Entry var6;

                do
                {
                    var6 = var5.nextEntry;
                    int var7 = getSlotIndex(var5.slotHash, var3);
                    var5.nextEntry = par1ArrayOfIntHashMapEntry[var7];
                    par1ArrayOfIntHashMapEntry[var7] = var5;
                    var5 = var6;
                }
                while (var6 != null);
            }
        }
    }

    public Object removeObject(int par1)
    {
        this.keySet.remove(Integer.valueOf(par1));
        IntHashMap.Entry var2 = this.removeEntry(par1);
        return var2 == null ? null : var2.valueEntry;
    }

    final IntHashMap.Entry removeEntry(int par1)
    {
        int var2 = computeHash(par1);
        int var3 = getSlotIndex(var2, this.slots.length);
        IntHashMap.Entry var4 = this.slots[var3];
        IntHashMap.Entry var5;
        IntHashMap.Entry var6;

        for (var5 = var4; var5 != null; var5 = var6)
        {
            var6 = var5.nextEntry;

            if (var5.hashEntry == par1)
            {
                ++this.versionStamp;
                --this.count;

                if (var4 == var5)
                {
                    this.slots[var3] = var6;
                }
                else
                {
                    var4.nextEntry = var6;
                }

                return var5;
            }

            var4 = var5;
        }

        return var5;
    }

    public void clearMap()
    {
        ++this.versionStamp;
        IntHashMap.Entry[] var1 = this.slots;

        for (int var2 = 0; var2 < var1.length; ++var2)
        {
            var1[var2] = null;
        }

        this.count = 0;
    }

    private void insert(int par1, int par2, Object par3Obj, int par4)
    {
        IntHashMap.Entry var5 = this.slots[par4];
        this.slots[par4] = new IntHashMap.Entry(par1, par2, par3Obj, var5);

        if (this.count++ >= this.threshold)
        {
            this.grow(2 * this.slots.length);
        }
    }

    static class Entry
    {
        final int hashEntry;
        Object valueEntry;
        IntHashMap.Entry nextEntry;
        final int slotHash;
        private static final String __OBFID = "CL_00001491";

        Entry(int par1, int par2, Object par3Obj, IntHashMap.Entry par4IntHashMapEntry)
        {
            this.valueEntry = par3Obj;
            this.nextEntry = par4IntHashMapEntry;
            this.hashEntry = par2;
            this.slotHash = par1;
        }

        public final int getHash()
        {
            return this.hashEntry;
        }

        public final Object getValue()
        {
            return this.valueEntry;
        }

        public final boolean equals(Object par1Obj)
        {
            if (!(par1Obj instanceof IntHashMap.Entry))
            {
                return false;
            }
            else
            {
                IntHashMap.Entry var2 = (IntHashMap.Entry)par1Obj;
                Integer var3 = Integer.valueOf(this.getHash());
                Integer var4 = Integer.valueOf(var2.getHash());

                if (var3 == var4 || var3 != null && var3.equals(var4))
                {
                    Object var5 = this.getValue();
                    Object var6 = var2.getValue();

                    if (var5 == var6 || var5 != null && var5.equals(var6))
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        public final int hashCode()
        {
            return IntHashMap.computeHash(this.hashEntry);
        }

        public final String toString()
        {
            return this.getHash() + "=" + this.getValue();
        }
    }
}
