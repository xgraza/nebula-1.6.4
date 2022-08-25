package optifine.json;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ItemList
{
    private String sp = ",";
    List items = new ArrayList();

    public ItemList() {}

    public ItemList(String s)
    {
        this.split(s, this.sp, this.items);
    }

    public ItemList(String s, String sp)
    {
        this.sp = s;
        this.split(s, sp, this.items);
    }

    public ItemList(String s, String sp, boolean isMultiToken)
    {
        this.split(s, sp, this.items, isMultiToken);
    }

    public List getItems()
    {
        return this.items;
    }

    public String[] getArray()
    {
        return (String[])this.items.toArray();
    }

    public void split(String s, String sp, List append, boolean isMultiToken)
    {
        if (s != null && sp != null)
        {
            if (isMultiToken)
            {
                StringTokenizer tokens = new StringTokenizer(s, sp);

                while (tokens.hasMoreTokens())
                {
                    append.add(tokens.nextToken().trim());
                }
            }
            else
            {
                this.split(s, sp, append);
            }
        }
    }

    public void split(String s, String sp, List append)
    {
        if (s != null && sp != null)
        {
            int pos = 0;
            boolean prevPos = false;
            int prevPos1;

            do
            {
                prevPos1 = pos;
                pos = s.indexOf(sp, pos);

                if (pos == -1)
                {
                    break;
                }

                append.add(s.substring(prevPos1, pos).trim());
                pos += sp.length();
            }
            while (pos != -1);

            append.add(s.substring(prevPos1).trim());
        }
    }

    public void setSP(String sp)
    {
        this.sp = sp;
    }

    public void add(int i, String item)
    {
        if (item != null)
        {
            this.items.add(i, item.trim());
        }
    }

    public void add(String item)
    {
        if (item != null)
        {
            this.items.add(item.trim());
        }
    }

    public void addAll(ItemList list)
    {
        this.items.addAll(list.items);
    }

    public void addAll(String s)
    {
        this.split(s, this.sp, this.items);
    }

    public void addAll(String s, String sp)
    {
        this.split(s, sp, this.items);
    }

    public void addAll(String s, String sp, boolean isMultiToken)
    {
        this.split(s, sp, this.items, isMultiToken);
    }

    public String get(int i)
    {
        return (String)this.items.get(i);
    }

    public int size()
    {
        return this.items.size();
    }

    public String toString()
    {
        return this.toString(this.sp);
    }

    public String toString(String sp)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < this.items.size(); ++i)
        {
            if (i == 0)
            {
                sb.append(this.items.get(i));
            }
            else
            {
                sb.append(sp);
                sb.append(this.items.get(i));
            }
        }

        return sb.toString();
    }

    public void clear()
    {
        this.items.clear();
    }

    public void reset()
    {
        this.sp = ",";
        this.items.clear();
    }
}
