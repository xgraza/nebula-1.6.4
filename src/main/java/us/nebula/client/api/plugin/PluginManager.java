package us.nebula.client.api.plugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.client.Nebula;
import us.nebula.client.api.manager.ITypedManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author xgraza
 * @since 4/10/26
 */
public final class PluginManager implements ITypedManager<Plugin>
{
    private static final Logger LOGGER = LogManager.getLogger("PluginManager");

    private final Map<String, Plugin> pluginKeyMap = new HashMap<>();
    private final File location = new File(
            Nebula.INSTANCE.getNebulaRootDir(), "plugins");

    @Override
    public void init()
    {
        if (!location.exists() && !location.isDirectory())
        {
            if (!location.mkdir())
            {
                LOGGER.info("Failed to create {}", location);
                return;
            }
            LOGGER.info("Created {}", location);
        }
        discoverPlugins();
    }

    private void discoverPlugins()
    {
        final File[] files = location.listFiles();
        if (files == null)
        {
            return;
        }
        final List<File> pluginCandidates = new ArrayList<>();
        for (final File file : files)
        {
            if (!file.getName().endsWith(".jar"))
            {
                return;
            }
            pluginCandidates.add(file);
        }

        if (pluginCandidates.isEmpty())
        {
            return;
        }

        LOGGER.info("Discovering {} plugin(s)", pluginCandidates.size());

        for (final File file : pluginCandidates)
        {
            discoverPlugin(file);
        }
    }

    private void discoverPlugin(final File file)
    {
        final Map<String, byte[]> classDataMap = new HashMap<>();

        ZipEntry entry;
        try (final ZipInputStream zis = new ZipInputStream(Files.newInputStream(file.toPath())))
        {
            while ((entry = zis.getNextEntry()) != null)
            {
                final String name = entry.getName();
                if (name.startsWith("us/nebula"))
                {
                    throw new RuntimeException("Plugin should not include plugin library data");
                }
                if (name.endsWith("/"))
                {
                    continue;
                }
                LOGGER.info("Taking file {}", entry.getName());

                classDataMap.put(name, null);

                zis.closeEntry();
            }
        }
        catch (final IOException e)
        {
            LOGGER.error("Failed to load plugin files", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Plugin> getAll()
    {
        return Collections.emptyList();
    }
}
