package optifine.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JSONObject extends LinkedHashMap implements Map, JSONAware, JSONStreamAware
{
    private static final long serialVersionUID = -503443796854799292L;

    public static void writeJSONString(Map map, Writer out) throws IOException
    {
        if (map == null)
        {
            out.write("null");
        }
        else
        {
            boolean first = true;
            Iterator iter = map.entrySet().iterator();
            out.write(123);

            while (iter.hasNext())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    out.write(44);
                }

                Entry entry = (Entry)iter.next();
                out.write(34);
                out.write(escape(String.valueOf(entry.getKey())));
                out.write(34);
                out.write(58);
                JSONValue.writeJSONString(entry.getValue(), out);
            }

            out.write(125);
        }
    }

    public void writeJSONString(Writer out) throws IOException
    {
        writeJSONString(this, out);
    }

    public static String toJSONString(Map map)
    {
        if (map == null)
        {
            return "null";
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            Iterator iter = map.entrySet().iterator();
            sb.append('{');

            while (iter.hasNext())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    sb.append(',');
                }

                Entry entry = (Entry)iter.next();
                toJSONString(String.valueOf(entry.getKey()), entry.getValue(), sb);
            }

            sb.append('}');
            return sb.toString();
        }
    }

    public String toJSONString()
    {
        return toJSONString(this);
    }

    private static String toJSONString(String key, Object value, StringBuffer sb)
    {
        sb.append('\"');

        if (key == null)
        {
            sb.append("null");
        }
        else
        {
            JSONValue.escape(key, sb);
        }

        sb.append('\"').append(':');
        sb.append(JSONValue.toJSONString(value));
        return sb.toString();
    }

    public String toString()
    {
        return this.toJSONString();
    }

    public static String toString(String key, Object value)
    {
        StringBuffer sb = new StringBuffer();
        toJSONString(key, value, sb);
        return sb.toString();
    }

    public static String escape(String s)
    {
        return JSONValue.escape(s);
    }
}
