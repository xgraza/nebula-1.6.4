package net.minecraft.client.main;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import us.nebula.client.ClientSettings;

public class Main
{
    public static void main(final String[] args)
    {
        System.setProperty("java.net.preferIPv4Stack", "true");

        final OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("fullscreen");

        ArgumentAcceptingOptionSpec<Boolean> nebulaDebugOpt = optionParser.accepts("nebulaDebug").withOptionalArg().ofType(Boolean.class).defaultsTo(false);
        ArgumentAcceptingOptionSpec<String> serverOpt = optionParser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> portOpt = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        ArgumentAcceptingOptionSpec<File> gameDirOpt = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        ArgumentAcceptingOptionSpec<File> assetsDirOpt = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> resourcePackDirOpt = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> proxyHostOpt = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> proxyPortOpt = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> proxyUsernameOpt = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> proxyPasswordOpt = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> usernameOpt = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        ArgumentAcceptingOptionSpec<String> uuidOpt = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> accessTokenOpt = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> versionOpt = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<Integer> widthOpt = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        ArgumentAcceptingOptionSpec<Integer> heightOpt = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        NonOptionArgumentSpec<String> nonOptionArgs = optionParser.nonOptions();
        OptionSet parsedOpts = optionParser.parse(args);

        Proxy proxy = Proxy.NO_PROXY;
        if (parsedOpts.has(proxyHostOpt) && parsedOpts.has(proxyPortOpt))
        {
            try
            {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(
                        parsedOpts.valueOf(proxyHostOpt), parsedOpts.valueOf(proxyPortOpt)));
            }
            catch (final Exception ignored)
            {
            }

            final String proxyUsername = parsedOpts.valueOf(proxyUsernameOpt);
            final String proxyPassword = parsedOpts.valueOf(proxyPasswordOpt);

            if (!proxy.equals(Proxy.NO_PROXY)
                    && isNotNullOrEmpty(proxyUsername)
                    && isNotNullOrEmpty(proxyPassword))
            {
                Authenticator.setDefault(new Authenticator()
                {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                    }
                });
            }
        }

        final int width = parsedOpts.valueOf(widthOpt);
        final int height = parsedOpts.valueOf(heightOpt);
        final boolean fullscreen = parsedOpts.has("fullscreen");
        final boolean demo = parsedOpts.has("demo");
        final String version = parsedOpts.valueOf(versionOpt);
        final File gameDir = parsedOpts.valueOf(gameDirOpt);
        final File assetsDir = parsedOpts.has(assetsDirOpt)
                ? parsedOpts.valueOf(assetsDirOpt)
                : new File(gameDir, "assets/");
        final File resourcePackDir = parsedOpts.has(resourcePackDirOpt)
                ? parsedOpts.valueOf(resourcePackDirOpt)
                : new File(gameDir, "resourcepacks/");
        final String uuid = parsedOpts.has(uuidOpt)
                ? uuidOpt.value(parsedOpts)
                : usernameOpt.value(parsedOpts);
        final Session session = new Session(usernameOpt.value(parsedOpts),
                uuid,
                accessTokenOpt.value(parsedOpts));
        final Minecraft client = new Minecraft(session,
                width,
                height,
                fullscreen,
                demo,
                gameDir,
                assetsDir,
                resourcePackDir,
                proxy,
                version);

        final String server = parsedOpts.valueOf(serverOpt);
        if (server != null)
        {
            client.setServer(server, parsedOpts.valueOf(portOpt));
        }

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread")
        {
            @Override
            public void run()
            {
                Minecraft.stopIntegratedServer();
            }
        });

        final List<String> ignoredArguments = parsedOpts.valuesOf(nonOptionArgs);
        if (!ignoredArguments.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + ignoredArguments);
        }

        ClientSettings.DEBUG = parsedOpts.valueOf(nebulaDebugOpt);
        if (ClientSettings.DEBUG)
        {
            System.out.println("Nebula debug enabled");
        }

        Thread.currentThread().setName("Client thread");
        client.run();
    }

    private static boolean isNotNullOrEmpty(String par0Str)
    {
        return par0Str != null && !par0Str.isEmpty();
    }
}
