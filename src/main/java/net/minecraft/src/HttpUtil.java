package net.minecraft.src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class HttpUtil
{
    /**
     * Builds an encoded HTTP POST content string from a string map
     */
    public static String buildPostString(Map par0Map)
    {
        StringBuilder var1 = new StringBuilder();
        Iterator var2 = par0Map.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();

            if (var1.length() > 0)
            {
                var1.append('&');
            }

            try
            {
                var1.append(URLEncoder.encode((String)var3.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException var6)
            {
                var6.printStackTrace();
            }

            if (var3.getValue() != null)
            {
                var1.append('=');

                try
                {
                    var1.append(URLEncoder.encode(var3.getValue().toString(), "UTF-8"));
                }
                catch (UnsupportedEncodingException var5)
                {
                    var5.printStackTrace();
                }
            }
        }

        return var1.toString();
    }

    /**
     * Sends a HTTP POST request to the given URL with data from a map
     */
    public static String sendPost(ILogAgent par0ILogAgent, URL par1URL, Map par2Map, boolean par3)
    {
        return sendPost(par0ILogAgent, par1URL, buildPostString(par2Map), par3);
    }

    /**
     * Sends a HTTP POST request to the given URL with data from a string
     */
    private static String sendPost(ILogAgent par0ILogAgent, URL par1URL, String par2Str, boolean par3)
    {
        try
        {
            Proxy var4 = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();

            if (var4 == null)
            {
                var4 = Proxy.NO_PROXY;
            }

            HttpURLConnection var5 = (HttpURLConnection)par1URL.openConnection(var4);
            var5.setRequestMethod("POST");
            var5.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            var5.setRequestProperty("Content-Length", "" + par2Str.getBytes().length);
            var5.setRequestProperty("Content-Language", "en-US");
            var5.setUseCaches(false);
            var5.setDoInput(true);
            var5.setDoOutput(true);
            DataOutputStream var6 = new DataOutputStream(var5.getOutputStream());
            var6.writeBytes(par2Str);
            var6.flush();
            var6.close();
            BufferedReader var7 = new BufferedReader(new InputStreamReader(var5.getInputStream()));
            StringBuffer var9 = new StringBuffer();
            String var8;

            while ((var8 = var7.readLine()) != null)
            {
                var9.append(var8);
                var9.append('\r');
            }

            var7.close();
            return var9.toString();
        }
        catch (Exception var10)
        {
            if (!par3)
            {
                if (par0ILogAgent != null)
                {
                    par0ILogAgent.logSevereException("Could not post to " + par1URL, var10);
                }
                else
                {
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Could not post to " + par1URL, var10);
                }
            }

            return "";
        }
    }

    public static int func_76181_a() throws IOException
    {
        ServerSocket var0 = null;
        boolean var1 = true;
        int var10;

        try
        {
            var0 = new ServerSocket(0);
            var10 = var0.getLocalPort();
        }
        finally
        {
            try
            {
                if (var0 != null)
                {
                    var0.close();
                }
            }
            catch (IOException var8)
            {
                ;
            }
        }

        return var10;
    }
}
