package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap
{
    /** An array of HashEntries representing the heads of hash slot lists */
    private transient Entry[] slots = new Entry[16];

    /** The number of items stored in this map */
    private transient int count;

    /** The grow threshold */
    private int threshold = 12;

    /** The scale factor used to determine when to grow the table */
    private final float growFactor = 0.75F;

    /** A serial stamp used to mark changes */
    private transient volatile int versionStamp;

    /** The set of all the keys stored in this MCHash object */
    private Set keySet = new HashSet();
    private static final String __OBFID = "CL_00001490";

    /**
     * Makes the passed in integer suitable for hashing by a number of shifts
     */
    private static int computeHash(int par0)
    {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    /**
     * Computes the index of the slot for the hash and slot count passed in.
     */
    private static int getSlotIndex(int par0, int par1)
    {
        return par0 & par1 - 1;
    }

    /**
     * Returns the object associated to a key
     */
    public Object lookup(int par1)
    {
        int var2 = computeHash(par1);

        for (Entry var3 = this.slots[getSlotIndex(var2, this.slots.length)]; var3 != null; var3 = var3.nextEntry)
        {
            if (var3.hashEntry == par1)
            {
                return var3.valueEntry;
            }
        }

        return null;
    }

    /**
     * Return true if an object is associated with the given key
     */
    public boolean containsItem(int par1)
    {
        return this.lookupEntry(par1) != null;
    }

    /**
     * Returns the key/object mapping for a given key as a MCHashEntry
     */
    final Entry lookupEntry(int par1)
    {
        int var2 = computeHash(par1);

        for (Entry var3 = this.slots[getSlotIndex(var2, this.slots.length)]; var3 != null; var3 = var3.nextEntry)
        {
            if (var3.hashEntry == par1)
            {
                return var3;
            }
        }

        return null;
    }

    /**
     * Adds a key and associated value to this map
     */
    public void addKey(int par1, Object par2Obj)
    {
        this.keySet.add(Integer.valueOf(par1));
        int var3 = computeHash(par1);
        int var4 = getSlotIndex(var3, this.slots.length);

        for (Entry var5 = this.slots[var4]; var5 != null; var5 = var5.nextEntry)
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

    /**
     * Increases the number of hash slots
     */
    private void grow(int par1)
    {
        Entry[] var2 = this.slots;
        int var3 = var2.length;

        if (var3 == 1073741824)
        {
            this.threshold = Integer.MAX_VALUE;
        }
        else
        {
            Entry[] var4 = new Entry[par1];
            this.copyTo(var4);
            this.slots = var4;
            this.threshold = (int)((float)par1 * this.growFactor);
        }
    }

    /**
     * Copies the hash slots to a new array
     */
    private void copyTo(Entry[] par1ArrayOfIntHashMapEntry)
    {
        Entry[] var2 = this.slots;
        int var3 = par1ArrayOfIntHashMapEntry.length;

        for (int var4 = 0; var4 < var2.length; ++var4)
        {
            Entry var5 = var2[var4];

            if (var5 != null)
            {
                var2[var4] = null;
                Entry var6;

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

    /**
     * Removes the specified object from the map and returns it
     */
    public Object removeObject(int par1)
    {
        this.keySet.remove(Integer.valueOf(par1));
        Entry var2 = this.removeEntry(par1);
        return var2 == null ? null : var2.valueEntry;
    }

    /**
     * Removes the specified entry from the map and returns it
     */
    final Entry removeEntry(int par1)
    {
        int var2 = computeHash(par1);
        int var3 = getSlotIndex(var2, this.slots.length);
        Entry var4 = this.slots[var3];
        Entry var5;
        Entry var6;

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

    /**
     * Removes all entries from the map
     */
    public void clearMap()
    {
        ++this.versionStamp;
        Entry[] var1 = this.slots;

        for (int var2 = 0; var2 < var1.length; ++var2)
        {
            var1[var2] = null;
        }

        this.count = 0;
    }

    /**
     * Adds an object to a slot
     */
    private void insert(int par1, int par2, Object par3Obj, int par4)
    {
        Entry var5 = this.slots[par4];
        this.slots[par4] = new Entry(par1, par2, par3Obj, var5);

        if (this.count++ >= this.threshold)
        {
            this.grow(2 * this.slots.length);
        }
    }

    static class Entry
    {
        final int hashEntry;
        Object valueEntry;
        Entry nextEntry;
        final int slotHash;
        private static final String __OBFID = "CL_00001491";

        Entry(int par1, int par2, Object par3Obj, Entry par4IntHashMapEntry)
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
            if (!(par1Obj instanceof Entry))
            {
                return false;
            }
            else
            {
                Entry var2 = (Entry)par1Obj;
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
