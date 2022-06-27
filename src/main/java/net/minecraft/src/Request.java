package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Request
{
    protected HttpURLConnection field_96367_a;
    private boolean field_96366_c;
    protected String field_96365_b;

    public Request(String par1Str, int par2, int par3)
    {
        try
        {
            this.field_96365_b = par1Str;
            this.field_96367_a = (HttpURLConnection)(new URL(par1Str)).openConnection(Minecraft.getMinecraft().getProxy());
            this.field_96367_a.setConnectTimeout(par2);
            this.field_96367_a.setReadTimeout(par3);
        }
        catch (Exception var5)
        {
            throw new ExceptionMcoHttp("Failed URL: " + par1Str, var5);
        }
    }

    public void func_100006_a(String par1Str, String par2Str)
    {
        String var3 = this.field_96367_a.getRequestProperty("Cookie");

        if (var3 == null)
        {
            this.field_96367_a.setRequestProperty("Cookie", par1Str + "=" + par2Str);
        }
        else
        {
            this.field_96367_a.setRequestProperty("Cookie", var3 + ";" + par1Str + "=" + par2Str);
        }
    }

    public int func_96362_a()
    {
        try
        {
            this.func_96354_d();
            return this.field_96367_a.getResponseCode();
        }
        catch (Exception var2)
        {
            throw new ExceptionMcoHttp("Failed URL: " + this.field_96365_b, var2);
        }
    }

    public int func_111221_b()
    {
        String var1 = this.field_96367_a.getHeaderField("Retry-After");

        try
        {
            return Integer.valueOf(var1).intValue();
        }
        catch (Exception var3)
        {
            return 5;
        }
    }

    public String func_96364_c()
    {
        try
        {
            this.func_96354_d();
            String var1 = this.func_96362_a() >= 400 ? this.func_96352_a(this.field_96367_a.getErrorStream()) : this.func_96352_a(this.field_96367_a.getInputStream());
            this.func_96360_f();
            return var1;
        }
        catch (IOException var2)
        {
            throw new ExceptionMcoHttp("Failed URL: " + this.field_96365_b, var2);
        }
    }

    private String func_96352_a(InputStream par1InputStream) throws IOException
    {
        if (par1InputStream == null)
        {
            throw new IOException("No response (null)");
        }
        else
        {
            StringBuilder var2 = new StringBuilder();

            for (int var3 = par1InputStream.read(); var3 != -1; var3 = par1InputStream.read())
            {
                var2.append((char)var3);
            }

            return var2.toString();
        }
    }

    private void func_96360_f()
    {
        byte[] var1 = new byte[1024];
        InputStream var3;

        try
        {
            boolean var2 = false;
            var3 = this.field_96367_a.getInputStream();

            while (true)
            {
                if (var3.read(var1) <= 0)
                {
                    var3.close();
                    break;
                }
            }
        }
        catch (Exception var6)
        {
            try
            {
                var3 = this.field_96367_a.getErrorStream();
                boolean var4 = false;

                while (true)
                {
                    if (var3.read(var1) <= 0)
                    {
                        var3.close();
                        break;
                    }
                }
            }
            catch (IOException var5)
            {
                ;
            }
        }
    }

    protected Request func_96354_d()
    {
        if (!this.field_96366_c)
        {
            Request var1 = this.func_96359_e();
            this.field_96366_c = true;
            return var1;
        }
        else
        {
            return this;
        }
    }

    protected abstract Request func_96359_e();

    public static Request func_96358_a(String par0Str)
    {
        return new RequestGet(par0Str, 5000, 10000);
    }

    public static Request func_96361_b(String par0Str, String par1Str)
    {
        return new RequestPost(par0Str, par1Str.getBytes(), 5000, 10000);
    }

    public static Request func_104064_a(String par0Str, String par1Str, int par2, int par3)
    {
        return new RequestPost(par0Str, par1Str.getBytes(), par2, par3);
    }

    public static Request func_96355_b(String par0Str)
    {
        return new RequestDelete(par0Str, 5000, 10000);
    }

    public static Request func_96363_c(String par0Str, String par1Str)
    {
        return new RequestPut(par0Str, par1Str.getBytes(), 5000, 10000);
    }

    public static Request func_96353_a(String par0Str, String par1Str, int par2, int par3)
    {
        return new RequestPut(par0Str, par1Str.getBytes(), par2, par3);
    }

    public int func_130110_g()
    {
        String var1 = this.field_96367_a.getHeaderField("Error-Code");

        try
        {
            return Integer.valueOf(var1).intValue();
        }
        catch (Exception var3)
        {
            return -1;
        }
    }
}
