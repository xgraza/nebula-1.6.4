package net.minecraft.util;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpUtil
{
    /**
     * The number of download threads that we have started so far.
     */
    private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final String __OBFID = "CL_00001485";

    /**
     * Builds an encoded HTTP POST content string from a string map
     */
    public static String buildPostString(Map par0Map)
    {
        StringBuilder var1 = new StringBuilder();
        Iterator var2 = par0Map.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry) var2.next();

            if (var1.length() > 0)
            {
                var1.append('&');
            }

            try
            {
                var1.append(URLEncoder.encode((String) var3.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException var6)
            {
                var6.printStackTrace();
            }

            if (var3.getValue() != null)
            {
                var1.append('=');

                try
                {
                    var1.append(URLEncoder.encode(var3.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException var5)
                {
                    var5.printStackTrace();
                }
            }
        }

        return var1.toString();
    }

    public static String func_151226_a(URL p_151226_0_, Map p_151226_1_, boolean p_151226_2_)
    {
        return func_151225_a(p_151226_0_, buildPostString(p_151226_1_), p_151226_2_);
    }

    private static String func_151225_a(URL p_151225_0_, String p_151225_1_, boolean p_151225_2_)
    {
        try
        {
            Proxy var3 = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();

            if (var3 == null)
            {
                var3 = Proxy.NO_PROXY;
            }

            HttpURLConnection var4 = (HttpURLConnection) p_151225_0_.openConnection(var3);
            var4.setRequestMethod("POST");
            var4.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            var4.setRequestProperty("Content-Length", "" + p_151225_1_.getBytes().length);
            var4.setRequestProperty("Content-Language", "en-US");
            var4.setUseCaches(false);
            var4.setDoInput(true);
            var4.setDoOutput(true);
            DataOutputStream var5 = new DataOutputStream(var4.getOutputStream());
            var5.writeBytes(p_151225_1_);
            var5.flush();
            var5.close();
            BufferedReader var6 = new BufferedReader(new InputStreamReader(var4.getInputStream()));
            StringBuffer var8 = new StringBuffer();
            String var7;

            while ((var7 = var6.readLine()) != null)
            {
                var8.append(var7);
                var8.append('\r');
            }

            var6.close();
            return var8.toString();
        } catch (Exception var9)
        {
            if (!p_151225_2_)
            {
                logger.error("Could not post to " + p_151225_0_, var9);
            }

            return "";
        }
    }

    public static void downloadTexturePack(final File outputFile, final String downloadURL, final HttpUtil.DownloadListener listener, final Map<String, String> headerMap, final int p_151223_4_, final IProgressUpdate progressScreenGui, final Proxy proxy)
    {
        Thread var7 = new Thread(() ->
        {
            URLConnection var1 = null;
            InputStream var2 = null;
            DataOutputStream var3 = null;

            if (progressScreenGui != null)
            {
                progressScreenGui.resetProgressAndMessage("Downloading Texture Pack");
                progressScreenGui.resetProgresAndWorkingMessage("Making Request...");
            }

            try
            {
                try
                {
                    byte[] var4 = new byte[4096];
                    URL var5 = new URL(downloadURL);
                    var1 = var5.openConnection(proxy);
                    float var6 = 0.0F;
                    float var10 = (float) headerMap.size();
                    Iterator var8 = headerMap.entrySet().iterator();

                    while (var8.hasNext())
                    {
                        Entry var9 = (Entry) var8.next();
                        var1.setRequestProperty((String) var9.getKey(), (String) var9.getValue());

                        if (progressScreenGui != null)
                        {
                            progressScreenGui.setLoadingProgress((int) (++var6 / var10 * 100.0F));
                        }
                    }

                    var2 = var1.getInputStream();
                    var10 = (float) var1.getContentLength();
                    int var28 = var1.getContentLength();

                    if (progressScreenGui != null)
                    {
                        progressScreenGui.resetProgresAndWorkingMessage(String.format("Downloading file (%.2f MB)...", Float.valueOf(var10 / 1000.0F / 1000.0F)));
                    }

                    if (outputFile.exists())
                    {
                        long var29 = outputFile.length();

                        if (var29 == (long) var28)
                        {
                            listener.func_148522_a(outputFile);

                            if (progressScreenGui != null)
                            {
                                progressScreenGui.func_146586_a();
                            }

                            return;
                        }

                        HttpUtil.logger.warn("Deleting " + outputFile + " as it does not match what we currently have (" + var28 + " vs our " + var29 + ").");
                        outputFile.delete();
                    } else if (outputFile.getParentFile() != null)
                    {
                        outputFile.getParentFile().mkdirs();
                    }

                    var3 = new DataOutputStream(new FileOutputStream(outputFile));

                    if (p_151223_4_ > 0 && var10 > (float) p_151223_4_)
                    {
                        if (progressScreenGui != null)
                        {
                            progressScreenGui.func_146586_a();
                        }

                        throw new IOException("Filesize is bigger than maximum allowed (file is " + var6 + ", limit is " + p_151223_4_ + ")");
                    }

                    boolean var30 = false;
                    int var31;

                    while ((var31 = var2.read(var4)) >= 0)
                    {
                        var6 += (float) var31;

                        if (progressScreenGui != null)
                        {
                            progressScreenGui.setLoadingProgress((int) (var6 / var10 * 100.0F));
                        }

                        if (p_151223_4_ > 0 && var6 > (float) p_151223_4_)
                        {
                            if (progressScreenGui != null)
                            {
                                progressScreenGui.func_146586_a();
                            }

                            throw new IOException("Filesize was bigger than maximum allowed (got >= " + var6 + ", limit was " + p_151223_4_ + ")");
                        }

                        var3.write(var4, 0, var31);
                    }

                    listener.func_148522_a(outputFile);

                    if (progressScreenGui != null)
                    {
                        progressScreenGui.func_146586_a();
                    }
                } catch (Throwable var26)
                {
                    var26.printStackTrace();
                }
            } finally
            {
                try
                {
                    if (var2 != null)
                    {
                        var2.close();
                    }
                } catch (IOException var25)
                {
                }

                try
                {
                    if (var3 != null)
                    {
                        var3.close();
                    }
                } catch (IOException var24)
                {
                }
            }
        }, "File Downloader #" + downloadThreadsStarted.incrementAndGet());
        var7.setDaemon(true);
        var7.start();
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
        } finally
        {
            try
            {
                if (var0 != null)
                {
                    var0.close();
                }
            } catch (IOException var8)
            {
            }
        }

        return var10;
    }

    public interface DownloadListener
    {
        void func_148522_a(File var1);
    }
}
