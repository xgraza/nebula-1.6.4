package optifine.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

public class JSONWriter
{
    private Writer writer = null;
    private int indentStep = 2;
    private int indent = 0;

    public JSONWriter(Writer writer)
    {
        this.writer = writer;
    }

    public JSONWriter(Writer writer, int indentStep)
    {
        this.writer = writer;
        this.indentStep = indentStep;
    }

    public JSONWriter(Writer writer, int indentStep, int indent)
    {
        this.writer = writer;
        this.indentStep = indentStep;
        this.indent = indent;
    }

    public void writeObject(Object obj) throws IOException
    {
        if (obj instanceof JSONObject)
        {
            JSONObject jArr1 = (JSONObject)obj;
            this.writeJsonObject(jArr1);
        }
        else if (obj instanceof JSONArray)
        {
            JSONArray jArr = (JSONArray)obj;
            this.writeJsonArray(jArr);
        }
        else
        {
            this.writer.write(JSONValue.toJSONString(obj));
        }
    }

    private void writeJsonArray(JSONArray jArr) throws IOException
    {
        this.writeLine("[");
        this.indentAdd();
        int num = jArr.size();

        for (int i = 0; i < num; ++i)
        {
            Object val = jArr.get(i);
            this.writeIndent();
            this.writeObject(val);

            if (i < jArr.size() - 1)
            {
                this.write(",");
            }

            this.writeLine("");
        }

        this.indentRemove();
        this.writeIndent();
        this.writer.write("]");
    }

    private void writeJsonObject(JSONObject jObj) throws IOException
    {
        this.writeLine("{");
        this.indentAdd();
        Set keys = jObj.keySet();
        int keyNum = keys.size();
        int count = 0;
        Iterator it = keys.iterator();

        while (it.hasNext())
        {
            String key = (String)it.next();
            Object val = jObj.get(key);
            this.writeIndent();
            this.writer.write(JSONValue.toJSONString(key));
            this.writer.write(": ");
            this.writeObject(val);
            ++count;

            if (count < keyNum)
            {
                this.writeLine(",");
            }
            else
            {
                this.writeLine("");
            }
        }

        this.indentRemove();
        this.writeIndent();
        this.writer.write("}");
    }

    private void writeLine(String str) throws IOException
    {
        this.writer.write(str);
        this.writer.write("\n");
    }

    private void write(String str) throws IOException
    {
        this.writer.write(str);
    }

    private void writeIndent() throws IOException
    {
        for (int i = 0; i < this.indent; ++i)
        {
            this.writer.write(32);
        }
    }

    private void indentAdd()
    {
        this.indent += this.indentStep;
    }

    private void indentRemove()
    {
        this.indent -= this.indentStep;
    }
}
