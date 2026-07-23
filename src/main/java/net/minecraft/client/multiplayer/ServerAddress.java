package net.minecraft.client.multiplayer;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class ServerAddress
{
    private static final Hashtable<String, String> OPTIONS = new Hashtable<>();
    private static final int DEFAULT_PORT = 25565;

    static
    {
        OPTIONS.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        OPTIONS.put("java.naming.provider.url", "dns:");
        OPTIONS.put("com.sun.jndi.dns.timeout.retries", "1");
    }

    private final String ipAddress;
    private final int serverPort;

    private ServerAddress(String par1Str, int par2)
    {
        this.ipAddress = par1Str;
        this.serverPort = par2;
    }

    public String getIP()
    {
        return this.ipAddress;
    }

    public int getPort()
    {
        return this.serverPort;
    }

    public static ServerAddress resolveAddress(String address)
    {
        if (address == null)
        {
            return null;
        }

        String[] parts = address.split(":");

        if (address.startsWith("["))
        {
            int var2 = address.indexOf("]");

            if (var2 > 0)
            {
                String var3 = address.substring(1, var2);
                String var4 = address.substring(var2 + 1).trim();

                if (var4.startsWith(":"))
                {
                    var4 = var4.substring(1);
                    parts = new String[]{ var3, var4 };
                } else
                {
                    parts = new String[]{ var3 };
                }
            }
        }

        if (parts.length > 2)
        {
            parts = new String[]{ address };
        }

        String var5 = parts[0];
        int var6 = parts.length > 1 ? parseIntWithDefault(parts[1], DEFAULT_PORT) : DEFAULT_PORT;

        if (var6 == DEFAULT_PORT)
        {
            String[] var7 = getServerAddress(var5);
            var5 = var7[0];
            var6 = parseIntWithDefault(var7[1], DEFAULT_PORT);
        }

        return new ServerAddress(var5, var6);
    }

    /**
     * Returns a server's address and port for the specified hostname, looking up the SRV record if possible
     */
    private static String[] getServerAddress(String hostname)
    {
        try
        {
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            InitialDirContext ctx = new InitialDirContext(OPTIONS);
            Attributes attributes = ctx.getAttributes("_minecraft._tcp." + hostname, new String[]{ "SRV" });
            String[] srv = attributes.get("srv").get().toString().split(" ", 4);
            return new String[]{ srv[3], srv[2] };
        } catch (Throwable throwable)
        {
            return new String[]{ hostname, Integer.toString(DEFAULT_PORT) };
        }
    }

    private static int parseIntWithDefault(String par0Str, int par1)
    {
        try
        {
            return Integer.parseInt(par0Str.trim());
        } catch (Exception var3)
        {
            return par1;
        }
    }
}
